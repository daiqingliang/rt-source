package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Enumeration;
import java.util.Vector;
import org.omg.CORBA.BAD_PARAM;

public class ServiceContextRegistry {
  private ORB orb;
  
  private Vector scCollection = new Vector();
  
  private void dprint(String paramString) { ORBUtility.dprint(this, paramString); }
  
  public ServiceContextRegistry(ORB paramORB) { this.orb = paramORB; }
  
  public void register(Class paramClass) {
    if (ORB.ORBInitDebug)
      dprint("Registering service context class " + paramClass); 
    ServiceContextData serviceContextData = new ServiceContextData(paramClass);
    if (findServiceContextData(serviceContextData.getId()) == null) {
      this.scCollection.addElement(serviceContextData);
    } else {
      throw new BAD_PARAM("Tried to register duplicate service context");
    } 
  }
  
  public ServiceContextData findServiceContextData(int paramInt) {
    if (ORB.ORBInitDebug)
      dprint("Searching registry for service context id " + paramInt); 
    Enumeration enumeration = this.scCollection.elements();
    while (enumeration.hasMoreElements()) {
      ServiceContextData serviceContextData = (ServiceContextData)enumeration.nextElement();
      if (serviceContextData.getId() == paramInt) {
        if (ORB.ORBInitDebug)
          dprint("Service context data found: " + serviceContextData); 
        return serviceContextData;
      } 
    } 
    if (ORB.ORBInitDebug)
      dprint("Service context data not found"); 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\servicecontext\ServiceContextRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */