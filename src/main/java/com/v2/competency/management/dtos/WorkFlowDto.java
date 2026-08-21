package com.v2.competency.management.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkFlowDto {

    String name;
    String objective;
    String industry;
    Long id;
    String companyId;
    Boolean complete;
    Boolean navigationBack;
    Boolean canBeUpdated;
    Boolean canBeDeleted;
}
