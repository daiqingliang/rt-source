package com.sun.xml.internal.ws.encoding.fastinfoset;

import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.internal.ws.encoding.ContentTypeImpl;
import com.sun.xml.internal.ws.message.stream.StreamHeader;
import com.sun.xml.internal.ws.message.stream.StreamHeader11;
import javax.xml.stream.XMLStreamReader;

final class FastInfosetStreamSOAP11Codec extends FastInfosetStreamSOAPCodec {
  FastInfosetStreamSOAP11Codec(StreamSOAPCodec paramStreamSOAPCodec, boolean paramBoolean) { super(paramStreamSOAPCodec, SOAPVersion.SOAP_11, paramBoolean, paramBoolean ? "application/vnd.sun.stateful.fastinfoset" : "application/fastinfoset"); }
  
  private FastInfosetStreamSOAP11Codec(FastInfosetStreamSOAP11Codec paramFastInfosetStreamSOAP11Codec) { super(paramFastInfosetStreamSOAP11Codec); }
  
  public Codec copy() { return new FastInfosetStreamSOAP11Codec(this); }
  
  protected final StreamHeader createHeader(XMLStreamReader paramXMLStreamReader, XMLStreamBuffer paramXMLStreamBuffer) { return new StreamHeader11(paramXMLStreamReader, paramXMLStreamBuffer); }
  
  protected ContentType getContentType(String paramString) { return (paramString == null || paramString.length() == 0) ? this._defaultContentType : new ContentTypeImpl(this._defaultContentType.getContentType(), paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\fastinfoset\FastInfosetStreamSOAP11Codec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */