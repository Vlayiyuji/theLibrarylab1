package validation;

import api.DictionaryValidator;

//Валидатор для словаря LATIN4, критерий для ключа 4 латинские буквы

public class Latin4Validator implements DictionaryValidator{
    private static final String PATTERN = "[A-Za-z]{4}";

    @Override
    public boolean isValid(String key)
    {
        return key != null && key.matches(PATTERN);
    }

    @Override
    public String getValidationRule() {
        return "Ключ: ровно 4 латинские буквы [A-Za-z]";
    }
}
