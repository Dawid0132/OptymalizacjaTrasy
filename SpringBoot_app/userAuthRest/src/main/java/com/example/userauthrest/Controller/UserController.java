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

    @PostMapping("/password/verify/{user_id}")
    public ResponseEntity<Void> password_verify(@PathVariable Long user_id, @RequestBody PasswordChanged passwordChanged) {
        return userService.verify_password(user_id, passwordChanged);
    }

    @PutMapping("/password/change/{user_id}")
    public ResponseEntity<Void> password_change(@PathVariable Long user_id, @RequestBody PasswordChanged passwordChanged) {
        return userService.password_change(user_id, passwordChanged);
    }

    @DeleteMapping("/account/delete/{user_id}")
    public ResponseEntity<Void> account_delete(@PathVariable Long user_id) {
        return userService.account_delete(user_id);
    }
}
