package com.kl.grooveo.boundedContext.library.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.boundedContext.library.dto.SoundTrackFormDTO;
import com.kl.grooveo.boundedContext.library.entity.FileInfo;
import com.kl.grooveo.boundedContext.library.service.FileInfoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
	public String showSoundUpload(SoundTrackFormDTO soundTrackForm) {
		return "usr/library/soundUpload";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/soundupload")
	public String uploadFiles(@Valid SoundTrackFormDTO soundTrackForm, BindingResult bindingResult) {
		try {
			if (soundTrackForm.getAlbumCover().isEmpty() || soundTrackForm.getSoundFile().isEmpty()) {
				bindingResult.rejectValue("file", "required", "음원과 앨범 등록은 필수입니다.");
				return "redirect:/library/soundUpload";
			}

			String albumCoverExtension = FilenameUtils.getExtension(
				soundTrackForm.getAlbumCover().getOriginalFilename());
			String albumCoverName = UUID.randomUUID().toString() + "." + albumCoverExtension;
			String albumCoverUrl =
				"https://s3." + region + ".amazonaws.com/" + bucket + "/albumCover/" + albumCoverName;

			String soundExtension = FilenameUtils.getExtension(soundTrackForm.getSoundFile().getOriginalFilename());
			String soundName = UUID.randomUUID().toString() + "." + soundExtension;
			String soundUrl = "https://s3." + region + ".amazonaws.com/" + bucket + "/sound/" + soundName;

			String title = URLEncoder.encode(soundTrackForm.getTitle(), StandardCharsets.UTF_8);
			String description = URLEncoder.encode(soundTrackForm.getDescription(), StandardCharsets.UTF_8);

			ObjectMetadata albumCoverMetadata = new ObjectMetadata();
			albumCoverMetadata.setContentType(soundTrackForm.getAlbumCover().getContentType());
			albumCoverMetadata.setContentLength(soundTrackForm.getAlbumCover().getSize());
			albumCoverMetadata.addUserMetadata("title", title);
			albumCoverMetadata.addUserMetadata("description", description);

			ObjectMetadata soundMetadata = new ObjectMetadata();
			soundMetadata.setContentType(soundTrackForm.getSoundFile().getContentType());
			soundMetadata.setContentLength(soundTrackForm.getSoundFile().getSize());
			soundMetadata.addUserMetadata("title", title);
			soundMetadata.addUserMetadata("description", description);

			amazonS3Client.putObject(new PutObjectRequest(bucket, "albumCover/" + albumCoverName,
				soundTrackForm.getAlbumCover().getInputStream(), albumCoverMetadata));
			amazonS3Client.putObject(
				new PutObjectRequest(bucket, "sound/" + soundName, soundTrackForm.getSoundFile().getInputStream(),
					soundMetadata));

			FileInfo fileInfo = FileInfo.builder()
				.title(soundTrackForm.getTitle())
				.artist(rq.getMember())
				.description(soundTrackForm.getDescription())
				.albumCoverUrl(albumCoverUrl)
				.soundUrl(soundUrl)
				.build();

			fileInfoService.saveFileInfo(fileInfo);

			return "redirect:/library/soundDetail/" + fileInfo.getId();
		} catch (IOException e) {
			e.printStackTrace();
			return "redirect:/library/soundUpload";
		}
	}
}