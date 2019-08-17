package org.omg.CORBA_2_3.portable;

import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;

public abstract class ObjectImpl extends ObjectImpl {
  public String _get_codebase() {
    Delegate delegate = _get_delegate();
    return (delegate instanceof Delegate) ? ((Delegate)delegate).get_codebase(this) : null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA_2_3\portable\ObjectImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */