package com.example.serviceuserauthrest.Controller;

import com.example.serviceuserauthrest.Pojo.Request.UserRegister;
import com.example.serviceuserauthrest.Service.ServiceUserAuthRestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
