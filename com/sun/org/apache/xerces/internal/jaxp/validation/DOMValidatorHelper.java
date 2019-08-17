package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.validation.EntityState;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.impl.xs.util.SimpleLocator;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import java.io.IOException;
import java.util.Enumeration;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import jdk.xml.internal.JdkXmlUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

final class DOMValidatorHelper implements ValidatorHelper, EntityState {
  private static final int CHUNK_SIZE = 1024;
  
  private static final int CHUNK_MASK = 1023;
  
  private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  private static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
  
  private static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
  
  private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  
  private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
  
  private XMLErrorReporter fErrorReporter;
  
  private NamespaceSupport fNamespaceContext;
  
  private DOMNamespaceContext fDOMNamespaceContext = new DOMNamespaceContext();
  
  private XMLSchemaValidator fSchemaValidator;
  
  private SymbolTable fSymbolTable;
  
  private ValidationManager fValidationManager;
  
  private XMLSchemaValidatorComponentManager fComponentManager;
  
  private final SimpleLocator fXMLLocator = new SimpleLocator(null, null, -1, -1, -1);
  
  private DOMDocumentHandler fDOMValidatorHandler;
  
  private final DOMResultAugmentor fDOMResultAugmentor = new DOMResultAugmentor(this);
  
  private final DOMResultBuilder fDOMResultBuilder = new DOMResultBuilder();
  
  private NamedNodeMap fEntities = null;
  
  private char[] fCharBuffer = new char[1024];
  
  private Node fRoot;
  
  private Node fCurrentElement;
  
  final QName fElementQName = new QName();
  
  final QName fAttributeQName = new QName();
  
  final XMLAttributesImpl fAttributes = new XMLAttributesImpl();
  
  final XMLString fTempString = new XMLString();
  
  public DOMValidatorHelper(XMLSchemaValidatorComponentManager paramXMLSchemaValidatorComponentManager) {
    this.fComponentManager = paramXMLSchemaValidatorComponentManager;
    this.fErrorReporter = (XMLErrorReporter)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    this.fNamespaceContext = (NamespaceSupport)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/namespace-context");
    this.fSchemaValidator = (XMLSchemaValidator)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validator/schema");
    this.fSymbolTable = (SymbolTable)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
    this.fValidationManager = (ValidationManager)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager");
  }
  
  public void validate(Source paramSource, Result paramResult) throws SAXException, IOException {
    if (paramResult instanceof DOMResult || paramResult == null) {
      DOMSource dOMSource = (DOMSource)paramSource;
      DOMResult dOMResult = (DOMResult)paramResult;
      Node node = dOMSource.getNode();
      this.fRoot = node;
      if (node != null) {
        this.fComponentManager.reset();
        this.fValidationManager.setEntityState(this);
        this.fDOMNamespaceContext.reset();
        String str = dOMSource.getSystemId();
        this.fXMLLocator.setLiteralSystemId(str);
        this.fXMLLocator.setExpandedSystemId(str);
        this.fErrorReporter.setDocumentLocator(this.fXMLLocator);
        try {
          setupEntityMap((node.getNodeType() == 9) ? (Document)node : node.getOwnerDocument());
          setupDOMResultHandler(dOMSource, dOMResult);
          this.fSchemaValidator.startDocument(this.fXMLLocator, null, this.fDOMNamespaceContext, null);
          validate(node);
          this.fSchemaValidator.endDocument(null);
        } catch (XMLParseException xMLParseException) {
          throw Util.toSAXParseException(xMLParseException);
        } catch (XNIException xNIException) {
          throw Util.toSAXException(xNIException);
        } finally {
          this.fRoot = null;
          this.fEntities = null;
          if (this.fDOMValidatorHandler != null)
            this.fDOMValidatorHandler.setDOMResult(null); 
        } 
      } 
      return;
    } 
    throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceResultMismatch", new Object[] { paramSource.getClass().getName(), paramResult.getClass().getName() }));
  }
  
  public boolean isEntityDeclared(String paramString) { return false; }
  
  public boolean isEntityUnparsed(String paramString) {
    if (this.fEntities != null) {
      Entity entity = (Entity)this.fEntities.getNamedItem(paramString);
      if (entity != null)
        return (entity.getNotationName() != null); 
    } 
    return false;
  }
  
  private void validate(Node paramNode) {
    Node node = paramNode;
    while (paramNode != null) {
      beginNode(paramNode);
      Node node1 = paramNode.getFirstChild();
      while (node1 == null) {
        finishNode(paramNode);
        if (node == paramNode)
          break; 
        node1 = paramNode.getNextSibling();
        if (node1 == null) {
          paramNode = paramNode.getParentNode();
          if (paramNode == null || node == paramNode) {
            if (paramNode != null)
              finishNode(paramNode); 
            node1 = null;
            break;
          } 
        } 
      } 
      paramNode = node1;
    } 
  }
  
  private void beginNode(Node paramNode) {
    switch (paramNode.getNodeType()) {
      case 1:
        this.fCurrentElement = paramNode;
        this.fNamespaceContext.pushContext();
        fillQName(this.fElementQName, paramNode);
        processAttributes(paramNode.getAttributes());
        this.fSchemaValidator.startElement(this.fElementQName, this.fAttributes, null);
        break;
      case 3:
        if (this.fDOMValidatorHandler != null) {
          this.fDOMValidatorHandler.setIgnoringCharacters(true);
          sendCharactersToValidator(paramNode.getNodeValue());
          this.fDOMValidatorHandler.setIgnoringCharacters(false);
          this.fDOMValidatorHandler.characters((Text)paramNode);
          break;
        } 
        sendCharactersToValidator(paramNode.getNodeValue());
        break;
      case 4:
        if (this.fDOMValidatorHandler != null) {
          this.fDOMValidatorHandler.setIgnoringCharacters(true);
          this.fSchemaValidator.startCDATA(null);
          sendCharactersToValidator(paramNode.getNodeValue());
          this.fSchemaValidator.endCDATA(null);
          this.fDOMValidatorHandler.setIgnoringCharacters(false);
          this.fDOMValidatorHandler.cdata((CDATASection)paramNode);
          break;
        } 
        this.fSchemaValidator.startCDATA(null);
        sendCharactersToValidator(paramNode.getNodeValue());
        this.fSchemaValidator.endCDATA(null);
        break;
      case 7:
        if (this.fDOMValidatorHandler != null)
          this.fDOMValidatorHandler.processingInstruction((ProcessingInstruction)paramNode); 
        break;
      case 8:
        if (this.fDOMValidatorHandler != null)
          this.fDOMValidatorHandler.comment((Comment)paramNode); 
        break;
      case 10:
        if (this.fDOMValidatorHandler != null)
          this.fDOMValidatorHandler.doctypeDecl((DocumentType)paramNode); 
        break;
    } 
  }
  
  private void finishNode(Node paramNode) {
    if (paramNode.getNodeType() == 1) {
      this.fCurrentElement = paramNode;
      fillQName(this.fElementQName, paramNode);
      this.fSchemaValidator.endElement(this.fElementQName, null);
      this.fNamespaceContext.popContext();
    } 
  }
  
  private void setupEntityMap(Document paramDocument) {
    if (paramDocument != null) {
      DocumentType documentType = paramDocument.getDoctype();
      if (documentType != null) {
        this.fEntities = documentType.getEntities();
        return;
      } 
    } 
    this.fEntities = null;
  }
  
  private void setupDOMResultHandler(DOMSource paramDOMSource, DOMResult paramDOMResult) throws SAXException {
    if (paramDOMResult == null) {
      this.fDOMValidatorHandler = null;
      this.fSchemaValidator.setDocumentHandler(null);
      return;
    } 
    Node node = paramDOMResult.getNode();
    if (paramDOMSource.getNode() == node) {
      this.fDOMValidatorHandler = this.fDOMResultAugmentor;
      this.fDOMResultAugmentor.setDOMResult(paramDOMResult);
      this.fSchemaValidator.setDocumentHandler(this.fDOMResultAugmentor);
      return;
    } 
    if (paramDOMResult.getNode() == null)
      try {
        DocumentBuilderFactory documentBuilderFactory = JdkXmlUtils.getDOMFactory(this.fComponentManager.getFeature("jdk.xml.overrideDefaultParser"));
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        paramDOMResult.setNode(documentBuilder.newDocument());
      } catch (ParserConfigurationException parserConfigurationException) {
        throw new SAXException(parserConfigurationException);
      }  
    this.fDOMValidatorHandler = this.fDOMResultBuilder;
    this.fDOMResultBuilder.setDOMResult(paramDOMResult);
    this.fSchemaValidator.setDocumentHandler(this.fDOMResultBuilder);
  }
  
  private void fillQName(QName paramQName, Node paramNode) {
    String str1 = paramNode.getPrefix();
    String str2 = paramNode.getLocalName();
    String str3 = paramNode.getNodeName();
    String str4 = paramNode.getNamespaceURI();
    paramQName.uri = (str4 != null && str4.length() > 0) ? this.fSymbolTable.addSymbol(str4) : null;
    paramQName.rawname = (str3 != null) ? this.fSymbolTable.addSymbol(str3) : XMLSymbols.EMPTY_STRING;
    if (str2 == null) {
      int i = str3.indexOf(':');
      if (i > 0) {
        paramQName.prefix = this.fSymbolTable.addSymbol(str3.substring(0, i));
        paramQName.localpart = this.fSymbolTable.addSymbol(str3.substring(i + 1));
      } else {
        paramQName.prefix = XMLSymbols.EMPTY_STRING;
        paramQName.localpart = paramQName.rawname;
      } 
    } else {
      paramQName.prefix = (str1 != null) ? this.fSymbolTable.addSymbol(str1) : XMLSymbols.EMPTY_STRING;
      paramQName.localpart = (str2 != null) ? this.fSymbolTable.addSymbol(str2) : XMLSymbols.EMPTY_STRING;
    } 
  }
  
  private void processAttributes(NamedNodeMap paramNamedNodeMap) {
    int i = paramNamedNodeMap.getLength();
    this.fAttributes.removeAllAttributes();
    for (byte b = 0; b < i; b++) {
      Attr attr = (Attr)paramNamedNodeMap.item(b);
      String str = attr.getValue();
      if (str == null)
        str = XMLSymbols.EMPTY_STRING; 
      fillQName(this.fAttributeQName, attr);
      this.fAttributes.addAttributeNS(this.fAttributeQName, XMLSymbols.fCDATASymbol, str);
      this.fAttributes.setSpecified(b, attr.getSpecified());
      if (this.fAttributeQName.uri == NamespaceContext.XMLNS_URI)
        if (this.fAttributeQName.prefix == XMLSymbols.PREFIX_XMLNS) {
          this.fNamespaceContext.declarePrefix(this.fAttributeQName.localpart, (str.length() != 0) ? this.fSymbolTable.addSymbol(str) : null);
        } else {
          this.fNamespaceContext.declarePrefix(XMLSymbols.EMPTY_STRING, (str.length() != 0) ? this.fSymbolTable.addSymbol(str) : null);
        }  
    } 
  }
  
  private void sendCharactersToValidator(String paramString) { // Byte code:
    //   0: aload_1
    //   1: ifnull -> 113
    //   4: aload_1
    //   5: invokevirtual length : ()I
    //   8: istore_2
    //   9: iload_2
    //   10: sipush #1023
    //   13: iand
    //   14: istore_3
    //   15: iload_3
    //   16: ifle -> 55
    //   19: aload_1
    //   20: iconst_0
    //   21: iload_3
    //   22: aload_0
    //   23: getfield fCharBuffer : [C
    //   26: iconst_0
    //   27: invokevirtual getChars : (II[CI)V
    //   30: aload_0
    //   31: getfield fTempString : Lcom/sun/org/apache/xerces/internal/xni/XMLString;
    //   34: aload_0
    //   35: getfield fCharBuffer : [C
    //   38: iconst_0
    //   39: iload_3
    //   40: invokevirtual setValues : ([CII)V
    //   43: aload_0
    //   44: getfield fSchemaValidator : Lcom/sun/org/apache/xerces/internal/impl/xs/XMLSchemaValidator;
    //   47: aload_0
    //   48: getfield fTempString : Lcom/sun/org/apache/xerces/internal/xni/XMLString;
    //   51: aconst_null
    //   52: invokevirtual characters : (Lcom/sun/org/apache/xerces/internal/xni/XMLString;Lcom/sun/org/apache/xerces/internal/xni/Augmentations;)V
    //   55: iload_3
    //   56: istore #4
    //   58: iload #4
    //   60: iload_2
    //   61: if_icmpge -> 113
    //   64: aload_1
    //   65: iload #4
    //   67: wide iinc #4 1024
    //   73: iload #4
    //   75: aload_0
    //   76: getfield fCharBuffer : [C
    //   79: iconst_0
    //   80: invokevirtual getChars : (II[CI)V
    //   83: aload_0
    //   84: getfield fTempString : Lcom/sun/org/apache/xerces/internal/xni/XMLString;
    //   87: aload_0
    //   88: getfield fCharBuffer : [C
    //   91: iconst_0
    //   92: sipush #1024
    //   95: invokevirtual setValues : ([CII)V
    //   98: aload_0
    //   99: getfield fSchemaValidator : Lcom/sun/org/apache/xerces/internal/impl/xs/XMLSchemaValidator;
    //   102: aload_0
    //   103: getfield fTempString : Lcom/sun/org/apache/xerces/internal/xni/XMLString;
    //   106: aconst_null
    //   107: invokevirtual characters : (Lcom/sun/org/apache/xerces/internal/xni/XMLString;Lcom/sun/org/apache/xerces/internal/xni/Augmentations;)V
    //   110: goto -> 58
    //   113: return }
  
  Node getCurrentElement() { return this.fCurrentElement; }
  
  final class DOMNamespaceContext implements NamespaceContext {
    protected String[] fNamespace = new String[32];
    
    protected int fNamespaceSize = 0;
    
    protected boolean fDOMContextBuilt = false;
    
    public void pushContext() { DOMValidatorHelper.this.fNamespaceContext.pushContext(); }
    
    public void popContext() { DOMValidatorHelper.this.fNamespaceContext.popContext(); }
    
    public boolean declarePrefix(String param1String1, String param1String2) { return DOMValidatorHelper.this.fNamespaceContext.declarePrefix(param1String1, param1String2); }
    
    public String getURI(String param1String) {
      String str = DOMValidatorHelper.this.fNamespaceContext.getURI(param1String);
      if (str == null) {
        if (!this.fDOMContextBuilt) {
          fillNamespaceContext();
          this.fDOMContextBuilt = true;
        } 
        if (this.fNamespaceSize > 0 && !DOMValidatorHelper.this.fNamespaceContext.containsPrefix(param1String))
          str = getURI0(param1String); 
      } 
      return str;
    }
    
    public String getPrefix(String param1String) { return DOMValidatorHelper.this.fNamespaceContext.getPrefix(param1String); }
    
    public int getDeclaredPrefixCount() { return DOMValidatorHelper.this.fNamespaceContext.getDeclaredPrefixCount(); }
    
    public String getDeclaredPrefixAt(int param1Int) { return DOMValidatorHelper.this.fNamespaceContext.getDeclaredPrefixAt(param1Int); }
    
    public Enumeration getAllPrefixes() { return DOMValidatorHelper.this.fNamespaceContext.getAllPrefixes(); }
    
    public void reset() {
      this.fDOMContextBuilt = false;
      this.fNamespaceSize = 0;
    }
    
    private void fillNamespaceContext() {
      if (DOMValidatorHelper.this.fRoot != null)
        for (Node node = DOMValidatorHelper.this.fRoot.getParentNode(); node != null; node = node.getParentNode()) {
          if (1 == node.getNodeType()) {
            NamedNodeMap namedNodeMap = node.getAttributes();
            int i = namedNodeMap.getLength();
            for (byte b = 0; b < i; b++) {
              Attr attr = (Attr)namedNodeMap.item(b);
              String str = attr.getValue();
              if (str == null)
                str = XMLSymbols.EMPTY_STRING; 
              DOMValidatorHelper.this.fillQName(DOMValidatorHelper.this.fAttributeQName, attr);
              if (this.this$0.fAttributeQName.uri == NamespaceContext.XMLNS_URI)
                if (this.this$0.fAttributeQName.prefix == XMLSymbols.PREFIX_XMLNS) {
                  declarePrefix0(this.this$0.fAttributeQName.localpart, (str.length() != 0) ? DOMValidatorHelper.this.fSymbolTable.addSymbol(str) : null);
                } else {
                  declarePrefix0(XMLSymbols.EMPTY_STRING, (str.length() != 0) ? DOMValidatorHelper.this.fSymbolTable.addSymbol(str) : null);
                }  
            } 
          } 
        }  
    }
    
    private void declarePrefix0(String param1String1, String param1String2) {
      if (this.fNamespaceSize == this.fNamespace.length) {
        String[] arrayOfString = new String[this.fNamespaceSize * 2];
        System.arraycopy(this.fNamespace, 0, arrayOfString, 0, this.fNamespaceSize);
        this.fNamespace = arrayOfString;
      } 
      this.fNamespace[this.fNamespaceSize++] = param1String1;
      this.fNamespace[this.fNamespaceSize++] = param1String2;
    }
    
    private String getURI0(String param1String) {
      for (boolean bool = false; bool < this.fNamespaceSize; bool += true) {
        if (this.fNamespace[bool] == param1String)
          return this.fNamespace[bool + true]; 
      } 
      return null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\DOMValidatorHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */