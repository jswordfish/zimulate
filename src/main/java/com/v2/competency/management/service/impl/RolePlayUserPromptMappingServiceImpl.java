package com.v2.competency.management.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.v2.competency.management.dtos.RolePlayUserPromptRequest;
import com.v2.competency.management.dtos.RolePlayUserPromptResponse;
import com.v2.competency.management.entities.RolePlayUserPromptMapping;
import com.v2.competency.management.entities.VFRolePlayTest;
import com.v2.competency.management.repos.RolePlayUserPromptMappingRepo;
import com.v2.competency.management.repos.VFRolePlayTestRepo;
import com.v2.competency.management.service.RolePlayUserPromptMappingService;

@Service
public class RolePlayUserPromptMappingServiceImpl
        implements RolePlayUserPromptMappingService {

    @Autowired
    private RolePlayUserPromptMappingRepo repo;

    @Autowired
    private VFRolePlayTestRepo testRepo;

    @Override
    @Transactional
    public RolePlayUserPromptMapping saveOrUpdate(
            RolePlayUserPromptRequest request) {

        VFRolePlayTest test = testRepo.findUniqueRecord(
                request.getTestName(),
                request.getCompanyId());

        if (test == null) {
            throw new RuntimeException(
                    "RolePlayTest does not exist for testName: "
                    + request.getTestName()
                    + " and companyId: "
                    + request.getCompanyId());
        }

        RolePlayUserPromptMapping existing =
                repo.findByTest_IdAndCompanyId(
                        test.getId(),
                        request.getCompanyId());

        if (existing != null) {

            existing.setUserPrompt(request.getUserPrompt());
            existing.setUpdateDate(new Date());

            return repo.save(existing);

        } else {

            RolePlayUserPromptMapping mapping =
                    new RolePlayUserPromptMapping();

            mapping.setTest(test);
            mapping.setUserPrompt(request.getUserPrompt());
            mapping.setCompanyId(request.getCompanyId());
            mapping.setCreateDate(new Date());

            return repo.save(mapping);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RolePlayUserPromptResponse getByTestNameAndCompanyId(
            String testName, String companyId) {

        VFRolePlayTest test =
                testRepo.findUniqueRecord(testName, companyId);

        if (test == null) {
            throw new RuntimeException(
                    "RolePlayTest does not exist for testName: "
                    + testName
                    + " and companyId: "
                    + companyId);
        }

        RolePlayUserPromptMapping mapping =
                repo.findByTest_IdAndCompanyId(
                        test.getId(), companyId);

        RolePlayUserPromptResponse response =
                new RolePlayUserPromptResponse();

        response.setTest(test);
        response.setUserPrompt(
                mapping != null ? mapping.getUserPrompt() : null);

        return response;
    }
}