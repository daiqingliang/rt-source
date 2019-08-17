package javax.annotation.processing;

public class Completions {
  public static Completion of(String paramString1, String paramString2) { return new SimpleCompletion(paramString1, paramString2); }
  
  public static Completion of(String paramString) { return new SimpleCompletion(paramString, ""); }
  
  private static class SimpleCompletion implements Completion {
    private String value;
    
    private String message;
    
    SimpleCompletion(String param1String1, String param1String2) {
      if (param1String1 == null || param1String2 == null)
        throw new NullPointerException("Null completion strings not accepted."); 
      this.value = param1String1;
      this.message = param1String2;
    }
    
    public String getValue() { return this.value; }
    
    public String getMessage() { return this.message; }
    
    public String toString() { return "[\"" + this.value + "\", \"" + this.message + "\"]"; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\annotation\processing\Completions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */