package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.Constants;

public final class BasicType extends Type {
  BasicType(byte paramByte) {
    super(paramByte, Constants.SHORT_TYPE_NAMES[paramByte]);
    if (paramByte < 4 || paramByte > 12)
      throw new ClassGenException("Invalid type: " + paramByte); 
  }
  
  public static final BasicType getType(byte paramByte) {
    switch (paramByte) {
      case 12:
        return VOID;
      case 4:
        return BOOLEAN;
      case 8:
        return BYTE;
      case 9:
        return SHORT;
      case 5:
        return CHAR;
      case 10:
        return INT;
      case 11:
        return LONG;
      case 7:
        return DOUBLE;
      case 6:
        return FLOAT;
    } 
    throw new ClassGenException("Invalid type: " + paramByte);
  }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof BasicType) ? ((((BasicType)paramObject).type == this.type)) : false; }
  
  public int hashCode() { return this.type; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\BasicType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */