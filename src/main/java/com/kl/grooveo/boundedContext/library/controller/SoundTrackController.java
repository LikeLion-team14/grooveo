package com.kl.grooveo.boundedContext.library.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.boundedContext.comment.dto.CommentFormDTO;
import com.kl.grooveo.boundedContext.comment.entity.SoundPostComment;
import com.kl.grooveo.boundedContext.comment.service.SoundPostCommentService;
import com.kl.grooveo.boundedContext.library.dto.SoundTrackFormDTO;
import com.kl.grooveo.boundedContext.library.entity.SoundTrack;
import com.kl.grooveo.boundedContext.library.service.SoundTrackService;
import com.kl.grooveo.boundedContext.reply.dto.ReplyFormDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/library")
@RequiredArgsConstructor
public class SoundTrackController {
	private final SoundTrackService soundTrackService;
	private final SoundPostCommentService soundPostCommentService;
	private final Rq rq;
	private final AmazonS3 amazonS3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${cloud.aws.region.static}")
	private String region;

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/list/{sortCode}")
	public String showLibrary(Model model, @PathVariable("sortCode") int sortCode,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "kw", defaultValue = "") String kw) {

		List<SoundTrack> soundTracks = soundTrackService.findAllForPrintByOrderByIdDesc(rq.getMember());

		Page<SoundTrack> paging = this.soundTrackService.getList(kw, page, sortCode);
		model.addAttribute("sortCode", sortCode);
		model.addAttribute("paging", paging);
		model.addAttribute("kw", kw);
		model.addAttribute("soundTracks", soundTracks);

		return "usr/library/soundTrackList";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/soundupload")
	public String showSoundUpload(Model model, SoundTrackFormDTO soundTrackForm) {
		return "usr/library/soundUpload";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/soundupload")
	public String uploadFiles(Model model, @Valid SoundTrackFormDTO soundTrackFormDTO, BindingResult bindingResult) {
		try {
			if (soundTrackFormDTO.getAlbumCover().isEmpty() || soundTrackFormDTO.getSoundFile().isEmpty()) {
				bindingResult.rejectValue("file", "required", "음원과 앨범 등록은 필수입니다.");
				return "redirect:/library/soundUpload";
			}

			String albumCoverExtension = FilenameUtils.getExtension(
				soundTrackFormDTO.getAlbumCover().getOriginalFilename());
			String albumCoverName = UUID.randomUUID().toString() + "." + albumCoverExtension;
			String albumCoverUrl =
				"https://s3." + region + ".amazonaws.com/" + bucket + "/albumCover/" + albumCoverName;

			String soundExtension = FilenameUtils.getExtension(soundTrackFormDTO.getSoundFile().getOriginalFilename());
			String soundName = UUID.randomUUID().toString() + "." + soundExtension;
			String soundUrl = "https://s3." + region + ".amazonaws.com/" + bucket + "/sound/" + soundName;

			String title = URLEncoder.encode(soundTrackFormDTO.getTitle(), StandardCharsets.UTF_8);
			String description = URLEncoder.encode(soundTrackFormDTO.getDescription(), StandardCharsets.UTF_8);

			ObjectMetadata albumCoverMetadata = new ObjectMetadata();
			albumCoverMetadata.setContentType(soundTrackFormDTO.getAlbumCover().getContentType());
			albumCoverMetadata.setContentLength(soundTrackFormDTO.getAlbumCover().getSize());
			albumCoverMetadata.addUserMetadata("title", title);
			albumCoverMetadata.addUserMetadata("description", description);

			ObjectMetadata soundMetadata = new ObjectMetadata();
			soundMetadata.setContentType(soundTrackFormDTO.getSoundFile().getContentType());
			soundMetadata.setContentLength(soundTrackFormDTO.getSoundFile().getSize());
			soundMetadata.addUserMetadata("title", title);
			soundMetadata.addUserMetadata("description", description);

			amazonS3Client.putObject(new PutObjectRequest(bucket, "albumCover/" + albumCoverName,
				soundTrackFormDTO.getAlbumCover().getInputStream(), albumCoverMetadata));
			amazonS3Client.putObject(
				new PutObjectRequest(bucket, "sound/" + soundName, soundTrackFormDTO.getSoundFile().getInputStream(),
					soundMetadata));

			SoundTrack soundTrack = SoundTrack.builder()
				.title(soundTrackFormDTO.getTitle())
				.artist(rq.getMember())
				.description(soundTrackFormDTO.getDescription())
				.albumCoverUrl(albumCoverUrl)
				.soundUrl(soundUrl)
				.build();

			soundTrackService.saveSoundTrack(soundTrack);

			return "redirect:/library/soundDetail/" + soundTrack.getId();
		} catch (IOException e) {
			e.printStackTrace();
			return "redirect:/library/soundUpload";
		}
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping(value = "/soundDetail/{id}")
	public String showMoreDetail(Model model, @PathVariable("id") Long id,
		@RequestParam(value = "so", defaultValue = "create") String so,
		@RequestParam(value = "commentPage", defaultValue = "0") int commentPage, CommentFormDTO commentForm,
		ReplyFormDTO replyForm,
		HttpServletRequest request, HttpServletResponse response) {
		SoundTrack soundTrack = this.soundTrackService.getSoundTrack(id);

		soundTrackService.updateViewCount(request, response, id);

		Page<SoundPostComment> commentPaging = this.soundPostCommentService.getList(soundTrack, commentPage, so);

		model.addAttribute("commentPaging", commentPaging);
		model.addAttribute("soundTrack", soundTrack);
		model.addAttribute("so", so);

		return "usr/library/soundDetail";
	}

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/soundTrack/{id}")
	public String soundTrackDelete(@PathVariable("id") Long id) {
		SoundTrack fileInfo = this.soundTrackService.getSoundTrack(id);

		if (!fileInfo.getArtist().getUsername().equals(rq.getMember().getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
		}

		this.soundTrackService.delete(fileInfo);
		return "redirect:/library/list/1";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/soundTrack/modify/{id}")
	public String soundTrackModify(Model model, @PathVariable("id") Long id) {
		SoundTrack soundTrack = this.soundTrackService.getSoundTrack(id);

		if (!soundTrack.getArtist().getUsername().equals(rq.getMember().getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}

		model.addAttribute("soundTrackFormDTO",
			new SoundTrackFormDTO(soundTrack.getTitle(), soundTrack.getDescription(), null, null));
		model.addAttribute("albumCoverUrl", soundTrack.getAlbumCoverUrl());
		model.addAttribute("soundUrl", soundTrack.getSoundUrl());

		return "usr/library/soundUpload";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/soundTrack/modify/{id}")
	public String soundTrackModify(@Valid SoundTrackFormDTO soundTrackForm, BindingResult bindingResult,
		@PathVariable("id") Long id) {
		if (bindingResult.hasErrors()) {
			return "usr/library/soundUpload";
		}

		SoundTrack soundTrack = this.soundTrackService.getSoundTrack(id);

		if (!soundTrack.getArtist().getUsername().equals(rq.getMember().getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}

		try {
			// 앨범 커버 수정 코드 추가
			String albumCoverExtension = FilenameUtils.getExtension(
				soundTrackForm.getAlbumCover().getOriginalFilename());
			String albumCoverName = UUID.randomUUID().toString() + "." + albumCoverExtension;
			String albumCoverUrl =
				"https://s3." + region + ".amazonaws.com/" + bucket + "/albumCover/" + albumCoverName;

			String title = URLEncoder.encode(soundTrackForm.getTitle(), StandardCharsets.UTF_8);
			String description = URLEncoder.encode(soundTrackForm.getDescription(), StandardCharsets.UTF_8);

			ObjectMetadata albumCoverMetadata = new ObjectMetadata();
			albumCoverMetadata.setContentType(soundTrackForm.getAlbumCover().getContentType());
			albumCoverMetadata.setContentLength(soundTrackForm.getAlbumCover().getSize());
			albumCoverMetadata.addUserMetadata("title", title);
			albumCoverMetadata.addUserMetadata("description", description);

			amazonS3Client.putObject(new PutObjectRequest(bucket, "albumCover/" + albumCoverName,
				soundTrackForm.getAlbumCover().getInputStream(), albumCoverMetadata));

			soundTrackService.modify(soundTrack, soundTrackForm, albumCoverUrl, soundTrack.getSoundUrl());
			return String.format("redirect:/library/soundDetail/%s", id);
		} catch (IOException e) {
			e.printStackTrace();
			return "redirect:/library/soundUpload";
		}
	}

	@GetMapping("/getView")
	@ResponseBody
	public String getViewCnt(@RequestParam("postId") Long postId) {
		return String.valueOf(soundTrackService.getViewCnt(postId));  // 조회수 String 처리
	}
}
