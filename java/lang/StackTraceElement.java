package java.lang;

import java.io.Serializable;
import java.util.Objects;

public final class StackTraceElement implements Serializable {
  private String declaringClass;
  
  private String methodName;
  
  private String fileName;
  
  private int lineNumber;
  
  private static final long serialVersionUID = 6992337162326171013L;
  
  public StackTraceElement(String paramString1, String paramString2, String paramString3, int paramInt) {
    this.declaringClass = (String)Objects.requireNonNull(paramString1, "Declaring class is null");
    this.methodName = (String)Objects.requireNonNull(paramString2, "Method name is null");
    this.fileName = paramString3;
    this.lineNumber = paramInt;
  }
  
  public String getFileName() { return this.fileName; }
  
  public int getLineNumber() { return this.lineNumber; }
  
  public String getClassName() { return this.declaringClass; }
  
  public String getMethodName() { return this.methodName; }
  
  public boolean isNativeMethod() { return (this.lineNumber == -2); }
  
  public String toString() { return getClassName() + "." + this.methodName + (isNativeMethod() ? "(Native Method)" : ((this.fileName != null && this.lineNumber >= 0) ? ("(" + this.fileName + ":" + this.lineNumber + ")") : ((this.fileName != null) ? ("(" + this.fileName + ")") : "(Unknown Source)"))); }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof StackTraceElement))
      return false; 
    StackTraceElement stackTraceElement = (StackTraceElement)paramObject;
    return (stackTraceElement.declaringClass.equals(this.declaringClass) && stackTraceElement.lineNumber == this.lineNumber && Objects.equals(this.methodName, stackTraceElement.methodName) && Objects.equals(this.fileName, stackTraceElement.fileName));
  }
  
  public int hashCode() {
    null = 31 * this.declaringClass.hashCode() + this.methodName.hashCode();
    null = 31 * null + Objects.hashCode(this.fileName);
    return 31 * null + this.lineNumber;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\StackTraceElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */