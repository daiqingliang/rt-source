package com.sun.org.apache.bcel.internal.generic;

public final class ArrayType extends ReferenceType {
  private int dimensions;
  
  private Type basic_type;
  
  public ArrayType(byte paramByte, int paramInt) { this(BasicType.getType(paramByte), paramInt); }
  
  public ArrayType(String paramString, int paramInt) { this(new ObjectType(paramString), paramInt); }
  
  public ArrayType(Type paramType, int paramInt) {
    super((byte)13, "<dummy>");
    if (paramInt < 1 || paramInt > 255)
      throw new ClassGenException("Invalid number of dimensions: " + paramInt); 
    switch (paramType.getType()) {
      case 13:
        arrayType = (ArrayType)paramType;
        this.dimensions = paramInt + arrayType.dimensions;
        this.basic_type = arrayType.basic_type;
        break;
      case 12:
        throw new ClassGenException("Invalid type: void[]");
      default:
        this.dimensions = paramInt;
        this.basic_type = paramType;
        break;
    } 
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < this.dimensions; b++)
      stringBuffer.append('['); 
    stringBuffer.append(this.basic_type.getSignature());
    this.signature = stringBuffer.toString();
  }
  
  public Type getBasicType() { return this.basic_type; }
  
  public Type getElementType() { return (this.dimensions == 1) ? this.basic_type : new ArrayType(this.basic_type, this.dimensions - 1); }
  
  public int getDimensions() { return this.dimensions; }
  
  public int hashCode() { return this.basic_type.hashCode() ^ this.dimensions; }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof ArrayType) {
      ArrayType arrayType = (ArrayType)paramObject;
      return (arrayType.dimensions == this.dimensions && arrayType.basic_type.equals(this.basic_type));
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\ArrayType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */