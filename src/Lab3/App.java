package Lab3;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Arrays;

import Lab2.MD5Hashing;
import Lab1.LinearCongruentialGenerator;

public class App {
    public static void main(String[] args) {
        String plaintext = "Hello World";
        byte[] key = new byte[16];
        for (int i = 0; i < 16; i++) {
            key[i] = (byte) i;  // простий ключ для тестування
        }
        Lab3.RC5 rc5_cbc_pad = new Lab3.RC5(key);

        byte[] iv = generateIV();  // 8 bytes IV for two 32-bit blocks
//        new Random().nextBytes(iv);  // Random IV for each encryption

        byte[] encrypted = rc5_cbc_pad.encryptCBC(plaintext.getBytes(), iv);
        byte[] decrypted = rc5_cbc_pad.decryptCBC(encrypted, iv);

        System.out.println("Original: " + plaintext);
        System.out.println("Encrypted: " + new String(encrypted));
        System.out.println("Decrypted: " + new String(decrypted));
    }

    public static String hashKeyWord(String input) {
        return MD5Hashing.generateMD5(input);
    }

    public static byte[] generateIV() {
        int n = 8;

        //Модуль порівняння
        int m = (int) Math.pow(2, 11);
        //Множник
        int a = (int) Math.pow(3, 5);
        //Приріст
        int c = 1;
        //Початкове значення
        int seed = 4;

        // Створення генератора
        LinearCongruentialGenerator lcg = new LinearCongruentialGenerator(m, a, c, seed);
        byte[] iv = new byte[n];
        List<Integer> randomNumbers = lcg.generateRandomNumbers(n);

        for(int i = 0; i < n; i++) {
            iv[i] = (byte) (randomNumbers.get(i) & 0xFF); // Мета цієї операції - забезпечити, що значення перетворюється в байт,
                                                          // відкидаючи всі біти, які не вміщуються в один байт.
                                                          //0xFF (яке в шістнадцятковій системі дорівнює 255 або 11111111 в двійковій системі)
        }
        return iv;
    }

//    public static void checkFileIndentity(String originalFile, String decryptedFile) throws Exception {
//        String originalContent = readFileAsString(originalFile);
//        String decryptedContent = readDecryptedFile(decryptedFile);
////        String str = new String(data, StandardCharsets.UTF_8);
//        System.out.println("originalContent " + originalContent);
//        System.out.println("decryptedContent " + decryptedContent);
//        if (originalContent.equals(decryptedContent)) {
//            System.out.println("Decryption is successful! Original and decrypted contents are identical.");
//        } else {
//            System.out.println("There seems to be a mismatch between original and decrypted contents.");
//        }
//    }
//    public static String readFileAsString(String filename) throws Exception {
//        return new String(Files.readAllBytes(Paths.get(filename)));
//    }
//
//    public static String readDecryptedFile(String filePath) throws IOException {
//        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
//        System.out.println(fileBytes);
//        return new String(fileBytes, StandardCharsets.UTF_8);
//    }
}
