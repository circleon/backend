package com.circleon.domain.user.service;

import com.circleon.domain.user.UserImageManager;

import com.circleon.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserFileStore userFileStore;

    public Resource loadImageAsResource(String filePath){
        return userFileStore.loadFileAsResource(filePath);
    }
}
