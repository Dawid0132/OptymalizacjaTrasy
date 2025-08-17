package com.example.tsp_rest_user_service.Controller;

import com.example.tsp_rest_user_service.Pojo.Request.PasswordChanged;
import com.example.tsp_rest_user_service.Pojo.Request.UserRegister;
import com.example.tsp_rest_user_service.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/user/v1")
public class UserServiceController {

    private final UserService userService;

    public UserServiceController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<String> register(@RequestBody UserRegister userRegister) {
        return ResponseEntity.ok(userService.register(userRegister));
    }

    @GetMapping(path = "/{user_id}/user/get")
    public ResponseEntity<Object> getUser(@PathVariable("user_id") Long user_id) {
        return ResponseEntity.ok(userService.getUser(user_id));
    }

    @GetMapping(path = "/{user_id}/logout")

    public ResponseEntity<Void> logout(@PathVariable("user_id") Long user_id) {
        return ResponseEntity.ok(userService.logout(user_id));
    }

    @PostMapping(path = "/{user_id}/password/change")
    public ResponseEntity<Void> password_change(@PathVariable("user_id") Long user_id, @RequestBody PasswordChanged passwordChanged) {
        return ResponseEntity.ok(userService.password_change(user_id, passwordChanged));
    }

    @PostMapping(path = "/{user_id}/password/verify")
    public ResponseEntity<Void> password_verify(@PathVariable("user_id") Long user_id, @RequestBody PasswordChanged passwordChanged) {
        return ResponseEntity.ok(userService.password_verify(user_id, passwordChanged));
    }

    @GetMapping(path = "/{user_id}/account/delete")
    public ResponseEntity<Void> account_delete(@PathVariable("user_id") Long user_id) {
        return ResponseEntity.ok(userService.account_delete(user_id));
    }


}
