//// 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스
//package com.ttasum.memorial.service;
//
//
//import com.ttasum.memorial.domain.entity.User;
//import com.ttasum.memorial.domain.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import javax.transaction.Transactional;
//
//@Service
//@RequiredArgsConstructor
//public class UserService {
//
//    private final UserRepository userRepository;
//
//    // 사용자 생성 메서드: 사용자 이름을 받아 저장 후 엔티티 반환
//    @Transactional
//    public User createUser(String name) {
//        return userRepository.save(User.builder().name(name).build());
//    }
//}
//
