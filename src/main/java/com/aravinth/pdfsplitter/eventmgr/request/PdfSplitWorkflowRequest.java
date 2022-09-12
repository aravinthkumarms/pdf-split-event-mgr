package com.aravinth.pdfsplitter.eventmgr.request;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class PdfSplitWorkflowRequest {
    private String fileId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String workFlowStepType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String workFlowStatusType;
    private String errorDescription;
}