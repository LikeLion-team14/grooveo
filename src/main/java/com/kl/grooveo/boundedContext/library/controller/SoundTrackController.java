package com.kl.grooveo.boundedContext.library.controller;

import java.io.IOException;
import java.util.List;

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

import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.boundedContext.comment.dto.CommentFormDTO;
import com.kl.grooveo.boundedContext.comment.entity.SoundPostComment;
import com.kl.grooveo.boundedContext.comment.service.SoundPostCommentService;
import com.kl.grooveo.boundedContext.library.dto.SoundTrackFormDTO;
import com.kl.grooveo.boundedContext.library.entity.SoundTrack;
import com.kl.grooveo.boundedContext.library.service.SoundTrackService;
import com.kl.grooveo.boundedContext.reply.dto.ReplyFormDTO;

import jakarta.servlet.http.Cookie;
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

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/list/{sortCode}")
	public String showLibrary(Model model, @PathVariable("sortCode") int sortCode,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "kw", defaultValue = "") String kw) {

		List<SoundTrack> soundTracks = soundTrackService.findAllForPrintByOrderByIdDesc(rq.getMember());

		Page<SoundTrack> paging = soundTrackService.getList(kw, page, sortCode);
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
	public String uploadSoundTrack(Model model, @Valid SoundTrackFormDTO soundTrackFormDTO,
		BindingResult bindingResult) throws IOException {
		if (soundTrackFormDTO.getAlbumCover().isEmpty() || soundTrackFormDTO.getSoundFile().isEmpty()) {
			bindingResult.rejectValue("file", "required", "음원과 앨범 등록은 필수입니다.");
			return "redirect:/library/soundUpload";
		}

		Long soundTrackId = soundTrackService.uploadSoundTrack(soundTrackFormDTO, rq.getMember());

		return "redirect:/library/soundDetail/" + soundTrackId;
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping(value = "/soundDetail/{id}")
	public String showMoreDetail(Model model, @PathVariable("id") Long id,
		@RequestParam(value = "so", defaultValue = "create") String so,
		@RequestParam(value = "commentPage", defaultValue = "0") int commentPage, CommentFormDTO commentForm,
		ReplyFormDTO replyForm,
		HttpServletRequest request, HttpServletResponse response) {
		SoundTrack soundTrack = soundTrackService.getSoundTrack(id);

		// 조회수 관련 로직
		Cookie oldCookie = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {   // 쿠키가 null 인지 검사
			// null 이 아니라면 "postView" 라는 이름의 쿠키가 있는지 검사
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("soundPostView")) {
					oldCookie = cookie;
				}
			}
		}

		if (oldCookie != null) {
			// "postView" 가 존재한다면
			// value 가 현재 접근한 게시글의 id 를 포함하고 있는지 검사
			if (!oldCookie.getValue().contains("[" + id.toString() + "]")) {
				// 포함하고 있지 않으면 조회수 증가
				soundTrackService.updateView(id);
				oldCookie.setValue(oldCookie.getValue() + "_[" + id + "]");
				oldCookie.setPath("/");
				oldCookie.setMaxAge(60 * 60 * 24);                            // 쿠키 시간
				response.addCookie(oldCookie);
			}
		} else {
			// "soundPostView" 가 존재하지 않는다면
			// 게시글의 id 를 포함하는 쿠키를 만들고
			// 마찬가지로 조회수 증가
			soundTrackService.updateView(id);
			Cookie newCookie = new Cookie("soundPostView", "[" + id + "]");
			newCookie.setPath("/");
			newCookie.setMaxAge(60 * 60 * 24);                                // 쿠키 시간
			response.addCookie(newCookie);
		}

		Page<SoundPostComment> commentPaging = soundPostCommentService.getList(soundTrack, commentPage, so);

		model.addAttribute("commentPaging", commentPaging);
		model.addAttribute("soundTrack", soundTrack);
		model.addAttribute("so", so);

		return "usr/library/soundDetail";
	}

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/soundTrack/{id}")
	public String soundTrackDelete(@PathVariable("id") Long id) {
		SoundTrack fileInfo = soundTrackService.getSoundTrack(id);

		if (!fileInfo.getArtist().getUsername().equals(rq.getMember().getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
		}

		this.soundTrackService.delete(fileInfo);
		return "redirect:/library/list/1";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/soundTrack/modify/{id}")
	public String soundTrackModify(Model model, @PathVariable("id") Long id) {
		SoundTrack soundTrack = soundTrackService.getSoundTrack(id);

		if (!soundTrack.getArtist().getUsername().equals(rq.getMember().getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}

		model.addAttribute("soundTrackFormDTO",
			new SoundTrackFormDTO(soundTrack.getTitle(), soundTrack.getDescription(), null, null));

		return "usr/library/soundUpload";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/soundTrack/modify/{id}")
	public String soundTrackModify(@Valid SoundTrackFormDTO soundTrackFormDTO, BindingResult bindingResult,
		@PathVariable("id") Long id) throws IOException {
		if (bindingResult.hasErrors()) {
			return "usr/library/soundUpload";
		}

		soundTrackService.modifySoundTrack(soundTrackFormDTO, id);

		return "redirect:/library/soundDetail/" + id;
	}

	@GetMapping("/getView")
	@ResponseBody
	public String getViewCnt(@RequestParam("postId") Long postId) {
		return String.valueOf(soundTrackService.getViewCnt(postId));  // 조회수 String 처리
	}
}
