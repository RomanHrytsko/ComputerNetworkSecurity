package Lab2;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class MD5Hashing {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Обчислення хешу для рядка
        System.out.print("Введіть рядок для обчислення хешу MD5: ");
        String inputString = scanner.nextLine();
        String stringHash = generateMD5(inputString);
        String fileHash = "no file found";
        System.out.println("Хеш рядка MD5: " + stringHash);

        // Обчислення хешу для файлу
        System.out.print("Введіть шлях до файлу для обчислення хешу MD5: ");
        String filePath = scanner.nextLine();
        File file = new File(filePath);

        if (file.exists()) {
            fileHash = generateMD5ForFile(file);
            System.out.println("Хеш файлу MD5: " + fileHash);
        } else {
            System.out.println("Файл не знайдено.");
        }

        System.out.print("Записати результат у файл?: ");
        Boolean saveToFile = scanner.nextBoolean();

        if(saveToFile) {
            saveResultToFile("src/Lab2/result.txt", "string hash: " + stringHash + "\nfile hash: " + fileHash );
        }

        System.out.print("Перевірити цілісніть файлу?: ");
        Boolean checkSum = scanner.nextBoolean();
        if(checkSum) {
            System.out.print("Введіть шлях до першого файлу ");
            String file1 = scanner.next();
            System.out.println("Очікуваний результат ");
            String expectedHash = scanner.next();
            boolean integrityCheck = checkFileIntegrity(file1, expectedHash);

            if (integrityCheck) {
                System.out.println("Файл цілий.");
            } else {
                System.out.println("Помилка: файл пошкоджений або хеш не співпадає.");
            }
        }
        scanner.close();
    }

    public static String generateMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : messageDigest) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String generateMD5ForFile(File file) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int bytesRead;
            InputStream is = new FileInputStream(file);

            while ((bytesRead = is.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }

            byte[] messageDigest = md.digest();
            StringBuilder hexString = new StringBuilder();

            for (byte b : messageDigest) {
                hexString.append(String.format("%02x", b));
            }

            is.close();
            return hexString.toString();
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveResultToFile(String fileName, String result) {
        try (FileOutputStream fos = new FileOutputStream(new File(fileName))) {
            byte[] resultBytes = result.getBytes(StandardCharsets.UTF_8);
            fos.write(resultBytes);
            System.out.println("Результат було успішно збережено в файлі: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Помилка при збереженні результату в файлі.");
        }
    }

    public static boolean checkFileIntegrity(String filePath, String expectedHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream fileInputStream = new FileInputStream(filePath);
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }

            byte[] fileDigest = md.digest();
            BigInteger fileNumber = new BigInteger(1, fileDigest);
            String fileHash = fileNumber.toString(16);

            while (fileHash.length() < 32) {
                fileHash = "0" + fileHash;
            }
            System.out.println(fileHash);
            return fileHash.equalsIgnoreCase(expectedHash);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
