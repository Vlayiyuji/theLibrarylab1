import api.FileStorage;
import service.DictionaryService;
import storage.JsonFileStorage;
import ui.ConsoleMenu;
import ui.UserInterface;
import validation.Digit5Validator;
import validation.Latin4Validator;

//Запуск приложения и сохранения файлов
public class Main {

    public static void main(String[] args) {

        //Структура бибилиотеки
        FileStorage fileStorage = new JsonFileStorage();
        UserInterface ui = new ConsoleMenu();

        // Валидаторы
        Latin4Validator latinValidator = new Latin4Validator();
        Digit5Validator digitValidator = new Digit5Validator();

        // Сервисы
        DictionaryService latinDict = new DictionaryService("LATIN4", latinValidator, fileStorage);
        DictionaryService digitDict = new DictionaryService("DIGIT5", digitValidator, fileStorage);

        // Запуск приложения
        ui.showWelcome();
        String sharedFilePath = ui.readFilePath();

        // Загрузка данных
        latinDict.load(sharedFilePath);
        digitDict.load(sharedFilePath);

        // Главный цикл
        boolean exit = false;
        while (!exit)
        {
            ui.showMainMenu();
            int choice = ui.readMenuChoice(0, 2);

            switch (choice) {
                case 1 -> viewDictionaries(ui, latinDict, digitDict);
                case 2 -> workOnDictionary(ui, latinDict, digitDict, sharedFilePath);
                case 0 -> exit = true;
                default -> ui.showMessage("Неверный пункт.");
            }
        }

        // Сохранение при выходе
        saveAllData(sharedFilePath, latinDict, digitDict, fileStorage);
        ui.showMessage("Данные сохранены в файл: " + sharedFilePath);
        ui.showMessage("Bye");
    }

    private static void viewDictionaries(UserInterface ui, DictionaryService latinDict, DictionaryService digitDict)
    {
        ui.showDictionaryList("Латинский словарь (ключ: 4 буквы)", latinDict.getAllOrdered());
        ui.showDictionaryList("Цифровой словарь (ключ: 5 цифр)", digitDict.getAllOrdered());
    }

    private static void workOnDictionary(UserInterface ui, DictionaryService latinDict, DictionaryService digitDict, String filePath) {
        boolean back = false;
        while (!back)
        {
            ui.showDictionarySelectionMenu();
            int d = ui.readDictionaryChoice();

            if (d == 0)
            {
                back = true;
                continue;
            }

            DictionaryService active = (d == 1) ? latinDict : digitDict;
            dictionarySubmenu(ui, active, filePath);
        }
    }

    private static void dictionarySubmenu(UserInterface ui, DictionaryService dict, String filePath) {
        boolean back = false;
        while (!back)
        {
            ui.showOperationsMenu(dict.getName(), filePath);
            int op = ui.readOperationChoice();

            switch (op)
            {
                case 1 ->
                {
                    String key = ui.readLine("Ключ: ");
                    String value = ui.readLine("Значение: ");
                    if (dict.addEntry(key, value))
                    {
                        ui.showMessage("Запись добавлена (будет сохранена при выходе).");
                    }
                }
                case 2 ->
                {
                    String key = ui.readLine("Ключ: ");
                    dict.findByKey(key).ifPresentOrElse(
                            v -> ui.showMessage("Значение: " + v),
                            () -> ui.showMessage("Запись не найдена.")
                    );
                }
                case 3 ->
                {
                    String key = ui.readLine("Ключ: ");
                    if (dict.deleteByKey(key))
                    {
                        ui.showMessage("Удалено (будет сохранено при выходе).");
                    }
                    else
                    {
                        ui.showMessage("Такого ключа нет или ключ не соответствует типу словаря.");
                    }
                }
                case 0 -> back = true;
                default -> ui.showMessage("Неверный пункт.");
            }
        }
    }

    private static void saveAllData(String filePath, DictionaryService latinDict, DictionaryService digitDict, FileStorage storage) {
        // Объединяем данные из обоих словарей для сохранения
        java.util.LinkedHashMap<String, String> combined = new java.util.LinkedHashMap<>();
        combined.putAll(latinDict.getAllOrdered());
        combined.putAll(digitDict.getAllOrdered());

        // Конвертируем в список KeyValue для сохранения
        var list = combined.entrySet().stream()
                .map(e -> new model.KeyValue(e.getKey(), e.getValue()))
                .toList();

        storage.save(filePath, list);
    }
}