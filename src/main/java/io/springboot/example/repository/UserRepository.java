package io.springboot.example.repository;

import org.springframework.stereotype.Repository;

import io.springboot.example.entity.User;

@Repository
public interface UserRepository extends BaseRepository<User, Integer> {

}
