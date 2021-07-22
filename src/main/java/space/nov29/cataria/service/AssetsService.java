package space.nov29.cataria.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class AssetsService {
    private final S3Client s3Client;

    @Value("${cataria.config.s3.bucketName}")
    private String bucketName;

    private String filePrefix = "assets/";

    public AssetsService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String put(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file");
        }

        String newName = generateAssetsName(file);
        String filePath = filePrefix + newName;
        PutObjectRequest putObjectRequest = PutObjectRequest
                .builder()
                .bucket(bucketName)
                .key(filePath)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .contentType(file.getContentType())
                .cacheControl("public, max-age=315576000, immutable")
                .build();
        RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());
        s3Client.putObject(putObjectRequest, requestBody);

        return filePath;
    }

    private String generateAssetsName(MultipartFile file) {
        UUID uuid = UUID.randomUUID();
        String originalFileName = file.getOriginalFilename();
        if(originalFileName.length() < 4) return uuid.toString();
        return uuid + originalFileName.substring(originalFileName.length() - 4);
    }
}
