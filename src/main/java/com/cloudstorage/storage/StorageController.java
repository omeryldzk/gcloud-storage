package com.cloudstorage.storage;

import com.cloudstorage.storage.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/storage")
public class StorageController {

    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }
    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadImageToCloudStorage(@RequestPart("image") MultipartFile file) {
        try {
            String fileName = storageService.uploadFile(file);
            return ResponseEntity.ok("File uploaded successfully: " + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<Void> deleteImageFromCloudStorage(@PathVariable String fileName) {
        storageService.deleteFile(fileName);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/emailList")
    public ResponseEntity<Void> saveToEmailList(@RequestBody String email) {
        storageService.saveEmailList(email);
        return ResponseEntity.ok().build();
    }
}
