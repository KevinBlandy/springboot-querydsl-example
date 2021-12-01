package io.springboot.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**

## 单表查询/删除/修改

## JOIN查询

## 条件分组

## 分页

## 排序

## 子查询

## 投影查询

## Exists 查询

## 锁查询

## 聚合检索

## 其他

**/
@EnableJpaRepositories(basePackages = { "io.springboot.example.repository" })
@EntityScan(basePackages = { "io.springboot.example.entity" })
@SpringBootApplication
public class ExampleApplication {
	public static void main(String[] args) {
		SpringApplication.run(ExampleApplication.class, args);
	}
}
