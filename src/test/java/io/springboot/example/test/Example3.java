package io.springboot.example.test;

import io.springboot.example.ExampleApplication;
import io.springboot.example.entity.QUser;
import io.springboot.example.entity.User;
import io.springboot.example.service.UserService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import com.querydsl.core.QueryResults;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExampleApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class Example3 {
	
	@Autowired
	private UserService userService;

	/**
	 * 分页 + count检索
	 */
	@SuppressWarnings("deprecation")
	@Test
	@Transactional
	@Rollback(false)
	public void test1() {
		
		// 第1页，5条记录
		int page = 1;
		int size = 5;
		
		QueryResults<User> result = this.userService.applyReadOnly(query -> {
			QUser qUser = QUser.user;
			return query.select(qUser)
				.from(qUser)
				// offset + limit 完成分页
				.offset((page - 1) * size)
				.limit(size)
				.fetchResults() // fetchResults 返回分页数据 + count检索数据
				;
		});
		
		log.info("test1: count={}", result.getTotal()); // long
		log.info("test1 list={}", result.getResults());  // List<User>
		
		/*
		 	SQL日志
	 	    select
		        count(user0_.id) as col_0_0_ 
		    from
		        user user0_
		        
		     
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
		        user user0_ limit ?
		test1: count=7
		test1 list=[User(id=1, name=曹操, gender=MALE, balance=122.50, departmentId=1, enabled=true, createAt=2021-12-01T21:39:02, updateAt=2021-12-01T21:43:50), User(id=2, name=许褚, gender=MALE, balance=15.00, departmentId=1, enabled=true, createAt=2021-12-01T21:39:02, updateAt=null), User(id=3, name=刘备, gender=MALE, balance=24.00, departmentId=2, enabled=true, createAt=2021-12-01T21:39:02, updateAt=null), User(id=4, name=关羽, gender=MALE, balance=100.00, departmentId=2, enabled=true, createAt=2021-12-01T21:39:02, updateAt=null), User(id=5, name=张飞, gender=MALE, balance=9.00, departmentId=2, enabled=true, createAt=2021-12-01T21:39:02, updateAt=null)]
		*/
	}

	/**
	 * 只分页，不检索总记录数量。并且排序
	 */
	@Test
	@Transactional
	@Rollback(false)
	public void test2 () {
		
		// 第1页，5条记录
		int page = 1;
		int size = 5;
		
		List<User> result = this.userService.applyReadOnly(query -> {
			QUser qUser = QUser.user;
			return query.select(qUser)
				.from(qUser)
				// offset + limit 完成分页
				.offset((page - 1) * size)
				.limit(size)
				.orderBy(qUser.createAt.desc(), 			// 根据 createAt desc 排序
						qUser.departmentId.asc().nullsFirst()) // 再根据 departmentId asc 排序， null 排在前面
				.fetch() // fetch 返回分页数据
				;
		});
		
		log.info("test2 : {}", result);
		
		/*
		SQL日志
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
		    order by
		        user0_.create_at desc,
		        case 
		            when user0_.department_id is null then 0 
		            else 1 
		        end,
		        user0_.department_id asc limit ?
		    
		    
		test2 : [User(id=1, name=曹操, gender=MALE, balance=122.50, departmentId=1, enabled=true, createAt=2021-12-01T21:39:02, updateAt=2021-12-01T21:43:50), User(id=2, name=许褚, gender=MALE, balance=15.00, departmentId=1, enabled=true, createAt=2021-12-01T21:39:02, updateAt=null), User(id=3, name=刘备, gender=MALE, balance=24.00, departmentId=2, enabled=true, createAt=2021-12-01T21:39:02, updateAt=null), User(id=4, name=关羽, gender=MALE, balance=100.00, departmentId=2, enabled=true, createAt=2021-12-01T21:39:02, updateAt=null), User(id=5, name=张飞, gender=MALE, balance=9.00, departmentId=2, enabled=true, createAt=2021-12-01T21:39:02, updateAt=null)]
		*/
	}
	
	/**
	 * 只检索总记录数量，不查询数据
	 */
	@Test
	@Transactional
	@Rollback(false)
	public void test3 () {
		
		@SuppressWarnings("deprecation")
		long count = this.userService.applyReadOnly(query -> {
			QUser qUser = QUser.user;
			return query.select(qUser)
				.from(qUser)
				.fetchCount()		// fetchCount 返回总数量
				;
		});
		
		log.info("test3 : {}", count);
		
		/*
		    select
		        count(user0_.id) as col_0_0_ 
		    from
		        user user0_
		   test3 : 7
		*/
	}
	
	/**
	 * fetchCount 和 fetchResults 很方便，但是被标记为过时，是最近这个版本才设置的。
	 * 是因为分页查询，只针对于简单的查询，对于group 查询可能会导致异常。
	 * 你如果非要使用的话，那么group查询是在内存中完成的分页。
	 * 所以官方给了过时的标识，但是你只要用在基本的简单查询中是没有问题的。
	 */
}
