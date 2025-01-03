package com.cloudstorage.storage;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class StorageService {

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    @Value("${spring.cloud.gcp.storage.bucket.rest}")
    private String bucketNameRest;


    @Value("${spring.cloud.gcp.storage.bucket.emailList}")
    private String emailBucketName;

    private final Storage storage;

    public StorageService() {
        this.storage = StorageOptions.getDefaultInstance().getService();
    }


    public void deleteAllFilesUnderRestaurant(String restaurantId) {
        Page<Blob> blobs = storage.list(bucketNameRest, Storage.BlobListOption.prefix(restaurantId + "/"));
        for (Blob blob : blobs.iterateAll()) {
            storage.delete(blob.getBlobId());
        }
    }

    public void deleteAllFilesUnderUser(String username) {
        Page<Blob> blobs = storage.list(bucketName, Storage.BlobListOption.prefix(username + "/"));
        for (Blob blob : blobs.iterateAll()) {
            storage.delete(blob.getBlobId());
        }
    }

    public void saveEmailList(String email) {
        BlobId blobId = BlobId.of(emailBucketName, "emailList.txt");
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();

        // Read the existing content
        byte[] existingContent = storage.readAllBytes(blobId);
        String updatedContent = new String(existingContent) + email + "\n";

        // Write the updated content back to the file
        storage.create(blobInfo, updatedContent.getBytes());
    }
    public String uploadFile(String restaurantId,MultipartFile file) throws IOException {
        String fileName = restaurantId + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        BlobId blobId = BlobId.of(bucketNameRest, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();

        Blob blob = storage.create(blobInfo, file.getBytes());
        // Return file path
        return "https://storage.googleapis.com/" + bucketNameRest + "/" + fileName;
    }

    public String uploadFileUser(String username, MultipartFile file) throws IOException {
        String fileName = username + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();

        Blob blob = storage.create(blobInfo, file.getBytes());
        // Return file path
        return "https://storage.googleapis.com/" + bucketName + "/" + fileName;
    }
}