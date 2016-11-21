package com.joe.main;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.joe.main.service.CoreService;
import com.joe.main.service.LoggerService;

@Configuration
@ComponentScan(basePackages = "com.joe")
public class Main {
	public static void main(String[] args) {
		System.setProperty("jsse.enableSNIExtension", "false");
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Main.class);
		CoreService service = context.getBean(CoreService.class);
		try {
			service.run();
		} catch (Error error) {
			LoggerService.info("微信机器人启动失败，失败原因：{}" , error);
		}
		context.close();
	}
}
