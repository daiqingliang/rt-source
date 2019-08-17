package com.sun.xml.internal.ws.transport.http.client;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.client.ClientTransportException;
import com.sun.xml.internal.ws.developer.HttpConfigFeature;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.resources.WsservletMessages;
import com.sun.xml.internal.ws.transport.Headers;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import com.sun.xml.internal.ws.util.RuntimeVersion;
import com.sun.xml.internal.ws.util.StreamUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

public class HttpTransportPipe extends AbstractTubeImpl {
  private static final List<String> USER_AGENT;
  
  private static final Logger LOGGER;
  
  public static boolean dump;
  
  private final Codec codec;
  
  private final WSBinding binding;
  
  private final CookieHandler cookieJar;
  
  private final boolean sticky;
  
  public HttpTransportPipe(Codec paramCodec, WSBinding paramWSBinding) {
    this.codec = paramCodec;
    this.binding = paramWSBinding;
    this.sticky = isSticky(paramWSBinding);
    HttpConfigFeature httpConfigFeature = (HttpConfigFeature)paramWSBinding.getFeature(HttpConfigFeature.class);
    if (httpConfigFeature == null)
      httpConfigFeature = new HttpConfigFeature(); 
    this.cookieJar = httpConfigFeature.getCookieHandler();
  }
  
  private static boolean isSticky(WSBinding paramWSBinding) {
    boolean bool = false;
    WebServiceFeature[] arrayOfWebServiceFeature = paramWSBinding.getFeatures().toArray();
    for (WebServiceFeature webServiceFeature : arrayOfWebServiceFeature) {
      if (webServiceFeature instanceof com.sun.xml.internal.ws.api.ha.StickyFeature) {
        bool = true;
        break;
      } 
    } 
    return bool;
  }
  
  private HttpTransportPipe(HttpTransportPipe paramHttpTransportPipe, TubeCloner paramTubeCloner) {
    this(paramHttpTransportPipe.codec.copy(), paramHttpTransportPipe.binding);
    paramTubeCloner.add(paramHttpTransportPipe, this);
  }
  
  public NextAction processException(@NotNull Throwable paramThrowable) { return doThrow(paramThrowable); }
  
  public NextAction processRequest(@NotNull Packet paramPacket) { return doReturnWith(process(paramPacket)); }
  
  public NextAction processResponse(@NotNull Packet paramPacket) { return doReturnWith(paramPacket); }
  
  protected HttpClientTransport getTransport(Packet paramPacket, Map<String, List<String>> paramMap) { return new HttpClientTransport(paramPacket, paramMap); }
  
  public Packet process(Packet paramPacket) {
    try {
      Headers headers = new Headers();
      Map map = (Map)paramPacket.invocationProperties.get("javax.xml.ws.http.request.headers");
      boolean bool = true;
      if (map != null) {
        headers.putAll(map);
        if (map.get("User-Agent") != null)
          bool = false; 
      } 
      if (bool)
        headers.put("User-Agent", USER_AGENT); 
      addBasicAuth(paramPacket, headers);
      addCookies(paramPacket, headers);
      HttpClientTransport httpClientTransport = getTransport(paramPacket, headers);
      paramPacket.addSatellite(new HttpResponseProperties(httpClientTransport));
      ContentType contentType = this.codec.getStaticContentType(paramPacket);
      if (contentType == null) {
        ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer();
        contentType = this.codec.encode(paramPacket, byteArrayBuffer);
        headers.put("Content-Length", Collections.singletonList(Integer.toString(byteArrayBuffer.size())));
        headers.put("Content-Type", Collections.singletonList(contentType.getContentType()));
        if (contentType.getAcceptHeader() != null)
          headers.put("Accept", Collections.singletonList(contentType.getAcceptHeader())); 
        if (this.binding instanceof javax.xml.ws.soap.SOAPBinding)
          writeSOAPAction(headers, contentType.getSOAPActionHeader()); 
        if (dump || LOGGER.isLoggable(Level.FINER))
          dump(byteArrayBuffer, "HTTP request", headers); 
        byteArrayBuffer.writeTo(httpClientTransport.getOutput());
      } else {
        headers.put("Content-Type", Collections.singletonList(contentType.getContentType()));
        if (contentType.getAcceptHeader() != null)
          headers.put("Accept", Collections.singletonList(contentType.getAcceptHeader())); 
        if (this.binding instanceof javax.xml.ws.soap.SOAPBinding)
          writeSOAPAction(headers, contentType.getSOAPActionHeader()); 
        if (dump || LOGGER.isLoggable(Level.FINER)) {
          ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer();
          this.codec.encode(paramPacket, byteArrayBuffer);
          dump(byteArrayBuffer, "HTTP request - " + paramPacket.endpointAddress, headers);
          OutputStream outputStream = httpClientTransport.getOutput();
          if (outputStream != null)
            byteArrayBuffer.writeTo(outputStream); 
        } else {
          OutputStream outputStream = httpClientTransport.getOutput();
          if (outputStream != null)
            this.codec.encode(paramPacket, outputStream); 
        } 
      } 
      httpClientTransport.closeOutput();
      return createResponsePacket(paramPacket, httpClientTransport);
    } catch (WebServiceException webServiceException) {
      throw webServiceException;
    } catch (Exception exception) {
      throw new WebServiceException(exception);
    } 
  }
  
  private Packet createResponsePacket(Packet paramPacket, HttpClientTransport paramHttpClientTransport) throws IOException {
    paramHttpClientTransport.readResponseCodeAndMessage();
    recordCookies(paramPacket, paramHttpClientTransport);
    InputStream inputStream1 = paramHttpClientTransport.getInput();
    if (dump || LOGGER.isLoggable(Level.FINER)) {
      ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer();
      if (inputStream1 != null) {
        byteArrayBuffer.write(inputStream1);
        inputStream1.close();
      } 
      dump(byteArrayBuffer, "HTTP response - " + paramPacket.endpointAddress + " - " + paramHttpClientTransport.statusCode, paramHttpClientTransport.getHeaders());
      inputStream1 = byteArrayBuffer.newInputStream();
    } 
    int i = paramHttpClientTransport.contentLength;
    InputStream inputStream2 = null;
    if (i == -1) {
      inputStream2 = StreamUtils.hasSomeData(inputStream1);
      if (inputStream2 != null)
        inputStream1 = inputStream2; 
    } 
    if ((i == 0 || (i == -1 && inputStream2 == null)) && inputStream1 != null) {
      inputStream1.close();
      inputStream1 = null;
    } 
    checkStatusCode(inputStream1, paramHttpClientTransport);
    Packet packet = paramPacket.createClientResponse(null);
    packet.wasTransportSecure = paramHttpClientTransport.isSecure();
    if (inputStream1 != null) {
      String str = paramHttpClientTransport.getContentType();
      if (str != null && str.contains("text/html") && this.binding instanceof javax.xml.ws.soap.SOAPBinding)
        throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(Integer.valueOf(paramHttpClientTransport.statusCode), paramHttpClientTransport.statusMessage)); 
      this.codec.decode(inputStream1, str, packet);
    } 
    return packet;
  }
  
  private void checkStatusCode(InputStream paramInputStream, HttpClientTransport paramHttpClientTransport) throws IOException {
    int i = paramHttpClientTransport.statusCode;
    String str = paramHttpClientTransport.statusMessage;
    if (this.binding instanceof javax.xml.ws.soap.SOAPBinding) {
      if (this.binding.getSOAPVersion() == SOAPVersion.SOAP_12) {
        if (i == 200 || i == 202 || isErrorCode(i)) {
          if (isErrorCode(i) && paramInputStream == null)
            throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(Integer.valueOf(i), str)); 
          return;
        } 
      } else if (i == 200 || i == 202 || i == 500) {
        if (i == 500 && paramInputStream == null)
          throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(Integer.valueOf(i), str)); 
        return;
      } 
      if (paramInputStream != null)
        paramInputStream.close(); 
      throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(Integer.valueOf(i), str));
    } 
  }
  
  private boolean isErrorCode(int paramInt) { return (paramInt == 500 || paramInt == 400); }
  
  private void addCookies(Packet paramPacket, Map<String, List<String>> paramMap) throws IOException {
    Boolean bool = (Boolean)paramPacket.invocationProperties.get("javax.xml.ws.session.maintain");
    if (bool != null && !bool.booleanValue())
      return; 
    if (this.sticky || (bool != null && bool.booleanValue())) {
      Map map = this.cookieJar.get(paramPacket.endpointAddress.getURI(), paramMap);
      processCookieHeaders(paramMap, map, "Cookie");
      processCookieHeaders(paramMap, map, "Cookie2");
    } 
  }
  
  private void processCookieHeaders(Map<String, List<String>> paramMap1, Map<String, List<String>> paramMap2, String paramString) {
    List list = (List)paramMap2.get(paramString);
    if (list != null && !list.isEmpty()) {
      List list1 = mergeUserCookies(list, (List)paramMap1.get(paramString));
      paramMap1.put(paramString, list1);
    } 
  }
  
  private List<String> mergeUserCookies(List<String> paramList1, List<String> paramList2) {
    if (paramList2 == null || paramList2.isEmpty())
      return paramList1; 
    HashMap hashMap = new HashMap();
    cookieListToMap(paramList1, hashMap);
    cookieListToMap(paramList2, hashMap);
    return new ArrayList(hashMap.values());
  }
  
  private void cookieListToMap(List<String> paramList, Map<String, String> paramMap) {
    for (String str1 : paramList) {
      int i = str1.indexOf("=");
      String str2 = str1.substring(0, i);
      paramMap.put(str2, str1);
    } 
  }
  
  private void recordCookies(Packet paramPacket, HttpClientTransport paramHttpClientTransport) throws IOException {
    Boolean bool = (Boolean)paramPacket.invocationProperties.get("javax.xml.ws.session.maintain");
    if (bool != null && !bool.booleanValue())
      return; 
    if (this.sticky || (bool != null && bool.booleanValue()))
      this.cookieJar.put(paramPacket.endpointAddress.getURI(), paramHttpClientTransport.getHeaders()); 
  }
  
  private void addBasicAuth(Packet paramPacket, Map<String, List<String>> paramMap) throws IOException {
    String str = (String)paramPacket.invocationProperties.get("javax.xml.ws.security.auth.username");
    if (str != null) {
      String str1 = (String)paramPacket.invocationProperties.get("javax.xml.ws.security.auth.password");
      if (str1 != null) {
        StringBuilder stringBuilder = new StringBuilder(str);
        stringBuilder.append(":");
        stringBuilder.append(str1);
        String str2 = DatatypeConverter.printBase64Binary(stringBuilder.toString().getBytes());
        paramMap.put("Authorization", Collections.singletonList("Basic " + str2));
      } 
    } 
  }
  
  private void writeSOAPAction(Map<String, List<String>> paramMap, String paramString) {
    if (SOAPVersion.SOAP_12.equals(this.binding.getSOAPVersion()))
      return; 
    if (paramString != null) {
      paramMap.put("SOAPAction", Collections.singletonList(paramString));
    } else {
      paramMap.put("SOAPAction", Collections.singletonList("\"\""));
    } 
  }
  
  public void preDestroy() {}
  
  public HttpTransportPipe copy(TubeCloner paramTubeCloner) { return new HttpTransportPipe(this, paramTubeCloner); }
  
  private void dump(ByteArrayBuffer paramByteArrayBuffer, String paramString, Map<String, List<String>> paramMap) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    PrintWriter printWriter = new PrintWriter(byteArrayOutputStream, true);
    printWriter.println("---[" + paramString + "]---");
    for (Map.Entry entry : paramMap.entrySet()) {
      if (((List)entry.getValue()).isEmpty()) {
        printWriter.println(entry.getValue());
        continue;
      } 
      for (String str1 : (List)entry.getValue())
        printWriter.println((String)entry.getKey() + ": " + str1); 
    } 
    if (paramByteArrayBuffer.size() > HttpAdapter.dump_threshold) {
      byte[] arrayOfByte = paramByteArrayBuffer.getRawData();
      byteArrayOutputStream.write(arrayOfByte, 0, HttpAdapter.dump_threshold);
      printWriter.println();
      printWriter.println(WsservletMessages.MESSAGE_TOO_LONG(HttpAdapter.class.getName() + ".dumpTreshold"));
    } else {
      paramByteArrayBuffer.writeTo(byteArrayOutputStream);
    } 
    printWriter.println("--------------------");
    String str = byteArrayOutputStream.toString();
    if (dump)
      System.out.println(str); 
    if (LOGGER.isLoggable(Level.FINER))
      LOGGER.log(Level.FINER, str); 
  }
  
  static  {
    boolean bool;
    USER_AGENT = Collections.singletonList(RuntimeVersion.VERSION.toString());
    LOGGER = Logger.getLogger(HttpTransportPipe.class.getName());
    try {
      bool = Boolean.getBoolean(HttpTransportPipe.class.getName() + ".dump");
    } catch (Throwable throwable) {
      bool = false;
    } 
    dump = bool;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\transport\http\client\HttpTransportPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */