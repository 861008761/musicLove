package com.mylovin.music.controller;

import com.alibaba.fastjson.JSON;
import com.mylovin.music.model.UserInfo;
import com.mylovin.music.model.UserMusic;
import com.mylovin.music.service.MusicService;
import com.mylovin.music.util.DateUtil;
import com.mylovin.music.util.RestResult;
import com.mylovin.music.util.ResultMessage;
import com.mylovin.music.util.SystemConstant;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/music")
public class UploadController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    @Value("${server.fileServer}")
    private String FILE_SERVER_PREFIX;

    @Autowired
    private MusicService musicService;

    //Save the uploaded file to this folder
    //应该写到配置文件中
    private static String UPLOADED_FOLDER = SystemConstant.UPLOADED_FOLDER_LINUX;

    static {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            UPLOADED_FOLDER = SystemConstant.UPLOADED_FOLDER_WIN;
        } else if (os.toLowerCase().startsWith("mac")) {
            UPLOADED_FOLDER = SystemConstant.UPLOADED_FOLDER_MACOS;
        }
        File uploadDir = new File(UPLOADED_FOLDER);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
    }

    @GetMapping("/upload")
    public String index() {
        return "upload";
    }

    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    @PostMapping("/uploadMusic") // //new annotation since 4.3
    @ResponseBody
    public String singleFileUpload(@RequestParam("file") MultipartFile file) {
        RestResult result = new RestResult();

        if (file.isEmpty()) {
            LOGGER.info("file is empty!");

            result.setRetCode(-1);
            result.setMsg("Please select a file to upload");
            return JSON.toJSONString(result);
        }

        try {
            //上传的文件重新命名
            //游客/已登录用户
            //如果是游客的话，文件只需要重新命名（格式如visitor_15892342434.mp3），在制作的时候可以知道名称就行了
            //如果是已经登录的用户的话，将文件名称重新命名并写入数据库，用户可以查看已经制作的音频文件（保存1个月）
            String fileName = "visitor_" + new Date().getTime() + ".mp3";
            UserInfo user = (UserInfo) SecurityUtils.getSubject().getPrincipal();
            if (null != user) {
                fileName = user.getUid() + "_" + new Date().getTime() + ".mp3";
                //TODO 写入数据库
                UserMusic music = new UserMusic();
                music.setFileName(fileName);
                music.setTime(DateUtil.formatDate());
                music.setStatus((byte) 0);
                music.setUserInfo(user);
                musicService.save(music);
            }

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            //Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Path path = Paths.get(UPLOADED_FOLDER + fileName);
            Files.write(path, bytes);

            LOGGER.info("You successfully uploaded {}", file.getOriginalFilename());
            result.setMsg("You successfully uploaded " + fileName + "! file path on file server is " + FILE_SERVER_PREFIX + fileName);
            result.setRetCode(0);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("upload failed! errMsg: [{}]", e.getMessage());
        }
        return JSON.toJSONString(result);
    }

    /**
     * 调用python文件，制作无人声音频伴奏
     */
    @GetMapping("/generateNonVoiceAudioAccompaniment")
    @ResponseBody
    public String generateNonVoiceAudioAccompaniment(@RequestParam("file") String file) {
        ResultMessage message = new ResultMessage();
        Map<String, Object> msg = message.getMsg();
        //调用python程序进行文件处理，返回结果
        Process proc;
        try {
            LOGGER.info("开始制作伴奏");
            String exe = "sh";
            String command = SystemConstant.GENERATE_NONVOICE_AUDIO_ACCOMPANIMENT_MACOS;
            String fileName = file;
            String processType = "2stems";
            String[] cmdArr = new String[]{exe, command, fileName, processType};
            proc = Runtime.getRuntime().exec(cmdArr);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            proc.waitFor();
            msg.put("message", "制作无人声音频伴奏完成");
            message.setStatus(200);
            LOGGER.info("制作伴奏完成");
        } catch (IOException e) {
            LOGGER.error("制作无人声音频伴奏失败！msg:", e.getMessage(), e);
            message.setStatus(500);
            msg.put("message", "制作无人声音频伴奏失败!");
            msg.put("cause", e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.error("制作无人声音频伴奏失败！msg:", e.getMessage(), e);
            message.setStatus(500);
            msg.put("message", "制作无人声音频伴奏失败!");
            msg.put("cause", e.getMessage());
        }
        return JSON.toJSONString(message);
    }

    /**
     * 查询用户制作历史
     *
     * @return
     */
    @RequestMapping("/history")
    @ResponseBody
    public String history() {
        RestResult result = new RestResult();
        UserInfo user = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        if (Objects.isNull(user)) {
            result.setMsg("user not login!");
            result.setRetCode(-1);
            return JSON.toJSONString(result);
        }
        List<UserMusic> userMusicList = musicService.findByUserInfo(user);
        result.setMsg(userMusicList);
        result.setRetCode(0);
        return JSON.toJSONString(result);
    }

    @GetMapping("/uploadStatus")
    @ResponseBody
    public String uploadStatus() {
        return "uploadStatus";
    }

}