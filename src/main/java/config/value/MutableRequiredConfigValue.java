package config.value;

import config.source.ConfigSource;

final class MutableRequiredConfigValue<T> extends AbstractRequiredConfigValue<T>
    implements Mutable<T> {

  MutableRequiredConfigValue(ConfigSource<?> source, String key, Class<T> type) {
    super(source, key, type);
  }

  @Override
  public void set(T newValue) {
    this.cachedValue.set(newValue);
  }
}
