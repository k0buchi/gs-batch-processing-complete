package com.example.batchprocessing;

import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

/**
 * プロパティファイル
 */
@ConfigurationProperties(prefix = "batch")
@Validated
@Data
public class BatchProperties {
	private String batchName;
	private String jobName;
	private Mail mail;
	private Properties properties;

	public BatchProperties() {
		this.mail = new Mail();
		this.properties = new Properties();
	}

	@Data
	public static class Mail {
		@NotBlank
		private String from;
		@NotBlank
		private String to;
	}
	
	@Data
	public static class Properties {
		@NotBlank
		private String application;
		@NotBlank
		private String job;
	}
}
