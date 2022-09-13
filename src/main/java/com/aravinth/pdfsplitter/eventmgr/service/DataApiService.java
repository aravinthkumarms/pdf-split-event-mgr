package com.aravinth.pdfsplitter.eventmgr.service;


import com.aravinth.pdfsplitter.eventmgr.request.PdfSplitWorkflowRequest;
import com.aravinth.pdfsplitter.eventmgr.response.BlobResponse;
import com.aravinth.pdfsplitter.eventmgr.response.ConnectionResponse;
import com.aravinth.pdfsplitter.eventmgr.response.PdfSplitWorkflowResponse;
import com.aravinth.pdfsplitter.eventmgr.utils.HTTPAgent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DataApiService {

    private ObjectMapper objectMapper;

    private DataApiService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Value("${DATA_API_HOST}")
    public String dataApiHost;

    @Value("${PDF_SPLIT_HOST}")
    public String pdfSplitHost;

    @Autowired
    public HTTPAgent httpAgent;

    public BlobResponse getBlobResponse(String fileName) {
        log.info("Calling DATA API {} for BlobResponse", dataApiHost);
        String url = dataApiHost + "blob/response/v2/?fileName=" + fileName;
        log.info(url);
        ResponseEntity<BlobResponse> response;
        response = httpAgent.get(url, BlobResponse.class, fileName);
        return response.getBody();
    }

    public ConnectionResponse getConnection() {
        log.info("Calling API {}", pdfSplitHost);
        String url = pdfSplitHost + "try/connect";
        log.info(url);
        ResponseEntity<ConnectionResponse> response;
        response = httpAgent.get(url, ConnectionResponse.class);
        return response.getBody();
    }

    @SneakyThrows
    public PdfSplitWorkflowResponse createWorkflow(PdfSplitWorkflowRequest pdfSplitWorkflowRequest){
        log.info("Calling DATA API {}",dataApiHost);
        String url = dataApiHost+"blob/createwrkflw/"+pdfSplitWorkflowRequest.getFileId()+"/v1";
        log.info(url);
        ResponseEntity<PdfSplitWorkflowResponse> response;
        response = httpAgent.post(url,pdfSplitWorkflowRequest,PdfSplitWorkflowResponse.class);
        return response.getBody();
    }
}
