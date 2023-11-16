package cyou.arfsd.spendbackend.Utils;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Map;

@Component
public class MinioHelper {
    private final MinioClient minioClient;

    @Value("${MINIO_ACCESS_KEY}")
    private String accessKey;

    @Value("${MINIO_SECRET_KEY}")
    private String secretKey;

    @Value("${MINIO_HOST}")
    private String endpoint;

    @Value("${MINIO_BUCKET_NAME}")
    private String bucketName; // Not static

    public MinioHelper(@Value("${MINIO_HOST}") String endpoint,
                       @Value("${MINIO_ACCESS_KEY}") String accessKey,
                       @Value("${MINIO_SECRET_KEY}") String secretKey,
                       @Value("${MINIO_BUCKET_NAME}") String bucketName) {
        this.endpoint = endpoint;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.bucketName = bucketName;

        try {
            minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                String policy = "{\"Version\": \"2012-10-17\",\"Statement\": [{\"Effect\": \"Allow\",\"Principal\": \"*\",\"Action\": [\"s3:GetObject\"],\"Resource\": [\"arn:aws:s3:::"
                        + bucketName + "/*\"]}]}";
                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build());
            }
        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            // Handle the exception appropriately, log or throw
            e.printStackTrace();
            throw new RuntimeException("Error initializing MinioClient: " + e.getMessage());
        }
    }

    public Map<String, Object> uploadFile(MultipartFile file, String user) {
        String fileName = user + "/receipt-" + Timestamp.valueOf(java.time.LocalDateTime.now()).getTime()
                + (file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(
                                    file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());

            Map<String, Object> response = Map.of(
                    "status", "success",
                    "message", "File uploaded successfully!",
                    "fileName", fileName);
            return response;
        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            e.printStackTrace();
            Map<String, Object> response = Map.of(
                    "status", "S3 bucket error!",
                    "message", e.getMessage());
            return response;
        }
    }
}
