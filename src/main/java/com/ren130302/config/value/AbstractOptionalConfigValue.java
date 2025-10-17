package com.ren130302.config.value;

import java.util.Optional;
import com.ren130302.config.source.ConfigSource;

abstract class AbstractOptionalConfigValue<T> extends AbstractConfigValue<Optional<T>>
    implements OptionalConfigValue<T> {

  protected AbstractOptionalConfigValue(ConfigSource<?> source, String key,
      Class<Optional<T>> type) {
    super(source, key, type);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected final Optional<T> transformRawValue() {
    T value = (T) this.getRaw();
    Optional<T> wrapped = Optional.ofNullable(value);
    return wrapped;
  }

}
