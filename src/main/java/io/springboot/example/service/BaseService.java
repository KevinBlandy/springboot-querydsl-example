package io.springboot.example.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface BaseService <T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor <T>, QuerydslPredicateExecutor<T> {

}