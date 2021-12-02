package io.springboot.example.test;

import io.springboot.example.ExampleApplication;
import io.springboot.example.entity.QUser;
import io.springboot.example.entity.User;
import io.springboot.example.service.UserService;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 
 * Spring-Data-Jpa 对QueryDsl的支持
 * @author KevinBlandy
 *
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExampleApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class Example10 {

	@Autowired
	private UserService userService;
	
	/**
	 * 
	 * QuerydslPredicateExecutor<T> 是spring-data提供的一个操作接口
	 * Repository 接口实现 ： QuerydslPredicateExecutor<T> 就可以使用这些支持方法
	 * 
	 *  可以自己查看源码学习
	 */
	@Test
	@Transactional
	@Rollback(false)
	public void test() {
		QUser qUser = QUser.user;
		
		// 根据条件检索一条记录
		this.userService.findOne(qUser.id.eq(1).and(qUser.createAt.isNotNull()));
		
		// 根据条件获取所有
		this.userService.findAll(qUser.enabled.eq(true));
		
		// 条件查询 & 排序
		this.userService.findAll(qUser.enabled.eq(true), qUser.id.desc());
		
		// 查询所有 & 排序
		this.userService.findAll(qUser.id.asc().nullsLast());
		
		// 条件查询 & 分页 & 排序
		Page<User> result = this.userService.findAll(qUser.id.in(1, 2, 3), PageRequest.of(0, 2, Sort.by(Sort.Order.asc("name"))));
		log.info("test result: count={}, list={}", result.getTotalElements(), result.getContent());
		
		// 根据条件判断是否存在
		this.userService.exists(qUser.id.eq(1));
		
		// TODO 这个方法还不咋熟悉，好像是响应式相关的
		this.userService.findBy(qUser.enabled.eq(true), query -> {
			return query.all();
		});
	}
}
