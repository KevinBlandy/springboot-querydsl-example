package io.springboot.example.test;

import io.springboot.example.ExampleApplication;
import io.springboot.example.entity.Department;
import io.springboot.example.entity.QDepartment;
import io.springboot.example.entity.QUser;
import io.springboot.example.entity.User;
import io.springboot.example.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
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

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;


/**
 * 
 * 结果集封装
 * 
 * 核心是 Projections 类
 * 
 * @author KevinBlandy
 *
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExampleApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class Example8 {

	@Autowired
	private UserService userService;
	
	@Data
	@Builder
	@With
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserDTO {
		private Integer id;
		private String name;
		private Department department; // 部门
	}
	
	/**
	 * 
	 * 前面的一些案例，已经展示了不少封装方式。Tuple/Projections 等等。
	 * 
	 * 封装为自定义的对象
	 */
	@Test
	@Transactional
	@Rollback(false)
	public void test() {
		List<UserDTO> result = this.userService.applyReadOnly(query -> {
			
			QUser qUser = QUser.user;
			QDepartment qDepartment = QDepartment.department;
			
			// 检索用户的id和name，封装到 UserDTO
			QBean<UserDTO> userQBean = Projections.fields(UserDTO.class, qUser.id, qUser.name);
			// 检索部门的id和title，封装到 Department
			QBean<Department> departmentQBean = Projections.fields(Department.class, qDepartment.id, qDepartment.title);
			
			return query.select(
						userQBean,
						departmentQBean
					)
					.from(qUser)
					.leftJoin(qDepartment).on(qDepartment.id.eq(qUser.departmentId))
					.fetch()
					.stream()
					.map(tuple -> {
						// 从 tuple 中获取到结果，封装到一个对象中
						return tuple.get(userQBean).withDepartment(tuple.get(departmentQBean));
					})
					.toList()
					;
		});
		
		log.info("test result: {}", result);
		
		/*
		
		    select
		        user0_.id as col_0_0_,
		        user0_.name as col_1_0_,
		        department1_.id as col_2_0_,
		        department1_.title as col_3_0_ 
		    from
		        user user0_ 
		    left outer join
		        department department1_ 
		            on (
		                department1_.id=user0_.department_id
		            )
		test result: [Example8.UserDTO(id=1, name=曹操, department=Department(id=1, title=魏国, remark=null, enabled=null, createAt=null)), Example8.UserDTO(id=2, name=许褚, department=Department(id=1, title=魏国, remark=null, enabled=null, createAt=null)), Example8.UserDTO(id=3, name=刘备, department=Department(id=2, title=蜀国, remark=null, enabled=null, createAt=null)), Example8.UserDTO(id=4, name=关羽, department=Department(id=2, title=蜀国, remark=null, enabled=null, createAt=null)), Example8.UserDTO(id=5, name=张飞, department=Department(id=2, title=蜀国, remark=null, enabled=null, createAt=null)), Example8.UserDTO(id=6, name=孙权, department=Department(id=3, title=吴国, remark=null, enabled=null, createAt=null)), Example8.UserDTO(id=7, name=孙尚香, department=Department(id=3, title=吴国, remark=null, enabled=null, createAt=null))]
		*/
	}
	
	/**
	 * Projections 的其他一些封装方式
	 */
	@SuppressWarnings("unchecked")
	public void test2 () {
		
		// 根据构造函数封装
		QUser qUser = QUser.user;
		Projections.constructor(User.class, qUser.id, qUser.name);
		
		// 封装为map
		Projections.map(qUser.id, qUser.name);
		
		//  多个相同类型的列, 封装为数组
		Projections.array(Integer[].class, qUser.id, qUser.departmentId);
		
		//.... 自己研究吧
	}
	
	/**
	 * 结果集分组
	 * 在一些一对一多的检索下，可以对结果集进行分组。
	 * QueryDsl提供了 groupBy Api，但是我不会。（我一般都是检索出结果后，用java的stream来完成）
	 * 可以参考官方Demo: https://github.com/querydsl/querydsl/blob/master/querydsl-collections/src/test/java/com/querydsl/collections/GroupByTest.java
	 * 
	 */
	public void test3 () {
		this.userService.applyReadOnly(query -> {
			return query.select(QUser.user)
				.from(QUser.user)
				.transform(null)  // 对结果集进行分组的函数
				;
		});
	}
}
