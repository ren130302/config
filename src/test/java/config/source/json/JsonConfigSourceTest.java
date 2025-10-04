package config.source.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

class JsonConfigSourceTest {

  private JsonConfigSource config;
  private ObjectMapper mapper;

  @BeforeEach
  void setUp() {
    this.mapper = new ObjectMapper();
    ObjectNode root = this.mapper.createObjectNode();
    this.config = new JsonConfigSource(root);
  }

  @Test
  void testPrimitiveGetSet() {
    this.config.setInt("server.port", 8080);
    assertEquals(8080, this.config.getInt("server.port"));

    this.config.setLong("timeout.ms", 5000L);
    assertEquals(5000L, this.config.getLong("timeout.ms"));

    this.config.setDouble("pi", 3.14);
    assertEquals(3.14, this.config.getDouble("pi"));

    this.config.setFloat("ratio", 0.5f);
    assertEquals(0.5f, this.config.getFloat("ratio"));

    this.config.setBoolean("enabled", true);
    assertTrue(this.config.getBoolean("enabled"));

    this.config.setShort("shortVal", (short) 123);
    assertEquals((short) 123, this.config.getShort("shortVal"));

    this.config.setByte("byteVal", (byte) 0x7F);
    assertEquals((byte) 0x7F, this.config.getByte("byteVal"));

    this.config.setString("greeting", "hello");
    assertEquals("hello", this.config.getString("greeting"));
  }

  @Test
  void testCollectionListSet() {
    List<String> list = Arrays.asList("a", "b", "c");
    this.config.setList("letters.list", list);
    List<String> loadedList = this.config.getList("letters.list");
    assertEquals(list, loadedList);

    Set<String> set = new HashSet<>(list);
    this.config.setSet("letters.set", set);
    Set<String> loadedSet = this.config.getSet("letters.set");
    assertEquals(set, loadedSet);

    Collection<String> col = this.config.getCollection("letters.list");
    assertEquals(list, new ArrayList<>(col));
  }

  @Test
  void testMap() {
    Map<String, String> map = Map.of("key1", "val1", "key2", "val2");
    this.config.setMap("myMap", map);

    Map<String, String> loaded = this.config.getMap("myMap");
    assertEquals(map, loaded);
  }

  @Test
  void testNestedKeys() {
    this.config.setInt("server.port", 25565);
    assertEquals(25565, this.config.getInt("server.port"));

    this.config.setString("server.host", "localhost");
    assertEquals("localhost", this.config.getString("server.host"));
  }

  @Test
  void testRemove() {
    this.config.setString("remove.me", "value");
    assertEquals("value", this.config.getString("remove.me"));

    this.config.remove("remove.me");
    assertNull(this.config.getString("remove.me"));
  }

  @Test
  void testNullValueThrows() {
    assertThrows(IllegalArgumentException.class, () -> this.config.setString("foo", null));
  }

  @Test
  void testSourceReturnsCopy() {
    this.config.setInt("a", 1);
    ObjectNode copy = (ObjectNode) this.config.source();
    assertEquals("1", copy.get("a").asText());

    // 元のノードに影響しないことを確認
    copy.put("a", 999);
    assertEquals(1, this.config.getInt("a"));
  }
}
