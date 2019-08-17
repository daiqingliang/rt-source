package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.IOP.ServiceContext;
import org.omg.IOP.TaggedComponent;
import org.omg.IOP.TaggedProfile;

public interface ClientRequestInfoOperations extends RequestInfoOperations {
  Object target();
  
  Object effective_target();
  
  TaggedProfile effective_profile();
  
  Any received_exception();
  
  String received_exception_id();
  
  TaggedComponent get_effective_component(int paramInt);
  
  TaggedComponent[] get_effective_components(int paramInt);
  
  Policy get_request_policy(int paramInt);
  
  void add_request_service_context(ServiceContext paramServiceContext, boolean paramBoolean);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableInterceptor\ClientRequestInfoOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */