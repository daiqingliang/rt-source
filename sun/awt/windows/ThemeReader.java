package sun.awt.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class ThemeReader {
  private static final Map<String, Long> widgetToTheme = new HashMap();
  
  private static final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
  
  private static final Lock readLock = readWriteLock.readLock();
  
  private static final Lock writeLock = readWriteLock.writeLock();
  
  static void flush() { valid = false; }
  
  private static native boolean initThemes();
  
  public static boolean isThemed() {
    writeLock.lock();
    try {
      isThemed = initThemes();
      return isThemed;
    } finally {
      writeLock.unlock();
    } 
  }
  
  public static boolean isXPStyleEnabled() { return xpStyleEnabled; }
  
  private static Long getThemeImpl(String paramString) {
    Long long = (Long)widgetToTheme.get(paramString);
    if (long == null) {
      int i = paramString.indexOf("::");
      if (i > 0) {
        setWindowTheme(paramString.substring(0, i));
        long = Long.valueOf(openTheme(paramString.substring(i + 2)));
        setWindowTheme(null);
      } else {
        long = Long.valueOf(openTheme(paramString));
      } 
      widgetToTheme.put(paramString, long);
    } 
    return long;
  }
  
  private static Long getTheme(String paramString) {
    if (!isThemed)
      throw new IllegalStateException("Themes are not loaded"); 
    if (!valid) {
      readLock.unlock();
      writeLock.lock();
      try {
        if (!valid) {
          for (Long long1 : widgetToTheme.values())
            closeTheme(long1.longValue()); 
          widgetToTheme.clear();
          valid = true;
        } 
      } finally {
        readLock.lock();
        writeLock.unlock();
      } 
    } 
    Long long = (Long)widgetToTheme.get(paramString);
    if (long == null) {
      readLock.unlock();
      writeLock.lock();
      try {
        long = getThemeImpl(paramString);
      } finally {
        readLock.lock();
        writeLock.unlock();
      } 
    } 
    return long;
  }
  
  private static native void paintBackground(int[] paramArrayOfInt, long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);
  
  public static void paintBackground(int[] paramArrayOfInt, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
    readLock.lock();
    try {
      paintBackground(paramArrayOfInt, getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
    } finally {
      readLock.unlock();
    } 
  }
  
  private static native Insets getThemeMargins(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  public static Insets getThemeMargins(String paramString, int paramInt1, int paramInt2, int paramInt3) {
    readLock.lock();
    try {
      return getThemeMargins(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3);
    } finally {
      readLock.unlock();
    } 
  }
  
  private static native boolean isThemePartDefined(long paramLong, int paramInt1, int paramInt2);
  
  public static boolean isThemePartDefined(String paramString, int paramInt1, int paramInt2) {
    readLock.lock();
    try {
      return isThemePartDefined(getTheme(paramString).longValue(), paramInt1, paramInt2);
    } finally {
      readLock.unlock();
    } 
  }
  
  private static native Color getColor(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  public static Color getColor(String paramString, int paramInt1, int paramInt2, int paramInt3) {
    readLock.lock();
    try {
      return getColor(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3);
    } finally {
      readLock.unlock();
    } 
  }
  
  private static native int getInt(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  public static int getInt(String paramString, int paramInt1, int paramInt2, int paramInt3) {
    readLock.lock();
    try {
      return getInt(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3);
    } finally {
      readLock.unlock();
    } 
  }
  
  private static native int getEnum(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  public static int getEnum(String paramString, int paramInt1, int paramInt2, int paramInt3) {
    readLock.lock();
    try {
      return getEnum(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3);
    } finally {
      readLock.unlock();
    } 
  }
  
  private static native boolean getBoolean(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  public static boolean getBoolean(String paramString, int paramInt1, int paramInt2, int paramInt3) {
    readLock.lock();
    try {
      return getBoolean(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3);
    } finally {
      readLock.unlock();
    } 
  }
  
  private static native boolean getSysBoolean(long paramLong, int paramInt);
  
  public static boolean getSysBoolean(String paramString, int paramInt) {
    readLock.lock();
    try {
      return getSysBoolean(getTheme(paramString).longValue(), paramInt);
    } finally {
      readLock.unlock();
    } 
  }
  
  private static native Point getPoint(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  public static Point getPoint(String paramString, int paramInt1, int paramInt2, int paramInt3) {
    readLock.lock();
    try {
      return getPoint(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3);
    } finally {
      readLock.unlock();
    } 
  }
  
  private static native Dimension getPosition(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  public static Dimension getPosition(String paramString, int paramInt1, int paramInt2, int paramInt3) {
    readLock.lock();
    try {
      return getPosition(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3);
    } finally {
      readLock.unlock();
    } 
  }
  
  private static native Dimension getPartSize(long paramLong, int paramInt1, int paramInt2);
  
  public static Dimension getPartSize(String paramString, int paramInt1, int paramInt2) {
    readLock.lock();
    try {
      return getPartSize(getTheme(paramString).longValue(), paramInt1, paramInt2);
    } finally {
      readLock.unlock();
    } 
  }
  
  private static native long openTheme(String paramString);
  
  private static native void closeTheme(long paramLong);
  
  private static native void setWindowTheme(String paramString);
  
  private static native long getThemeTransitionDuration(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public static long getThemeTransitionDuration(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    readLock.lock();
    try {
      return getThemeTransitionDuration(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3, paramInt4);
    } finally {
      readLock.unlock();
    } 
  }
  
  public static native boolean isGetThemeTransitionDurationDefined();
  
  private static native Insets getThemeBackgroundContentMargins(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public static Insets getThemeBackgroundContentMargins(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    readLock.lock();
    try {
      return getThemeBackgroundContentMargins(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3, paramInt4);
    } finally {
      readLock.unlock();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\ThemeReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */