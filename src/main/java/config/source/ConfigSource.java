package config.source;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ConfigSource<T> {

  T source();

  Object get(String key);

  void set(String key, Object value);

  String getString(String key);

  void setString(String key, String value);

  Integer getInt(String key);

  void setInt(String key, int value);

  Long getLong(String key);

  void setLong(String key, long value);

  Double getDouble(String key);

  void setDouble(String key, double value);

  Float getFloat(String key);

  void setFloat(String key, float value);

  Boolean getBoolean(String key);

  void setBoolean(String key, boolean value);

  Short getShort(String key);

  void setShort(String key, short value);

  Byte getByte(String key);

  void setByte(String key, byte value);

  <E> Collection<E> getCollection(String key);

  <E> void setCollection(String key, Collection<E> value);

  <E> List<E> getList(String key);

  <E> void setList(String key, List<E> value);

  <E> Set<E> getSet(String key);

  <E> void setSet(String key, Set<E> value);

  <K, V> Map<K, V> getMap(String key);

  <K, V> void setMap(String key, Map<K, V> value);

  void remove(String key);
}
