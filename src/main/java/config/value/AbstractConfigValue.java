package config.value;

import java.util.concurrent.atomic.AtomicReference;
import config.source.ConfigSource;

abstract class AbstractConfigValue<T> implements ConfigValue<T> {

  protected final ConfigSource<?> source;
  protected final String key;
  protected final AtomicReference<T> cachedValue = new AtomicReference<>();

  protected AbstractConfigValue(ConfigSource<?> source, String key) {
    this.source = source;
    this.key = key;
  }

  @Override
  public String key() {
    return this.key;
  }


  @Override
  public final T get() {
    return this.cachedValue.updateAndGet(current -> {
      if (current == null) {
        return this.transformRawValue();
      }
      return current;
    });
  }

  protected abstract T transformRawValue();

  protected final Object getRaw() {
    return this.source.get(this.key);
  }

  @Override
  public final void clear() {
    this.cachedValue.set(null);
  }

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();

    buffer.append("ConfigValue[");
    buffer.append("key=").append(this.key);

    buffer.append(",value=");
    T value = this.cachedValue.get();
    if (value == null) {
      buffer.append("<not loaded>");
    } else {
      buffer.append(value);
    }
    buffer.append("]");

    return buffer.toString();
  }
}
