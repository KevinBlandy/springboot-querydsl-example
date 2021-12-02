package io.springboot.example.test;

import io.springboot.example.ExampleApplication;
import io.springboot.example.entity.QUser;
import io.springboot.example.entity.User;
import io.springboot.example.service.UserService;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import com.querydsl.core.BooleanBuilder;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExampleApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class Example6 {
	
	@Autowired
	private UserService service;

	/**
	 * 条件分组
	 */
	@Test
	@Transactional
	@Rollback(false)
	public void test() {
		List<User> result = this.service.applyReadOnly(query -> {
			
			QUser qUser = QUser.user;
			
			// 条件
			BooleanBuilder condition = new BooleanBuilder();
			
			// 分组1
			BooleanBuilder group1 = new BooleanBuilder(qUser.name.eq("张飞").or(qUser.id.gt(1)));
			
			// 分组2
			BooleanBuilder group2 = new BooleanBuilder(qUser.createAt.before(LocalDateTime.now()).and(qUser.enabled.eq(true)));
			
			// 合并分组，关系是and
			condition.and(group1.and(group2));
			
			return query.select(qUser).from(qUser).where(condition).fetch();
		});
		
		log.info("test result={}", result);
		/*
		    select
		        user0_.id as id1_1_,
		        user0_.balance as balance2_1_,
		        user0_.create_at as create_a3_1_,
		        user0_.department_id as departme4_1_,
		        user0_.enabled as enabled5_1_,
		        user0_.gender as gender6_1_,
		        user0_.name as name7_1_,
		        user0_.update_at as update_a8_1_ 
		    from
		        user user0_ 
		    where
		        (
		            user0_.name=? 
		            or user0_.id>?
		        ) 
		        and user0_.create_at<? 
		        and user0_.enabled=?
		        test result=[User(id=2, name=许褚, gender=MALE, balance=15.00, departmentId=1, enabled=true, createAt=2021-12-02T08:59:43, updateAt=null), User(id=3, name=刘备, gender=MALE, balance=24.00, departmentId=2, enabled=true, createAt=2021-12-02T08:59:43, updateAt=null), User(id=4, name=关羽, gender=MALE, balance=100.00, departmentId=2, enabled=true, createAt=2021-12-02T08:59:43, updateAt=null), User(id=5, name=张飞, gender=MALE, balance=9.00, departmentId=2, enabled=true, createAt=2021-12-02T08:59:43, updateAt=null), User(id=6, name=孙权, gender=MALE, balance=12.00, departmentId=3, enabled=true, createAt=2021-12-02T08:59:43, updateAt=null), User(id=7, name=孙尚香, gender=FEMALE, balance=54.00, departmentId=3, enabled=true, createAt=2021-12-02T08:59:43, updateAt=null)]
		*/
	}
}
