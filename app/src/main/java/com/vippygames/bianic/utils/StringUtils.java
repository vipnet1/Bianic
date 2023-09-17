package com.vippygames.bianic.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

public class StringUtils {
    private static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

    public String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rng = new Random();
        char[] text = new char[length];

        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }

        return new String(text);
    }

    public String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }

    public String convertUtcToLocalTime(String utcTime) {
        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat(DATE_FORMAT_NOW);
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = utcFormat.parse(utcTime);

            SimpleDateFormat localFormat = new SimpleDateFormat(DATE_FORMAT_NOW);
            localFormat.setTimeZone(TimeZone.getDefault());
            return localFormat.format(date);
        } catch (Exception e) {
            return "UTC TIME: " + utcTime;
        }
    }

    public boolean isAlphanumeric(String text) {
        return text.matches("^[a-zA-Z0-9]*$");
    }

    public String convertDoubleToString(double number, int digitsAfterDot) {
        BigDecimal bigDecimal = new BigDecimal(String.format(Locale.ENGLISH, "%." + digitsAfterDot + "f", number));

        // Added because there is a bug when number is zero trailing zero's aren't removed
        BigDecimal zero = BigDecimal.ZERO;
        if (bigDecimal.compareTo(zero) == 0) {
            bigDecimal = zero;
        }

        return bigDecimal.stripTrailingZeros().toPlainString();
    }
}
