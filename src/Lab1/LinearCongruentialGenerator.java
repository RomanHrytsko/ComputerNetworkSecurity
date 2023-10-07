package Lab1;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LinearCongruentialGenerator {
    private int m;
    private int a;
    private int c;
    private int seed;

    public LinearCongruentialGenerator(int m, int a, int c, int seed) {
        this.m = m;
        this.a = a;
        this.c = c;
        this.seed = seed;
    }

    public List<Integer> generateRandomNumbers(int n) {
        List<Integer> randomNumbers = new ArrayList<>();

//        обчислення відбувається за формулою  X n+1 = (aXn + с)mod(2 в 31 степені -1)

//        Xn - це поточне псевдовипадкове число, яке знаходиться на кроці n генерації.
//        Xn+1 - це наступне псевдовипадкове число, яке ми генеруємо на кроці n+1.
//        a - множник. Це ціле число, яке визначає, як буде перетворено поточне число Xn, щоб отримати наступне число.
//        mod(2 в 31 степені -1) - оператор модуля. Все число, яке отримується після множення на a, буде поділене на 2 в 31 степені - 1 (дуже велике просте число),
//        і результат буде залишок від ділення.

        int x = seed;
        for (int i = 0; i < n; i++) {
            x = (a * x + c) % m;
            randomNumbers.add(x);
        }
        return randomNumbers;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введіть кількість псевдовипадкових чисел для генерації: ");
        int n = scanner.nextInt();

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

        // Генерація псевдовипадкових чисел
        List<Integer> randomNumbers = lcg.generateRandomNumbers(n);

        // Вивід результатів на екран
        System.out.println("Псевдовипадкові числа:");
        for (int number : randomNumbers) {
            System.out.println(number);
        }

        // Збереження результатів у файл
        try (FileWriter writer = new FileWriter("random_numbers.txt")) {
            for (int number : randomNumbers) {
                writer.write(String.valueOf(number) + "\n");
            }
            System.out.println("Результати збережено у файл 'random_numbers.txt'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
