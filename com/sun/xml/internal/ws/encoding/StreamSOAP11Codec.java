package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import java.util.Collections;
import java.util.List;

final class StreamSOAP11Codec extends StreamSOAPCodec {
  public static final String SOAP11_MIME_TYPE = "text/xml";
  
  public static final String DEFAULT_SOAP11_CONTENT_TYPE = "text/xml; charset=utf-8";
  
  private static final List<String> EXPECTED_CONTENT_TYPES = Collections.singletonList("text/xml");
  
  StreamSOAP11Codec() { super(SOAPVersion.SOAP_11); }
  
  StreamSOAP11Codec(WSBinding paramWSBinding) { super(paramWSBinding); }
  
  StreamSOAP11Codec(WSFeatureList paramWSFeatureList) { super(paramWSFeatureList); }
  
  public String getMimeType() { return "text/xml"; }
  
  protected ContentType getContentType(Packet paramPacket) {
    ContentTypeImpl.Builder builder = getContenTypeBuilder(paramPacket);
    builder.soapAction = paramPacket.soapAction;
    return builder.build();
  }
  
  protected String getDefaultContentType() { return "text/xml; charset=utf-8"; }
  
  protected List<String> getExpectedContentTypes() { return EXPECTED_CONTENT_TYPES; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\StreamSOAP11Codec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */