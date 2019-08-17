package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

class IsA extends SpecialMethod {
  public boolean isNonExistentMethod() { return false; }
  
  public String getName() { return "_is_a"; }
  
  public CorbaMessageMediator invoke(Object paramObject, CorbaMessageMediator paramCorbaMessageMediator, byte[] paramArrayOfByte, ObjectAdapter paramObjectAdapter) {
    if (paramObject == null || paramObject instanceof com.sun.corba.se.spi.oa.NullServant) {
      ORB oRB = (ORB)paramCorbaMessageMediator.getBroker();
      ORBUtilSystemException oRBUtilSystemException = ORBUtilSystemException.get(oRB, "oa.invocation");
      return paramCorbaMessageMediator.getProtocolHandler().createSystemExceptionResponse(paramCorbaMessageMediator, oRBUtilSystemException.badSkeleton(), null);
    } 
    String[] arrayOfString = paramObjectAdapter.getInterfaces(paramObject, paramArrayOfByte);
    String str = ((InputStream)paramCorbaMessageMediator.getInputObject()).read_string();
    boolean bool = false;
    for (byte b = 0; b < arrayOfString.length; b++) {
      if (arrayOfString[b].equals(str)) {
        bool = true;
        break;
      } 
    } 
    CorbaMessageMediator corbaMessageMediator = paramCorbaMessageMediator.getProtocolHandler().createResponse(paramCorbaMessageMediator, null);
    ((OutputStream)corbaMessageMediator.getOutputObject()).write_boolean(bool);
    return corbaMessageMediator;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\IsA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */