package com.sun.jndi.cosnaming;

import com.sun.jndi.toolkit.corba.CorbaUtils;
import java.rmi.Remote;
import java.util.Hashtable;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.StateFactory;

public class RemoteToCorba implements StateFactory {
  public Object getStateToBind(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable) throws NamingException {
    if (paramObject instanceof org.omg.CORBA.Object)
      return null; 
    if (paramObject instanceof Remote)
      try {
        return CorbaUtils.remoteToCorba((Remote)paramObject, ((CNCtx)paramContext)._orb);
      } catch (ClassNotFoundException classNotFoundException) {
        throw new ConfigurationException("javax.rmi packages not available");
      }  
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\cosnaming\RemoteToCorba.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */