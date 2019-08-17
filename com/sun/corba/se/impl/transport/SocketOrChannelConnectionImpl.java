package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.encoding.CDROutputObject;
import com.sun.corba.se.impl.encoding.CachedCodeBase;
import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.giopmsgheaders.CancelRequestMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ConnectionCache;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.EventHandler;
import com.sun.corba.se.pept.transport.InboundConnectionCache;
import com.sun.corba.se.pept.transport.OutboundConnectionCache;
import com.sun.corba.se.pept.transport.ResponseWaitingRoom;
import com.sun.corba.se.pept.transport.Selector;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchThreadPoolException;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchWorkQueueException;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.CorbaResponseWaitingRoom;
import com.sun.corba.se.spi.transport.ReadTimeouts;
import com.sun.org.omg.SendingContext.CodeBase;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.SystemException;
import sun.corba.OutputStreamFactory;

public class SocketOrChannelConnectionImpl extends EventHandlerBase implements CorbaConnection, Work {
  public static boolean dprintWriteLocks = false;
  
  protected long enqueueTime;
  
  protected SocketChannel socketChannel;
  
  protected CorbaContactInfo contactInfo;
  
  protected Acceptor acceptor;
  
  protected ConnectionCache connectionCache;
  
  protected Socket socket;
  
  protected long timeStamp = 0L;
  
  protected boolean isServer = false;
  
  protected int requestId = 5;
  
  protected CorbaResponseWaitingRoom responseWaitingRoom;
  
  protected int state;
  
  protected Object stateEvent = new Object();
  
  protected Object writeEvent = new Object();
  
  protected boolean writeLocked;
  
  protected int serverRequestCount = 0;
  
  Map serverRequestMap = null;
  
  protected boolean postInitialContexts = false;
  
  protected IOR codeBaseServerIOR;
  
  protected CachedCodeBase cachedCodeBase = new CachedCodeBase(this);
  
  protected ORBUtilSystemException wrapper;
  
  protected ReadTimeouts readTimeouts;
  
  protected boolean shouldReadGiopHeaderOnly;
  
  protected CorbaMessageMediator partialMessageMediator = null;
  
  protected CodeSetComponentInfo.CodeSetContext codeSetContext = null;
  
  protected MessageMediator clientReply_1_1;
  
  protected MessageMediator serverRequest_1_1;
  
  public SocketChannel getSocketChannel() { return this.socketChannel; }
  
  protected SocketOrChannelConnectionImpl(ORB paramORB) {
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.transport");
    setWork(this);
    this.responseWaitingRoom = new CorbaResponseWaitingRoomImpl(paramORB, this);
    setReadTimeouts(paramORB.getORBData().getTransportTCPReadTimeouts());
  }
  
  protected SocketOrChannelConnectionImpl(ORB paramORB, boolean paramBoolean1, boolean paramBoolean2) {
    this(paramORB);
    setUseSelectThreadToWait(paramBoolean1);
    setUseWorkerThreadForEvent(paramBoolean2);
  }
  
  public SocketOrChannelConnectionImpl(ORB paramORB, CorbaContactInfo paramCorbaContactInfo, boolean paramBoolean1, boolean paramBoolean2, String paramString1, String paramString2, int paramInt) {
    this(paramORB, paramBoolean1, paramBoolean2);
    this.contactInfo = paramCorbaContactInfo;
    try {
      this.socket = paramORB.getORBData().getSocketFactory().createSocket(paramString1, new InetSocketAddress(paramString2, paramInt));
      this.socketChannel = this.socket.getChannel();
      if (this.socketChannel != null) {
        boolean bool = !paramBoolean1;
        this.socketChannel.configureBlocking(bool);
      } else {
        setUseSelectThreadToWait(false);
      } 
      if (paramORB.transportDebugFlag)
        dprint(".initialize: connection created: " + this.socket); 
    } catch (Throwable throwable) {
      throw this.wrapper.connectFailure(throwable, paramString1, paramString2, Integer.toString(paramInt));
    } 
    this.state = 1;
  }
  
  public SocketOrChannelConnectionImpl(ORB paramORB, CorbaContactInfo paramCorbaContactInfo, String paramString1, String paramString2, int paramInt) { this(paramORB, paramCorbaContactInfo, paramORB.getORBData().connectionSocketUseSelectThreadToWait(), paramORB.getORBData().connectionSocketUseWorkerThreadForEvent(), paramString1, paramString2, paramInt); }
  
  public SocketOrChannelConnectionImpl(ORB paramORB, Acceptor paramAcceptor, Socket paramSocket, boolean paramBoolean1, boolean paramBoolean2) {
    this(paramORB, paramBoolean1, paramBoolean2);
    this.socket = paramSocket;
    this.socketChannel = paramSocket.getChannel();
    if (this.socketChannel != null)
      try {
        boolean bool = !paramBoolean1;
        this.socketChannel.configureBlocking(bool);
      } catch (IOException iOException) {
        RuntimeException runtimeException = new RuntimeException();
        runtimeException.initCause(iOException);
        throw runtimeException;
      }  
    this.acceptor = paramAcceptor;
    this.serverRequestMap = Collections.synchronizedMap(new HashMap());
    this.isServer = true;
    this.state = 2;
  }
  
  public SocketOrChannelConnectionImpl(ORB paramORB, Acceptor paramAcceptor, Socket paramSocket) { this(paramORB, paramAcceptor, paramSocket, (paramSocket.getChannel() == null) ? false : paramORB.getORBData().connectionSocketUseSelectThreadToWait(), (paramSocket.getChannel() == null) ? false : paramORB.getORBData().connectionSocketUseWorkerThreadForEvent()); }
  
  public boolean shouldRegisterReadEvent() { return true; }
  
  public boolean shouldRegisterServerReadEvent() { return true; }
  
  public boolean read() {
    try {
      if (this.orb.transportDebugFlag)
        dprint(".read->: " + this); 
      CorbaMessageMediator corbaMessageMediator = readBits();
      if (corbaMessageMediator != null)
        return dispatch(corbaMessageMediator); 
      return true;
    } finally {
      if (this.orb.transportDebugFlag)
        dprint(".read<-: " + this); 
    } 
  }
  
  protected CorbaMessageMediator readBits() {
    try {
      MessageMediator messageMediator;
      if (this.orb.transportDebugFlag)
        dprint(".readBits->: " + this); 
      if (this.contactInfo != null) {
        messageMediator = this.contactInfo.createMessageMediator(this.orb, this);
      } else if (this.acceptor != null) {
        messageMediator = this.acceptor.createMessageMediator(this.orb, this);
      } else {
        throw new RuntimeException("SocketOrChannelConnectionImpl.readBits");
      } 
      return (CorbaMessageMediator)messageMediator;
    } catch (ThreadDeath threadDeath) {
      if (this.orb.transportDebugFlag)
        dprint(".readBits: " + this + ": ThreadDeath: " + threadDeath, threadDeath); 
      try {
        purgeCalls(this.wrapper.connectionAbort(threadDeath), false, false);
      } catch (Throwable throwable) {
        if (this.orb.transportDebugFlag)
          dprint(".readBits: " + this + ": purgeCalls: Throwable: " + throwable, throwable); 
      } 
      throw threadDeath;
    } catch (Throwable throwable) {
      if (this.orb.transportDebugFlag)
        dprint(".readBits: " + this + ": Throwable: " + throwable, throwable); 
      try {
        if (throwable instanceof INTERNAL)
          sendMessageError(GIOPVersion.DEFAULT_VERSION); 
      } catch (IOException iOException) {
        if (this.orb.transportDebugFlag)
          dprint(".readBits: " + this + ": sendMessageError: IOException: " + iOException, iOException); 
      } 
      Selector selector = this.orb.getTransportManager().getSelector(0);
      if (selector != null)
        selector.unregisterForEvent(this); 
      purgeCalls(this.wrapper.connectionAbort(throwable), true, false);
    } finally {
      if (this.orb.transportDebugFlag)
        dprint(".readBits<-: " + this); 
    } 
    return null;
  }
  
  protected CorbaMessageMediator finishReadingBits(MessageMediator paramMessageMediator) {
    try {
      if (this.orb.transportDebugFlag)
        dprint(".finishReadingBits->: " + this); 
      if (this.contactInfo != null) {
        paramMessageMediator = this.contactInfo.finishCreatingMessageMediator(this.orb, this, paramMessageMediator);
      } else if (this.acceptor != null) {
        paramMessageMediator = this.acceptor.finishCreatingMessageMediator(this.orb, this, paramMessageMediator);
      } else {
        throw new RuntimeException("SocketOrChannelConnectionImpl.finishReadingBits");
      } 
      return (CorbaMessageMediator)paramMessageMediator;
    } catch (ThreadDeath threadDeath) {
      if (this.orb.transportDebugFlag)
        dprint(".finishReadingBits: " + this + ": ThreadDeath: " + threadDeath, threadDeath); 
      try {
        purgeCalls(this.wrapper.connectionAbort(threadDeath), false, false);
      } catch (Throwable throwable) {
        if (this.orb.transportDebugFlag)
          dprint(".finishReadingBits: " + this + ": purgeCalls: Throwable: " + throwable, throwable); 
      } 
      throw threadDeath;
    } catch (Throwable throwable) {
      if (this.orb.transportDebugFlag)
        dprint(".finishReadingBits: " + this + ": Throwable: " + throwable, throwable); 
      try {
        if (throwable instanceof INTERNAL)
          sendMessageError(GIOPVersion.DEFAULT_VERSION); 
      } catch (IOException iOException) {
        if (this.orb.transportDebugFlag)
          dprint(".finishReadingBits: " + this + ": sendMessageError: IOException: " + iOException, iOException); 
      } 
      this.orb.getTransportManager().getSelector(0).unregisterForEvent(this);
      purgeCalls(this.wrapper.connectionAbort(throwable), true, false);
    } finally {
      if (this.orb.transportDebugFlag)
        dprint(".finishReadingBits<-: " + this); 
    } 
    return null;
  }
  
  protected boolean dispatch(CorbaMessageMediator paramCorbaMessageMediator) {
    try {
      if (this.orb.transportDebugFlag)
        dprint(".dispatch->: " + this); 
      boolean bool = paramCorbaMessageMediator.getProtocolHandler().handleRequest(paramCorbaMessageMediator);
      return bool;
    } catch (ThreadDeath threadDeath) {
      if (this.orb.transportDebugFlag)
        dprint(".dispatch: ThreadDeath", threadDeath); 
      try {
        purgeCalls(this.wrapper.connectionAbort(threadDeath), false, false);
      } catch (Throwable throwable) {
        if (this.orb.transportDebugFlag)
          dprint(".dispatch: purgeCalls: Throwable", throwable); 
      } 
      throw threadDeath;
    } catch (Throwable throwable) {
      if (this.orb.transportDebugFlag)
        dprint(".dispatch: Throwable", throwable); 
      try {
        if (throwable instanceof INTERNAL)
          sendMessageError(GIOPVersion.DEFAULT_VERSION); 
      } catch (IOException iOException) {
        if (this.orb.transportDebugFlag)
          dprint(".dispatch: sendMessageError: IOException", iOException); 
      } 
      purgeCalls(this.wrapper.connectionAbort(throwable), false, false);
    } finally {
      if (this.orb.transportDebugFlag)
        dprint(".dispatch<-: " + this); 
    } 
    return true;
  }
  
  public boolean shouldUseDirectByteBuffers() { return (getSocketChannel() != null); }
  
  public ByteBuffer read(int paramInt1, int paramInt2, int paramInt3, long paramLong) throws IOException {
    if (shouldUseDirectByteBuffers()) {
      ByteBuffer byteBuffer1 = this.orb.getByteBufferPool().getByteBuffer(paramInt1);
      if (this.orb.transportDebugFlag) {
        int i = System.identityHashCode(byteBuffer1);
        StringBuffer stringBuffer = new StringBuffer(80);
        stringBuffer.append(".read: got ByteBuffer id (");
        stringBuffer.append(i).append(") from ByteBufferPool.");
        String str = stringBuffer.toString();
        dprint(str);
      } 
      byteBuffer1.position(paramInt2);
      byteBuffer1.limit(paramInt1);
      readFully(byteBuffer1, paramInt3, paramLong);
      return byteBuffer1;
    } 
    byte[] arrayOfByte = new byte[paramInt1];
    readFully(getSocket().getInputStream(), arrayOfByte, paramInt2, paramInt3, paramLong);
    ByteBuffer byteBuffer = ByteBuffer.wrap(arrayOfByte);
    byteBuffer.limit(paramInt1);
    return byteBuffer;
  }
  
  public ByteBuffer read(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, long paramLong) throws IOException {
    int i = paramInt1 + paramInt2;
    if (shouldUseDirectByteBuffers()) {
      if (!paramByteBuffer.isDirect())
        throw this.wrapper.unexpectedNonDirectByteBufferWithChannelSocket(); 
      if (i > paramByteBuffer.capacity()) {
        if (this.orb.transportDebugFlag) {
          int j = System.identityHashCode(paramByteBuffer);
          StringBuffer stringBuffer = new StringBuffer(80);
          stringBuffer.append(".read: releasing ByteBuffer id (").append(j).append(") to ByteBufferPool.");
          String str = stringBuffer.toString();
          dprint(str);
        } 
        this.orb.getByteBufferPool().releaseByteBuffer(paramByteBuffer);
        paramByteBuffer = this.orb.getByteBufferPool().getByteBuffer(i);
      } 
      paramByteBuffer.position(paramInt1);
      paramByteBuffer.limit(i);
      readFully(paramByteBuffer, paramInt2, paramLong);
      paramByteBuffer.position(0);
      paramByteBuffer.limit(i);
      return paramByteBuffer;
    } 
    if (paramByteBuffer.isDirect())
      throw this.wrapper.unexpectedDirectByteBufferWithNonChannelSocket(); 
    byte[] arrayOfByte = new byte[i];
    readFully(getSocket().getInputStream(), arrayOfByte, paramInt1, paramInt2, paramLong);
    return ByteBuffer.wrap(arrayOfByte);
  }
  
  public void readFully(ByteBuffer paramByteBuffer, int paramInt, long paramLong) throws IOException {
    int i = 0;
    int j = 0;
    long l1 = this.readTimeouts.get_initial_time_to_wait();
    long l2 = 0L;
    do {
      j = getSocketChannel().read(paramByteBuffer);
      if (j < 0)
        throw new IOException("End-of-stream"); 
      if (j == 0) {
        try {
          Thread.sleep(l1);
          l2 += l1;
          l1 = (long)(l1 * this.readTimeouts.get_backoff_factor());
        } catch (InterruptedException interruptedException) {
          if (this.orb.transportDebugFlag)
            dprint("readFully(): unexpected exception " + interruptedException.toString()); 
        } 
      } else {
        i += j;
      } 
    } while (i < paramInt && l2 < paramLong);
    if (i < paramInt && l2 >= paramLong)
      throw this.wrapper.transportReadTimeoutExceeded(new Integer(paramInt), new Integer(i), new Long(paramLong), new Long(l2)); 
    getConnectionCache().stampTime(this);
  }
  
  public void readFully(InputStream paramInputStream, byte[] paramArrayOfByte, int paramInt1, int paramInt2, long paramLong) throws IOException {
    int i = 0;
    int j = 0;
    long l1 = this.readTimeouts.get_initial_time_to_wait();
    long l2 = 0L;
    do {
      j = paramInputStream.read(paramArrayOfByte, paramInt1 + i, paramInt2 - i);
      if (j < 0)
        throw new IOException("End-of-stream"); 
      if (j == 0) {
        try {
          Thread.sleep(l1);
          l2 += l1;
          l1 = (long)(l1 * this.readTimeouts.get_backoff_factor());
        } catch (InterruptedException interruptedException) {
          if (this.orb.transportDebugFlag)
            dprint("readFully(): unexpected exception " + interruptedException.toString()); 
        } 
      } else {
        i += j;
      } 
    } while (i < paramInt2 && l2 < paramLong);
    if (i < paramInt2 && l2 >= paramLong)
      throw this.wrapper.transportReadTimeoutExceeded(new Integer(paramInt2), new Integer(i), new Long(paramLong), new Long(l2)); 
    getConnectionCache().stampTime(this);
  }
  
  public void write(ByteBuffer paramByteBuffer) throws IOException {
    if (shouldUseDirectByteBuffers()) {
      do {
        getSocketChannel().write(paramByteBuffer);
      } while (paramByteBuffer.hasRemaining());
    } else {
      if (!paramByteBuffer.hasArray())
        throw this.wrapper.unexpectedDirectByteBufferWithNonChannelSocket(); 
      byte[] arrayOfByte = paramByteBuffer.array();
      getSocket().getOutputStream().write(arrayOfByte, 0, paramByteBuffer.limit());
      getSocket().getOutputStream().flush();
    } 
    getConnectionCache().stampTime(this);
  }
  
  public void close() {
    try {
      if (this.orb.transportDebugFlag)
        dprint(".close->: " + this); 
      writeLock();
      if (isBusy()) {
        writeUnlock();
        if (this.orb.transportDebugFlag)
          dprint(".close: isBusy so no close: " + this); 
        return;
      } 
      try {
        try {
          sendCloseConnection(GIOPVersion.V1_0);
        } catch (Throwable throwable) {
          this.wrapper.exceptionWhenSendingCloseConnection(throwable);
        } 
        synchronized (this.stateEvent) {
          this.state = 3;
          this.stateEvent.notifyAll();
        } 
        purgeCalls(this.wrapper.connectionRebind(), false, true);
      } catch (Exception exception) {
        if (this.orb.transportDebugFlag)
          dprint(".close: exception: " + this, exception); 
      } 
      try {
        Selector selector = this.orb.getTransportManager().getSelector(0);
        if (selector != null)
          selector.unregisterForEvent(this); 
        if (this.socketChannel != null)
          this.socketChannel.close(); 
        this.socket.close();
      } catch (IOException iOException) {
        if (this.orb.transportDebugFlag)
          dprint(".close: " + this, iOException); 
      } 
      closeConnectionResources();
    } finally {
      if (this.orb.transportDebugFlag)
        dprint(".close<-: " + this); 
    } 
  }
  
  public void closeConnectionResources() {
    if (this.orb.transportDebugFlag)
      dprint(".closeConnectionResources->: " + this); 
    Selector selector = this.orb.getTransportManager().getSelector(0);
    if (selector != null)
      selector.unregisterForEvent(this); 
    try {
      if (this.socketChannel != null)
        this.socketChannel.close(); 
      if (this.socket != null && !this.socket.isClosed())
        this.socket.close(); 
    } catch (IOException iOException) {
      if (this.orb.transportDebugFlag)
        dprint(".closeConnectionResources: " + this, iOException); 
    } 
    if (this.orb.transportDebugFlag)
      dprint(".closeConnectionResources<-: " + this); 
  }
  
  public Acceptor getAcceptor() { return this.acceptor; }
  
  public ContactInfo getContactInfo() { return this.contactInfo; }
  
  public EventHandler getEventHandler() { return this; }
  
  public OutputObject createOutputObject(MessageMediator paramMessageMediator) { throw new RuntimeException("*****SocketOrChannelConnectionImpl.createOutputObject - should not be called."); }
  
  public boolean isServer() { return this.isServer; }
  
  public boolean isBusy() { return (this.serverRequestCount > 0 || getResponseWaitingRoom().numberRegistered() > 0); }
  
  public long getTimeStamp() { return this.timeStamp; }
  
  public void setTimeStamp(long paramLong) { this.timeStamp = paramLong; }
  
  public void setState(String paramString) {
    synchronized (this.stateEvent) {
      if (paramString.equals("ESTABLISHED")) {
        this.state = 2;
        this.stateEvent.notifyAll();
      } 
    } 
  }
  
  public void writeLock() {
    try {
      if (dprintWriteLocks && this.orb.transportDebugFlag)
        dprint(".writeLock->: " + this); 
      while (true) {
        int i = this.state;
        switch (i) {
          case 1:
            synchronized (this.stateEvent) {
              if (this.state != 1)
                continue; 
              try {
                this.stateEvent.wait();
              } catch (InterruptedException interruptedException) {
                if (this.orb.transportDebugFlag)
                  dprint(".writeLock: OPENING InterruptedException: " + this); 
              } 
              continue;
            } 
          case 2:
            synchronized (this.writeEvent) {
              if (!this.writeLocked) {
                this.writeLocked = true;
                return;
              } 
              try {
                while (this.state == 2 && this.writeLocked)
                  this.writeEvent.wait(100L); 
              } catch (InterruptedException interruptedException) {
                if (this.orb.transportDebugFlag)
                  dprint(".writeLock: ESTABLISHED InterruptedException: " + this); 
              } 
              continue;
            } 
          case 5:
            synchronized (this.stateEvent) {
              if (this.state != 5)
                continue; 
              throw this.wrapper.writeErrorSend();
            } 
          case 4:
            synchronized (this.stateEvent) {
              if (this.state != 4)
                continue; 
              throw this.wrapper.connectionCloseRebind();
            } 
        } 
        break;
      } 
      if (this.orb.transportDebugFlag)
        dprint(".writeLock: default: " + this); 
      throw new RuntimeException(".writeLock: bad state");
    } finally {
      if (dprintWriteLocks && this.orb.transportDebugFlag)
        dprint(".writeLock<-: " + this); 
    } 
  }
  
  public void writeUnlock() {
    try {
      if (dprintWriteLocks && this.orb.transportDebugFlag)
        dprint(".writeUnlock->: " + this); 
      synchronized (this.writeEvent) {
        this.writeLocked = false;
        this.writeEvent.notify();
      } 
    } finally {
      if (dprintWriteLocks && this.orb.transportDebugFlag)
        dprint(".writeUnlock<-: " + this); 
    } 
  }
  
  public void sendWithoutLock(OutputObject paramOutputObject) {
    try {
      CDROutputObject cDROutputObject = (CDROutputObject)paramOutputObject;
      cDROutputObject.writeTo(this);
    } catch (IOException iOException) {
      COMM_FAILURE cOMM_FAILURE = this.wrapper.writeErrorSend(iOException);
      purgeCalls(cOMM_FAILURE, false, true);
      throw cOMM_FAILURE;
    } 
  }
  
  public void registerWaiter(MessageMediator paramMessageMediator) { this.responseWaitingRoom.registerWaiter(paramMessageMediator); }
  
  public void unregisterWaiter(MessageMediator paramMessageMediator) { this.responseWaitingRoom.unregisterWaiter(paramMessageMediator); }
  
  public InputObject waitForResponse(MessageMediator paramMessageMediator) { return this.responseWaitingRoom.waitForResponse(paramMessageMediator); }
  
  public void setConnectionCache(ConnectionCache paramConnectionCache) { this.connectionCache = paramConnectionCache; }
  
  public ConnectionCache getConnectionCache() { return this.connectionCache; }
  
  public void setUseSelectThreadToWait(boolean paramBoolean) {
    this.useSelectThreadToWait = paramBoolean;
    setReadGiopHeaderOnly(shouldUseSelectThreadToWait());
  }
  
  public void handleEvent() {
    if (this.orb.transportDebugFlag)
      dprint(".handleEvent->: " + this); 
    getSelectionKey().interestOps(getSelectionKey().interestOps() & (getInterestOps() ^ 0xFFFFFFFF));
    if (shouldUseWorkerThreadForEvent()) {
      NoSuchWorkQueueException noSuchWorkQueueException = null;
      try {
        int i = 0;
        if (shouldReadGiopHeaderOnly()) {
          this.partialMessageMediator = readBits();
          i = this.partialMessageMediator.getThreadPoolToUse();
        } 
        if (this.orb.transportDebugFlag)
          dprint(".handleEvent: addWork to pool: " + i); 
        this.orb.getThreadPoolManager().getThreadPool(i).getWorkQueue(0).addWork(getWork());
      } catch (NoSuchThreadPoolException noSuchThreadPoolException) {
        noSuchWorkQueueException = noSuchThreadPoolException;
      } catch (NoSuchWorkQueueException noSuchWorkQueueException1) {
        noSuchWorkQueueException = noSuchWorkQueueException1;
      } 
      if (noSuchWorkQueueException != null) {
        if (this.orb.transportDebugFlag)
          dprint(".handleEvent: " + noSuchWorkQueueException); 
        INTERNAL iNTERNAL = new INTERNAL("NoSuchThreadPoolException");
        iNTERNAL.initCause(noSuchWorkQueueException);
        throw iNTERNAL;
      } 
    } else {
      if (this.orb.transportDebugFlag)
        dprint(".handleEvent: doWork"); 
      getWork().doWork();
    } 
    if (this.orb.transportDebugFlag)
      dprint(".handleEvent<-: " + this); 
  }
  
  public SelectableChannel getChannel() { return this.socketChannel; }
  
  public int getInterestOps() { return 1; }
  
  public Connection getConnection() { return this; }
  
  public String getName() { return toString(); }
  
  public void doWork() {
    try {
      if (this.orb.transportDebugFlag)
        dprint(".doWork->: " + this); 
      if (!shouldReadGiopHeaderOnly()) {
        read();
      } else {
        CorbaMessageMediator corbaMessageMediator = getPartialMessageMediator();
        corbaMessageMediator = finishReadingBits(corbaMessageMediator);
        if (corbaMessageMediator != null)
          dispatch(corbaMessageMediator); 
      } 
    } catch (Throwable throwable) {
      if (this.orb.transportDebugFlag)
        dprint(".doWork: ignoring Throwable: " + throwable + " " + this); 
    } finally {
      if (this.orb.transportDebugFlag)
        dprint(".doWork<-: " + this); 
    } 
  }
  
  public void setEnqueueTime(long paramLong) { this.enqueueTime = paramLong; }
  
  public long getEnqueueTime() { return this.enqueueTime; }
  
  public boolean shouldReadGiopHeaderOnly() { return this.shouldReadGiopHeaderOnly; }
  
  protected void setReadGiopHeaderOnly(boolean paramBoolean) { this.shouldReadGiopHeaderOnly = paramBoolean; }
  
  public ResponseWaitingRoom getResponseWaitingRoom() { return this.responseWaitingRoom; }
  
  public void serverRequestMapPut(int paramInt, CorbaMessageMediator paramCorbaMessageMediator) { this.serverRequestMap.put(new Integer(paramInt), paramCorbaMessageMediator); }
  
  public CorbaMessageMediator serverRequestMapGet(int paramInt) { return (CorbaMessageMediator)this.serverRequestMap.get(new Integer(paramInt)); }
  
  public void serverRequestMapRemove(int paramInt) { this.serverRequestMap.remove(new Integer(paramInt)); }
  
  public Socket getSocket() { return this.socket; }
  
  public void serverRequestProcessingBegins() { this.serverRequestCount++; }
  
  public void serverRequestProcessingEnds() { this.serverRequestCount--; }
  
  public int getNextRequestId() { return this.requestId++; }
  
  public ORB getBroker() { return this.orb; }
  
  public CodeSetComponentInfo.CodeSetContext getCodeSetContext() {
    if (this.codeSetContext == null)
      synchronized (this) {
        return this.codeSetContext;
      }  
    return this.codeSetContext;
  }
  
  public void setCodeSetContext(CodeSetComponentInfo.CodeSetContext paramCodeSetContext) {
    if (this.codeSetContext == null) {
      if (OSFCodeSetRegistry.lookupEntry(paramCodeSetContext.getCharCodeSet()) == null || OSFCodeSetRegistry.lookupEntry(paramCodeSetContext.getWCharCodeSet()) == null)
        throw this.wrapper.badCodesetsFromClient(); 
      this.codeSetContext = paramCodeSetContext;
    } 
  }
  
  public MessageMediator clientRequestMapGet(int paramInt) { return this.responseWaitingRoom.getMessageMediator(paramInt); }
  
  public void clientReply_1_1_Put(MessageMediator paramMessageMediator) { this.clientReply_1_1 = paramMessageMediator; }
  
  public MessageMediator clientReply_1_1_Get() { return this.clientReply_1_1; }
  
  public void clientReply_1_1_Remove() { this.clientReply_1_1 = null; }
  
  public void serverRequest_1_1_Put(MessageMediator paramMessageMediator) { this.serverRequest_1_1 = paramMessageMediator; }
  
  public MessageMediator serverRequest_1_1_Get() { return this.serverRequest_1_1; }
  
  public void serverRequest_1_1_Remove() { this.serverRequest_1_1 = null; }
  
  protected String getStateString(int paramInt) {
    synchronized (this.stateEvent) {
      switch (paramInt) {
        case 1:
          return "OPENING";
        case 2:
          return "ESTABLISHED";
        case 3:
          return "CLOSE_SENT";
        case 4:
          return "CLOSE_RECVD";
        case 5:
          return "ABORT";
      } 
      return "???";
    } 
  }
  
  public boolean isPostInitialContexts() { return this.postInitialContexts; }
  
  public void setPostInitialContexts() { this.postInitialContexts = true; }
  
  public void purgeCalls(SystemException paramSystemException, boolean paramBoolean1, boolean paramBoolean2) {
    i = paramSystemException.minor;
    try {
      if (this.orb.transportDebugFlag)
        dprint(".purgeCalls->: " + i + "/" + paramBoolean1 + "/" + paramBoolean2 + " " + this); 
      synchronized (this.stateEvent) {
        if (this.state == 5 || this.state == 4) {
          if (this.orb.transportDebugFlag)
            dprint(".purgeCalls: exiting since state is: " + getStateString(this.state) + " " + this); 
          return;
        } 
      } 
      try {
        if (!paramBoolean2)
          writeLock(); 
      } catch (SystemException systemException) {
        if (this.orb.transportDebugFlag)
          dprint(".purgeCalls: SystemException" + systemException + "; continuing " + this); 
      } 
      synchronized (this.stateEvent) {
        if (i == 1398079697) {
          this.state = 4;
          paramSystemException.completed = CompletionStatus.COMPLETED_NO;
        } else {
          this.state = 5;
          paramSystemException.completed = CompletionStatus.COMPLETED_MAYBE;
        } 
        this.stateEvent.notifyAll();
      } 
      try {
        this.socket.getInputStream().close();
        this.socket.getOutputStream().close();
        this.socket.close();
      } catch (Exception exception) {
        if (this.orb.transportDebugFlag)
          dprint(".purgeCalls: Exception closing socket: " + exception + " " + this); 
      } 
      this.responseWaitingRoom.signalExceptionToAllWaiters(paramSystemException);
    } finally {
      if (this.contactInfo != null) {
        ((OutboundConnectionCache)getConnectionCache()).remove(this.contactInfo);
      } else if (this.acceptor != null) {
        ((InboundConnectionCache)getConnectionCache()).remove(this);
      } 
      writeUnlock();
      if (this.orb.transportDebugFlag)
        dprint(".purgeCalls<-: " + i + "/" + paramBoolean1 + "/" + paramBoolean2 + " " + this); 
    } 
  }
  
  public void sendCloseConnection(GIOPVersion paramGIOPVersion) throws IOException {
    Message message = MessageBase.createCloseConnection(paramGIOPVersion);
    sendHelper(paramGIOPVersion, message);
  }
  
  public void sendMessageError(GIOPVersion paramGIOPVersion) throws IOException {
    Message message = MessageBase.createMessageError(paramGIOPVersion);
    sendHelper(paramGIOPVersion, message);
  }
  
  public void sendCancelRequest(GIOPVersion paramGIOPVersion, int paramInt) throws IOException {
    CancelRequestMessage cancelRequestMessage = MessageBase.createCancelRequest(paramGIOPVersion, paramInt);
    sendHelper(paramGIOPVersion, cancelRequestMessage);
  }
  
  protected void sendHelper(GIOPVersion paramGIOPVersion, Message paramMessage) throws IOException {
    CDROutputObject cDROutputObject = OutputStreamFactory.newCDROutputObject(this.orb, null, paramGIOPVersion, this, paramMessage, (byte)1);
    paramMessage.write(cDROutputObject);
    cDROutputObject.writeTo(this);
  }
  
  public void sendCancelRequestWithLock(GIOPVersion paramGIOPVersion, int paramInt) throws IOException {
    writeLock();
    try {
      sendCancelRequest(paramGIOPVersion, paramInt);
    } finally {
      writeUnlock();
    } 
  }
  
  public final void setCodeBaseIOR(IOR paramIOR) { this.codeBaseServerIOR = paramIOR; }
  
  public final IOR getCodeBaseIOR() { return this.codeBaseServerIOR; }
  
  public final CodeBase getCodeBase() { return this.cachedCodeBase; }
  
  protected void setReadTimeouts(ReadTimeouts paramReadTimeouts) { this.readTimeouts = paramReadTimeouts; }
  
  protected void setPartialMessageMediator(CorbaMessageMediator paramCorbaMessageMediator) { this.partialMessageMediator = paramCorbaMessageMediator; }
  
  protected CorbaMessageMediator getPartialMessageMediator() { return this.partialMessageMediator; }
  
  public String toString() {
    synchronized (this.stateEvent) {
      return "SocketOrChannelConnectionImpl[ " + ((this.socketChannel == null) ? this.socket.toString() : this.socketChannel.toString()) + " " + getStateString(this.state) + " " + shouldUseSelectThreadToWait() + " " + shouldUseWorkerThreadForEvent() + " " + shouldReadGiopHeaderOnly() + "]";
    } 
  }
  
  public void dprint(String paramString) { ORBUtility.dprint("SocketOrChannelConnectionImpl", paramString); }
  
  protected void dprint(String paramString, Throwable paramThrowable) {
    dprint(paramString);
    paramThrowable.printStackTrace(System.out);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\transport\SocketOrChannelConnectionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */