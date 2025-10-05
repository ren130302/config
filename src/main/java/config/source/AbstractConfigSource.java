package config.source;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractConfigSource<T> implements ConfigSource<T> {

  protected final T source;
  protected final KeyPathResolver keyPathResolver;

  protected AbstractConfigSource(T source, KeyPathResolver resolver) {
    this.source = Objects.requireNonNull(source, "source must not be null");
    this.keyPathResolver = Objects.requireNonNull(resolver, "resolver must not be null");
  }

  @Override
  public T source() {
    return this.source;
  }

  @Override
  public KeyPathResolver keyPathResolver() {
    return this.keyPathResolver;
  }

  protected String normalizeKey(String key) {
    List<String> path = this.keyPathResolver.parse(key);
    if (path.isEmpty()) {
      throw new IllegalArgumentException("Invalid key path: " + key);
    }
    return this.keyPathResolver.join(path);
  }

  @SuppressWarnings("unchecked")
  protected <V> V getValue(String key, Class<V> type) {
    Object value = this.getRaw(this.normalizeKey(key));
    if (value == null) {
      return null;
    }
    if (!type.isInstance(value)) {
      throw new ClassCastException("Expected type " + type.getSimpleName() + " but found "
          + value.getClass().getSimpleName());
    }
    return (V) value;
  }

  @Override
  public void set(String key, Object value) {
    this.setRaw(this.normalizeKey(key), value);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <V> V get(String key) {
    return (V) this.getRaw(this.normalizeKey(key));
  }

  @Override
  public <V> V get(String key, Class<V> type) {
    Object raw = this.getRaw(this.normalizeKey(key));
    if (raw == null) {
      return null;
    }
    if (type.isInstance(raw)) {
      return (V) raw;
    }

    String str = raw.toString();
    if (type == String.class) {
      return (V) str;
    }
    if (type == Integer.class) {
      return (V) Integer.valueOf(str);
    }
    if (type == Long.class) {
      return (V) Long.valueOf(str);
    }
    if (type == Double.class) {
      return (V) Double.valueOf(str);
    }
    if (type == Float.class) {
      return (V) Float.valueOf(str);
    }
    if (type == Boolean.class) {
      return (V) Boolean.valueOf(str);
    }
    if (type == Byte.class) {
      return (V) Byte.valueOf(str);
    }
    if (type == Short.class) {
      return (V) Short.valueOf(str);
    }

    throw new UnsupportedOperationException("Unsupported type: " + type.getSimpleName());
  }

  @Override
  @SuppressWarnings("unchecked")
  public <V> V get(String key, V defaultValue) {
    V value =
        (V) this.get(key, defaultValue != null ? (Class<V>) defaultValue.getClass() : Object.class);
    return value != null ? value : defaultValue;
  }

  @Override
  public void remove(String key) {
    this.setRaw(this.normalizeKey(key), null);
  }

  @Override
  public void putAll(Map<String, Object> map) {
    if (map != null) {
      map.forEach(this::set);
    }
  }

  @Override
  public boolean containsKey(String key) {
    return this.keys().contains(this.normalizeKey(key));
  }

  @Override
  public <E> List<E> getList(String key, Class<E> elementType) {
    Object raw = this.getRaw(this.normalizeKey(key));
    if (raw == null) {
      return List.of();
    }
    if (!(raw instanceof Collection<?> collection)) {
      throw new ClassCastException("Expected List but found " + raw.getClass().getSimpleName());
    }
    return collection.stream().map(elementType::cast).collect(Collectors.toList());
  }

  @Override
  public <E> void setList(String key, List<E> value) {
    this.set(key, value);
  }

  @Override
  public <E> Set<E> getSet(String key, Class<E> elementType) {
    Object raw = this.getRaw(this.normalizeKey(key));
    if (raw == null) {
      return Set.of();
    }
    if (!(raw instanceof Collection<?> collection)) {
      throw new ClassCastException("Expected Set but found " + raw.getClass().getSimpleName());
    }
    return collection.stream().map(elementType::cast).collect(Collectors.toSet());
  }

  @Override
  public <E> void setSet(String key, Set<E> value) {
    this.set(key, value);
  }

  @Override
  public <K, V> Map<K, V> getMap(String key, Class<K> keyType, Class<V> valueType) {
    Object raw = this.getRaw(this.normalizeKey(key));
    if (raw == null) {
      return Map.of();
    }
    if (!(raw instanceof Map<?, ?> map)) {
      throw new ClassCastException("Expected Map but found " + raw.getClass().getSimpleName());
    }
    return map.entrySet().stream().collect(
        Collectors.toMap(e -> keyType.cast(e.getKey()), e -> valueType.cast(e.getValue())));
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
}
