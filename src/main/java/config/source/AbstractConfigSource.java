package config.source;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 抽象クラスとしての ConfigSource 基底。 生の値を返すだけで型変換は ConfigSource の default メソッドに任せる。 List/Set/Map など
 * Collection 変換は安全に行う。
 */
public abstract class AbstractConfigSource<T> implements ConfigSource<T> {

  protected final T source;
  protected final KeyPathResolver keyPathResolver;

  protected AbstractConfigSource(T source, KeyPathResolver resolver) {
    this.source = source;
    this.keyPathResolver = resolver;
  }

  @Override
  public T source() {
    return this.source;
  }

  @Override
  public <V> V get(String key) {
    return (V) this.getRaw(this.keyPathResolver.normalizeKey(key));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <V> V get(String key, Class<V> type) {
    Object value = this.getRaw(this.keyPathResolver.normalizeKey(key));
    if (value == null) {
      return null;
    }
    if (type.isInstance(value)) {
      return (V) value;
    }

    if (type == String.class) {
      return (V) this.getString(key);
    }
    if (type == Integer.class) {
      return (V) Integer.valueOf(this.getInt(key));
    }
    if (type == Long.class) {
      return (V) Long.valueOf(this.getLong(key));
    }
    if (type == Boolean.class) {
      return (V) Boolean.valueOf(this.getBoolean(key));
    }
    if (type == Double.class) {
      return (V) Double.valueOf(this.getDouble(key));
    }
    if (type == Float.class) {
      return (V) Float.valueOf(this.getFloat(key));
    }
    if (type == Short.class) {
      return (V) Short.valueOf(this.getShort(key));
    }
    if (type == Byte.class) {
      return (V) Byte.valueOf(this.getByte(key));
    }

    throw new UnsupportedOperationException("Unsupported type: " + type.getSimpleName());
  }

  @Override
  @SuppressWarnings("unchecked")
  public <V> V get(String key, V defaultValue) {
    V value;
    if (defaultValue != null) {
      value = this.get(key, (Class<V>) defaultValue.getClass());
    } else {
      value = (V) this.get(key);
    }
    return value != null ? value : defaultValue;
  }

  @Override
  public KeyPathResolver keyPathResolver() {
    return this.keyPathResolver;
  }

  @Override
  public void set(String key, Object value) {
    this.setRaw(this.keyPathResolver.normalizeKey(key), value);
  }

  @Override
  public void remove(String key) {
    this.setRaw(this.keyPathResolver.normalizeKey(key), null);
  }

  @Override
  public void putAll(Map<String, Object> map) {
    if (map != null) {
      map.forEach(this::set);
    }
  }

  @Override
  public boolean containsKey(String key) {
    return this.keys().contains(this.keyPathResolver.normalizeKey(key));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E> List<E> getList(String key, Class<E> elementType) {
    Object raw = this.getRaw(this.keyPathResolver.normalizeKey(key));
    if (raw == null) {
      return List.of();
    }

    if (raw instanceof Collection<?> collection) {
      return collection.stream().filter(Objects::nonNull).map(elementType::cast)
          .collect(Collectors.toList());
    }

    throw new ClassCastException(
        "Expected List/Collection but found " + raw.getClass().getSimpleName());
  }

  @Override
  public <E> void setList(String key, List<E> value) {
    this.set(key, value);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E> Set<E> getSet(String key, Class<E> elementType) {
    Object raw = this.getRaw(this.keyPathResolver.normalizeKey(key));
    if (raw == null) {
      return Set.of();
    }

    if (raw instanceof Collection<?> collection) {
      return collection.stream().filter(Objects::nonNull).map(elementType::cast)
          .collect(Collectors.toSet());
    }

    throw new ClassCastException(
        "Expected Set/Collection but found " + raw.getClass().getSimpleName());
  }

  @Override
  public <E> void setSet(String key, Set<E> value) {
    this.set(key, value);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <K, V> Map<K, V> getMap(String key, Class<K> keyType, Class<V> valueType) {
    Object raw = this.getRaw(this.keyPathResolver.normalizeKey(key));
    if (raw == null) {
      return Map.of();
    }

    if (raw instanceof Map<?, ?> map) {
      return map.entrySet().stream().collect(
          Collectors.toMap(e -> keyType.cast(e.getKey()), e -> valueType.cast(e.getValue())));
    }

    throw new ClassCastException("Expected Map but found " + raw.getClass().getSimpleName());
  }

  @Override
  public <K, V> void setMap(String key, Map<K, V> value) {
    this.set(key, value);
  }

  @Override
  public abstract Object getRaw(String key);

  @Override
  public abstract Object getRaw(String key, Object defaultValue);

  @Override
  public abstract void setRaw(String key, Object value);

  @Override
  public abstract Set<String> keys();
}
