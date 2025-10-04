package config.value;

import config.source.ConfigSource;

final class ImmutableOptionalConfigValue<T> extends AbstractOptionalConfigValue<T>
    implements Immutable {

  ImmutableOptionalConfigValue(ConfigSource<?> source, String key) {
    super(source, key);
  }

}
