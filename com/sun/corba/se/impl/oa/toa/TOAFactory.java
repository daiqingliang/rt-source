package com.sun.corba.se.impl.oa.toa;

import com.sun.corba.se.impl.ior.ObjectKeyTemplateBase;
import com.sun.corba.se.impl.javax.rmi.CORBA.Util;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.ORB;
import java.util.HashMap;
import java.util.Map;

public class TOAFactory implements ObjectAdapterFactory {
  private ORB orb;
  
  private ORBUtilSystemException wrapper;
  
  private TOAImpl toa;
  
  private Map codebaseToTOA;
  
  private TransientObjectManager tom;
  
  public ObjectAdapter find(ObjectAdapterId paramObjectAdapterId) {
    if (paramObjectAdapterId.equals(ObjectKeyTemplateBase.JIDL_OAID))
      return getTOA(); 
    throw this.wrapper.badToaOaid();
  }
  
  public void init(ORB paramORB) {
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "oa.lifecycle");
    this.tom = new TransientObjectManager(paramORB);
    this.codebaseToTOA = new HashMap();
  }
  
  public void shutdown(boolean paramBoolean) {
    if (Util.isInstanceDefined())
      Util.getInstance().unregisterTargetsForORB(this.orb); 
  }
  
  public TOA getTOA(String paramString) {
    TOA tOA = (TOA)this.codebaseToTOA.get(paramString);
    if (tOA == null) {
      tOA = new TOAImpl(this.orb, this.tom, paramString);
      this.codebaseToTOA.put(paramString, tOA);
    } 
    return tOA;
  }
  
  public TOA getTOA() {
    if (this.toa == null)
      this.toa = new TOAImpl(this.orb, this.tom, null); 
    return this.toa;
  }
  
  public ORB getORB() { return this.orb; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\oa\toa\TOAFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */