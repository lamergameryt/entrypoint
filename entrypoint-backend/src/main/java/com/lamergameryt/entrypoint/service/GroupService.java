package com.lamergameryt.entrypoint.service;

import org.springframework.stereotype.Service;

import com.lamergameryt.entrypoint.model.Group;
import com.lamergameryt.entrypoint.repository.GroupRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;

    public Group getById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + id));
    }
}