package com.sun.corba.se.spi.transport;

import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ResponseWaitingRoom;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.legacy.connection.Connection;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.org.omg.SendingContext.CodeBase;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import org.omg.CORBA.SystemException;

public interface CorbaConnection extends Connection, Connection {
  public static final int OPENING = 1;
  
  public static final int ESTABLISHED = 2;
  
  public static final int CLOSE_SENT = 3;
  
  public static final int CLOSE_RECVD = 4;
  
  public static final int ABORT = 5;
  
  boolean shouldUseDirectByteBuffers();
  
  boolean shouldReadGiopHeaderOnly();
  
  ByteBuffer read(int paramInt1, int paramInt2, int paramInt3, long paramLong) throws IOException;
  
  ByteBuffer read(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, long paramLong) throws IOException;
  
  void write(ByteBuffer paramByteBuffer) throws IOException;
  
  void dprint(String paramString);
  
  int getNextRequestId();
  
  ORB getBroker();
  
  CodeSetComponentInfo.CodeSetContext getCodeSetContext();
  
  void setCodeSetContext(CodeSetComponentInfo.CodeSetContext paramCodeSetContext);
  
  MessageMediator clientRequestMapGet(int paramInt);
  
  void clientReply_1_1_Put(MessageMediator paramMessageMediator);
  
  MessageMediator clientReply_1_1_Get();
  
  void clientReply_1_1_Remove();
  
  void serverRequest_1_1_Put(MessageMediator paramMessageMediator);
  
  MessageMediator serverRequest_1_1_Get();
  
  void serverRequest_1_1_Remove();
  
  boolean isPostInitialContexts();
  
  void setPostInitialContexts();
  
  void purgeCalls(SystemException paramSystemException, boolean paramBoolean1, boolean paramBoolean2);
  
  void setCodeBaseIOR(IOR paramIOR);
  
  IOR getCodeBaseIOR();
  
  CodeBase getCodeBase();
  
  void sendCloseConnection(GIOPVersion paramGIOPVersion) throws IOException;
  
  void sendMessageError(GIOPVersion paramGIOPVersion) throws IOException;
  
  void sendCancelRequest(GIOPVersion paramGIOPVersion, int paramInt) throws IOException;
  
  void sendCancelRequestWithLock(GIOPVersion paramGIOPVersion, int paramInt) throws IOException;
  
  ResponseWaitingRoom getResponseWaitingRoom();
  
  void serverRequestMapPut(int paramInt, CorbaMessageMediator paramCorbaMessageMediator);
  
  CorbaMessageMediator serverRequestMapGet(int paramInt);
  
  void serverRequestMapRemove(int paramInt);
  
  SocketChannel getSocketChannel();
  
  void serverRequestProcessingBegins();
  
  void serverRequestProcessingEnds();
  
  void closeConnectionResources();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\transport\CorbaConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */