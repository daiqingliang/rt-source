package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.server.DocumentAddressResolver;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.api.server.SDDocumentFilter;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.util.RuntimeVersion;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderToXMLStreamWriter;
import com.sun.xml.internal.ws.wsdl.SDDocumentResolver;
import com.sun.xml.internal.ws.wsdl.parser.ParserUtil;
import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
import com.sun.xml.internal.ws.wsdl.writer.DocumentLocationResolver;
import com.sun.xml.internal.ws.wsdl.writer.WSDLPatcher;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.WebServiceException;

public class SDDocumentImpl extends SDDocumentSource implements SDDocument {
  private static final String NS_XSD = "http://www.w3.org/2001/XMLSchema";
  
  private static final QName SCHEMA_INCLUDE_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "include");
  
  private static final QName SCHEMA_IMPORT_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "import");
  
  private static final QName SCHEMA_REDEFINE_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "redefine");
  
  private static final String VERSION_COMMENT = " Published by JAX-WS RI (http://jax-ws.java.net). RI's version is " + RuntimeVersion.VERSION + ". ";
  
  private final QName rootName;
  
  private final SDDocumentSource source;
  
  @Nullable
  List<SDDocumentFilter> filters;
  
  @Nullable
  SDDocumentResolver sddocResolver;
  
  private final URL url;
  
  private final Set<String> imports;
  
  public static SDDocumentImpl create(SDDocumentSource paramSDDocumentSource, QName paramQName1, QName paramQName2) {
    URL uRL = paramSDDocumentSource.getSystemId();
    try {
      xMLStreamReader = paramSDDocumentSource.read();
      try {
        XMLStreamReaderUtil.nextElementContent(xMLStreamReader);
        QName qName = xMLStreamReader.getName();
        if (qName.equals(WSDLConstants.QNAME_SCHEMA)) {
          String str = ParserUtil.getMandatoryNonEmptyAttribute(xMLStreamReader, "targetNamespace");
          HashSet hashSet = new HashSet();
          while (XMLStreamReaderUtil.nextContent(xMLStreamReader) != 8) {
            if (xMLStreamReader.getEventType() != 1)
              continue; 
            QName qName1 = xMLStreamReader.getName();
            if (SCHEMA_INCLUDE_QNAME.equals(qName1) || SCHEMA_IMPORT_QNAME.equals(qName1) || SCHEMA_REDEFINE_QNAME.equals(qName1)) {
              String str1 = xMLStreamReader.getAttributeValue(null, "schemaLocation");
              if (str1 != null)
                hashSet.add((new URL(paramSDDocumentSource.getSystemId(), str1)).toString()); 
            } 
          } 
          return new SchemaImpl(qName, uRL, paramSDDocumentSource, str, hashSet);
        } 
        if (qName.equals(WSDLConstants.QNAME_DEFINITIONS)) {
          String str = ParserUtil.getMandatoryNonEmptyAttribute(xMLStreamReader, "targetNamespace");
          boolean bool1 = false;
          boolean bool2 = false;
          HashSet hashSet1 = new HashSet();
          HashSet hashSet2 = new HashSet();
          while (XMLStreamReaderUtil.nextContent(xMLStreamReader) != 8) {
            if (xMLStreamReader.getEventType() != 1)
              continue; 
            QName qName1 = xMLStreamReader.getName();
            if (WSDLConstants.QNAME_PORT_TYPE.equals(qName1)) {
              String str1 = ParserUtil.getMandatoryNonEmptyAttribute(xMLStreamReader, "name");
              if (paramQName2 != null && paramQName2.getLocalPart().equals(str1) && paramQName2.getNamespaceURI().equals(str))
                bool1 = true; 
              continue;
            } 
            if (WSDLConstants.QNAME_SERVICE.equals(qName1)) {
              String str1 = ParserUtil.getMandatoryNonEmptyAttribute(xMLStreamReader, "name");
              QName qName2 = new QName(str, str1);
              hashSet2.add(qName2);
              if (paramQName1.equals(qName2))
                bool2 = true; 
              continue;
            } 
            if (WSDLConstants.QNAME_IMPORT.equals(qName1)) {
              String str1 = xMLStreamReader.getAttributeValue(null, "location");
              if (str1 != null)
                hashSet1.add((new URL(paramSDDocumentSource.getSystemId(), str1)).toString()); 
              continue;
            } 
            if (SCHEMA_INCLUDE_QNAME.equals(qName1) || SCHEMA_IMPORT_QNAME.equals(qName1) || SCHEMA_REDEFINE_QNAME.equals(qName1)) {
              String str1 = xMLStreamReader.getAttributeValue(null, "schemaLocation");
              if (str1 != null)
                hashSet1.add((new URL(paramSDDocumentSource.getSystemId(), str1)).toString()); 
            } 
          } 
          return new WSDLImpl(qName, uRL, paramSDDocumentSource, str, bool1, bool2, hashSet1, hashSet2);
        } 
        return new SDDocumentImpl(qName, uRL, paramSDDocumentSource);
      } finally {
        xMLStreamReader.close();
      } 
    } catch (WebServiceException webServiceException) {
      throw new ServerRtException("runtime.parser.wsdl", new Object[] { uRL, webServiceException });
    } catch (IOException iOException) {
      throw new ServerRtException("runtime.parser.wsdl", new Object[] { uRL, iOException });
    } catch (XMLStreamException xMLStreamException) {
      throw new ServerRtException("runtime.parser.wsdl", new Object[] { uRL, xMLStreamException });
    } 
  }
  
  protected SDDocumentImpl(QName paramQName, URL paramURL, SDDocumentSource paramSDDocumentSource) { this(paramQName, paramURL, paramSDDocumentSource, new HashSet()); }
  
  protected SDDocumentImpl(QName paramQName, URL paramURL, SDDocumentSource paramSDDocumentSource, Set<String> paramSet) {
    if (paramURL == null)
      throw new IllegalArgumentException("Cannot construct SDDocument with null URL."); 
    this.rootName = paramQName;
    this.source = paramSDDocumentSource;
    this.url = paramURL;
    this.imports = paramSet;
  }
  
  void setFilters(List<SDDocumentFilter> paramList) { this.filters = paramList; }
  
  void setResolver(SDDocumentResolver paramSDDocumentResolver) { this.sddocResolver = paramSDDocumentResolver; }
  
  public QName getRootName() { return this.rootName; }
  
  public boolean isWSDL() { return false; }
  
  public boolean isSchema() { return false; }
  
  public URL getURL() { return this.url; }
  
  public XMLStreamReader read(XMLInputFactory paramXMLInputFactory) throws IOException, XMLStreamException { return this.source.read(paramXMLInputFactory); }
  
  public XMLStreamReader read() throws IOException, XMLStreamException { return this.source.read(); }
  
  public URL getSystemId() { return this.url; }
  
  public Set<String> getImports() { return this.imports; }
  
  public void writeTo(OutputStream paramOutputStream) throws IOException {
    xMLStreamWriter = null;
    try {
      xMLStreamWriter = XMLStreamWriterFactory.create(paramOutputStream, "UTF-8");
      xMLStreamWriter.writeStartDocument("UTF-8", "1.0");
      (new XMLStreamReaderToXMLStreamWriter()).bridge(this.source.read(), xMLStreamWriter);
      xMLStreamWriter.writeEndDocument();
    } catch (XMLStreamException xMLStreamException) {
      IOException iOException = new IOException(xMLStreamException.getMessage());
      iOException.initCause(xMLStreamException);
      throw iOException;
    } finally {
      try {
        if (xMLStreamWriter != null)
          xMLStreamWriter.close(); 
      } catch (XMLStreamException xMLStreamException) {
        IOException iOException = new IOException(xMLStreamException.getMessage());
        iOException.initCause(xMLStreamException);
        throw iOException;
      } 
    } 
  }
  
  public void writeTo(PortAddressResolver paramPortAddressResolver, DocumentAddressResolver paramDocumentAddressResolver, OutputStream paramOutputStream) throws IOException {
    xMLStreamWriter = null;
    try {
      xMLStreamWriter = XMLStreamWriterFactory.create(paramOutputStream, "UTF-8");
      xMLStreamWriter.writeStartDocument("UTF-8", "1.0");
      writeTo(paramPortAddressResolver, paramDocumentAddressResolver, xMLStreamWriter);
      xMLStreamWriter.writeEndDocument();
    } catch (XMLStreamException xMLStreamException) {
      IOException iOException = new IOException(xMLStreamException.getMessage());
      iOException.initCause(xMLStreamException);
      throw iOException;
    } finally {
      try {
        if (xMLStreamWriter != null)
          xMLStreamWriter.close(); 
      } catch (XMLStreamException xMLStreamException) {
        IOException iOException = new IOException(xMLStreamException.getMessage());
        iOException.initCause(xMLStreamException);
        throw iOException;
      } 
    } 
  }
  
  public void writeTo(PortAddressResolver paramPortAddressResolver, DocumentAddressResolver paramDocumentAddressResolver, XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException, IOException {
    if (this.filters != null)
      for (SDDocumentFilter sDDocumentFilter : this.filters)
        paramXMLStreamWriter = sDDocumentFilter.filter(this, paramXMLStreamWriter);  
    xMLStreamReader = this.source.read();
    try {
      paramXMLStreamWriter.writeComment(VERSION_COMMENT);
      (new WSDLPatcher(paramPortAddressResolver, new DocumentLocationResolverImpl(this, paramDocumentAddressResolver))).bridge(xMLStreamReader, paramXMLStreamWriter);
    } finally {
      xMLStreamReader.close();
    } 
  }
  
  private class DocumentLocationResolverImpl implements DocumentLocationResolver {
    private DocumentAddressResolver delegate;
    
    DocumentLocationResolverImpl(DocumentAddressResolver param1DocumentAddressResolver) { this.delegate = param1DocumentAddressResolver; }
    
    public String getLocationFor(String param1String1, String param1String2) {
      if (SDDocumentImpl.this.sddocResolver == null)
        return param1String2; 
      try {
        URL uRL = new URL(SDDocumentImpl.this.getURL(), param1String2);
        SDDocument sDDocument = SDDocumentImpl.this.sddocResolver.resolve(uRL.toExternalForm());
        return (sDDocument == null) ? param1String2 : this.delegate.getRelativeAddressFor(SDDocumentImpl.this, sDDocument);
      } catch (MalformedURLException malformedURLException) {
        return null;
      } 
    }
  }
  
  private static final class SchemaImpl extends SDDocumentImpl implements SDDocument.Schema {
    private final String targetNamespace;
    
    public SchemaImpl(QName param1QName, URL param1URL, SDDocumentSource param1SDDocumentSource, String param1String, Set<String> param1Set) {
      super(param1QName, param1URL, param1SDDocumentSource, param1Set);
      this.targetNamespace = param1String;
    }
    
    public String getTargetNamespace() { return this.targetNamespace; }
    
    public boolean isSchema() { return true; }
  }
  
  private static final class WSDLImpl extends SDDocumentImpl implements SDDocument.WSDL {
    private final String targetNamespace;
    
    private final boolean hasPortType;
    
    private final boolean hasService;
    
    private final Set<QName> allServices;
    
    public WSDLImpl(QName param1QName, URL param1URL, SDDocumentSource param1SDDocumentSource, String param1String, boolean param1Boolean1, boolean param1Boolean2, Set<String> param1Set1, Set<QName> param1Set2) {
      super(param1QName, param1URL, param1SDDocumentSource, param1Set1);
      this.targetNamespace = param1String;
      this.hasPortType = param1Boolean1;
      this.hasService = param1Boolean2;
      this.allServices = param1Set2;
    }
    
    public String getTargetNamespace() { return this.targetNamespace; }
    
    public boolean hasPortType() { return this.hasPortType; }
    
    public boolean hasService() { return this.hasService; }
    
    public Set<QName> getAllServices() { return this.allServices; }
    
    public boolean isWSDL() { return true; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\SDDocumentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */