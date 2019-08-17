package java.nio.charset;

public final class StandardCharsets {
  public static final Charset US_ASCII;
  
  public static final Charset ISO_8859_1;
  
  public static final Charset UTF_8;
  
  public static final Charset UTF_16BE;
  
  public static final Charset UTF_16LE;
  
  public static final Charset UTF_16 = (UTF_16LE = (UTF_16BE = (UTF_8 = (ISO_8859_1 = (US_ASCII = Charset.forName("US-ASCII")).forName("ISO-8859-1")).forName("UTF-8")).forName("UTF-16BE")).forName("UTF-16LE")).forName("UTF-16");
  
  private StandardCharsets() { throw new AssertionError("No java.nio.charset.StandardCharsets instances for you!"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\charset\StandardCharsets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */