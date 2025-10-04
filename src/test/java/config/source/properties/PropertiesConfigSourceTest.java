package config.source.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PropertiesConfigSourceTest {

  private Properties properties;
  private PropertiesConfigSource config;

  @BeforeEach
  void setUp() {
    this.properties = new Properties();
    this.config = new PropertiesConfigSource(this.properties);
  }

  // ---- 基本 get/set ----
  @Test
  void testSetAndGetObject() {
    this.config.set("objKey", 123);
    assertEquals("123", this.config.get("objKey"));
  }

  @Test
  void testSetNullValueThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> this.config.set("nullKey", null));
  }


  @Test
  void testSourceReturnsCopy() {
    this.config.setString("x", "y");
    Properties copy = this.config.source();
    assertEquals("y", copy.getProperty("x"));
    copy.setProperty("x", "z");
    assertEquals("y", this.config.getString("x"), "copyは独立しているべき");
  }

  // ---- String ----
  @Test
  void testString() {
    this.config.setString("s", "hello");
    assertEquals("hello", this.config.getString("s"));
  }

  // ---- Integer ----
  @Test
  void testInteger() {
    this.config.setInt("i", 42);
    assertEquals(42, this.config.getInt("i"));
  }

  @Test
  void testIntegerNullIfMissing() {
    assertNull(this.config.getInt("missing"));
  }

  // ---- Long ----
  @Test
  void testLong() {
    this.config.setLong("l", 123456789L);
    assertEquals(123456789L, this.config.getLong("l"));
  }

  // ---- Double ----
  @Test
  void testDouble() {
    this.config.setDouble("d", 3.14);
    assertEquals(3.14, this.config.getDouble("d"));
  }

  // ---- Float ----
  @Test
  void testFloat() {
    this.config.setFloat("f", 1.23f);
    assertEquals(1.23f, this.config.getFloat("f"));
  }

  // ---- Boolean ----
  @Test
  void testBooleanTrueFalse() {
    this.config.setBoolean("b1", true);
    assertTrue(this.config.getBoolean("b1"));
    this.config.setBoolean("b2", false);
    assertFalse(this.config.getBoolean("b2"));
  }

  @Test
  void testBooleanCaseInsensitive() {
    this.properties.setProperty("bool", "TrUe");
    assertTrue(this.config.getBoolean("bool"));
  }

  // ---- Short ----
  @Test
  void testShort() {
    this.config.setShort("sh", (short) 12);
    assertEquals((short) 12, this.config.getShort("sh"));
  }

  // ---- Byte ----
  @Test
  void testByte() {
    this.config.setByte("by", (byte) 7);
    assertEquals((byte) 7, this.config.getByte("by"));
  }

  // ---- Collection ----
  @Test
  void testCollection() {
    List<String> list = List.of("a", "b", "c");
    this.config.setCollection("col", list);

    Collection<String> result = this.config.getCollection("col");
    assertEquals(List.of("a", "b", "c"), new ArrayList<>(result));
  }

  @Test
  void testEmptyCollectionWhenMissing() {
    assertTrue(this.config.getCollection("missing").isEmpty());
  }

  // ---- List ----
  @Test
  void testList() {
    List<String> values = List.of("x", "y");
    this.config.setList("list", values);
    assertEquals(values, this.config.getList("list"));
  }

  // ---- Set ----
  @Test
  void testSet() {
    Set<String> values = Set.of("red", "green");
    this.config.setSet("set", values);

    Set<String> result = this.config.getSet("set");
    assertTrue(result.contains("red"));
    assertTrue(result.contains("green"));
  }

  // ---- Map ----
  @Test
  void testMap() {
    Map<String, String> map = Map.of("a", "1", "b", "2");
    this.config.setMap("map", map);

    Map<String, String> result = this.config.getMap("map");
    assertEquals("1", result.get("a"));
    assertEquals("2", result.get("b"));
  }

  @Test
  void testEmptyMapWhenMissing() {
    assertTrue(this.config.getMap("missing").isEmpty());
  }

  @Test
  void testMapIgnoresInvalidEntry() {
    this.properties.setProperty("badmap", "a=1,b");
    Map<String, String> result = this.config.getMap("badmap");
    assertEquals(Map.of("a", "1"), result);
  }

  // ---- 境界値 ----
  @Test
  void testNumericBoundaries() {
    this.config.setInt("intMax", Integer.MAX_VALUE);
    assertEquals(Integer.MAX_VALUE, this.config.getInt("intMax"));

    this.config.setLong("longMin", Long.MIN_VALUE);
    assertEquals(Long.MIN_VALUE, this.config.getLong("longMin"));
  }

  @Test
  void testBooleanParsing() {
    this.properties.setProperty("bool1", "false");
    this.properties.setProperty("bool2", "TRUE");
    this.properties.setProperty("bool3", "notBool");

    assertFalse(this.config.getBoolean("bool1"));
    assertTrue(this.config.getBoolean("bool2"));
    assertFalse(this.config.getBoolean("bool3"), "不正値は false に解釈される");
  }
}
