package com.kl.grooveo.boundedContext.home.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.kl.grooveo.boundedContext.library.dto.FileInfoDTO;
import com.kl.grooveo.boundedContext.library.service.FileInfoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {

	private final FileInfoService fileInfoService;

	@GetMapping("/")
	public String main(Model model) {
		List<FileInfoDTO> latestSongsDTOS = fileInfoService.convertLatestSongsToDTO();
		List<FileInfoDTO> popularSongDTOS = fileInfoService.convertToPopularSongTop10DTO();

		model.addAttribute("latestSongsDTOS", latestSongsDTOS);
		model.addAttribute("popularSongDTOS", popularSongDTOS);

		return "usr/home/main";
	}
}