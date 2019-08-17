package com.sun.corba.se.spi.transport;

import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.spi.ior.IORTemplate;

public interface CorbaAcceptor extends Acceptor {
  String getObjectAdapterId();
  
  String getObjectAdapterManagerId();
  
  void addToIORTemplate(IORTemplate paramIORTemplate, Policies paramPolicies, String paramString);
  
  String getMonitoringName();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\transport\CorbaAcceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */