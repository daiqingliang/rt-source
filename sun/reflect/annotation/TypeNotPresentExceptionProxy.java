package sun.reflect.annotation;

public class TypeNotPresentExceptionProxy extends ExceptionProxy {
  private static final long serialVersionUID = 5565925172427947573L;
  
  String typeName;
  
  Throwable cause;
  
  public TypeNotPresentExceptionProxy(String paramString, Throwable paramThrowable) {
    this.typeName = paramString;
    this.cause = paramThrowable;
  }
  
  protected RuntimeException generateException() { return new TypeNotPresentException(this.typeName, this.cause); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\annotation\TypeNotPresentExceptionProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */