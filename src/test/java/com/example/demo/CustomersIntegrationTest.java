package com.example.demo;

import com.example.demo.domain.Customer;
import com.fasterxml.jackson.core.type.TypeReference;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
class CustomersIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MockHTTPConverter mockHTTPConverter;

    private static final String TRUNCATE = "TRUNCATE $1 CASCADE";

    private static final List<String> TABLES_TO_TRUNCATE = List.of(
            "customers"
    );

    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer("postgres:15-alpine")
            .withDatabaseName("integration-test-db")
            .withUsername("postgres")
            .withPassword("postgres");
    //.withInitScript("test-data.sql"); Caso queria iniciar com algum init temos a propriedade....

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", POSTGRES::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @BeforeAll
    static void setUp(){
        POSTGRES.start();
    }

    @AfterAll
    static void afterAll(){
        POSTGRES.stop();
    }

    @AfterEach
    void clearTable(){
        TABLES_TO_TRUNCATE.forEach(table -> jdbcTemplate.execute(TRUNCATE.replace("$1", table)));
    }

    @Test
    void containerOn(){
        Assertions.assertThat(POSTGRES.isRunning())
                .isTrue();
    }


    @Sql("/db/customers/insert_customers.sql")
    @Test
    void getAllCustomersTest() throws Exception {
        Assertions.assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate,"customers"))
                .isEqualTo(2);

        var response = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/customers")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse();

        final var expectedResponse = List.of(
                new Customer(1L,"Test","test@gmail.com"),
                new Customer(2L,"Other Customer","other.customer@gmail.com"));

        final var responseContent = mockHTTPConverter.convertJsonResponse(response,  new TypeReference<List<Customer>>() {});
        Assertions.assertThat(responseContent)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }

    @Sql("/db/customers/insert_customers.sql")
    @Test
    void getCustomerByIdTest() throws Exception {
        final var id = 1L;
        var response = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/customers/%s".formatted(id))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse();

        final var expectedResponse = new Customer(id,"Test","test@gmail.com");
        final var responseContent = mockHTTPConverter.convertJsonResponse(response, Customer.class);
        Assertions.assertThat(responseContent).usingRecursiveComparison().isEqualTo(expectedResponse);
    }

    @Test
    void getCustomerByIdNotFoundTest() throws Exception {
        final var id = 1L;
        var response = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/customers/%s".formatted(id))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn().getResponse();
        final var expectedResponse = "Customer not found for this id %s".formatted(id);
        Assertions.assertThat(response.getContentAsString()).isEqualTo(expectedResponse);
    }


    @Test
    void insertCustomerValidTest() throws Exception {
        final var customer = new Customer("Test","test@gmail.com");

        var response = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/customers/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                { "name":  "Test", "email":  "test@gmail.com"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse();

        final var responseContent = mockHTTPConverter.convertJsonResponse(response, Customer.class);
        Assertions.assertThat(responseContent)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(customer);

        final var customerDB = jdbcTemplate.queryForMap("select * from customers where email = '%s'".formatted(customer.getEmail()));
        Assertions.assertThat(customerDB)
                .containsEntry("id", 1L)
                .containsEntry("name",customer.getName())
                .containsEntry("email",customer.getEmail());
    }

    @Sql("/db/customers/insert_customers.sql")
    @Test
    void insertCustomerAlreadyExistsValidTest() throws Exception {
        final var customer = new Customer("Test","test@gmail.com");

        var response = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/customers/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                { "name":  "Test", "email":  "test@gmail.com"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn().getResponse();

        final var expectedResponse = "Customer %s already exists.".formatted(customer.getEmail());
        Assertions.assertThat(response.getContentAsString()).isEqualTo(expectedResponse);
        Assertions.assertThat(JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"customers","email = '%s'".formatted(customer.getEmail())))
                .isEqualTo(1);
    }


}
