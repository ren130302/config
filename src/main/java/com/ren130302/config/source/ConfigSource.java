package com.ren130302.config.source;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ConfigSource<T> {

  boolean containsKey(String key);

  Object getRaw(String key);

  Object getRaw(String key, Object defaultValue);

  void setRaw(String key, Object value);

  void remove(String key);

  Set<String> keys();

  void putAll(Map<String, Object> map);

  void set(String key, Object value);

  <E> List<E> getList(String key, Class<E> elementType);

  <E> void setList(String key, List<E> value);

  <E> Set<E> getSet(String key, Class<E> elementType);

  <E> void setSet(String key, Set<E> value);

  <K, V> Map<K, V> getMap(String key, Class<K> keyType, Class<V> valueType);

  <K, V> void setMap(String key, Map<K, V> value);

  KeyPathResolver keyPathResolver();

  T source();


  default String getString(String key) {
    Object raw = this.getRaw(key);
    return raw != null ? raw.toString() : null;
  }

  default String getString(String key, String defaultValue) {
    String value = this.getString(key);
    return value != null ? value : defaultValue;
  }

  default char getChar(String key) {
    String value = this.getString(key);
    return (value != null && !value.isEmpty()) ? value.charAt(0) : '\0';
  }

  default char getChar(String key, char defaultValue) {
    String value = this.getString(key);
    return (value != null && !value.isEmpty()) ? value.charAt(0) : defaultValue;
  }

  default boolean getBoolean(String key) {
    String value = this.getString(key);
    return value != null ? Boolean.parseBoolean(value) : false;
  }

  default boolean getBoolean(String key, boolean defaultValue) {
    String value = this.getString(key);
    return value != null ? Boolean.parseBoolean(value) : defaultValue;
  }

  default byte getByte(String key) {
    String value = this.getString(key);
    return value != null ? Byte.parseByte(value) : 0;
  }

  default byte getByte(String key, byte defaultValue) {
    String value = this.getString(key);
    return value != null ? Byte.parseByte(value) : defaultValue;
  }

  default short getShort(String key) {
    String value = this.getString(key);
    return value != null ? Short.parseShort(value) : 0;
  }

  default short getShort(String key, short defaultValue) {
    String value = this.getString(key);
    return value != null ? Short.parseShort(value) : defaultValue;
  }

  default int getInt(String key) {
    String value = this.getString(key);
    return value != null ? Integer.parseInt(value) : 0;
  }

  default int getInt(String key, int defaultValue) {
    String value = this.getString(key);
    return value != null ? Integer.parseInt(value) : defaultValue;
  }

  default long getLong(String key) {
    String value = this.getString(key);
    return value != null ? Long.parseLong(value) : 0L;
  }

  default long getLong(String key, long defaultValue) {
    String value = this.getString(key);
    return value != null ? Long.parseLong(value) : defaultValue;
  }

  default float getFloat(String key) {
    String value = this.getString(key);
    return value != null ? Float.parseFloat(value) : 0f;
  }

  default float getFloat(String key, float defaultValue) {
    String value = this.getString(key);
    return value != null ? Float.parseFloat(value) : defaultValue;
  }

  default double getDouble(String key) {
    String value = this.getString(key);
    return value != null ? Double.parseDouble(value) : 0d;
  }

  default double getDouble(String key, double defaultValue) {
    String value = this.getString(key);
    return value != null ? Double.parseDouble(value) : defaultValue;
  }

  <V> V get(String key);

  <V> V get(String key, Class<V> type);

  <V> V get(String key, V defaultValue);
}
