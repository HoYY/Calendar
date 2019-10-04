package com.example.demo.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {
	private static final Logger log = LogManager.getLogger(MailServiceImpl.class);
	
	@Autowired
	private JavaMailSender javaMailSender;
	
    public void sendMessage(String target, String scheduleTitle) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(target);
        message.setSubject("I invite you to my schedule!!");
        message.setText("http://210.117.250.19:5000/email/"+scheduleTitle);
        message.setFrom("hoyymail@naver.com");
        
        try {
        	javaMailSender.send(message);
        	log.info("Mail Send Success!");
        } 
        catch (MailException me) {
        	log.error("MailServiceImpl.sendMessage MailException error!!");
        	me.printStackTrace();
        }
		catch(Exception e) {
			log.error("MailServiceImpl.sendMessage error!!");
			log.error(e);
		}
        
    }
}
