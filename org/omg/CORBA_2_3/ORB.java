package org.omg.CORBA_2_3;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ValueFactory;

public abstract class ORB extends ORB {
  public ValueFactory register_value_factory(String paramString, ValueFactory paramValueFactory) { throw new NO_IMPLEMENT(); }
  
  public void unregister_value_factory(String paramString) { throw new NO_IMPLEMENT(); }
  
  public ValueFactory lookup_value_factory(String paramString) { throw new NO_IMPLEMENT(); }
  
  public Object get_value_def(String paramString) throws BAD_PARAM { throw new NO_IMPLEMENT(); }
  
  public void set_delegate(Object paramObject) { throw new NO_IMPLEMENT(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA_2_3\ORB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */