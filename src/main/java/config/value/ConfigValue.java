package config.value;

public interface ConfigValue<T> {

  String key();

  T get();

  void clear();

  @Override
  String toString();

}
