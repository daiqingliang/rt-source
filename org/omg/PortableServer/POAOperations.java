package org.omg.PortableServer;

import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;
import org.omg.PortableServer.POAPackage.AdapterNonExistent;
import org.omg.PortableServer.POAPackage.InvalidPolicy;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongAdapter;
import org.omg.PortableServer.POAPackage.WrongPolicy;

public interface POAOperations {
  POA create_POA(String paramString, POAManager paramPOAManager, Policy[] paramArrayOfPolicy) throws AdapterAlreadyExists, InvalidPolicy;
  
  POA find_POA(String paramString, boolean paramBoolean) throws AdapterNonExistent;
  
  void destroy(boolean paramBoolean1, boolean paramBoolean2);
  
  ThreadPolicy create_thread_policy(ThreadPolicyValue paramThreadPolicyValue);
  
  LifespanPolicy create_lifespan_policy(LifespanPolicyValue paramLifespanPolicyValue);
  
  IdUniquenessPolicy create_id_uniqueness_policy(IdUniquenessPolicyValue paramIdUniquenessPolicyValue);
  
  IdAssignmentPolicy create_id_assignment_policy(IdAssignmentPolicyValue paramIdAssignmentPolicyValue);
  
  ImplicitActivationPolicy create_implicit_activation_policy(ImplicitActivationPolicyValue paramImplicitActivationPolicyValue);
  
  ServantRetentionPolicy create_servant_retention_policy(ServantRetentionPolicyValue paramServantRetentionPolicyValue);
  
  RequestProcessingPolicy create_request_processing_policy(RequestProcessingPolicyValue paramRequestProcessingPolicyValue);
  
  String the_name();
  
  POA the_parent();
  
  POA[] the_children();
  
  POAManager the_POAManager();
  
  AdapterActivator the_activator();
  
  void the_activator(AdapterActivator paramAdapterActivator);
  
  ServantManager get_servant_manager() throws WrongPolicy;
  
  void set_servant_manager(ServantManager paramServantManager) throws WrongPolicy;
  
  Servant get_servant() throws NoServant, WrongPolicy;
  
  void set_servant(Servant paramServant) throws WrongPolicy;
  
  byte[] activate_object(Servant paramServant) throws ServantAlreadyActive, WrongPolicy;
  
  void activate_object_with_id(byte[] paramArrayOfByte, Servant paramServant) throws ServantAlreadyActive, ObjectAlreadyActive, WrongPolicy;
  
  void deactivate_object(byte[] paramArrayOfByte) throws ObjectNotActive, WrongPolicy;
  
  Object create_reference(String paramString) throws WrongPolicy;
  
  Object create_reference_with_id(byte[] paramArrayOfByte, String paramString);
  
  byte[] servant_to_id(Servant paramServant) throws ServantAlreadyActive, WrongPolicy;
  
  Object servant_to_reference(Servant paramServant) throws ServantNotActive, WrongPolicy;
  
  Servant reference_to_servant(Object paramObject) throws ObjectNotActive, WrongPolicy, WrongAdapter;
  
  byte[] reference_to_id(Object paramObject) throws WrongAdapter, WrongPolicy;
  
  Servant id_to_servant(byte[] paramArrayOfByte) throws ObjectNotActive, WrongPolicy;
  
  Object id_to_reference(byte[] paramArrayOfByte) throws ObjectNotActive, WrongPolicy;
  
  byte[] id();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\POAOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */