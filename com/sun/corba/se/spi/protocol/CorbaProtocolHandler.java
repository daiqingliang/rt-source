package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateRequestMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage;
import com.sun.corba.se.pept.protocol.ProtocolHandler;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.UnknownException;

public interface CorbaProtocolHandler extends ProtocolHandler {
  void handleRequest(RequestMessage paramRequestMessage, CorbaMessageMediator paramCorbaMessageMediator);
  
  void handleRequest(LocateRequestMessage paramLocateRequestMessage, CorbaMessageMediator paramCorbaMessageMediator);
  
  CorbaMessageMediator createResponse(CorbaMessageMediator paramCorbaMessageMediator, ServiceContexts paramServiceContexts);
  
  CorbaMessageMediator createUserExceptionResponse(CorbaMessageMediator paramCorbaMessageMediator, ServiceContexts paramServiceContexts);
  
  CorbaMessageMediator createUnknownExceptionResponse(CorbaMessageMediator paramCorbaMessageMediator, UnknownException paramUnknownException);
  
  CorbaMessageMediator createSystemExceptionResponse(CorbaMessageMediator paramCorbaMessageMediator, SystemException paramSystemException, ServiceContexts paramServiceContexts);
  
  CorbaMessageMediator createLocationForward(CorbaMessageMediator paramCorbaMessageMediator, IOR paramIOR, ServiceContexts paramServiceContexts);
  
  void handleThrowableDuringServerDispatch(CorbaMessageMediator paramCorbaMessageMediator, Throwable paramThrowable, CompletionStatus paramCompletionStatus);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\protocol\CorbaProtocolHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */