package javax.xml.stream;

public class FactoryConfigurationError extends Error {
  private static final long serialVersionUID = -2994412584589975744L;
  
  Exception nested;
  
  public FactoryConfigurationError() {}
  
  public FactoryConfigurationError(Exception paramException) { this.nested = paramException; }
  
  public FactoryConfigurationError(Exception paramException, String paramString) {
    super(paramString);
    this.nested = paramException;
  }
  
  public FactoryConfigurationError(String paramString, Exception paramException) {
    super(paramString);
    this.nested = paramException;
  }
  
  public FactoryConfigurationError(String paramString) { super(paramString); }
  
  public Exception getException() { return this.nested; }
  
  public Throwable getCause() { return this.nested; }
  
  public String getMessage() {
    String str = super.getMessage();
    if (str != null)
      return str; 
    if (this.nested != null) {
      str = this.nested.getMessage();
      if (str == null)
        str = this.nested.getClass().toString(); 
    } 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\stream\FactoryConfigurationError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */