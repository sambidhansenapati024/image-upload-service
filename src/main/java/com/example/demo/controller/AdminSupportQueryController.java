package com.example.demo.controller;

import com.example.demo.dto.AdminSupportQueryResponse;
import com.example.demo.service.adminSupportQuery.AdminSupportQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/support/queries")
public class AdminSupportQueryController {

    private final AdminSupportQueryService adminSupportQueryService;

    public AdminSupportQueryController(
            AdminSupportQueryService adminSupportQueryService) {

        this.adminSupportQueryService =
                adminSupportQueryService;
    }

    @GetMapping
    public ResponseEntity<List<AdminSupportQueryResponse>>
    getAllQueries() {

        return ResponseEntity.ok(
                adminSupportQueryService.getAllQueries()
        );
    }

    @GetMapping("/{queryId}")
    public ResponseEntity<AdminSupportQueryResponse>
    getQueryById(
            @PathVariable Long queryId) {

        return ResponseEntity.ok(
                adminSupportQueryService.getQueryById(
                        queryId
                )
        );
    }

    @PostMapping("/{queryId}/send-reset-link")
    public ResponseEntity<Void> sendPasswordResetLink(
            @PathVariable Long queryId) {

        adminSupportQueryService
                .sendPasswordResetLink(queryId);

        return ResponseEntity.ok().build();
    }
}