package com.sun.jndi.ldap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import javax.naming.CommunicationException;
import javax.naming.InterruptedNamingException;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import javax.naming.ldap.Control;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import sun.misc.IOUtils;

public final class Connection implements Runnable {
  private static final boolean debug = false;
  
  private static final int dump = 0;
  
  private final Thread worker;
  
  private boolean v3 = true;
  
  public final String host;
  
  public final int port;
  
  private boolean bound = false;
  
  private OutputStream traceFile = null;
  
  private String traceTagIn = null;
  
  private String traceTagOut = null;
  
  public InputStream inStream;
  
  public OutputStream outStream;
  
  public Socket sock;
  
  private final LdapClient parent;
  
  private int outMsgId = 0;
  
  private LdapRequest pendingRequests = null;
  
  int readTimeout;
  
  int connectTimeout;
  
  private static final boolean IS_HOSTNAME_VERIFICATION_DISABLED = hostnameVerificationDisabledValue();
  
  private Object pauseLock = new Object();
  
  private boolean paused = false;
  
  private static boolean hostnameVerificationDisabledValue() {
    PrivilegedAction privilegedAction = () -> System.getProperty("com.sun.jndi.ldap.object.disableEndpointIdentification");
    String str = (String)AccessController.doPrivileged(privilegedAction);
    return (str == null) ? false : (str.isEmpty() ? true : Boolean.parseBoolean(str));
  }
  
  void setV3(boolean paramBoolean) { this.v3 = paramBoolean; }
  
  void setBound() { this.bound = true; }
  
  Connection(LdapClient paramLdapClient, String paramString1, int paramInt1, String paramString2, int paramInt2, int paramInt3, OutputStream paramOutputStream) throws NamingException {
    this.host = paramString1;
    this.port = paramInt1;
    this.parent = paramLdapClient;
    this.readTimeout = paramInt3;
    this.connectTimeout = paramInt2;
    if (paramOutputStream != null) {
      this.traceFile = paramOutputStream;
      this.traceTagIn = "<- " + paramString1 + ":" + paramInt1 + "\n\n";
      this.traceTagOut = "-> " + paramString1 + ":" + paramInt1 + "\n\n";
    } 
    try {
      this.sock = createSocket(paramString1, paramInt1, paramString2, paramInt2);
      this.inStream = new BufferedInputStream(this.sock.getInputStream());
      this.outStream = new BufferedOutputStream(this.sock.getOutputStream());
    } catch (InvocationTargetException invocationTargetException) {
      Throwable throwable = invocationTargetException.getTargetException();
      CommunicationException communicationException = new CommunicationException(paramString1 + ":" + paramInt1);
      communicationException.setRootCause(throwable);
      throw communicationException;
    } catch (Exception exception) {
      CommunicationException communicationException = new CommunicationException(paramString1 + ":" + paramInt1);
      communicationException.setRootCause(exception);
      throw communicationException;
    } 
    this.worker = Obj.helper.createThread(this);
    this.worker.setDaemon(true);
    this.worker.start();
  }
  
  private Object createInetSocketAddress(String paramString, int paramInt) throws NoSuchMethodException {
    try {
      Class clazz = Class.forName("java.net.InetSocketAddress");
      Constructor constructor = clazz.getConstructor(new Class[] { String.class, int.class });
      return constructor.newInstance(new Object[] { paramString, new Integer(paramInt) });
    } catch (ClassNotFoundException|InstantiationException|InvocationTargetException|IllegalAccessException classNotFoundException) {
      throw new NoSuchMethodException();
    } 
  }
  
  private Socket createSocket(String paramString1, int paramInt1, String paramString2, int paramInt2) throws Exception {
    Socket socket = null;
    if (paramString2 != null) {
      Class clazz = Obj.helper.loadClass(paramString2);
      Method method1 = clazz.getMethod("getDefault", new Class[0]);
      Object object = method1.invoke(null, new Object[0]);
      Method method2 = null;
      if (paramInt2 > 0)
        try {
          method2 = clazz.getMethod("createSocket", new Class[0]);
          Method method = Socket.class.getMethod("connect", new Class[] { Class.forName("java.net.SocketAddress"), int.class });
          Object object1 = createInetSocketAddress(paramString1, paramInt1);
          socket = (Socket)method2.invoke(object, new Object[0]);
          method.invoke(socket, new Object[] { object1, new Integer(paramInt2) });
        } catch (NoSuchMethodException noSuchMethodException) {} 
      if (socket == null) {
        method2 = clazz.getMethod("createSocket", new Class[] { String.class, int.class });
        socket = (Socket)method2.invoke(object, new Object[] { paramString1, new Integer(paramInt1) });
      } 
    } else {
      if (paramInt2 > 0)
        try {
          Constructor constructor = Socket.class.getConstructor(new Class[0]);
          Method method = Socket.class.getMethod("connect", new Class[] { Class.forName("java.net.SocketAddress"), int.class });
          Object object = createInetSocketAddress(paramString1, paramInt1);
          socket = (Socket)constructor.newInstance(new Object[0]);
          method.invoke(socket, new Object[] { object, new Integer(paramInt2) });
        } catch (NoSuchMethodException noSuchMethodException) {} 
      if (socket == null)
        socket = new Socket(paramString1, paramInt1); 
    } 
    if (socket instanceof SSLSocket) {
      SSLSocket sSLSocket = (SSLSocket)socket;
      if (!IS_HOSTNAME_VERIFICATION_DISABLED) {
        SSLParameters sSLParameters = sSLSocket.getSSLParameters();
        sSLParameters.setEndpointIdentificationAlgorithm("LDAPS");
        sSLSocket.setSSLParameters(sSLParameters);
      } 
      if (paramInt2 > 0) {
        int i = sSLSocket.getSoTimeout();
        sSLSocket.setSoTimeout(paramInt2);
        sSLSocket.startHandshake();
        sSLSocket.setSoTimeout(i);
      } 
    } 
    return socket;
  }
  
  int getMsgId() { return ++this.outMsgId; }
  
  LdapRequest writeRequest(BerEncoder paramBerEncoder, int paramInt) throws IOException { return writeRequest(paramBerEncoder, paramInt, false, -1); }
  
  LdapRequest writeRequest(BerEncoder paramBerEncoder, int paramInt, boolean paramBoolean) throws IOException { return writeRequest(paramBerEncoder, paramInt, paramBoolean, -1); }
  
  LdapRequest writeRequest(BerEncoder paramBerEncoder, int paramInt1, boolean paramBoolean, int paramInt2) throws IOException {
    LdapRequest ldapRequest = new LdapRequest(paramInt1, paramBoolean, paramInt2);
    addRequest(ldapRequest);
    if (this.traceFile != null)
      Ber.dumpBER(this.traceFile, this.traceTagOut, paramBerEncoder.getBuf(), 0, paramBerEncoder.getDataLen()); 
    unpauseReader();
    try {
      synchronized (this) {
        this.outStream.write(paramBerEncoder.getBuf(), 0, paramBerEncoder.getDataLen());
        this.outStream.flush();
      } 
    } catch (IOException iOException) {
      cleanup(null, true);
      throw this.closureReason = iOException;
    } 
    return ldapRequest;
  }
  
  BerDecoder readReply(LdapRequest paramLdapRequest) throws IOException, NamingException {
    long l1 = 0L;
    long l2 = 0L;
    BerDecoder berDecoder;
    while ((berDecoder = paramLdapRequest.getReplyBer()) == null && (this.readTimeout <= 0 || l1 < this.readTimeout)) {
      try {
        synchronized (this) {
          if (this.sock == null)
            throw new ServiceUnavailableException(this.host + ":" + this.port + "; socket closed"); 
        } 
        synchronized (paramLdapRequest) {
          berDecoder = paramLdapRequest.getReplyBer();
          if (berDecoder == null) {
            if (this.readTimeout > 0) {
              long l = System.nanoTime();
              paramLdapRequest.wait(this.readTimeout - l1);
              l2 += System.nanoTime() - l;
              l1 += l2 / 1000000L;
              l2 %= 1000000L;
            } else {
              paramLdapRequest.wait();
            } 
          } else {
            break;
          } 
        } 
      } catch (InterruptedException interruptedException) {
        throw new InterruptedNamingException("Interrupted during LDAP operation");
      } 
    } 
    if (berDecoder == null && l1 >= this.readTimeout) {
      abandonRequest(paramLdapRequest, null);
      throw new NamingException("LDAP response read timed out, timeout used:" + this.readTimeout + "ms.");
    } 
    return berDecoder;
  }
  
  private void addRequest(LdapRequest paramLdapRequest) {
    LdapRequest ldapRequest = this.pendingRequests;
    if (ldapRequest == null) {
      this.pendingRequests = paramLdapRequest;
      paramLdapRequest.next = null;
    } else {
      paramLdapRequest.next = this.pendingRequests;
      this.pendingRequests = paramLdapRequest;
    } 
  }
  
  LdapRequest findRequest(int paramInt) {
    for (LdapRequest ldapRequest = this.pendingRequests; ldapRequest != null; ldapRequest = ldapRequest.next) {
      if (ldapRequest.msgId == paramInt)
        return ldapRequest; 
    } 
    return null;
  }
  
  void removeRequest(LdapRequest paramLdapRequest) {
    LdapRequest ldapRequest1 = this.pendingRequests;
    LdapRequest ldapRequest2 = null;
    while (ldapRequest1 != null) {
      if (ldapRequest1 == paramLdapRequest) {
        ldapRequest1.cancel();
        if (ldapRequest2 != null) {
          ldapRequest2.next = ldapRequest1.next;
        } else {
          this.pendingRequests = ldapRequest1.next;
        } 
        ldapRequest1.next = null;
      } 
      ldapRequest2 = ldapRequest1;
      ldapRequest1 = ldapRequest1.next;
    } 
  }
  
  void abandonRequest(LdapRequest paramLdapRequest, Control[] paramArrayOfControl) {
    removeRequest(paramLdapRequest);
    BerEncoder berEncoder = new BerEncoder(256);
    int i = getMsgId();
    try {
      berEncoder.beginSeq(48);
      berEncoder.encodeInt(i);
      berEncoder.encodeInt(paramLdapRequest.msgId, 80);
      if (this.v3)
        LdapClient.encodeControls(berEncoder, paramArrayOfControl); 
      berEncoder.endSeq();
      if (this.traceFile != null)
        Ber.dumpBER(this.traceFile, this.traceTagOut, berEncoder.getBuf(), 0, berEncoder.getDataLen()); 
      synchronized (this) {
        this.outStream.write(berEncoder.getBuf(), 0, berEncoder.getDataLen());
        this.outStream.flush();
      } 
    } catch (IOException iOException) {}
  }
  
  void abandonOutstandingReqs(Control[] paramArrayOfControl) {
    LdapRequest ldapRequest = this.pendingRequests;
    while (ldapRequest != null) {
      abandonRequest(ldapRequest, paramArrayOfControl);
      this.pendingRequests = ldapRequest = ldapRequest.next;
    } 
  }
  
  private void ldapUnbind(Control[] paramArrayOfControl) {
    BerEncoder berEncoder = new BerEncoder(256);
    int i = getMsgId();
    try {
      berEncoder.beginSeq(48);
      berEncoder.encodeInt(i);
      berEncoder.encodeByte(66);
      berEncoder.encodeByte(0);
      if (this.v3)
        LdapClient.encodeControls(berEncoder, paramArrayOfControl); 
      berEncoder.endSeq();
      if (this.traceFile != null)
        Ber.dumpBER(this.traceFile, this.traceTagOut, berEncoder.getBuf(), 0, berEncoder.getDataLen()); 
      synchronized (this) {
        this.outStream.write(berEncoder.getBuf(), 0, berEncoder.getDataLen());
        this.outStream.flush();
      } 
    } catch (IOException iOException) {}
  }
  
  void cleanup(Control[] paramArrayOfControl, boolean paramBoolean) {
    boolean bool = false;
    synchronized (this) {
      this.useable = false;
      if (this.sock != null) {
        try {
          if (!paramBoolean)
            abandonOutstandingReqs(paramArrayOfControl); 
          if (this.bound)
            ldapUnbind(paramArrayOfControl); 
        } finally {
          try {
            this.outStream.flush();
            this.sock.close();
            unpauseReader();
          } catch (IOException iOException) {}
          if (!paramBoolean)
            for (LdapRequest ldapRequest = this.pendingRequests; ldapRequest != null; ldapRequest = ldapRequest.next)
              ldapRequest.cancel();  
          this.sock = null;
        } 
        bool = paramBoolean;
      } 
      if (bool) {
        LdapRequest ldapRequest = this.pendingRequests;
        while (ldapRequest != null) {
          synchronized (ldapRequest) {
            ldapRequest.notify();
            ldapRequest = ldapRequest.next;
          } 
        } 
      } 
    } 
    if (bool)
      this.parent.processConnectionClosure(); 
  }
  
  public void replaceStreams(InputStream paramInputStream, OutputStream paramOutputStream) {
    this.inStream = paramInputStream;
    try {
      this.outStream.flush();
    } catch (IOException iOException) {}
    this.outStream = paramOutputStream;
  }
  
  private InputStream getInputStream() { return this.inStream; }
  
  private void unpauseReader() {
    synchronized (this.pauseLock) {
      if (this.paused) {
        this.paused = false;
        this.pauseLock.notify();
      } 
    } 
  }
  
  private void pauseReader() {
    this.paused = true;
    try {
      while (this.paused)
        this.pauseLock.wait(); 
    } catch (InterruptedException interruptedException) {
      throw new InterruptedIOException("Pause/unpause reader has problems.");
    } 
  }
  
  public void run() {
    InputStream inputStream = null;
    try {
      while (true) {
        try {
          byte[] arrayOfByte1 = new byte[129];
          int j = 0;
          byte b = 0;
          int k = 0;
          inputStream = getInputStream();
          int i = inputStream.read(arrayOfByte1, j, 1);
          if (i < 0) {
            if (inputStream != getInputStream())
              continue; 
            break;
          } 
          if (arrayOfByte1[j++] != 48)
            continue; 
          i = inputStream.read(arrayOfByte1, j, 1);
          if (i < 0)
            break; 
          b = arrayOfByte1[j++];
          if ((b & 0x80) == 128) {
            k = b & 0x7F;
            i = 0;
            boolean bool = false;
            while (i < k) {
              int n = inputStream.read(arrayOfByte1, j + i, k - i);
              if (n < 0) {
                bool = true;
                break;
              } 
              i += n;
            } 
            if (bool)
              break; 
            b = 0;
            for (int m = 0; m < k; m++)
              b = (b << 8) + (arrayOfByte1[j + m] & 0xFF); 
            j += i;
          } 
          byte[] arrayOfByte2 = IOUtils.readFully(inputStream, b, false);
          arrayOfByte1 = Arrays.copyOf(arrayOfByte1, j + arrayOfByte2.length);
          System.arraycopy(arrayOfByte2, 0, arrayOfByte1, j, arrayOfByte2.length);
          j += arrayOfByte2.length;
          try {
            BerDecoder berDecoder = new BerDecoder(arrayOfByte1, 0, j);
            if (this.traceFile != null)
              Ber.dumpBER(this.traceFile, this.traceTagIn, arrayOfByte1, 0, j); 
            berDecoder.parseSeq(null);
            int m = berDecoder.parseInt();
            berDecoder.reset();
            boolean bool = false;
            if (m == 0) {
              this.parent.processUnsolicited(berDecoder);
              continue;
            } 
            LdapRequest ldapRequest = findRequest(m);
            if (ldapRequest != null)
              synchronized (this.pauseLock) {
                bool = ldapRequest.addReplyBer(berDecoder);
                if (bool)
                  pauseReader(); 
              }  
          } catch (DecodeException decodeException) {}
        } catch (IOException iOException) {
          if (inputStream != getInputStream())
            continue; 
          throw iOException;
        } 
      } 
    } catch (IOException iOException) {
      this.closureReason = iOException;
    } finally {
      cleanup(null, true);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\Connection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */