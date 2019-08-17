package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import javax.rmi.CORBA.Tie;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ServantObject;

public class JIDLLocalCRDImpl extends LocalClientRequestDispatcherBase {
  protected ServantObject servant;
  
  public JIDLLocalCRDImpl(ORB paramORB, int paramInt, IOR paramIOR) { super(paramORB, paramInt, paramIOR); }
  
  public ServantObject servant_preinvoke(Object paramObject, String paramString, Class paramClass) { return !checkForCompatibleServant(this.servant, paramClass) ? null : this.servant; }
  
  public void servant_postinvoke(Object paramObject, ServantObject paramServantObject) {}
  
  public void setServant(Object paramObject) {
    if (paramObject != null && paramObject instanceof Tie) {
      this.servant = new ServantObject();
      this.servant.servant = ((Tie)paramObject).getTarget();
    } else {
      this.servant = null;
    } 
  }
  
  public void unexport() { this.servant = null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\JIDLLocalCRDImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */