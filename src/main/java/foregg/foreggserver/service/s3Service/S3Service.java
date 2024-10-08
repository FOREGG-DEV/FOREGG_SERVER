package foregg.foreggserver.service.s3Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import foregg.foreggserver.jwt.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.UUID;

@Slf4j
@Service
public class S3Service {

    private final AmazonS3 amazonS3;
    private final String bucket;

    public S3Service(AmazonS3 amazonS3, @Value("${cloud.aws.s3.bucket}") String bucket) {
        this.amazonS3 = amazonS3;
        this.bucket = bucket;
    }

    public String upload(MultipartFile multipartFile) throws IOException {

        if (multipartFile == null) {
            return null;
        }

        String dirName = SecurityUtil.getCurrentUser();

        // 파일 이름에서 공백을 제거한 새로운 파일 이름 생성
        String originalFileName = multipartFile.getOriginalFilename();

        // UUID를 파일명에 추가
        String uuid = UUID.randomUUID().toString();
        String uniqueFileName = uuid + "_" + originalFileName.replaceAll("\\s", "_");

        String fileName = dirName + "/" + uniqueFileName;
        File uploadFile = convert(multipartFile);

        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);

        return uploadImageUrl;
    }

    private File convert(MultipartFile file) throws IOException {
        File convertFile = new File(file.getOriginalFilename()); // UUID 추가하지 않음
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            } catch (IOException e) {
                log.error("파일 변환 중 오류 발생: {}", e.getMessage());
                throw e;
            }
            return convertFile;
        }
        throw new IllegalArgumentException(String.format("파일 변환에 실패했습니다. %s", file.getOriginalFilename()));
    }


    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

    public void deleteFile(String fileName) {
        try {
            // URL 디코딩을 통해 원래의 파일 이름을 가져옵니다.
            String decodedFileName = URLDecoder.decode(fileName, "UTF-8");
            log.info("Deleting file from S3: " + decodedFileName);
            amazonS3.deleteObject(bucket, decodedFileName);
        } catch (UnsupportedEncodingException e) {
            log.error("Error while decoding the file name: {}", e.getMessage());
        }
    }

    public String updateFile(MultipartFile newFile, String oldFileName, String dirName) throws IOException {
        // 기존 파일 삭제
        log.info("S3 oldFileName: " + oldFileName);
        deleteFile(oldFileName);
        // 새 파일 업로드
        return upload(newFile);
    }

    public void deleteFileByUrl(String fileUrl) {
        try {
            // S3 URL에서 버킷 경로 이후의 파일 경로 추출
            String fileName = extractFilePathFromUrl(fileUrl);

            // 추출된 파일 경로로 삭제
            log.info("Deleting file from S3: " + fileName);
            amazonS3.deleteObject(bucket, fileName);

        } catch (UnsupportedEncodingException e) {
            log.error("Error while decoding the file URL: {}", e.getMessage());
        }
    }

    private String extractFilePathFromUrl(String fileUrl) throws UnsupportedEncodingException {
        // URL에서 버킷 이름 이후의 파일 경로만 추출
        String decodedUrl = URLDecoder.decode(fileUrl, "UTF-8");

        // S3 URL 형식에서 버킷 이름 이후의 파일 경로를 추출
        String filePath = decodedUrl.substring(decodedUrl.indexOf(bucket) + bucket.length() + 1);

        return filePath;
    }

}
