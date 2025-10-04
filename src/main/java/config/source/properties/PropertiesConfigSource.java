package config.source.properties;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import config.source.ConfigSource;

public class PropertiesConfigSource implements ConfigSource<Properties> {

  private final Properties properties;

  public PropertiesConfigSource(Properties properties) {
    this.properties = Objects.requireNonNull(properties);
  }

  @Override
  public Properties source() {
    return new Properties(this.properties);
  }

  @Override
  public Object get(String key) {
    return this.properties.getProperty(key);
  }

  @Override
  public void set(String key, Object value) {
    if (value == null) {
      throw new IllegalArgumentException(
          "null is not supported in PropertiesConfigSource. Use remove(key) instead.");
    }

    if (value instanceof String v) {
      this.setString(key, v);
    } else {
      this.properties.setProperty(key, value.toString());
    }
  }

  @Override
  public String getString(String key) {
    return this.properties.getProperty(key);
  }

  @Override
  public void setString(String key, String value) {
    this.properties.setProperty(key, value);
  }

  @Override
  public Integer getInt(String key) {
    String v = this.properties.getProperty(key);
    return v != null ? Integer.valueOf(v) : null;
  }

  @Override
  public void setInt(String key, int value) {
    this.properties.setProperty(key, String.valueOf(value));
  }

  @Override
  public Long getLong(String key) {
    String v = this.properties.getProperty(key);
    return v != null ? Long.valueOf(v) : null;
  }

  @Override
  public void setLong(String key, long value) {
    this.properties.setProperty(key, String.valueOf(value));
  }

  @Override
  public Double getDouble(String key) {
    String v = this.properties.getProperty(key);
    return v != null ? Double.valueOf(v) : null;
  }

  @Override
  public void setDouble(String key, double value) {
    this.properties.setProperty(key, String.valueOf(value));
  }

  @Override
  public Float getFloat(String key) {
    String v = this.properties.getProperty(key);
    return v != null ? Float.valueOf(v) : null;
  }

  @Override
  public void setFloat(String key, float value) {
    this.properties.setProperty(key, String.valueOf(value));
  }

  @Override
  public Boolean getBoolean(String key) {
    String v = this.properties.getProperty(key);
    return v != null ? Boolean.valueOf(v) : null;
  }

  @Override
  public void setBoolean(String key, boolean value) {
    this.properties.setProperty(key, String.valueOf(value));
  }

  @Override
  public Short getShort(String key) {
    String v = this.properties.getProperty(key);
    return v != null ? Short.valueOf(v) : null;
  }

  @Override
  public void setShort(String key, short value) {
    this.properties.setProperty(key, String.valueOf(value));
  }

  @Override
  public Byte getByte(String key) {
    String v = this.properties.getProperty(key);
    return v != null ? Byte.valueOf(v) : null;
  }

  @Override
  public void setByte(String key, byte value) {
    this.properties.setProperty(key, String.valueOf(value));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <E> Collection<E> getCollection(String key) {
    String v = this.properties.getProperty(key);
    if (v == null) {
      return List.of();
    }
    return (Collection<E>) Arrays.asList(v.split(","));
  }

  @Override
  public <E> void setCollection(String key, Collection<E> value) {
    if (value == null) {
      return;
    }
    String joined = value.stream().map(Object::toString).collect(Collectors.joining(","));
    this.properties.setProperty(key, joined);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <E> List<E> getList(String key) {
    return (List<E>) new ArrayList<>(this.getCollection(key));
  }

  @Override
  public <E> void setList(String key, List<E> value) {
    this.setCollection(key, value);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <E> Set<E> getSet(String key) {
    return (Set<E>) new HashSet<>(this.getCollection(key));
  }

  @Override
  public <E> void setSet(String key, Set<E> value) {
    this.setCollection(key, value);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <K, V> Map<K, V> getMap(String key) {
    String v = this.properties.getProperty(key);
    if (v == null || v.isBlank()) {
      return Map.of();
    }
    return Arrays.stream(v.split(",")).map(s -> s.split("=", 2)).filter(arr -> arr.length == 2)
        .collect(Collectors.toMap(arr -> (K) arr[0].trim(), arr -> (V) arr[1].trim()));
  }

  @Override
  public <K, V> void setMap(String key, Map<K, V> value) {
    if (value == null) {
      return;
    }
    String joined = value.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
        .collect(Collectors.joining(","));
    this.properties.setProperty(key, joined);
  }

  @Override
  public void remove(String key) {
    this.properties.remove(key);
  }
}
