package com.monetastore.monetastore.controllers;

import com.monetastore.monetastore.Models.Product;
import com.monetastore.monetastore.Models.User;
import com.monetastore.monetastore.dto.ProductDto;
import com.monetastore.monetastore.services.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ProductsControllerTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductsController productsController;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private ProductDto productDto;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private MultipartFile imageFile;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testShowProductList() {
        User user = new User();
        user.setId(1L);

        List<Product> products = new ArrayList<>();
        products.add(new Product());
        products.add(new Product());

        when(session.getAttribute("user")).thenReturn(user);
        when(productRepository.findByUserId((long) eq(1), any())).thenReturn(products);

        assertEquals("products/index", productsController.showProductList(model, session));
    }

    @Test
    public void testShowCreatePage() {
        assertEquals("products/CreateProduct", productsController.showCreatePage(model));
    }
    @Test
    public void testShowCreatePageError() {
        assertEquals("products/CreateProduct", productsController.showCreatePage(model));

    }
    @Test
    public void testCreateProduct() {
        assertEquals("redirect:/login", productsController.createProduct(productDto, bindingResult,session));
    }
    @Test
    public void testShowEditPage() {
        // Mock
        when(session.getAttribute("user")).thenReturn(new User());
        when(productRepository.findById(anyInt())).thenReturn(java.util.Optional.of(new Product()));

        String viewName = productsController.showEditPage(model, 1, session);
        assertEquals("products/EditProduct", viewName);

    }
    @Test
    public void testUpdateProduct() {
        // Mock
        when(session.getAttribute("user")).thenReturn(new User());
        when(productRepository.findById(anyInt())).thenReturn(java.util.Optional.of(new Product()));

        String viewName = productsController.updateProduct(model, 1, productDto,bindingResult, session);
        assertEquals("redirect:/products", viewName);

    }
    @Test
    public void testDeleteProduct() {
        // Mock
        when(session.getAttribute("user")).thenReturn(new User());
        when(productRepository.findById(anyInt())).thenReturn(java.util.Optional.of(new Product()));

        String viewName = productsController.deleteProduct( 1, session);
        assertEquals("redirect:/products", viewName);

    }
    @Test
    public void testShowProductList_UserLoggedIn() {
        // Mock
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        when(session.getAttribute("user")).thenReturn(user);

        List<Product> userProducts = new ArrayList<>();
        userProducts.add(new Product());
        when(productRepository.findByUserId(eq(user.getId()), any())).thenReturn(userProducts);

        // Test
        String viewName = productsController.showProductList(model, session);

        // Verify
        assertEquals("products/index", viewName);
        verify(model).addAttribute(eq("products"), eq(userProducts));
    }

    @Test
    public void testShowProductList_UserNotLoggedIn() {
        // If user is not logged in, it should redirect to login page
        when(session.getAttribute("user")).thenReturn(null);

        String viewName = productsController.showProductList(model, session);

        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(productRepository);
    }
    @Test
    public void testCreateProduct_ValidInput() {
        // Mock
        User user = new User();
        when(session.getAttribute("user")).thenReturn(user);

        ProductDto productDto = new ProductDto();
        productDto.setName("Test Product");
        productDto.setImageFile(mock(MultipartFile.class));

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = productsController.createProduct(productDto, bindingResult, session);

        assertEquals("redirect:/products", viewName);
        verify(productRepository).save(any(Product.class));
    }
    @Test
    public void testCreateProduct_InvalidInput() {
        // Mock
        ProductDto productDto = new ProductDto();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        // Test
        String viewName = productsController.createProduct(productDto, bindingResult, session);

        // Verify
        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(productRepository);
    }

    @Test
    public void testShowProductList_UserLoggedInWithProducts() {
        // Mock
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        when(session.getAttribute("user")).thenReturn(user);

        List<Product> userProducts = new ArrayList<>();
        userProducts.add(new Product());
        when(productRepository.findByUserId(eq(user.getId()), any())).thenReturn(userProducts);

        // Test
        String viewName = productsController.showProductList(model, session);

        // Verify
        assertEquals("products/index", viewName);
        verify(model).addAttribute(eq("products"), eq(userProducts));
    }

    @Test
    public void testShowProductList_UserLoggedInWithNoProducts() {
        // Mock
        User user = new User();
        user.setId(1L);
        when(session.getAttribute("user")).thenReturn(user);

        List<Product> userProducts = new ArrayList<>();
        when(productRepository.findByUserId(anyLong(), any())).thenReturn(userProducts);

        // Test
        String viewName = productsController.showProductList(model, session);

        // Verify
        assertEquals("products/index", viewName);
        verify(model).addAttribute(eq("products"), isNull());
    }
    @Test
    public void testShowEditPage_ProductFound() {
        // Mock
        Product product = new Product();
        product.setId(1);
        when(productRepository.findById(anyInt())).thenReturn(Optional.of(product));

        // Test
        String viewName = productsController.showEditPage(model, 1, session);

        // Verify
        assertEquals("products/EditProduct", viewName);
        verify(model).addAttribute(eq("product"), eq(product));
    }

    @Test
    public void testShowEditPage_ProductNotFound() {
        // Mock
        when(productRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Test
        String viewName = productsController.showEditPage(model, 1, session);

        // Verify
        assertEquals("redirect:/products", viewName);
    }
    @Test
    public void testDeleteProduct_UserLoggedIn_ProductFound() {
        // Mock
        User user = new User();
        user.setId(1L);
        when(session.getAttribute("user")).thenReturn(user);

        Product product = new Product();
        product.setId(1);
        when(productRepository.findById(anyInt())).thenReturn(Optional.of(product));

        // Test
        String viewName = productsController.deleteProduct(1, session);

        // Verify
        assertEquals("redirect:/products", viewName);
        verify(productRepository).delete(product);
    }

    @Test
    public void testDeleteProduct_UserLoggedIn_ProductNotFound() {
        // Mock
        User user = new User();
        user.setId(1L);
        when(session.getAttribute("user")).thenReturn(user);

        when(productRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Test
        String viewName = productsController.deleteProduct(1, session);

        // Verify
        assertEquals("redirect:/products", viewName);
    }

    @Test
    public void testDeleteProduct_UserNotLoggedIn() {
        // Mock
        when(session.getAttribute("user")).thenReturn(null);

        // Test
        String viewName = productsController.deleteProduct(1, session);

        // Verify
        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(productRepository);
    }
    @Test
    public void testUpdateProduct_UserNotLoggedIn() {
        // Mock
        when(session.getAttribute("user")).thenReturn(null);

        // Test
        String viewName = productsController.updateProduct(model, 1, new ProductDto(), mock(BindingResult.class), session);

        // Verify
        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(productRepository);
    }

    @Test
    public void testUpdateProduct_ImageNotProvided() {
        // Mock
        when(session.getAttribute("user")).thenReturn(new User());
        when(productRepository.findById(anyInt())).thenReturn(Optional.of(new Product()));
        ProductDto productDto = new ProductDto(); // Image file not provided
        BindingResult result = mock(BindingResult.class);

        // Test
        String viewName = productsController.updateProduct(model, 1, productDto, result, session);

        // Verify
        assertEquals("redirect:/products", viewName);
        verify(productRepository, never()).save(any());
    }

    @Test
    public void testUpdateProduct_ImageFileException() {
        // Mock
        when(session.getAttribute("user")).thenReturn(new User());
        when(productRepository.findById(anyInt())).thenReturn(Optional.of(new Product()));
        ProductDto productDto = new ProductDto();
        productDto.setImageFile(mock(MultipartFile.class)); // Mock a MultipartFile object
        BindingResult result = mock(BindingResult.class);
        when(productRepository.save(any())).thenThrow(new RuntimeException("Image file exception"));

        // Test
        String viewName = productsController.updateProduct(model, 1, productDto, result, session);

        // Verify
        assertEquals("redirect:/products", viewName);
    }



}
