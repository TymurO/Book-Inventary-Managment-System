package com.tovch.grpcserver.util;

public class IsbnValidator {

    public static boolean isValidIsbn10(String isbn) {
        if (isbn == null || isbn.length() != 10 || !isbn.matches("\\d{9}[0-9X]")) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (isbn.charAt(i) - '0') * (10 - i);
        }
        char lastChar = isbn.charAt(9);
        if (lastChar == 'X') {
            sum += 10;
        } else {
            sum += lastChar - '0';
        }
        return sum % 11 == 0;
    }

    public static boolean isValidIsbn13(String isbn) {
        if (isbn == null || isbn.length() != 13 || !isbn.matches("\\d{13}")) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = isbn.charAt(i) - '0';
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int checksum = 10 - (sum % 10);
        if (checksum == 10) {
            checksum = 0;
        }
        return checksum == isbn.charAt(12) - '0';
    }
}
