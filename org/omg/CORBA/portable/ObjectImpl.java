package org.omg.CORBA.portable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.DomainManager;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.Request;
import org.omg.CORBA.SetOverrideType;

public abstract class ObjectImpl implements Object {
  private Delegate __delegate;
  
  public Delegate _get_delegate() {
    if (this.__delegate == null)
      throw new BAD_OPERATION("The delegate has not been set!"); 
    return this.__delegate;
  }
  
  public void _set_delegate(Delegate paramDelegate) { this.__delegate = paramDelegate; }
  
  public abstract String[] _ids();
  
  public Object _duplicate() { return _get_delegate().duplicate(this); }
  
  public void _release() { _get_delegate().release(this); }
  
  public boolean _is_a(String paramString) { return _get_delegate().is_a(this, paramString); }
  
  public boolean _is_equivalent(Object paramObject) { return _get_delegate().is_equivalent(this, paramObject); }
  
  public boolean _non_existent() { return _get_delegate().non_existent(this); }
  
  public int _hash(int paramInt) { return _get_delegate().hash(this, paramInt); }
  
  public Request _request(String paramString) { return _get_delegate().request(this, paramString); }
  
  public Request _create_request(Context paramContext, String paramString, NVList paramNVList, NamedValue paramNamedValue) { return _get_delegate().create_request(this, paramContext, paramString, paramNVList, paramNamedValue); }
  
  public Request _create_request(Context paramContext, String paramString, NVList paramNVList, NamedValue paramNamedValue, ExceptionList paramExceptionList, ContextList paramContextList) { return _get_delegate().create_request(this, paramContext, paramString, paramNVList, paramNamedValue, paramExceptionList, paramContextList); }
  
  public Object _get_interface_def() {
    Delegate delegate = _get_delegate();
    try {
      return delegate.get_interface_def(this);
    } catch (NO_IMPLEMENT nO_IMPLEMENT) {
      try {
        Class[] arrayOfClass = { Object.class };
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
  
  public ORB _orb() { return _get_delegate().orb(this); }
  
  public Policy _get_policy(int paramInt) { return _get_delegate().get_policy(this, paramInt); }
  
  public DomainManager[] _get_domain_managers() { return _get_delegate().get_domain_managers(this); }
  
  public Object _set_policy_override(Policy[] paramArrayOfPolicy, SetOverrideType paramSetOverrideType) { return _get_delegate().set_policy_override(this, paramArrayOfPolicy, paramSetOverrideType); }
  
  public boolean _is_local() { return _get_delegate().is_local(this); }
  
  public ServantObject _servant_preinvoke(String paramString, Class paramClass) { return _get_delegate().servant_preinvoke(this, paramString, paramClass); }
  
  public void _servant_postinvoke(ServantObject paramServantObject) { _get_delegate().servant_postinvoke(this, paramServantObject); }
  
  public OutputStream _request(String paramString, boolean paramBoolean) { return _get_delegate().request(this, paramString, paramBoolean); }
  
  public InputStream _invoke(OutputStream paramOutputStream) throws ApplicationException, RemarshalException { return _get_delegate().invoke(this, paramOutputStream); }
  
  public void _releaseReply(InputStream paramInputStream) { _get_delegate().releaseReply(this, paramInputStream); }
  
  public String toString() { return (this.__delegate != null) ? this.__delegate.toString(this) : (getClass().getName() + ": no delegate set"); }
  
  public int hashCode() { return (this.__delegate != null) ? this.__delegate.hashCode(this) : super.hashCode(); }
  
  public boolean equals(Object paramObject) { return (this.__delegate != null) ? this.__delegate.equals(this, paramObject) : ((this == paramObject) ? 1 : 0); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\portable\ObjectImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */