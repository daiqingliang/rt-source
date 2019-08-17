package com.sun.xml.internal.ws.server;

import com.oracle.webservices.internal.api.databinding.WSDLResolver;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

final class WSDLGenResolver implements WSDLResolver {
  private final List<SDDocumentImpl> docs;
  
  private final List<SDDocumentSource> newDocs = new ArrayList();
  
  private SDDocumentSource concreteWsdlSource;
  
  private SDDocumentImpl abstractWsdl;
  
  private SDDocumentImpl concreteWsdl;
  
  private final Map<String, List<SDDocumentImpl>> nsMapping = new HashMap();
  
  private final QName serviceName;
  
  private final QName portTypeName;
  
  public WSDLGenResolver(@NotNull List<SDDocumentImpl> paramList, QName paramQName1, QName paramQName2) {
    this.docs = paramList;
    this.serviceName = paramQName1;
    this.portTypeName = paramQName2;
    for (SDDocumentImpl sDDocumentImpl : paramList) {
      if (sDDocumentImpl.isWSDL()) {
        SDDocument.WSDL wSDL = (SDDocument.WSDL)sDDocumentImpl;
        if (wSDL.hasPortType())
          this.abstractWsdl = sDDocumentImpl; 
      } 
      if (sDDocumentImpl.isSchema()) {
        SDDocument.Schema schema = (SDDocument.Schema)sDDocumentImpl;
        List list = (List)this.nsMapping.get(schema.getTargetNamespace());
        if (list == null) {
          list = new ArrayList();
          this.nsMapping.put(schema.getTargetNamespace(), list);
        } 
        list.add(sDDocumentImpl);
      } 
    } 
  }
  
  public Result getWSDL(String paramString) {
    URL uRL = createURL(paramString);
    MutableXMLStreamBuffer mutableXMLStreamBuffer = new MutableXMLStreamBuffer();
    mutableXMLStreamBuffer.setSystemId(uRL.toExternalForm());
    this.concreteWsdlSource = SDDocumentSource.create(uRL, mutableXMLStreamBuffer);
    this.newDocs.add(this.concreteWsdlSource);
    XMLStreamBufferResult xMLStreamBufferResult = new XMLStreamBufferResult(mutableXMLStreamBuffer);
    xMLStreamBufferResult.setSystemId(paramString);
    return xMLStreamBufferResult;
  }
  
  private URL createURL(String paramString) {
    try {
      return new URL("file:///" + paramString);
    } catch (MalformedURLException malformedURLException) {
      throw new WebServiceException(malformedURLException);
    } 
  }
  
  public Result getAbstractWSDL(Holder<String> paramHolder) {
    if (this.abstractWsdl != null) {
      paramHolder.value = this.abstractWsdl.getURL().toString();
      return null;
    } 
    URL uRL = createURL((String)paramHolder.value);
    MutableXMLStreamBuffer mutableXMLStreamBuffer = new MutableXMLStreamBuffer();
    mutableXMLStreamBuffer.setSystemId(uRL.toExternalForm());
    SDDocumentSource sDDocumentSource = SDDocumentSource.create(uRL, mutableXMLStreamBuffer);
    this.newDocs.add(sDDocumentSource);
    XMLStreamBufferResult xMLStreamBufferResult = new XMLStreamBufferResult(mutableXMLStreamBuffer);
    xMLStreamBufferResult.setSystemId((String)paramHolder.value);
    return xMLStreamBufferResult;
  }
  
  public Result getSchemaOutput(String paramString, Holder<String> paramHolder) {
    List list = (List)this.nsMapping.get(paramString);
    if (list != null) {
      if (list.size() > 1)
        throw new ServerRtException("server.rt.err", new Object[] { "More than one schema for the target namespace " + paramString }); 
      paramHolder.value = ((SDDocumentImpl)list.get(0)).getURL().toExternalForm();
      return null;
    } 
    URL uRL = createURL((String)paramHolder.value);
    MutableXMLStreamBuffer mutableXMLStreamBuffer = new MutableXMLStreamBuffer();
    mutableXMLStreamBuffer.setSystemId(uRL.toExternalForm());
    SDDocumentSource sDDocumentSource = SDDocumentSource.create(uRL, mutableXMLStreamBuffer);
    this.newDocs.add(sDDocumentSource);
    XMLStreamBufferResult xMLStreamBufferResult = new XMLStreamBufferResult(mutableXMLStreamBuffer);
    xMLStreamBufferResult.setSystemId((String)paramHolder.value);
    return xMLStreamBufferResult;
  }
  
  public SDDocumentImpl updateDocs() {
    for (SDDocumentSource sDDocumentSource : this.newDocs) {
      SDDocumentImpl sDDocumentImpl = SDDocumentImpl.create(sDDocumentSource, this.serviceName, this.portTypeName);
      if (sDDocumentSource == this.concreteWsdlSource)
        this.concreteWsdl = sDDocumentImpl; 
      this.docs.add(sDDocumentImpl);
    } 
    return this.concreteWsdl;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\WSDLGenResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */