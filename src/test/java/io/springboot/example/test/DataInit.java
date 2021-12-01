package io.springboot.example.test;

import io.springboot.example.ExampleApplication;
import io.springboot.example.entity.Department;
import io.springboot.example.entity.User;
import io.springboot.example.service.DepartmentService;
import io.springboot.example.service.UserService;

import java.time.LocalDateTime;

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
 * 初始化一些数据
 * 
 * @author KevinBlandy
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExampleApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class DataInit {

	@Autowired
	private UserService userService;
	
	@Autowired
	private DepartmentService departmentService;
	
	@Test
	@Transactional
	@Rollback(false)
	public void test() {
		// 创建几个部门
 		Department dept1 = Department.builder().title("魏国").createAt(LocalDateTime.now()).remark("曹魏控XX").enabled(true).build();
 		Department dept2 = Department.builder().title("蜀国").createAt(LocalDateTime.now()).remark("蜀汉全是X").enabled(true).build();
 		Department dept3 = Department.builder().title("吴国").createAt(LocalDateTime.now()).remark("孙吴爱XX").enabled(true).build();
 		
		this.departmentService.save(dept1);
		this.departmentService.save(dept2);
		this.departmentService.save(dept3);
		
		// 创建一些用户
		User u1 = User.builder().name("曹操").createAt(LocalDateTime.now()).gender(User.Gender.MALE).departmentId(dept1.getId()).enabled(true).build();
		User u2 = User.builder().name("许褚").createAt(LocalDateTime.now()).gender(User.Gender.MALE).departmentId(dept1.getId()).enabled(true).build();
		
		User u3 = User.builder().name("刘备").createAt(LocalDateTime.now()).gender(User.Gender.MALE).departmentId(dept2.getId()).enabled(true).build();
		User u4 = User.builder().name("关羽").createAt(LocalDateTime.now()).gender(User.Gender.MALE).departmentId(dept2.getId()).enabled(true).build();
		User u5 = User.builder().name("张飞").createAt(LocalDateTime.now()).gender(User.Gender.MALE).departmentId(dept2.getId()).enabled(true).build();
		
		User u6 = User.builder().name("孙权").createAt(LocalDateTime.now()).gender(User.Gender.MALE).departmentId(dept3.getId()).enabled(true).build();
		User u7 = User.builder().name("孙尚香").createAt(LocalDateTime.now()).gender(User.Gender.FEMALE).departmentId(dept3.getId()).enabled(true).build();
		
		this.userService.save(u1);
		this.userService.save(u2);
		this.userService.save(u3);
		this.userService.save(u4);
		this.userService.save(u5);
		this.userService.save(u6);
		this.userService.save(u7);
	}
}
