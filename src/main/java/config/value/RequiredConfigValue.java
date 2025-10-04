package config.value;

import config.source.ConfigSource;

public interface RequiredConfigValue<T> extends ConfigValue<T> {

  public static <T> RequiredConfigValue<T> immutable(ConfigSource<?> source, String key) {
    return new ImmutableRequiredConfigValue<>(source, key);
  }

  public static <T> RequiredConfigValue<T> mutable(ConfigSource<?> source, String key) {
    return new MutableRequiredConfigValue<>(source, key);
  }

}
