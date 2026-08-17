package com.example.demo.controller;

import com.example.demo.dto.SupportQueryDetailsResponse;
import com.example.demo.dto.SupportQueryListResponse;
import com.example.demo.dto.SupportQueryRequest;
import com.example.demo.dto.SupportQueryResponse;
import com.example.demo.service.supportQuerry.SupportQueryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/support/queries")
public class SupportQueryController {

    private final SupportQueryService supportQueryService;

    public SupportQueryController(
            SupportQueryService supportQueryService) {

        this.supportQueryService = supportQueryService;

    }

    @PostMapping
    public ResponseEntity<SupportQueryResponse> createQuery(
            @Valid @RequestBody SupportQueryRequest request) {

        SupportQueryResponse response =
                supportQueryService.createQuery(request);

        if (!response.isSuccess()) {

            return ResponseEntity
                    .badRequest()
                    .body(response);

        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/myQueries")
    public ResponseEntity<List<SupportQueryListResponse>> getMyQueries() {

        return ResponseEntity.ok(
                supportQueryService.getMyQueries()
        );

    }

    @GetMapping("/myQueriesById/{queryId}")
    public ResponseEntity<SupportQueryDetailsResponse> getMyQueryDetails(
            @PathVariable Long queryId) {

        return ResponseEntity.ok(
                supportQueryService.getMyQueryDetails(queryId)
        );
    }
}
