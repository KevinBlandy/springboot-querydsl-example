package io.springboot.example.test;

import io.springboot.example.ExampleApplication;
import io.springboot.example.entity.QDepartment;
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

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExampleApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class Example4 {

	@Autowired
	private UserService userService;
	
	/**
	 * 条件中的单行单列子查询
	 */
	@Test
	@Transactional
	@Rollback(false)
	public void test() {
		List<User> result = this.userService.applyReadOnly(query -> {
			QUser qUser = QUser.user;
			QDepartment qDepartment = QDepartment.department;
			
			return query.select(qUser)
				.from(qUser)
				.where(qUser.departmentId.eq(
						// 单行单列子查询
					JPAExpressions.select(qDepartment.id).from(qDepartment).where(qDepartment.title.eq("魏国"))
				))
				.fetch()
				;
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
		        user0_.department_id=(
		            select
		                department1_.id 
		            from
		                department department1_ 
		            where
		                department1_.title=?
		        )
        
        test result=[User(id=1, name=曹操, gender=MALE, balance=22.50, departmentId=1, enabled=true, createAt=2021-12-02T08:59:43, updateAt=null), User(id=2, name=许褚, gender=MALE, balance=15.00, departmentId=1, enabled=true, createAt=2021-12-02T08:59:43, updateAt=null)]
		*/
	}
	
	/**
	 * 条件中的单列多行子查询
	 */
	@Test
	@Transactional
	@Rollback(false)
	public void test1() {
		List<User> result = this.userService.applyReadOnly(query -> {
			QUser qUser = QUser.user;
			QDepartment qDepartment = QDepartment.department;
			
			return query.select(qUser)
				.from(qUser)
				.where(qUser.departmentId.in(
						// 单行多列子查询
					JPAExpressions.select(qDepartment.id).from(qDepartment).where(
						qDepartment.title.eq("魏国").or(qDepartment.title.eq("吴国"))		// 条件是 or ，会有2个结果
					)
				))
				.fetch()
				;
		});
		log.info("test1 result={}", result);
		
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
		        user0_.department_id in (
		            select
		                department1_.id 
		            from
		                department department1_ 
		            where
		                department1_.title=? 
		                or department1_.title=?
		        )
		*/
		
		//		test1 result=[User(id=6, name=孙权, gender=MALE, balance=12.00, departmentId=3, enabled=true, createAt=2021-12-02T08:59:43, updateAt=null), User(id=7, name=孙尚香, gender=FEMALE, balance=54.00, departmentId=3, enabled=true, createAt=2021-12-02T08:59:43, updateAt=null), User(id=1, name=曹操, gender=MALE, balance=22.50, departmentId=1, enabled=true, createAt=2021-12-02T08:59:43, updateAt=null), User(id=2, name=许褚, gender=MALE, balance=15.00, departmentId=1, enabled=true, createAt=2021-12-02T08:59:43, updateAt=null)]
	}
	
	/**
	 * 结果集中的COUNT子查询
	 */
	@Test
	@Transactional
	@Rollback(false)
	public void test3() {
		List<Tuple> result = this.userService.applyReadOnly(query -> {
			QDepartment qDepartment = QDepartment.department;
			QUser qUser = QUser.user;
			
			// count 子查询
			JPQLQuery<Long> count = JPAExpressions.select(qUser.departmentId.count()).from(qUser).where(qUser.departmentId.eq(qDepartment.id));
			
			return query.select(qDepartment.id, qDepartment.title, count)
					.from(qDepartment)
					.fetch()
					;
			
		});
		
		for (Tuple tuple : result) {
			// tuple 也可以通过下标和类型取值
			Integer id = tuple.get(0, Integer.class);
			String title = tuple.get(1, String.class);
			Long count = tuple.get(2, Long.class);
			log.info("tes3 result: id={}, title={}, count={}", id, title, count);
		}
		
		/*
		    select
		        department0_.id as col_0_0_,
		        department0_.title as col_1_0_,
		        (select
		            count(user1_.department_id) 
		        from
		            user user1_ 
		        where
		            user1_.department_id=department0_.id) as col_2_0_ 
		    from
		        department department0_
		   
			tes3 result: id=3, title=吴国, count=2
			tes3 result: id=2, title=蜀国, count=3
			tes3 result: id=1, title=魏国, count=2
		*/
	}
	
	/**
	 * 结果集中的 EXISTS 子查询
	 */
	@Test
	@Transactional
	@Rollback(false)
	public void test4() {
		List<Tuple> result = this.userService.applyReadOnly(query -> {
			QDepartment qDepartment = QDepartment.department;
			QUser qUser = QUser.user;
			
			// exists 子查询
			BooleanExpression exists = JPAExpressions.selectOne()
					.from(qUser)
					.where(qUser.departmentId.eq(qDepartment.id)
							.and(qUser.gender.eq(User.Gender.FEMALE)))
					.exists();
			
			return query.select(
						qDepartment.id, qDepartment.title, exists
					)
					.from(qDepartment)
					.fetch()
					;
			
		});
		log.info("test4 result={}", result);
		
		/*
		    select
		        department0_.id as col_0_0_,
		        department0_.title as col_1_0_,
		        exists (select
		            1 
		        from
		            user user1_ 
		        where
		            user1_.department_id=department0_.id 
		            and user1_.gender=?) as col_2_0_ 
		    from
		        department department0_
		   test4 result=[[3, 吴国, true], [2, 蜀国, false], [1, 魏国, false]]
		*/
	}
}

