package config.value;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import config.source.ConfigSource;

abstract class AbstractConfigValue<T> implements ConfigValue<T> {

  protected final ConfigSource<?> source;
  protected final String key;
  protected final AtomicReference<T> cachedValue = new AtomicReference<>();
  protected final Class<T> type;

  protected AbstractConfigValue(ConfigSource<?> source, String key, Class<T> type) {
    this.source = Objects.requireNonNull(source, "source must not be null");
    this.key = Objects.requireNonNull(key, "key must not be null");
    this.type = Objects.requireNonNull(type, "type must not be null");
  }

  @Override
  public String key() {
    return this.key;
  }

  @Override
  public Class<T> type() {
    return this.type;
  }

  @Override
  public final T get() {
    return this.cachedValue.updateAndGet(current -> {
      if (current == null) {
        return this.transformRawValue();
      }
      return current;
    });
  }

  /**
   * 実装クラスで Raw -> T に変換する
   */
  protected abstract T transformRawValue();

  /**
   * 共通型変換ロジック
   */
  @SuppressWarnings("unchecked")
  protected final T transformRawValueWithConversion() {
    Object raw = this.getRaw();
    if (raw == null) {
      return null;
    }

    // List に変換
    if (List.class.isAssignableFrom(this.type)) {
      if (raw instanceof String str) {
        return (T) List.of(str.split(","));
      }
      if (raw instanceof List<?> list) {
        return (T) list;
      }
    }

    // Set に変換
    if (Set.class.isAssignableFrom(this.type)) {
      if (raw instanceof String str) {
        return (T) Set.of(str.split(","));
      }
      if (raw instanceof Set<?> set) {
        return (T) set;
      }
    }

    // Map に変換
    if (Map.class.isAssignableFrom(this.type)) {
      if (raw instanceof Map<?, ?> map) {
        return (T) map;
      }
    }

    // プリミティブ／ラッパー型
    if (this.type.isInstance(raw)) {
      return (T) raw;
    }
    if (raw instanceof String str) {
      if (this.type == Integer.class) {
        return (T) Integer.valueOf(str);
      }
      if (this.type == Long.class) {
        return (T) Long.valueOf(str);
      }
      if (this.type == Boolean.class) {
        return (T) Boolean.valueOf(str);
      }
      if (this.type == Double.class) {
        return (T) Double.valueOf(str);
      }
      if (this.type == Float.class) {
        return (T) Float.valueOf(str);
      }
      if (this.type == Short.class) {
        return (T) Short.valueOf(str);
      }
      if (this.type == Byte.class) {
        return (T) Byte.valueOf(str);
      }
      if (this.type == String.class) {
        return (T) str;
      }
    }

    throw new ClassCastException(
        "Cannot convert " + raw.getClass().getSimpleName() + " to " + this.type.getSimpleName());
  }

  protected final Object getRaw() {
    return this.source.get(this.key);
  }

  @Override
  public final void clear() {
    this.cachedValue.set(null);
  }

  @Override
  public String toString() {
    T value = this.cachedValue.get();
    return "ConfigValue[key=" + this.key + ",value=" + (value != null ? value : "<not loaded>")
        + "]";
  }
}
