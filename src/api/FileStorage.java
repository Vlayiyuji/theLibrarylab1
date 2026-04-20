package api;

import java.util.List;
import model.KeyValue;

//Это интерфейс для операций с файловым хранилищем
public interface FileStorage {
    //загружается список пар ключ-значение
    List<KeyValue> load(String filePath);
    //Созраняется список пар ключ-значение в файл
    void save(String filePath, List<KeyValue> data);
}
