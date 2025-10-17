package com.ren130302.config.value;

import com.ren130302.config.source.ConfigSource;

public interface RequiredConfigValue<T> extends ConfigValue<T> {

  public static <T> RequiredConfigValue<T> immutable(ConfigSource<?> source, String key,
      Class<T> type) {
    return new ImmutableRequiredConfigValue<>(source, key, type);
  }

  public static <T> RequiredConfigValue<T> mutable(ConfigSource<?> source, String key,
      Class<T> type) {
    return new MutableRequiredConfigValue<>(source, key, type);
  }

}
