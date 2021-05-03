package com.yc.snacknet.task;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync // 开启异步事件处理
public class AsyncConfig {
	private int corePoolSize = 10; // 核心线程数
	private int maxPoolSize = 200; // 最大线程数
	private int queueCapacity = 10; // 队列大小
	private int keepAliveSeconds = 3000; // 最大空闲时间
	
	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(queueCapacity);
		executor.setKeepAliveSeconds(keepAliveSeconds);
		executor.initialize();
		return executor;
	}
}
