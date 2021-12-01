package io.springboot.example.test;

import io.springboot.example.ExampleApplication;
import io.springboot.example.entity.Department;
import io.springboot.example.entity.QDepartment;
import io.springboot.example.entity.QUser;
import io.springboot.example.entity.User;
import io.springboot.example.service.UserService;
import lombok.Data;
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

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;

/**
 * 
 * join检索
 * @author KevinBlandy
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExampleApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@Slf4j
public class Example2 {
	
	@Autowired
	private UserService userService;  // 为了方便，直接用 service 进行检索，它封装了 JAPQueryFactory
	
	
	/**
	 * Join查询
	 */
	@Test
	@Transactional
	@Rollback(false)
	public void test1() {
		this.userService.applyReadOnly(query -> {
			QUser qUser = new QUser("u");
			QDepartment qDepartment = new QDepartment("d");

			List<Tuple> list = query.select(
							qUser, 
							qDepartment)	
				.from(qUser)
				.innerJoin(qDepartment).on(qDepartment.id.eq(qUser.departmentId))
				// leftJoin ..都有，自己，慢慢试
				.where(qDepartment.enabled.eq(true)
						.and(qUser.id.between(1, 3)))
				.fetch() // fetch 返回列表
				;
			
			// tuple 本质上像是一个Map，可以获取每一列的数据
			
			list.forEach(tuple -> {
				User user = tuple.get(qUser);
				Department department = tuple.get(qDepartment);
				log.info("rest1 result: user={}, department={}", user, department);
			});
			
			
			/*
			 SQL 日志
			     select
			        user0_.id as id1_1_0_,
			        department1_.id as id1_0_1_,
			        user0_.balance as balance2_1_0_,
			        user0_.create_at as create_a3_1_0_,
			        user0_.department_id as departme4_1_0_,
			        user0_.enabled as enabled5_1_0_,
			        user0_.gender as gender6_1_0_,
			        user0_.name as name7_1_0_,
			        user0_.update_at as update_a8_1_0_,
			        department1_.create_at as create_a2_0_1_,
			        department1_.enabled as enabled3_0_1_,
			        department1_.remark as remark4_0_1_,
			        department1_.title as title5_0_1_ 
			    from
			        user user0_ 
			    inner join
			        department department1_ 
			            on (
			                department1_.id=user0_.department_id
			            ) 
			    where
			        department1_.enabled=? 
			        and (
			            user0_.id between ? and ?
			        )
			       
				rest1 result: user=User(id=1, name=曹操, gender=MALE, balance=122.50, departmentId=1, enabled=true, createAt=2021-12-01T21:39:02, updateAt=2021-12-01T21:43:50), department=Department(id=1, title=魏国, remark=曹魏控XX, enabled=true, createAt=2021-12-01T21:39:02)
				rest1 result: user=User(id=2, name=许褚, gender=MALE, balance=15.00, departmentId=1, enabled=true, createAt=2021-12-01T21:39:02, updateAt=null), department=Department(id=1, title=魏国, remark=曹魏控XX, enabled=true, createAt=2021-12-01T21:39:02)
				rest1 result: user=User(id=3, name=刘备, gender=MALE, balance=24.00, departmentId=2, enabled=true, createAt=2021-12-01T21:39:02, updateAt=null), department=Department(id=2, title=蜀国, remark=蜀汉全是X, enabled=true, createAt=2021-12-01T21:39:02)
			*/
			
			return list;
		});
	}
	
	// 自定义一个对象来接收数据
	@Data
	public static class UserDTO {
		private Integer id;
		private String name;
		private LocalDateTime createAt;
		private Integer departmentId;
		private String departmentTitle;
	}

	/**
	 * Join投影查询
	 */
	@Test
	@Transactional
	@Rollback(false)
	public void test2() {
		
		List<UserDTO> results = this.userService.applyReadOnly(query -> {
			
			QUser qUser = QUser.user;
			QDepartment qDepartment = QDepartment.department; 

			return query.select(
							Projections.fields(UserDTO.class,			// Projections.fields 根据字段属性名称封装数据
								qUser.id, qUser.name, qUser.createAt, 			// user 表的列
								qDepartment.id.as("departmentId"), qDepartment.title.as("departmentTitle") //department 表的列
							)
						)
						.from(qUser)
						.innerJoin(qDepartment).on(qDepartment.id.eq(qUser.departmentId))
						.where(qUser.enabled.eq(true)
								.and(qDepartment.enabled.eq(true))
								.and(qUser.id.in(1, 2, 3)))
						.orderBy(qUser.id.asc(), qUser.name.desc())
						.fetch()
						;
		});
		
		log.info("test2 result={}", results);
		
		/*
		SQL日志
		    select
		        user0_.id as col_0_0_,
		        user0_.name as col_1_0_,
		        user0_.create_at as col_2_0_,
		        department1_.id as col_3_0_,
		        department1_.title as col_4_0_ 
		    from
		        user user0_ 
		    inner join
		        department department1_ 
		            on (
		                department1_.id=user0_.department_id
		            ) 
		    where
		        user0_.enabled=? 
		        and department1_.enabled=? 
		        and (
		            user0_.id in (
		                ? , ? , ?
		            )
		        ) 
		    order by
		        user0_.id asc,
		        user0_.name desc
		 	
		 	test2 result=[Example2.UserDTO(id=1, name=曹操, createAt=2021-12-01T21:39:02, departmentId=1, departmentTitle=魏国), Example2.UserDTO(id=2, name=许褚, createAt=2021-12-01T21:39:02, departmentId=1, departmentTitle=魏国), Example2.UserDTO(id=3, name=刘备, createAt=2021-12-01T21:39:02, departmentId=2, departmentTitle=蜀国)]
		*/
	}
}
