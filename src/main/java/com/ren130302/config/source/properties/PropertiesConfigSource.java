package com.ren130302.config.source.properties;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import com.ren130302.config.source.AbstractConfigSource;

public class PropertiesConfigSource extends AbstractConfigSource<Properties> {

  public PropertiesConfigSource(Properties properties) {
    super(properties, com.ren130302.config.source.KeyPathResolvers.DOT);
  }

  @Override
  public Object getRaw(String key) {
    return this.source.getProperty(key);
  }

  @Override
  public Object getRaw(String key, Object defaultValue) {
    return this.source.getProperty(key, defaultValue != null ? defaultValue.toString() : null);
  }

  @Override
  public void setRaw(String key, Object value) {
    if (value == null) {
      this.source.remove(key);
    } else {
      this.source.setProperty(key, value.toString());
    }
  }

  @Override
  public Set<String> keys() {
    return this.source.stringPropertyNames().stream().collect(Collectors.toSet());
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E> List<E> getList(String key, Class<E> elementType) {
    Object raw = this.getRaw(this.keyPathResolver.normalizeKey(key));
    if (raw == null) {
      return List.of();
    }

    List<String> items;
    if (raw instanceof String str) {
      items = Arrays.stream(str.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    } else if (raw instanceof Collection<?> collection) {
      items = collection.stream().map(Object::toString).toList();
    } else {
      throw new ClassCastException(
          "Expected String or Collection but found " + raw.getClass().getSimpleName());
    }

    if (elementType == String.class) {
      return (List<E>) items;
    }
    return items.stream().map(s -> this.convertString(s, elementType)).toList();
  }


  @Override
  public <E> Set<E> getSet(String key, Class<E> elementType) {
    List<E> list = this.getList(key, elementType);
    return new LinkedHashSet<>(list);
  }

  /**
   * String を指定型に変換
   */
  @SuppressWarnings("unchecked")
  private <E> E convertString(String str, Class<E> type) {
    if (type == String.class) {
      return (E) str;
    }
    if (type == Integer.class) {
      return (E) Integer.valueOf(str);
    }
    if (type == Long.class) {
      return (E) Long.valueOf(str);
    }
    if (type == Boolean.class) {
      return (E) Boolean.valueOf(str);
    }
    if (type == Double.class) {
      return (E) Double.valueOf(str);
    }
    if (type == Float.class) {
      return (E) Float.valueOf(str);
    }
    if (type == Short.class) {
      return (E) Short.valueOf(str);
    }
    if (type == Byte.class) {
      return (E) Byte.valueOf(str);
    }
    throw new UnsupportedOperationException("Unsupported element type: " + type.getSimpleName());
  }
}
