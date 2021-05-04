package com.yc.snacknet.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.yc.snacknet.redis.impl.RedisUtil;

@RestController
public class GoodsController {
	Logger logger = LoggerFactory.getLogger(GoodsController.class);
	
	@Autowired
	private RedisUtil redisUtil;
	
	// 总库存
	private long balance = 0;

	// 获取锁的超时时间
	private int timeout = 30 * 1000;
	
	@GetMapping("/setnx/{key}/{val}")
	public boolean setnx(@PathVariable String key, @PathVariable String val) {
		return redisUtil.setnx(key, val);
	}
	
	@GetMapping("/delnx/{key}/{val}")
	public int delnx(@PathVariable String key, @PathVariable String val) {
		return redisUtil.delnx(key, val);
	}
	
	
	@GetMapping("/qiangdan/{key}")
	public List<String> qiangdan(@PathVariable String key) {
		List<String> shopUsers = new ArrayList<String>(); // 抢到了商品的用户列表
		
		List<String> users = new ArrayList<String>(); // 构造很多用户
		
		IntStream.range(0, 100000).parallel().forEach(i -> {
			users.add("神牛-" + i);
		});
		
		balance = 10; // 10W用户抢购10个商品
		
		// 模拟开抢
		users.parallelStream().forEach(a -> {
			String shopUser = qiang(key, a);
			if (!StringUtils.isEmpty(shopUser)) {
				shopUsers.add(shopUser);
			}
		});
		
		return shopUsers;
	}
	
	// 模拟抢单
	public String qiang(String goodsKey, String b) {
		long startTime = System.currentTimeMillis();
		while((startTime + timeout) >= System.currentTimeMillis()) { // 判断未抢成功的用户
			if (balance <= 0) {
				break;
			}
			
			if (redisUtil.setnx(goodsKey, b)) {
				logger.info("用户{}拿到了锁...", b);
				
				try {
					// 检查是否还有商品
					if (balance <= 0) {
						break;
					}
					
					try {
						TimeUnit.SECONDS.sleep(1); // 处理点单的时间
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
					
					balance -= 1;
					logger.info("用户{}抢单成功跳出，所剩库存：{}...", b, balance);
					return b + "抢单成功，所剩库存：" + balance;
				} finally {
					logger.info("用户{}释放锁...", b);
					redisUtil.delnx(goodsKey, b);
				}
			} else {
				// 用户b没有拿到锁，在超市范围内继续请求锁
			}
		}
		return "";
	}
}
