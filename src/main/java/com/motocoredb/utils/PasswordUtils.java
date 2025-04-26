package com.motocoredb.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils {
    private static final int SALT_LENGTH = 16;
    private static final String ALGORITHM = "SHA-256";
    
    public static String encrypt(String password) {
        try {
            // Generar salt aleatorio
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Combinar salt con password y hashear
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            digest.update(salt);
            byte[] hashedPassword = digest.digest(password.getBytes());
            
            // Combinar salt + hash para almacenamiento
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);
            
            return Base64.getEncoder().encodeToString(combined);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar contraseña", e);
        }
    }
    
    public static boolean verify(String password, String storedHash) {
        try {
            // Decodificar el hash almacenado
            byte[] combined = Base64.getDecoder().decode(storedHash);
            
            if (combined.length <= SALT_LENGTH) {
                System.err.println("[ERROR] Hash almacenado es inválido");
                return false;
            }
            
            // Extraer el salt (primeros 16 bytes)
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(combined, 0, salt, 0, SALT_LENGTH);
            
            // Calcular hash con el mismo salt
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            digest.update(salt);
            byte[] testHash = digest.digest(password.getBytes());
            
            // Comparar con el hash almacenado (bytes después del salt)
            for (int i = 0; i < testHash.length; i++) {
                if (testHash[i] != combined[i + SALT_LENGTH]) {
                    return false;
                }
            }
            return true;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al verificar contraseña", e);
        }
    }
}