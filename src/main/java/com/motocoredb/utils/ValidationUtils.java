package com.motocoredb.utils;

public class ValidationUtils {
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^[+]?[(]?[0-9]{1,4}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{3,6}$");
    }

    public static boolean isValidDocument(String document) {
        return document != null && document.matches("^[0-9]{6,12}$");
    }
}