package java.io;

import java.lang.reflect.Field;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;

public class ObjectStreamField extends Object implements Comparable<Object> {
  private final String name;
  
  private final String signature;
  
  private final Class<?> type;
  
  private final boolean unshared;
  
  private final Field field;
  
  private int offset = 0;
  
  public ObjectStreamField(String paramString, Class<?> paramClass) { this(paramString, paramClass, false); }
  
  public ObjectStreamField(String paramString, Class<?> paramClass, boolean paramBoolean) {
    if (paramString == null)
      throw new NullPointerException(); 
    this.name = paramString;
    this.type = paramClass;
    this.unshared = paramBoolean;
    this.signature = getClassSignature(paramClass).intern();
    this.field = null;
  }
  
  ObjectStreamField(String paramString1, String paramString2, boolean paramBoolean) {
    if (paramString1 == null)
      throw new NullPointerException(); 
    this.name = paramString1;
    this.signature = paramString2.intern();
    this.unshared = paramBoolean;
    this.field = null;
    switch (paramString2.charAt(0)) {
      case 'Z':
        this.type = boolean.class;
        return;
      case 'B':
        this.type = byte.class;
        return;
      case 'C':
        this.type = char.class;
        return;
      case 'S':
        this.type = short.class;
        return;
      case 'I':
        this.type = int.class;
        return;
      case 'J':
        this.type = long.class;
        return;
      case 'F':
        this.type = float.class;
        return;
      case 'D':
        this.type = double.class;
        return;
      case 'L':
      case '[':
        this.type = Object.class;
        return;
    } 
    throw new IllegalArgumentException("illegal signature");
  }
  
  ObjectStreamField(Field paramField, boolean paramBoolean1, boolean paramBoolean2) {
    this.field = paramField;
    this.unshared = paramBoolean1;
    this.name = paramField.getName();
    Class clazz = paramField.getType();
    this.type = (paramBoolean2 || clazz.isPrimitive()) ? clazz : Object.class;
    this.signature = getClassSignature(clazz).intern();
  }
  
  public String getName() { return this.name; }
  
  @CallerSensitive
  public Class<?> getType() {
    if (System.getSecurityManager() != null) {
      Class clazz = Reflection.getCallerClass();
      if (ReflectUtil.needsPackageAccessCheck(clazz.getClassLoader(), this.type.getClassLoader()))
        ReflectUtil.checkPackageAccess(this.type); 
    } 
    return this.type;
  }
  
  public char getTypeCode() { return this.signature.charAt(0); }
  
  public String getTypeString() { return isPrimitive() ? null : this.signature; }
  
  public int getOffset() { return this.offset; }
  
  protected void setOffset(int paramInt) { this.offset = paramInt; }
  
  public boolean isPrimitive() {
    char c = this.signature.charAt(0);
    return (c != 'L' && c != '[');
  }
  
  public boolean isUnshared() { return this.unshared; }
  
  public int compareTo(Object paramObject) {
    ObjectStreamField objectStreamField = (ObjectStreamField)paramObject;
    boolean bool = isPrimitive();
    return (bool != objectStreamField.isPrimitive()) ? (bool ? -1 : 1) : this.name.compareTo(objectStreamField.name);
  }
  
  public String toString() { return this.signature + ' ' + this.name; }
  
  Field getField() { return this.field; }
  
  String getSignature() { return this.signature; }
  
  private static String getClassSignature(Class<?> paramClass) {
    StringBuilder stringBuilder = new StringBuilder();
    while (paramClass.isArray()) {
      stringBuilder.append('[');
      paramClass = paramClass.getComponentType();
    } 
    if (paramClass.isPrimitive()) {
      if (paramClass == int.class) {
        stringBuilder.append('I');
      } else if (paramClass == byte.class) {
        stringBuilder.append('B');
      } else if (paramClass == long.class) {
        stringBuilder.append('J');
      } else if (paramClass == float.class) {
        stringBuilder.append('F');
      } else if (paramClass == double.class) {
        stringBuilder.append('D');
      } else if (paramClass == short.class) {
        stringBuilder.append('S');
      } else if (paramClass == char.class) {
        stringBuilder.append('C');
      } else if (paramClass == boolean.class) {
        stringBuilder.append('Z');
      } else if (paramClass == void.class) {
        stringBuilder.append('V');
      } else {
        throw new InternalError();
      } 
    } else {
      stringBuilder.append('L' + paramClass.getName().replace('.', '/') + ';');
    } 
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\ObjectStreamField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */