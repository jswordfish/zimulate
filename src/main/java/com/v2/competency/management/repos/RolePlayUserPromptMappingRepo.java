package com.v2.competency.management.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.v2.competency.management.entities.RolePlayUserPromptMapping;

@Repository
public interface RolePlayUserPromptMappingRepo
        extends JpaRepository<RolePlayUserPromptMapping, Long> {

    RolePlayUserPromptMapping findByTest_IdAndCompanyId(
            Long testId, String companyId);
}