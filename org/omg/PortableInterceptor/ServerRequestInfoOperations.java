package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;
import org.omg.CORBA.Policy;
import org.omg.IOP.ServiceContext;

public interface ServerRequestInfoOperations extends RequestInfoOperations {
  Any sending_exception();
  
  byte[] object_id();
  
  byte[] adapter_id();
  
  String server_id();
  
  String orb_id();
  
  String[] adapter_name();
  
  String target_most_derived_interface();
  
  Policy get_server_policy(int paramInt);
  
  void set_slot(int paramInt, Any paramAny) throws InvalidSlot;
  
  boolean target_is_a(String paramString);
  
  void add_reply_service_context(ServiceContext paramServiceContext, boolean paramBoolean);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableInterceptor\ServerRequestInfoOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */