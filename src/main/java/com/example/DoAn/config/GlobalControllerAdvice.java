package com.example.DoAn.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("imageAssets")
    public ImageAssets populateImageAssets() {
        return new ImageAssets();
    }
}
