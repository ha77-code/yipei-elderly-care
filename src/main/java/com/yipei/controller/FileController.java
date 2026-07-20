package com.yipei.controller;

import com.yipei.entity.ApiResponse;
import com.yipei.service.FileStorageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/file")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    /** 通用文件上传（图片或文档） */
    @PostMapping("/upload")
    public ApiResponse<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        String url = fileStorageService.storeGeneralFile(file);
        String filename = url.substring(url.lastIndexOf("/") + 1);
        return ApiResponse.success(Map.of("url", url, "filename", filename));
    }
}
