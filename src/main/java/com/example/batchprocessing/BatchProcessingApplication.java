package com.example.batchprocessing;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableConfigurationProperties({BatchProperties.class})
@Slf4j
public class BatchProcessingApplication {

	private static BatchProperties batchProperties;

	/**
	 * コンストラクタ
	 */
	public BatchProcessingApplication(BatchProperties batchProperties) {
		BatchProcessingApplication.batchProperties = batchProperties;
	}

	/**
	 * mainメソッド（コマンドライン引数例）
	 *  --spring.batch.job.name=importUserJob
	 *  --spring.config.import=optional:classpath:job1.properties,optional:file:job1.properties
	 *  --spring.profiles.active=dev
	 */
	public static void main(String[] args) {
		try {
			System.exit(SpringApplication.exit(SpringApplication.run(BatchProcessingApplication.class, args)));
		} catch (Exception e) {
			log.error(e.getCause().getMessage());
			try {
				sendError(e.getCause().getMessage(), args);
			} catch (Exception e1) {
				log.error(e.getMessage());
			}
		}
	}

	/**
	 * エラー通知
	 */
	private static void sendError(String message, String[] args) throws Exception {
		loadResourceProperties(args);
		Mail mail = new Mail(batchProperties.getMail());
		mail.send(batchProperties.getBatchName() + "エラー", message);
	}

	/**
	 * 外部プロパティファイルが読み込めない場合、組み込みプロパティファイルを読み込む
	 */
	private static void loadResourceProperties(String[] args) {
		if (batchProperties == null) {
			batchProperties = new BatchProperties();
			String profile = getParameter(args, "--spring\\.profiles\\.active=(\\w+)");
			// アプリケーションプロパティを読み込む
			try {
				Properties properties = new Properties();
				Resource resource = new ClassPathResource(getPropertiesFilename("application.properties", profile));
				properties = PropertiesLoaderUtils.loadProperties(resource);
				batchProperties.setBatchName(properties.getProperty("batch.batchName"));
			} catch (IOException e) {
				log.error(e.getMessage());
			}
			// ジョブプロパティを読み込む
			try {
				Properties properties = new Properties();
				String jobProperties = getParameter(args, "--spring\\.config\\.import=.*?(?:optional:classpath:|optional:file:)?([^,]+\\.properties)");
				Resource resource = new ClassPathResource(getPropertiesFilename(jobProperties, profile));
				properties = PropertiesLoaderUtils.loadProperties(resource);
				batchProperties.setJobName(properties.getProperty("batch.jobName"));
				batchProperties.getMail().setFrom(properties.getProperty("batch.mail.from"));
				batchProperties.getMail().setTo(properties.getProperty("batch.mail.to"));
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
	}

	/**
	 * コマンドライン引数からパラメータを取得する
	 */
	private static String getParameter(String[] args, String patternText) {
		Pattern pattern = Pattern.compile(patternText);
		String parameter = null;
		for (String arg : args) {
			Matcher matcher = pattern.matcher(arg);
			while (matcher.find()) {
				parameter = matcher.group(1);
			}
		}
		return parameter;
	}

	/**
	 * プロファイルに対応するプロパティファイル名を取得する
	 */
	private static String getPropertiesFilename(String properties, String profile) {
		return properties.replaceAll("\\.properties$", "-" + profile + ".properties");
	}
}
