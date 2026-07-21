package com.yipei.service;

import com.yipei.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

/** 文件存储服务：统一处理上传文件的校验、落盘与访问 URL 生成 */
@Service
public class FileStorageService {

    /** 允许上传的图片扩展名 */
    private static final Set<String> IMAGE_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp");

    /** 允许上传的通用文件扩展名（图片 + 文档） */
    private static final Set<String> GENERAL_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp", ".pdf", ".doc", ".docx");

    /** 头像最大 5MB */
    private static final long MAX_IMAGE_SIZE = 5L * 1024 * 1024;

    @Value("${yipei.upload-dir:uploads}")
    private String uploadDir;

    /** 存储通用文件（图片或文档），返回可访问的相对 URL */
    public String storeGeneralFile(MultipartFile file) {
        return store(file, GENERAL_EXTENSIONS, 0);
    }

    /** 存储头像图片，限制为图片类型且不超过 5MB，返回可访问的相对 URL */
    public String storeImage(MultipartFile file) {
        return store(file, IMAGE_EXTENSIONS, MAX_IMAGE_SIZE);
    }

    /**
     * 校验并保存文件。
     *
     * @param allowedExtensions 允许的扩展名集合
     * @param maxSize           最大字节数，0 表示不限制
     * @return 形如 /uploads/xxx.png 的访问路径
     */
    private String store(MultipartFile file, Set<String> allowedExtensions, long maxSize) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "文件不能为空");
        }
        if (maxSize > 0 && file.getSize() > maxSize) {
            throw new BusinessException(400, "文件大小超过限制（最大 " + (maxSize / 1024 / 1024) + "MB）");
        }
        String ext = extractExtension(file.getOriginalFilename());
        if (!allowedExtensions.contains(ext)) {
            throw new BusinessException(400, "不支持的文件类型，仅允许：" + String.join("、", allowedExtensions));
        }
        try {
            Path dir = Paths.get(uploadDir);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            String filename = UUID.randomUUID().toString().replace("-", "") + ext;
            Path target = dir.resolve(filename);
            file.transferTo(target.toAbsolutePath().toFile());
            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new BusinessException(500, "文件上传失败：" + e.getMessage());
        }
    }

    /** 提取小写扩展名，如 ".png"；无扩展名返回空串 */
    private String extractExtension(String originalName) {
        if (originalName == null || !originalName.contains(".")) {
            return "";
        }
        return originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
    }
}
