package config.value;

import java.util.Optional;
import config.source.ConfigSource;

public interface OptionalConfigValue<T> extends ConfigValue<Optional<T>> {

  public static <T> OptionalConfigValue<T> immutable(ConfigSource<?> source, String key,
      Class<Optional<T>> type) {
    return new ImmutableOptionalConfigValue<>(source, key, type);
  }

  public static <T> OptionalConfigValue<T> mutable(ConfigSource<?> source, String key,
      Class<Optional<T>> type) {
    return new MutableOptionalConfigValue<>(source, key, type);
  }

}
