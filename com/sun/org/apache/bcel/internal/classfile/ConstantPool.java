package com.sun.org.apache.bcel.internal.classfile;

import com.sun.org.apache.bcel.internal.Constants;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class ConstantPool implements Cloneable, Node, Serializable {
  private int constant_pool_count;
  
  private Constant[] constant_pool;
  
  public ConstantPool(Constant[] paramArrayOfConstant) { setConstantPool(paramArrayOfConstant); }
  
  ConstantPool(DataInputStream paramDataInputStream) throws IOException, ClassFormatException {
    this.constant_pool_count = paramDataInputStream.readUnsignedShort();
    this.constant_pool = new Constant[this.constant_pool_count];
    for (byte b = 1; b < this.constant_pool_count; b++) {
      this.constant_pool[b] = Constant.readConstant(paramDataInputStream);
      byte b1 = this.constant_pool[b].getTag();
      if (b1 == 6 || b1 == 5)
        b++; 
    } 
  }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitConstantPool(this); }
  
  public String constantToString(Constant paramConstant) throws ClassFormatException {
    int i;
    byte b = paramConstant.getTag();
    switch (b) {
      case 7:
        i = ((ConstantClass)paramConstant).getNameIndex();
        paramConstant = getConstant(i, (byte)1);
        return Utility.compactClassName(((ConstantUtf8)paramConstant).getBytes(), false);
      case 8:
        i = ((ConstantString)paramConstant).getStringIndex();
        paramConstant = getConstant(i, (byte)1);
        return "\"" + escape(((ConstantUtf8)paramConstant).getBytes()) + "\"";
      case 1:
        return ((ConstantUtf8)paramConstant).getBytes();
      case 6:
        return "" + ((ConstantDouble)paramConstant).getBytes();
      case 4:
        return "" + ((ConstantFloat)paramConstant).getBytes();
      case 5:
        return "" + ((ConstantLong)paramConstant).getBytes();
      case 3:
        return "" + ((ConstantInteger)paramConstant).getBytes();
      case 12:
        return constantToString(((ConstantNameAndType)paramConstant).getNameIndex(), (byte)1) + " " + constantToString(((ConstantNameAndType)paramConstant).getSignatureIndex(), (byte)1);
      case 9:
      case 10:
      case 11:
        return constantToString(((ConstantCP)paramConstant).getClassIndex(), (byte)7) + "." + constantToString(((ConstantCP)paramConstant).getNameAndTypeIndex(), (byte)12);
    } 
    throw new RuntimeException("Unknown constant type " + b);
  }
  
  private static final String escape(String paramString) {
    int i = paramString.length();
    StringBuffer stringBuffer = new StringBuffer(i + 5);
    char[] arrayOfChar = paramString.toCharArray();
    for (byte b = 0; b < i; b++) {
      switch (arrayOfChar[b]) {
        case '\n':
          stringBuffer.append("\\n");
          break;
        case '\r':
          stringBuffer.append("\\r");
          break;
        case '\t':
          stringBuffer.append("\\t");
          break;
        case '\b':
          stringBuffer.append("\\b");
          break;
        case '"':
          stringBuffer.append("\\\"");
          break;
        default:
          stringBuffer.append(arrayOfChar[b]);
          break;
      } 
    } 
    return stringBuffer.toString();
  }
  
  public String constantToString(int paramInt, byte paramByte) throws ClassFormatException {
    Constant constant = getConstant(paramInt, paramByte);
    return constantToString(constant);
  }
  
  public void dump(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeShort(this.constant_pool_count);
    for (byte b = 1; b < this.constant_pool_count; b++) {
      if (this.constant_pool[b] != null)
        this.constant_pool[b].dump(paramDataOutputStream); 
    } 
  }
  
  public Constant getConstant(int paramInt) {
    if (paramInt >= this.constant_pool.length || paramInt < 0)
      throw new ClassFormatException("Invalid constant pool reference: " + paramInt + ". Constant pool size is: " + this.constant_pool.length); 
    return this.constant_pool[paramInt];
  }
  
  public Constant getConstant(int paramInt, byte paramByte) throws ClassFormatException {
    Constant constant = getConstant(paramInt);
    if (constant == null)
      throw new ClassFormatException("Constant pool at index " + paramInt + " is null."); 
    if (constant.getTag() == paramByte)
      return constant; 
    throw new ClassFormatException("Expected class `" + Constants.CONSTANT_NAMES[paramByte] + "' at index " + paramInt + " and got " + constant);
  }
  
  public Constant[] getConstantPool() { return this.constant_pool; }
  
  public String getConstantString(int paramInt, byte paramByte) throws ClassFormatException {
    int i;
    Constant constant = getConstant(paramInt, paramByte);
    switch (paramByte) {
      case 7:
        i = ((ConstantClass)constant).getNameIndex();
        constant = getConstant(i, (byte)1);
        return ((ConstantUtf8)constant).getBytes();
      case 8:
        i = ((ConstantString)constant).getStringIndex();
        constant = getConstant(i, (byte)1);
        return ((ConstantUtf8)constant).getBytes();
    } 
    throw new RuntimeException("getConstantString called with illegal tag " + paramByte);
  }
  
  public int getLength() { return this.constant_pool_count; }
  
  public void setConstant(int paramInt, Constant paramConstant) { this.constant_pool[paramInt] = paramConstant; }
  
  public void setConstantPool(Constant[] paramArrayOfConstant) {
    this.constant_pool = paramArrayOfConstant;
    this.constant_pool_count = (paramArrayOfConstant == null) ? 0 : paramArrayOfConstant.length;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 1; b < this.constant_pool_count; b++)
      stringBuffer.append(b + ")" + this.constant_pool[b] + "\n"); 
    return stringBuffer.toString();
  }
  
  public ConstantPool copy() {
    ConstantPool constantPool = null;
    try {
      constantPool = (ConstantPool)clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {}
    constantPool.constant_pool = new Constant[this.constant_pool_count];
    for (byte b = 1; b < this.constant_pool_count; b++) {
      if (this.constant_pool[b] != null)
        constantPool.constant_pool[b] = this.constant_pool[b].copy(); 
    } 
    return constantPool;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ConstantPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */