package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.encoding.MarshalInputStream;
import com.sun.corba.se.impl.encoding.MarshalOutputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import java.util.Set;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;

public class BootstrapServerRequestDispatcher implements CorbaServerRequestDispatcher {
  private ORB orb;
  
  ORBUtilSystemException wrapper;
  
  private static final boolean debug = false;
  
  public BootstrapServerRequestDispatcher(ORB paramORB) {
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
  }
  
  public void dispatch(MessageMediator paramMessageMediator) {
    CorbaMessageMediator corbaMessageMediator1 = (CorbaMessageMediator)paramMessageMediator;
    CorbaMessageMediator corbaMessageMediator2 = null;
    try {
      MarshalInputStream marshalInputStream = (MarshalInputStream)corbaMessageMediator1.getInputObject();
      String str = corbaMessageMediator1.getOperationName();
      corbaMessageMediator2 = corbaMessageMediator1.getProtocolHandler().createResponse(corbaMessageMediator1, null);
      MarshalOutputStream marshalOutputStream = (MarshalOutputStream)corbaMessageMediator2.getOutputObject();
      if (str.equals("get")) {
        String str1 = marshalInputStream.read_string();
        Object object = this.orb.getLocalResolver().resolve(str1);
        marshalOutputStream.write_Object(object);
      } else if (str.equals("list")) {
        Set set = this.orb.getLocalResolver().list();
        marshalOutputStream.write_long(set.size());
        for (String str1 : set)
          marshalOutputStream.write_string(str1); 
      } else {
        throw this.wrapper.illegalBootstrapOperation(str);
      } 
    } catch (SystemException systemException) {
      corbaMessageMediator2 = corbaMessageMediator1.getProtocolHandler().createSystemExceptionResponse(corbaMessageMediator1, systemException, null);
    } catch (RuntimeException runtimeException) {
      BAD_PARAM bAD_PARAM = this.wrapper.bootstrapRuntimeException(runtimeException);
      corbaMessageMediator2 = corbaMessageMediator1.getProtocolHandler().createSystemExceptionResponse(corbaMessageMediator1, bAD_PARAM, null);
    } catch (Exception exception) {
      BAD_PARAM bAD_PARAM = this.wrapper.bootstrapException(exception);
      corbaMessageMediator2 = corbaMessageMediator1.getProtocolHandler().createSystemExceptionResponse(corbaMessageMediator1, bAD_PARAM, null);
    } 
  }
  
  public IOR locate(ObjectKey paramObjectKey) { return null; }
  
  public int getId() { throw this.wrapper.genericNoImpl(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\BootstrapServerRequestDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */