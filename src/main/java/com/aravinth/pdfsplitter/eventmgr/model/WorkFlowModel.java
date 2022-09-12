package com.aravinth.pdfsplitter.eventmgr.model;



import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


@Data
public class WorkFlowModel {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String workFlowStepType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String workFlowStatusType;
}