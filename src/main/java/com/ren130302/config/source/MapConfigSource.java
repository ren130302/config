package com.ren130302.config.source;

import java.util.Map;
import java.util.Set;

public class MapConfigSource extends AbstractConfigSource<Map<String, Object>> {

  public MapConfigSource(Map<String, Object> map) {
    super(map, KeyPathResolvers.DOT);
  }

  @Override
  public Object getRaw(String key) {
    return this.source.get(key);
  }

  @Override
  public Object getRaw(String key, Object defaultValue) {
    return this.source.getOrDefault(key, defaultValue);
  }

  @Override
  public void setRaw(String key, Object value) {
    if (value == null) {
      this.source.remove(key);
    } else {
      this.source.put(key, value);
    }
  }

  @Override
  public Set<String> keys() {
    return this.source.keySet();
  }

}
