package com.stkapi.stk;

import java.security.SecureRandom;

public class TransactionCodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String NUMBERS = "0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateCode(int length) {
        if (length < 7) {
            length = 10;
        }

        StringBuilder sb = new StringBuilder(length);

        sb.append('U');
        sb.append('J');

        sb.append(NUMBERS.charAt(RANDOM.nextInt(NUMBERS.length())));

        sb.append('I');

        for (int i = 0; i < 3; i++) {
            sb.append(NUMBERS.charAt(RANDOM.nextInt(NUMBERS.length())));
        }

        for (int i = 7; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }

        return sb.toString();
    }
}