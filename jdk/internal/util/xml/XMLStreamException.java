package jdk.internal.util.xml;

public class XMLStreamException extends Exception {
  private static final long serialVersionUID = 1L;
  
  protected Throwable nested;
  
  public XMLStreamException() {}
  
  public XMLStreamException(String paramString) { super(paramString); }
  
  public XMLStreamException(Throwable paramThrowable) {
    super(paramThrowable);
    this.nested = paramThrowable;
  }
  
  public XMLStreamException(String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
    this.nested = paramThrowable;
  }
  
  public Throwable getNestedException() { return this.nested; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\interna\\util\xml\XMLStreamException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */