package com.vippygames.bianic.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

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

    public String getCurrentTime() {
        String dateFormatNow = "yyyy-MM-dd HH:mm:ss";

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatNow);
        return sdf.format(cal.getTime());
    }

    public String convertUtcToLocalTime(String utcTime) {
        try {
            String format = "yyyy-MM-dd HH:mm:ss";

            SimpleDateFormat utcFormat = new SimpleDateFormat(format);
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = utcFormat.parse(utcTime);

            SimpleDateFormat localFormat = new SimpleDateFormat(format);
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
        BigDecimal bigDecimal = new BigDecimal(String.format("%." + digitsAfterDot + "f", number));

        // Added because there is a bug when number is zero trailing zero's aren't removed
        BigDecimal zero = BigDecimal.ZERO;
        if (bigDecimal.compareTo(zero) == 0) {
            bigDecimal = zero;
        }

        return bigDecimal.stripTrailingZeros().toPlainString();
    }
}
