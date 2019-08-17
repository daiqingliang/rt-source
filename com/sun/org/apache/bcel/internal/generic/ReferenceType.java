package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.Repository;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;

public abstract class ReferenceType extends Type {
  protected ReferenceType(byte paramByte, String paramString) { super(paramByte, paramString); }
  
  ReferenceType() { super((byte)14, "<null object>"); }
  
  public boolean isCastableTo(Type paramType) { return equals(Type.NULL) ? true : isAssignmentCompatibleWith(paramType); }
  
  public boolean isAssignmentCompatibleWith(Type paramType) {
    if (!(paramType instanceof ReferenceType))
      return false; 
    ReferenceType referenceType = (ReferenceType)paramType;
    if (equals(Type.NULL))
      return true; 
    if (this instanceof ObjectType && ((ObjectType)this).referencesClass()) {
      if (referenceType instanceof ObjectType && ((ObjectType)referenceType).referencesClass()) {
        if (equals(referenceType))
          return true; 
        if (Repository.instanceOf(((ObjectType)this).getClassName(), ((ObjectType)referenceType).getClassName()))
          return true; 
      } 
      if (referenceType instanceof ObjectType && ((ObjectType)referenceType).referencesInterface() && Repository.implementationOf(((ObjectType)this).getClassName(), ((ObjectType)referenceType).getClassName()))
        return true; 
    } 
    if (this instanceof ObjectType && ((ObjectType)this).referencesInterface()) {
      if (referenceType instanceof ObjectType && ((ObjectType)referenceType).referencesClass() && referenceType.equals(Type.OBJECT))
        return true; 
      if (referenceType instanceof ObjectType && ((ObjectType)referenceType).referencesInterface()) {
        if (equals(referenceType))
          return true; 
        if (Repository.implementationOf(((ObjectType)this).getClassName(), ((ObjectType)referenceType).getClassName()))
          return true; 
      } 
    } 
    if (this instanceof ArrayType) {
      if (referenceType instanceof ObjectType && ((ObjectType)referenceType).referencesClass() && referenceType.equals(Type.OBJECT))
        return true; 
      if (referenceType instanceof ArrayType) {
        Type type1 = ((ArrayType)this).getElementType();
        Type type2 = ((ArrayType)this).getElementType();
        if (type1 instanceof BasicType && type2 instanceof BasicType && type1.equals(type2))
          return true; 
        if (type2 instanceof ReferenceType && type1 instanceof ReferenceType && ((ReferenceType)type1).isAssignmentCompatibleWith((ReferenceType)type2))
          return true; 
      } 
      if (referenceType instanceof ObjectType && ((ObjectType)referenceType).referencesInterface())
        for (byte b = 0; b < Constants.INTERFACES_IMPLEMENTED_BY_ARRAYS.length; b++) {
          if (referenceType.equals(new ObjectType(Constants.INTERFACES_IMPLEMENTED_BY_ARRAYS[b])))
            return true; 
        }  
    } 
    return false;
  }
  
  public ReferenceType getFirstCommonSuperclass(ReferenceType paramReferenceType) {
    if (equals(Type.NULL))
      return paramReferenceType; 
    if (paramReferenceType.equals(Type.NULL))
      return this; 
    if (equals(paramReferenceType))
      return this; 
    if (this instanceof ArrayType && paramReferenceType instanceof ArrayType) {
      ArrayType arrayType1 = (ArrayType)this;
      ArrayType arrayType2 = (ArrayType)paramReferenceType;
      if (arrayType1.getDimensions() == arrayType2.getDimensions() && arrayType1.getBasicType() instanceof ObjectType && arrayType2.getBasicType() instanceof ObjectType)
        return new ArrayType(((ObjectType)arrayType1.getBasicType()).getFirstCommonSuperclass((ObjectType)arrayType2.getBasicType()), arrayType1.getDimensions()); 
    } 
    if (this instanceof ArrayType || paramReferenceType instanceof ArrayType)
      return Type.OBJECT; 
    if ((this instanceof ObjectType && ((ObjectType)this).referencesInterface()) || (paramReferenceType instanceof ObjectType && ((ObjectType)paramReferenceType).referencesInterface()))
      return Type.OBJECT; 
    ObjectType objectType1 = (ObjectType)this;
    ObjectType objectType2 = (ObjectType)paramReferenceType;
    JavaClass[] arrayOfJavaClass1 = Repository.getSuperClasses(objectType1.getClassName());
    JavaClass[] arrayOfJavaClass2 = Repository.getSuperClasses(objectType2.getClassName());
    if (arrayOfJavaClass1 == null || arrayOfJavaClass2 == null)
      return null; 
    JavaClass[] arrayOfJavaClass3 = new JavaClass[arrayOfJavaClass1.length + 1];
    JavaClass[] arrayOfJavaClass4 = new JavaClass[arrayOfJavaClass2.length + 1];
    System.arraycopy(arrayOfJavaClass1, 0, arrayOfJavaClass3, 1, arrayOfJavaClass1.length);
    System.arraycopy(arrayOfJavaClass2, 0, arrayOfJavaClass4, 1, arrayOfJavaClass2.length);
    arrayOfJavaClass3[0] = Repository.lookupClass(objectType1.getClassName());
    arrayOfJavaClass4[0] = Repository.lookupClass(objectType2.getClassName());
    for (byte b = 0; b < arrayOfJavaClass4.length; b++) {
      for (byte b1 = 0; b1 < arrayOfJavaClass3.length; b1++) {
        if (arrayOfJavaClass3[b1].equals(arrayOfJavaClass4[b]))
          return new ObjectType(arrayOfJavaClass3[b1].getClassName()); 
      } 
    } 
    return null;
  }
  
  public ReferenceType firstCommonSuperclass(ReferenceType paramReferenceType) {
    if (equals(Type.NULL))
      return paramReferenceType; 
    if (paramReferenceType.equals(Type.NULL))
      return this; 
    if (equals(paramReferenceType))
      return this; 
    if (this instanceof ArrayType || paramReferenceType instanceof ArrayType)
      return Type.OBJECT; 
    if ((this instanceof ObjectType && ((ObjectType)this).referencesInterface()) || (paramReferenceType instanceof ObjectType && ((ObjectType)paramReferenceType).referencesInterface()))
      return Type.OBJECT; 
    ObjectType objectType1 = (ObjectType)this;
    ObjectType objectType2 = (ObjectType)paramReferenceType;
    JavaClass[] arrayOfJavaClass1 = Repository.getSuperClasses(objectType1.getClassName());
    JavaClass[] arrayOfJavaClass2 = Repository.getSuperClasses(objectType2.getClassName());
    if (arrayOfJavaClass1 == null || arrayOfJavaClass2 == null)
      return null; 
    JavaClass[] arrayOfJavaClass3 = new JavaClass[arrayOfJavaClass1.length + 1];
    JavaClass[] arrayOfJavaClass4 = new JavaClass[arrayOfJavaClass2.length + 1];
    System.arraycopy(arrayOfJavaClass1, 0, arrayOfJavaClass3, 1, arrayOfJavaClass1.length);
    System.arraycopy(arrayOfJavaClass2, 0, arrayOfJavaClass4, 1, arrayOfJavaClass2.length);
    arrayOfJavaClass3[0] = Repository.lookupClass(objectType1.getClassName());
    arrayOfJavaClass4[0] = Repository.lookupClass(objectType2.getClassName());
    for (byte b = 0; b < arrayOfJavaClass4.length; b++) {
      for (byte b1 = 0; b1 < arrayOfJavaClass3.length; b1++) {
        if (arrayOfJavaClass3[b1].equals(arrayOfJavaClass4[b]))
          return new ObjectType(arrayOfJavaClass3[b1].getClassName()); 
      } 
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\ReferenceType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */