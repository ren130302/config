package example;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ren130302.config.source.MapConfigSource;
import com.ren130302.config.source.json.JsonConfigSource;
import com.ren130302.config.source.properties.PropertiesConfigSource;

public final class MultiFormatConfig {

  private static final ObjectMapper mapper = new ObjectMapper();

  private MultiFormatConfig() {} // インスタンス化禁止

  /** JSON / Properties / Map をまとめて保存 */
  public static void saveAll(Map<String, Object> data, String jsonFile, String propertiesFile,
      String mapFile) throws IOException {

    // JSON
    ObjectNode jsonRoot = mapper.createObjectNode();
    JsonConfigSource jsonConfig = new JsonConfigSource(jsonRoot);
    data.forEach(jsonConfig::set);
    mapper.writerWithDefaultPrettyPrinter().writeValue(new java.io.File(jsonFile), jsonRoot);

    // Properties
    Properties props = new Properties();
    PropertiesConfigSource propsConfig = new PropertiesConfigSource(props);
    data.forEach(propsConfig::set);
    try (FileOutputStream out = new FileOutputStream(propertiesFile)) {
      props.store(out, "Generated Properties Config");
    }

    // Map
    Map<String, Object> mapRoot = new java.util.HashMap<>();
    MapConfigSource mapConfig = new MapConfigSource(mapRoot);
    data.forEach(mapConfig::set);
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(mapFile))) {
      oos.writeObject(mapRoot);
    }
  }

  /** mainでのテスト実行 */
  public static void main(String[] args) throws IOException {
    Map<String, Object> data = Map.of("server.port", 8080, "server.host", "localhost", "app.users",
        List.of("alice", "bob"), "database",
        Map.of("url", "jdbc:mysql://localhost", "user", "root"));

    saveAll(data, "config.json", "config.properties", "config.map");
    System.out.println("All config files created!");
  }
}
