package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.Attribute;
import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.classfile.ConstantObject;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.ConstantValue;
import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.util.ArrayList;
import java.util.Iterator;

public class FieldGen extends FieldGenOrMethodGen {
  private Object value = null;
  
  private ArrayList observers;
  
  public FieldGen(int paramInt, Type paramType, String paramString, ConstantPoolGen paramConstantPoolGen) {
    setAccessFlags(paramInt);
    setType(paramType);
    setName(paramString);
    setConstantPool(paramConstantPoolGen);
  }
  
  public FieldGen(Field paramField, ConstantPoolGen paramConstantPoolGen) {
    this(paramField.getAccessFlags(), Type.getType(paramField.getSignature()), paramField.getName(), paramConstantPoolGen);
    Attribute[] arrayOfAttribute = paramField.getAttributes();
    for (byte b = 0; b < arrayOfAttribute.length; b++) {
      if (arrayOfAttribute[b] instanceof ConstantValue) {
        setValue(((ConstantValue)arrayOfAttribute[b]).getConstantValueIndex());
      } else {
        addAttribute(arrayOfAttribute[b]);
      } 
    } 
  }
  
  private void setValue(int paramInt) {
    ConstantPool constantPool = this.cp.getConstantPool();
    Constant constant = constantPool.getConstant(paramInt);
    this.value = ((ConstantObject)constant).getConstantValue(constantPool);
  }
  
  public void setInitValue(String paramString) {
    checkType(new ObjectType("java.lang.String"));
    if (paramString != null)
      this.value = paramString; 
  }
  
  public void setInitValue(long paramLong) {
    checkType(Type.LONG);
    if (paramLong != 0L)
      this.value = new Long(paramLong); 
  }
  
  public void setInitValue(int paramInt) {
    checkType(Type.INT);
    if (paramInt != 0)
      this.value = new Integer(paramInt); 
  }
  
  public void setInitValue(short paramShort) {
    checkType(Type.SHORT);
    if (paramShort != 0)
      this.value = new Integer(paramShort); 
  }
  
  public void setInitValue(char paramChar) {
    checkType(Type.CHAR);
    if (paramChar != '\000')
      this.value = new Integer(paramChar); 
  }
  
  public void setInitValue(byte paramByte) {
    checkType(Type.BYTE);
    if (paramByte != 0)
      this.value = new Integer(paramByte); 
  }
  
  public void setInitValue(boolean paramBoolean) {
    checkType(Type.BOOLEAN);
    if (paramBoolean)
      this.value = new Integer(1); 
  }
  
  public void setInitValue(float paramFloat) {
    checkType(Type.FLOAT);
    if (paramFloat != 0.0D)
      this.value = new Float(paramFloat); 
  }
  
  public void setInitValue(double paramDouble) {
    checkType(Type.DOUBLE);
    if (paramDouble != 0.0D)
      this.value = new Double(paramDouble); 
  }
  
  public void cancelInitValue() { this.value = null; }
  
  private void checkType(Type paramType) {
    if (this.type == null)
      throw new ClassGenException("You haven't defined the type of the field yet"); 
    if (!isFinal())
      throw new ClassGenException("Only final fields may have an initial value!"); 
    if (!this.type.equals(paramType))
      throw new ClassGenException("Types are not compatible: " + this.type + " vs. " + paramType); 
  }
  
  public Field getField() {
    String str = getSignature();
    int i = this.cp.addUtf8(this.name);
    int j = this.cp.addUtf8(str);
    if (this.value != null) {
      checkType(this.type);
      int k = addConstant();
      addAttribute(new ConstantValue(this.cp.addUtf8("ConstantValue"), 2, k, this.cp.getConstantPool()));
    } 
    return new Field(this.access_flags, i, j, getAttributes(), this.cp.getConstantPool());
  }
  
  private int addConstant() {
    switch (this.type.getType()) {
      case 4:
      case 5:
      case 8:
      case 9:
      case 10:
        return this.cp.addInteger(((Integer)this.value).intValue());
      case 6:
        return this.cp.addFloat(((Float)this.value).floatValue());
      case 7:
        return this.cp.addDouble(((Double)this.value).doubleValue());
      case 11:
        return this.cp.addLong(((Long)this.value).longValue());
      case 14:
        return this.cp.addString((String)this.value);
    } 
    throw new RuntimeException("Oops: Unhandled : " + this.type.getType());
  }
  
  public String getSignature() { return this.type.getSignature(); }
  
  public void addObserver(FieldObserver paramFieldObserver) {
    if (this.observers == null)
      this.observers = new ArrayList(); 
    this.observers.add(paramFieldObserver);
  }
  
  public void removeObserver(FieldObserver paramFieldObserver) {
    if (this.observers != null)
      this.observers.remove(paramFieldObserver); 
  }
  
  public void update() {
    if (this.observers != null) {
      Iterator iterator = this.observers.iterator();
      while (iterator.hasNext())
        ((FieldObserver)iterator.next()).notify(this); 
    } 
  }
  
  public String getInitValue() { return (this.value != null) ? this.value.toString() : null; }
  
  public final String toString() {
    String str3 = Utility.accessToString(this.access_flags);
    str3 = str3.equals("") ? "" : (str3 + " ");
    String str2 = this.type.toString();
    String str1 = getName();
    StringBuffer stringBuffer = new StringBuffer(str3 + str2 + " " + str1);
    String str4 = getInitValue();
    if (str4 != null)
      stringBuffer.append(" = " + str4); 
    return stringBuffer.toString();
  }
  
  public FieldGen copy(ConstantPoolGen paramConstantPoolGen) {
    FieldGen fieldGen = (FieldGen)clone();
    fieldGen.setConstantPool(paramConstantPoolGen);
    return fieldGen;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\FieldGen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */