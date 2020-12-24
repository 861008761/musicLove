package com.mylovin.music.controller;

import com.alibaba.fastjson.JSON;
import com.mylovin.music.util.RestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class UploadController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    //Save the uploaded file to this folder
    //应该写到配置文件中
    private static String UPLOADED_FOLDER = "F://temp//";

    static {
        File uploadDir = new File(UPLOADED_FOLDER);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
    }

    @GetMapping("/")
    public String index() {
        return "upload";
    }

    /**
     * 上传文件
     * @param file
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/upload") // //new annotation since 4.3
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                       RedirectAttributes redirectAttributes) {
        RestResult result = new RestResult();

        if (file.isEmpty()) {
            LOGGER.info("file is empty!");

            result.setRetCode(-1);
            result.setMsg("Please select a file to upload");
            return JSON.toJSONString(result);
        }

        try {
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);

            LOGGER.info("You successfully uploaded {}", file.getOriginalFilename());
            result.setMsg("You successfully uploaded " + file.getOriginalFilename());
            result.setRetCode(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSON.toJSONString(result);
    }

    /**
     * 制作无人声音频伴奏
     */
    @PostMapping("/generateNonVoiceAudioAccompaniment")
    public String generateNonVoiceAudioAccompaniment(@RequestParam("file") String file) {
        RestResult result = new RestResult();
        //调用python程序进行文件处理，返回结果
        return JSON.toJSONString(result);
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }

}