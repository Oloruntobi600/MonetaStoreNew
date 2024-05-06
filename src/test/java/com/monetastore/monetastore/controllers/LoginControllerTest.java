package com.monetastore.monetastore.controllers;

import com.monetastore.monetastore.Models.User;
import com.monetastore.monetastore.Service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class LoginControllerTest {

    @Mock
    private LoginService loginService;

    @InjectMocks
    private LoginController loginController;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLogin_ValidUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");

        when(loginService.login(anyString(), anyString())).thenReturn(user);

        String result = loginController.login(session, user, model);

        verify(session).setAttribute("user", user);
        assert result.equals("redirect:/products");
    }

    @Test
    public void testLogin_InvalidUser() {
        when(loginService.login(anyString(), anyString())).thenReturn(null);

        String result = loginController.login(session, new User(), model);

        verify(model).addAttribute("errorMessage", "Invalid Username/Password.");
        assert result.equals("products/login");
    }
    @Test
    public void testLogin_Successful() {
        // Mock
        User user = new User();
        when(loginService.login(anyString(), anyString())).thenReturn(user);

        // Test
        String viewName = loginController.login(session, new User(), model);

        // Verify
        assertEquals("products/login", viewName);
    }

    @Test
    public void testLogin_Unsuccessful() {
        // Mock
        when(loginService.login(anyString(), anyString())).thenReturn(null);

        // Test
        String viewName = loginController.login(session, new User(), model);

        // Verify
        assertEquals("products/login", viewName);
        verify(model).addAttribute("errorMessage", "Invalid Username/Password.");
    }

    @Test
    public void testAdminLogin() {
        // Test
        String viewName = loginController.adminLogin(model);

        // Verify
        assertEquals("products/login", viewName);
    }

    @Test
    public void testLogout() {
        // Test
        String viewName = loginController.logout(model, session);

        // Verify
        assertEquals("redirect:/products/login", viewName);
        verify(session).invalidate();

    }

    @Test
    public void testLogoutDo() {
        // Test
        String viewName = loginController.logoutDo(mock(HttpServletRequest.class), mock(HttpServletResponse.class));

        // Verify
        assertEquals("redirect:/login", viewName);
    }

}
