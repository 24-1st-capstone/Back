package com.capstone.usa.auth.security;

import com.capstone.usa.auth.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        if (!userRepository.existsById(phoneNumber)) {
            throw new UsernameNotFoundException(phoneNumber + "에 해당하는 사용자가 존재하지 않습니다.");
        }
        return userRepository.findById(phoneNumber).get();
    }
}
