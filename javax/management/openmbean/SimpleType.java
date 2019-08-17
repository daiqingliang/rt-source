package javax.management.openmbean;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.management.ObjectName;

public final class SimpleType<T> extends OpenType<T> {
  static final long serialVersionUID = 2215577471957694503L;
  
  public static final SimpleType<Void> VOID = new SimpleType(Void.class);
  
  public static final SimpleType<Boolean> BOOLEAN = new SimpleType(Boolean.class);
  
  public static final SimpleType<Character> CHARACTER = new SimpleType(Character.class);
  
  public static final SimpleType<Byte> BYTE = new SimpleType(Byte.class);
  
  public static final SimpleType<Short> SHORT = new SimpleType(Short.class);
  
  public static final SimpleType<Integer> INTEGER = new SimpleType(Integer.class);
  
  public static final SimpleType<Long> LONG = new SimpleType(Long.class);
  
  public static final SimpleType<Float> FLOAT = new SimpleType(Float.class);
  
  public static final SimpleType<Double> DOUBLE = new SimpleType(Double.class);
  
  public static final SimpleType<String> STRING = new SimpleType(String.class);
  
  public static final SimpleType<BigDecimal> BIGDECIMAL = new SimpleType(BigDecimal.class);
  
  public static final SimpleType<BigInteger> BIGINTEGER = new SimpleType(BigInteger.class);
  
  public static final SimpleType<Date> DATE = new SimpleType(Date.class);
  
  public static final SimpleType<ObjectName> OBJECTNAME = new SimpleType(ObjectName.class);
  
  private static final SimpleType<?>[] typeArray = { 
      VOID, BOOLEAN, CHARACTER, BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE, STRING, 
      BIGDECIMAL, BIGINTEGER, DATE, OBJECTNAME };
  
  private Integer myHashCode = null;
  
  private String myToString = null;
  
  private static final Map<SimpleType<?>, SimpleType<?>> canonicalTypes = new HashMap();
  
  private SimpleType(Class<T> paramClass) { super(paramClass.getName(), paramClass.getName(), paramClass.getName(), false); }
  
  public boolean isValue(Object paramObject) { return (paramObject == null) ? false : getClassName().equals(paramObject.getClass().getName()); }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof SimpleType))
      return false; 
    SimpleType simpleType = (SimpleType)paramObject;
    return getClassName().equals(simpleType.getClassName());
  }
  
  public int hashCode() {
    if (this.myHashCode == null)
      this.myHashCode = Integer.valueOf(getClassName().hashCode()); 
    return this.myHashCode.intValue();
  }
  
  public String toString() {
    if (this.myToString == null)
      this.myToString = getClass().getName() + "(name=" + getTypeName() + ")"; 
    return this.myToString;
  }
  
  public Object readResolve() throws ObjectStreamException {
    SimpleType simpleType = (SimpleType)canonicalTypes.get(this);
    if (simpleType == null)
      throw new InvalidObjectException("Invalid SimpleType: " + this); 
    return simpleType;
  }
  
  static  {
    for (byte b = 0; b < typeArray.length; b++) {
      SimpleType simpleType = typeArray[b];
      canonicalTypes.put(simpleType, simpleType);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\openmbean\SimpleType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */