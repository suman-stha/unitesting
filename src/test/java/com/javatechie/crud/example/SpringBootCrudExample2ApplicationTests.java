package com.javatechie.crud.example;

import com.javatechie.crud.example.entity.Product;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringBootCrudExample2ApplicationTests {

    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";

    private static RestTemplate restTemplate;
    @Autowired

    private TestH2Repository h2Repository;

    @BeforeAll
    public static void init() {
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void setUp() {
        baseUrl = baseUrl.concat(":").concat(String.valueOf(port)).concat("/products");
    }

    @Test
    void addProductTest() {
        Product product = new Product("headset", 2, 7000);

        Product response = restTemplate.postForObject(baseUrl, product, Product.class);
        assert response != null;
        assertEquals("headset", response.getName());
        assertEquals(1, h2Repository.findAll().size());
    }

    @Test
    @Sql(statements = "INSERT INTO PRODUCT_TBL(id,name,quantity,price)VALUES (4,'AC',1,34566)",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM PRODUCT_TBL WHERE name='AC'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getProductsTest() {
        List products = restTemplate.getForObject(baseUrl, List.class);
        assert products != null;
        assertEquals(1, products.size());
        assertEquals(1, h2Repository.findAll().size());
    }

    @Test
    @Sql(statements = "INSERT INTO PRODUCT_TBL(id,name, quantity,price)VALUES(1,'CAR', 1, 334000)",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM PRODUCT_TBL WHERE id=2", executionPhase =
            Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findProductById() {
        Product product = restTemplate.getForObject(baseUrl + "/{id}", Product.class, 1);
        assertAll(() -> assertNotNull(product)
                , () -> {
                    assertEquals(1, product.getId());
                },
                () -> assertEquals("CAR", product.getName()));
    }

}
