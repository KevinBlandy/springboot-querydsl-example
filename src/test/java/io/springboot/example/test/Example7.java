package io.springboot.example.test;

import io.springboot.example.ExampleApplication;
import io.springboot.example.entity.QUser;
import io.springboot.example.service.UserService;

import javax.persistence.LockModeType;
import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
/**
 * 
 * 加锁查询
 * @author KevinBlandy
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExampleApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class Example7 {
	
	@Autowired
	private UserService service;

	/**
	 * 共享锁/独占锁
	 * 
	 * 通过 LockModeType 枚举来指定锁类型
	 */
	@Test
	@Transactional
	@Rollback(false)
	public void test() {
		service.applyReadOnly(query -> {
			query.select(QUser.user.id).from(QUser.user)
				.setLockMode(LockModeType.PESSIMISTIC_READ)
				.fetch();
			
			query.select(QUser.user.id).from(QUser.user)
				.setLockMode(LockModeType.PESSIMISTIC_WRITE)
				.fetch();
			return null;
		});
		
		/*
		
		    select
		        user0_.id as col_0_0_ 
		    from
		        user user0_ for share
		    
	        select
		        user0_.id as col_0_0_ 
		    from
		        user user0_ for update
		
		*/
	}
}
