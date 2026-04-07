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

/**
 * Сервис словаря: загрузка/сохранение в JSON и операции над данными.
 * Порядок записей задаётся LinkedHashMap (как в файле).
 */
public class DictionaryService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** Тип словаря: "LATIN4" или "DIGIT5". */
    private final String dictType;
    private final LinkedHashMap<String, String> data = new LinkedHashMap<>();

    public DictionaryService(String dictType) {
        this.dictType = dictType;
    }

    /**
     * Элемент JSON-массива: {"key":"...","value":"..."}.
     * Поля public — для Jackson без лишних геттеров.
     */
    public static class KeyValue {
        public String key;
        public String value;

        public KeyValue() {
        }

        public KeyValue(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public void load(String filePath) {
        Path path = Path.of(filePath);
        data.clear();
        if (!Files.exists(path)) {
            return;
        }
        try {
            String json = Files.readString(path);
            if (json == null || json.isBlank()) {
                return;
            }
            List<KeyValue> list = MAPPER.readValue(json, new TypeReference<List<KeyValue>>() {
            });
            for (KeyValue kv : list) {
                if (kv != null && kv.key != null) {
                    data.put(kv.key, kv.value != null ? kv.value : "");
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла \"" + filePath + "\": " + e.getMessage());
        }
    }

    public void save(String filePath) {
        try {
            List<KeyValue> list = new ArrayList<>();
            for (Map.Entry<String, String> e : data.entrySet()) {
                list.add(new KeyValue(e.getKey(), e.getValue()));
            }
            // Один массив объектов в компактном JSON (порядок как в list)
            String json = MAPPER.writeValueAsString(list);
            Files.writeString(Path.of(filePath), json);
        } catch (IOException e) {
            System.out.println("Ошибка записи файла \"" + filePath + "\": " + e.getMessage());
        }
    }

    public Optional<String> findByKey(String key) {
        return Optional.ofNullable(data.get(key));
    }

    public boolean deleteByKey(String key) {
        return data.remove(key) != null;
    }

    /**
     * Добавление пары ключ-значение. При неверном ключе — сообщение в консоль, без исключений.
     *
     * @return true, если запись добавлена
     */
    public boolean addEntry(String key, String value) {
        if (!validateKey(key)) {
            return false;
        }
        if (data.containsKey(key)) {
            System.out.println("Запись с таким ключом уже существует.");
            return false;
        }
        if (value == null) {
            System.out.println("Значение не может быть null.");
            return false;
        }
        data.put(key, value);
        return true;
    }

    /** Все пары в порядке вставки (для просмотра). */
    public LinkedHashMap<String, String> getAllOrdered() {
        return new LinkedHashMap<>(data);
    }

    public String getDictType() {
        return dictType;
    }

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
