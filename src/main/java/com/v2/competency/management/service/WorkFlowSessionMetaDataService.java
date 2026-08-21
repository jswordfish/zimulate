package com.v2.competency.management.service;

import java.time.LocalDateTime;

import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.entities.WorkFlowSessionMetaData;

public interface WorkFlowSessionMetaDataService {

    WorkFlowSessionMetaData saveWorkFlowSessionMetaData(
            WorkFlowSessionMetaData metaData
    );

    PaginatedResponseDto listWorkFlowSessionMetaData(

            String companyId,

            Long rolePlaySessionId,

            Long elevenLabsSessionId,

            String rolePlayTestName,

            Integer attempt,

            String email,

            LocalDateTime startTime,

            LocalDateTime endTime,

            Integer durationInMinutes,

            Long tokenUsed,

            String search,

            String sort,

            int page,

            int size
    );
}
