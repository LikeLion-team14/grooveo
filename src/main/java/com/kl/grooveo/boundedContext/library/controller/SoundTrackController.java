package com.kl.grooveo.boundedContext.library.controller;

import com.kl.grooveo.boundedContext.library.entity.FileInfo;
import com.kl.grooveo.boundedContext.library.service.SoundTrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/library")
@RequiredArgsConstructor
public class SoundTrackController {
    private final SoundTrackService soundTrackService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String showLibrary(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "kw", defaultValue = "") String kw) {

        Page<FileInfo> paging = this.soundTrackService.getList(kw, page);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);

        return "usr/library/soundTrackList";
    }
}
