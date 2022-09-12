package com.aravinth.pdfsplitter.eventmgr.response;



import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
public class PdfSplitWorkflowResponse {
    private String fileId;
    private Long workFlowDtlId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String workFlowStepType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String workFlowStatusType;
    private Date createdDateTime;
    private String errorDescription;
}