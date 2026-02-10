package com.ecommerce.product.GCP.storage;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GcsDeleteService {

    @Value("${BucketName}")
    private String bucketName;

    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    public boolean deleteObject(String objectName) {

        BlobId blobId = BlobId.of(bucketName, objectName);

        boolean deleted = storage.delete(blobId);



        return deleted;
    }
}
