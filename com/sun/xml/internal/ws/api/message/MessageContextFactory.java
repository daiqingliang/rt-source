package com.sun.xml.internal.ws.api.message;

import com.oracle.webservices.internal.api.EnvelopeStyle;
import com.oracle.webservices.internal.api.EnvelopeStyleFeature;
import com.oracle.webservices.internal.api.message.MessageContext;
import com.oracle.webservices.internal.api.message.MessageContextFactory;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.Codecs;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.MTOMFeature;

public class MessageContextFactory extends MessageContextFactory {
  private WSFeatureList features;
  
  private Codec soapCodec;
  
  private Codec xmlCodec;
  
  private EnvelopeStyleFeature envelopeStyle;
  
  private EnvelopeStyle.Style singleSoapStyle;
  
  public MessageContextFactory(WebServiceFeature[] paramArrayOfWebServiceFeature) { this(new WebServiceFeatureList(paramArrayOfWebServiceFeature)); }
  
  public MessageContextFactory(WSFeatureList paramWSFeatureList) {
    this.features = paramWSFeatureList;
    this.envelopeStyle = (EnvelopeStyleFeature)this.features.get(EnvelopeStyleFeature.class);
    if (this.envelopeStyle == null) {
      this.envelopeStyle = new EnvelopeStyleFeature(new EnvelopeStyle.Style[] { EnvelopeStyle.Style.SOAP11 });
      this.features.mergeFeatures(new WebServiceFeature[] { this.envelopeStyle }, false);
    } 
    for (EnvelopeStyle.Style style : this.envelopeStyle.getStyles()) {
      if (style.isXML()) {
        if (this.xmlCodec == null)
          this.xmlCodec = Codecs.createXMLCodec(this.features); 
      } else {
        if (this.soapCodec == null)
          this.soapCodec = Codecs.createSOAPBindingCodec(this.features); 
        this.singleSoapStyle = style;
      } 
    } 
  }
  
  protected MessageContextFactory newFactory(WebServiceFeature... paramVarArgs) { return new MessageContextFactory(paramVarArgs); }
  
  public MessageContext createContext() { return packet(null); }
  
  public MessageContext createContext(SOAPMessage paramSOAPMessage) {
    throwIfIllegalMessageArgument(paramSOAPMessage);
    return packet(Messages.create(paramSOAPMessage));
  }
  
  public MessageContext createContext(Source paramSource, EnvelopeStyle.Style paramStyle) {
    throwIfIllegalMessageArgument(paramSource);
    return packet(Messages.create(paramSource, SOAPVersion.from(paramStyle)));
  }
  
  public MessageContext createContext(Source paramSource) {
    throwIfIllegalMessageArgument(paramSource);
    return packet(Messages.create(paramSource, SOAPVersion.from(this.singleSoapStyle)));
  }
  
  public MessageContext createContext(InputStream paramInputStream, String paramString) throws IOException {
    throwIfIllegalMessageArgument(paramInputStream);
    Packet packet = packet(null);
    this.soapCodec.decode(paramInputStream, paramString, packet);
    return packet;
  }
  
  @Deprecated
  public MessageContext createContext(InputStream paramInputStream, MimeHeaders paramMimeHeaders) throws IOException {
    String str = getHeader(paramMimeHeaders, "Content-Type");
    Packet packet = (Packet)createContext(paramInputStream, str);
    packet.acceptableMimeTypes = getHeader(paramMimeHeaders, "Accept");
    packet.soapAction = HttpAdapter.fixQuotesAroundSoapAction(getHeader(paramMimeHeaders, "SOAPAction"));
    return packet;
  }
  
  static String getHeader(MimeHeaders paramMimeHeaders, String paramString) {
    String[] arrayOfString = paramMimeHeaders.getHeader(paramString);
    return (arrayOfString != null && arrayOfString.length > 0) ? arrayOfString[0] : null;
  }
  
  static Map<String, List<String>> toMap(MimeHeaders paramMimeHeaders) {
    HashMap hashMap = new HashMap();
    Iterator iterator = paramMimeHeaders.getAllHeaders();
    while (iterator.hasNext()) {
      MimeHeader mimeHeader = (MimeHeader)iterator.next();
      List list = (List)hashMap.get(mimeHeader.getName());
      if (list == null) {
        list = new ArrayList();
        hashMap.put(mimeHeader.getName(), list);
      } 
      list.add(mimeHeader.getValue());
    } 
    return hashMap;
  }
  
  public MessageContext createContext(Message paramMessage) {
    throwIfIllegalMessageArgument(paramMessage);
    return packet(paramMessage);
  }
  
  private Packet packet(Message paramMessage) {
    Packet packet = new Packet();
    packet.codec = this.soapCodec;
    if (paramMessage != null)
      packet.setMessage(paramMessage); 
    MTOMFeature mTOMFeature = (MTOMFeature)this.features.get(MTOMFeature.class);
    if (mTOMFeature != null)
      packet.setMtomFeature(mTOMFeature); 
    return packet;
  }
  
  private void throwIfIllegalMessageArgument(Object paramObject) throws IllegalArgumentException {
    if (paramObject == null)
      throw new IllegalArgumentException("null messages are not allowed.  Consider using MessageContextFactory.createContext()"); 
  }
  
  @Deprecated
  public MessageContext doCreate() { return packet(null); }
  
  @Deprecated
  public MessageContext doCreate(SOAPMessage paramSOAPMessage) { return createContext(paramSOAPMessage); }
  
  @Deprecated
  public MessageContext doCreate(Source paramSource, SOAPVersion paramSOAPVersion) { return packet(Messages.create(paramSource, paramSOAPVersion)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\message\MessageContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */