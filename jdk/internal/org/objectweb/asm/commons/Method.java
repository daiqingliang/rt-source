package jdk.internal.org.objectweb.asm.commons;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import jdk.internal.org.objectweb.asm.Type;

public class Method {
  private final String name;
  
  private final String desc;
  
  private static final Map<String, String> DESCRIPTORS = new HashMap();
  
  public Method(String paramString1, String paramString2) {
    this.name = paramString1;
    this.desc = paramString2;
  }
  
  public Method(String paramString, Type paramType, Type[] paramArrayOfType) { this(paramString, Type.getMethodDescriptor(paramType, paramArrayOfType)); }
  
  public static Method getMethod(Method paramMethod) { return new Method(paramMethod.getName(), Type.getMethodDescriptor(paramMethod)); }
  
  public static Method getMethod(Constructor<?> paramConstructor) { return new Method("<init>", Type.getConstructorDescriptor(paramConstructor)); }
  
  public static Method getMethod(String paramString) throws IllegalArgumentException { return getMethod(paramString, false); }
  
  public static Method getMethod(String paramString, boolean paramBoolean) throws IllegalArgumentException {
    int m;
    int i = paramString.indexOf(' ');
    int j = paramString.indexOf('(', i) + 1;
    int k = paramString.indexOf(')', j);
    if (i == -1 || j == -1 || k == -1)
      throw new IllegalArgumentException(); 
    String str1 = paramString.substring(0, i);
    String str2 = paramString.substring(i + 1, j - 1).trim();
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append('(');
    do {
      String str;
      m = paramString.indexOf(',', j);
      if (m == -1) {
        str = map(paramString.substring(j, k).trim(), paramBoolean);
      } else {
        str = map(paramString.substring(j, m).trim(), paramBoolean);
        j = m + 1;
      } 
      stringBuilder.append(str);
    } while (m != -1);
    stringBuilder.append(')');
    stringBuilder.append(map(str1, paramBoolean));
    return new Method(str2, stringBuilder.toString());
  }
  
  private static String map(String paramString, boolean paramBoolean) {
    if ("".equals(paramString))
      return paramString; 
    StringBuilder stringBuilder = new StringBuilder();
    int i = 0;
    while ((i = paramString.indexOf("[]", i) + 1) > 0)
      stringBuilder.append('['); 
    String str1 = paramString.substring(0, paramString.length() - stringBuilder.length() * 2);
    String str2 = (String)DESCRIPTORS.get(str1);
    if (str2 != null) {
      stringBuilder.append(str2);
    } else {
      stringBuilder.append('L');
      if (str1.indexOf('.') < 0) {
        if (!paramBoolean)
          stringBuilder.append("java/lang/"); 
        stringBuilder.append(str1);
      } else {
        stringBuilder.append(str1.replace('.', '/'));
      } 
      stringBuilder.append(';');
    } 
    return stringBuilder.toString();
  }
  
  public String getName() { return this.name; }
  
  public String getDescriptor() { return this.desc; }
  
  public Type getReturnType() { return Type.getReturnType(this.desc); }
  
  public Type[] getArgumentTypes() { return Type.getArgumentTypes(this.desc); }
  
  public String toString() { return this.name + this.desc; }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof Method))
      return false; 
    Method method = (Method)paramObject;
    return (this.name.equals(method.name) && this.desc.equals(method.desc));
  }
  
  public int hashCode() { return this.name.hashCode() ^ this.desc.hashCode(); }
  
  static  {
    DESCRIPTORS.put("void", "V");
    DESCRIPTORS.put("byte", "B");
    DESCRIPTORS.put("char", "C");
    DESCRIPTORS.put("double", "D");
    DESCRIPTORS.put("float", "F");
    DESCRIPTORS.put("int", "I");
    DESCRIPTORS.put("long", "J");
    DESCRIPTORS.put("short", "S");
    DESCRIPTORS.put("boolean", "Z");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\commons\Method.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */