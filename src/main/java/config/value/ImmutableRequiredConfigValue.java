package config.value;

import config.source.ConfigSource;

final class ImmutableRequiredConfigValue<T> extends AbstractRequiredConfigValue<T>
    implements Immutable {

  ImmutableRequiredConfigValue(ConfigSource<?> source, String key) {
    super(source, key);
  }

}
