package javax.xml.transform;

public class TransformerFactoryConfigurationError extends Error {
  private static final long serialVersionUID = -6527718720676281516L;
  
  private Exception exception = null;
  
  public TransformerFactoryConfigurationError() {}
  
  public TransformerFactoryConfigurationError(String paramString) { super(paramString); }
  
  public TransformerFactoryConfigurationError(Exception paramException) { super(paramException.toString()); }
  
  public TransformerFactoryConfigurationError(Exception paramException, String paramString) { super(paramString); }
  
  public String getMessage() {
    String str = super.getMessage();
    return (str == null && this.exception != null) ? this.exception.getMessage() : str;
  }
  
  public Exception getException() { return this.exception; }
  
  public Throwable getCause() { return this.exception; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\transform\TransformerFactoryConfigurationError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */