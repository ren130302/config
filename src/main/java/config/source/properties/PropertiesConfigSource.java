package config.source.properties;

import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import config.source.AbstractConfigSource;

public class PropertiesConfigSource extends AbstractConfigSource<Properties> {

  public PropertiesConfigSource(Properties properties) {
    super(properties, config.source.KeyPathResolvers.DOT);
  }

  @Override
  public Object getRaw(String key) {
    return this.source.getProperty(key);
  }

  @Override
  public Object getRaw(String key, Object defaultValue) {
    return this.source.getProperty(key, defaultValue != null ? defaultValue.toString() : null);
  }

  @Override
  public void setRaw(String key, Object value) {
    if (value == null) {
      this.source.remove(key);
    } else {
      this.source.setProperty(key, value.toString());
    }
  }

  @Override
  public Set<String> keys() {
    return this.source.stringPropertyNames().stream().collect(Collectors.toSet());
  }
}
