package com.example.demo_sftp.dto;

import net.schmizz.sshj.xfer.InMemorySourceFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageFileUploadDto extends InMemorySourceFile {
    private final InputStream inputStream;
    private final MultipartFile file;

    public ImageFileUploadDto(MultipartFile file) throws IOException{
        this.inputStream = new ByteArrayInputStream(file.getBytes());
        this.file = file;
    }

    @Override
    public String getName() {
        return file.getOriginalFilename();
    }

    @Override
    public long getLength() {
        return file.getSize();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.inputStream;
    }
}
