package java.util;

public class MissingResourceException extends RuntimeException {
  private static final long serialVersionUID = -4876345176062000401L;
  
  private String className;
  
  private String key;
  
  public MissingResourceException(String paramString1, String paramString2, String paramString3) {
    super(paramString1);
    this.className = paramString2;
    this.key = paramString3;
  }
  
  MissingResourceException(String paramString1, String paramString2, String paramString3, Throwable paramThrowable) {
    super(paramString1, paramThrowable);
    this.className = paramString2;
    this.key = paramString3;
  }
  
  public String getClassName() { return this.className; }
  
  public String getKey() { return this.key; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\MissingResourceException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */