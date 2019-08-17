package java.nio.charset;

public class CodingErrorAction {
  private String name;
  
  public static final CodingErrorAction IGNORE = new CodingErrorAction("IGNORE");
  
  public static final CodingErrorAction REPLACE = new CodingErrorAction("REPLACE");
  
  public static final CodingErrorAction REPORT = new CodingErrorAction("REPORT");
  
  private CodingErrorAction(String paramString) { this.name = paramString; }
  
  public String toString() { return this.name; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\charset\CodingErrorAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */