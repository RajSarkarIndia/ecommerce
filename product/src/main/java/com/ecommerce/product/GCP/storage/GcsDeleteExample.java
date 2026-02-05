package com.ecommerce.product.GCP.storage;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GcsDeleteExample {

    public static boolean deleteObject(@Value("${gcp.project-id}") String projectId, @Value("${gcp.bucket-name}")String bucketName,String objectName) {

        Storage storage = StorageOptions.getDefaultInstance().getService();

        BlobId blobId = BlobId.of(bucketName, objectName);

        boolean deleted = storage.delete(blobId);

        if (deleted) {
            System.out.println("Object deleted successfully.");
        } else {
            System.out.println("Object not found or already deleted.");
        }

        return deleted;
    }
}

