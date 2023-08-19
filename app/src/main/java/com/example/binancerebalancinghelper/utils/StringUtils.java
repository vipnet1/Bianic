package com.example.binancerebalancinghelper.utils;

import java.util.Random;

public class StringUtils {
    public String generateRandomString(int length)
    {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rng = new Random();
        char[] text = new char[length];

        for (int i = 0; i < length; i++)
        {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }

        return new String(text);
    }
}
