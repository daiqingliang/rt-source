package org.omg.PortableServer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA_2_3.ORB;
import org.omg.PortableServer.portable.Delegate;

public abstract class Servant {
  private Delegate _delegate = null;
  
  public final Delegate _get_delegate() {
    if (this._delegate == null)
      throw new BAD_INV_ORDER("The Servant has not been associated with an ORB instance"); 
    return this._delegate;
  }
  
  public final void _set_delegate(Delegate paramDelegate) { this._delegate = paramDelegate; }
  
  public final Object _this_object() { return _get_delegate().this_object(this); }
  
  public final Object _this_object(ORB paramORB) {
    try {
      ((ORB)paramORB).set_delegate(this);
    } catch (ClassCastException classCastException) {
      throw new BAD_PARAM("POA Servant requires an instance of org.omg.CORBA_2_3.ORB");
    } 
    return _this_object();
  }
  
  public final ORB _orb() { return _get_delegate().orb(this); }
  
  public final POA _poa() { return _get_delegate().poa(this); }
  
  public final byte[] _object_id() { return _get_delegate().object_id(this); }
  
  public POA _default_POA() { return _get_delegate().default_POA(this); }
  
  public boolean _is_a(String paramString) { return _get_delegate().is_a(this, paramString); }
  
  public boolean _non_existent() { return _get_delegate().non_existent(this); }
  
  public Object _get_interface_def() {
    Delegate delegate = _get_delegate();
    try {
      return delegate.get_interface_def(this);
    } catch (AbstractMethodError abstractMethodError) {
      try {
        Class[] arrayOfClass = { Servant.class };
        Method method = delegate.getClass().getMethod("get_interface", arrayOfClass);
        Object[] arrayOfObject = { this };
        return (Object)method.invoke(delegate, arrayOfObject);
      } catch (InvocationTargetException invocationTargetException) {
        Throwable throwable = invocationTargetException.getTargetException();
        if (throwable instanceof Error)
          throw (Error)throwable; 
        if (throwable instanceof RuntimeException)
          throw (RuntimeException)throwable; 
        throw new NO_IMPLEMENT();
      } catch (RuntimeException runtimeException) {
        throw runtimeException;
      } catch (Exception exception) {
        throw new NO_IMPLEMENT();
      } 
    } 
  }
  
  public abstract String[] _all_interfaces(POA paramPOA, byte[] paramArrayOfByte);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\Servant.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */