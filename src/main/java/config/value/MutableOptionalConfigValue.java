package config.value;

import java.util.Optional;
import config.source.ConfigSource;

final class MutableOptionalConfigValue<T> extends AbstractOptionalConfigValue<T>
    implements Mutable<T> {

  MutableOptionalConfigValue(ConfigSource<?> source, String key) {
    super(source, key);
  }

  @Override
  public void set(T newValue) {
    this.cachedValue.set(Optional.ofNullable(newValue));
  }
}
