package com.aravinth.pdfsplitter.eventmgr.utils;

import com.aravinth.pdfsplitter.eventmgr.request.PdfSplitWorkflowRequest;
import com.aravinth.pdfsplitter.eventmgr.response.PdfSplitWorkflowResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WorkflowResponseTemplate {

    public  PdfSplitWorkflowRequest startedWorkflowRequest(String fileId,String Constant) {
        PdfSplitWorkflowRequest startedRequest = new PdfSplitWorkflowRequest();
        startedRequest.setFileId(fileId);
        startedRequest.setWorkFlowStepType(Constant);
        startedRequest.setWorkFlowStatusType(Constants.statusStarted);
        startedRequest.setErrorDescription(null);
        return startedRequest;
    }

    public  PdfSplitWorkflowRequest completedWorkflowRequest(String fileId,String Constant) {
        PdfSplitWorkflowRequest completedRequest = new PdfSplitWorkflowRequest();
        completedRequest.setFileId(fileId);
        completedRequest.setWorkFlowStepType(Constant);
        completedRequest.setWorkFlowStatusType(Constants.statusCompleted);
        completedRequest.setErrorDescription(null);
        return completedRequest;
    }

    public  PdfSplitWorkflowRequest exceptionWorkflowRequest(String fileId,String Constant,String errDesc) {
        PdfSplitWorkflowRequest completedRequest = new PdfSplitWorkflowRequest();
        completedRequest.setFileId(fileId);
        completedRequest.setWorkFlowStepType(Constant);
        completedRequest.setWorkFlowStatusType(Constants.statusException);
        completedRequest.setErrorDescription(errDesc);
        return completedRequest;
    }

    public List<PdfSplitWorkflowResponse> makeList(PdfSplitWorkflowResponse startedResponse, PdfSplitWorkflowResponse completedResponse){
        List<PdfSplitWorkflowResponse> responseList = new ArrayList<>();
        responseList.add(startedResponse);
        responseList.add(completedResponse);
        return  responseList;
    }


}
