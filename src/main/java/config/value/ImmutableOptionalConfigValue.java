package config.value;

import java.util.Optional;
import config.source.ConfigSource;

final class ImmutableOptionalConfigValue<T> extends AbstractOptionalConfigValue<T>
    implements Immutable {

  ImmutableOptionalConfigValue(ConfigSource<?> source, String key, Class<Optional<T>> type) {
    super(source, key, type);
  }

}
