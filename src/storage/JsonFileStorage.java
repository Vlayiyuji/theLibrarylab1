package storage;

import api.FileStorage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.KeyValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonFileStorage implements FileStorage {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public List<KeyValue> load(String filePath)
    {
        Path path = Path.of(filePath);
        if (!Files.exists(path))
        {
            return new ArrayList<>();
        }
        try
        {
            String json = Files.readString(path);
            if (json == null || json.isBlank())
            {
                return new ArrayList<>();
            }
            return MAPPER.readValue(json, new TypeReference<List<KeyValue>>() {});
        }
        catch (IOException e)
        {
            System.err.println("Ошибка чтения файла " + filePath + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void save(String filePath, List<KeyValue> data)
    {
        try
        {
            String json = MAPPER.writeValueAsString(data);
            Files.writeString(Path.of(filePath), json);
        } catch (IOException e) {
            System.err.println("Ошибка записи файла " + filePath + e.getMessage());
        }
    }
}
