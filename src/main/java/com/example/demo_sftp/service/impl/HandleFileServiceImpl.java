package com.example.demo_sftp.service.impl;

import com.example.demo_sftp.config.SftpConfig;
import com.example.demo_sftp.dto.ImageFileDownloadDto;
import com.example.demo_sftp.dto.ImageFileUploadDto;
import com.example.demo_sftp.entity.Image;
import com.example.demo_sftp.exception.NotFoundException;
import com.example.demo_sftp.repository.ImageRepository;
import com.example.demo_sftp.service.HandleFileService;
import com.example.demo_sftp.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.sftp.StatefulSFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.apache.tika.Tika;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class HandleFileServiceImpl implements HandleFileService {
    private final SftpConfig sftpConfig;
    private final Tika tika;
    private final ImageRepository imageRepository;
    private final ResponseUtils responseUtils;

//    public UploadFileServiceImpl(SftpConfig sftpConfig, Tika tika) {
//        this.sftpConfig = sftpConfig;
//        this.tika = tika;
//    }

    private SSHClient setupSshj() throws IOException {
        SSHClient sshClient = new SSHClient();
        //thêm 1 verify để check tính hợp lệ của HostKey khi connect ssh
        //PromiscuousVerifier là 1 verifier(lỏng lẻo) cho phép tất cả server mà ko kiểm tra tính hợp lệ của khóa server
        sshClient.addHostKeyVerifier(new PromiscuousVerifier());
        sshClient.connect(sftpConfig.getHost(), sftpConfig.getPort());
        sshClient.authPassword(sftpConfig.getUid(), sftpConfig.getPwd());
        return sshClient;
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        String mime = tika.detect(file.getInputStream());
        if (mime != null && mime.contains("image")) {
            LocalDateTime localDate = LocalDateTime.now();
            String dir = sftpConfig.getBasePath() + "/" + localDate.format(DateTimeFormatter.ofPattern("yyyy/dd-MM")) + "/" + localDate.toLocalTime();
            log.info("dir: " + dir);
            SSHClient sshClient = setupSshj();
            //khởi tạo và thiết lập một kết nối SFTP (SSH File Transfer Protocol) với máy chủ từ xa
            //cho phép thực hiện các thao tác truyền tải tập tin qua SFTP(StatefulSFTPClient)
            StatefulSFTPClient sftpClient = (StatefulSFTPClient) sshClient.newStatefulSFTPClient();
            if (sftpClient.statExistence(dir) == null) {
                sftpClient.mkdirs(dir);
            }
            sftpClient.cd(dir);
            String orginFileName = file.getOriginalFilename();
            String imagePath = Paths.get(UUID.randomUUID() + orginFileName.substring(orginFileName.lastIndexOf('.')))
                    .toString();
            Image image = Image.builder()
                    .name(dir).path(dir + "/" + imagePath).build();
            imageRepository.save(image);
            ImageFileUploadDto imageFileUploadDto = new ImageFileUploadDto(file);
            sftpClient.put(imageFileUploadDto, imagePath);
            sshClient.disconnect();
            return "OKKK";
        }
        throw new IOException("LOI");
    }

    @Override
    public ResponseEntity<Object> downloadFile(UUID imageId) throws IOException {
        Image image = imageRepository.findById(imageId).orElseThrow(
                () -> new NotFoundException("Can't find Image with imageId: " + imageId)
        );
        SSHClient sshClient = setupSshj();
        SFTPClient sftpClient = sshClient.newSFTPClient();
        ImageFileDownloadDto imageFileDownloadDto = new ImageFileDownloadDto("image/png");
        sftpClient.get(image.getPath(), imageFileDownloadDto);
        sshClient.disconnect();
        return responseUtils.handleExportResponse(imageFileDownloadDto);
    }

    @Override
    public List<Image> findAll() {
        return imageRepository.findAll();
    }
}
