package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class FieldAccessor_Boolean extends Accessor {
  public FieldAccessor_Boolean() { super(Boolean.class); }
  
  public Object get(Object paramObject) { return Boolean.valueOf(((Bean)paramObject).f_boolean); }
  
  public void set(Object paramObject1, Object paramObject2) { ((Bean)paramObject1).f_boolean = (paramObject2 == null) ? Const.default_value_boolean : ((Boolean)paramObject2).booleanValue(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\opt\FieldAccessor_Boolean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */