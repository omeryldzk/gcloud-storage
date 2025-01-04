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

    @PostMapping(value = "user-picture/{userId}/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadImageToCloudStorage(@RequestPart("image") MultipartFile file,@PathVariable String userId ) {
        try {
            String fileUrl = storageService.uploadFileUser(userId,file);
            return ResponseEntity.ok( fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }

    @PostMapping(value = "restaurant-picture/{restaurantId}/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadRestImageToCloudStorage(@RequestPart("image") MultipartFile file,
                                                                @PathVariable String restaurantId) {
        try {
            String fileUrl = storageService.uploadFile(restaurantId,file);
            return ResponseEntity.ok( fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }

    @DeleteMapping("user-picture/{username}/delete")
    public ResponseEntity<Void> deleteImageFromCloudStorage(@PathVariable String username) {
        storageService.deleteAllFilesUnderUser(username);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("restaurant-picture/{restaurantId}/delete")
    public ResponseEntity<Void> deleteRestImageFromCloudStorage(@PathVariable String restaurantId) {
        storageService.deleteAllFilesUnderRestaurant(restaurantId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/emailList")
    public ResponseEntity<Void> saveToEmailList(@RequestBody String email) {
        storageService.saveEmailList(email);
        return ResponseEntity.ok().build();
    }
}