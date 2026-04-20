import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис словаря: загрузка/сохранение в JSON и операции над данными.
 * Все записи хранятся в одном файле, фильтрация по типу ключа выполняется при доступе.
 */
public class DictionaryService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** Тип словаря: "LATIN4" или "DIGIT5". */
    private final String dictType;
    /** Все загруженные записи из файла (в порядке вставки) */
    private final LinkedHashMap<String, String> allData = new LinkedHashMap<>();

    public DictionaryService(String dictType) {
        this.dictType = dictType;
    }

    public static class KeyValue {
        public String key;
        public String value;

        public KeyValue() {}

        public KeyValue(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    /**
     * Загружает ВСЕ записи из файла, независимо от типа ключа.
     * Некорректные записи (с дубликатами ключей) обрабатываются: последняя побеждает.
     */
    public void load(String filePath) {
        Path path = Path.of(filePath);
        allData.clear();
        if (!Files.exists(path)) {
            return;
        }
        try {
            String json = Files.readString(path);
            if (json == null || json.isBlank()) {
                return;
            }
            List<KeyValue> list = MAPPER.readValue(json, new TypeReference<List<KeyValue>>() {});
            for (KeyValue kv : list) {
                if (kv != null && kv.key != null) {
                    allData.put(kv.key, kv.value != null ? kv.value : "");
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла \"" + filePath + "\": " + e.getMessage());
        }
    }

    /**
     * Сохраняет в файл ТОЛЬКО записи, валидные для текущего типа словаря.
     * Это предотвращает накопление "мусорных" записей в общем файле.
     */
    public void save(String filePath) {
        try {
            List<KeyValue> list = new ArrayList<>();
            for (Map.Entry<String, String> e : allData.entrySet()) {
                if (isValidKeyForCurrentDict(e.getKey())) {
                    list.add(new KeyValue(e.getKey(), e.getValue()));
                }
            }
            String json = MAPPER.writeValueAsString(list);
            Files.writeString(Path.of(filePath), json);
        } catch (IOException e) {
            System.out.println("Ошибка записи файла \"" + filePath + "\": " + e.getMessage());
        }
    }

    /**
     * Поиск с фильтрацией: возвращает значение только если ключ валиден для текущего словаря.
     */
    public Optional<String> findByKey(String key) {
        if (!isValidKeyForCurrentDict(key)) {
            return Optional.empty();
        }
        return Optional.ofNullable(allData.get(key));
    }

    /**
     * Удаление: удаляет запись только если ключ валиден для текущего словаря.
     * @return true, если запись была найдена и удалена
     */
    public boolean deleteByKey(String key) {
        if (!isValidKeyForCurrentDict(key)) {
            return false;
        }
        return allData.remove(key) != null;
    }

    /**
     * Добавление пары. Ключ должен соответствовать правилам текущего словаря.
     * @return true, если запись добавлена
     */
    public boolean addEntry(String key, String value) {
        if (!validateKey(key)) {
            return false;
        }
        if (allData.containsKey(key)) {
            System.out.println("Запись с таким ключом уже существует.");
            return false;
        }
        if (value == null) {
            System.out.println("Значение не может быть null.");
            return false;
        }
        allData.put(key, value);
        return true;
    }

    /**
     * Возвращает только записи, валидные для текущего типа словаря, в порядке вставки.
     */
    public LinkedHashMap<String, String> getAllOrdered() {
        return allData.entrySet().stream()
                .filter(e -> isValidKeyForCurrentDict(e.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
    }

    // Получить ВСЕ данные (без фильтрации)
    public LinkedHashMap<String, String> getAllData() {
        return new LinkedHashMap<>(allData);
    }

    // Сохранить конкретный набор данных в файл
    public static void saveAllData(String filePath, Map<String, String> data) {
        try {
            List<KeyValue> list = new ArrayList<>();
            for (Map.Entry<String, String> e : data.entrySet()) {
                list.add(new KeyValue(e.getKey(), e.getValue()));
            }
            String json = MAPPER.writeValueAsString(list);
            Files.writeString(Path.of(filePath), json);
        } catch (IOException e) {
            System.out.println("Ошибка записи файла: " + e.getMessage());
        }
    }

    public String getDictType() {
        return dictType;
    }


    /**
     * Проверка: соответствует ли ключ правилам текущего словаря.
     */
    private boolean isValidKeyForCurrentDict(String key) {
        if (key == null) return false;
        if ("LATIN4".equals(dictType)) {
            return key.matches("[A-Za-z]{4}");
        } else if ("DIGIT5".equals(dictType)) {
            return key.matches("[0-9]{5}");
        }
        return false;
    }

    /**
     * Валидация с выводом сообщений об ошибках (для addEntry).
     */
    private boolean validateKey(String key) {
        if (key == null) {
            System.out.println("Ключ не может быть пустым (null).");
            return false;
        }
        if ("LATIN4".equals(dictType)) {
            if (!key.matches("[A-Za-z]{4}")) {
                System.out.println("Ошибка ввода");
                System.out.println("Ключ типа LATIN4: ровно 4 латинские буквы [A-Za-z].");
                return false;
            }
        } else if ("DIGIT5".equals(dictType)) {
            if (!key.matches("[0-9]{5}")) {
                System.out.println("Ошибка ввода");
                System.out.println("Ключ типа DIGIT5: ровно 5 цифр [0-9].");
                return false;
            }
        } else {
            System.out.println("Неизвестный тип словаря: " + dictType);
            return false;
        }
        return true;
    }
}