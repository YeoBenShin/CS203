package CS203G3.tariff_backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import CS203G3.tariff_backend.config.TestSecurityConfig;
import CS203G3.tariff_backend.exception.ResourceNotFoundException;
import CS203G3.tariff_backend.model.Product;
import CS203G3.tariff_backend.service.ProductService;

@WebMvcTest(value = ProductController.class, 
    excludeAutoConfiguration = {
        OAuth2ClientAutoConfiguration.class, 
        OAuth2ResourceServerAutoConfiguration.class,
        SecurityAutoConfiguration.class
    })
@Import(TestSecurityConfig.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product;
    private List<Product> productList;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setHSCode("0101");
        product.setDescription("Live horses");

        Product product2 = new Product();
        product2.setHSCode("0102");
        product2.setDescription("Live bovine animals");

        productList = Arrays.asList(product, product2);
    }

    @Test
    void getAllProducts_ShouldReturnProductList() throws Exception {
        when(productService.getAllProducts()).thenReturn(productList);

        mockMvc.perform(get("/api/products"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isArray())
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[0].hsCode").value("0101"))
               .andExpect(jsonPath("$[1].hsCode").value("0102"));
    }

    @Test
    void getAllProducts_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/products"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isArray())
               .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getProductByHSCode_WhenExists_ShouldReturnProduct() throws Exception {
        when(productService.getProductByhSCode("0101")).thenReturn(product);

        mockMvc.perform(get("/api/products/hsCode/0101"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.hsCode").value("0101"))
               .andExpect(jsonPath("$.description").value("Live horses"));
    }

    @Test
    void getProductByHSCode_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(productService.getProductByhSCode("9999")).thenReturn(null);

        mockMvc.perform(get("/api/products/hsCode/9999"))
               .andExpect(status().isNotFound());
    }

    @Test
    void getProductByDescription_WhenExists_ShouldReturnProduct() throws Exception {
        when(productService.findByDescription("Live horses")).thenReturn(product);

        mockMvc.perform(get("/api/products/Live horses"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.hsCode").value("0101"))
               .andExpect(jsonPath("$.description").value("Live horses"));
    }

    @Test
    void createProduct_WithValidData_ShouldReturnCreated() throws Exception {
        when(productService.createProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/api/products")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(product)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.hsCode").value("0101"))
               .andExpect(jsonPath("$.description").value("Live horses"));
    }

    @Test
    void updateProduct_WhenExists_ShouldReturnUpdatedProduct() throws Exception {
        Product updatedProduct = new Product();
        updatedProduct.setHSCode("0101");
        updatedProduct.setDescription("Updated Live horses");

        when(productService.updateProduct(eq("0101"), any(Product.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/api/products/0101")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(updatedProduct)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.hsCode").value("0101"))
               .andExpect(jsonPath("$.description").value("Updated Live horses"));
    }

    @Test
    void updateProduct_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(productService.updateProduct(eq("9999"), any(Product.class))).thenReturn(null);

        mockMvc.perform(put("/api/products/9999")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(product)))
               .andExpect(status().isNotFound());
    }

    @Test
    void deleteProduct_WhenExists_ShouldReturnNoContent() throws Exception {
        doNothing().when(productService).deleteProduct("0101");

        mockMvc.perform(delete("/api/products/0101"))
               .andExpect(status().isNoContent());
    }

    @Test
    void deleteProduct_WhenNotExists_ShouldReturnNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Product", "9999"))
            .when(productService).deleteProduct("9999");

        mockMvc.perform(delete("/api/products/9999"))
               .andExpect(status().isNotFound());
    }
}
