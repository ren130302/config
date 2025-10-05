package config.value;

import config.source.ConfigSource;

abstract class AbstractRequiredConfigValue<T> extends AbstractConfigValue<T>
    implements RequiredConfigValue<T> {

  protected AbstractRequiredConfigValue(ConfigSource<?> source, String key, Class<T> type) {
    super(source, key, type);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected final T transformRawValue() {
    T value = (T) this.getRaw();

    if (value == null) {
      throw new IllegalStateException("Required value is missing: " + this.key);
    }

    return value;
  }

}
