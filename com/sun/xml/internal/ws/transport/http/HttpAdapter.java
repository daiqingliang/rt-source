package com.sun.xml.internal.ws.transport.http;

import com.oracle.webservices.internal.api.message.PropertySet;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.NonAnonymousResponseProcessor;
import com.sun.xml.internal.ws.api.ha.HaInfo;
import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.server.AbstractServerAsyncTransport;
import com.sun.xml.internal.ws.api.server.Adapter;
import com.sun.xml.internal.ws.api.server.BoundEndpoint;
import com.sun.xml.internal.ws.api.server.DocumentAddressResolver;
import com.sun.xml.internal.ws.api.server.Module;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.api.server.ServiceDefinition;
import com.sun.xml.internal.ws.api.server.TransportBackChannel;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.resources.WsservletMessages;
import com.sun.xml.internal.ws.server.UnsupportedMediaException;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import com.sun.xml.internal.ws.util.Pool;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.WebServiceException;

public class HttpAdapter extends Adapter<HttpAdapter.HttpToolkit> {
  private static final Logger LOGGER = Logger.getLogger(HttpAdapter.class.getName());
  
  protected Map<String, SDDocument> wsdls;
  
  private Map<SDDocument, String> revWsdls;
  
  private ServiceDefinition serviceDefinition = null;
  
  public final HttpAdapterList<? extends HttpAdapter> owner;
  
  public final String urlPattern;
  
  protected boolean stickyCookie;
  
  protected boolean disableJreplicaCookie = false;
  
  public static final CompletionCallback NO_OP_COMPLETION_CALLBACK = new CompletionCallback() {
      public void onCompletion() {}
    };
  
  public static HttpAdapter createAlone(WSEndpoint paramWSEndpoint) { return (new DummyList(null)).createAdapter("", "", paramWSEndpoint); }
  
  protected HttpAdapter(WSEndpoint paramWSEndpoint, HttpAdapterList<? extends HttpAdapter> paramHttpAdapterList) { this(paramWSEndpoint, paramHttpAdapterList, null); }
  
  protected HttpAdapter(WSEndpoint paramWSEndpoint, HttpAdapterList<? extends HttpAdapter> paramHttpAdapterList, String paramString) {
    super(paramWSEndpoint);
    this.owner = paramHttpAdapterList;
    this.urlPattern = paramString;
    initWSDLMap(paramWSEndpoint.getServiceDefinition());
  }
  
  public ServiceDefinition getServiceDefinition() { return this.serviceDefinition; }
  
  public final void initWSDLMap(ServiceDefinition paramServiceDefinition) {
    this.serviceDefinition = paramServiceDefinition;
    if (paramServiceDefinition == null) {
      this.wsdls = Collections.emptyMap();
      this.revWsdls = Collections.emptyMap();
    } else {
      this.wsdls = new HashMap();
      TreeMap treeMap = new TreeMap();
      for (SDDocument sDDocument : paramServiceDefinition) {
        if (sDDocument == paramServiceDefinition.getPrimary()) {
          this.wsdls.put("wsdl", sDDocument);
          this.wsdls.put("WSDL", sDDocument);
          continue;
        } 
        treeMap.put(sDDocument.getURL().toString(), sDDocument);
      } 
      byte b1 = 1;
      byte b2 = 1;
      for (Map.Entry entry : treeMap.entrySet()) {
        SDDocument sDDocument = (SDDocument)entry.getValue();
        if (sDDocument.isWSDL())
          this.wsdls.put("wsdl=" + b1++, sDDocument); 
        if (sDDocument.isSchema())
          this.wsdls.put("xsd=" + b2++, sDDocument); 
      } 
      this.revWsdls = new HashMap();
      for (Map.Entry entry : this.wsdls.entrySet()) {
        if (!((String)entry.getKey()).equals("WSDL"))
          this.revWsdls.put(entry.getValue(), entry.getKey()); 
      } 
    } 
  }
  
  public String getValidPath() { return this.urlPattern.endsWith("/*") ? this.urlPattern.substring(0, this.urlPattern.length() - 2) : this.urlPattern; }
  
  protected HttpToolkit createToolkit() { return new HttpToolkit(); }
  
  public void handle(@NotNull WSHTTPConnection paramWSHTTPConnection) throws IOException {
    if (handleGet(paramWSHTTPConnection))
      return; 
    pool = getPool();
    httpToolkit = (HttpToolkit)pool.take();
    try {
      httpToolkit.handle(paramWSHTTPConnection);
    } finally {
      pool.recycle(httpToolkit);
    } 
  }
  
  public boolean handleGet(@NotNull WSHTTPConnection paramWSHTTPConnection) throws IOException {
    if (paramWSHTTPConnection.getRequestMethod().equals("GET")) {
      for (Component component : this.endpoint.getComponents()) {
        HttpMetadataPublisher httpMetadataPublisher = (HttpMetadataPublisher)component.getSPI(HttpMetadataPublisher.class);
        if (httpMetadataPublisher != null && httpMetadataPublisher.handleMetadataRequest(this, paramWSHTTPConnection))
          return true; 
      } 
      if (isMetadataQuery(paramWSHTTPConnection.getQueryString())) {
        publishWSDL(paramWSHTTPConnection);
        return true;
      } 
      WSBinding wSBinding = getEndpoint().getBinding();
      if (!(wSBinding instanceof javax.xml.ws.http.HTTPBinding)) {
        writeWebServicesHtmlPage(paramWSHTTPConnection);
        return true;
      } 
    } else if (paramWSHTTPConnection.getRequestMethod().equals("HEAD")) {
      paramWSHTTPConnection.getInput().close();
      WSBinding wSBinding = getEndpoint().getBinding();
      if (isMetadataQuery(paramWSHTTPConnection.getQueryString())) {
        SDDocument sDDocument = (SDDocument)this.wsdls.get(paramWSHTTPConnection.getQueryString());
        paramWSHTTPConnection.setStatus((sDDocument != null) ? 200 : 404);
        paramWSHTTPConnection.getOutput().close();
        paramWSHTTPConnection.close();
        return true;
      } 
      if (!(wSBinding instanceof javax.xml.ws.http.HTTPBinding)) {
        paramWSHTTPConnection.setStatus(404);
        paramWSHTTPConnection.getOutput().close();
        paramWSHTTPConnection.close();
        return true;
      } 
    } 
    return false;
  }
  
  private Packet decodePacket(@NotNull WSHTTPConnection paramWSHTTPConnection, @NotNull Codec paramCodec) throws IOException {
    String str = paramWSHTTPConnection.getRequestHeader("Content-Type");
    InputStream inputStream = paramWSHTTPConnection.getInput();
    Packet packet = new Packet();
    packet.soapAction = fixQuotesAroundSoapAction(paramWSHTTPConnection.getRequestHeader("SOAPAction"));
    packet.wasTransportSecure = paramWSHTTPConnection.isSecure();
    packet.acceptableMimeTypes = paramWSHTTPConnection.getRequestHeader("Accept");
    packet.addSatellite(paramWSHTTPConnection);
    addSatellites(packet);
    packet.isAdapterDeliversNonAnonymousResponse = true;
    packet.component = this;
    packet.transportBackChannel = new Oneway(paramWSHTTPConnection);
    packet.webServiceContextDelegate = paramWSHTTPConnection.getWebServiceContextDelegate();
    packet.setState(Packet.State.ServerRequest);
    if (dump || LOGGER.isLoggable(Level.FINER)) {
      ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer();
      byteArrayBuffer.write(inputStream);
      inputStream.close();
      dump(byteArrayBuffer, "HTTP request", paramWSHTTPConnection.getRequestHeaders());
      inputStream = byteArrayBuffer.newInputStream();
    } 
    paramCodec.decode(inputStream, str, packet);
    return packet;
  }
  
  protected void addSatellites(Packet paramPacket) {}
  
  public static String fixQuotesAroundSoapAction(String paramString) {
    if (paramString != null && (!paramString.startsWith("\"") || !paramString.endsWith("\""))) {
      if (LOGGER.isLoggable(Level.INFO))
        LOGGER.log(Level.INFO, "Received WS-I BP non-conformant Unquoted SoapAction HTTP header: {0}", paramString); 
      String str = paramString;
      if (!paramString.startsWith("\""))
        str = "\"" + str; 
      if (!paramString.endsWith("\""))
        str = str + "\""; 
      return str;
    } 
    return paramString;
  }
  
  protected NonAnonymousResponseProcessor getNonAnonymousResponseProcessor() { return NonAnonymousResponseProcessor.getDefault(); }
  
  protected void writeClientError(int paramInt, @NotNull OutputStream paramOutputStream, @NotNull Packet paramPacket) throws IOException {}
  
  private boolean isClientErrorStatus(int paramInt) { return (paramInt == 403); }
  
  private boolean isNonAnonymousUri(EndpointAddress paramEndpointAddress) { return (paramEndpointAddress != null && !paramEndpointAddress.toString().equals(AddressingVersion.W3C.anonymousUri) && !paramEndpointAddress.toString().equals(AddressingVersion.MEMBER.anonymousUri)); }
  
  private void encodePacket(@NotNull Packet paramPacket, @NotNull WSHTTPConnection paramWSHTTPConnection, @NotNull Codec paramCodec) throws IOException {
    if (isNonAnonymousUri(paramPacket.endpointAddress) && paramPacket.getMessage() != null)
      try {
        paramPacket = getNonAnonymousResponseProcessor().process(paramPacket);
      } catch (RuntimeException runtimeException) {
        SOAPVersion sOAPVersion = paramPacket.getBinding().getSOAPVersion();
        Message message1 = SOAPFaultBuilder.createSOAPFaultMessage(sOAPVersion, null, runtimeException);
        paramPacket = paramPacket.createServerResponse(message1, paramPacket.endpoint.getPort(), null, paramPacket.endpoint.getBinding());
      }  
    if (paramWSHTTPConnection.isClosed())
      return; 
    Message message = paramPacket.getMessage();
    addStickyCookie(paramWSHTTPConnection);
    addReplicaCookie(paramWSHTTPConnection, paramPacket);
    if (message == null) {
      if (!paramWSHTTPConnection.isClosed()) {
        if (paramWSHTTPConnection.getStatus() == 0)
          paramWSHTTPConnection.setStatus(202); 
        OutputStream outputStream = paramWSHTTPConnection.getProtocol().contains("1.1") ? paramWSHTTPConnection.getOutput() : new Http10OutputStream(paramWSHTTPConnection);
        if (dump || LOGGER.isLoggable(Level.FINER)) {
          ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer();
          paramCodec.encode(paramPacket, byteArrayBuffer);
          dump(byteArrayBuffer, "HTTP response " + paramWSHTTPConnection.getStatus(), paramWSHTTPConnection.getResponseHeaders());
          byteArrayBuffer.writeTo(outputStream);
        } else {
          paramCodec.encode(paramPacket, outputStream);
        } 
        try {
          outputStream.close();
        } catch (IOException iOException) {
          throw new WebServiceException(iOException);
        } 
      } 
    } else {
      if (paramWSHTTPConnection.getStatus() == 0)
        paramWSHTTPConnection.setStatus(message.isFault() ? 500 : 200); 
      if (isClientErrorStatus(paramWSHTTPConnection.getStatus())) {
        OutputStream outputStream = paramWSHTTPConnection.getOutput();
        if (dump || LOGGER.isLoggable(Level.FINER)) {
          ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer();
          writeClientError(paramWSHTTPConnection.getStatus(), byteArrayBuffer, paramPacket);
          dump(byteArrayBuffer, "HTTP response " + paramWSHTTPConnection.getStatus(), paramWSHTTPConnection.getResponseHeaders());
          byteArrayBuffer.writeTo(outputStream);
        } else {
          writeClientError(paramWSHTTPConnection.getStatus(), outputStream, paramPacket);
        } 
        outputStream.close();
        return;
      } 
      ContentType contentType = paramCodec.getStaticContentType(paramPacket);
      if (contentType != null) {
        paramWSHTTPConnection.setContentTypeResponseHeader(contentType.getContentType());
        OutputStream outputStream = paramWSHTTPConnection.getProtocol().contains("1.1") ? paramWSHTTPConnection.getOutput() : new Http10OutputStream(paramWSHTTPConnection);
        if (dump || LOGGER.isLoggable(Level.FINER)) {
          ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer();
          paramCodec.encode(paramPacket, byteArrayBuffer);
          dump(byteArrayBuffer, "HTTP response " + paramWSHTTPConnection.getStatus(), paramWSHTTPConnection.getResponseHeaders());
          byteArrayBuffer.writeTo(outputStream);
        } else {
          paramCodec.encode(paramPacket, outputStream);
        } 
        outputStream.close();
      } else {
        ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer();
        contentType = paramCodec.encode(paramPacket, byteArrayBuffer);
        paramWSHTTPConnection.setContentTypeResponseHeader(contentType.getContentType());
        if (dump || LOGGER.isLoggable(Level.FINER))
          dump(byteArrayBuffer, "HTTP response " + paramWSHTTPConnection.getStatus(), paramWSHTTPConnection.getResponseHeaders()); 
        OutputStream outputStream = paramWSHTTPConnection.getOutput();
        byteArrayBuffer.writeTo(outputStream);
        outputStream.close();
      } 
    } 
  }
  
  private void addStickyCookie(WSHTTPConnection paramWSHTTPConnection) throws IOException {
    if (this.stickyCookie) {
      String str1 = paramWSHTTPConnection.getRequestHeader("proxy-jroute");
      if (str1 == null)
        return; 
      String str2 = paramWSHTTPConnection.getCookie("JROUTE");
      if (str2 == null || !str2.equals(str1))
        paramWSHTTPConnection.setCookie("JROUTE", str1); 
    } 
  }
  
  private void addReplicaCookie(WSHTTPConnection paramWSHTTPConnection, Packet paramPacket) {
    if (this.stickyCookie) {
      HaInfo haInfo = null;
      if (paramPacket.supports("com.sun.xml.internal.ws.api.message.packet.hainfo"))
        haInfo = (HaInfo)paramPacket.get("com.sun.xml.internal.ws.api.message.packet.hainfo"); 
      if (haInfo != null) {
        paramWSHTTPConnection.setCookie("METRO_KEY", haInfo.getKey());
        if (!this.disableJreplicaCookie)
          paramWSHTTPConnection.setCookie("JREPLICA", haInfo.getReplicaInstance()); 
      } 
    } 
  }
  
  public void invokeAsync(WSHTTPConnection paramWSHTTPConnection) throws IOException { invokeAsync(paramWSHTTPConnection, NO_OP_COMPLETION_CALLBACK); }
  
  public void invokeAsync(final WSHTTPConnection con, final CompletionCallback callback) throws IOException {
    Packet packet;
    if (handleGet(paramWSHTTPConnection)) {
      paramCompletionCallback.onCompletion();
      return;
    } 
    final Pool currentPool = getPool();
    final HttpToolkit tk = (HttpToolkit)pool.take();
    try {
      packet = decodePacket(paramWSHTTPConnection, httpToolkit.codec);
    } catch (ExceptionHasMessage exceptionHasMessage) {
      LOGGER.log(Level.SEVERE, exceptionHasMessage.getMessage(), exceptionHasMessage);
      Packet packet1 = new Packet();
      packet1.setMessage(exceptionHasMessage.getFaultMessage());
      encodePacket(packet1, paramWSHTTPConnection, httpToolkit.codec);
      pool.recycle(httpToolkit);
      paramWSHTTPConnection.close();
      paramCompletionCallback.onCompletion();
      return;
    } catch (UnsupportedMediaException unsupportedMediaException) {
      LOGGER.log(Level.SEVERE, unsupportedMediaException.getMessage(), unsupportedMediaException);
      Packet packet1 = new Packet();
      paramWSHTTPConnection.setStatus(415);
      encodePacket(packet1, paramWSHTTPConnection, httpToolkit.codec);
      pool.recycle(httpToolkit);
      paramWSHTTPConnection.close();
      paramCompletionCallback.onCompletion();
      return;
    } 
    this.endpoint.process(packet, new WSEndpoint.CompletionCallback() {
          public void onCompletion(@NotNull Packet param1Packet) {
            try {
              try {
                HttpAdapter.this.encodePacket(param1Packet, con, this.val$tk.codec);
              } catch (IOException iOException) {
                LOGGER.log(Level.SEVERE, iOException.getMessage(), iOException);
              } 
              currentPool.recycle(tk);
            } finally {
              con.close();
              callback.onCompletion();
            } 
          }
        }null);
  }
  
  private boolean isMetadataQuery(String paramString) { return (paramString != null && (paramString.equals("WSDL") || paramString.startsWith("wsdl") || paramString.startsWith("xsd="))); }
  
  public void publishWSDL(@NotNull WSHTTPConnection paramWSHTTPConnection) throws IOException {
    paramWSHTTPConnection.getInput().close();
    SDDocument sDDocument = (SDDocument)this.wsdls.get(paramWSHTTPConnection.getQueryString());
    if (sDDocument == null) {
      writeNotFoundErrorPage(paramWSHTTPConnection, "Invalid Request");
      return;
    } 
    paramWSHTTPConnection.setStatus(200);
    paramWSHTTPConnection.setContentTypeResponseHeader("text/xml;charset=utf-8");
    OutputStream outputStream = paramWSHTTPConnection.getProtocol().contains("1.1") ? paramWSHTTPConnection.getOutput() : new Http10OutputStream(paramWSHTTPConnection);
    PortAddressResolver portAddressResolver = getPortAddressResolver(paramWSHTTPConnection.getBaseAddress());
    DocumentAddressResolver documentAddressResolver = getDocumentAddressResolver(portAddressResolver);
    sDDocument.writeTo(portAddressResolver, documentAddressResolver, outputStream);
    outputStream.close();
  }
  
  public PortAddressResolver getPortAddressResolver(String paramString) { return this.owner.createPortAddressResolver(paramString, this.endpoint.getImplementationClass()); }
  
  public DocumentAddressResolver getDocumentAddressResolver(PortAddressResolver paramPortAddressResolver) {
    final String address = paramPortAddressResolver.getAddressFor(this.endpoint.getServiceName(), this.endpoint.getPortName().getLocalPart());
    assert str != null;
    return new DocumentAddressResolver() {
        public String getRelativeAddressFor(@NotNull SDDocument param1SDDocument1, @NotNull SDDocument param1SDDocument2) {
          assert HttpAdapter.this.revWsdls.containsKey(param1SDDocument2);
          return address + '?' + (String)HttpAdapter.this.revWsdls.get(param1SDDocument2);
        }
      };
  }
  
  private void writeNotFoundErrorPage(WSHTTPConnection paramWSHTTPConnection, String paramString) throws IOException {
    paramWSHTTPConnection.setStatus(404);
    paramWSHTTPConnection.setContentTypeResponseHeader("text/html; charset=utf-8");
    PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(paramWSHTTPConnection.getOutput(), "UTF-8"));
    printWriter.println("<html>");
    printWriter.println("<head><title>");
    printWriter.println(WsservletMessages.SERVLET_HTML_TITLE());
    printWriter.println("</title></head>");
    printWriter.println("<body>");
    printWriter.println(WsservletMessages.SERVLET_HTML_NOT_FOUND(paramString));
    printWriter.println("</body>");
    printWriter.println("</html>");
    printWriter.close();
  }
  
  private void writeInternalServerError(WSHTTPConnection paramWSHTTPConnection) throws IOException {
    paramWSHTTPConnection.setStatus(500);
    paramWSHTTPConnection.getOutput().close();
  }
  
  private static void dump(ByteArrayBuffer paramByteArrayBuffer, String paramString, Map<String, List<String>> paramMap) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    PrintWriter printWriter = new PrintWriter(byteArrayOutputStream, true);
    printWriter.println("---[" + paramString + "]---");
    if (paramMap != null)
      for (Map.Entry entry : paramMap.entrySet()) {
        if (((List)entry.getValue()).isEmpty()) {
          printWriter.println(entry.getValue());
          continue;
        } 
        for (String str1 : (List)entry.getValue())
          printWriter.println((String)entry.getKey() + ": " + str1); 
      }  
    if (paramByteArrayBuffer.size() > dump_threshold) {
      byte[] arrayOfByte = paramByteArrayBuffer.getRawData();
      byteArrayOutputStream.write(arrayOfByte, 0, dump_threshold);
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
  
  private void writeWebServicesHtmlPage(WSHTTPConnection paramWSHTTPConnection) throws IOException {
    if (!publishStatusPage)
      return; 
    paramWSHTTPConnection.getInput().close();
    paramWSHTTPConnection.setStatus(200);
    paramWSHTTPConnection.setContentTypeResponseHeader("text/html; charset=utf-8");
    PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(paramWSHTTPConnection.getOutput(), "UTF-8"));
    printWriter.println("<html>");
    printWriter.println("<head><title>");
    printWriter.println(WsservletMessages.SERVLET_HTML_TITLE());
    printWriter.println("</title></head>");
    printWriter.println("<body>");
    printWriter.println(WsservletMessages.SERVLET_HTML_TITLE_2());
    Module module = (Module)getEndpoint().getContainer().getSPI(Module.class);
    List list = Collections.emptyList();
    if (module != null)
      list = module.getBoundEndpoints(); 
    if (list.isEmpty()) {
      printWriter.println(WsservletMessages.SERVLET_HTML_NO_INFO_AVAILABLE());
    } else {
      printWriter.println("<table width='100%' border='1'>");
      printWriter.println("<tr>");
      printWriter.println("<td>");
      printWriter.println(WsservletMessages.SERVLET_HTML_COLUMN_HEADER_PORT_NAME());
      printWriter.println("</td>");
      printWriter.println("<td>");
      printWriter.println(WsservletMessages.SERVLET_HTML_COLUMN_HEADER_INFORMATION());
      printWriter.println("</td>");
      printWriter.println("</tr>");
      for (BoundEndpoint boundEndpoint : list) {
        String str = boundEndpoint.getAddress(paramWSHTTPConnection.getBaseAddress()).toString();
        printWriter.println("<tr>");
        printWriter.println("<td>");
        printWriter.println(WsservletMessages.SERVLET_HTML_ENDPOINT_TABLE(boundEndpoint.getEndpoint().getServiceName(), boundEndpoint.getEndpoint().getPortName()));
        printWriter.println("</td>");
        printWriter.println("<td>");
        printWriter.println(WsservletMessages.SERVLET_HTML_INFORMATION_TABLE(str, boundEndpoint.getEndpoint().getImplementationClass().getName()));
        printWriter.println("</td>");
        printWriter.println("</tr>");
      } 
      printWriter.println("</table>");
    } 
    printWriter.println("</body>");
    printWriter.println("</html>");
    printWriter.close();
  }
  
  public static void setPublishStatus(boolean paramBoolean) { publishStatusPage = paramBoolean; }
  
  public static void setDump(boolean paramBoolean) { dump = paramBoolean; }
  
  static  {
    try {
      dump = Boolean.getBoolean(HttpAdapter.class.getName() + ".dump");
    } catch (SecurityException securityException) {
      if (LOGGER.isLoggable(Level.CONFIG))
        LOGGER.log(Level.CONFIG, "Cannot read ''{0}'' property, using defaults.", new Object[] { HttpAdapter.class.getName() + ".dump" }); 
    } 
    try {
      dump_threshold = Integer.getInteger(HttpAdapter.class.getName() + ".dumpTreshold", 4096).intValue();
    } catch (SecurityException securityException) {
      if (LOGGER.isLoggable(Level.CONFIG))
        LOGGER.log(Level.CONFIG, "Cannot read ''{0}'' property, using defaults.", new Object[] { HttpAdapter.class.getName() + ".dumpTreshold" }); 
    } 
    try {
      setPublishStatus(Boolean.getBoolean(HttpAdapter.class.getName() + ".publishStatusPage"));
    } catch (SecurityException securityException) {
      if (LOGGER.isLoggable(Level.CONFIG))
        LOGGER.log(Level.CONFIG, "Cannot read ''{0}'' property, using defaults.", new Object[] { HttpAdapter.class.getName() + ".publishStatusPage" }); 
    } 
  }
  
  final class AsyncTransport extends AbstractServerAsyncTransport<WSHTTPConnection> {
    public AsyncTransport(HttpAdapter this$0) { super(this$0.endpoint); }
    
    public void handleAsync(WSHTTPConnection param1WSHTTPConnection) throws IOException { handle(param1WSHTTPConnection); }
    
    protected void encodePacket(WSHTTPConnection param1WSHTTPConnection, @NotNull Packet param1Packet, @NotNull Codec param1Codec) throws IOException { this.this$0.encodePacket(param1Packet, param1WSHTTPConnection, param1Codec); }
    
    @Nullable
    protected String getAcceptableMimeTypes(WSHTTPConnection param1WSHTTPConnection) { return null; }
    
    @Nullable
    protected TransportBackChannel getTransportBackChannel(WSHTTPConnection param1WSHTTPConnection) { return new HttpAdapter.Oneway(param1WSHTTPConnection); }
    
    @NotNull
    protected PropertySet getPropertySet(WSHTTPConnection param1WSHTTPConnection) { return param1WSHTTPConnection; }
    
    @NotNull
    protected WebServiceContextDelegate getWebServiceContextDelegate(WSHTTPConnection param1WSHTTPConnection) { return param1WSHTTPConnection.getWebServiceContextDelegate(); }
  }
  
  public static interface CompletionCallback {
    void onCompletion();
  }
  
  private static final class DummyList extends HttpAdapterList<HttpAdapter> {
    private DummyList() {}
    
    protected HttpAdapter createHttpAdapter(String param1String1, String param1String2, WSEndpoint<?> param1WSEndpoint) { return new HttpAdapter(param1WSEndpoint, this, param1String2); }
  }
  
  private static final class Http10OutputStream extends ByteArrayBuffer {
    private final WSHTTPConnection con;
    
    Http10OutputStream(WSHTTPConnection param1WSHTTPConnection) throws IOException { this.con = param1WSHTTPConnection; }
    
    public void close() {
      super.close();
      this.con.setContentLengthResponseHeader(size());
      OutputStream outputStream = this.con.getOutput();
      writeTo(outputStream);
      outputStream.close();
    }
  }
  
  final class HttpToolkit extends Adapter.Toolkit {
    HttpToolkit() { super(HttpAdapter.this); }
    
    public void handle(WSHTTPConnection param1WSHTTPConnection) throws IOException {
      try {
        Packet packet;
        boolean bool = false;
        try {
          packet = HttpAdapter.this.decodePacket(param1WSHTTPConnection, this.codec);
          bool = true;
        } catch (Exception exception) {
          packet = new Packet();
          if (exception instanceof ExceptionHasMessage) {
            LOGGER.log(Level.SEVERE, exception.getMessage(), exception);
            packet.setMessage(((ExceptionHasMessage)exception).getFaultMessage());
          } else if (exception instanceof UnsupportedMediaException) {
            LOGGER.log(Level.SEVERE, exception.getMessage(), exception);
            param1WSHTTPConnection.setStatus(415);
          } else {
            LOGGER.log(Level.SEVERE, exception.getMessage(), exception);
            param1WSHTTPConnection.setStatus(500);
          } 
        } 
        if (bool)
          try {
            packet = this.head.process(packet, param1WSHTTPConnection.getWebServiceContextDelegate(), packet.transportBackChannel);
          } catch (Throwable throwable) {
            LOGGER.log(Level.SEVERE, throwable.getMessage(), throwable);
            if (!param1WSHTTPConnection.isClosed())
              HttpAdapter.this.writeInternalServerError(param1WSHTTPConnection); 
            return;
          }  
        HttpAdapter.this.encodePacket(packet, param1WSHTTPConnection, this.codec);
      } finally {
        if (!param1WSHTTPConnection.isClosed()) {
          if (LOGGER.isLoggable(Level.FINE))
            LOGGER.log(Level.FINE, "Closing HTTP Connection with status: {0}", Integer.valueOf(param1WSHTTPConnection.getStatus())); 
          param1WSHTTPConnection.close();
        } 
      } 
    }
  }
  
  static final class Oneway implements TransportBackChannel {
    WSHTTPConnection con;
    
    boolean closed;
    
    Oneway(WSHTTPConnection param1WSHTTPConnection) throws IOException { this.con = param1WSHTTPConnection; }
    
    public void close() {
      if (!this.closed) {
        this.closed = true;
        if (this.con.getStatus() == 0)
          this.con.setStatus(202); 
        OutputStream outputStream = null;
        try {
          outputStream = this.con.getOutput();
        } catch (IOException iOException) {}
        if (HttpAdapter.dump || LOGGER.isLoggable(Level.FINER))
          try {
            ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer();
            HttpAdapter.dump(byteArrayBuffer, "HTTP response " + this.con.getStatus(), this.con.getResponseHeaders());
          } catch (Exception exception) {
            throw new WebServiceException(exception.toString(), exception);
          }  
        if (outputStream != null)
          try {
            outputStream.close();
          } catch (IOException iOException) {
            throw new WebServiceException(iOException);
          }  
        this.con.close();
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\transport\http\HttpAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */