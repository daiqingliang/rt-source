package java.lang;

public class EnumConstantNotPresentException extends RuntimeException {
  private static final long serialVersionUID = -6046998521960521108L;
  
  private Class<? extends Enum> enumType;
  
  private String constantName;
  
  public EnumConstantNotPresentException(Class<? extends Enum> paramClass, String paramString) {
    super(paramClass.getName() + "." + paramString);
    this.enumType = paramClass;
    this.constantName = paramString;
  }
  
  public Class<? extends Enum> enumType() { return this.enumType; }
  
  public String constantName() { return this.constantName; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\EnumConstantNotPresentException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */