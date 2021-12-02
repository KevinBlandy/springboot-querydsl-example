package io.springboot.example.test;

import io.springboot.example.ExampleApplication;
import io.springboot.example.entity.QDepartment;
import io.springboot.example.entity.QUser;
import io.springboot.example.entity.User;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;


/**
 * 
 * 单表的查询/编辑/删除
 * @author KevinBlandy
 *
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExampleApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@Slf4j
public class Example1 {
	
	@PersistenceContext
	private EntityManager entityManager;
	

	/**
	 * 基本查询
	 */
	@Test
	@Transactional
	@Rollback(false)
	public void test1() {
		JPAQueryFactory query = new JPAQueryFactory(this.entityManager);
		
		QUser qUser = QUser.user;	// 生成的查询对象，可以理解为数据表
		
		User user = query.select(qUser).from(qUser).where(qUser.id.eq(1)).fetchOne();  // 查询唯一记录，如果结果不止一个则异常
		
		log.info("rest1 reuslt={}", user);
	}
	
	/**
	 * 投影查询，仅仅查询指定的列
	 */
	@Test
	@Transactional
	@Rollback(false)
	public void test2() {
		JPAQueryFactory query = new JPAQueryFactory(this.entityManager);
		
		QUser qUser = QUser.user;	
		
		/**
		 * 通过 Projections.bean 来指定封装的结果对象，以及要查询的列。
		 * 属性通过相同的 getter/setter 输入，如果属性名称不同，可以通过列的 as() 来修改
		 */
		User user = query.select(
						Projections.bean(User.class, qUser.id, qUser.enabled,
								qUser.createAt.as("updateAt")) // 这里把 create_at 列的值封装到结果对象的 updateAt 属性中
					)
					.from(qUser).where(qUser.enabled.eq(true))
					.fetchFirst();  // 查询第一条记录，会主动添加类似于 LIMIT 1 限制语句
		
		log.info("test2 result={}", user);
		
		/*
		 	从SQL日志可以看出，仅仅查询了声明的列。非常的灵活。避免滥用 SELECT * 查询。
		        select
			        user0_.id as col_0_0_,
			        user0_.enabled as col_1_0_,
			        user0_.create_at as col_2_0_ 
			    from
			        user user0_ 
			    where
			        user0_.enabled=? limit ?
		
			test2 result=User(id=1, name=null, gender=null, balance=null, departmentId=null, enabled=true, createAt=null, updateAt=2021-12-01T21:39:02)
		*/
	}
	
	/**
	 * 更新操作
	 */
	@Test
	@Transactional
	@Rollback(false)
	public void test3() {
		JPAQueryFactory query = new JPAQueryFactory(this.entityManager);
		
		QUser qUser = QUser.user;
		QDepartment qDepartment = QDepartment.department;
		
		long ret = query.update(qUser)
			.set(qUser.balance, qUser.balance.add(100))  // 自增
			.set(qUser.updateAt, LocalDateTime.now())	// 设置列值
			.setNull(qUser.updateAt) 					// 设置为null
			.set(qUser.departmentId, 
					JPAExpressions.select(qDepartment.id).from(qDepartment).where(qDepartment.id.eq(qUser.id))
			) // 子查询赋值（这里完全了为了演示这么写的）
			.where(qUser.id.eq(1).and(qUser.enabled.eq(true)))
			.execute()
			;
		log.info("test3 result={}", ret);
		
		/*
			SQL日志
			
		    update
		        user 
		    set
		        balance=balance+?,
		        update_at=?,
		        department_id=(select
		            department1_.id 
		        from
		            department department1_ 
		        where
		            department1_.id=user.id) 
		    where
		        id=? 
		        and enabled=?
		   test3 result=1
		*/
	}
	
	
	// 新增 ？？？？
	
	
	/**
	 * 我发现QueryDsl好像没新增Api，所以新增用JPA的Save就行。
	 */
}
