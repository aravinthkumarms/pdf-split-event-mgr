package com.aravinth.pdfsplitter.eventmgr.service;


import com.aravinth.pdfsplitter.eventmgr.request.PdfSplitWorkflowRequest;
import com.aravinth.pdfsplitter.eventmgr.response.BlobResponse;
import com.aravinth.pdfsplitter.eventmgr.response.ConnectionResponse;
import com.aravinth.pdfsplitter.eventmgr.response.PdfSplitWorkflowResponse;
import com.aravinth.pdfsplitter.eventmgr.utils.Constants;
import com.aravinth.pdfsplitter.eventmgr.utils.HTTPAgent;
import com.aravinth.pdfsplitter.eventmgr.utils.WorkflowResponseTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class EventManagerService  {
    private ObjectMapper objectMapper;

    private EventManagerService(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }
    @Autowired
    private PdfSplitService pdfSplitService;

    @Value("${PDF_SPLIT_HOST}")
    public String pdfSplitHost;

    @Value("${DATA_API_HOST}")
    public String dataApiHost;

    @Autowired
    public HTTPAgent httpAgent;

    @Autowired
    public WorkflowResponseTemplate workflowResponseTemplate;
    @SneakyThrows
    public String getFileId(String fileName){
        BlobResponse blobResponse = pdfSplitService.getBlobResponse(fileName);
        log.info("BlobResponse for the filename {} - {}",fileName,objectMapper.writeValueAsString(blobResponse));
        String fileId = blobResponse.getFileId();
        return fileId;
    }

    public ResponseEntity<PdfSplitWorkflowResponse> eventMgrStarted(String fileName){
        String fileId = getFileId(fileName);
        PdfSplitWorkflowRequest startedRequest = workflowResponseTemplate.startedWorkflowRequest(fileId,Constants.eventMgr);
        PdfSplitWorkflowResponse pdfSplitWorkflowStartedResponse = pdfSplitService.createWorkflow(startedRequest);
        log.info("Event Manager started");
        return new ResponseEntity(pdfSplitWorkflowStartedResponse,HttpStatus.CREATED);
    }
    public ResponseEntity<PdfSplitWorkflowResponse> eventMgrCompleted(String fileName){
        String fileId = getFileId(fileName);
        PdfSplitWorkflowRequest completedRequest = workflowResponseTemplate.completedWorkflowRequest(fileId,Constants.eventMgr);
        PdfSplitWorkflowResponse pdfSplitWorkflowComlpletedResponse = pdfSplitService.createWorkflow(completedRequest);
        log.info("Event Manager Completed");
        return new ResponseEntity(pdfSplitWorkflowComlpletedResponse,HttpStatus.CREATED);
    }

    @SneakyThrows
    public Boolean splitPdf(String fileName)  {
        String fileId = getFileId(fileName);
        try {
            String url = pdfSplitHost+"split/file/v2/"+fileName;
            log.info("Calling API {}",url);
            log.info("Splitting PDF Job Starts");
            PdfSplitWorkflowRequest startedRequest = workflowResponseTemplate.startedWorkflowRequest(fileId,Constants.split);
            PdfSplitWorkflowResponse pdfSplitWorkflowStartedResponse = pdfSplitService.createWorkflow(startedRequest);
            log.info("Created Workflow Detail for the fileId {} with request {}",fileId,objectMapper.writeValueAsString(startedRequest));
            ResponseEntity<ConnectionResponse> response;
            response= httpAgent.get(url, ConnectionResponse.class);
            log.info("Splitting App Response {}",objectMapper.writeValueAsString(response.getBody()));
            log.info("Splitting PDF Job Ends");
            PdfSplitWorkflowRequest completedRequest = workflowResponseTemplate.completedWorkflowRequest(fileId,Constants.split);
            PdfSplitWorkflowResponse pdfSplitWorkflowCompletedResponse= pdfSplitService.createWorkflow(completedRequest);
            log.info("Created Workflow Detail for the fileId {} with request {}",fileId,objectMapper.writeValueAsString(completedRequest));
            List<PdfSplitWorkflowResponse> responseList = workflowResponseTemplate.makeList(pdfSplitWorkflowStartedResponse,pdfSplitWorkflowCompletedResponse);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            log.error("{}",e);
            PdfSplitWorkflowRequest exceptionRequest = workflowResponseTemplate.exceptionWorkflowRequest(fileId,Constants.split, String.valueOf(e));
            PdfSplitWorkflowResponse pdfSplitWorkflowExceptionResponse= pdfSplitService.createWorkflow(exceptionRequest);
            log.info("Created Workflow Detail for the fileId {} with request {}",fileId,objectMapper.writeValueAsString(exceptionRequest));
            List<PdfSplitWorkflowResponse> responseList = new ArrayList<>();
            responseList.add(pdfSplitWorkflowExceptionResponse);
            return false;
        }

    }

    @SneakyThrows
    public Boolean uploadSplitPDF(String fileName) {
        String fileId = getFileId(fileName);

        try {
            String url = pdfSplitHost + "upload/file/v1/" + fileName;
            log.info("Calling API {}", url);
            log.info("Uploading Splitted Images to GCS Starts");
            PdfSplitWorkflowRequest startedRequest = workflowResponseTemplate.startedWorkflowRequest(fileId, Constants.delivery);
            PdfSplitWorkflowResponse pdfSplitWorkflowStartedResponse = pdfSplitService.createWorkflow(startedRequest);
            log.info("Created Workflow Detail for the fileId {} with request {}", fileId, objectMapper.writeValueAsString(startedRequest));
            ResponseEntity<ConnectionResponse> response;
            response = httpAgent.get(url, ConnectionResponse.class);
            log.info("Splitting App Response {}", objectMapper.writeValueAsString(response.getBody()));
            log.info("Splitting PDF Job Ends");
            PdfSplitWorkflowRequest completedRequest = workflowResponseTemplate.completedWorkflowRequest(fileId, Constants.delivery);
            PdfSplitWorkflowResponse pdfSplitWorkflowCompletedResponse = pdfSplitService.createWorkflow(completedRequest);
            log.info("Created Workflow Detail for the fileId {} with request {}", fileId, objectMapper.writeValueAsString(completedRequest));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("{}", e);
            PdfSplitWorkflowRequest exceptionRequest = workflowResponseTemplate.exceptionWorkflowRequest(fileId, Constants.delivery, String.valueOf(e));
            PdfSplitWorkflowResponse pdfSplitWorkflowExceptionResponse= pdfSplitService.createWorkflow(exceptionRequest);
            log.info("Created Workflow Detail for the fileId {} with request {}",fileId,objectMapper.writeValueAsString(exceptionRequest));
            List<PdfSplitWorkflowResponse> responseList = new ArrayList<>();
            responseList.add(pdfSplitWorkflowExceptionResponse);
            return false;

        }
    }

    public List<String> fileNameToBeProcessed(){
        try {
            String url = dataApiHost + "get/filenames/to/process";
            ResponseEntity<List> fileNames = httpAgent.get(url, List.class);
            log.info("FileName resposne from status code - {} ,DATA-API - {}", fileNames.getStatusCode(), objectMapper.writeValueAsString(fileNames.getBody()));
            return fileNames.getBody();
        }
        catch(ResourceAccessException e){
            log.info("Error occured while connecting to DATA API");
            return (List<String>) e;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
