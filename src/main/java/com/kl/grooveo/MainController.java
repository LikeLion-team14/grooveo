package com.kl.grooveo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String main() {
        return "redirect:/community/list";
    }

    @GetMapping("/community")
    public String showCommunity() {
        return "redirect:/community/list";
    }
}
