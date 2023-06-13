package com.kl.grooveo.boundedContext.home.controller;

import com.kl.grooveo.boundedContext.library.entity.FileInfo;
import com.kl.grooveo.boundedContext.library.service.FileInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final FileInfoService fileInfoService;

    @GetMapping("/")
    public String main(Model model) {
        List<FileInfo> latestSongs = fileInfoService.getLatestSongs();

        model.addAttribute("latestSongs",latestSongs);

        return "usr/home/main";
    }
}