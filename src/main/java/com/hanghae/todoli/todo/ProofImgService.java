package com.hanghae.todoli.todo;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hanghae.todoli.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProofImgService {

    /**
     * 인증 사진 등록
     * - 사진 재등록시 기존 이미지 삭제
     * - 사진 등록시 인증 날짜 = 종료일 + 3
     */

    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final TodoRepository todoRepository;

    @Transactional
    public void imgRegister(Long id, ProofImgRequestDto imgRequestDto, UserDetailsImpl userDetails) {
        // 개시글 조회
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Todo가 존재하지 않습니다."));

        // 로그인 사용자 가져와서 작성자와 일치하는지 확인
        Long id1 = userDetails.getMember().getId();

        if (!todo.getWriter().getId().equals(id1)) {
            throw new IllegalArgumentException("투두 작성자가 아닙니다!");
        }

        // url이  not null 일 때
        if (todo.getProofImg() != null) {
            // S3에서 기존 이미지 삭제
//          https://twodo-li.s3.ap-northeast-2.amazonaws.com/
            String imgUrl = todo.getProofImg();

            String imgName = imgUrl.substring(49);

            // S3 저장된 기존 이미지 삭제
            deleteFile(imgName);
        }

        // 이미지 url을 String으로 변환
        String proofImgUrl = getImgUrl(imgRequestDto.getProofImg());

        /**
         * 인증 가능 날짜 = 종료일 + 3
         * 이미 url이 존재 하는 경우에는 증가 X
         * url 초기 등록시만 +3
         */
        if (todo.getProofImg() == null){
            todo.setConfirmDate(todo.getEndDate().plusDays(3));
        }

        // 이미지 url 저장
        todo.setProofImg(proofImgUrl);
    }

    private String getImgUrl(MultipartFile imageUrl) {
        String fileName = createFileName(imageUrl.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(imageUrl.getSize());
        objectMetadata.setContentType(imageUrl.getContentType());

        try (InputStream inputStream = imageUrl.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다.");
        }
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }

    public String deleteFile(String fileName) {
        DeleteObjectRequest request = new DeleteObjectRequest(bucket, fileName);
        amazonS3.deleteObject(request);
        return "삭제완료";
    }
}
