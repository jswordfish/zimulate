package com.v2.competency.management.webservices;


import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.v2.competency.management.dtos.GenericApiRequest;
import com.v2.competency.management.service.impl.GenericApiProxyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/proxy")
@RequiredArgsConstructor
public class ApiProxyController {

    private final GenericApiProxyService genericApiProxyService;

    @PostMapping("/call")
    public ResponseEntity<Object> callExternalApi(@Valid @RequestBody GenericApiRequest request) {
        return genericApiProxyService.callExternalApi(request);
    }
}
