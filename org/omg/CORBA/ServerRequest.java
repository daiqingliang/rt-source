package org.omg.CORBA;

public abstract class ServerRequest {
  @Deprecated
  public String op_name() { return operation(); }
  
  public String operation() { throw new NO_IMPLEMENT(); }
  
  @Deprecated
  public void params(NVList paramNVList) { arguments(paramNVList); }
  
  public void arguments(NVList paramNVList) { throw new NO_IMPLEMENT(); }
  
  @Deprecated
  public void result(Any paramAny) { set_result(paramAny); }
  
  public void set_result(Any paramAny) { throw new NO_IMPLEMENT(); }
  
  @Deprecated
  public void except(Any paramAny) { set_exception(paramAny); }
  
  public void set_exception(Any paramAny) { throw new NO_IMPLEMENT(); }
  
  public abstract Context ctx();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\ServerRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */