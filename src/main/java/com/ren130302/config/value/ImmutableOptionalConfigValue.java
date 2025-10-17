package com.ren130302.config.value;

import java.util.Optional;
import com.ren130302.config.source.ConfigSource;

final class ImmutableOptionalConfigValue<T> extends AbstractOptionalConfigValue<T>
    implements Immutable {

  ImmutableOptionalConfigValue(ConfigSource<?> source, String key, Class<Optional<T>> type) {
    super(source, key, type);
  }

}
