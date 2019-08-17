package jdk.internal.org.objectweb.asm.tree.analysis;

import jdk.internal.org.objectweb.asm.Type;

public class BasicValue implements Value {
  public static final BasicValue UNINITIALIZED_VALUE = new BasicValue(null);
  
  public static final BasicValue INT_VALUE = new BasicValue(Type.INT_TYPE);
  
  public static final BasicValue FLOAT_VALUE = new BasicValue(Type.FLOAT_TYPE);
  
  public static final BasicValue LONG_VALUE = new BasicValue(Type.LONG_TYPE);
  
  public static final BasicValue DOUBLE_VALUE = new BasicValue(Type.DOUBLE_TYPE);
  
  public static final BasicValue REFERENCE_VALUE = new BasicValue(Type.getObjectType("java/lang/Object"));
  
  public static final BasicValue RETURNADDRESS_VALUE = new BasicValue(Type.VOID_TYPE);
  
  private final Type type;
  
  public BasicValue(Type paramType) { this.type = paramType; }
  
  public Type getType() { return this.type; }
  
  public int getSize() { return (this.type == Type.LONG_TYPE || this.type == Type.DOUBLE_TYPE) ? 2 : 1; }
  
  public boolean isReference() { return (this.type != null && (this.type.getSort() == 10 || this.type.getSort() == 9)); }
  
  public boolean equals(Object paramObject) { return (paramObject == this) ? true : ((paramObject instanceof BasicValue) ? ((this.type == null) ? ((((BasicValue)paramObject).type == null)) : this.type.equals(((BasicValue)paramObject).type)) : false); }
  
  public int hashCode() { return (this.type == null) ? 0 : this.type.hashCode(); }
  
  public String toString() { return (this == UNINITIALIZED_VALUE) ? "." : ((this == RETURNADDRESS_VALUE) ? "A" : ((this == REFERENCE_VALUE) ? "R" : this.type.getDescriptor())); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\analysis\BasicValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */