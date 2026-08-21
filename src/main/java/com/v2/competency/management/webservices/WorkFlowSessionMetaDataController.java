package com.v2.competency.management.webservices;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.entities.WorkFlowSessionMetaData;
import com.v2.competency.management.service.WorkFlowSessionMetaDataService;

@RestController
@CrossOrigin
@RequestMapping("/workFlowSessionMetaData")
public class WorkFlowSessionMetaDataController {

    @Autowired
    private WorkFlowSessionMetaDataService service;


    @PostMapping
    public ResponseEntity<WorkFlowSessionMetaData> saveWorkFlowSessionMetaData(

            @RequestBody
            WorkFlowSessionMetaData metaData,

            @RequestParam
            String token
    ) {

        return ResponseEntity.ok(
                service.saveWorkFlowSessionMetaData(
                        metaData
                )
        );
    }


    @GetMapping
    public ResponseEntity<PaginatedResponseDto> listWorkFlowSessionMetaData(

            @RequestParam
            String companyId,


            @RequestParam(required = false)
            Long rolePlaySessionId,


            @RequestParam(required = false)
            Long elevenLabsSessionId,


            @RequestParam(required = false)
            String rolePlayTestName,


            @RequestParam(required = false)
            Integer attempt,


            @RequestParam(required = false)
            String email,


            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startTime,


            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endTime,


            @RequestParam(required = false)
            Integer durationInMinutes,


            @RequestParam(required = false)
            Long tokenUsed,


            @RequestParam(required = false)
            String search,


            @RequestParam(required = false)
            String sort,


            @RequestParam(defaultValue = "0")
            int page,


            @RequestParam(defaultValue = "10")
            int size,


            @RequestParam
            String token
    ) {

        return ResponseEntity.ok(

                service.listWorkFlowSessionMetaData(

                        companyId,

                        rolePlaySessionId,

                        elevenLabsSessionId,

                        rolePlayTestName,

                        attempt,

                        email,

                        startTime,

                        endTime,

                        durationInMinutes,

                        tokenUsed,

                        search,

                        sort,

                        page,

                        size
                )
        );
    }
}
