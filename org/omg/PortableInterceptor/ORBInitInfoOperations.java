package org.omg.PortableInterceptor;

import org.omg.CORBA.Object;
import org.omg.IOP.CodecFactory;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.PortableInterceptor.ORBInitInfoPackage.InvalidName;

public interface ORBInitInfoOperations {
  String[] arguments();
  
  String orb_id();
  
  CodecFactory codec_factory();
  
  void register_initial_reference(String paramString, Object paramObject) throws InvalidName;
  
  Object resolve_initial_references(String paramString) throws InvalidName;
  
  void add_client_request_interceptor(ClientRequestInterceptor paramClientRequestInterceptor) throws DuplicateName;
  
  void add_server_request_interceptor(ServerRequestInterceptor paramServerRequestInterceptor) throws DuplicateName;
  
  void add_ior_interceptor(IORInterceptor paramIORInterceptor) throws DuplicateName;
  
  int allocate_slot_id();
  
  void register_policy_factory(int paramInt, PolicyFactory paramPolicyFactory);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableInterceptor\ORBInitInfoOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */