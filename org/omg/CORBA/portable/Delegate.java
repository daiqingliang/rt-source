package org.omg.CORBA.portable;

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

public abstract class Delegate {
  public abstract Object get_interface_def(Object paramObject);
  
  public abstract Object duplicate(Object paramObject);
  
  public abstract void release(Object paramObject);
  
  public abstract boolean is_a(Object paramObject, String paramString);
  
  public abstract boolean non_existent(Object paramObject);
  
  public abstract boolean is_equivalent(Object paramObject1, Object paramObject2);
  
  public abstract int hash(Object paramObject, int paramInt);
  
  public abstract Request request(Object paramObject, String paramString);
  
  public abstract Request create_request(Object paramObject, Context paramContext, String paramString, NVList paramNVList, NamedValue paramNamedValue);
  
  public abstract Request create_request(Object paramObject, Context paramContext, String paramString, NVList paramNVList, NamedValue paramNamedValue, ExceptionList paramExceptionList, ContextList paramContextList);
  
  public ORB orb(Object paramObject) { throw new NO_IMPLEMENT(); }
  
  public Policy get_policy(Object paramObject, int paramInt) { throw new NO_IMPLEMENT(); }
  
  public DomainManager[] get_domain_managers(Object paramObject) { throw new NO_IMPLEMENT(); }
  
  public Object set_policy_override(Object paramObject, Policy[] paramArrayOfPolicy, SetOverrideType paramSetOverrideType) { throw new NO_IMPLEMENT(); }
  
  public boolean is_local(Object paramObject) { return false; }
  
  public ServantObject servant_preinvoke(Object paramObject, String paramString, Class paramClass) { return null; }
  
  public void servant_postinvoke(Object paramObject, ServantObject paramServantObject) {}
  
  public OutputStream request(Object paramObject, String paramString, boolean paramBoolean) { throw new NO_IMPLEMENT(); }
  
  public InputStream invoke(Object paramObject, OutputStream paramOutputStream) throws ApplicationException, RemarshalException { throw new NO_IMPLEMENT(); }
  
  public void releaseReply(Object paramObject, InputStream paramInputStream) { throw new NO_IMPLEMENT(); }
  
  public String toString(Object paramObject) { return paramObject.getClass().getName() + ":" + toString(); }
  
  public int hashCode(Object paramObject) { return System.identityHashCode(paramObject); }
  
  public boolean equals(Object paramObject, Object paramObject1) { return (paramObject == paramObject1); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\portable\Delegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */