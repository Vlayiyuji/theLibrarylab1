import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Консольное меню: два словаря (латинский и цифровой), безопасный ввод.
 */
public class Main {

    private static final String FILE_LATIN = "dictionary_latin4.json";
    private static final String FILE_DIGIT = "dictionary_digit5.json";

    private static final Scanner SCANNER = new Scanner(System.in);

    private static DictionaryService latinDict;
    private static DictionaryService digitDict;

    public static void main(String[] args) {
        latinDict = new DictionaryService("LATIN4");
        digitDict = new DictionaryService("DIGIT5");
        latinDict.load(FILE_LATIN);
        digitDict.load(FILE_DIGIT);

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
        System.out.println("Bye");
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
            String file = (d == 1) ? FILE_LATIN : FILE_DIGIT;
            dictionarySubmenu(active, file);
        }
    }

    private static void dictionarySubmenu(DictionaryService dict, String filePath) {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("Словарь: " + dict.getDictType() + " | файл: " + filePath);
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
                        dict.save(filePath);
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
                        System.out.println("Удалено.");
                        dict.save(filePath);
                    } else {
                        System.out.println("Такого ключа нет.");
                    }
                }
                case 0 -> back = true;
                default -> System.out.println("Неверный пункт.");
            }
        }
    }

    /** Читает непустую строку (пустые строки игнорируются с напоминанием). */
    private static String readLine(String partOfDisc) {
        while (true) {
            System.out.print(partOfDisc);
            String line = SCANNER.nextLine();
            if (line != null && !line.isEmpty()) {
                return line;
            }
            System.out.println("Строка не должна быть пустой. Повторите ввод:");
        }
    }

    /**
     * Целое число в диапазоне [min, max]; пустой/неверный ввод — сообщение и повтор.
     */
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
