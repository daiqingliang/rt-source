package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.ClassFormatException;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;

public abstract class Type implements Serializable {
  protected byte type;
  
  protected String signature;
  
  public static final BasicType VOID = new BasicType((byte)12);
  
  public static final BasicType BOOLEAN = new BasicType((byte)4);
  
  public static final BasicType INT = new BasicType((byte)10);
  
  public static final BasicType SHORT = new BasicType((byte)9);
  
  public static final BasicType BYTE = new BasicType((byte)8);
  
  public static final BasicType LONG = new BasicType((byte)11);
  
  public static final BasicType DOUBLE = new BasicType((byte)7);
  
  public static final BasicType FLOAT = new BasicType((byte)6);
  
  public static final BasicType CHAR = new BasicType((byte)5);
  
  public static final ObjectType OBJECT = new ObjectType("java.lang.Object");
  
  public static final ObjectType STRING = new ObjectType("java.lang.String");
  
  public static final ObjectType STRINGBUFFER = new ObjectType("java.lang.StringBuffer");
  
  public static final ObjectType THROWABLE = new ObjectType("java.lang.Throwable");
  
  public static final Type[] NO_ARGS = new Type[0];
  
  public static final ReferenceType NULL = new ReferenceType() {
    
    };
  
  public static final Type UNKNOWN = new Type((byte)15, "<unknown object>") {
    
    };
  
  private static int consumed_chars = 0;
  
  protected Type(byte paramByte, String paramString) {
    this.type = paramByte;
    this.signature = paramString;
  }
  
  public String getSignature() { return this.signature; }
  
  public byte getType() { return this.type; }
  
  public int getSize() {
    switch (this.type) {
      case 7:
      case 11:
        return 2;
      case 12:
        return 0;
    } 
    return 1;
  }
  
  public String toString() { return (equals(NULL) || this.type >= 15) ? this.signature : Utility.signatureToString(this.signature, false); }
  
  public static String getMethodSignature(Type paramType, Type[] paramArrayOfType) {
    StringBuffer stringBuffer = new StringBuffer("(");
    boolean bool = (paramArrayOfType == null) ? 0 : paramArrayOfType.length;
    for (byte b = 0; b < bool; b++)
      stringBuffer.append(paramArrayOfType[b].getSignature()); 
    stringBuffer.append(')');
    stringBuffer.append(paramType.getSignature());
    return stringBuffer.toString();
  }
  
  public static final Type getType(String paramString) throws StringIndexOutOfBoundsException {
    byte b = Utility.typeOfSignature(paramString);
    if (b <= 12) {
      consumed_chars = 1;
      return BasicType.getType(b);
    } 
    if (b == 13) {
      int j = 0;
      do {
        j++;
      } while (paramString.charAt(j) == '[');
      Type type1 = getType(paramString.substring(j));
      consumed_chars += j;
      return new ArrayType(type1, j);
    } 
    int i = paramString.indexOf(';');
    if (i < 0)
      throw new ClassFormatException("Invalid signature: " + paramString); 
    consumed_chars = i + 1;
    return new ObjectType(paramString.substring(1, i).replace('/', '.'));
  }
  
  public static Type getReturnType(String paramString) throws StringIndexOutOfBoundsException {
    try {
      int i = paramString.lastIndexOf(')') + 1;
      return getType(paramString.substring(i));
    } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
      throw new ClassFormatException("Invalid method signature: " + paramString);
    } 
  }
  
  public static Type[] getArgumentTypes(String paramString) {
    ArrayList arrayList = new ArrayList();
    try {
      if (paramString.charAt(0) != '(')
        throw new ClassFormatException("Invalid method signature: " + paramString); 
      for (int i = 1; paramString.charAt(i) != ')'; i += consumed_chars)
        arrayList.add(getType(paramString.substring(i))); 
    } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
      throw new ClassFormatException("Invalid method signature: " + paramString);
    } 
    Type[] arrayOfType = new Type[arrayList.size()];
    arrayList.toArray(arrayOfType);
    return arrayOfType;
  }
  
  public static Type getType(Class paramClass) {
    if (paramClass == null)
      throw new IllegalArgumentException("Class must not be null"); 
    if (paramClass.isArray())
      return getType(paramClass.getName()); 
    if (paramClass.isPrimitive()) {
      if (paramClass == int.class)
        return INT; 
      if (paramClass == void.class)
        return VOID; 
      if (paramClass == double.class)
        return DOUBLE; 
      if (paramClass == float.class)
        return FLOAT; 
      if (paramClass == boolean.class)
        return BOOLEAN; 
      if (paramClass == byte.class)
        return BYTE; 
      if (paramClass == short.class)
        return SHORT; 
      if (paramClass == byte.class)
        return BYTE; 
      if (paramClass == long.class)
        return LONG; 
      if (paramClass == char.class)
        return CHAR; 
      throw new IllegalStateException("Ooops, what primitive type is " + paramClass);
    } 
    return new ObjectType(paramClass.getName());
  }
  
  public static String getSignature(Method paramMethod) {
    StringBuffer stringBuffer = new StringBuffer("(");
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    for (byte b = 0; b < arrayOfClass.length; b++)
      stringBuffer.append(getType(arrayOfClass[b]).getSignature()); 
    stringBuffer.append(")");
    stringBuffer.append(getType(paramMethod.getReturnType()).getSignature());
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\Type.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */