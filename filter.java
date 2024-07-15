package com.example.util;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileFilterUtil {
    private static boolean appendMode = false;
    private static boolean shortStats = false;
    private static boolean fullStats = false;
    private static String outputPath = "";
    private static String prefix = "";

    private static final List<String> integerData = new ArrayList<>();
    private static final List<String> floatData = new ArrayList<>();
    private static final List<String> stringData = new ArrayList<>();

    public static void main(String[] args) {
        List<String> inputFiles = parseArguments(args);

        for (String fileName : inputFiles) {
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(fileName))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    classifyData(line);
                }
            } catch (IOException e) {
                System.err.println("Error reading file " + fileName + ": " + e.getMessage());
            }
        }

        writeOutputFiles();
        printStatistics();
    }

    private static List<String> parseArguments(String[] args) {
        List<String> inputFiles = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-o":
                    outputPath = args[++i];
                    break;
                case "-p":
                    prefix = args[++i];
                    break;
                case "-a":
                    appendMode = true;
                    break;
                case "-s":
                    shortStats = true;
                    break;
                case "-f":
                    fullStats = true;
                    break;
                default:
                    inputFiles.add(args[i]);
                    break;
            }
        }
        return inputFiles;
    }

    private static void classifyData(String data) {
        try {
            if (data.contains(".")) {
                Float.parseFloat(data);
                floatData.add(data);
            } else {
                Integer.parseInt(data);
                integerData.add(data);
            }
        } catch (NumberFormatException e) {
            stringData.add(data);
        }
    }

    private static void writeOutputFiles() {
        writeFile("integers.txt", integerData);
        writeFile("floats.txt", floatData);
        writeFile("strings.txt", stringData);
    }

    private static void writeFile(String fileName, List<String> data) {
        if (data.isEmpty()) {
            return;
        }

        Path filePath = Paths.get(outputPath, prefix + fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(filePath,
                appendMode ? StandardOpenOption.APPEND : StandardOpenOption.CREATE)) {
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing file " + filePath + ": " + e.getMessage());
        }
    }

    private static void printStatistics() {
        if (shortStats) {
            printShortStatistics();
        } else if (fullStats) {
            printFullStatistics();
        }
    }

    private static void printShortStatistics() {
        System.out.println("Short Statistics:");
        System.out.println("Integers: " + integerData.size());
        System.out.println("Floats: " + floatData.size());
        System.out.println("Strings: " + stringData.size());
    }

    private static void printFullStatistics() {
        System.out.println("Full Statistics:");
        printNumberStatistics("Integers", integerData);
        printNumberStatistics("Floats", floatData);
        printStringStatistics();
    }

    private static void printNumberStatistics(String label, List<String> data) {
        List<Double> numbers = data.stream().map(Double::parseDouble).collect(Collectors.toList());
        double min = Collections.min(numbers);
        double max = Collections.max(numbers);
        double sum = numbers.stream().mapToDouble(Double::doubleValue).sum();
        double avg = sum / numbers.size();

        System.out.println(label + ":");
        System.out.println("Count: " + numbers.size());
        System.out.println("Min: " + min);
        System.out.println("Max: " + max);
        System.out.println("Sum: " + sum);
        System.out.println("Avg: " + avg);
    }

    private static void printStringStatistics() {
        int minLength = stringData.stream().mapToInt(String::length).min().orElse(0);
        int maxLength = stringData.stream().mapToInt(String::length).max().orElse(0);

        System.out.println("Strings:");
        System.out.println("Count: " + stringData.size());
        System.out.println("Min length: " + minLength);
        System.out.println("Max length: " + maxLength);
    }
}
