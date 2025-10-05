package config.source;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import config.source.json.JsonConfigSource;
import config.source.properties.PropertiesConfigSource;

@SuppressWarnings("unchecked")
public class ConfigFileReadTest {

  private static final String JSON_FILE = "config.json";
  private static final String PROPS_FILE = "config.properties";

  // ==============================
  // 共通アサートロジック
  // ==============================
  private void assertServerSection(ConfigSource<?> config, boolean isStringMode) {
    if (isStringMode) {
      assertEquals("localhost", config.get("server.host"));
      assertEquals("8080", config.get("server.port"));
      assertEquals("true", config.get("server.tls.enabled"));
    } else {
      assertEquals("localhost", config.get("server.host"));
      assertEquals(8080, ((Number) config.get("server.port")).intValue());
      assertEquals(true, config.get("server.tls.enabled"));
    }
    assertEquals("keystore.jks", config.get("server.tls.keyStore"));
    assertEquals("truststore.jks", config.get("server.tls.trustStore"));
  }

  private void assertAppSection(ConfigSource<?> config, boolean isStringMode) {
    if (isStringMode) {
      String usersCsv = (String) config.get("app.users");
      assertArrayEquals(new String[] {"alice", "bob", "carol"}, usersCsv.split(","));
      assertEquals("login", config.get("app.features.0.name"));
      assertEquals("true", config.get("app.features.0.enabled"));
      assertEquals("payments", config.get("app.features.1.name"));
      assertEquals("false", config.get("app.features.1.enabled"));
    } else {
      assertEquals(List.of("alice", "bob", "carol"), config.getList("app.users", String.class));

      List<?> rawFeatures = config.getList("app.features", Map.class);
      List<Map<String, Object>> features =
          rawFeatures.stream().map(o -> (Map<String, Object>) o).collect(Collectors.toList());

      assertEquals(2, features.size());
      assertEquals("login", features.get(0).get("name"));
      assertEquals(true, features.get(0).get("enabled"));
      assertEquals("payments", features.get(1).get("name"));
      assertEquals(false, features.get(1).get("enabled"));
    }
  }

  private void assertDatabaseSection(ConfigSource<?> config, boolean isStringMode) {
    if (isStringMode) {
      assertEquals("jdbc:mysql://localhost/db1", config.get("database.connections.0.url"));
      assertEquals("root", config.get("database.connections.0.user"));
      assertEquals("jdbc:mysql://localhost/db2", config.get("database.connections.1.url"));
      assertEquals("admin", config.get("database.connections.1.user"));
    } else {
      List<?> rawConns = config.getList("database.connections", Map.class);
      List<Map<String, Object>> conns =
          rawConns.stream().map(o -> (Map<String, Object>) o).collect(Collectors.toList());

      assertEquals(2, conns.size());
      assertEquals("jdbc:mysql://localhost/db1", conns.get(0).get("url"));
      assertEquals("root", conns.get(0).get("user"));
      assertEquals("jdbc:mysql://localhost/db2", conns.get(1).get("url"));
      assertEquals("admin", conns.get(1).get("user"));
    }
  }

  private void assertConfigValues(ConfigSource<?> config, boolean isStringMode) {
    this.assertServerSection(config, isStringMode);
    this.assertAppSection(config, isStringMode);
    this.assertDatabaseSection(config, isStringMode);
  }

  @Test
  public void testJsonRead() throws Exception {
    File file = new File(JSON_FILE);
    assertTrue(file.exists(), JSON_FILE + " ファイルが存在しません。");

    ObjectMapper mapper = new ObjectMapper();
    ObjectNode root = (ObjectNode) mapper.readTree(file);
    ConfigSource<?> config = new JsonConfigSource(root);

    this.assertConfigValues(config, false);
  }

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
