package com.kl.grooveo.boundedContext.library.controller;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kl.grooveo.boundedContext.library.service.FileInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Controller
@RequestMapping("/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final AmazonS3Client amazonS3Client;
    private final FileInfoService fileInfoService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @GetMapping
    public String displayForm() {
        return "usr/library/soundUpload";
    }

    @PostMapping
    public ResponseEntity<String> uploadFile(
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            String fileUrl = "https://" + bucket + "/test/" + fileName;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            // 사용자로부터 받은 정보를 metadata에 추가
            metadata.addUserMetadata("title", title);
            metadata.addUserMetadata("author", author);
            metadata.addUserMetadata("description", description);

            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata));

            // DB에 파일 정보를 저장
            fileInfoService.saveFileInfo(title, author, description, fileUrl);

            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}