package com.kl.grooveo.boundedContext.library.controller;

import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.form.FreedomPostForm;
import com.kl.grooveo.boundedContext.form.SoundTrackForm;
import com.kl.grooveo.boundedContext.library.entity.FileInfo;
import com.kl.grooveo.boundedContext.library.service.SoundTrackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/library")
@RequiredArgsConstructor
public class SoundTrackController {
    private final SoundTrackService soundTrackService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String showLibrary(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "kw", defaultValue = "") String kw) {

        Page<FileInfo> paging = this.soundTrackService.getList(kw, page);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);

        return "usr/library/soundTrackList";
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/soundTrack/{id}")
    public String soundTrackDelete(@PathVariable("id") Long id) {
        FileInfo fileInfo = this.soundTrackService.getSoundTrack(id);

        if (!fileInfo.getArtist().getUsername().equals(rq.getMember().getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }

        this.soundTrackService.delete(fileInfo);
        return "redirect:/library/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/soundTrack/modify/{id}")
    public String soundTrackModify(SoundTrackForm soundTrackForm, @PathVariable("id") Long id) {
        FileInfo fileInfo = this.soundTrackService.getSoundTrack(id);

        if(!fileInfo.getArtist().getUsername().equals(rq.getMember().getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        soundTrackForm.setTitle(fileInfo.getTitle());
        soundTrackForm.setDescription(fileInfo.getDescription());
        return "usr/library/soundUpload";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/soundTrack/modify/{id}")
    public String soundTrackModify(@Valid SoundTrackForm soundTrackForm, BindingResult bindingResult,
                                    @PathVariable("id") Long id) {
        if (bindingResult.hasErrors()) {
            return "usr/library/soundUpload";
        }

        FileInfo fileInfo = this.soundTrackService.getSoundTrack(id);

        if (!fileInfo.getArtist().getUsername().equals(rq.getMember().getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        this.soundTrackService.modify(fileInfo, soundTrackForm.getTitle(), soundTrackForm.getDescription());
        return String.format("redirect:/library/soundDetail/%s", id);
    }
}
