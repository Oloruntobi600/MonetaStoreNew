package com.monetastore.monetastore.Service;


import com.monetastore.monetastore.Models.User;
import com.monetastore.monetastore.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class LoginService {

    @Autowired
    private UserRepository repo;


    public User login(String username, String password) {
        Optional<User> userOptional = repo.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

}