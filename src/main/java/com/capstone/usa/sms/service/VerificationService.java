package com.capstone.usa.sms.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class VerificationService {
    private static final Map<String, String> verificationCodes = new HashMap<>();

    public static String GenerateNumber(String phoneNumber){
        Random random = new Random();
        int randomNumber = random.nextInt(9000) + 1000;
        String verificationCode = String.valueOf(randomNumber);

        verificationCodes.put(phoneNumber, verificationCode);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                deleteVerificationCode(phoneNumber);
            }
        }, 5 * 60 * 1000);

        return verificationCode;
    }

    public static String getVerificationCode(String phoneNumber) {
        return verificationCodes.get(phoneNumber);
    }

    public static void deleteVerificationCode(String phoneNumber) {
        verificationCodes.remove(phoneNumber);
    }
}
