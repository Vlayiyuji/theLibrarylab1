package model;

import java.util.LinkedHashMap;
import  java.util.Map;
import java.util.Optional;

// Класс реализующий модель словаря, который хранит данные о нем и предоставляет базовые операции над ним.

public class Dictionary {
    private  final LinkedHashMap<String, String> data = new LinkedHashMap<>();

    public Optional<String> findByKey(String key) {
        return Optional.ofNullable(data.get(key));
    }

    public boolean containsKey(String key)
    {
        return data.containsKey(key);
    }

    public boolean add(String key, String value)
    {
        if (data.containsKey(key))
        {
            return false;
        }
        data.put(key,value);
        return true;
    }
    public boolean remove(String key)
    {
        return data.remove(key) != null;
    }

    public LinkedHashMap<String, String>  getAll()
    {
        return new LinkedHashMap<>(data);
    }

    public LinkedHashMap<String, String> getFiltered(java.util.function.Predicate<String> filter)
    {
        return data.entrySet().stream().filter(e->filter.test(e.getKey())).collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1,v2) -> v1, LinkedHashMap::new));
    }

    public int size()
    {
        return data.size();
    }
}
