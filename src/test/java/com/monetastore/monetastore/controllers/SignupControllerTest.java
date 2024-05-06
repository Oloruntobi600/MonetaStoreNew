package com.monetastore.monetastore.controllers;

import com.monetastore.monetastore.Models.User;
import com.monetastore.monetastore.Service.SignupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SignupControllerTest {

    @Mock
    private SignupService signupService;

    @InjectMocks
    private SignupController signupController;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testShowSignupForm() {
        String viewName = signupController.showSignupForm(model);
        assertEquals("products/signup", viewName);
    }

    @Test
    public void testSignup_Successful() {
        User user = new User();
        when(bindingResult.hasErrors()).thenReturn(false);

        String redirectUrl = signupController.signup(user, bindingResult);
        assertEquals("redirect:/login", redirectUrl);
        verify(signupService).signup(user);
    }

    @Test
    public void testSignup_WithErrors() {
        User user = new User();
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = signupController.signup(user, bindingResult);
        assertEquals("products/signup", viewName);
    }
}
