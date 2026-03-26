package com.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * DEMO VULNERABILITY - SAST (Coverity will flag this)
     * CWE-89: SQL Injection
     *
     * FIX for demo: replace with parameterized query:
     *   return jdbcTemplate.queryForList("SELECT * FROM users WHERE name = ?", name);
     */
    @GetMapping("/users")
    public List<Map<String, Object>> getUsers(@RequestParam String name) {
        return jdbcTemplate.queryForList("SELECT * FROM users WHERE name = ?", name);
    }

    @GetMapping("/products")
    public List<Map<String, Object>> getProducts(@RequestParam(required = false) String category) {
        return jdbcTemplate.queryForList("SELECT * FROM products");
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "blackduck-demo");
        status.put("version", "1.0.1");
        return status;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> credentials) {
        // Simple login endpoint - target for DAST authentication testing
        Map<String, String> response = new HashMap<>();
        String username = credentials.getOrDefault("username", "");
        String password = credentials.getOrDefault("password", "");

        if ("admin".equals(username) && "password123".equals(password)) {
            response.put("status", "success");
            response.put("token", "demo-token-12345");
        } else {
            response.put("status", "unauthorized");
        }
        return response;
    }
}
