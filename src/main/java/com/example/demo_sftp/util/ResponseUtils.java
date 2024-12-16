package com.example.demo_sftp.util;

import com.example.demo_sftp.dto.ImageFileDownloadDto;
import com.example.demo_sftp.dto.ImageFileUploadDto;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class ResponseUtils {
    private final String DEFAULT_MIME_IMAGE_TYPE = "image/png";

    public static String encodeFileName(String fileName) throws UnsupportedEncodingException {
        return URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replace("+", "%20");
    }

    @SneakyThrows
    public ResponseEntity<Object> handleExportResponse(ImageFileDownloadDto imageFileDownloadDto) {
//        InputStreamSource resource = new InputStreamResource(ByteSource.wrap(surveyImageDownloadDto.getBytes()).openStream());
        InputStreamSource resource = new InputStreamResource(new ByteArrayInputStream(imageFileDownloadDto.getBytes()));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename*=" + encodeFileName(imageFileDownloadDto.getFilename()));

        String mimeType = imageFileDownloadDto.getMimeType();
        if (StringUtils.isBlank(imageFileDownloadDto.getMimeType())) {
            mimeType = DEFAULT_MIME_IMAGE_TYPE;
        }
        String imageBase64 = "data:" +
                mimeType +
                ";base64, " +
                Base64.getEncoder().encodeToString(IOUtils.toByteArray(resource.getInputStream()));
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(imageBase64.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(imageBase64);
    }
}
