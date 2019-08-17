package sun.reflect.annotation;

public class EnumConstantNotPresentExceptionProxy extends ExceptionProxy {
  private static final long serialVersionUID = -604662101303187330L;
  
  Class<? extends Enum<?>> enumType;
  
  String constName;
  
  public EnumConstantNotPresentExceptionProxy(Class<? extends Enum<?>> paramClass, String paramString) {
    this.enumType = paramClass;
    this.constName = paramString;
  }
  
  protected RuntimeException generateException() { return new EnumConstantNotPresentException(this.enumType, this.constName); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\annotation\EnumConstantNotPresentExceptionProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */