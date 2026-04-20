package api;

//Интерфейс валидации словарей
public interface DictionaryValidator {
    boolean isValid(String Key);

    //Сообщение с описанием правил валидации
    String getValidationRule();
}
