package com.ren130302.config.value;

public interface ConfigValue<T> {

  String key();

  T get();

  Class<T> type();

  void clear();

  @Override
  String toString();

}
