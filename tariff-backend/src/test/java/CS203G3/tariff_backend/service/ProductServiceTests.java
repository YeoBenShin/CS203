package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.exception.ResourceNotFoundException;
import CS203G3.tariff_backend.model.Product;
import CS203G3.tariff_backend.repository.ProductRepository;
import CS203G3.tariff_backend.repository.TariffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private TariffRepository tariffRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private List<Product> productList;

    @BeforeEach
    void setUp() {
        // Setup sample products
        product = new Product();
        product.setHSCode("1234");
        product.setDescription("Test Product");

        Product product2 = new Product();
        product2.setHSCode("5678");
        product2.setDescription("Another Product");

        Product product3 = new Product();
        product3.setHSCode("9012");
        product3.setDescription("Third Product");

        productList = Arrays.asList(product, product2, product3);
    }

    @Test
    void getAllProducts_ReturnsAllProducts() {
        // Arrange
        when(productRepository.findAll()).thenReturn(productList);

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("1234", result.get(0).getHSCode());
        assertEquals("5678", result.get(1).getHSCode());
        assertEquals("9012", result.get(2).getHSCode());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getAllProducts_WhenEmpty_ReturnsEmptyList() {
        // Arrange
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductByhSCode_WhenExists_ReturnsProduct() {
        // Arrange
        when(productRepository.findById("1234")).thenReturn(Optional.of(product));

        // Act
        Product result = productService.getProductByhSCode("1234");

        // Assert
        assertNotNull(result);
        assertEquals("1234", result.getHSCode());
        assertEquals("Test Product", result.getDescription());
        verify(productRepository, times(1)).findById("1234");
    }

    @Test
    void getProductByhSCode_WhenNotExists_ThrowsException() {
        // Arrange
        when(productRepository.findById("9999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
            () -> productService.getProductByhSCode("9999"));
        verify(productRepository, times(1)).findById("9999");
    }

    @Test
    void findByDescription_WhenExists_ReturnsProduct() {
        // Arrange
        when(productRepository.findByDescription("Test Product")).thenReturn(product);

        // Act
        Product result = productService.findByDescription("Test Product");

        // Assert
        assertNotNull(result);
        assertEquals("1234", result.getHSCode());
        assertEquals("Test Product", result.getDescription());
        verify(productRepository, times(1)).findByDescription("Test Product");
    }

    @Test
    void findByDescription_WhenNotExists_ThrowsException() {
        // Arrange
        when(productRepository.findByDescription("Non-existent Product")).thenReturn(null);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
            () -> productService.findByDescription("Non-existent Product"));
        verify(productRepository, times(1)).findByDescription("Non-existent Product");
    }

    @Test
    void createProduct_WithValidData_CreatesAndReturnsProduct() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product result = productService.createProduct(product);

        // Assert
        assertNotNull(result);
        assertEquals("1234", result.getHSCode());
        assertEquals("Test Product", result.getDescription());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void updateProduct_WhenExists_UpdatesAndReturnsProduct() {
        // Arrange
        Product updatedData = new Product();
        updatedData.setHSCode("1234");
        updatedData.setDescription("Updated Product Description");

        when(productRepository.existsById("1234")).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenReturn(updatedData);

        // Act
        Product result = productService.updateProduct("1234", updatedData);

        // Assert
        assertNotNull(result);
        assertEquals("1234", result.getHSCode());
        assertEquals("Updated Product Description", result.getDescription());
        verify(productRepository, times(1)).existsById("1234");
        verify(productRepository, times(1)).save(updatedData);
    }

    @Test
    void updateProduct_WhenNotExists_ThrowsException() {
        // Arrange
        Product updatedData = new Product();
        updatedData.setHSCode("9999");
        updatedData.setDescription("Non-existent Product");

        when(productRepository.existsById("9999")).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
            () -> productService.updateProduct("9999", updatedData));
        verify(productRepository, times(1)).existsById("9999");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_WhenExists_DeletesProductAndRelatedTariffs() {
        // Arrange
        List<Long> tariffIds = Arrays.asList(1L, 2L, 3L);
        when(productRepository.existsById("1234")).thenReturn(true);
        when(tariffRepository.findTariffIdByProduct_HsCode("1234")).thenReturn(tariffIds);
        doNothing().when(tariffRepository).deleteById(anyLong());
        doNothing().when(productRepository).deleteById("1234");

        // Act
        productService.deleteProduct("1234");

        // Assert
        verify(productRepository, times(1)).existsById("1234");
        verify(tariffRepository, times(1)).findTariffIdByProduct_HsCode("1234");
        verify(tariffRepository, times(3)).deleteById(anyLong());
        verify(productRepository, times(1)).deleteById("1234");
    }

    @Test
    void deleteProduct_WhenExistsWithNoTariffs_DeletesProduct() {
        // Arrange
        when(productRepository.existsById("1234")).thenReturn(true);
        when(tariffRepository.findTariffIdByProduct_HsCode("1234")).thenReturn(Collections.emptyList());
        doNothing().when(productRepository).deleteById("1234");

        // Act
        productService.deleteProduct("1234");

        // Assert
        verify(productRepository, times(1)).existsById("1234");
        verify(tariffRepository, times(1)).findTariffIdByProduct_HsCode("1234");
        verify(tariffRepository, never()).deleteById(anyLong());
        verify(productRepository, times(1)).deleteById("1234");
    }

    @Test
    void deleteProduct_WhenNotExists_ThrowsException() {
        // Arrange
        when(productRepository.existsById("9999")).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
            () -> productService.deleteProduct("9999"));
        verify(productRepository, times(1)).existsById("9999");
        verify(tariffRepository, never()).findTariffIdByProduct_HsCode(anyString());
        verify(productRepository, never()).deleteById(anyString());
    }
}
