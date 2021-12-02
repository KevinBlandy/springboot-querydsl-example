package io.springboot.example.test;

import io.springboot.example.ExampleApplication;
import io.springboot.example.entity.QDepartment;
import io.springboot.example.entity.QUser;
import io.springboot.example.service.UserService;
import lombok.extern.slf4j.Slf4j;

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
 * 聚合查询
 * @author KevinBlandy
 *
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExampleApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class Example5 {
	
	@Autowired
	private UserService userService;

	@Test
	@Transactional
	@Rollback(false)
	public void test() {
		
		this.userService.applyReadOnly(query -> {
			QUser qUser = QUser.user;
			QDepartment qDepartment = QDepartment.department;
			
			query.select(qDepartment.id, qDepartment.title, 
						qUser.id.count() // 查询用户数量
					)
				.from(qDepartment)
				.innerJoin(qUser).on(qUser.departmentId.eq(qDepartment.id))
				.groupBy(qDepartment.id, qDepartment.title) // groupBy
					.having(qDepartment.title.in("魏国", "蜀国"))	// having
				.fetch()
				.stream()
				.forEach(tuple -> {
					log.info("test result: id={}, title={}, userCount={}"
							,tuple.get(qDepartment.id)
							,tuple.get(qDepartment.title)
							,tuple.get(qUser.id.count())
							);
				})
				;
			
			return null;
		});
		
		/*
		
		    select
		        department0_.id as col_0_0_,
		        department0_.title as col_1_0_,
		        count(user1_.id) as col_2_0_ 
		    from
		        department department0_ 
		    inner join
		        user user1_ 
		            on (
		                user1_.department_id=department0_.id
		            ) 
		    group by
		        department0_.id ,
		        department0_.title 
		    having
		        department0_.title in (
		            ? , ?
		        )
			test result: id=2, title=蜀国, userCount=3
			test result: id=1, title=魏国, userCount=2
		*/
	}
}
