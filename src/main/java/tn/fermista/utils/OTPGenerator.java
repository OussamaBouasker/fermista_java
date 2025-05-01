package tn.fermista.utils;

import java.util.Random;

public class OTPGenerator {
    private static final String NUMBERS = "0123456789";
    private static final int OTP_LENGTH = 6;
    private static final Random random = new Random();

    public static String generateOTP() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        }
        return otp.toString();
    }
} 