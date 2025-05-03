package com.motocoredb.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils {
    private static final int SALT_LENGTH = 16;
    private static final String ALGORITHM = "SHA-256";
    
    /**
     * Encripta una contraseña utilizando un salt aleatorio y el algoritmo SHA-256.
     * @param password La contraseña a encriptar.
     * @return La contraseña encriptada en formato Base64.
     */
    public static String encrypt(String password) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            digest.update(salt);
            byte[] hashedPassword = digest.digest(password.getBytes());
            
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);
            
            return Base64.getEncoder().encodeToString(combined);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar contraseña", e);
        }
    }
    
    /**
     * Verifica si una contraseña coincide con su hash almacenado.
     * @param password La contraseña a verificar.
     * @param storedHash El hash almacenado en formato Base64.
     * @return true si la contraseña coincide, false en caso contrario.
     */
    public static boolean verify(String password, String storedHash) {
        try {
            byte[] combined = Base64.getDecoder().decode(storedHash);
            
            if (combined.length <= SALT_LENGTH) {
                System.err.println("[ERROR] Hash almacenado es inválido");
                return false;
            }
            
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(combined, 0, salt, 0, SALT_LENGTH);
            
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            digest.update(salt);
            byte[] testHash = digest.digest(password.getBytes());
        
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