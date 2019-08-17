package org.omg.CORBA;

import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ServantObject;

public class LocalObject implements Object {
  private static String reason = "This is a locally constrained object.";
  
  public boolean _is_equivalent(Object paramObject) { return equals(paramObject); }
  
  public boolean _non_existent() { return false; }
  
  public int _hash(int paramInt) { return hashCode(); }
  
  public boolean _is_a(String paramString) { throw new NO_IMPLEMENT(reason); }
  
  public Object _duplicate() { throw new NO_IMPLEMENT(reason); }
  
  public void _release() { throw new NO_IMPLEMENT(reason); }
  
  public Request _request(String paramString) { throw new NO_IMPLEMENT(reason); }
  
  public Request _create_request(Context paramContext, String paramString, NVList paramNVList, NamedValue paramNamedValue) { throw new NO_IMPLEMENT(reason); }
  
  public Request _create_request(Context paramContext, String paramString, NVList paramNVList, NamedValue paramNamedValue, ExceptionList paramExceptionList, ContextList paramContextList) { throw new NO_IMPLEMENT(reason); }
  
  public Object _get_interface() { throw new NO_IMPLEMENT(reason); }
  
  public Object _get_interface_def() { throw new NO_IMPLEMENT(reason); }
  
  public ORB _orb() { throw new NO_IMPLEMENT(reason); }
  
  public Policy _get_policy(int paramInt) { throw new NO_IMPLEMENT(reason); }
  
  public DomainManager[] _get_domain_managers() { throw new NO_IMPLEMENT(reason); }
  
  public Object _set_policy_override(Policy[] paramArrayOfPolicy, SetOverrideType paramSetOverrideType) { throw new NO_IMPLEMENT(reason); }
  
  public boolean _is_local() { throw new NO_IMPLEMENT(reason); }
  
  public ServantObject _servant_preinvoke(String paramString, Class paramClass) { throw new NO_IMPLEMENT(reason); }
  
  public void _servant_postinvoke(ServantObject paramServantObject) { throw new NO_IMPLEMENT(reason); }
  
  public OutputStream _request(String paramString, boolean paramBoolean) { throw new NO_IMPLEMENT(reason); }
  
  public InputStream _invoke(OutputStream paramOutputStream) throws ApplicationException, RemarshalException { throw new NO_IMPLEMENT(reason); }
  
  public void _releaseReply(InputStream paramInputStream) { throw new NO_IMPLEMENT(reason); }
  
  public boolean validate_connection() { throw new NO_IMPLEMENT(reason); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\LocalObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */