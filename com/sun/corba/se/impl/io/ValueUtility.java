package com.sun.corba.se.impl.io;

import com.sun.corba.se.impl.util.RepositoryId;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.CORBA._IDLTypeStub;
import com.sun.org.omg.SendingContext.CodeBase;
import java.lang.reflect.Modifier;
import java.util.Stack;
import javax.rmi.CORBA.ValueHandler;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ValueMember;
import sun.corba.JavaCorbaAccess;
import sun.corba.SharedSecrets;

public class ValueUtility {
  public static final short PRIVATE_MEMBER = 0;
  
  public static final short PUBLIC_MEMBER = 1;
  
  private static final String[] primitiveConstants = { 
      null, null, "S", "I", "S", "I", "F", "D", "Z", "C", 
      "B", null, null, null, null, null, null, null, null, null, 
      null, null, null, "J", "J", "D", "C", null, null, null, 
      null, null, null };
  
  public static String getSignature(ValueMember paramValueMember) throws ClassNotFoundException {
    if (paramValueMember.type.kind().value() == 30 || paramValueMember.type.kind().value() == 29 || paramValueMember.type.kind().value() == 14) {
      Class clazz = RepositoryId.cache.getId(paramValueMember.id).getClassFromType();
      return ObjectStreamClass.getSignature(clazz);
    } 
    return primitiveConstants[paramValueMember.type.kind().value()];
  }
  
  public static FullValueDescription translate(ORB paramORB, ObjectStreamClass paramObjectStreamClass, ValueHandler paramValueHandler) {
    FullValueDescription fullValueDescription = new FullValueDescription();
    Class clazz1 = paramObjectStreamClass.forClass();
    ValueHandlerImpl valueHandlerImpl = (ValueHandlerImpl)paramValueHandler;
    String str = valueHandlerImpl.createForAnyType(clazz1);
    fullValueDescription.name = valueHandlerImpl.getUnqualifiedName(str);
    if (fullValueDescription.name == null)
      fullValueDescription.name = ""; 
    fullValueDescription.id = valueHandlerImpl.getRMIRepositoryID(clazz1);
    if (fullValueDescription.id == null)
      fullValueDescription.id = ""; 
    fullValueDescription.is_abstract = ObjectStreamClassCorbaExt.isAbstractInterface(clazz1);
    fullValueDescription.is_custom = (paramObjectStreamClass.hasWriteObject() || paramObjectStreamClass.isExternalizable());
    fullValueDescription.defined_in = valueHandlerImpl.getDefinedInId(str);
    if (fullValueDescription.defined_in == null)
      fullValueDescription.defined_in = ""; 
    fullValueDescription.version = valueHandlerImpl.getSerialVersionUID(str);
    if (fullValueDescription.version == null)
      fullValueDescription.version = ""; 
    fullValueDescription.operations = new com.sun.org.omg.CORBA.OperationDescription[0];
    fullValueDescription.attributes = new com.sun.org.omg.CORBA.AttributeDescription[0];
    IdentityKeyValueStack identityKeyValueStack = new IdentityKeyValueStack(null);
    fullValueDescription.members = translateMembers(paramORB, paramObjectStreamClass, paramValueHandler, identityKeyValueStack);
    fullValueDescription.initializers = new com.sun.org.omg.CORBA.Initializer[0];
    Class[] arrayOfClass = paramObjectStreamClass.forClass().getInterfaces();
    byte b1 = 0;
    fullValueDescription.supported_interfaces = new String[arrayOfClass.length];
    byte b2;
    for (b2 = 0; b2 < arrayOfClass.length; b2++) {
      fullValueDescription.supported_interfaces[b2] = valueHandlerImpl.createForAnyType(arrayOfClass[b2]);
      if (!java.rmi.Remote.class.isAssignableFrom(arrayOfClass[b2]) || !Modifier.isPublic(arrayOfClass[b2].getModifiers()))
        b1++; 
    } 
    fullValueDescription.abstract_base_values = new String[b1];
    for (b2 = 0; b2 < arrayOfClass.length; b2++) {
      if (!java.rmi.Remote.class.isAssignableFrom(arrayOfClass[b2]) || !Modifier.isPublic(arrayOfClass[b2].getModifiers()))
        fullValueDescription.abstract_base_values[b2] = valueHandlerImpl.createForAnyType(arrayOfClass[b2]); 
    } 
    fullValueDescription.is_truncatable = false;
    Class clazz2 = paramObjectStreamClass.forClass().getSuperclass();
    if (java.io.Serializable.class.isAssignableFrom(clazz2)) {
      fullValueDescription.base_value = valueHandlerImpl.getRMIRepositoryID(clazz2);
    } else {
      fullValueDescription.base_value = "";
    } 
    fullValueDescription.type = paramORB.get_primitive_tc(TCKind.tk_value);
    return fullValueDescription;
  }
  
  private static ValueMember[] translateMembers(ORB paramORB, ObjectStreamClass paramObjectStreamClass, ValueHandler paramValueHandler, IdentityKeyValueStack paramIdentityKeyValueStack) {
    ValueHandlerImpl valueHandlerImpl = (ValueHandlerImpl)paramValueHandler;
    ObjectStreamField[] arrayOfObjectStreamField = paramObjectStreamClass.getFields();
    int i = arrayOfObjectStreamField.length;
    ValueMember[] arrayOfValueMember = new ValueMember[i];
    for (byte b = 0; b < i; b++) {
      String str = valueHandlerImpl.getRMIRepositoryID(arrayOfObjectStreamField[b].getClazz());
      arrayOfValueMember[b] = new ValueMember();
      (arrayOfValueMember[b]).name = arrayOfObjectStreamField[b].getName();
      (arrayOfValueMember[b]).id = str;
      (arrayOfValueMember[b]).defined_in = valueHandlerImpl.getDefinedInId(str);
      (arrayOfValueMember[b]).version = "1.0";
      (arrayOfValueMember[b]).type_def = new _IDLTypeStub();
      if (arrayOfObjectStreamField[b].getField() == null) {
        (arrayOfValueMember[b]).access = 0;
      } else {
        int j = arrayOfObjectStreamField[b].getField().getModifiers();
        if (Modifier.isPublic(j)) {
          (arrayOfValueMember[b]).access = 1;
        } else {
          (arrayOfValueMember[b]).access = 0;
        } 
      } 
      switch (arrayOfObjectStreamField[b].getTypeCode()) {
        case 'B':
          (arrayOfValueMember[b]).type = paramORB.get_primitive_tc(TCKind.tk_octet);
          break;
        case 'C':
          (arrayOfValueMember[b]).type = paramORB.get_primitive_tc(valueHandlerImpl.getJavaCharTCKind());
          break;
        case 'F':
          (arrayOfValueMember[b]).type = paramORB.get_primitive_tc(TCKind.tk_float);
          break;
        case 'D':
          (arrayOfValueMember[b]).type = paramORB.get_primitive_tc(TCKind.tk_double);
          break;
        case 'I':
          (arrayOfValueMember[b]).type = paramORB.get_primitive_tc(TCKind.tk_long);
          break;
        case 'J':
          (arrayOfValueMember[b]).type = paramORB.get_primitive_tc(TCKind.tk_longlong);
          break;
        case 'S':
          (arrayOfValueMember[b]).type = paramORB.get_primitive_tc(TCKind.tk_short);
          break;
        case 'Z':
          (arrayOfValueMember[b]).type = paramORB.get_primitive_tc(TCKind.tk_boolean);
          break;
        default:
          (arrayOfValueMember[b]).type = createTypeCodeForClassInternal(paramORB, arrayOfObjectStreamField[b].getClazz(), valueHandlerImpl, paramIdentityKeyValueStack);
          (arrayOfValueMember[b]).id = valueHandlerImpl.createForAnyType(arrayOfObjectStreamField[b].getType());
          break;
      } 
    } 
    return arrayOfValueMember;
  }
  
  private static boolean exists(String paramString, String[] paramArrayOfString) {
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (paramString.equals(paramArrayOfString[b]))
        return true; 
    } 
    return false;
  }
  
  public static boolean isAssignableFrom(String paramString, FullValueDescription paramFullValueDescription, CodeBase paramCodeBase) {
    if (exists(paramString, paramFullValueDescription.supported_interfaces))
      return true; 
    if (paramString.equals(paramFullValueDescription.id))
      return true; 
    if (paramFullValueDescription.base_value != null && !paramFullValueDescription.base_value.equals("")) {
      FullValueDescription fullValueDescription = paramCodeBase.meta(paramFullValueDescription.base_value);
      return isAssignableFrom(paramString, fullValueDescription, paramCodeBase);
    } 
    return false;
  }
  
  public static TypeCode createTypeCodeForClass(ORB paramORB, Class paramClass, ValueHandler paramValueHandler) {
    IdentityKeyValueStack identityKeyValueStack = new IdentityKeyValueStack(null);
    return createTypeCodeForClassInternal(paramORB, paramClass, paramValueHandler, identityKeyValueStack);
  }
  
  private static TypeCode createTypeCodeForClassInternal(ORB paramORB, Class paramClass, ValueHandler paramValueHandler, IdentityKeyValueStack paramIdentityKeyValueStack) {
    TypeCode typeCode = null;
    String str = (String)paramIdentityKeyValueStack.get(paramClass);
    if (str != null)
      return paramORB.create_recursive_tc(str); 
    str = paramValueHandler.getRMIRepositoryID(paramClass);
    if (str == null)
      str = ""; 
    paramIdentityKeyValueStack.push(paramClass, str);
    typeCode = createTypeCodeInternal(paramORB, paramClass, paramValueHandler, str, paramIdentityKeyValueStack);
    paramIdentityKeyValueStack.pop();
    return typeCode;
  }
  
  private static TypeCode createTypeCodeInternal(ORB paramORB, Class paramClass, ValueHandler paramValueHandler, String paramString, IdentityKeyValueStack paramIdentityKeyValueStack) {
    if (paramClass.isArray()) {
      TypeCode typeCode1;
      Class clazz1 = paramClass.getComponentType();
      if (clazz1.isPrimitive()) {
        typeCode1 = getPrimitiveTypeCodeForClass(paramORB, clazz1, paramValueHandler);
      } else {
        typeCode1 = createTypeCodeForClassInternal(paramORB, clazz1, paramValueHandler, paramIdentityKeyValueStack);
      } 
      TypeCode typeCode2 = paramORB.create_sequence_tc(0, typeCode1);
      return paramORB.create_value_box_tc(paramString, "Sequence", typeCode2);
    } 
    if (paramClass == String.class) {
      TypeCode typeCode1 = paramORB.create_string_tc(0);
      return paramORB.create_value_box_tc(paramString, "StringValue", typeCode1);
    } 
    if (java.rmi.Remote.class.isAssignableFrom(paramClass))
      return paramORB.get_primitive_tc(TCKind.tk_objref); 
    if (org.omg.CORBA.Object.class.isAssignableFrom(paramClass))
      return paramORB.get_primitive_tc(TCKind.tk_objref); 
    ObjectStreamClass objectStreamClass = ObjectStreamClass.lookup(paramClass);
    if (objectStreamClass == null)
      return paramORB.create_value_box_tc(paramString, "Value", paramORB.get_primitive_tc(TCKind.tk_value)); 
    int i = objectStreamClass.isCustomMarshaled() ? 1 : 0;
    TypeCode typeCode = null;
    Class clazz = paramClass.getSuperclass();
    if (clazz != null && java.io.Serializable.class.isAssignableFrom(clazz))
      typeCode = createTypeCodeForClassInternal(paramORB, clazz, paramValueHandler, paramIdentityKeyValueStack); 
    ValueMember[] arrayOfValueMember = translateMembers(paramORB, objectStreamClass, paramValueHandler, paramIdentityKeyValueStack);
    return paramORB.create_value_tc(paramString, paramClass.getName(), i, typeCode, arrayOfValueMember);
  }
  
  public static TypeCode getPrimitiveTypeCodeForClass(ORB paramORB, Class paramClass, ValueHandler paramValueHandler) { return (paramClass == int.class) ? paramORB.get_primitive_tc(TCKind.tk_long) : ((paramClass == byte.class) ? paramORB.get_primitive_tc(TCKind.tk_octet) : ((paramClass == long.class) ? paramORB.get_primitive_tc(TCKind.tk_longlong) : ((paramClass == float.class) ? paramORB.get_primitive_tc(TCKind.tk_float) : ((paramClass == double.class) ? paramORB.get_primitive_tc(TCKind.tk_double) : ((paramClass == short.class) ? paramORB.get_primitive_tc(TCKind.tk_short) : ((paramClass == char.class) ? paramORB.get_primitive_tc(((ValueHandlerImpl)paramValueHandler).getJavaCharTCKind()) : ((paramClass == boolean.class) ? paramORB.get_primitive_tc(TCKind.tk_boolean) : paramORB.get_primitive_tc(TCKind.tk_any)))))))); }
  
  static  {
    SharedSecrets.setJavaCorbaAccess(new JavaCorbaAccess() {
          public ValueHandlerImpl newValueHandlerImpl() { return ValueHandlerImpl.getInstance(); }
          
          public Class<?> loadClass(String param1String) throws ClassNotFoundException { return (Thread.currentThread().getContextClassLoader() != null) ? Thread.currentThread().getContextClassLoader().loadClass(param1String) : ClassLoader.getSystemClassLoader().loadClass(param1String); }
        });
  }
  
  private static class IdentityKeyValueStack {
    Stack pairs = null;
    
    private IdentityKeyValueStack() {}
    
    Object get(Object param1Object) {
      if (this.pairs == null)
        return null; 
      for (KeyValuePair keyValuePair : this.pairs) {
        if (keyValuePair.key == param1Object)
          return keyValuePair.value; 
      } 
      return null;
    }
    
    void push(Object param1Object1, Object param1Object2) {
      if (this.pairs == null)
        this.pairs = new Stack(); 
      this.pairs.push(new KeyValuePair(param1Object1, param1Object2));
    }
    
    void pop() { this.pairs.pop(); }
    
    private static class KeyValuePair {
      Object key;
      
      Object value;
      
      KeyValuePair(Object param2Object1, Object param2Object2) {
        this.key = param2Object1;
        this.value = param2Object2;
      }
      
      boolean equals(KeyValuePair param2KeyValuePair) { return (param2KeyValuePair.key == this.key); }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\io\ValueUtility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */