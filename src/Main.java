import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.nio.file.*;

public class Main {

    private static final Scanner SCANNER = new Scanner(System.in);

    private static DictionaryService latinDict;
    private static DictionaryService digitDict;
    private static String sharedFilePath; // единый путь к файлу для обоих словарей

    public static void main(String[] args) {
        sharedFilePath = readFilePath();

        latinDict = new DictionaryService("LATIN4");
        digitDict = new DictionaryService("DIGIT5");
        latinDict.load(sharedFilePath);
        digitDict.load(sharedFilePath);

        boolean exit = false;
        while (!exit) {
            System.out.println();
            System.out.println("1 - Просмотр содержимого обоих словарей");
            System.out.println("2 - Работа со словарём");
            System.out.println("0 - Выход");
            int choice = readIntInRange("Выберите пункт: ", 0, 2);
            switch (choice) {
                case 1 -> viewDics();
                case 2 -> workOnDictionary();
                case 0 -> exit = true;
                default -> System.out.println("Неверный пункт.");
            }
        }

        // === СОХРАНЕНИЕ ПРИ ВЫХОДЕ (ОБЪЕДИНЁННОЕ) ===
        LinkedHashMap<String, String> combinedData = new LinkedHashMap<>();
        combinedData.putAll(latinDict.getAllData());
        combinedData.putAll(digitDict.getAllData());
        DictionaryService.saveAllData(sharedFilePath, combinedData);
        System.out.println("Данные сохранены в файл: " + sharedFilePath);

        System.out.println("Bye");
    }

    /**
     * Запрос пути к файлу. Минимальная валидация: непустая строка.
     * Файл может не существовать — он будет создан при первом сохранении.
     */
    private static String readFilePath() {
        while (true) {
            System.out.print("Введите путь к файлу словаря (JSON): ");
            String path = SCANNER.nextLine().trim();

            if (path.isEmpty()) {
                System.out.println("Путь не может быть пустым. Повторите ввод.");
                continue;
            }

            // Базовая проверка: путь не должен содержать недопустимых символов
            try {
                Path filePath = Path.of(path);

                // Проверка расширения
                String fileName = filePath.getFileName().toString().toLowerCase();
                if (!fileName.endsWith(".json")) {
                    System.out.println("Файл должен иметь расширение .json. Повторите ввод.");
                    continue;
                }

                // Проверка существования файла
                if (!Files.exists(filePath)) {
                    System.out.println("Файл не существует: " + path);
                    System.out.println("Повторите ввод с корректным путём.");
                    continue;
                }

                // Проверка, что это обычный файл, а не директория
                if (!Files.isRegularFile(filePath)) {
                    System.out.println("Указанный путь не является файлом. Повторите ввод.");
                    continue;
                }

                // Проверка прав на чтение
                if (!Files.isReadable(filePath)) {
                    System.out.println("Нет прав на чтение файла: " + path);
                    continue;
                }

                // Все проверки пройдены
                return path;

            } catch (java.nio.file.InvalidPathException e) {
                System.out.println("Некорректный формат пути. Повторите ввод.");
            }
        }
    }

    private static void viewDics() {
        System.out.println();
        printDictionaryBlock("Латинский словарь (ключ: 4 буквы)", latinDict);
        printDictionaryBlock("Цифровой словарь (ключ: 5 цифр)", digitDict);
    }

    private static void printDictionaryBlock(String title, DictionaryService service) {
        System.out.println(title);
        LinkedHashMap<String, String> map = service.getAllOrdered();
        if (map.isEmpty()) {
            System.out.println("(пусто)");
            return;
        }
        for (Map.Entry<String, String> e : map.entrySet()) {
            System.out.println("  " + e.getKey() + " => " + e.getValue());
        }
    }

    private static void workOnDictionary() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("Выберите словарь:");
            System.out.println("1 - Латинский (LATIN4)");
            System.out.println("2 - Цифровой (DIGIT5)");
            System.out.println("0 - Назад");
            int d = readIntInRange("Выбор: ", 0, 2);
            if (d == 0) {
                back = true;
                continue;
            }
            DictionaryService active = (d == 1) ? latinDict : digitDict;
            dictionarySubmenu(active);
        }
    }

    private static void dictionarySubmenu(DictionaryService dict) {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("Словарь: " + dict.getDictType() + " | файл: " + sharedFilePath);
            System.out.println("1 - Добавить запись");
            System.out.println("2 - Найти по ключу");
            System.out.println("3 - Удалить по ключу");
            System.out.println("0 - Назад");
            int op = readIntInRange("Пункт: ", 0, 3);
            switch (op) {
                case 1 -> {
                    String key = readLine("Ключ: ");
                    String value = readLine("Значение: ");
                    if (dict.addEntry(key, value)) {
                        System.out.println("Запись добавлена (будет сохранена при выходе).");
                    }
                }
                case 2 -> {
                    String key = readLine("Ключ: ");
                    dict.findByKey(key).ifPresentOrElse(
                            v -> System.out.println("Найдено: " + v),
                            () -> System.out.println("Запись не найдена.")
                    );
                }
                case 3 -> {
                    String key = readLine("Ключ: ");
                    if (dict.deleteByKey(key)) {
                        System.out.println("Удалено (будет сохранено при выходе).");
                    } else {
                        System.out.println("Такого ключа нет или ключ не соответствует типу словаря.");
                    }
                }
                case 0 -> back = true;
                default -> System.out.println("Неверный пункт.");
            }
        }
    }

    /** Читает непустую строку. */
    private static String readLine(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine();
            if (line != null && !line.isEmpty()) {
                return line;
            }
            System.out.println("Строка не должна быть пустой. Повторите ввод:");
        }
    }

    /** Целое число в диапазоне [min, max]. */
    private static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String raw = SCANNER.nextLine();
            if (raw == null || raw.isBlank()) {
                System.out.println("Введите непустое число.");
                continue;
            }
            try {
                int n = Integer.parseInt(raw.trim());
                if (n < min || n > max) {
                    System.out.println("Число должно быть от " + min + " до " + max);
                    continue;
                }
                return n;
            } catch (NumberFormatException e) {
                System.out.println("Ожидалось целое число");
            }
        }
    }
}