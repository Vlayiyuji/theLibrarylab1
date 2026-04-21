package ui;

import service.DictionaryService;

//Интерфейс пользователя
public interface UserInterface {

    void showWelcome();

    void showMainMenu();

    int readMenuChoice(int min, int max);

    void showDictionaryList(String title, java.util.LinkedHashMap<String, String> entries);

    void showDictionarySelectionMenu();

    int readDictionaryChoice();

    void showOperationsMenu(String dictName, String filePath);

    int readOperationChoice();

    String readLine(String prompt);

    void showMessage(String message);

    String readFilePath();
}
