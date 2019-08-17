package com.sun.org.apache.bcel.internal.classfile;

import com.sun.org.apache.bcel.internal.Constants;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

public abstract class Attribute implements Cloneable, Node, Serializable {
  protected int name_index;
  
  protected int length;
  
  protected byte tag;
  
  protected ConstantPool constant_pool;
  
  private static HashMap readers = new HashMap();
  
  protected Attribute(byte paramByte, int paramInt1, int paramInt2, ConstantPool paramConstantPool) {
    this.tag = paramByte;
    this.name_index = paramInt1;
    this.length = paramInt2;
    this.constant_pool = paramConstantPool;
  }
  
  public abstract void accept(Visitor paramVisitor);
  
  public void dump(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeShort(this.name_index);
    paramDataOutputStream.writeInt(this.length);
  }
  
  public static void addAttributeReader(String paramString, AttributeReader paramAttributeReader) { readers.put(paramString, paramAttributeReader); }
  
  public static void removeAttributeReader(String paramString) { readers.remove(paramString); }
  
  public static final Attribute readAttribute(DataInputStream paramDataInputStream, ConstantPool paramConstantPool) throws IOException, ClassFormatException {
    AttributeReader attributeReader;
    byte b1 = -1;
    int i = paramDataInputStream.readUnsignedShort();
    ConstantUtf8 constantUtf8 = (ConstantUtf8)paramConstantPool.getConstant(i, (byte)1);
    String str = constantUtf8.getBytes();
    int j = paramDataInputStream.readInt();
    byte b2;
    for (b2 = 0; b2 < 13; b2 = (byte)(b2 + 1)) {
      if (str.equals(Constants.ATTRIBUTE_NAMES[b2])) {
        b1 = b2;
        break;
      } 
    } 
    switch (b1) {
      case -1:
        attributeReader = (AttributeReader)readers.get(str);
        return (attributeReader != null) ? attributeReader.createAttribute(i, j, paramDataInputStream, paramConstantPool) : new Unknown(i, j, paramDataInputStream, paramConstantPool);
      case 1:
        return new ConstantValue(i, j, paramDataInputStream, paramConstantPool);
      case 0:
        return new SourceFile(i, j, paramDataInputStream, paramConstantPool);
      case 2:
        return new Code(i, j, paramDataInputStream, paramConstantPool);
      case 3:
        return new ExceptionTable(i, j, paramDataInputStream, paramConstantPool);
      case 4:
        return new LineNumberTable(i, j, paramDataInputStream, paramConstantPool);
      case 5:
        return new LocalVariableTable(i, j, paramDataInputStream, paramConstantPool);
      case 12:
        return new LocalVariableTypeTable(i, j, paramDataInputStream, paramConstantPool);
      case 6:
        return new InnerClasses(i, j, paramDataInputStream, paramConstantPool);
      case 7:
        return new Synthetic(i, j, paramDataInputStream, paramConstantPool);
      case 8:
        return new Deprecated(i, j, paramDataInputStream, paramConstantPool);
      case 9:
        return new PMGClass(i, j, paramDataInputStream, paramConstantPool);
      case 10:
        return new Signature(i, j, paramDataInputStream, paramConstantPool);
      case 11:
        return new StackMap(i, j, paramDataInputStream, paramConstantPool);
    } 
    throw new IllegalStateException("Ooops! default case reached.");
  }
  
  public final int getLength() { return this.length; }
  
  public final void setLength(int paramInt) { this.length = paramInt; }
  
  public final void setNameIndex(int paramInt) { this.name_index = paramInt; }
  
  public final int getNameIndex() { return this.name_index; }
  
  public final byte getTag() { return this.tag; }
  
  public final ConstantPool getConstantPool() { return this.constant_pool; }
  
  public final void setConstantPool(ConstantPool paramConstantPool) { this.constant_pool = paramConstantPool; }
  
  public Object clone() {
    Object object = null;
    try {
      object = super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      cloneNotSupportedException.printStackTrace();
    } 
    return object;
  }
  
  public abstract Attribute copy(ConstantPool paramConstantPool);
  
  public String toString() { return Constants.ATTRIBUTE_NAMES[this.tag]; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\Attribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */