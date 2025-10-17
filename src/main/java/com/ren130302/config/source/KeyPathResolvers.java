package com.ren130302.config.source;

import java.util.List;

public enum KeyPathResolvers implements KeyPathResolver {

  FLAT {
    @Override
    public List<String> parse(String keyPath) {
      return List.of(keyPath);
    }

    @Override
    public String join(List<String> path) {
      return path.get(0);
    }
  },

  DOT {
    @Override
    public List<String> parse(String keyPath) {
      return List.of(keyPath.split("\\."));
    }

    @Override
    public String join(List<String> path) {
      return String.join(".", path);
    }
  },

  ENV {
    @Override
    public List<String> parse(String keyPath) {
      return List.of(keyPath.toUpperCase().replace('.', '_'));
    }

    @Override
    public String join(List<String> path) {
      return path.get(0);
    }
  };

}
