package sun.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import sun.misc.Unsafe;

abstract class UnsafeFieldAccessorImpl extends FieldAccessorImpl {
  static final Unsafe unsafe = Unsafe.getUnsafe();
  
  protected final Field field;
  
  protected final long fieldOffset;
  
  protected final boolean isFinal;
  
  UnsafeFieldAccessorImpl(Field paramField) {
    this.field = paramField;
    if (Modifier.isStatic(paramField.getModifiers())) {
      this.fieldOffset = unsafe.staticFieldOffset(paramField);
    } else {
      this.fieldOffset = unsafe.objectFieldOffset(paramField);
    } 
    this.isFinal = Modifier.isFinal(paramField.getModifiers());
  }
  
  protected void ensureObj(Object paramObject) {
    if (!this.field.getDeclaringClass().isAssignableFrom(paramObject.getClass()))
      throwSetIllegalArgumentException(paramObject); 
  }
  
  private String getQualifiedFieldName() { return this.field.getDeclaringClass().getName() + "." + this.field.getName(); }
  
  protected IllegalArgumentException newGetIllegalArgumentException(String paramString) { return new IllegalArgumentException("Attempt to get " + this.field.getType().getName() + " field \"" + getQualifiedFieldName() + "\" with illegal data type conversion to " + paramString); }
  
  protected void throwFinalFieldIllegalAccessException(String paramString1, String paramString2) throws IllegalAccessException { throw new IllegalAccessException(getSetMessage(paramString1, paramString2)); }
  
  protected void throwFinalFieldIllegalAccessException(Object paramObject) { throwFinalFieldIllegalAccessException((paramObject != null) ? paramObject.getClass().getName() : "", ""); }
  
  protected void throwFinalFieldIllegalAccessException(boolean paramBoolean) throws IllegalAccessException { throwFinalFieldIllegalAccessException("boolean", Boolean.toString(paramBoolean)); }
  
  protected void throwFinalFieldIllegalAccessException(char paramChar) throws IllegalAccessException { throwFinalFieldIllegalAccessException("char", Character.toString(paramChar)); }
  
  protected void throwFinalFieldIllegalAccessException(byte paramByte) throws IllegalAccessException { throwFinalFieldIllegalAccessException("byte", Byte.toString(paramByte)); }
  
  protected void throwFinalFieldIllegalAccessException(short paramShort) throws IllegalAccessException { throwFinalFieldIllegalAccessException("short", Short.toString(paramShort)); }
  
  protected void throwFinalFieldIllegalAccessException(int paramInt) throws IllegalAccessException { throwFinalFieldIllegalAccessException("int", Integer.toString(paramInt)); }
  
  protected void throwFinalFieldIllegalAccessException(long paramLong) throws IllegalAccessException { throwFinalFieldIllegalAccessException("long", Long.toString(paramLong)); }
  
  protected void throwFinalFieldIllegalAccessException(float paramFloat) throws IllegalAccessException { throwFinalFieldIllegalAccessException("float", Float.toString(paramFloat)); }
  
  protected void throwFinalFieldIllegalAccessException(double paramDouble) throws IllegalAccessException { throwFinalFieldIllegalAccessException("double", Double.toString(paramDouble)); }
  
  protected IllegalArgumentException newGetBooleanIllegalArgumentException() { return newGetIllegalArgumentException("boolean"); }
  
  protected IllegalArgumentException newGetByteIllegalArgumentException() { return newGetIllegalArgumentException("byte"); }
  
  protected IllegalArgumentException newGetCharIllegalArgumentException() { return newGetIllegalArgumentException("char"); }
  
  protected IllegalArgumentException newGetShortIllegalArgumentException() { return newGetIllegalArgumentException("short"); }
  
  protected IllegalArgumentException newGetIntIllegalArgumentException() { return newGetIllegalArgumentException("int"); }
  
  protected IllegalArgumentException newGetLongIllegalArgumentException() { return newGetIllegalArgumentException("long"); }
  
  protected IllegalArgumentException newGetFloatIllegalArgumentException() { return newGetIllegalArgumentException("float"); }
  
  protected IllegalArgumentException newGetDoubleIllegalArgumentException() { return newGetIllegalArgumentException("double"); }
  
  protected String getSetMessage(String paramString1, String paramString2) {
    String str = "Can not set";
    if (Modifier.isStatic(this.field.getModifiers()))
      str = str + " static"; 
    if (this.isFinal)
      str = str + " final"; 
    str = str + " " + this.field.getType().getName() + " field " + getQualifiedFieldName() + " to ";
    if (paramString2.length() > 0) {
      str = str + "(" + paramString1 + ")" + paramString2;
    } else if (paramString1.length() > 0) {
      str = str + paramString1;
    } else {
      str = str + "null value";
    } 
    return str;
  }
  
  protected void throwSetIllegalArgumentException(String paramString1, String paramString2) throws IllegalAccessException { throw new IllegalArgumentException(getSetMessage(paramString1, paramString2)); }
  
  protected void throwSetIllegalArgumentException(Object paramObject) { throwSetIllegalArgumentException((paramObject != null) ? paramObject.getClass().getName() : "", ""); }
  
  protected void throwSetIllegalArgumentException(boolean paramBoolean) throws IllegalAccessException { throwSetIllegalArgumentException("boolean", Boolean.toString(paramBoolean)); }
  
  protected void throwSetIllegalArgumentException(byte paramByte) throws IllegalAccessException { throwSetIllegalArgumentException("byte", Byte.toString(paramByte)); }
  
  protected void throwSetIllegalArgumentException(char paramChar) throws IllegalAccessException { throwSetIllegalArgumentException("char", Character.toString(paramChar)); }
  
  protected void throwSetIllegalArgumentException(short paramShort) throws IllegalAccessException { throwSetIllegalArgumentException("short", Short.toString(paramShort)); }
  
  protected void throwSetIllegalArgumentException(int paramInt) throws IllegalAccessException { throwSetIllegalArgumentException("int", Integer.toString(paramInt)); }
  
  protected void throwSetIllegalArgumentException(long paramLong) throws IllegalAccessException { throwSetIllegalArgumentException("long", Long.toString(paramLong)); }
  
  protected void throwSetIllegalArgumentException(float paramFloat) throws IllegalAccessException { throwSetIllegalArgumentException("float", Float.toString(paramFloat)); }
  
  protected void throwSetIllegalArgumentException(double paramDouble) throws IllegalAccessException { throwSetIllegalArgumentException("double", Double.toString(paramDouble)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\UnsafeFieldAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */