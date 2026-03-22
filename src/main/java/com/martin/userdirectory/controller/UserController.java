package com.martin.userdirectory.controller;

import com.martin.userdirectory.dto.CreateUserRequest;
import com.martin.userdirectory.dto.UserResponse;
import com.martin.userdirectory.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tenants/{tenantId}/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @PathVariable Long tenantId,
            @RequestBody CreateUserRequest request) {
        request.setTenantId(tenantId);
        UserResponse user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(
            @PathVariable Long tenantId,
            @PathVariable Long userId) {
        UserResponse user = userService.getUser(tenantId, userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsersByTenant(@PathVariable Long tenantId) {
        List<UserResponse> users = userService.getUsersByTenant(tenantId);
        return ResponseEntity.ok(users);
    }
}