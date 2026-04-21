package service;

import api.DictionaryValidator;
import api.FileStorage;
import model.Dictionary;
import model.KeyValue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


//Бизнес-логика работы со словарём.
public class DictionaryService
{

    private final String name;
    private final DictionaryValidator validator;
    private final FileStorage storage;
    private final Dictionary dictionary;

    public DictionaryService(String name, DictionaryValidator validator, FileStorage storage)
    {
        this.name = name;
        this.validator = validator;
        this.storage = storage;
        this.dictionary = new Dictionary();
    }

    public void load(String filePath)
    {
        List<KeyValue> data = storage.load(filePath);
        for (KeyValue kv : data)
        {
            if (kv != null && kv.key != null && validator.isValid(kv.key))
            {
                dictionary.add(kv.key, kv.value != null ? kv.value : "");
            }
        }
    }

    public void save(String filePath) {
        List<KeyValue> validEntries = dictionary.getAll().entrySet().stream()
                .filter(e -> validator.isValid(e.getKey()))
                .map(e -> new KeyValue(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        storage.save(filePath, validEntries);
    }

    public boolean addEntry(String key, String value)
    {
        if (!validator.isValid(key))
        {
            System.out.println("Ошибка валидации ключа: " + validator.getValidationRule());
            return false;
        }
        if (!dictionary.add(key, value))
        {
            System.out.println("Запись с таким ключом уже существует.");
            return false;
        }
        return true;
    }

    public Optional<String> findByKey(String key)
    {
        if (!validator.isValid(key))
        {
            return Optional.empty();
        }
        return dictionary.findByKey(key);
    }

    public boolean deleteByKey(String key)
    {
        if (!validator.isValid(key))
        {
            return false;
        }
        return dictionary.remove(key);
    }

    public LinkedHashMap<String, String> getAllOrdered()
    {
        return dictionary.getFiltered(validator::isValid);
    }

    public String getName()
    {
        return name;
    }

    public String getValidationRule()
    {
        return validator.getValidationRule();
    }
}
