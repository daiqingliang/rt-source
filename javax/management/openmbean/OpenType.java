package javax.management.openmbean;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;

public abstract class OpenType<T> extends Object implements Serializable {
  static final long serialVersionUID = -9195195325186646468L;
  
  public static final List<String> ALLOWED_CLASSNAMES_LIST = Collections.unmodifiableList(Arrays.asList(new String[] { 
          "java.lang.Void", "java.lang.Boolean", "java.lang.Character", "java.lang.Byte", "java.lang.Short", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.lang.String", 
          "java.math.BigDecimal", "java.math.BigInteger", "java.util.Date", "javax.management.ObjectName", CompositeData.class.getName(), TabularData.class.getName() }));
  
  @Deprecated
  public static final String[] ALLOWED_CLASSNAMES = (String[])ALLOWED_CLASSNAMES_LIST.toArray(new String[0]);
  
  private String className;
  
  private String description;
  
  private String typeName;
  
  private boolean isArray = false;
  
  private Descriptor descriptor;
  
  protected OpenType(String paramString1, String paramString2, String paramString3) throws OpenDataException {
    checkClassNameOverride();
    this.typeName = valid("typeName", paramString2);
    this.description = valid("description", paramString3);
    this.className = validClassName(paramString1);
    this.isArray = (this.className != null && this.className.startsWith("["));
  }
  
  OpenType(String paramString1, String paramString2, String paramString3, boolean paramBoolean) {
    this.className = valid("className", paramString1);
    this.typeName = valid("typeName", paramString2);
    this.description = valid("description", paramString3);
    this.isArray = paramBoolean;
  }
  
  private void checkClassNameOverride() throws SecurityException {
    if (getClass().getClassLoader() == null)
      return; 
    if (overridesGetClassName(getClass())) {
      GetPropertyAction getPropertyAction = new GetPropertyAction("jmx.extend.open.types");
      if (AccessController.doPrivileged(getPropertyAction) == null)
        throw new SecurityException("Cannot override getClassName() unless -Djmx.extend.open.types"); 
    } 
  }
  
  private static boolean overridesGetClassName(final Class<?> c) { return ((Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
          public Boolean run() {
            try {
              return Boolean.valueOf((c.getMethod("getClassName", new Class[false]).getDeclaringClass() != OpenType.class));
            } catch (Exception exception) {
              return Boolean.valueOf(true);
            } 
          }
        })).booleanValue(); }
  
  private static String validClassName(String paramString) throws OpenDataException {
    String str;
    paramString = valid("className", paramString);
    byte b;
    for (b = 0; paramString.startsWith("[", b); b++);
    boolean bool = false;
    if (b > 0) {
      if (paramString.startsWith("L", b) && paramString.endsWith(";")) {
        str = paramString.substring(b + 1, paramString.length() - 1);
      } else if (b == paramString.length() - 1) {
        str = paramString.substring(b, paramString.length());
        bool = true;
      } else {
        throw new OpenDataException("Argument className=\"" + paramString + "\" is not a valid class name");
      } 
    } else {
      str = paramString;
    } 
    boolean bool1 = false;
    if (bool) {
      bool1 = ArrayType.isPrimitiveContentType(str);
    } else {
      bool1 = ALLOWED_CLASSNAMES_LIST.contains(str);
    } 
    if (!bool1)
      throw new OpenDataException("Argument className=\"" + paramString + "\" is not one of the allowed Java class names for open data."); 
    return paramString;
  }
  
  private static String valid(String paramString1, String paramString2) {
    if (paramString2 == null || (paramString2 = paramString2.trim()).equals(""))
      throw new IllegalArgumentException("Argument " + paramString1 + " cannot be null or empty"); 
    return paramString2;
  }
  
  Descriptor getDescriptor() {
    if (this.descriptor == null)
      this.descriptor = new ImmutableDescriptor(new String[] { "openType" }, new Object[] { this }); 
    return this.descriptor;
  }
  
  public String getClassName() { return this.className; }
  
  String safeGetClassName() { return this.className; }
  
  public String getTypeName() { return this.typeName; }
  
  public String getDescription() { return this.description; }
  
  public boolean isArray() { return this.isArray; }
  
  public abstract boolean isValue(Object paramObject);
  
  boolean isAssignableFrom(OpenType<?> paramOpenType) { return equals(paramOpenType); }
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    String str3;
    String str2;
    String str1;
    checkClassNameOverride();
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    try {
      str1 = validClassName((String)getField.get("className", null));
      str2 = valid("description", (String)getField.get("description", null));
      str3 = valid("typeName", (String)getField.get("typeName", null));
    } catch (Exception exception) {
      InvalidObjectException invalidObjectException = new InvalidObjectException(exception.getMessage());
      invalidObjectException.initCause(exception);
      throw invalidObjectException;
    } 
    this.className = str1;
    this.description = str2;
    this.typeName = str3;
    this.isArray = this.className.startsWith("[");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\openmbean\OpenType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */