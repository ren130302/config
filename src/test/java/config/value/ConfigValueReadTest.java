package config.value;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import config.source.ConfigSource;
import config.source.json.JsonConfigSource;
import config.source.properties.PropertiesConfigSource;

class ConfigValueReadTest {

  private static final String JSON_FILE = "config.json";
  private static final String PROPS_FILE = "config.properties";

  // ------------------------------
  // ジェネリクスキャスト用ヘルパー
  // ------------------------------
  @SuppressWarnings("unchecked")
  private <E> RequiredConfigValue<List<E>> list(ConfigSource<?> config, String key) {
    return (RequiredConfigValue<List<E>>) (RequiredConfigValue<?>) RequiredConfigValue
        .immutable(config, key, List.class);
  }

  @SuppressWarnings("unchecked")
  private <K, V> RequiredConfigValue<Map<K, V>> map(ConfigSource<?> config, String key) {
    return (RequiredConfigValue<Map<K, V>>) (RequiredConfigValue<?>) RequiredConfigValue
        .immutable(config, key, Map.class);
  }

  // ------------------------------
  // アサートロジック
  // ------------------------------
  private void assertServerSection(ConfigSource<?> config, boolean isStringMode) {
    RequiredConfigValue<String> host =
        RequiredConfigValue.immutable(config, "server.host", String.class);
    RequiredConfigValue<String> portStr =
        RequiredConfigValue.immutable(config, "server.port", String.class);
    RequiredConfigValue<String> tlsEnabledStr =
        RequiredConfigValue.immutable(config, "server.tls.enabled", String.class);
    RequiredConfigValue<String> keyStore =
        RequiredConfigValue.immutable(config, "server.tls.keyStore", String.class);
    RequiredConfigValue<String> trustStore =
        RequiredConfigValue.immutable(config, "server.tls.trustStore", String.class);

    assertEquals("localhost", host.get());

    if (isStringMode) {
      int port = Integer.parseInt(portStr.get());
      boolean tlsEnabled = Boolean.parseBoolean(tlsEnabledStr.get());
      assertEquals(8080, port);
      assertEquals(true, tlsEnabled);
    } else {
      RequiredConfigValue<Integer> port =
          RequiredConfigValue.immutable(config, "server.port", Integer.class);
      RequiredConfigValue<Boolean> tlsEnabled =
          RequiredConfigValue.immutable(config, "server.tls.enabled", Boolean.class);
      assertEquals(8080, port.get());
      assertEquals(true, tlsEnabled.get());
    }

    assertEquals("keystore.jks", keyStore.get());
    assertEquals("truststore.jks", trustStore.get());
  }

  private void assertAppSection(ConfigSource<?> config, boolean isStringMode) {
    RequiredConfigValue<List<String>> users = this.list(config, "app.users");
    RequiredConfigValue<List<Map<String, Object>>> features = this.list(config, "app.features");

    if (isStringMode) {
      String usersCsv = String.join(",", users.get());
      assertArrayEquals(new String[] {"alice", "bob", "carol"}, usersCsv.split(","));
    } else {
      assertEquals(List.of("alice", "bob", "carol"), users.get());
      assertEquals(2, features.get().size());
    }

    assertEquals("login", features.get().get(0).get("name"));
    assertEquals(true, features.get().get(0).get("enabled"));
    assertEquals("payments", features.get().get(1).get("name"));
    assertEquals(false, features.get().get(1).get("enabled"));
  }

  private void assertDatabaseSection(ConfigSource<?> config, boolean isStringMode) {
    RequiredConfigValue<List<Map<String, Object>>> connections =
        this.list(config, "database.connections");

    assertEquals(2, connections.get().size());
    assertEquals("jdbc:mysql://localhost/db1", connections.get().get(0).get("url"));
    assertEquals("root", connections.get().get(0).get("user"));
    assertEquals("jdbc:mysql://localhost/db2", connections.get().get(1).get("url"));
    assertEquals("admin", connections.get().get(1).get("user"));
  }

  private void assertConfigValues(ConfigSource<?> config, boolean isStringMode) {
    this.assertServerSection(config, isStringMode);
    this.assertAppSection(config, isStringMode);
    this.assertDatabaseSection(config, isStringMode);
  }

  // ------------------------------
  // JSON テスト
  // ------------------------------
  @Test
  public void testJsonRead() throws Exception {
    File file = new File(JSON_FILE);
    assertTrue(file.exists(), JSON_FILE + " ファイルが存在しません。");

    ObjectMapper mapper = new ObjectMapper();
    ObjectNode root = (ObjectNode) mapper.readTree(file);
    ConfigSource<?> config = new JsonConfigSource(root);

    this.assertConfigValues(config, false);
  }

  // ------------------------------
  // Properties テスト
  // ------------------------------
  @Test
  public void testPropertiesRead() throws Exception {
    File file = new File(PROPS_FILE);
    assertTrue(file.exists(), PROPS_FILE + " ファイルが存在しません。");

    Properties props = new Properties();
    try (FileInputStream in = new FileInputStream(file)) {
      props.load(in);
    }
    ConfigSource<?> config = new PropertiesConfigSource(props);

    this.assertConfigValues(config, true);
  }
}
