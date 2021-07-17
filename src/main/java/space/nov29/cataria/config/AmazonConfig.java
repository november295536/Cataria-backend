package space.nov29.cataria.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.nio.file.Paths;

@Configuration
@Slf4j
public class AmazonConfig {

    private Region region = Region.AP_NORTHEAST_1;
    @Value("${nov29.one-S3-API.credentialFilePath}")
    private String credentialFilePath;

    @Bean
    public S3Client s3Client() {
        ProfileFile profileFile = ProfileFile
                .builder()
                .type(ProfileFile.Type.CREDENTIALS)
                .content(Paths.get(credentialFilePath))
                .build();
        ProfileCredentialsProvider profileCredentialProvider = ProfileCredentialsProvider
                .builder()
                .profileFile(profileFile)
                .build();

        return S3Client.builder()
                .region(region)
                .credentialsProvider(profileCredentialProvider)
                .build();
    }
}
