package com.example.demo_sftp.dto;

import lombok.Data;
import net.schmizz.sshj.xfer.InMemoryDestFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Data
public class ImageFileDownloadDto extends InMemoryDestFile {
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private String filename;
    private String mimeType;

    public ImageFileDownloadDto(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public long getLength() {
        return filename.length();
    }

    @Override
    public OutputStream getOutputStream(boolean b) throws IOException {
        return this.outputStream;
    }
    @Override
    public InMemoryDestFile getTargetFile(String filename) throws IOException {
        this.filename = filename;
        return this;
    }
    public byte[] getBytes() {
        return this.outputStream.toByteArray();
    }
}
