package sun.management;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;

public class StackTraceElementCompositeData extends LazyCompositeData {
  private final StackTraceElement ste;
  
  private static final CompositeType stackTraceElementCompositeType;
  
  private static final String CLASS_NAME = "className";
  
  private static final String METHOD_NAME = "methodName";
  
  private static final String FILE_NAME = "fileName";
  
  private static final String LINE_NUMBER = "lineNumber";
  
  private static final String NATIVE_METHOD = "nativeMethod";
  
  private static final String[] stackTraceElementItemNames;
  
  private static final long serialVersionUID = -2704607706598396827L;
  
  private StackTraceElementCompositeData(StackTraceElement paramStackTraceElement) { this.ste = paramStackTraceElement; }
  
  public StackTraceElement getStackTraceElement() { return this.ste; }
  
  public static StackTraceElement from(CompositeData paramCompositeData) {
    validateCompositeData(paramCompositeData);
    return new StackTraceElement(getString(paramCompositeData, "className"), getString(paramCompositeData, "methodName"), getString(paramCompositeData, "fileName"), getInt(paramCompositeData, "lineNumber"));
  }
  
  public static CompositeData toCompositeData(StackTraceElement paramStackTraceElement) {
    StackTraceElementCompositeData stackTraceElementCompositeData = new StackTraceElementCompositeData(paramStackTraceElement);
    return stackTraceElementCompositeData.getCompositeData();
  }
  
  protected CompositeData getCompositeData() {
    Object[] arrayOfObject = { this.ste.getClassName(), this.ste.getMethodName(), this.ste.getFileName(), new Integer(this.ste.getLineNumber()), new Boolean(this.ste.isNativeMethod()) };
    try {
      return new CompositeDataSupport(stackTraceElementCompositeType, stackTraceElementItemNames, arrayOfObject);
    } catch (OpenDataException openDataException) {
      throw new AssertionError(openDataException);
    } 
  }
  
  public static void validateCompositeData(CompositeData paramCompositeData) {
    if (paramCompositeData == null)
      throw new NullPointerException("Null CompositeData"); 
    if (!isTypeMatched(stackTraceElementCompositeType, paramCompositeData.getCompositeType()))
      throw new IllegalArgumentException("Unexpected composite type for StackTraceElement"); 
  }
  
  static  {
    try {
      stackTraceElementCompositeType = (CompositeType)MappedMXBeanType.toOpenType(StackTraceElement.class);
    } catch (OpenDataException openDataException) {
      throw new AssertionError(openDataException);
    } 
    stackTraceElementItemNames = new String[] { "className", "methodName", "fileName", "lineNumber", "nativeMethod" };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\StackTraceElementCompositeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */