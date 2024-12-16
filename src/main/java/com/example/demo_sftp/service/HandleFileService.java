package com.example.demo_sftp.service;

import com.example.demo_sftp.entity.Image;
import javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface HandleFileService {
    public String uploadFile(MultipartFile multipartFile) throws IOException;
    public ResponseEntity<Object> downloadFile(UUID imageId) throws IOException;
    List<Image> findAll();
}
