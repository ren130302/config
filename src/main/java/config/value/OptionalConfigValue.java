package config.value;

import java.util.Optional;
import config.source.ConfigSource;

public interface OptionalConfigValue<T> extends ConfigValue<Optional<T>> {

  public static <T> OptionalConfigValue<T> immutable(ConfigSource<?> source, String key) {
    return new ImmutableOptionalConfigValue<>(source, key);
  }

  public static <T> OptionalConfigValue<T> mutable(ConfigSource<?> source, String key) {
    return new MutableOptionalConfigValue<>(source, key);
  }

}
