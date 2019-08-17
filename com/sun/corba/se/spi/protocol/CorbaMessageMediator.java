package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyOrReplyMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import java.nio.ByteBuffer;
import org.omg.CORBA.Request;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA_2_3.portable.InputStream;

public interface CorbaMessageMediator extends MessageMediator, ResponseHandler {
  void setReplyHeader(LocateReplyOrReplyMessage paramLocateReplyOrReplyMessage);
  
  LocateReplyMessage getLocateReplyHeader();
  
  ReplyMessage getReplyHeader();
  
  void setReplyExceptionDetailMessage(String paramString);
  
  RequestMessage getRequestHeader();
  
  GIOPVersion getGIOPVersion();
  
  byte getEncodingVersion();
  
  int getRequestId();
  
  Integer getRequestIdInteger();
  
  boolean isOneWay();
  
  short getAddrDisposition();
  
  String getOperationName();
  
  ServiceContexts getRequestServiceContexts();
  
  ServiceContexts getReplyServiceContexts();
  
  Message getDispatchHeader();
  
  void setDispatchHeader(Message paramMessage);
  
  ByteBuffer getDispatchBuffer();
  
  void setDispatchBuffer(ByteBuffer paramByteBuffer);
  
  int getThreadPoolToUse();
  
  byte getStreamFormatVersion();
  
  byte getStreamFormatVersionForReply();
  
  void sendCancelRequestIfFinalFragmentNotSent();
  
  void setDIIInfo(Request paramRequest);
  
  boolean isDIIRequest();
  
  Exception unmarshalDIIUserException(String paramString, InputStream paramInputStream);
  
  void setDIIException(Exception paramException);
  
  void handleDIIReply(InputStream paramInputStream);
  
  boolean isSystemExceptionReply();
  
  boolean isUserExceptionReply();
  
  boolean isLocationForwardReply();
  
  boolean isDifferentAddrDispositionRequestedReply();
  
  short getAddrDispositionReply();
  
  IOR getForwardedIOR();
  
  SystemException getSystemExceptionReply();
  
  ObjectKey getObjectKey();
  
  void setProtocolHandler(CorbaProtocolHandler paramCorbaProtocolHandler);
  
  CorbaProtocolHandler getProtocolHandler();
  
  OutputStream createReply();
  
  OutputStream createExceptionReply();
  
  boolean executeReturnServantInResponseConstructor();
  
  void setExecuteReturnServantInResponseConstructor(boolean paramBoolean);
  
  boolean executeRemoveThreadInfoInResponseConstructor();
  
  void setExecuteRemoveThreadInfoInResponseConstructor(boolean paramBoolean);
  
  boolean executePIInResponseConstructor();
  
  void setExecutePIInResponseConstructor(boolean paramBoolean);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\protocol\CorbaMessageMediator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */