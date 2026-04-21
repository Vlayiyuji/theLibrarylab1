package ui;

import service.DictionaryService;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Scanner;

//Реализация методов из пользовательского интерфейса
public class ConsoleMenu implements UserInterface
{

    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void showWelcome()
    {
        System.out.println("Добро пожаловать в Библиотеку для работы со словарём");
    }

    @Override
    public void showMainMenu()
    {
        System.out.println("\n1 - Просмотр содержимого обоих словарей");
        System.out.println("2 - Работа со словарём");
        System.out.println("0 - Выход");
    }

    @Override
    public int readMenuChoice(int min, int max)
    {
        return readIntInRange("Выберите пункт: ", min, max);
    }

    @Override
    public void showDictionaryList(String title, java.util.LinkedHashMap<String, String> entries)
    {
        System.out.println(title);
        if (entries.isEmpty())
        {
            System.out.println("(пусто)");
            return;
        }
        for (var e : entries.entrySet())
        {
            System.out.println("  " + e.getKey() + " => " + e.getValue());
        }
    }

    @Override
    public void showDictionarySelectionMenu()
    {
        System.out.println("\nВыберите словарь:");
        System.out.println("1 - Латинский (LATIN4)");
        System.out.println("2 - Цифровой (DIGIT5)");
        System.out.println("0 - Назад");
    }

    @Override
    public int readDictionaryChoice()
    {
        return readIntInRange("Выбор: ", 0, 2);
    }

    @Override
    public void showOperationsMenu(String dictName, String filePath)
    {
        System.out.println("\nСловарь: " + dictName + " | файл: " + filePath);
        System.out.println("1 - Добавить запись");
        System.out.println("2 - Найти по ключу");
        System.out.println("3 - Удалить по ключу");
        System.out.println("0 - Назад");
    }

    @Override
    public int readOperationChoice()
    {
        return readIntInRange("Пункт: ", 0, 3);
    }

    @Override
    public String readLine(String prompt)
    {
        while (true)
        {
            System.out.print(prompt);
            String line = scanner.nextLine();
            if (line != null && !line.isEmpty())
            {
                return line;
            }
            System.out.println("Строка не должна быть пустой. Повторите ввод:");
        }
    }

    @Override
    public void showMessage(String message)
    {
        System.out.println(message);
    }

    @Override
    public String readFilePath()
    {
        while (true)
        {
            System.out.print("Введите путь к файлу словаря(только файлы json): ");
            String path = scanner.nextLine().trim();

            if (path.isEmpty())
            {
                System.out.println("Путь не может быть пустым. Повторите ввод.");
                continue;
            }

            try {
                Path filePath = Path.of(path);
                String fileName = filePath.getFileName().toString().toLowerCase();

                if (!fileName.endsWith(".json"))
                {
                    System.out.println("Файл должен иметь тип .json. Повторите ввод.");
                    continue;
                }

                if (!Files.exists(filePath))
                {
                    System.out.println("Файл не существует: " + path);
                    System.out.println("Повторите ввод с корректным путём.");
                    continue;
                }

                if (!Files.isRegularFile(filePath))
                {
                    System.out.println("Указанный путь не является файлом. Повторите ввод.");
                    continue;
                }

                if (!Files.isReadable(filePath))
                {
                    System.out.println("Нет прав на чтение файла: " + path);
                    continue;
                }

                return path;

            }
            catch (InvalidPathException e) {
                System.out.println("Некорректный формат пути. Повторите ввод.");
            }
        }
    }

    // Проверка корректности ввода на панели выбора типа взаимодействия
    private int readIntInRange(String prompt, int min, int max)
    {
        while (true)
        {
            System.out.print(prompt);
            String raw = scanner.nextLine();
            if (raw == null || raw.isBlank())
            {
                System.out.println("Введите непустое число.");
                continue;
            }
            try
            {
                int n = Integer.parseInt(raw.trim());
                if (n < min || n > max) {
                    System.out.println("Число должно быть от " + min + " до " + max);
                    continue;
                }
                return n;
            }
            catch (NumberFormatException e)
            {
                System.out.println("Ожидалось целое число");
            }
        }
    }
}