package com.v2.competency.management.repos;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.v2.competency.management.entities.WorkFlowSessionMetaData;

@Repository
public interface WorkFlowSessionMetaDataRepository
        extends JpaRepository<WorkFlowSessionMetaData, Long> {

    @Query(
        "SELECT w FROM WorkFlowSessionMetaData w " +
        "WHERE w.companyId = :companyId " +

        "AND (:rolePlaySessionId IS NULL " +
        "     OR w.rolePlaySessionId = :rolePlaySessionId) " +

        "AND (:elevenLabsSessionId IS NULL " +
        "     OR w.elevenLabsSessionId = :elevenLabsSessionId) " +

        "AND (:rolePlayTestName IS NULL " +
        "     OR w.rolePlayTestName LIKE %:rolePlayTestName%) " +

        "AND (:attempt IS NULL " +
        "     OR w.attempt = :attempt) " +

        "AND (:email IS NULL " +
        "     OR w.email LIKE %:email%) " +

        "AND (:startTime IS NULL " +
        "     OR w.startTime >= :startTime) " +

        "AND (:endTime IS NULL " +
        "     OR w.endTime <= :endTime) " +

        "AND (:durationInMinutes IS NULL " +
        "     OR w.durationInMinutes = :durationInMinutes) " +

        "AND (:tokenUsed IS NULL " +
        "     OR w.tokenUsed = :tokenUsed) " +

        "AND (" +
        "     :search IS NULL " +
        "     OR w.rolePlayTestName LIKE %:search% " +
        "     OR w.email LIKE %:search% " +
        ")"
    )
    Page<WorkFlowSessionMetaData> searchWorkFlowSessionMetaData(

            @Param("companyId")
            String companyId,

            @Param("rolePlaySessionId")
            Long rolePlaySessionId,

            @Param("elevenLabsSessionId")
            Long elevenLabsSessionId,

            @Param("rolePlayTestName")
            String rolePlayTestName,

            @Param("attempt")
            Integer attempt,

            @Param("email")
            String email,

            @Param("startTime")
            LocalDateTime startTime,

            @Param("endTime")
            LocalDateTime endTime,

            @Param("durationInMinutes")
            Integer durationInMinutes,

            @Param("tokenUsed")
            Long tokenUsed,

            @Param("search")
            String search,

            Pageable pageable
    );
}
