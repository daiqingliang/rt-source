package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class FieldAccessor_Ref extends Accessor {
  public FieldAccessor_Ref() { super(Ref.class); }
  
  public Object get(Object paramObject) { return ((Bean)paramObject).f_ref; }
  
  public void set(Object paramObject1, Object paramObject2) { ((Bean)paramObject1).f_ref = (Ref)paramObject2; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\opt\FieldAccessor_Ref.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */