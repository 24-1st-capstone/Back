package com.capstone.usa.auth.service;

import com.capstone.usa.auth.dto.ModifyUserDto;
import com.capstone.usa.sms.service.VerificationService;
import com.capstone.usa.auth.dto.CreateUserDto;
import com.capstone.usa.auth.dto.LoginUserDto;
import com.capstone.usa.auth.dto.TokenDto;
import com.capstone.usa.auth.model.User;
import com.capstone.usa.auth.repository.UserRepository;
import com.capstone.usa.auth.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.security.core.AuthenticationException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;
    private JwtUtil jwtUtil;
    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> createUser(CreateUserDto dto) {
        String savedVerificationCode = VerificationService.getVerificationCode(dto.getPhoneNumber());

        if(userRepository.findById(dto.getPhoneNumber()).isPresent()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이미 등록된 전화번호 입니다");
        }
        else{
            if (savedVerificationCode != null && savedVerificationCode.equals(dto.getVerNumber())) {
                User user = new User(
                        dto.getName(),
                        dto.getPhoneNumber(),
                        passwordEncoder.encode(dto.getPassword())
                );
                userRepository.save(user);
                VerificationService.deleteVerificationCode(dto.getPhoneNumber());

                return ResponseEntity.ok().body("회원가입이 성공적으로 되었습니다");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증번호가 일치하지 않습니다");
            }
        }
    }

    public ResponseEntity<?> loginWithAuthenticationManager(LoginUserDto dto) {
        Optional<User> userOptional = userRepository.findById(dto.getPhoneNumber());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("존재하지 않는 전화번호입니다");
        }
        var request = new UsernamePasswordAuthenticationToken(
                dto.getPhoneNumber(), dto.getPassword()
        );
        try {
            var result = authenticationManager.authenticate(request);
            var token = new TokenDto(jwtUtil.generateToken((User) result.getPrincipal()));
            return ResponseEntity.ok().body(token);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("올바르지 않은 비밀번호입니다");
        }
    }

    public ResponseEntity<?> modifyUser(ModifyUserDto dto, User user) {
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("현재 비밀번호가 일치하지 않습니다");
        }

        user.setName(dto.getName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }
}
