package org.omg.CORBA;

public interface Object {
  boolean _is_a(String paramString);
  
  boolean _is_equivalent(Object paramObject);
  
  boolean _non_existent();
  
  int _hash(int paramInt);
  
  Object _duplicate();
  
  void _release();
  
  Object _get_interface_def();
  
  Request _request(String paramString);
  
  Request _create_request(Context paramContext, String paramString, NVList paramNVList, NamedValue paramNamedValue);
  
  Request _create_request(Context paramContext, String paramString, NVList paramNVList, NamedValue paramNamedValue, ExceptionList paramExceptionList, ContextList paramContextList);
  
  Policy _get_policy(int paramInt);
  
  DomainManager[] _get_domain_managers();
  
  Object _set_policy_override(Policy[] paramArrayOfPolicy, SetOverrideType paramSetOverrideType);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\Object.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */