package com.sun.media.sound;

final class Printer {
  static final boolean err = false;
  
  static final boolean debug = false;
  
  static final boolean trace = false;
  
  static final boolean verbose = false;
  
  static final boolean release = false;
  
  static final boolean SHOW_THREADID = false;
  
  static final boolean SHOW_TIMESTAMP = false;
  
  private static long startTime = 0L;
  
  public static void err(String paramString) {}
  
  public static void debug(String paramString) {}
  
  public static void trace(String paramString) {}
  
  public static void verbose(String paramString) {}
  
  public static void release(String paramString) {}
  
  public static void println(String paramString) {
    String str = "";
    System.out.println(str + paramString);
  }
  
  public static void println() { System.out.println(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\Printer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */