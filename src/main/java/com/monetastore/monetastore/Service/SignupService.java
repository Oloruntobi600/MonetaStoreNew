package com.monetastore.monetastore.Service;

import com.monetastore.monetastore.Repository.UserRepository;
import com.monetastore.monetastore.Models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignupService {

    @Autowired
    private UserRepository userRepository;

    public void signup(User user) {
        userRepository.save(user);
    }
}

