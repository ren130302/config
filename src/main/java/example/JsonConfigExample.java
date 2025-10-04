package example;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import config.source.ConfigSource;
import config.source.json.JsonConfigSource;
import config.value.ConfigValue;
import config.value.RequiredConfigValue;

public class JsonConfigExample {
  public static void main(String[] args) throws Exception {
    ObjectNode root = new ObjectMapper().createObjectNode();
    ConfigSource<?> config = new JsonConfigSource(root);

    config.setInt("server.port", 8080);
    config.setString("server.host", "localhost");
    config.setList("app.users", List.of("alice", "bob"));
    config.setMap("database", Map.of("url", "jdbc:mysql://localhost", "user", "root"));


    ConfigValue<Integer> port = RequiredConfigValue.immutable(config, "server.port");
    System.out.println(port); // <not loaded>
    System.out.println(port.get()); // 8080

    System.out.println(config.getString("server.host"));
    System.out.println(config.getList("app.users"));
    System.out.println(config.getMap("database"));


    config.setInt("server.timeout.ms", 5000);
    System.out.println(config.getInt("server.timeout.ms"));

    config.remove("server.port");
    System.out.println(config.getInt("server.port"));
  }

}
