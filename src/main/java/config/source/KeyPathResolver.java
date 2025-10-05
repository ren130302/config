package config.source;

import java.util.List;

public interface KeyPathResolver {

  List<String> parse(String keyPath);

  String join(List<String> path);

  default String normalizeKey(String key) {
    var path = this.parse(key);
    if (path.isEmpty()) {
      throw new IllegalArgumentException("Invalid key path: " + key);
    }
    return this.join(path);
  }

}
