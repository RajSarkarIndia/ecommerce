package com.ecommerce.product.GCP.storage;


import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class GenerateV4PutObjectSignedUrl {

    @Value("${BucketName}")
    private String bucketName;

    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    public void uploadFile(MultipartFile file, String fileName) throws IOException {


        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());

       return;
    }
}
