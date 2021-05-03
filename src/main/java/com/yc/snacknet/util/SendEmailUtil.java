package com.yc.snacknet.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.yml")
public class SendEmailUtil {
	@Value("${spring.mail.username}")
	private String sendEmail; // 发件箱
	
	@Value("${spring.mail.password}")
	private String password; // 发件箱密码
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	/**
	 * 发送邮件的方法
	 * @param receiveEmail
	 * @return
	 * @throws MessagingException 
	 */
	public boolean sendEmail(String receiveEmail, String code, String nickName) {
		if (StringUtil.checkNull(receiveEmail, code, nickName)) {
			return false;
		}
		
		try {
			// 建立邮件的消息，我们需要发送的是html格式的邮件
			MimeMessage mailMessagge = javaMailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessagge);
			
			messageHelper.setTo(receiveEmail); // 收件箱
			messageHelper.setFrom(sendEmail); // 发件箱
			messageHelper.setSubject("零食网注册中心"); // 邮件主题
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
			String str = "<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body><p style='font-size: 20px;font-weight:bold;'>尊敬的："+nickName+"，您好！</p>"
					+ "<p style='text-indent:2em; font-size: 20px;'>欢迎注册零食网，您本次的注册码是 "
					+ "<span style='font-size:30px;font-weight:bold;color:red'>"+code+"</span>，3分钟之内有效，请尽快使用！</p>"
					+ "<p style='text-align:right; padding-right: 20px;'>"
					+ "<a href='http://www.hyycinfo.com' style='font-size:18px'>衡阳市源辰信息科技有限公司技术部</a></p>"
					+ "<span style='font-size:18px; float:right; margin-right: 60px;'>"+sdf.format(new Date())+"</span></body></html>";
			
			
			
			messageHelper.setText(str, true); // 邮件的正文，第二个参数说明是一个html格式的正文
			javaMailSender.send(mailMessagge);
			return true;
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}
