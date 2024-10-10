package com.capstone.usa.sms.service;

import com.capstone.usa.sms.dto.PhoneDto;
import com.capstone.usa.auth.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class SmsService {
    private final String apiKey;
    private final String id;
    private final String sender;
    private final UserRepository userRepository;

    public SmsService(@Value("${aligo.apiKey}") String apiKey,
                      @Value("${aligo.userId}") String id,
                      @Value("${aligo.sender}") String sender,
                      UserRepository userRepository) {

        this.apiKey = apiKey;
        this.id = id;
        this.sender = sender;
        this.userRepository = userRepository;
    }

    public ResponseEntity<String> sendOne(PhoneDto dto) throws JsonProcessingException {
        if (userRepository.findById(dto.getPhoneNumber()).isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이미 등록된 전화번호 입니다");
        } else {
            String smsUrl = "https://apis.aligo.in/send/";

            Map<String, String> sms = new HashMap<>();
            sms.put("user_id", id);
            sms.put("key", apiKey);

            String verificationCode = VerificationService.GenerateNumber(dto.getPhoneNumber());
            sms.put("msg", "[USA]인증번호는 " + verificationCode + " 입니다.");
            sms.put("receiver", dto.getPhoneNumber());
            sms.put("sender", sender);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            for (Map.Entry<String, String> entry : sms.entrySet()) {
                body.add(entry.getKey(), entry.getValue());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(smsUrl, requestEntity, String.class);

            System.out.println(response.getBody());

            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode responseJson = objectMapper.readTree(responseBody);
            String resultCode = responseJson.get("result_code").asText();

            if ("1".equals(resultCode)) {
                return ResponseEntity.status(HttpStatus.OK).body("인증번호가 전송되었습니다");
            } else
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("인증번호 전송 중 오류가 발생했습니다");
        }
    }
}
