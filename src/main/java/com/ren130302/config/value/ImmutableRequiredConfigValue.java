package com.ren130302.config.value;

import com.ren130302.config.source.ConfigSource;

final class ImmutableRequiredConfigValue<T> extends AbstractRequiredConfigValue<T>
    implements Immutable {

  ImmutableRequiredConfigValue(ConfigSource<?> source, String key, Class<T> type) {
    super(source, key, type);
  }

}
