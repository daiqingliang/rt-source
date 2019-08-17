package com.sun.corba.se.impl.oa.poa;

import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantManager;

public interface POAPolicyMediator {
  Policies getPolicies();
  
  int getScid();
  
  int getServerId();
  
  Object getInvocationServant(byte[] paramArrayOfByte, String paramString) throws ForwardRequest;
  
  void returnServant();
  
  void etherealizeAll();
  
  void clearAOM();
  
  ServantManager getServantManager() throws WrongPolicy;
  
  void setServantManager(ServantManager paramServantManager) throws WrongPolicy;
  
  Servant getDefaultServant() throws NoServant, WrongPolicy;
  
  void setDefaultServant(Servant paramServant) throws WrongPolicy;
  
  void activateObject(byte[] paramArrayOfByte, Servant paramServant) throws ObjectAlreadyActive, ServantAlreadyActive, WrongPolicy;
  
  Servant deactivateObject(byte[] paramArrayOfByte) throws ObjectNotActive, WrongPolicy;
  
  byte[] newSystemId() throws WrongPolicy;
  
  byte[] servantToId(Servant paramServant) throws ServantNotActive, WrongPolicy;
  
  Servant idToServant(byte[] paramArrayOfByte) throws ObjectNotActive, WrongPolicy;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\oa\poa\POAPolicyMediator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */