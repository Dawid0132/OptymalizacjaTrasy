package com.example.userauthrest.Service;

import com.example.databaseCore.Entities.User.User;
import com.example.databaseCore.Pojos.User.Req.UserRegister;
import com.example.databaseCore.Repositories.User.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<String> register(UserRegister userRegister) {
        Optional<User> user = userRepository.findUserByEmail(userRegister.get_email());
        if (user.isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } else if (!userRegister.get_password().equals(userRegister.get_confirm_password())) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            User _user = new User();
            _user.setFirstname(userRegister.get_firstname());
            _user.setLastname(userRegister.get_lastname());
            _user.setEmail(userRegister.get_email());
            _user.setPassword(passwordEncoder.encode(userRegister.get_password()));
            userRepository.save(_user);
            return ResponseEntity.ok("Pomyślnie udało Ci się zarejstrować");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
