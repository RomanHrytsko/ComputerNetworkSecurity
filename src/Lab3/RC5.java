package Lab3;

import java.security.MessageDigest;
import java.nio.file.*;
import java.io.*;

public class RC5 {

    private final int w;          // розмір слова в бітах
    private final int r;          // кількість раундів
    private final int b;          // кількість раундів
    private static final int P = 0xB7E15163;
    private static final int Q = 0x9E3779B9;

    private int[] S;                         // раундові ключі

    public RC5(int w, int r, int b) {
        this.w = w;
        this.r = r;
        this.b = b;
        this.S = new int[2*(r+1)];
    }

    // Ініціалізація таблиці S
    public void initializeTable(byte[] key) {
        int keyLength = key.length;
        int u = w / 8;
        int c = (keyLength + u - 1) / u;
        int[] L = new int[c];

        // 1. Конвертація ключа у масив L
        for (int i = 0; i < keyLength; i++) {
            L[i / u] = (L[i / u] << 8) + key[i];
        }

        // 2. Ініціалізація таблиці S
        S = new int[2 * r + 2];
        S[0] = P;
        for (int i = 1; i < S.length; i++) {
            S[i] = S[i - 1] + Q;
        }

        // 3. Змішування ключа
        int A, B, i, j;
        A = B = i = j = 0;
        int v = 3 * Math.max(c, S.length);
        for (int k = 0; k < v; k++) {
            A = S[i] = leftRotate((S[i] + A + B), 3);
            B = L[j] = leftRotate((L[j] + A + B), A + B);
            i = (i + 1) % S.length;
            j = (j + 1) % c;
        }
    }
    public void encryptFile(String inputFilePath, String outputFilePath, String passwordPhrase) throws Exception {
        byte[] key = generateKey(passwordPhrase);
        initializeTable(key);

        byte[] fileData = Files.readAllBytes(Paths.get(inputFilePath));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (int i = 0; i < fileData.length; i += 8) {
            int[] block = new int[2];
            block[0] = (fileData[i] << 24) | ((i + 1 < fileData.length ? fileData[i + 1] : 0) << 16)
                    | ((i + 2 < fileData.length ? fileData[i + 2] : 0) << 8) | (i + 3 < fileData.length ? fileData[i + 3] : 0);
            block[1] = (i + 4 < fileData.length ? fileData[i + 4] : 0) << 24
                    | ((i + 5 < fileData.length ? fileData[i + 5] : 0) << 16)
                    | ((i + 6 < fileData.length ? fileData[i + 6] : 0) << 8) | (i + 7 < fileData.length ? fileData[i + 7] : 0);

            int[] encryptedBlock = new int[2];
            encrypt(block, encryptedBlock);

            for (int j = 0; j < 4; j++) {
                outputStream.write((encryptedBlock[0] >> (24 - j * 8)) & 0xFF);
                outputStream.write((encryptedBlock[1] >> (24 - j * 8)) & 0xFF);
            }
        }

        Files.write(Paths.get(outputFilePath), outputStream.toByteArray());
    }

    public void decryptFile(String inputFilePath, String outputFilePath, String passwordPhrase) throws Exception {
        byte[] key = generateKey(passwordPhrase);
        initializeTable(key);

        byte[] fileData = Files.readAllBytes(Paths.get(inputFilePath));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (int i = 0; i < fileData.length; i += 8) {
            int[] block = new int[2];
            block[0] = (fileData[i] << 24) | ((i + 1 < fileData.length ? fileData[i + 1] : 0) << 16)
                    | ((i + 2 < fileData.length ? fileData[i + 2] : 0) << 8) | (i + 3 < fileData.length ? fileData[i + 3] : 0);
            block[1] = (i + 4 < fileData.length ? fileData[i + 4] : 0) << 24
                    | ((i + 5 < fileData.length ? fileData[i + 5] : 0) << 16)
                    | ((i + 6 < fileData.length ? fileData[i + 6] : 0) << 8) | (i + 7 < fileData.length ? fileData[i + 7] : 0);

            int[] decryptedBlock = new int[2];
            decrypt(block, decryptedBlock);

            for (int j = 0; j < 4; j++) {
                outputStream.write((decryptedBlock[0] >> (24 - j * 8)) & 0xFF);
                outputStream.write((decryptedBlock[1] >> (24 - j * 8)) & 0xFF);
            }
        }

        Files.write(Paths.get(outputFilePath), outputStream.toByteArray());
    }

    public byte[] generateKey(String passwordPhrase) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return md.digest(passwordPhrase.getBytes("UTF-8"));
    }

    public void encrypt(int[] pt, int[] ct) {
        int A = pt[0] + S[0];
        int B = pt[1] + S[1];
        for (int i = 1; i <= r; i++) {
            A = leftRotate(A ^ B, B) + S[2 * i];
            B = leftRotate(B ^ A, A) + S[2 * i + 1];
        }
        ct[0] = A;
        ct[1] = B;
    }

    public void decrypt(int[] ct, int[] pt) {
        int B = ct[1];
        int A = ct[0];
        for (int i = r; i >= 1; i--) {
            B = rightRotate(B - S[2 * i + 1], A) ^ A;
            A = rightRotate(A - S[2 * i], B) ^ B;
        }
        pt[1] = B - S[1];
        pt[0] = A - S[0];
    }


    // Функція для лівого циклічного зсуву
    private int leftRotate(int x, int y) {
        return (x << y) | (x >>> (w - y));
    }

    // Функція для правого циклічного зсуву
    private int rightRotate(int x, int y) {
        return (x >>> y) | (x << (w - y));
    }
}
