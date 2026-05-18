package cl.duoc.libroDigital.authService.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptGenerator {
    public static void main(String[] args) {
        String password = "admin123!";
        String hash = new BCryptPasswordEncoder().encode(password);
        System.out.println("Hash para '" + password + "':");
        System.out.println(hash);
    }
}
