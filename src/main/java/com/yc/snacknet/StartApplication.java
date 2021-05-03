package com.yc.snacknet;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan // 扫描解析@WebServlet、@WebFilter、@WebListener注解
@SpringBootApplication
@MapperScan("com.yc.snacknet.mapper") // 配置mapper映射文件对应的接口的包路径
public class StartApplication  {
	public static void main(String[] args) {
		SpringApplication.run(StartApplication.class, args);
	}
}