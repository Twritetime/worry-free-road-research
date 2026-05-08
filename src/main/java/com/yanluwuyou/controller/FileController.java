package com.yanluwuyou.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.yanluwuyou.common.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 文件上传控制器
 * 提供文件上传功能，支持多种文件类型
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${server.port:8080}")
    private String port;

    @Value("${files.upload.path}")
    private String fileUploadPath;

    /**
     * 上传文件
     * 使用UUID重命名文件避免冲突
     * 
     * @param file 上传的文件
     * @return 文件访问URL
     * @throws IOException 文件操作异常
     */
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String type = FileUtil.extName(originalFilename);
        long size = file.getSize();

        // Define file uuid
        String uuid = IdUtil.fastSimpleUUID();
        String fileUUID = uuid + StrUtil.DOT + type;

        File uploadFile = new File(fileUploadPath + fileUUID);
        // Create parent directory if not exists
        if (!uploadFile.getParentFile().exists()) {
            uploadFile.getParentFile().mkdirs();
        }

        // Save file
        file.transferTo(uploadFile);

        // TODO: Get actual host from request if possible, or use localhost
        String url = "http://localhost:" + port + "/files/" + fileUUID;
        return Result.success(url);
    }
}
