package cyou.arfsd.spendbackend.Utils;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.errors.MinioException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

public class MinioHelper {
    // constructor
    public MinioClient minioClient;

    @Value("${minio.auth.accessKey}")
    private String accessKey;

    @Value("${minio.auth.secretKey}")
    private String secretKey;

    public MinioHelper(String endpoint, String bucketName, String user) {
        this.accessKey = "spr1ngb00t";
        this.secretKey = "spr1ngb00t";
        try {
            this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
            boolean found = this.minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                // make the bucket publicly readable w/o ACL
                this.minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                String policy = "{\"Version\": \"2012-10-17\",\"Statement\": [{\"Effect\": \"Allow\",\"Principal\": \"*\",\"Action\": [\"s3:GetObject\"],\"Resource\": [\"arn:aws:s3:::" + bucketName + "/*\"]}]}";
                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build());
            }
        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            System.out.println("Error occurred: " + e);
        }
    }

    public Map<String, Object> UploadFile(MultipartFile file, String bucketName, String user) {
        String fileName = user + "/receipt-" + Timestamp.valueOf(java.time.LocalDateTime.now()).getTime() + (file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
        try {
            this.minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(
                        file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
                );
            Map <String, Object> response = Map.of(
                "status", "success",
                "message", "File uploaded successfully!",
                "fileName", fileName
            );
            return response;
        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            Map <String, Object> response = Map.of(
                "status", "S3 bucket error!",
                "message", e.getMessage()
            );
            return response;
        }
    }
    
}
