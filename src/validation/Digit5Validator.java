package validation;

import api.DictionaryValidator;
//
public class Digit5Validator implements DictionaryValidator{
    private static final String PATTERN = "[0-9]{5}";

    @Override
    public boolean isValid(String key)
    {
        return key != null && key.matches(PATTERN);
    }
    @Override
    public String getValidationRule()
    {
        return "Ключ: ровно 5 цифр [0-9]";
    }
}
