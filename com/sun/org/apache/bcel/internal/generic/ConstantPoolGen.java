package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.classfile.ConstantCP;
import com.sun.org.apache.bcel.internal.classfile.ConstantClass;
import com.sun.org.apache.bcel.internal.classfile.ConstantDouble;
import com.sun.org.apache.bcel.internal.classfile.ConstantFieldref;
import com.sun.org.apache.bcel.internal.classfile.ConstantFloat;
import com.sun.org.apache.bcel.internal.classfile.ConstantInteger;
import com.sun.org.apache.bcel.internal.classfile.ConstantInterfaceMethodref;
import com.sun.org.apache.bcel.internal.classfile.ConstantLong;
import com.sun.org.apache.bcel.internal.classfile.ConstantMethodref;
import com.sun.org.apache.bcel.internal.classfile.ConstantNameAndType;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.ConstantString;
import com.sun.org.apache.bcel.internal.classfile.ConstantUtf8;
import java.io.Serializable;
import java.util.HashMap;

public class ConstantPoolGen implements Serializable {
  protected int size = 1024;
  
  protected Constant[] constants = new Constant[this.size];
  
  protected int index = 1;
  
  private static final String METHODREF_DELIM = ":";
  
  private static final String IMETHODREF_DELIM = "#";
  
  private static final String FIELDREF_DELIM = "&";
  
  private static final String NAT_DELIM = "%";
  
  private HashMap string_table = new HashMap();
  
  private HashMap class_table = new HashMap();
  
  private HashMap utf8_table = new HashMap();
  
  private HashMap n_a_t_table = new HashMap();
  
  private HashMap cp_table = new HashMap();
  
  public ConstantPoolGen(Constant[] paramArrayOfConstant) {
    if (paramArrayOfConstant.length > this.size) {
      this.size = paramArrayOfConstant.length;
      this.constants = new Constant[this.size];
    } 
    System.arraycopy(paramArrayOfConstant, 0, this.constants, 0, paramArrayOfConstant.length);
    if (paramArrayOfConstant.length > 0)
      this.index = paramArrayOfConstant.length; 
    for (byte b = 1; b < this.index; b++) {
      Constant constant = this.constants[b];
      if (constant instanceof ConstantString) {
        ConstantString constantString = (ConstantString)constant;
        ConstantUtf8 constantUtf8 = (ConstantUtf8)this.constants[constantString.getStringIndex()];
        this.string_table.put(constantUtf8.getBytes(), new Index(b));
      } else if (constant instanceof ConstantClass) {
        ConstantClass constantClass = (ConstantClass)constant;
        ConstantUtf8 constantUtf8 = (ConstantUtf8)this.constants[constantClass.getNameIndex()];
        this.class_table.put(constantUtf8.getBytes(), new Index(b));
      } else if (constant instanceof ConstantNameAndType) {
        ConstantNameAndType constantNameAndType = (ConstantNameAndType)constant;
        ConstantUtf8 constantUtf81 = (ConstantUtf8)this.constants[constantNameAndType.getNameIndex()];
        ConstantUtf8 constantUtf82 = (ConstantUtf8)this.constants[constantNameAndType.getSignatureIndex()];
        this.n_a_t_table.put(constantUtf81.getBytes() + "%" + constantUtf82.getBytes(), new Index(b));
      } else if (constant instanceof ConstantUtf8) {
        ConstantUtf8 constantUtf8 = (ConstantUtf8)constant;
        this.utf8_table.put(constantUtf8.getBytes(), new Index(b));
      } else if (constant instanceof ConstantCP) {
        ConstantCP constantCP = (ConstantCP)constant;
        ConstantClass constantClass = (ConstantClass)this.constants[constantCP.getClassIndex()];
        ConstantNameAndType constantNameAndType = (ConstantNameAndType)this.constants[constantCP.getNameAndTypeIndex()];
        ConstantUtf8 constantUtf8 = (ConstantUtf8)this.constants[constantClass.getNameIndex()];
        String str1 = constantUtf8.getBytes().replace('/', '.');
        constantUtf8 = (ConstantUtf8)this.constants[constantNameAndType.getNameIndex()];
        String str2 = constantUtf8.getBytes();
        constantUtf8 = (ConstantUtf8)this.constants[constantNameAndType.getSignatureIndex()];
        String str3 = constantUtf8.getBytes();
        String str4 = ":";
        if (constant instanceof ConstantInterfaceMethodref) {
          str4 = "#";
        } else if (constant instanceof ConstantFieldref) {
          str4 = "&";
        } 
        this.cp_table.put(str1 + str4 + str2 + str4 + str3, new Index(b));
      } 
    } 
  }
  
  public ConstantPoolGen(ConstantPool paramConstantPool) { this(paramConstantPool.getConstantPool()); }
  
  public ConstantPoolGen() {}
  
  protected void adjustSize() {
    if (this.index + 3 >= this.size) {
      Constant[] arrayOfConstant = this.constants;
      this.size *= 2;
      this.constants = new Constant[this.size];
      System.arraycopy(arrayOfConstant, 0, this.constants, 0, this.index);
    } 
  }
  
  public int lookupString(String paramString) {
    Index index1 = (Index)this.string_table.get(paramString);
    return (index1 != null) ? index1.index : -1;
  }
  
  public int addString(String paramString) {
    int i;
    if ((i = lookupString(paramString)) != -1)
      return i; 
    int j = addUtf8(paramString);
    adjustSize();
    ConstantString constantString = new ConstantString(j);
    i = this.index;
    this.constants[this.index++] = constantString;
    this.string_table.put(paramString, new Index(i));
    return i;
  }
  
  public int lookupClass(String paramString) {
    Index index1 = (Index)this.class_table.get(paramString.replace('.', '/'));
    return (index1 != null) ? index1.index : -1;
  }
  
  private int addClass_(String paramString) {
    int i;
    if ((i = lookupClass(paramString)) != -1)
      return i; 
    adjustSize();
    ConstantClass constantClass = new ConstantClass(addUtf8(paramString));
    i = this.index;
    this.constants[this.index++] = constantClass;
    this.class_table.put(paramString, new Index(i));
    return i;
  }
  
  public int addClass(String paramString) { return addClass_(paramString.replace('.', '/')); }
  
  public int addClass(ObjectType paramObjectType) { return addClass(paramObjectType.getClassName()); }
  
  public int addArrayClass(ArrayType paramArrayType) { return addClass_(paramArrayType.getSignature()); }
  
  public int lookupInteger(int paramInt) {
    for (byte b = 1; b < this.index; b++) {
      if (this.constants[b] instanceof ConstantInteger) {
        ConstantInteger constantInteger = (ConstantInteger)this.constants[b];
        if (constantInteger.getBytes() == paramInt)
          return b; 
      } 
    } 
    return -1;
  }
  
  public int addInteger(int paramInt) {
    int i;
    if ((i = lookupInteger(paramInt)) != -1)
      return i; 
    adjustSize();
    i = this.index;
    this.constants[this.index++] = new ConstantInteger(paramInt);
    return i;
  }
  
  public int lookupFloat(float paramFloat) {
    int i = Float.floatToIntBits(paramFloat);
    for (byte b = 1; b < this.index; b++) {
      if (this.constants[b] instanceof ConstantFloat) {
        ConstantFloat constantFloat = (ConstantFloat)this.constants[b];
        if (Float.floatToIntBits(constantFloat.getBytes()) == i)
          return b; 
      } 
    } 
    return -1;
  }
  
  public int addFloat(float paramFloat) {
    int i;
    if ((i = lookupFloat(paramFloat)) != -1)
      return i; 
    adjustSize();
    i = this.index;
    this.constants[this.index++] = new ConstantFloat(paramFloat);
    return i;
  }
  
  public int lookupUtf8(String paramString) {
    Index index1 = (Index)this.utf8_table.get(paramString);
    return (index1 != null) ? index1.index : -1;
  }
  
  public int addUtf8(String paramString) {
    int i;
    if ((i = lookupUtf8(paramString)) != -1)
      return i; 
    adjustSize();
    i = this.index;
    this.constants[this.index++] = new ConstantUtf8(paramString);
    this.utf8_table.put(paramString, new Index(i));
    return i;
  }
  
  public int lookupLong(long paramLong) {
    for (byte b = 1; b < this.index; b++) {
      if (this.constants[b] instanceof ConstantLong) {
        ConstantLong constantLong = (ConstantLong)this.constants[b];
        if (constantLong.getBytes() == paramLong)
          return b; 
      } 
    } 
    return -1;
  }
  
  public int addLong(long paramLong) {
    int i;
    if ((i = lookupLong(paramLong)) != -1)
      return i; 
    adjustSize();
    i = this.index;
    this.constants[this.index] = new ConstantLong(paramLong);
    this.index += 2;
    return i;
  }
  
  public int lookupDouble(double paramDouble) {
    long l = Double.doubleToLongBits(paramDouble);
    for (byte b = 1; b < this.index; b++) {
      if (this.constants[b] instanceof ConstantDouble) {
        ConstantDouble constantDouble = (ConstantDouble)this.constants[b];
        if (Double.doubleToLongBits(constantDouble.getBytes()) == l)
          return b; 
      } 
    } 
    return -1;
  }
  
  public int addDouble(double paramDouble) {
    int i;
    if ((i = lookupDouble(paramDouble)) != -1)
      return i; 
    adjustSize();
    i = this.index;
    this.constants[this.index] = new ConstantDouble(paramDouble);
    this.index += 2;
    return i;
  }
  
  public int lookupNameAndType(String paramString1, String paramString2) {
    Index index1 = (Index)this.n_a_t_table.get(paramString1 + "%" + paramString2);
    return (index1 != null) ? index1.index : -1;
  }
  
  public int addNameAndType(String paramString1, String paramString2) {
    int i;
    if ((i = lookupNameAndType(paramString1, paramString2)) != -1)
      return i; 
    adjustSize();
    int j = addUtf8(paramString1);
    int k = addUtf8(paramString2);
    i = this.index;
    this.constants[this.index++] = new ConstantNameAndType(j, k);
    this.n_a_t_table.put(paramString1 + "%" + paramString2, new Index(i));
    return i;
  }
  
  public int lookupMethodref(String paramString1, String paramString2, String paramString3) {
    Index index1 = (Index)this.cp_table.get(paramString1 + ":" + paramString2 + ":" + paramString3);
    return (index1 != null) ? index1.index : -1;
  }
  
  public int lookupMethodref(MethodGen paramMethodGen) { return lookupMethodref(paramMethodGen.getClassName(), paramMethodGen.getName(), paramMethodGen.getSignature()); }
  
  public int addMethodref(String paramString1, String paramString2, String paramString3) {
    int i;
    if ((i = lookupMethodref(paramString1, paramString2, paramString3)) != -1)
      return i; 
    adjustSize();
    int k = addNameAndType(paramString2, paramString3);
    int j = addClass(paramString1);
    i = this.index;
    this.constants[this.index++] = new ConstantMethodref(j, k);
    this.cp_table.put(paramString1 + ":" + paramString2 + ":" + paramString3, new Index(i));
    return i;
  }
  
  public int addMethodref(MethodGen paramMethodGen) { return addMethodref(paramMethodGen.getClassName(), paramMethodGen.getName(), paramMethodGen.getSignature()); }
  
  public int lookupInterfaceMethodref(String paramString1, String paramString2, String paramString3) {
    Index index1 = (Index)this.cp_table.get(paramString1 + "#" + paramString2 + "#" + paramString3);
    return (index1 != null) ? index1.index : -1;
  }
  
  public int lookupInterfaceMethodref(MethodGen paramMethodGen) { return lookupInterfaceMethodref(paramMethodGen.getClassName(), paramMethodGen.getName(), paramMethodGen.getSignature()); }
  
  public int addInterfaceMethodref(String paramString1, String paramString2, String paramString3) {
    int i;
    if ((i = lookupInterfaceMethodref(paramString1, paramString2, paramString3)) != -1)
      return i; 
    adjustSize();
    int j = addClass(paramString1);
    int k = addNameAndType(paramString2, paramString3);
    i = this.index;
    this.constants[this.index++] = new ConstantInterfaceMethodref(j, k);
    this.cp_table.put(paramString1 + "#" + paramString2 + "#" + paramString3, new Index(i));
    return i;
  }
  
  public int addInterfaceMethodref(MethodGen paramMethodGen) { return addInterfaceMethodref(paramMethodGen.getClassName(), paramMethodGen.getName(), paramMethodGen.getSignature()); }
  
  public int lookupFieldref(String paramString1, String paramString2, String paramString3) {
    Index index1 = (Index)this.cp_table.get(paramString1 + "&" + paramString2 + "&" + paramString3);
    return (index1 != null) ? index1.index : -1;
  }
  
  public int addFieldref(String paramString1, String paramString2, String paramString3) {
    int i;
    if ((i = lookupFieldref(paramString1, paramString2, paramString3)) != -1)
      return i; 
    adjustSize();
    int j = addClass(paramString1);
    int k = addNameAndType(paramString2, paramString3);
    i = this.index;
    this.constants[this.index++] = new ConstantFieldref(j, k);
    this.cp_table.put(paramString1 + "&" + paramString2 + "&" + paramString3, new Index(i));
    return i;
  }
  
  public Constant getConstant(int paramInt) { return this.constants[paramInt]; }
  
  public void setConstant(int paramInt, Constant paramConstant) { this.constants[paramInt] = paramConstant; }
  
  public ConstantPool getConstantPool() { return new ConstantPool(this.constants); }
  
  public int getSize() { return this.index; }
  
  public ConstantPool getFinalConstantPool() {
    Constant[] arrayOfConstant = new Constant[this.index];
    System.arraycopy(this.constants, 0, arrayOfConstant, 0, this.index);
    return new ConstantPool(arrayOfConstant);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 1; b < this.index; b++)
      stringBuffer.append(b + ")" + this.constants[b] + "\n"); 
    return stringBuffer.toString();
  }
  
  public int addConstant(Constant paramConstant, ConstantPoolGen paramConstantPoolGen) {
    String str3;
    String str2;
    String str1;
    ConstantUtf8 constantUtf83;
    ConstantUtf8 constantUtf82;
    ConstantNameAndType constantNameAndType2;
    ConstantClass constantClass2;
    ConstantUtf8 constantUtf81;
    ConstantNameAndType constantNameAndType1;
    ConstantCP constantCP;
    ConstantClass constantClass1;
    ConstantString constantString;
    Constant[] arrayOfConstant = paramConstantPoolGen.getConstantPool().getConstantPool();
    switch (paramConstant.getTag()) {
      case 8:
        constantString = (ConstantString)paramConstant;
        constantUtf81 = (ConstantUtf8)arrayOfConstant[constantString.getStringIndex()];
        return addString(constantUtf81.getBytes());
      case 7:
        constantClass1 = (ConstantClass)paramConstant;
        constantUtf81 = (ConstantUtf8)arrayOfConstant[constantClass1.getNameIndex()];
        return addClass(constantUtf81.getBytes());
      case 12:
        constantNameAndType1 = (ConstantNameAndType)paramConstant;
        constantUtf81 = (ConstantUtf8)arrayOfConstant[constantNameAndType1.getNameIndex()];
        constantUtf82 = (ConstantUtf8)arrayOfConstant[constantNameAndType1.getSignatureIndex()];
        return addNameAndType(constantUtf81.getBytes(), constantUtf82.getBytes());
      case 1:
        return addUtf8(((ConstantUtf8)paramConstant).getBytes());
      case 6:
        return addDouble(((ConstantDouble)paramConstant).getBytes());
      case 4:
        return addFloat(((ConstantFloat)paramConstant).getBytes());
      case 5:
        return addLong(((ConstantLong)paramConstant).getBytes());
      case 3:
        return addInteger(((ConstantInteger)paramConstant).getBytes());
      case 9:
      case 10:
      case 11:
        constantCP = (ConstantCP)paramConstant;
        constantClass2 = (ConstantClass)arrayOfConstant[constantCP.getClassIndex()];
        constantNameAndType2 = (ConstantNameAndType)arrayOfConstant[constantCP.getNameAndTypeIndex()];
        constantUtf83 = (ConstantUtf8)arrayOfConstant[constantClass2.getNameIndex()];
        str1 = constantUtf83.getBytes().replace('/', '.');
        constantUtf83 = (ConstantUtf8)arrayOfConstant[constantNameAndType2.getNameIndex()];
        str2 = constantUtf83.getBytes();
        constantUtf83 = (ConstantUtf8)arrayOfConstant[constantNameAndType2.getSignatureIndex()];
        str3 = constantUtf83.getBytes();
        switch (paramConstant.getTag()) {
          case 11:
            return addInterfaceMethodref(str1, str2, str3);
          case 10:
            return addMethodref(str1, str2, str3);
          case 9:
            return addFieldref(str1, str2, str3);
        } 
        throw new RuntimeException("Unknown constant type " + paramConstant);
    } 
    throw new RuntimeException("Unknown constant type " + paramConstant);
  }
  
  private static class Index implements Serializable {
    int index;
    
    Index(int param1Int) { this.index = param1Int; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\ConstantPoolGen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */