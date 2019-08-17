package com.sun.xml.internal.messaging.saaj.client.p2p;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.util.Base64;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ParseUtil;
import com.sun.xml.internal.messaging.saaj.util.SAAJUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.Provider;
import java.security.Security;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

class HttpSOAPConnection extends SOAPConnection {
  public static final String vmVendor = SAAJUtil.getSystemProperty("java.vendor.url");
  
  private static final String sunVmVendor = "http://java.sun.com/";
  
  private static final String ibmVmVendor = "http://www.ibm.com/";
  
  private static final boolean isSunVM = "http://java.sun.com/".equals(vmVendor);
  
  private static final boolean isIBMVM = "http://www.ibm.com/".equals(vmVendor);
  
  private static final String JAXM_URLENDPOINT = "javax.xml.messaging.URLEndpoint";
  
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.client.p2p", "com.sun.xml.internal.messaging.saaj.client.p2p.LocalStrings");
  
  MessageFactory messageFactory = null;
  
  boolean closed = false;
  
  private static final String SSL_PKG;
  
  private static final String SSL_PROVIDER;
  
  private static final int dL = 0;
  
  public HttpSOAPConnection() throws SOAPException {
    try {
      this.messageFactory = MessageFactory.newInstance("Dynamic Protocol");
    } catch (NoSuchMethodError noSuchMethodError) {
      this.messageFactory = MessageFactory.newInstance();
    } catch (Exception exception) {
      log.log(Level.SEVERE, "SAAJ0001.p2p.cannot.create.msg.factory", exception);
      throw new SOAPExceptionImpl("Unable to create message factory", exception);
    } 
  }
  
  public void close() throws SOAPException {
    if (this.closed) {
      log.severe("SAAJ0002.p2p.close.already.closed.conn");
      throw new SOAPExceptionImpl("Connection already closed");
    } 
    this.messageFactory = null;
    this.closed = true;
  }
  
  public SOAPMessage call(SOAPMessage paramSOAPMessage, Object paramObject) throws SOAPException {
    if (this.closed) {
      log.severe("SAAJ0003.p2p.call.already.closed.conn");
      throw new SOAPExceptionImpl("Connection is closed");
    } 
    Class clazz = null;
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    try {
      if (classLoader != null) {
        clazz = classLoader.loadClass("javax.xml.messaging.URLEndpoint");
      } else {
        clazz = Class.forName("javax.xml.messaging.URLEndpoint");
      } 
    } catch (ClassNotFoundException classNotFoundException) {
      if (log.isLoggable(Level.FINEST))
        log.finest("SAAJ0090.p2p.endpoint.available.only.for.JAXM"); 
    } 
    if (clazz != null && clazz.isInstance(paramObject)) {
      String str = null;
      try {
        Method method = clazz.getMethod("getURL", (Class[])null);
        str = (String)method.invoke(paramObject, (Object[])null);
      } catch (Exception exception) {
        log.log(Level.SEVERE, "SAAJ0004.p2p.internal.err", exception);
        throw new SOAPExceptionImpl("Internal error: " + exception.getMessage());
      } 
      try {
        paramObject = new URL(str);
      } catch (MalformedURLException malformedURLException) {
        log.log(Level.SEVERE, "SAAJ0005.p2p.", malformedURLException);
        throw new SOAPExceptionImpl("Bad URL: " + malformedURLException.getMessage());
      } 
    } 
    if (paramObject instanceof String)
      try {
        paramObject = new URL((String)paramObject);
      } catch (MalformedURLException malformedURLException) {
        log.log(Level.SEVERE, "SAAJ0006.p2p.bad.URL", malformedURLException);
        throw new SOAPExceptionImpl("Bad URL: " + malformedURLException.getMessage());
      }  
    if (paramObject instanceof URL)
      try {
        return post(paramSOAPMessage, (URL)paramObject);
      } catch (Exception exception) {
        throw new SOAPExceptionImpl(exception);
      }  
    log.severe("SAAJ0007.p2p.bad.endPoint.type");
    throw new SOAPExceptionImpl("Bad endPoint type " + paramObject);
  }
  
  SOAPMessage post(SOAPMessage paramSOAPMessage, URL paramURL) throws SOAPException, IOException {
    boolean bool = false;
    URL uRL = null;
    httpURLConnection = null;
    int i = 0;
    try {
      if (paramURL.getProtocol().equals("https"))
        initHttps(); 
      URI uRI = new URI(paramURL.toString());
      String str = uRI.getRawUserInfo();
      uRL = paramURL;
      if (!uRL.getProtocol().equalsIgnoreCase("http") && !uRL.getProtocol().equalsIgnoreCase("https")) {
        log.severe("SAAJ0052.p2p.protocol.mustbe.http.or.https");
        throw new IllegalArgumentException("Protocol " + uRL.getProtocol() + " not supported in URL " + uRL);
      } 
      httpURLConnection = createConnection(uRL);
      httpURLConnection.setRequestMethod("POST");
      httpURLConnection.setDoOutput(true);
      httpURLConnection.setDoInput(true);
      httpURLConnection.setUseCaches(false);
      httpURLConnection.setInstanceFollowRedirects(true);
      if (paramSOAPMessage.saveRequired())
        paramSOAPMessage.saveChanges(); 
      MimeHeaders mimeHeaders = paramSOAPMessage.getMimeHeaders();
      Iterator iterator = mimeHeaders.getAllHeaders();
      boolean bool1 = false;
      while (iterator.hasNext()) {
        MimeHeader mimeHeader = (MimeHeader)iterator.next();
        String[] arrayOfString = mimeHeaders.getHeader(mimeHeader.getName());
        if (arrayOfString.length == 1) {
          httpURLConnection.setRequestProperty(mimeHeader.getName(), mimeHeader.getValue());
        } else {
          StringBuffer stringBuffer = new StringBuffer();
          for (byte b = 0; b < arrayOfString.length; b++) {
            if (b)
              stringBuffer.append(','); 
            stringBuffer.append(arrayOfString[b]);
          } 
          httpURLConnection.setRequestProperty(mimeHeader.getName(), stringBuffer.toString());
        } 
        if ("Authorization".equals(mimeHeader.getName())) {
          bool1 = true;
          if (log.isLoggable(Level.FINE))
            log.fine("SAAJ0091.p2p.https.auth.in.POST.true"); 
        } 
      } 
      if (!bool1 && str != null)
        initAuthUserInfo(httpURLConnection, str); 
      outputStream = httpURLConnection.getOutputStream();
      try {
        paramSOAPMessage.writeTo(outputStream);
        outputStream.flush();
      } finally {
        outputStream.close();
      } 
      httpURLConnection.connect();
      try {
        i = httpURLConnection.getResponseCode();
        if (i == 500) {
          bool = true;
        } else if (i / 100 != 2) {
          log.log(Level.SEVERE, "SAAJ0008.p2p.bad.response", new String[] { httpURLConnection.getResponseMessage() });
          throw new SOAPExceptionImpl("Bad response: (" + i + httpURLConnection.getResponseMessage());
        } 
      } catch (IOException iOException) {
        i = httpURLConnection.getResponseCode();
        if (i == 500) {
          bool = true;
        } else {
          throw iOException;
        } 
      } 
    } catch (SOAPException sOAPException) {
      throw sOAPException;
    } catch (Exception exception) {
      log.severe("SAAJ0009.p2p.msg.send.failed");
      throw new SOAPExceptionImpl("Message send failed", exception);
    } 
    SOAPMessage sOAPMessage = null;
    inputStream = null;
    if (i == 200 || bool)
      try {
        MimeHeaders mimeHeaders = new MimeHeaders();
        for (byte b = 1;; b++) {
          String str1 = httpURLConnection.getHeaderFieldKey(b);
          String str2 = httpURLConnection.getHeaderField(b);
          if (str1 == null && str2 == null)
            break; 
          if (str1 != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(str2, ",");
            while (stringTokenizer.hasMoreTokens())
              mimeHeaders.addHeader(str1, stringTokenizer.nextToken().trim()); 
          } 
        } 
        inputStream = bool ? httpURLConnection.getErrorStream() : httpURLConnection.getInputStream();
        byte[] arrayOfByte = readFully(inputStream);
        int j = (httpURLConnection.getContentLength() == -1) ? arrayOfByte.length : httpURLConnection.getContentLength();
        if (j == 0) {
          sOAPMessage = null;
          log.warning("SAAJ0014.p2p.content.zero");
        } else {
          ByteInputStream byteInputStream = new ByteInputStream(arrayOfByte, j);
          sOAPMessage = this.messageFactory.createMessage(mimeHeaders, byteInputStream);
        } 
      } catch (SOAPException sOAPException) {
        throw sOAPException;
      } catch (Exception exception) {
        log.log(Level.SEVERE, "SAAJ0010.p2p.cannot.read.resp", exception);
        throw new SOAPExceptionImpl("Unable to read response: " + exception.getMessage());
      } finally {
        if (inputStream != null)
          inputStream.close(); 
        httpURLConnection.disconnect();
      }  
    return sOAPMessage;
  }
  
  public SOAPMessage get(Object paramObject) throws SOAPException {
    if (this.closed) {
      log.severe("SAAJ0011.p2p.get.already.closed.conn");
      throw new SOAPExceptionImpl("Connection is closed");
    } 
    Class clazz = null;
    try {
      clazz = Class.forName("javax.xml.messaging.URLEndpoint");
    } catch (Exception exception) {}
    if (clazz != null && clazz.isInstance(paramObject)) {
      String str = null;
      try {
        Method method = clazz.getMethod("getURL", (Class[])null);
        str = (String)method.invoke(paramObject, (Object[])null);
      } catch (Exception exception) {
        log.severe("SAAJ0004.p2p.internal.err");
        throw new SOAPExceptionImpl("Internal error: " + exception.getMessage());
      } 
      try {
        paramObject = new URL(str);
      } catch (MalformedURLException malformedURLException) {
        log.severe("SAAJ0005.p2p.");
        throw new SOAPExceptionImpl("Bad URL: " + malformedURLException.getMessage());
      } 
    } 
    if (paramObject instanceof String)
      try {
        paramObject = new URL((String)paramObject);
      } catch (MalformedURLException malformedURLException) {
        log.severe("SAAJ0006.p2p.bad.URL");
        throw new SOAPExceptionImpl("Bad URL: " + malformedURLException.getMessage());
      }  
    if (paramObject instanceof URL)
      try {
        return doGet((URL)paramObject);
      } catch (Exception exception) {
        throw new SOAPExceptionImpl(exception);
      }  
    throw new SOAPExceptionImpl("Bad endPoint type " + paramObject);
  }
  
  SOAPMessage doGet(URL paramURL) throws SOAPException, IOException {
    boolean bool = false;
    URL uRL = null;
    httpURLConnection = null;
    int i = 0;
    try {
      if (paramURL.getProtocol().equals("https"))
        initHttps(); 
      URI uRI = new URI(paramURL.toString());
      String str = uRI.getRawUserInfo();
      uRL = paramURL;
      if (!uRL.getProtocol().equalsIgnoreCase("http") && !uRL.getProtocol().equalsIgnoreCase("https")) {
        log.severe("SAAJ0052.p2p.protocol.mustbe.http.or.https");
        throw new IllegalArgumentException("Protocol " + uRL.getProtocol() + " not supported in URL " + uRL);
      } 
      httpURLConnection = createConnection(uRL);
      httpURLConnection.setRequestMethod("GET");
      httpURLConnection.setDoOutput(true);
      httpURLConnection.setDoInput(true);
      httpURLConnection.setUseCaches(false);
      httpURLConnection.setFollowRedirects(true);
      httpURLConnection.connect();
      try {
        i = httpURLConnection.getResponseCode();
        if (i == 500) {
          bool = true;
        } else if (i / 100 != 2) {
          log.log(Level.SEVERE, "SAAJ0008.p2p.bad.response", new String[] { httpURLConnection.getResponseMessage() });
          throw new SOAPExceptionImpl("Bad response: (" + i + httpURLConnection.getResponseMessage());
        } 
      } catch (IOException iOException) {
        i = httpURLConnection.getResponseCode();
        if (i == 500) {
          bool = true;
        } else {
          throw iOException;
        } 
      } 
    } catch (SOAPException sOAPException) {
      throw sOAPException;
    } catch (Exception exception) {
      log.severe("SAAJ0012.p2p.get.failed");
      throw new SOAPExceptionImpl("Get failed", exception);
    } 
    SOAPMessage sOAPMessage = null;
    inputStream = null;
    if (i == 200 || bool)
      try {
        MimeHeaders mimeHeaders = new MimeHeaders();
        for (byte b = 1;; b++) {
          String str1 = httpURLConnection.getHeaderFieldKey(b);
          String str2 = httpURLConnection.getHeaderField(b);
          if (str1 == null && str2 == null)
            break; 
          if (str1 != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(str2, ",");
            while (stringTokenizer.hasMoreTokens())
              mimeHeaders.addHeader(str1, stringTokenizer.nextToken().trim()); 
          } 
        } 
        inputStream = bool ? httpURLConnection.getErrorStream() : httpURLConnection.getInputStream();
        if (inputStream == null || httpURLConnection.getContentLength() == 0 || inputStream.available() == 0) {
          sOAPMessage = null;
          log.warning("SAAJ0014.p2p.content.zero");
        } else {
          sOAPMessage = this.messageFactory.createMessage(mimeHeaders, inputStream);
        } 
      } catch (SOAPException sOAPException) {
        throw sOAPException;
      } catch (Exception exception) {
        log.log(Level.SEVERE, "SAAJ0010.p2p.cannot.read.resp", exception);
        throw new SOAPExceptionImpl("Unable to read response: " + exception.getMessage());
      } finally {
        if (inputStream != null)
          inputStream.close(); 
        httpURLConnection.disconnect();
      }  
    return sOAPMessage;
  }
  
  private byte[] readFully(InputStream paramInputStream) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byte[] arrayOfByte = new byte[1024];
    int i = 0;
    while ((i = paramInputStream.read(arrayOfByte)) != -1)
      byteArrayOutputStream.write(arrayOfByte, 0, i); 
    return byteArrayOutputStream.toByteArray();
  }
  
  private void initHttps() throws SOAPException {
    String str = SAAJUtil.getSystemProperty("java.protocol.handler.pkgs");
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "SAAJ0053.p2p.providers", new String[] { str }); 
    if (str == null || str.indexOf(SSL_PKG) < 0) {
      if (str == null) {
        str = SSL_PKG;
      } else {
        str = str + "|" + SSL_PKG;
      } 
      System.setProperty("java.protocol.handler.pkgs", str);
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "SAAJ0054.p2p.set.providers", new String[] { str }); 
      try {
        Class clazz = Class.forName(SSL_PROVIDER);
        Provider provider = (Provider)clazz.newInstance();
        Security.addProvider(provider);
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, "SAAJ0055.p2p.added.ssl.provider", new String[] { SSL_PROVIDER }); 
      } catch (Exception exception) {}
    } 
  }
  
  private void initAuthUserInfo(HttpURLConnection paramHttpURLConnection, String paramString) {
    if (paramString != null) {
      String str2;
      String str1;
      int i = paramString.indexOf(':');
      if (i == -1) {
        str1 = ParseUtil.decode(paramString);
        str2 = null;
      } else {
        str1 = ParseUtil.decode(paramString.substring(0, i++));
        str2 = ParseUtil.decode(paramString.substring(i));
      } 
      String str3 = str1 + ":";
      byte[] arrayOfByte1 = str3.getBytes();
      byte[] arrayOfByte2 = str2.getBytes();
      byte[] arrayOfByte3 = new byte[arrayOfByte1.length + arrayOfByte2.length];
      System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, arrayOfByte1.length);
      System.arraycopy(arrayOfByte2, 0, arrayOfByte3, arrayOfByte1.length, arrayOfByte2.length);
      String str4 = "Basic " + new String(Base64.encode(arrayOfByte3));
      paramHttpURLConnection.setRequestProperty("Authorization", str4);
    } 
  }
  
  private void d(String paramString) {
    log.log(Level.SEVERE, "SAAJ0013.p2p.HttpSOAPConnection", new String[] { paramString });
    System.err.println("HttpSOAPConnection: " + paramString);
  }
  
  private HttpURLConnection createConnection(URL paramURL) throws IOException { return (HttpURLConnection)paramURL.openConnection(); }
  
  static  {
    if (isIBMVM) {
      SSL_PKG = "com.ibm.net.ssl.internal.www.protocol";
      SSL_PROVIDER = "com.ibm.net.ssl.internal.ssl.Provider";
    } else {
      SSL_PKG = "com.sun.net.ssl.internal.www.protocol";
      SSL_PROVIDER = "com.sun.net.ssl.internal.ssl.Provider";
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\client\p2p\HttpSOAPConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */