package io.springboot.example.repository;

import org.springframework.stereotype.Repository;

import io.springboot.example.entity.Department;

@Repository
public interface DepartmentRepository extends BaseRepository<Department, Integer> {

}
