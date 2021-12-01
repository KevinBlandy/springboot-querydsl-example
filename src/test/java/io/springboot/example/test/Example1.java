package io.springboot.example.test;

import io.springboot.example.ExampleApplication;
import io.springboot.example.entity.QDepartment;
import io.springboot.example.entity.QUser;
import io.springboot.example.service.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExampleApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@Slf4j
public class Example1 {
	
	@Autowired
	private UserService userService;
	
	@Data
	public static class UserDTO {
		private Integer userId;
		private String name;
		private LocalDateTime createAt;
		private Integer groupId;
		private String departmentTitle;
	}

	@Test
	@Transactional
	@Rollback(false)
	public void test() {
		
		@SuppressWarnings("deprecation")
		QueryResults<UserDTO> results = this.userService.applyReadOnly(query -> {
			
			QUser qUser = QUser.user;
			QDepartment qDepartment = QDepartment.department; 

			return query.select(
							Projections.fields(UserDTO.class,
								qUser.id, qUser.name, qUser.createAt, 
								qDepartment.id.as("groupId"), qDepartment.title.as("departmentTitle")
							)
						)
						.from(qUser)
						.innerJoin(qDepartment).on(qDepartment.id.eq(qUser.departmentId))
						.where(qUser.enabled.eq(true)
								.and(qDepartment.enabled.eq(true))
								.and(qDepartment.createAt.between(null, LocalDateTime.now())))
						.orderBy(qUser.id.asc(), qUser.name.desc())
						.offset(1)
						.limit(10)
						// .fetch()  // 只分页，不检索总记录数量，返回 List<UserDTO>
						.fetchResults()
						;
		});
		log.info("count={}", results.getTotal());
		log.info("list={}", results.getResults());
	}
}
