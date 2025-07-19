package com.example.userauthrest.Controller;



import com.example.databaseCore.Pojos.User.Req.PasswordChanged;
import com.example.databaseCore.Pojos.User.Req.UserRegister;
import com.example.userauthrest.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/user/v2")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserRegister userRegister) {
        return userService.register(userRegister);
    }

    @PutMapping("/logout/{user_id}")
    public ResponseEntity<Void> logout(@PathVariable("user_id") Long user_id) {
        return userService.logout(user_id);
    }

}
