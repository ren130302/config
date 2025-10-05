package config.source;

import java.util.List;

public interface KeyPathResolver {

  List<String> parse(String keyPath);

  String join(List<String> path);

}
