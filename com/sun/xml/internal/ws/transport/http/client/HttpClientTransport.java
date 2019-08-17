package com.sun.xml.internal.ws.transport.http.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.client.ClientTransportException;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.transport.Headers;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceException;

public class HttpClientTransport {
  private static final byte[] THROW_AWAY_BUFFER = new byte[8192];
  
  int statusCode;
  
  String statusMessage;
  
  int contentLength;
  
  private final Map<String, List<String>> reqHeaders;
  
  private Map<String, List<String>> respHeaders = null;
  
  private OutputStream outputStream;
  
  private boolean https;
  
  private HttpURLConnection httpConnection = null;
  
  private final EndpointAddress endpoint;
  
  private final Packet context;
  
  private final Integer chunkSize;
  
  public HttpClientTransport(@NotNull Packet paramPacket, @NotNull Map<String, List<String>> paramMap) {
    this.endpoint = paramPacket.endpointAddress;
    this.context = paramPacket;
    this.reqHeaders = paramMap;
    this.chunkSize = (Integer)this.context.invocationProperties.get("com.sun.xml.internal.ws.transport.http.client.streaming.chunk.size");
  }
  
  OutputStream getOutput() {
    try {
      createHttpConnection();
      if (requiresOutputStream()) {
        this.outputStream = this.httpConnection.getOutputStream();
        if (this.chunkSize != null)
          this.outputStream = new WSChunkedOuputStream(this.outputStream, this.chunkSize.intValue()); 
        List list = (List)this.reqHeaders.get("Content-Encoding");
        if (list != null && ((String)list.get(0)).contains("gzip"))
          this.outputStream = new GZIPOutputStream(this.outputStream); 
      } 
      this.httpConnection.connect();
    } catch (Exception exception) {
      throw new ClientTransportException(ClientMessages.localizableHTTP_CLIENT_FAILED(exception), exception);
    } 
    return this.outputStream;
  }
  
  void closeOutput() throws IOException {
    if (this.outputStream != null) {
      this.outputStream.close();
      this.outputStream = null;
    } 
  }
  
  @Nullable
  InputStream getInput() {
    InputStream inputStream;
    try {
      inputStream = readResponse();
      if (inputStream != null) {
        String str = this.httpConnection.getContentEncoding();
        if (str != null && str.contains("gzip"))
          inputStream = new GZIPInputStream(inputStream); 
      } 
    } catch (IOException iOException) {
      throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(Integer.valueOf(this.statusCode), this.statusMessage), iOException);
    } 
    return inputStream;
  }
  
  public Map<String, List<String>> getHeaders() {
    if (this.respHeaders != null)
      return this.respHeaders; 
    this.respHeaders = new Headers();
    this.respHeaders.putAll(this.httpConnection.getHeaderFields());
    return this.respHeaders;
  }
  
  @Nullable
  protected InputStream readResponse() {
    InputStream inputStream1;
    try {
      inputStream1 = this.httpConnection.getInputStream();
    } catch (IOException iOException) {
      inputStream1 = this.httpConnection.getErrorStream();
    } 
    if (inputStream1 == null)
      return inputStream1; 
    final InputStream temp = inputStream1;
    return new FilterInputStream(inputStream2) {
        boolean closed;
        
        public void close() throws IOException {
          if (!this.closed) {
            this.closed = true;
            while (temp.read(THROW_AWAY_BUFFER) != -1);
            super.close();
          } 
        }
      };
  }
  
  protected void readResponseCodeAndMessage() throws IOException {
    try {
      this.statusCode = this.httpConnection.getResponseCode();
      this.statusMessage = this.httpConnection.getResponseMessage();
      this.contentLength = this.httpConnection.getContentLength();
    } catch (IOException iOException) {
      throw new WebServiceException(iOException);
    } 
  }
  
  protected HttpURLConnection openConnection(Packet paramPacket) { return null; }
  
  protected boolean checkHTTPS(HttpURLConnection paramHttpURLConnection) {
    if (paramHttpURLConnection instanceof HttpsURLConnection) {
      String str = (String)this.context.invocationProperties.get("com.sun.xml.internal.ws.client.http.HostnameVerificationProperty");
      if (str != null && str.equalsIgnoreCase("true"))
        ((HttpsURLConnection)paramHttpURLConnection).setHostnameVerifier(new HttpClientVerifier(null)); 
      HostnameVerifier hostnameVerifier = (HostnameVerifier)this.context.invocationProperties.get("com.sun.xml.internal.ws.transport.https.client.hostname.verifier");
      if (hostnameVerifier != null)
        ((HttpsURLConnection)paramHttpURLConnection).setHostnameVerifier(hostnameVerifier); 
      SSLSocketFactory sSLSocketFactory = (SSLSocketFactory)this.context.invocationProperties.get("com.sun.xml.internal.ws.transport.https.client.SSLSocketFactory");
      if (sSLSocketFactory != null)
        ((HttpsURLConnection)paramHttpURLConnection).setSSLSocketFactory(sSLSocketFactory); 
      return true;
    } 
    return false;
  }
  
  private void createHttpConnection() throws IOException {
    this.httpConnection = openConnection(this.context);
    if (this.httpConnection == null)
      this.httpConnection = (HttpURLConnection)this.endpoint.openConnection(); 
    String str1 = this.endpoint.getURI().getScheme();
    if (str1.equals("https"))
      this.https = true; 
    if (checkHTTPS(this.httpConnection))
      this.https = true; 
    this.httpConnection.setAllowUserInteraction(true);
    this.httpConnection.setDoOutput(true);
    this.httpConnection.setDoInput(true);
    String str2 = (String)this.context.invocationProperties.get("javax.xml.ws.http.request.method");
    String str3 = (str2 != null) ? str2 : "POST";
    this.httpConnection.setRequestMethod(str3);
    Integer integer1 = (Integer)this.context.invocationProperties.get("com.sun.xml.internal.ws.request.timeout");
    if (integer1 != null)
      this.httpConnection.setReadTimeout(integer1.intValue()); 
    Integer integer2 = (Integer)this.context.invocationProperties.get("com.sun.xml.internal.ws.connect.timeout");
    if (integer2 != null)
      this.httpConnection.setConnectTimeout(integer2.intValue()); 
    Integer integer3 = (Integer)this.context.invocationProperties.get("com.sun.xml.internal.ws.transport.http.client.streaming.chunk.size");
    if (integer3 != null)
      this.httpConnection.setChunkedStreamingMode(integer3.intValue()); 
    for (Map.Entry entry : this.reqHeaders.entrySet()) {
      if ("Content-Length".equals(entry.getKey()))
        continue; 
      for (String str : (List)entry.getValue())
        this.httpConnection.addRequestProperty((String)entry.getKey(), str); 
    } 
  }
  
  boolean isSecure() { return this.https; }
  
  protected void setStatusCode(int paramInt) { this.statusCode = paramInt; }
  
  private boolean requiresOutputStream() { return (!this.httpConnection.getRequestMethod().equalsIgnoreCase("GET") && !this.httpConnection.getRequestMethod().equalsIgnoreCase("HEAD") && !this.httpConnection.getRequestMethod().equalsIgnoreCase("DELETE")); }
  
  @Nullable
  String getContentType() { return this.httpConnection.getContentType(); }
  
  public int getContentLength() { return this.httpConnection.getContentLength(); }
  
  static  {
    try {
      JAXBContext.newInstance(new Class[0]).createUnmarshaller();
    } catch (JAXBException jAXBException) {}
  }
  
  private static class HttpClientVerifier implements HostnameVerifier {
    private HttpClientVerifier() throws IOException {}
    
    public boolean verify(String param1String, SSLSession param1SSLSession) { return true; }
  }
  
  private static class LocalhostHttpClientVerifier implements HostnameVerifier {
    public boolean verify(String param1String, SSLSession param1SSLSession) { return ("localhost".equalsIgnoreCase(param1String) || "127.0.0.1".equals(param1String)); }
  }
  
  private static final class WSChunkedOuputStream extends FilterOutputStream {
    final int chunkSize;
    
    WSChunkedOuputStream(OutputStream param1OutputStream, int param1Int) {
      super(param1OutputStream);
      this.chunkSize = param1Int;
    }
    
    public void write(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      while (param1Int2 > 0) {
        int i = (param1Int2 > this.chunkSize) ? this.chunkSize : param1Int2;
        this.out.write(param1ArrayOfByte, param1Int1, i);
        param1Int2 -= i;
        param1Int1 += i;
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\transport\http\client\HttpClientTransport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */