package tn.fermista.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Classe utilitaire pour gérer le hachage des mots de passe
 */
public class PasswordUtils {

    /**
     * Hache un mot de passe en utilisant SHA-256
     * @param password Le mot de passe en clair
     * @return Le mot de passe haché
     */
    public static String hashPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }

    /**
     * Vérifie si un mot de passe correspond à un hash
     * @param password Le mot de passe en clair
     * @param hashedPassword Le mot de passe haché
     * @return true si le mot de passe correspond au hash, false sinon
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        return hashPassword(password).equals(hashedPassword);
    }
}
