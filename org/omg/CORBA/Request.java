package org.omg.CORBA;

public abstract class Request {
  public abstract Object target();
  
  public abstract String operation();
  
  public abstract NVList arguments();
  
  public abstract NamedValue result();
  
  public abstract Environment env();
  
  public abstract ExceptionList exceptions();
  
  public abstract ContextList contexts();
  
  public abstract Context ctx();
  
  public abstract void ctx(Context paramContext);
  
  public abstract Any add_in_arg();
  
  public abstract Any add_named_in_arg(String paramString);
  
  public abstract Any add_inout_arg();
  
  public abstract Any add_named_inout_arg(String paramString);
  
  public abstract Any add_out_arg();
  
  public abstract Any add_named_out_arg(String paramString);
  
  public abstract void set_return_type(TypeCode paramTypeCode);
  
  public abstract Any return_value();
  
  public abstract void invoke();
  
  public abstract void send_oneway();
  
  public abstract void send_deferred();
  
  public abstract boolean poll_response();
  
  public abstract void get_response();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\Request.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */