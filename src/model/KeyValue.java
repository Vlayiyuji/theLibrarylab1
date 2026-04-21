package model;

//Публичные поля, чтобы упростить работу с бибилиотекой Jackson
public class KeyValue {
    public String key;
    public String value;

    public KeyValue() {

    }

    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
