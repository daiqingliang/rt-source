package jdk.internal.org.objectweb.asm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Type {
  public static final int VOID = 0;
  
  public static final int BOOLEAN = 1;
  
  public static final int CHAR = 2;
  
  public static final int BYTE = 3;
  
  public static final int SHORT = 4;
  
  public static final int INT = 5;
  
  public static final int FLOAT = 6;
  
  public static final int LONG = 7;
  
  public static final int DOUBLE = 8;
  
  public static final int ARRAY = 9;
  
  public static final int OBJECT = 10;
  
  public static final int METHOD = 11;
  
  public static final Type VOID_TYPE = new Type(0, null, 1443168256, 1);
  
  public static final Type BOOLEAN_TYPE = new Type(1, null, 1509950721, 1);
  
  public static final Type CHAR_TYPE = new Type(2, null, 1124075009, 1);
  
  public static final Type BYTE_TYPE = new Type(3, null, 1107297537, 1);
  
  public static final Type SHORT_TYPE = new Type(4, null, 1392510721, 1);
  
  public static final Type INT_TYPE = new Type(5, null, 1224736769, 1);
  
  public static final Type FLOAT_TYPE = new Type(6, null, 1174536705, 1);
  
  public static final Type LONG_TYPE = new Type(7, null, 1241579778, 1);
  
  public static final Type DOUBLE_TYPE = new Type(8, null, 1141048066, 1);
  
  private final int sort;
  
  private final char[] buf;
  
  private final int off;
  
  private final int len;
  
  private Type(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3) {
    this.sort = paramInt1;
    this.buf = paramArrayOfChar;
    this.off = paramInt2;
    this.len = paramInt3;
  }
  
  public static Type getType(String paramString) { return getType(paramString.toCharArray(), 0); }
  
  public static Type getObjectType(String paramString) {
    char[] arrayOfChar = paramString.toCharArray();
    return new Type((arrayOfChar[0] == '[') ? 9 : 10, arrayOfChar, 0, arrayOfChar.length);
  }
  
  public static Type getMethodType(String paramString) { return getType(paramString.toCharArray(), 0); }
  
  public static Type getMethodType(Type paramType, Type... paramVarArgs) { return getType(getMethodDescriptor(paramType, paramVarArgs)); }
  
  public static Type getType(Class<?> paramClass) { return paramClass.isPrimitive() ? ((paramClass == int.class) ? INT_TYPE : ((paramClass == void.class) ? VOID_TYPE : ((paramClass == boolean.class) ? BOOLEAN_TYPE : ((paramClass == byte.class) ? BYTE_TYPE : ((paramClass == char.class) ? CHAR_TYPE : ((paramClass == short.class) ? SHORT_TYPE : ((paramClass == double.class) ? DOUBLE_TYPE : ((paramClass == float.class) ? FLOAT_TYPE : LONG_TYPE)))))))) : getType(getDescriptor(paramClass)); }
  
  public static Type getType(Constructor<?> paramConstructor) { return getType(getConstructorDescriptor(paramConstructor)); }
  
  public static Type getType(Method paramMethod) { return getType(getMethodDescriptor(paramMethod)); }
  
  public static Type[] getArgumentTypes(String paramString) {
    char[] arrayOfChar = paramString.toCharArray();
    int i = 1;
    byte b = 0;
    while (true) {
      char c = arrayOfChar[i++];
      if (c == ')')
        break; 
      if (c == 'L') {
        while (arrayOfChar[i++] != ';');
        b++;
        continue;
      } 
      if (c != '[')
        b++; 
    } 
    Type[] arrayOfType = new Type[b];
    i = 1;
    for (b = 0; arrayOfChar[i] != ')'; b++) {
      arrayOfType[b] = getType(arrayOfChar, i);
      i += (arrayOfType[b]).len + (((arrayOfType[b]).sort == 10) ? 2 : 0);
    } 
    return arrayOfType;
  }
  
  public static Type[] getArgumentTypes(Method paramMethod) {
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    Type[] arrayOfType = new Type[arrayOfClass.length];
    for (int i = arrayOfClass.length - 1; i >= 0; i--)
      arrayOfType[i] = getType(arrayOfClass[i]); 
    return arrayOfType;
  }
  
  public static Type getReturnType(String paramString) {
    char[] arrayOfChar = paramString.toCharArray();
    return getType(arrayOfChar, paramString.indexOf(')') + 1);
  }
  
  public static Type getReturnType(Method paramMethod) { return getType(paramMethod.getReturnType()); }
  
  public static int getArgumentsAndReturnSizes(String paramString) {
    byte b1 = 1;
    byte b2 = 1;
    while (true) {
      char c = paramString.charAt(b2++);
      if (c == ')') {
        c = paramString.charAt(b2);
        return b1 << 2 | ((c == 'V') ? 0 : ((c == 'D' || c == 'J') ? 2 : 1));
      } 
      if (c == 'L') {
        while (paramString.charAt(b2++) != ';');
        b1++;
        continue;
      } 
      if (c == '[') {
        while ((c = paramString.charAt(b2)) == '[')
          b2++; 
        if (c == 'D' || c == 'J')
          b1--; 
        continue;
      } 
      if (c == 'D' || c == 'J') {
        b1 += 2;
        continue;
      } 
      b1++;
    } 
  }
  
  private static Type getType(char[] paramArrayOfChar, int paramInt) {
    int i;
    switch (paramArrayOfChar[paramInt]) {
      case 'V':
        return VOID_TYPE;
      case 'Z':
        return BOOLEAN_TYPE;
      case 'C':
        return CHAR_TYPE;
      case 'B':
        return BYTE_TYPE;
      case 'S':
        return SHORT_TYPE;
      case 'I':
        return INT_TYPE;
      case 'F':
        return FLOAT_TYPE;
      case 'J':
        return LONG_TYPE;
      case 'D':
        return DOUBLE_TYPE;
      case '[':
        for (i = 1; paramArrayOfChar[paramInt + i] == '['; i++);
        if (paramArrayOfChar[paramInt + i] == 'L')
          while (paramArrayOfChar[paramInt + ++i] != ';')
            i++;  
        return new Type(9, paramArrayOfChar, paramInt, i + 1);
      case 'L':
        for (i = 1; paramArrayOfChar[paramInt + i] != ';'; i++);
        return new Type(10, paramArrayOfChar, paramInt + 1, i - 1);
    } 
    return new Type(11, paramArrayOfChar, paramInt, paramArrayOfChar.length - paramInt);
  }
  
  public int getSort() { return this.sort; }
  
  public int getDimensions() {
    int i;
    for (i = 1; this.buf[this.off + i] == '['; i++);
    return i;
  }
  
  public Type getElementType() { return getType(this.buf, this.off + getDimensions()); }
  
  public String getClassName() {
    int i;
    StringBuilder stringBuilder;
    switch (this.sort) {
      case 0:
        return "void";
      case 1:
        return "boolean";
      case 2:
        return "char";
      case 3:
        return "byte";
      case 4:
        return "short";
      case 5:
        return "int";
      case 6:
        return "float";
      case 7:
        return "long";
      case 8:
        return "double";
      case 9:
        stringBuilder = new StringBuilder(getElementType().getClassName());
        for (i = getDimensions(); i > 0; i--)
          stringBuilder.append("[]"); 
        return stringBuilder.toString();
      case 10:
        return (new String(this.buf, this.off, this.len)).replace('/', '.');
    } 
    return null;
  }
  
  public String getInternalName() { return new String(this.buf, this.off, this.len); }
  
  public Type[] getArgumentTypes() { return getArgumentTypes(getDescriptor()); }
  
  public Type getReturnType() { return getReturnType(getDescriptor()); }
  
  public int getArgumentsAndReturnSizes() { return getArgumentsAndReturnSizes(getDescriptor()); }
  
  public String getDescriptor() {
    StringBuffer stringBuffer = new StringBuffer();
    getDescriptor(stringBuffer);
    return stringBuffer.toString();
  }
  
  public static String getMethodDescriptor(Type paramType, Type... paramVarArgs) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append('(');
    for (byte b = 0; b < paramVarArgs.length; b++)
      paramVarArgs[b].getDescriptor(stringBuffer); 
    stringBuffer.append(')');
    paramType.getDescriptor(stringBuffer);
    return stringBuffer.toString();
  }
  
  private void getDescriptor(StringBuffer paramStringBuffer) {
    if (this.buf == null) {
      paramStringBuffer.append((char)((this.off & 0xFF000000) >>> 24));
    } else if (this.sort == 10) {
      paramStringBuffer.append('L');
      paramStringBuffer.append(this.buf, this.off, this.len);
      paramStringBuffer.append(';');
    } else {
      paramStringBuffer.append(this.buf, this.off, this.len);
    } 
  }
  
  public static String getInternalName(Class<?> paramClass) { return paramClass.getName().replace('.', '/'); }
  
  public static String getDescriptor(Class<?> paramClass) {
    StringBuffer stringBuffer = new StringBuffer();
    getDescriptor(stringBuffer, paramClass);
    return stringBuffer.toString();
  }
  
  public static String getConstructorDescriptor(Constructor<?> paramConstructor) {
    Class[] arrayOfClass = paramConstructor.getParameterTypes();
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append('(');
    for (byte b = 0; b < arrayOfClass.length; b++)
      getDescriptor(stringBuffer, arrayOfClass[b]); 
    return stringBuffer.append(")V").toString();
  }
  
  public static String getMethodDescriptor(Method paramMethod) {
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append('(');
    for (byte b = 0; b < arrayOfClass.length; b++)
      getDescriptor(stringBuffer, arrayOfClass[b]); 
    stringBuffer.append(')');
    getDescriptor(stringBuffer, paramMethod.getReturnType());
    return stringBuffer.toString();
  }
  
  private static void getDescriptor(StringBuffer paramStringBuffer, Class<?> paramClass) {
    Class<?> clazz = paramClass;
    while (true) {
      if (clazz.isPrimitive()) {
        char c;
        if (clazz == int.class) {
          c = 'I';
        } else if (clazz == void.class) {
          c = 'V';
        } else if (clazz == boolean.class) {
          c = 'Z';
        } else if (clazz == byte.class) {
          c = 'B';
        } else if (clazz == char.class) {
          c = 'C';
        } else if (clazz == short.class) {
          c = 'S';
        } else if (clazz == double.class) {
          c = 'D';
        } else if (clazz == float.class) {
          c = 'F';
        } else {
          c = 'J';
        } 
        paramStringBuffer.append(c);
        return;
      } 
      if (clazz.isArray()) {
        paramStringBuffer.append('[');
        clazz = clazz.getComponentType();
        continue;
      } 
      break;
    } 
    paramStringBuffer.append('L');
    String str = clazz.getName();
    int i = str.length();
    for (byte b = 0; b < i; b++) {
      char c = str.charAt(b);
      paramStringBuffer.append((c == '.') ? 47 : c);
    } 
    paramStringBuffer.append(';');
  }
  
  public int getSize() { return (this.buf == null) ? (this.off & 0xFF) : 1; }
  
  public int getOpcode(int paramInt) { return (paramInt == 46 || paramInt == 79) ? (paramInt + ((this.buf == null) ? ((this.off & 0xFF00) >> 8) : 4)) : (paramInt + ((this.buf == null) ? ((this.off & 0xFF0000) >> 16) : 4)); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof Type))
      return false; 
    Type type = (Type)paramObject;
    if (this.sort != type.sort)
      return false; 
    if (this.sort >= 9) {
      if (this.len != type.len)
        return false; 
      int i = this.off;
      int j = type.off;
      int k = i + this.len;
      while (i < k) {
        if (this.buf[i] != type.buf[j])
          return false; 
        i++;
        j++;
      } 
    } 
    return true;
  }
  
  public int hashCode() {
    int i = 13 * this.sort;
    if (this.sort >= 9) {
      int j = this.off;
      int k = j + this.len;
      while (j < k) {
        i = 17 * (i + this.buf[j]);
        j++;
      } 
    } 
    return i;
  }
  
  public String toString() { return getDescriptor(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\Type.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */