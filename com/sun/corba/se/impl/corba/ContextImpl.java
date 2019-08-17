package com.sun.corba.se.impl.corba;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.Context;
import org.omg.CORBA.NVList;
import org.omg.CORBA.ORB;

public final class ContextImpl extends Context {
  private ORB _orb;
  
  private ORBUtilSystemException wrapper;
  
  public ContextImpl(ORB paramORB) {
    this._orb = paramORB;
    this.wrapper = ORBUtilSystemException.get((ORB)paramORB, "rpc.presentation");
  }
  
  public ContextImpl(Context paramContext) { throw this.wrapper.contextNotImplemented(); }
  
  public String context_name() { throw this.wrapper.contextNotImplemented(); }
  
  public Context parent() { throw this.wrapper.contextNotImplemented(); }
  
  public Context create_child(String paramString) { throw this.wrapper.contextNotImplemented(); }
  
  public void set_one_value(String paramString, Any paramAny) { throw this.wrapper.contextNotImplemented(); }
  
  public void set_values(NVList paramNVList) { throw this.wrapper.contextNotImplemented(); }
  
  public void delete_values(String paramString) { throw this.wrapper.contextNotImplemented(); }
  
  public NVList get_values(String paramString1, int paramInt, String paramString2) { throw this.wrapper.contextNotImplemented(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\corba\ContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */