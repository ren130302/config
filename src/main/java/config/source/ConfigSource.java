package config.source;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ConfigSource<T> {

  // 管理系
  boolean containsKey(String key);

  void remove(String key);

  Set<String> keys();

  void putAll(Map<String, Object> map);

  Object getRaw(String key);

  Object getRaw(String key, Object defaultValue);

  void setRaw(String key, Object value);

  <V> V get(String key);

  <V> V get(String key, Class<V> type);

  <V> V get(String key, V defaultValue);

  void set(String key, Object value);

  <E> List<E> getList(String key, Class<E> elementType);

  <E> void setList(String key, List<E> value);

  <E> Set<E> getSet(String key, Class<E> elementType);

  <E> void setSet(String key, Set<E> value);

  <K, V> Map<K, V> getMap(String key, Class<K> keyType, Class<V> valueType);

  <K, V> void setMap(String key, Map<K, V> value);

  KeyPathResolver keyPathResolver();

  T source();
}
