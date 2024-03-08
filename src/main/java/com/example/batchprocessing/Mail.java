package com.example.batchprocessing;

import javax.mail.internet.InternetAddress;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class Mail {

	private final BatchProperties.Mail properties;

	public Mail(BatchProperties.Mail properties) {
		this.properties = properties;
	}

	public void send(String subject, String text) throws Exception {
		String from = properties.getFrom();
		String[] to = properties.getTo().split(";");
		InternetAddress[] toAddress = new InternetAddress[to.length];
		for (int i = 0; i < to.length; i++) {
			toAddress[i] = new InternetAddress(to[i]);
		}
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from);
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.send(message);
	}
}
