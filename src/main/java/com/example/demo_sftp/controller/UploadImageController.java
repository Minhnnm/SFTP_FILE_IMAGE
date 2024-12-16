package com.example.demo_sftp.controller;

import com.example.demo_sftp.service.HandleFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("image")
public class UploadImageController {
    @Autowired
    private HandleFileService handleFileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestPart("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(handleFileService.uploadFile(file));
    }

    @GetMapping("/download")
    public ResponseEntity<?> downloadFile(@RequestParam("imageId") UUID imageId) throws IOException {
        return ResponseEntity.ok(handleFileService.downloadFile(imageId));
    }

    @GetMapping("")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(handleFileService.findAll());
    }
}
