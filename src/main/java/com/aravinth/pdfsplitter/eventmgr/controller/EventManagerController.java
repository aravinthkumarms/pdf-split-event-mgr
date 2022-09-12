package com.aravinth.pdfsplitter.eventmgr.controller;


import com.aravinth.pdfsplitter.eventmgr.response.BlobResponse;
import com.aravinth.pdfsplitter.eventmgr.response.ConnectionResponse;
import com.aravinth.pdfsplitter.eventmgr.response.PdfSplitWorkflowResponse;
import com.aravinth.pdfsplitter.eventmgr.service.EventManagerService;
import com.aravinth.pdfsplitter.eventmgr.service.PdfSplitService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class EventManagerController {

    @Autowired
    private PdfSplitService pdfSplitService;

    @Autowired
    private EventManagerService eventManagerService;

    @GetMapping("blob/response/{fileName}")
    public BlobResponse getBlobResponse(@PathVariable String fileName){
        log.info("Calling PdfSplitService.getBlobResponse for API Call");
        BlobResponse response = pdfSplitService.getBlobResponse(fileName);
        return response;
    }

    @GetMapping("pdfsplit/connect")
    public ConnectionResponse connectionResponse(){
        ConnectionResponse response = pdfSplitService.getConnection();
        return response;
    }

    @GetMapping("pdfsplit/{fileName}")
    public ResponseEntity<Object> splitPdf(@PathVariable String fileName) throws IOException {
        eventManagerService.splitPdf(fileName);
        return new ResponseEntity<>("success", HttpStatus.CREATED);
    }

    @GetMapping("upload/pdfsplit/{fileName}")
    public ResponseEntity<Object> uploadSplitPDF(@PathVariable String fileName){
        eventManagerService.uploadSplitPDF(fileName);
        return new ResponseEntity<>("success", HttpStatus.CREATED);
    }

    @GetMapping("split/upload/{fileName}")
    public ResponseEntity<Object> splitAndUpload(@PathVariable String fileName) throws IOException {
        eventManagerService.eventMgrStarted(fileName);
        eventManagerService.splitPdf(fileName);
        eventManagerService.uploadSplitPDF(fileName);
        return new ResponseEntity<>("success", HttpStatus.CREATED);
    }

    @GetMapping("filename/response")
    public List<String> fileNameResponse(){
        List<String> responseList= eventManagerService.fileNameToBeProcessed();
        return responseList;
    }
}
