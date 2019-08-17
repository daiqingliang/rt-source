package java.lang;

public class ClassCircularityError extends LinkageError {
  private static final long serialVersionUID = 1054362542914539689L;
  
  public ClassCircularityError() {}
  
  public ClassCircularityError(String paramString) { super(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\ClassCircularityError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */