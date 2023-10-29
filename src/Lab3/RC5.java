package Lab3;

import java.util.Arrays;
import java.util.Random;

public class RC5 {
    private static final int w = 32;  // розмір блоку в бітах
    private static final int r = 12;  // кількість раундів
    private static final int b = 16;  // розмір ключа в байтах (128 біт)

    private static final int t = 2 * (r + 1);
    private int[] S = new int[t];

    public RC5(byte[] key) {
        // Ініціалізація S з допомогою деяких магічних констант
        int P = 0xb7e15163;
        int Q = 0x9e3779b9;

        S[0] = P;
        for (int i = 1; i < t; i++) {
            S[i] = S[i - 1] + Q;
        }

        int[] L = new int[b / 4];
        for (int i = 0; i < b; i++) {
            L[i / 4] = (L[i / 4] << 8) + (key[i] & 0xff);
        }

        int A, B, i, j;
        A = B = i = j = 0;

        int v = 3 * Math.max(t, b / 4);
        for (int k = 0; k < v; k++) {
            A = S[i] = rotateLeft(S[i] + A + B, 3);
            B = L[j] = rotateLeft(L[j] + A + B, A + B);
            i = (i + 1) % t;
            j = (j + 1) % (b / 4);
        }
    }

    private int rotateLeft(int value, int shift) {
        return (value << shift) | (value >>> (w - shift));
    }

    private int rotateRight(int value, int shift) {
        return (value >>> shift) | (value << (w - shift));
    }

    public byte[] encrypt(byte[] plaintext) {
        int A = bytesToInt(plaintext, 0);
        int B = bytesToInt(plaintext, 4);
        A += S[0];
        B += S[1];
        for (int i = 1; i <= r; i++) {
            A = rotateLeft(A ^ B, B) + S[2 * i];
            B = rotateLeft(B ^ A, A) + S[2 * i + 1];
        }
        byte[] result = new byte[8];
        intToBytes(A, result, 0);
        intToBytes(B, result, 4);
        return result;
    }

    public byte[] decrypt(byte[] ciphertext) {
        int A = bytesToInt(ciphertext, 0);
        int B = bytesToInt(ciphertext, 4);
        for (int i = r; i >= 1; i--) {
            B = rotateRight(B - S[2 * i + 1], A) ^ A;
            A = rotateRight(A - S[2 * i], B) ^ B;
        }
        B -= S[1];
        A -= S[0];
        byte[] result = new byte[8];
        intToBytes(A, result, 0);
        intToBytes(B, result, 4);
        return result;
    }

    private int bytesToInt(byte[] array, int offset) {
        return ((array[offset] & 0xff) << 24) |
                ((array[offset + 1] & 0xff) << 16) |
                ((array[offset + 2] & 0xff) << 8) |
                (array[offset + 3] & 0xff);
    }

    private void intToBytes(int value, byte[] array, int offset) {
        array[offset] = (byte)(value >>> 24);
        array[offset + 1] = (byte)(value >>> 16);
        array[offset + 2] = (byte)(value >>> 8);
        array[offset + 3] = (byte)value;
    }


    public byte[] encryptCBC(byte[] plaintext, byte[] iv) {
        int blocksize = 2 * w / 8;  // 8 bytes for two 32-bit blocks
        byte[] paddedText = pad(plaintext, blocksize);
        byte[] encrypted = new byte[paddedText.length];

        byte[] block = new byte[blocksize];
        byte[] previousBlock = Arrays.copyOf(iv, blocksize); // Ensure IV has the correct length

        for (int i = 0; i < paddedText.length; i += blocksize) {
            for (int j = 0; j < blocksize; j++) {
                block[j] = (byte) (paddedText[i + j] ^ previousBlock[j]);
            }

            byte[] encryptedBlock = encrypt(block);
            System.arraycopy(encryptedBlock, 0, encrypted, i, blocksize);
            previousBlock = encryptedBlock;
        }

        return encrypted;
    }

    public byte[] decryptCBC(byte[] ciphertext, byte[] iv) {
        int blocksize = 2 * w / 8;
        byte[] decrypted = new byte[ciphertext.length];

        byte[] block = new byte[blocksize];
        byte[] previousBlock = iv;

        for (int i = 0; i < ciphertext.length; i += blocksize) {
            byte[] decryptedBlock = decrypt(Arrays.copyOfRange(ciphertext, i, i + blocksize));

            for (int j = 0; j < blocksize; j++) {
                block[j] = (byte) (decryptedBlock[j] ^ previousBlock[j]);
            }

            System.arraycopy(block, 0, decrypted, i, blocksize);
            previousBlock = Arrays.copyOfRange(ciphertext, i, i + blocksize);
        }

        return unpad(decrypted);
    }

    private byte[] pad(byte[] data, int blocksize) {
        int paddingLength = blocksize - (data.length % blocksize);
        byte[] paddedData = new byte[data.length + paddingLength];
        System.arraycopy(data, 0, paddedData, 0, data.length);
        for (int i = 0; i < paddingLength; i++) {
            paddedData[data.length + i] = (byte) paddingLength;
        }
        return paddedData;
    }

    private byte[] unpad(byte[] data) {
        int paddingLength = data[data.length - 1] & 0xFF;
        return Arrays.copyOf(data, data.length - paddingLength);
    }
}

//public class RC5_CBC_Pad {
//    // ... [RC5 implementation goes here, without the main function] ...
//    private static final int w = 32;  // розмір блоку в бітах
//    private static final int r = 12;  // кількість раундів
//    private static final int b = 16;  // розмір ключа в байтах (128 біт)
//
//    public byte[] encryptCBC(byte[] plaintext, byte[] iv) {
//        int blocksize = w / 8;  // 4 bytes for 32-bit blocks
//        byte[] paddedText = pad(plaintext, blocksize);
//        byte[] encrypted = new byte[paddedText.length];
//
//        byte[] block = new byte[blocksize];
//        byte[] previousBlock = iv;
//
//        for (int i = 0; i < paddedText.length; i += blocksize) {
//            for (int j = 0; j < blocksize; j++) {
//                block[j] = (byte) (paddedText[i + j] ^ previousBlock[j]);
//            }
//
//            byte[] encryptedBlock = encrypt(block);
//            System.arraycopy(encryptedBlock, 0, encrypted, i, blocksize);
//            previousBlock = encryptedBlock;
//        }
//
//        return encrypted;
//    }
//
//    public byte[] decryptCBC(byte[] ciphertext, byte[] iv) {
//        int blocksize = w / 8;
//        byte[] decrypted = new byte[ciphertext.length];
//
//        byte[] block = new byte[blocksize];
//        byte[] previousBlock = iv;
//
//        for (int i = 0; i < ciphertext.length; i += blocksize) {
//            byte[] decryptedBlock = decrypt(Arrays.copyOfRange(ciphertext, i, i + blocksize));
//
//            for (int j = 0; j < blocksize; j++) {
//                block[j] = (byte) (decryptedBlock[j] ^ previousBlock[j]);
//            }
//
//            System.arraycopy(block, 0, decrypted, i, blocksize);
//            previousBlock = Arrays.copyOfRange(ciphertext, i, i + blocksize);
//        }
//
//        return unpad(decrypted);
//    }
//
//    private byte[] pad(byte[] data, int blocksize) {
//        int paddingLength = blocksize - (data.length % blocksize);
//        byte[] paddedData = new byte[data.length + paddingLength];
//        System.arraycopy(data, 0, paddedData, 0, data.length);
//        for (int i = 0; i < paddingLength; i++) {
//            paddedData[data.length + i] = (byte) paddingLength;
//        }
//        return paddedData;
//    }
//
//    private byte[] unpad(byte[] data) {
//        int paddingLength = data[data.length - 1] & 0xFF;
//        return Arrays.copyOf(data, data.length - paddingLength);
//    }
//
//    public byte[] encrypt(byte[] plaintext) {
//        int A = bytesToInt(plaintext, 0);
//        int B = bytesToInt(plaintext, 4);
//        A += S[0];
//        B += S[1];
//        for (int i = 1; i <= r; i++) {
//            A = rotateLeft(A ^ B, B) + S[2 * i];
//            B = rotateLeft(B ^ A, A) + S[2 * i + 1];
//        }
//        byte[] result = new byte[8];
//        intToBytes(A, result, 0);
//        intToBytes(B, result, 4);
//        return result;
//    }
//
//    public byte[] decrypt(byte[] ciphertext) {
//        int A = bytesToInt(ciphertext, 0);
//        int B = bytesToInt(ciphertext, 4);
//        for (int i = r; i >= 1; i--) {
//            B = rotateRight(B - S[2 * i + 1], A) ^ A;
//            A = rotateRight(A - S[2 * i], B) ^ B;
//        }
//        B -= S[1];
//        A -= S[0];
//        byte[] result = new byte[8];
//        intToBytes(A, result, 0);
//        intToBytes(B, result, 4);
//        return result;
//    }
//
//    private int bytesToInt(byte[] array, int offset) {
//        return ((array[offset] & 0xff) << 24) |
//                ((array[offset + 1] & 0xff) << 16) |
//                ((array[offset + 2] & 0xff) << 8) |
//                (array[offset + 3] & 0xff);
//    }
//
//    private void intToBytes(int value, byte[] array, int offset) {
//        array[offset] = (byte)(value >>> 24);
//        array[offset + 1] = (byte)(value >>> 16);
//        array[offset + 2] = (byte)(value >>> 8);
//        array[offset + 3] = (byte)value;
//    }
//}
