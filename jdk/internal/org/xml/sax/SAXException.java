package jdk.internal.org.xml.sax;

public class SAXException extends Exception {
  private Exception exception = null;
  
  static final long serialVersionUID = 583241635256073760L;
  
  public SAXException() {}
  
  public SAXException(String paramString) { super(paramString); }
  
  public SAXException(Exception paramException) {}
  
  public SAXException(String paramString, Exception paramException) { super(paramString); }
  
  public String getMessage() {
    String str = super.getMessage();
    return (str == null && this.exception != null) ? this.exception.getMessage() : str;
  }
  
  public Exception getException() { return this.exception; }
  
  public Throwable getCause() { return this.exception; }
  
  public String toString() { return (this.exception != null) ? (super.toString() + "\n" + this.exception.toString()) : super.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\xml\sax\SAXException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */