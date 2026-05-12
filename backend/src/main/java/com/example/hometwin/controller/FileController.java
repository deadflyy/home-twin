package com.example.hometwin.controller;

import com.example.hometwin.dto.response.ApiResponse;
import com.example.hometwin.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/avatar")
    public ApiResponse<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String avatarUrl = fileService.uploadAvatar(file);
        return ApiResponse.success(Map.of("url", avatarUrl));
    }

    @PostMapping("/avatar/base64")
    public ApiResponse<Map<String, String>> uploadAvatarBase64(@RequestBody Map<String, String> request) {
        String base64Image = request.get("image");
        String avatarUrl = fileService.uploadAvatarFromBase64(base64Image);
        return ApiResponse.success(Map.of("url", avatarUrl));
    }
}