package com.aravinth.pdfsplitter.eventmgr.scheduler;


import com.aravinth.pdfsplitter.eventmgr.response.PdfSplitWorkflowResponse;
import com.aravinth.pdfsplitter.eventmgr.service.EventManagerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class EventScheduler {
    private ObjectMapper objectMapper;

    private EventScheduler(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }
    @Autowired
    private EventManagerService eventManagerService;


    @Scheduled(fixedDelay = 10000)
    private void pdfSplitAndUploadScheduler() throws ExecutionException, InterruptedException,Exception {

        List<String> fileNames = eventManagerService.fileNameToBeProcessed();
        if (fileNames.isEmpty()) {
            log.info("No files to be processed");
        } else {
            try {
                log.info("Event Schedular calling API to get filenames to be processed");
                log.info("Scheduled pdf splitting starts");
                for (String fileName : fileNames) {
                    eventManagerService.eventMgrStarted(fileName);
                    Boolean splitResponse = eventManagerService.splitPdf(fileName);
                    Boolean uploadResponse = false;
                    if (splitResponse) {
                        uploadResponse = eventManagerService.uploadSplitPDF(fileName);
                    }
                    if (splitResponse && uploadResponse) {
                        eventManagerService.eventMgrCompleted(fileName);
                        log.info("Response for the filename:{} - {}", fileName, objectMapper.writeValueAsString("Success"));
                    } else {
                        log.info("Error occured in Event manager in one of the process");
                    }
                    log.info("Scheduled pdf splitting ends");
                    log.info("No new docs to get processed");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }
}
