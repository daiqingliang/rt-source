package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;

public class INSServerRequestDispatcher implements CorbaServerRequestDispatcher {
  private ORB orb = null;
  
  private ORBUtilSystemException wrapper;
  
  public INSServerRequestDispatcher(ORB paramORB) {
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
  }
  
  public IOR locate(ObjectKey paramObjectKey) {
    String str = new String(paramObjectKey.getBytes(this.orb));
    return getINSReference(str);
  }
  
  public void dispatch(MessageMediator paramMessageMediator) {
    CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
    String str = new String(corbaMessageMediator.getObjectKey().getBytes(this.orb));
    corbaMessageMediator.getProtocolHandler().createLocationForward(corbaMessageMediator, getINSReference(str), null);
  }
  
  private IOR getINSReference(String paramString) {
    IOR iOR = ORBUtility.getIOR(this.orb.getLocalResolver().resolve(paramString));
    if (iOR != null)
      return iOR; 
    throw this.wrapper.servantNotFound();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\INSServerRequestDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */