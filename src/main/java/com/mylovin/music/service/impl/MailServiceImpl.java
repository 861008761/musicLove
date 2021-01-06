package com.mylovin.music.service.impl;

import com.mylovin.music.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * Created by summer on 2017/5/4.
 */
@Service
public class MailServiceImpl implements MailService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * Spring Boot 提供了一个发送邮件的简单抽象，使用的是下面这个接口，这里直接注入即可使用
     */
    @Autowired
    private JavaMailSender mailSender;

    /**
     * 配置文件中的qq邮箱
     */
    @Value("${mail.fromMail.addr}")
    private String from;

    /**
     * 发送文本邮件
     *
     * @param to
     * @param subject
     * @param content
     */
    @Override
    public void sendSimpleMail(String to, String subject, String content) {
        //创建SimpleMailMessage对象
        SimpleMailMessage message = new SimpleMailMessage();
        //邮件发送人
        message.setFrom(from);
        //邮件接收人
        message.setTo(to);
        //邮件主题
        message.setSubject(subject);
        //邮件内容
        message.setText(content);

        try {
            //发送邮件
            mailSender.send(message);
            LOGGER.info("简单邮件已经发送。");
        } catch (Exception e) {
            LOGGER.error("发送简单邮件时发生异常！", e);
        }
    }

    /**
     * 发送html邮件
     *
     * @param to
     * @param subject
     * @param content
     */
    @Override
    public void sendHtmlMail(String to, String subject, String content) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            //true表示需要创建一个multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
            LOGGER.info("html邮件发送成功");
        } catch (MessagingException e) {
            LOGGER.error("发送html邮件时发生异常！", e);
        }
    }


    /**
     * 发送带附件的邮件
     *
     * @param to
     * @param subject
     * @param content
     * @param filePath
     */
    public void sendAttachmentsMail(String to, String subject, String content, String filePath) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            FileSystemResource file = new FileSystemResource(new File(filePath));
            String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
            helper.addAttachment(fileName, file);
            //helper.addAttachment("test"+fileName, file);

            mailSender.send(message);
            LOGGER.info("带附件的邮件已经发送。");
        } catch (MessagingException e) {
            LOGGER.error("发送带附件的邮件时发生异常！", e);
        }
    }


    /**
     * 发送正文中有静态资源（图片）的邮件
     *
     * @param to
     * @param subject
     * @param content
     * @param rscPath
     * @param rscId
     */
    public void sendInlineResourceMail(String to, String subject, String content, String rscPath, String rscId) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            FileSystemResource res = new FileSystemResource(new File(rscPath));
            helper.addInline(rscId, res);

            mailSender.send(message);
            LOGGER.info("嵌入静态资源的邮件已经发送。");
        } catch (MessagingException e) {
            LOGGER.error("发送嵌入静态资源的邮件时发生异常！", e);
        }
    }
}
