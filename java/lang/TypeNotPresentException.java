package java.lang;

public class TypeNotPresentException extends RuntimeException {
  private static final long serialVersionUID = -5101214195716534496L;
  
  private String typeName;
  
  public TypeNotPresentException(String paramString, Throwable paramThrowable) {
    super("Type " + paramString + " not present", paramThrowable);
    this.typeName = paramString;
  }
  
  public String typeName() { return this.typeName; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\TypeNotPresentException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */