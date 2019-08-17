package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.encoding.ByteBufferWithInfo;
import com.sun.corba.se.impl.encoding.CDRInputObject;
import com.sun.corba.se.impl.encoding.CDROutputObject;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.RemarshalException;

public class SharedCDRClientRequestDispatcherImpl extends CorbaClientRequestDispatcherImpl {
  public InputObject marshalingComplete(Object paramObject, OutputObject paramOutputObject) throws ApplicationException, RemarshalException {
    oRB = null;
    corbaMessageMediator = null;
    try {
      corbaMessageMediator = (CorbaMessageMediator)paramOutputObject.getMessageMediator();
      oRB = (ORB)corbaMessageMediator.getBroker();
      if (oRB.subcontractDebugFlag)
        dprint(".marshalingComplete->: " + opAndId(corbaMessageMediator)); 
      CDROutputObject cDROutputObject = (CDROutputObject)paramOutputObject;
      ByteBufferWithInfo byteBufferWithInfo = cDROutputObject.getByteBufferWithInfo();
      cDROutputObject.getMessageHeader().setSize(byteBufferWithInfo.byteBuffer, byteBufferWithInfo.getSize());
      final ORB inOrb = oRB;
      final ByteBuffer inBuffer = byteBufferWithInfo.byteBuffer;
      final Message inMsg = cDROutputObject.getMessageHeader();
      CDRInputObject cDRInputObject1 = (CDRInputObject)AccessController.doPrivileged(new PrivilegedAction<CDRInputObject>() {
            public CDRInputObject run() { return new CDRInputObject(inOrb, null, inBuffer, inMsg); }
          });
      corbaMessageMediator.setInputObject(cDRInputObject1);
      cDRInputObject1.setMessageMediator(corbaMessageMediator);
      ((CorbaMessageMediatorImpl)corbaMessageMediator).handleRequestRequest(corbaMessageMediator);
      try {
        cDRInputObject1.close();
      } catch (IOException iOException) {
        if (oRB.transportDebugFlag)
          dprint(".marshalingComplete: ignoring IOException - " + iOException.toString()); 
      } 
      cDROutputObject = (CDROutputObject)corbaMessageMediator.getOutputObject();
      byteBufferWithInfo = cDROutputObject.getByteBufferWithInfo();
      cDROutputObject.getMessageHeader().setSize(byteBufferWithInfo.byteBuffer, byteBufferWithInfo.getSize());
      final ORB inOrb2 = oRB;
      final ByteBuffer inBuffer2 = byteBufferWithInfo.byteBuffer;
      final Message inMsg2 = cDROutputObject.getMessageHeader();
      cDRInputObject1 = (CDRInputObject)AccessController.doPrivileged(new PrivilegedAction<CDRInputObject>() {
            public CDRInputObject run() { return new CDRInputObject(inOrb2, null, inBuffer2, inMsg2); }
          });
      corbaMessageMediator.setInputObject(cDRInputObject1);
      cDRInputObject1.setMessageMediator(corbaMessageMediator);
      cDRInputObject1.unmarshalHeader();
      CDRInputObject cDRInputObject2 = cDRInputObject1;
      return processResponse(oRB, corbaMessageMediator, cDRInputObject2);
    } finally {
      if (oRB.subcontractDebugFlag)
        dprint(".marshalingComplete<-: " + opAndId(corbaMessageMediator)); 
    } 
  }
  
  protected void dprint(String paramString) { ORBUtility.dprint("SharedCDRClientRequestDispatcherImpl", paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\SharedCDRClientRequestDispatcherImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */