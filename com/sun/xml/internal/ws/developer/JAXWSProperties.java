package com.sun.xml.internal.ws.developer;

public interface JAXWSProperties {
  @Deprecated
  public static final String CONTENT_NEGOTIATION_PROPERTY = "com.sun.xml.ws.client.ContentNegotiation";
  
  public static final String MTOM_THRESHOLOD_VALUE = "com.sun.xml.internal.ws.common.MtomThresholdValue";
  
  public static final String HTTP_EXCHANGE = "com.sun.xml.internal.ws.http.exchange";
  
  public static final String CONNECT_TIMEOUT = "com.sun.xml.internal.ws.connect.timeout";
  
  public static final String REQUEST_TIMEOUT = "com.sun.xml.internal.ws.request.timeout";
  
  public static final String HTTP_CLIENT_STREAMING_CHUNK_SIZE = "com.sun.xml.internal.ws.transport.http.client.streaming.chunk.size";
  
  public static final String HOSTNAME_VERIFIER = "com.sun.xml.internal.ws.transport.https.client.hostname.verifier";
  
  public static final String SSL_SOCKET_FACTORY = "com.sun.xml.internal.ws.transport.https.client.SSLSocketFactory";
  
  public static final String INBOUND_HEADER_LIST_PROPERTY = "com.sun.xml.internal.ws.api.message.HeaderList";
  
  public static final String WSENDPOINT = "com.sun.xml.internal.ws.api.server.WSEndpoint";
  
  public static final String ADDRESSING_TO = "com.sun.xml.internal.ws.api.addressing.to";
  
  public static final String ADDRESSING_FROM = "com.sun.xml.internal.ws.api.addressing.from";
  
  public static final String ADDRESSING_ACTION = "com.sun.xml.internal.ws.api.addressing.action";
  
  public static final String ADDRESSING_MESSAGEID = "com.sun.xml.internal.ws.api.addressing.messageId";
  
  public static final String HTTP_REQUEST_URL = "com.sun.xml.internal.ws.transport.http.servlet.requestURL";
  
  public static final String REST_BINDING = "http://jax-ws.dev.java.net/rest";
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\developer\JAXWSProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */