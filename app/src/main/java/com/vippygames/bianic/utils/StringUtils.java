package com.vippygames.bianic.utils;

import java.math.BigDecimal;
import java.util.Random;

public class StringUtils {
    public String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rng = new Random();
        char[] text = new char[length];

        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }

        return new String(text);
    }

    public boolean isAlphanumeric(String text) {
        return text.matches("^[a-zA-Z0-9]*$");
    }

    public String convertDoubleToString(double number, int digitsAfterDot) {
        BigDecimal bigDecimal = new BigDecimal(String.format("%." + digitsAfterDot + "f", number));

        // Added because there is a bug when number is zero trailing zero's aren't removed
        BigDecimal zero = BigDecimal.ZERO;
        if (bigDecimal.compareTo(zero) == 0) {
            bigDecimal = zero;
        }

        return bigDecimal.stripTrailingZeros().toPlainString();
    }
}
