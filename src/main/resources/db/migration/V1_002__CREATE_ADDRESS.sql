create table if not exists address (
    cep varchar not null,
    logradouro varchar,
    complemento varchar,
    bairro varchar,
    localidade varchar,
    uf varchar,
    ibge varchar,
    gia varchar,
    ddd varchar,
    siafi varchar,
    primary key (cep),
    UNIQUE (cep)
);

ALTER TABLE customers
    ADD COLUMN address_id varchar,
ADD CONSTRAINT fk_customer_address
    FOREIGN KEY (address_id)
    REFERENCES address(cep);
