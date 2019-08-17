package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ServantObject;

public class NotLocalLocalCRDImpl implements LocalClientRequestDispatcher {
  public boolean useLocalInvocation(Object paramObject) { return false; }
  
  public boolean is_local(Object paramObject) { return false; }
  
  public ServantObject servant_preinvoke(Object paramObject, String paramString, Class paramClass) { return null; }
  
  public void servant_postinvoke(Object paramObject, ServantObject paramServantObject) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\NotLocalLocalCRDImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */