package org.prevayler.implementation;

import java.lang.reflect.Field;

/**
 * Things that normal code shouldn't do, and maybe tests shouldn't either, but
 * that can be useful for some exceptional testing situations.
 */
public class Sneaky {

  /**
   * Get a field value using reflection.
   *
   * @param root The object to start from.
   * @param path The field name, or a "path" such as "a.b.c".
   */
  public static Object get(Object root, String path) throws Exception {
    int dot = path.indexOf('.');
    String first = dot == -1 ? path : path.substring(0, dot);
    String rest = dot == -1 ? null : path.substring(dot + 1);

    Field field = root.getClass().getDeclaredField(first);
    field.setAccessible(true);
    Object value = field.get(root);

    return rest == null ? value : get(value, rest);
  }

}
