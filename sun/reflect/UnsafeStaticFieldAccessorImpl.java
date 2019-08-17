package sun.reflect;

import java.lang.reflect.Field;

abstract class UnsafeStaticFieldAccessorImpl extends UnsafeFieldAccessorImpl {
  protected final Object base;
  
  UnsafeStaticFieldAccessorImpl(Field paramField) {
    super(paramField);
    this.base = unsafe.staticFieldBase(paramField);
  }
  
  static  {
    Reflection.registerFieldsToFilter(UnsafeStaticFieldAccessorImpl.class, new String[] { "base" });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\UnsafeStaticFieldAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */