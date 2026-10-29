package com.v2.competency.management.webservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.v2.competency.management.dtos.RolePlayUserPromptRequest;
import com.v2.competency.management.dtos.RolePlayUserPromptResponse;
import com.v2.competency.management.entities.RolePlayUserPromptMapping;
import com.v2.competency.management.service.RolePlayUserPromptMappingService;

@RestController
@CrossOrigin
@RequestMapping("/roleplay-user-prompts")
public class RolePlayUserPromptMappingController {

    @Autowired
    private RolePlayUserPromptMappingService service;

    @PostMapping("/save-or-update")
    public ResponseEntity<RolePlayUserPromptMapping> saveOrUpdate(
            @RequestBody RolePlayUserPromptRequest request,
            @RequestParam String token) {

        return ResponseEntity.ok(service.saveOrUpdate(request));
    }

    @GetMapping
    public ResponseEntity<RolePlayUserPromptResponse> getByTestNameAndCompanyId(
            @RequestParam String testName,
            @RequestParam String companyId,
            @RequestParam String token) {

        return ResponseEntity.ok(
                service.getByTestNameAndCompanyId(testName, companyId));
    }
}
