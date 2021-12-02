package io.springboot.example.test;

import io.springboot.example.ExampleApplication;
import io.springboot.example.entity.QUser;
import io.springboot.example.entity.User;
import io.springboot.example.entity.User.Gender;
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
import com.querydsl.core.types.dsl.CaseBuilder;

/**
 * 
 * @author KevinBlandy
 *
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExampleApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class Example9 {

	@Autowired
	private UserService userService;
	
	/**
	 * 结果列的一些操作
	 * 
	 * 还有很多，可以自己研究。这里只列了我用过的
	 */
	@Test
	@Transactional
	@Rollback(false)
	public void test() {
		List<Tuple> result = userService.applyReadOnly(query -> {
			
			
			QUser qUser = QUser.user;
			
			qUser.gender.when(User.Gender.FEMALE).then("");
			
			return query.select(qUser.id, 
					
					qUser.name.upper(),				// 字段转换为大写
					
					qUser.createAt.dayOfYear(),		// 获取一年中的日
					
					new CaseBuilder().when(qUser.createAt.year().eq(2021)).then("2021创建的")
									.when(qUser.createAt.year().eq(2022)).then("2022创建的")
									.otherwise("不知道啥时候创建的"),			// 根据 createAt 字段，做 case 判断
					
					
					qUser.gender.when(Gender.FEMALE).then("女的")
								.when(Gender.MALE).then("男的")
								.otherwise("未知的"),	// 根据 gender 字段，做 case 判断
						
					qUser.updateAt.isNotNull().as("updated") // 是否有更新过
					)
				.from(qUser)
				.fetch()
				;
		});
		log.info("test result: {}", result);
		
		/*
		
		    select
		        user0_.id as col_0_0_,
		        upper(user0_.name) as col_1_0_,
		        dayofyear(user0_.create_at) as col_2_0_,
		        case 
		            when year(user0_.create_at)=? then ? 
		            when year(user0_.create_at)=? then ? 
		            else '不知道啥时候创建的' 
		        end as col_3_0_,
		        case 
		            when user0_.gender=? then ? 
		            when user0_.gender=? then ? 
		            else '未知的' 
		        end as col_4_0_,
		        user0_.update_at is not null as col_5_0_ 
		    from
		        user user0_
			test result: [[1, 曹操, 336, 2021创建的, 男的, false], [2, 许褚, 336, 2021创建的, 男的, false], [3, 刘备, 336, 2021创建的, 男的, false], [4, 关羽, 336, 2021创建的, 男的, false], [5, 张飞, 336, 2021创建的, 男的, false], [6, 孙权, 336, 2021创建的, 男的, false], [7, 孙尚香, 336, 2021创建的, 女的, false]]
		*/
	}
}
