package com.example.serviceuserauthrest.Controller;

import com.example.serviceuserauthrest.Pojo.Request.PasswordChanged;
import com.example.serviceuserauthrest.Pojo.Request.UserRegister;
import com.example.serviceuserauthrest.Service.ServiceUserAuthRestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/user/v1")
public class UserServiceController {
    private final ServiceUserAuthRestService serviceUserAuthRestService;

    public UserServiceController(ServiceUserAuthRestService serviceUserAuthRestService) {
        this.serviceUserAuthRestService = serviceUserAuthRestService;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<String> register(@RequestBody UserRegister userRegister) {
        return ResponseEntity.ok(serviceUserAuthRestService.register(userRegister));
    }

    @GetMapping(path = "/logout/{user_id}")
    public ResponseEntity<Void> logout(@PathVariable("user_id") Long user_id, @RequestHeader("Authorization") String authToken) {
        return ResponseEntity.ok(serviceUserAuthRestService.logout(user_id, authToken));
    }

    @PostMapping(path = "/password/change/{user_id}")
    public ResponseEntity<Void> password_change(@PathVariable("user_id") Long user_id, @RequestBody PasswordChanged passwordChanged, @RequestHeader("Authorization") String authToken) {
        return ResponseEntity.ok(serviceUserAuthRestService.password_change(user_id, passwordChanged, authToken));
    }

    @PostMapping(path = "/password/verify/{user_id}")
    public ResponseEntity<Void> password_verify(@PathVariable("user_id") Long user_id, @RequestBody PasswordChanged passwordChanged, @RequestHeader("Authorization") String authToken) {
        return ResponseEntity.ok(serviceUserAuthRestService.password_verify(user_id, passwordChanged, authToken));
    }

    @GetMapping(path = "/account/delete/{user_id}")
    public ResponseEntity<Void> account_delete(@PathVariable("user_id") Long user_id, @RequestHeader("Authorization") String authToken) {
        return ResponseEntity.ok(serviceUserAuthRestService.account_delete(user_id, authToken));
    }

}
