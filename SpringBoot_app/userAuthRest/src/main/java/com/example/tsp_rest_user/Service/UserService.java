package com.example.tsp_rest_user.Service;


import com.example.databaseCore.Entities.User.User;
import com.example.databaseCore.Pojos.User.Res.UserRes;
import com.example.databaseCore.Repositories.User.UserRepository;
import com.example.databaseCore.Pojos.User.Req.PasswordChanged;
import com.example.databaseCore.Pojos.User.Req.UserRegister;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
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

    public ResponseEntity<UserRes> getUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            User _user = user.get();
            UserRes userRes = new UserRes(_user.getFirstname(), _user.getLastname(), _user.getCreatedAt(), _user.getLastLogin());
            return ResponseEntity.ok(userRes);
        } else {
            log.error("Nie znaleziono użytkownika o ID: {}", userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    public ResponseEntity<Void> logout(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        try {
            if (user.isPresent()) {
                User _user = user.get();
                if (_user.getLoggedIn()) {
                    _user.setLastLogin(LocalDateTime.now());
                    _user.setLoggedIn(Boolean.FALSE);
                } else {
                    _user.setLoggedIn(Boolean.TRUE);
                }
                userRepository.save(_user);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<Void> verify_password(Long userId, PasswordChanged passwordChanged) {
        Optional<User> user = userRepository.findById(userId);

        try {
            if (user.isPresent()) {
                User _user = user.get();
                if (passwordChanged.getPassword().equals(passwordChanged.getNew_password()) && passwordEncoder.matches(passwordChanged.getPassword(), _user.getPassword())) {
                    _user.setPasswordChanged(Boolean.TRUE);
                    userRepository.save(_user);
                    return new ResponseEntity<>(HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<Void> password_change(Long userId, PasswordChanged passwordChanged) {
        Optional<User> user = userRepository.findById(userId);
        try {
            if (user.isPresent()) {
                User _user = user.get();
                if (passwordChanged.getPassword().equals(passwordChanged.getNew_password())) {
                    if (_user.getPasswordChanged()) {
                        _user.setPassword(passwordEncoder.encode(passwordChanged.getPassword()));
                        _user.setPasswordChanged(Boolean.FALSE);
                        userRepository.save(_user);
                        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                    } else {
                        return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<Void> account_delete(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        try {
            if (user.isPresent()) {
                User _user = user.get();
                userRepository.delete(_user);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
