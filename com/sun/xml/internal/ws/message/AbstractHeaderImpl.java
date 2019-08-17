package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.api.BridgeContext;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.helpers.AttributesImpl;

public abstract class AbstractHeaderImpl implements Header {
  protected static final AttributesImpl EMPTY_ATTS = new AttributesImpl();
  
  public final <T> T readAsJAXB(Bridge<T> paramBridge, BridgeContext paramBridgeContext) throws JAXBException { return (T)readAsJAXB(paramBridge); }
  
  public <T> T readAsJAXB(Unmarshaller paramUnmarshaller) throws JAXBException {
    try {
      return (T)paramUnmarshaller.unmarshal(readHeader());
    } catch (Exception exception) {
      throw new JAXBException(exception);
    } 
  }
  
  public <T> T readAsJAXB(Bridge<T> paramBridge) throws JAXBException {
    try {
      return (T)paramBridge.unmarshal(readHeader());
    } catch (XMLStreamException xMLStreamException) {
      throw new JAXBException(xMLStreamException);
    } 
  }
  
  public <T> T readAsJAXB(XMLBridge<T> paramXMLBridge) throws JAXBException {
    try {
      return (T)paramXMLBridge.unmarshal(readHeader(), null);
    } catch (XMLStreamException xMLStreamException) {
      throw new JAXBException(xMLStreamException);
    } 
  }
  
  public WSEndpointReference readAsEPR(AddressingVersion paramAddressingVersion) throws XMLStreamException {
    XMLStreamReader xMLStreamReader = readHeader();
    WSEndpointReference wSEndpointReference = new WSEndpointReference(xMLStreamReader, paramAddressingVersion);
    XMLStreamReaderFactory.recycle(xMLStreamReader);
    return wSEndpointReference;
  }
  
  public boolean isIgnorable(@NotNull SOAPVersion paramSOAPVersion, @NotNull Set<String> paramSet) {
    String str = getAttribute(paramSOAPVersion.nsUri, "mustUnderstand");
    return (str == null || !parseBool(str)) ? true : ((paramSet == null) ? true : (!paramSet.contains(getRole(paramSOAPVersion))));
  }
  
  @NotNull
  public String getRole(@NotNull SOAPVersion paramSOAPVersion) {
    String str = getAttribute(paramSOAPVersion.nsUri, paramSOAPVersion.roleAttributeName);
    if (str == null)
      str = paramSOAPVersion.implicitRole; 
    return str;
  }
  
  public boolean isRelay() {
    String str = getAttribute(SOAPVersion.SOAP_12.nsUri, "relay");
    return (str == null) ? false : parseBool(str);
  }
  
  public String getAttribute(QName paramQName) { return getAttribute(paramQName.getNamespaceURI(), paramQName.getLocalPart()); }
  
  protected final boolean parseBool(String paramString) {
    if (paramString.length() == 0)
      return false; 
    char c = paramString.charAt(0);
    return (c == 't' || c == '1');
  }
  
  public String getStringContent() {
    try {
      XMLStreamReader xMLStreamReader = readHeader();
      xMLStreamReader.nextTag();
      return xMLStreamReader.getElementText();
    } catch (XMLStreamException xMLStreamException) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\AbstractHeaderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */