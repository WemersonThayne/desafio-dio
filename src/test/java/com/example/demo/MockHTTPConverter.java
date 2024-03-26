package com.example.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class MockHTTPConverter {

    private final ObjectMapper objectMapper;

    public <T> T convertJsonResponse(final MockHttpServletResponse response, final Class<T> type) throws Exception {
        return objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), type);
    }

    public <T> T convertJsonResponse(final MockHttpServletResponse response, final TypeReference<T> type) throws Exception {
        return objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), type);
    }

}
