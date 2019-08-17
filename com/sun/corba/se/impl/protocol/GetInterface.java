package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;

class GetInterface extends SpecialMethod {
  public boolean isNonExistentMethod() { return false; }
  
  public String getName() { return "_interface"; }
  
  public CorbaMessageMediator invoke(Object paramObject, CorbaMessageMediator paramCorbaMessageMediator, byte[] paramArrayOfByte, ObjectAdapter paramObjectAdapter) {
    ORB oRB = (ORB)paramCorbaMessageMediator.getBroker();
    ORBUtilSystemException oRBUtilSystemException = ORBUtilSystemException.get(oRB, "oa.invocation");
    return (paramObject == null || paramObject instanceof com.sun.corba.se.spi.oa.NullServant) ? paramCorbaMessageMediator.getProtocolHandler().createSystemExceptionResponse(paramCorbaMessageMediator, oRBUtilSystemException.badSkeleton(), null) : paramCorbaMessageMediator.getProtocolHandler().createSystemExceptionResponse(paramCorbaMessageMediator, oRBUtilSystemException.getinterfaceNotImplemented(), null);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\GetInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */