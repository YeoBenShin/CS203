package CS203G3.tariff_backend.controller;

import CS203G3.tariff_backend.service.ProductService;
import CS203G3.tariff_backend.model.Product;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{description}")
    public Product getHSCode(@PathVariable String description) {
        return productService.findByDescription(description);
    }
    

}
