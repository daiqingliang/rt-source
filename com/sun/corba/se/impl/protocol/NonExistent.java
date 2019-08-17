package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import org.omg.CORBA.portable.OutputStream;

class NonExistent extends SpecialMethod {
  public boolean isNonExistentMethod() { return true; }
  
  public String getName() { return "_non_existent"; }
  
  public CorbaMessageMediator invoke(Object paramObject, CorbaMessageMediator paramCorbaMessageMediator, byte[] paramArrayOfByte, ObjectAdapter paramObjectAdapter) {
    boolean bool = (paramObject == null || paramObject instanceof com.sun.corba.se.spi.oa.NullServant);
    CorbaMessageMediator corbaMessageMediator = paramCorbaMessageMediator.getProtocolHandler().createResponse(paramCorbaMessageMediator, null);
    ((OutputStream)corbaMessageMediator.getOutputObject()).write_boolean(bool);
    return corbaMessageMediator;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\NonExistent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */