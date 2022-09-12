package com.aravinth.pdfsplitter.eventmgr.response;


import lombok.Data;

@Data
public class BlobResponse {
    private String fileId;
    private String fileName;
    private Long blobId;
}
