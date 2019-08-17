package com.sun.xml.internal.ws.util.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.api.server.DocumentAddressResolver;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import com.sun.xml.internal.ws.developer.SchemaValidationFeature;
import com.sun.xml.internal.ws.developer.ValidationErrorHandler;
import com.sun.xml.internal.ws.server.SDDocumentImpl;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.SDDocumentResolver;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.NamespaceSupport;

public abstract class AbstractSchemaValidationTube extends AbstractFilterTubeImpl {
  private static final Logger LOGGER = Logger.getLogger(AbstractSchemaValidationTube.class.getName());
  
  protected final WSBinding binding;
  
  protected final SchemaValidationFeature feature;
  
  protected final DocumentAddressResolver resolver = new ValidationDocumentAddressResolver(null);
  
  protected final SchemaFactory sf;
  
  public AbstractSchemaValidationTube(WSBinding paramWSBinding, Tube paramTube) {
    super(paramTube);
    this.binding = paramWSBinding;
    this.feature = (SchemaValidationFeature)paramWSBinding.getFeature(SchemaValidationFeature.class);
    this.sf = XmlUtil.allowExternalAccess(SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema"), "file", false);
  }
  
  protected AbstractSchemaValidationTube(AbstractSchemaValidationTube paramAbstractSchemaValidationTube, TubeCloner paramTubeCloner) {
    super(paramAbstractSchemaValidationTube, paramTubeCloner);
    this.binding = paramAbstractSchemaValidationTube.binding;
    this.feature = paramAbstractSchemaValidationTube.feature;
    this.sf = paramAbstractSchemaValidationTube.sf;
  }
  
  protected abstract Validator getValidator();
  
  protected abstract boolean isNoValidation();
  
  private Document createDOM(SDDocument paramSDDocument) {
    ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer();
    try {
      paramSDDocument.writeTo(null, this.resolver, byteArrayBuffer);
    } catch (IOException iOException) {
      throw new WebServiceException(iOException);
    } 
    Transformer transformer = XmlUtil.newTransformer();
    StreamSource streamSource = new StreamSource(byteArrayBuffer.newInputStream(), null);
    DOMResult dOMResult = new DOMResult();
    try {
      transformer.transform(streamSource, dOMResult);
    } catch (TransformerException transformerException) {
      throw new WebServiceException(transformerException);
    } 
    return (Document)dOMResult.getNode();
  }
  
  private void updateMultiSchemaForTns(String paramString1, String paramString2, Map<String, List<String>> paramMap) {
    List list = (List)paramMap.get(paramString1);
    if (list == null) {
      list = new ArrayList();
      paramMap.put(paramString1, list);
    } 
    list.add(paramString2);
  }
  
  protected Source[] getSchemaSources(Iterable<SDDocument> paramIterable, MetadataResolverImpl paramMetadataResolverImpl) {
    HashMap hashMap1 = new HashMap();
    HashMap hashMap2 = new HashMap();
    for (SDDocument sDDocument : paramIterable) {
      if (sDDocument.isWSDL()) {
        Document document = createDOM(sDDocument);
        addSchemaFragmentSource(document, sDDocument.getURL().toExternalForm(), hashMap1);
        continue;
      } 
      if (sDDocument.isSchema())
        updateMultiSchemaForTns(((SDDocument.Schema)sDDocument).getTargetNamespace(), sDDocument.getURL().toExternalForm(), hashMap2); 
    } 
    if (LOGGER.isLoggable(Level.FINE))
      LOGGER.log(Level.FINE, "WSDL inlined schema fragment documents(these are used to create a pseudo schema) = {0}", hashMap1.keySet()); 
    for (DOMSource dOMSource : hashMap1.values()) {
      String str = getTargetNamespace(dOMSource);
      updateMultiSchemaForTns(str, dOMSource.getSystemId(), hashMap2);
    } 
    if (hashMap2.isEmpty())
      return new Source[0]; 
    if (hashMap2.size() == 1 && ((List)hashMap2.values().iterator().next()).size() == 1) {
      String str = (String)((List)hashMap2.values().iterator().next()).get(0);
      return new Source[] { (Source)hashMap1.get(str) };
    } 
    paramMetadataResolverImpl.addSchemas(hashMap1.values());
    HashMap hashMap3 = new HashMap();
    byte b = 0;
    for (Map.Entry entry : hashMap2.entrySet()) {
      String str;
      List list = (List)entry.getValue();
      if (list.size() > 1) {
        str = "file:x-jax-ws-include-" + b++;
        Source source1 = createSameTnsPseudoSchema((String)entry.getKey(), list, str);
        paramMetadataResolverImpl.addSchema(source1);
      } else {
        str = (String)list.get(0);
      } 
      hashMap3.put(entry.getKey(), str);
    } 
    Source source = createMasterPseudoSchema(hashMap3);
    return new Source[] { source };
  }
  
  @Nullable
  private void addSchemaFragmentSource(Document paramDocument, String paramString, Map<String, DOMSource> paramMap) {
    Element element = paramDocument.getDocumentElement();
    assert element.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/");
    assert element.getLocalName().equals("definitions");
    NodeList nodeList = element.getElementsByTagNameNS("http://schemas.xmlsoap.org/wsdl/", "types");
    for (byte b = 0; b < nodeList.getLength(); b++) {
      NodeList nodeList1 = ((Element)nodeList.item(b)).getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "schema");
      for (byte b1 = 0; b1 < nodeList1.getLength(); b1++) {
        Element element1 = (Element)nodeList1.item(b1);
        NamespaceSupport namespaceSupport = new NamespaceSupport();
        buildNamespaceSupport(namespaceSupport, element1);
        patchDOMFragment(namespaceSupport, element1);
        String str = paramString + "#schema" + b1;
        paramMap.put(str, new DOMSource(element1, str));
      } 
    } 
  }
  
  private void buildNamespaceSupport(NamespaceSupport paramNamespaceSupport, Node paramNode) {
    if (paramNode == null || paramNode.getNodeType() != 1)
      return; 
    buildNamespaceSupport(paramNamespaceSupport, paramNode.getParentNode());
    paramNamespaceSupport.pushContext();
    NamedNodeMap namedNodeMap = paramNode.getAttributes();
    for (byte b = 0; b < namedNodeMap.getLength(); b++) {
      Attr attr = (Attr)namedNodeMap.item(b);
      if ("xmlns".equals(attr.getPrefix())) {
        paramNamespaceSupport.declarePrefix(attr.getLocalName(), attr.getValue());
      } else if ("xmlns".equals(attr.getName())) {
        paramNamespaceSupport.declarePrefix("", attr.getValue());
      } 
    } 
  }
  
  @Nullable
  private void patchDOMFragment(NamespaceSupport paramNamespaceSupport, Element paramElement) {
    NamedNodeMap namedNodeMap = paramElement.getAttributes();
    Enumeration enumeration = paramNamespaceSupport.getPrefixes();
    while (enumeration.hasMoreElements()) {
      String str = (String)enumeration.nextElement();
      for (byte b = 0; b < namedNodeMap.getLength(); b++) {
        Attr attr = (Attr)namedNodeMap.item(b);
        if (!"xmlns".equals(attr.getPrefix()) || !attr.getLocalName().equals(str)) {
          if (LOGGER.isLoggable(Level.FINE))
            LOGGER.log(Level.FINE, "Patching with xmlns:{0}={1}", new Object[] { str, paramNamespaceSupport.getURI(str) }); 
          paramElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + str, paramNamespaceSupport.getURI(str));
        } 
      } 
    } 
  }
  
  @Nullable
  private Source createSameTnsPseudoSchema(String paramString1, Collection<String> paramCollection, String paramString2) {
    assert paramCollection.size() > 1;
    final StringBuilder sb = new StringBuilder("<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'");
    if (!paramString1.equals(""))
      stringBuilder.append(" targetNamespace='").append(paramString1).append("'"); 
    stringBuilder.append(">\n");
    for (String str : paramCollection)
      stringBuilder.append("<xsd:include schemaLocation='").append(str).append("'/>\n"); 
    stringBuilder.append("</xsd:schema>\n");
    if (LOGGER.isLoggable(Level.FINE))
      LOGGER.log(Level.FINE, "Pseudo Schema for the same tns={0}is {1}", new Object[] { paramString1, stringBuilder }); 
    return new StreamSource(paramString2) {
        public Reader getReader() { return new StringReader(sb.toString()); }
      };
  }
  
  private Source createMasterPseudoSchema(Map<String, String> paramMap) {
    final StringBuilder sb = new StringBuilder("<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema' targetNamespace='urn:x-jax-ws-master'>\n");
    for (Map.Entry entry : paramMap.entrySet()) {
      String str1 = (String)entry.getValue();
      String str2 = (String)entry.getKey();
      stringBuilder.append("<xsd:import schemaLocation='").append(str1).append("'");
      if (!str2.equals(""))
        stringBuilder.append(" namespace='").append(str2).append("'"); 
      stringBuilder.append("/>\n");
    } 
    stringBuilder.append("</xsd:schema>");
    if (LOGGER.isLoggable(Level.FINE))
      LOGGER.log(Level.FINE, "Master Pseudo Schema = {0}", stringBuilder); 
    return new StreamSource("file:x-jax-ws-master-doc") {
        public Reader getReader() { return new StringReader(sb.toString()); }
      };
  }
  
  protected void doProcess(Packet paramPacket) throws SAXException {
    ValidationErrorHandler validationErrorHandler;
    getValidator().reset();
    Class clazz = this.feature.getErrorHandler();
    try {
      validationErrorHandler = (ValidationErrorHandler)clazz.newInstance();
    } catch (Exception exception) {
      throw new WebServiceException(exception);
    } 
    validationErrorHandler.setPacket(paramPacket);
    getValidator().setErrorHandler(validationErrorHandler);
    Message message = paramPacket.getMessage().copy();
    Source source = message.readPayloadAsSource();
    try {
      getValidator().validate(source);
    } catch (IOException iOException) {
      throw new WebServiceException(iOException);
    } 
  }
  
  private String getTargetNamespace(DOMSource paramDOMSource) {
    Element element = (Element)paramDOMSource.getNode();
    return element.getAttribute("targetNamespace");
  }
  
  protected class MetadataResolverImpl implements SDDocumentResolver, LSResourceResolver {
    final Map<String, SDDocument> docs = new HashMap();
    
    final Map<String, SDDocument> nsMapping = new HashMap();
    
    public MetadataResolverImpl() {}
    
    public MetadataResolverImpl(Iterable<SDDocument> param1Iterable) {
      for (SDDocument sDDocument : param1Iterable) {
        if (sDDocument.isSchema()) {
          this.docs.put(sDDocument.getURL().toExternalForm(), sDDocument);
          this.nsMapping.put(((SDDocument.Schema)sDDocument).getTargetNamespace(), sDDocument);
        } 
      } 
    }
    
    void addSchema(Source param1Source) {
      assert param1Source.getSystemId() != null;
      String str = param1Source.getSystemId();
      try {
        XMLStreamBufferResult xMLStreamBufferResult = (XMLStreamBufferResult)XmlUtil.identityTransform(param1Source, new XMLStreamBufferResult());
        SDDocumentSource sDDocumentSource = SDDocumentSource.create(new URL(str), xMLStreamBufferResult.getXMLStreamBuffer());
        SDDocumentImpl sDDocumentImpl = SDDocumentImpl.create(sDDocumentSource, new QName(""), new QName(""));
        this.docs.put(str, sDDocumentImpl);
        this.nsMapping.put(((SDDocument.Schema)sDDocumentImpl).getTargetNamespace(), sDDocumentImpl);
      } catch (Exception exception) {
        LOGGER.log(Level.WARNING, "Exception in adding schemas to resolver", exception);
      } 
    }
    
    void addSchemas(Collection<? extends Source> param1Collection) {
      for (Source source : param1Collection)
        addSchema(source); 
    }
    
    public SDDocument resolve(String param1String) {
      SDDocument sDDocument = (SDDocument)this.docs.get(param1String);
      if (sDDocument == null) {
        SDDocumentSource sDDocumentSource;
        try {
          sDDocumentSource = SDDocumentSource.create(new URL(param1String));
        } catch (MalformedURLException malformedURLException) {
          throw new WebServiceException(malformedURLException);
        } 
        sDDocument = SDDocumentImpl.create(sDDocumentSource, new QName(""), new QName(""));
        this.docs.put(param1String, sDDocument);
      } 
      return sDDocument;
    }
    
    public LSInput resolveResource(String param1String1, String param1String2, String param1String3, String param1String4, String param1String5) {
      if (LOGGER.isLoggable(Level.FINE))
        LOGGER.log(Level.FINE, "type={0} namespaceURI={1} publicId={2} systemId={3} baseURI={4}", new Object[] { param1String1, param1String2, param1String3, param1String4, param1String5 }); 
      try {
        final SDDocument doc;
        if (param1String4 == null) {
          sDDocument = (SDDocument)this.nsMapping.get(param1String2);
        } else {
          URI uRI = (param1String5 != null) ? (new URI(param1String5)).resolve(param1String4) : new URI(param1String4);
          sDDocument = (SDDocument)this.docs.get(uRI.toString());
        } 
        if (sDDocument != null)
          return new LSInput() {
              public Reader getCharacterStream() { return null; }
              
              public void setCharacterStream(Reader param2Reader) { throw new UnsupportedOperationException(); }
              
              public InputStream getByteStream() {
                ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer();
                try {
                  doc.writeTo(null, AbstractSchemaValidationTube.this.resolver, byteArrayBuffer);
                } catch (IOException iOException) {
                  throw new WebServiceException(iOException);
                } 
                return byteArrayBuffer.newInputStream();
              }
              
              public void setByteStream(InputStream param2InputStream) { throw new UnsupportedOperationException(); }
              
              public String getStringData() { return null; }
              
              public void setStringData(String param2String) { throw new UnsupportedOperationException(); }
              
              public String getSystemId() { return doc.getURL().toExternalForm(); }
              
              public void setSystemId(String param2String) { throw new UnsupportedOperationException(); }
              
              public String getPublicId() { return null; }
              
              public void setPublicId(String param2String) { throw new UnsupportedOperationException(); }
              
              public String getBaseURI() { return doc.getURL().toExternalForm(); }
              
              public void setBaseURI(String param2String) { throw new UnsupportedOperationException(); }
              
              public String getEncoding() { return null; }
              
              public void setEncoding(String param2String) { throw new UnsupportedOperationException(); }
              
              public boolean getCertifiedText() { return false; }
              
              public void setCertifiedText(boolean param2Boolean) { throw new UnsupportedOperationException(); }
            }; 
      } catch (Exception exception) {
        LOGGER.log(Level.WARNING, "Exception in LSResourceResolver impl", exception);
      } 
      if (LOGGER.isLoggable(Level.FINE))
        LOGGER.log(Level.FINE, "Don''t know about systemId={0} baseURI={1}", new Object[] { param1String4, param1String5 }); 
      return null;
    }
  }
  
  private static class ValidationDocumentAddressResolver implements DocumentAddressResolver {
    private ValidationDocumentAddressResolver() {}
    
    @Nullable
    public String getRelativeAddressFor(@NotNull SDDocument param1SDDocument1, @NotNull SDDocument param1SDDocument2) {
      LOGGER.log(Level.FINE, "Current = {0} resolved relative={1}", new Object[] { param1SDDocument1.getURL(), param1SDDocument2.getURL() });
      return param1SDDocument2.getURL().toExternalForm();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\pipe\AbstractSchemaValidationTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */