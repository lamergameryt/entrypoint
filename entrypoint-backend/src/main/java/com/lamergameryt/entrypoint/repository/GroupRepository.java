package com.lamergameryt.entrypoint.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lamergameryt.entrypoint.model.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {}

