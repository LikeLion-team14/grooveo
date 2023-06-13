package com.kl.grooveo.boundedContext.library.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.boundedContext.form.SoundTrackForm;
import com.kl.grooveo.boundedContext.library.entity.FileInfo;
import com.kl.grooveo.boundedContext.library.service.FileInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;


@Controller
@RequestMapping("/library")
@RequiredArgsConstructor
public class FileUploadController {

    private final AmazonS3 amazonS3Client;
    private final FileInfoService fileInfoService;
    private final Rq rq;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/soundupload")
    public String showSoundUpload(Model model, SoundTrackForm soundTrackForm) {
        return "usr/library/soundUpload";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/soundupload")
    public String uploadFiles(Model model, @Valid SoundTrackForm soundTrackForm, BindingResult bindingResult,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("albumCover") MultipartFile albumCover,
            @RequestParam("sound") MultipartFile sound) {
        try {
            String albumCoverName = albumCover.getOriginalFilename();
            String albumCoverUrl = "https://s3." + region + ".amazonaws.com/"  + bucket + "/albumCover/" + albumCoverName;
            String soundName = sound.getOriginalFilename();
            String soundUrl = "https://s3." + region + ".amazonaws.com/"  + bucket + "/sound/" + soundName;

            ObjectMetadata albumCoverMetadata = new ObjectMetadata();
            albumCoverMetadata.setContentType(albumCover.getContentType());
            albumCoverMetadata.setContentLength(albumCover.getSize());
            albumCoverMetadata.addUserMetadata("title", title);
            albumCoverMetadata.addUserMetadata("description", description);

            ObjectMetadata soundMetadata = new ObjectMetadata();
            soundMetadata.setContentType(sound.getContentType());
            soundMetadata.setContentLength(sound.getSize());
            soundMetadata.addUserMetadata("title", title);
            soundMetadata.addUserMetadata("description", description);

            amazonS3Client.putObject(new PutObjectRequest(bucket, "albumCover/" + albumCoverName, albumCover.getInputStream(), albumCoverMetadata));
            amazonS3Client.putObject(new PutObjectRequest(bucket, "sound/" + soundName, sound.getInputStream(), soundMetadata));

            FileInfo fileInfo = new FileInfo();
            fileInfo.setTitle(title);
            fileInfo.setDescription(description);
            fileInfo.setAlbumCoverUrl(albumCoverUrl);
            fileInfo.setSoundUrl(soundUrl);
            fileInfo.setCreateDate(LocalDateTime.now());
            fileInfoService.saveFileInfo(fileInfo);

            System.out.println(ResponseEntity.ok("업로드 성공하였습니다. 앨범커버 URL : " + albumCoverUrl + ", 음원 URL : " + soundUrl));

            return "redirect:/library/soundDetail/" + fileInfo.getId();
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/library/soundUpload";
        }
    }
}