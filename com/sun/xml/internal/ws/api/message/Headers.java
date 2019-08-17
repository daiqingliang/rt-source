package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.db.glassfish.BridgeWrapper;
import com.sun.xml.internal.ws.message.DOMHeader;
import com.sun.xml.internal.ws.message.StringHeader;
import com.sun.xml.internal.ws.message.jaxb.JAXBHeader;
import com.sun.xml.internal.ws.message.saaj.SAAJHeader;
import com.sun.xml.internal.ws.message.stream.StreamHeader11;
import com.sun.xml.internal.ws.message.stream.StreamHeader12;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.w3c.dom.Element;

public abstract class Headers {
  public static Header create(SOAPVersion paramSOAPVersion, Marshaller paramMarshaller, Object paramObject) { return new JAXBHeader(BindingContextFactory.getBindingContext(paramMarshaller), paramObject); }
  
  public static Header create(JAXBContext paramJAXBContext, Object paramObject) { return new JAXBHeader(BindingContextFactory.create(paramJAXBContext), paramObject); }
  
  public static Header create(BindingContext paramBindingContext, Object paramObject) { return new JAXBHeader(paramBindingContext, paramObject); }
  
  public static Header create(SOAPVersion paramSOAPVersion, Marshaller paramMarshaller, QName paramQName, Object paramObject) { return create(paramSOAPVersion, paramMarshaller, new JAXBElement(paramQName, paramObject.getClass(), paramObject)); }
  
  public static Header create(Bridge paramBridge, Object paramObject) { return new JAXBHeader(new BridgeWrapper(null, paramBridge), paramObject); }
  
  public static Header create(XMLBridge paramXMLBridge, Object paramObject) { return new JAXBHeader(paramXMLBridge, paramObject); }
  
  public static Header create(SOAPHeaderElement paramSOAPHeaderElement) { return new SAAJHeader(paramSOAPHeaderElement); }
  
  public static Header create(Element paramElement) { return new DOMHeader(paramElement); }
  
  public static Header create(SOAPVersion paramSOAPVersion, Element paramElement) { return create(paramElement); }
  
  public static Header create(SOAPVersion paramSOAPVersion, XMLStreamReader paramXMLStreamReader) throws XMLStreamException {
    switch (paramSOAPVersion) {
      case SOAP_11:
        return new StreamHeader11(paramXMLStreamReader);
      case SOAP_12:
        return new StreamHeader12(paramXMLStreamReader);
    } 
    throw new AssertionError();
  }
  
  public static Header create(QName paramQName, String paramString) { return new StringHeader(paramQName, paramString); }
  
  public static Header createMustUnderstand(@NotNull SOAPVersion paramSOAPVersion, @NotNull QName paramQName, @NotNull String paramString) { return new StringHeader(paramQName, paramString, paramSOAPVersion, true); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\message\Headers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */