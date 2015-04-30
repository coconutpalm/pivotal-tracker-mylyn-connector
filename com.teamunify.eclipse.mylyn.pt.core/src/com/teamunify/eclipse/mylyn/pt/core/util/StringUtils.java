package com.teamunify.eclipse.mylyn.pt.core.util;

public class StringUtils {
  public static boolean isEmpty(final String string) {
    return string == null || string.isEmpty();
  }

  public static boolean isNotEmpty(final String string) {
    return !isEmpty(string);
  }
}
