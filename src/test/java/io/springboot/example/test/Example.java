package io.springboot.example.test;

import io.springboot.example.ExampleApplication;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExampleApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class Example {

	@Test
	@Transactional
	@Rollback(false)
	public void test() {
		log.info("");
	}
}
