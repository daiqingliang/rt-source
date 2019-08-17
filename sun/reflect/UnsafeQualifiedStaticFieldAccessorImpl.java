package sun.reflect;

import java.lang.reflect.Field;

abstract class UnsafeQualifiedStaticFieldAccessorImpl extends UnsafeStaticFieldAccessorImpl {
  protected final boolean isReadOnly;
  
  UnsafeQualifiedStaticFieldAccessorImpl(Field paramField, boolean paramBoolean) {
    super(paramField);
    this.isReadOnly = paramBoolean;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\UnsafeQualifiedStaticFieldAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */