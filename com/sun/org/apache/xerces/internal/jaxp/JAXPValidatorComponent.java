package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.dom.DOMInputImpl;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.xs.opti.DefaultXMLDocumentHandler;
import com.sun.org.apache.xerces.internal.util.AttributesProxy;
import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerProxy;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.LocatorProxy;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import javax.xml.validation.TypeInfoProvider;
import javax.xml.validation.ValidatorHandler;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

final class JAXPValidatorComponent extends TeeXMLDocumentFilterImpl implements XMLComponent {
  private static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
  
  private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  
  private final ValidatorHandler validator;
  
  private final XNI2SAX xni2sax = new XNI2SAX(null);
  
  private final SAX2XNI sax2xni = new SAX2XNI(null);
  
  private final TypeInfoProvider typeInfoProvider;
  
  private Augmentations fCurrentAug;
  
  private XMLAttributes fCurrentAttributes;
  
  private SymbolTable fSymbolTable;
  
  private XMLErrorReporter fErrorReporter;
  
  private XMLEntityResolver fEntityResolver;
  
  private static final TypeInfoProvider noInfoProvider = new TypeInfoProvider() {
      public TypeInfo getElementTypeInfo() { return null; }
      
      public TypeInfo getAttributeTypeInfo(int param1Int) { return null; }
      
      public TypeInfo getAttributeTypeInfo(String param1String) { return null; }
      
      public TypeInfo getAttributeTypeInfo(String param1String1, String param1String2) { return null; }
      
      public boolean isIdAttribute(int param1Int) { return false; }
      
      public boolean isSpecified(int param1Int) { return false; }
    };
  
  public JAXPValidatorComponent(ValidatorHandler paramValidatorHandler) {
    this.validator = paramValidatorHandler;
    TypeInfoProvider typeInfoProvider1 = paramValidatorHandler.getTypeInfoProvider();
    if (typeInfoProvider1 == null)
      typeInfoProvider1 = noInfoProvider; 
    this.typeInfoProvider = typeInfoProvider1;
    this.xni2sax.setContentHandler(this.validator);
    this.validator.setContentHandler(this.sax2xni);
    setSide(this.xni2sax);
    this.validator.setErrorHandler(new ErrorHandlerProxy() {
          protected XMLErrorHandler getErrorHandler() {
            XMLErrorHandler xMLErrorHandler = JAXPValidatorComponent.this.fErrorReporter.getErrorHandler();
            return (xMLErrorHandler != null) ? xMLErrorHandler : new ErrorHandlerWrapper(JAXPValidatorComponent.DraconianErrorHandler.getInstance());
          }
        });
    this.validator.setResourceResolver(new LSResourceResolver() {
          public LSInput resolveResource(String param1String1, String param1String2, String param1String3, String param1String4, String param1String5) {
            if (JAXPValidatorComponent.this.fEntityResolver == null)
              return null; 
            try {
              XMLInputSource xMLInputSource = JAXPValidatorComponent.this.fEntityResolver.resolveEntity(new XMLResourceIdentifierImpl(param1String3, param1String4, param1String5, null));
              if (xMLInputSource == null)
                return null; 
              DOMInputImpl dOMInputImpl = new DOMInputImpl();
              dOMInputImpl.setBaseURI(xMLInputSource.getBaseSystemId());
              dOMInputImpl.setByteStream(xMLInputSource.getByteStream());
              dOMInputImpl.setCharacterStream(xMLInputSource.getCharacterStream());
              dOMInputImpl.setEncoding(xMLInputSource.getEncoding());
              dOMInputImpl.setPublicId(xMLInputSource.getPublicId());
              dOMInputImpl.setSystemId(xMLInputSource.getSystemId());
              return dOMInputImpl;
            } catch (IOException iOException) {
              throw new XNIException(iOException);
            } 
          }
        });
  }
  
  public void startElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {
    this.fCurrentAttributes = paramXMLAttributes;
    this.fCurrentAug = paramAugmentations;
    this.xni2sax.startElement(paramQName, paramXMLAttributes, null);
    this.fCurrentAttributes = null;
  }
  
  public void endElement(QName paramQName, Augmentations paramAugmentations) throws XNIException {
    this.fCurrentAug = paramAugmentations;
    this.xni2sax.endElement(paramQName, null);
  }
  
  public void emptyElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {
    startElement(paramQName, paramXMLAttributes, paramAugmentations);
    endElement(paramQName, paramAugmentations);
  }
  
  public void characters(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    this.fCurrentAug = paramAugmentations;
    this.xni2sax.characters(paramXMLString, null);
  }
  
  public void ignorableWhitespace(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    this.fCurrentAug = paramAugmentations;
    this.xni2sax.ignorableWhitespace(paramXMLString, null);
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager) throws XMLConfigurationException {
    this.fSymbolTable = (SymbolTable)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
    this.fErrorReporter = (XMLErrorReporter)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    try {
      this.fEntityResolver = (XMLEntityResolver)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
    } catch (XMLConfigurationException xMLConfigurationException) {
      this.fEntityResolver = null;
    } 
  }
  
  private void updateAttributes(Attributes paramAttributes) {
    int i = paramAttributes.getLength();
    for (byte b = 0; b < i; b++) {
      String str1 = paramAttributes.getQName(b);
      int j = this.fCurrentAttributes.getIndex(str1);
      String str2 = paramAttributes.getValue(b);
      if (j == -1) {
        String str;
        int k = str1.indexOf(':');
        if (k < 0) {
          str = null;
        } else {
          str = symbolize(str1.substring(0, k));
        } 
        j = this.fCurrentAttributes.addAttribute(new QName(str, symbolize(paramAttributes.getLocalName(b)), symbolize(str1), symbolize(paramAttributes.getURI(b))), paramAttributes.getType(b), str2);
      } else if (!str2.equals(this.fCurrentAttributes.getValue(j))) {
        this.fCurrentAttributes.setValue(j, str2);
      } 
    } 
  }
  
  private String symbolize(String paramString) { return this.fSymbolTable.addSymbol(paramString); }
  
  public String[] getRecognizedFeatures() { return null; }
  
  public void setFeature(String paramString, boolean paramBoolean) throws XMLConfigurationException {}
  
  public String[] getRecognizedProperties() { return new String[] { "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/symbol-table" }; }
  
  public void setProperty(String paramString, Object paramObject) throws XMLConfigurationException {}
  
  public Boolean getFeatureDefault(String paramString) { return null; }
  
  public Object getPropertyDefault(String paramString) { return null; }
  
  private static final class DraconianErrorHandler implements ErrorHandler {
    private static final DraconianErrorHandler ERROR_HANDLER_INSTANCE = new DraconianErrorHandler();
    
    public static DraconianErrorHandler getInstance() { return ERROR_HANDLER_INSTANCE; }
    
    public void warning(SAXParseException param1SAXParseException) throws SAXException {}
    
    public void error(SAXParseException param1SAXParseException) throws SAXException { throw param1SAXParseException; }
    
    public void fatalError(SAXParseException param1SAXParseException) throws SAXException { throw param1SAXParseException; }
  }
  
  private final class SAX2XNI extends DefaultHandler {
    private final Augmentations fAugmentations = new AugmentationsImpl();
    
    private final QName fQName = new QName();
    
    private SAX2XNI() {}
    
    public void characters(char[] param1ArrayOfChar, int param1Int1, int param1Int2) throws SAXException {
      try {
        handler().characters(new XMLString(param1ArrayOfChar, param1Int1, param1Int2), aug());
      } catch (XNIException xNIException) {
        throw toSAXException(xNIException);
      } 
    }
    
    public void ignorableWhitespace(char[] param1ArrayOfChar, int param1Int1, int param1Int2) throws SAXException {
      try {
        handler().ignorableWhitespace(new XMLString(param1ArrayOfChar, param1Int1, param1Int2), aug());
      } catch (XNIException xNIException) {
        throw toSAXException(xNIException);
      } 
    }
    
    public void startElement(String param1String1, String param1String2, String param1String3, Attributes param1Attributes) throws SAXException {
      try {
        JAXPValidatorComponent.this.updateAttributes(param1Attributes);
        handler().startElement(toQName(param1String1, param1String2, param1String3), JAXPValidatorComponent.this.fCurrentAttributes, elementAug());
      } catch (XNIException xNIException) {
        throw toSAXException(xNIException);
      } 
    }
    
    public void endElement(String param1String1, String param1String2, String param1String3) throws SAXException {
      try {
        handler().endElement(toQName(param1String1, param1String2, param1String3), aug());
      } catch (XNIException xNIException) {
        throw toSAXException(xNIException);
      } 
    }
    
    private Augmentations elementAug() { return aug(); }
    
    private Augmentations aug() {
      if (JAXPValidatorComponent.this.fCurrentAug != null) {
        Augmentations augmentations = JAXPValidatorComponent.this.fCurrentAug;
        JAXPValidatorComponent.this.fCurrentAug = null;
        return augmentations;
      } 
      this.fAugmentations.removeAllItems();
      return this.fAugmentations;
    }
    
    private XMLDocumentHandler handler() { return JAXPValidatorComponent.this.getDocumentHandler(); }
    
    private SAXException toSAXException(XNIException param1XNIException) {
      Exception exception = param1XNIException.getException();
      if (exception == null)
        exception = param1XNIException; 
      return (exception instanceof SAXException) ? (SAXException)exception : new SAXException(exception);
    }
    
    private QName toQName(String param1String1, String param1String2, String param1String3) {
      String str = null;
      int i = param1String3.indexOf(':');
      if (i > 0)
        str = JAXPValidatorComponent.this.symbolize(param1String3.substring(0, i)); 
      param1String2 = JAXPValidatorComponent.this.symbolize(param1String2);
      param1String3 = JAXPValidatorComponent.this.symbolize(param1String3);
      param1String1 = JAXPValidatorComponent.this.symbolize(param1String1);
      this.fQName.setValues(str, param1String2, param1String3, param1String1);
      return this.fQName;
    }
  }
  
  private final class XNI2SAX extends DefaultXMLDocumentHandler {
    private ContentHandler fContentHandler;
    
    private String fVersion;
    
    protected NamespaceContext fNamespaceContext;
    
    private final AttributesProxy fAttributesProxy = new AttributesProxy(null);
    
    private XNI2SAX() {}
    
    public void setContentHandler(ContentHandler param1ContentHandler) { this.fContentHandler = param1ContentHandler; }
    
    public ContentHandler getContentHandler() { return this.fContentHandler; }
    
    public void xmlDecl(String param1String1, String param1String2, String param1String3, Augmentations param1Augmentations) throws XNIException { this.fVersion = param1String1; }
    
    public void startDocument(XMLLocator param1XMLLocator, String param1String, NamespaceContext param1NamespaceContext, Augmentations param1Augmentations) throws XNIException {
      this.fNamespaceContext = param1NamespaceContext;
      this.fContentHandler.setDocumentLocator(new LocatorProxy(param1XMLLocator));
      try {
        this.fContentHandler.startDocument();
      } catch (SAXException sAXException) {
        throw new XNIException(sAXException);
      } 
    }
    
    public void endDocument(Augmentations param1Augmentations) throws XNIException {
      try {
        this.fContentHandler.endDocument();
      } catch (SAXException sAXException) {
        throw new XNIException(sAXException);
      } 
    }
    
    public void processingInstruction(String param1String, XMLString param1XMLString, Augmentations param1Augmentations) throws XNIException {
      try {
        this.fContentHandler.processingInstruction(param1String, param1XMLString.toString());
      } catch (SAXException sAXException) {
        throw new XNIException(sAXException);
      } 
    }
    
    public void startElement(QName param1QName, XMLAttributes param1XMLAttributes, Augmentations param1Augmentations) throws XNIException {
      try {
        int i = this.fNamespaceContext.getDeclaredPrefixCount();
        if (i > 0) {
          String str3 = null;
          String str4 = null;
          for (byte b = 0; b < i; b++) {
            str3 = this.fNamespaceContext.getDeclaredPrefixAt(b);
            str4 = this.fNamespaceContext.getURI(str3);
            this.fContentHandler.startPrefixMapping(str3, (str4 == null) ? "" : str4);
          } 
        } 
        String str1 = (param1QName.uri != null) ? param1QName.uri : "";
        String str2 = param1QName.localpart;
        this.fAttributesProxy.setAttributes(param1XMLAttributes);
        this.fContentHandler.startElement(str1, str2, param1QName.rawname, this.fAttributesProxy);
      } catch (SAXException sAXException) {
        throw new XNIException(sAXException);
      } 
    }
    
    public void endElement(QName param1QName, Augmentations param1Augmentations) throws XNIException {
      try {
        String str1 = (param1QName.uri != null) ? param1QName.uri : "";
        String str2 = param1QName.localpart;
        this.fContentHandler.endElement(str1, str2, param1QName.rawname);
        int i = this.fNamespaceContext.getDeclaredPrefixCount();
        if (i > 0)
          for (byte b = 0; b < i; b++)
            this.fContentHandler.endPrefixMapping(this.fNamespaceContext.getDeclaredPrefixAt(b));  
      } catch (SAXException sAXException) {
        throw new XNIException(sAXException);
      } 
    }
    
    public void emptyElement(QName param1QName, XMLAttributes param1XMLAttributes, Augmentations param1Augmentations) throws XNIException {
      startElement(param1QName, param1XMLAttributes, param1Augmentations);
      endElement(param1QName, param1Augmentations);
    }
    
    public void characters(XMLString param1XMLString, Augmentations param1Augmentations) throws XNIException {
      try {
        this.fContentHandler.characters(param1XMLString.ch, param1XMLString.offset, param1XMLString.length);
      } catch (SAXException sAXException) {
        throw new XNIException(sAXException);
      } 
    }
    
    public void ignorableWhitespace(XMLString param1XMLString, Augmentations param1Augmentations) throws XNIException {
      try {
        this.fContentHandler.ignorableWhitespace(param1XMLString.ch, param1XMLString.offset, param1XMLString.length);
      } catch (SAXException sAXException) {
        throw new XNIException(sAXException);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\JAXPValidatorComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */