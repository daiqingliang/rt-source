package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.RevalidationHandler;
import com.sun.org.apache.xerces.internal.impl.dtd.DTDGrammar;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDDescription;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidator;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.xs.util.SimpleLocator;
import com.sun.org.apache.xerces.internal.parsers.XMLGrammarPreparser;
import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Vector;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class DOMNormalizer implements XMLDocumentHandler {
  protected static final boolean DEBUG_ND = false;
  
  protected static final boolean DEBUG = false;
  
  protected static final boolean DEBUG_EVENTS = false;
  
  protected static final String PREFIX = "NS";
  
  protected DOMConfigurationImpl fConfiguration = null;
  
  protected CoreDocumentImpl fDocument = null;
  
  protected final XMLAttributesProxy fAttrProxy = new XMLAttributesProxy();
  
  protected final QName fQName = new QName();
  
  protected RevalidationHandler fValidationHandler;
  
  protected SymbolTable fSymbolTable;
  
  protected DOMErrorHandler fErrorHandler;
  
  private final DOMErrorImpl fError = new DOMErrorImpl();
  
  protected boolean fNamespaceValidation = false;
  
  protected boolean fPSVI = false;
  
  protected final NamespaceContext fNamespaceContext = new NamespaceSupport();
  
  protected final NamespaceContext fLocalNSBinder = new NamespaceSupport();
  
  protected final ArrayList fAttributeList = new ArrayList(5);
  
  protected final DOMLocatorImpl fLocator = new DOMLocatorImpl();
  
  protected Node fCurrentNode = null;
  
  private QName fAttrQName = new QName();
  
  final XMLString fNormalizedValue = new XMLString(new char[16], 0, 0);
  
  private XMLDTDValidator fDTDValidator;
  
  private boolean allWhitespace = false;
  
  protected void normalizeDocument(CoreDocumentImpl paramCoreDocumentImpl, DOMConfigurationImpl paramDOMConfigurationImpl) {
    this.fDocument = paramCoreDocumentImpl;
    this.fConfiguration = paramDOMConfigurationImpl;
    this.fSymbolTable = (SymbolTable)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/symbol-table");
    this.fNamespaceContext.reset();
    this.fNamespaceContext.declarePrefix(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING);
    if ((this.fConfiguration.features & 0x40) != 0) {
      String str = (String)this.fConfiguration.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
      if (str != null && str.equals(Constants.NS_XMLSCHEMA)) {
        this.fValidationHandler = CoreDOMImplementationImpl.singleton.getValidator("http://www.w3.org/2001/XMLSchema");
        this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema", true);
        this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
        this.fNamespaceValidation = true;
        this.fPSVI = ((this.fConfiguration.features & 0x80) != 0);
      } 
      this.fConfiguration.setFeature("http://xml.org/sax/features/validation", true);
      this.fDocument.clearIdentifiers();
      if (this.fValidationHandler != null)
        ((XMLComponent)this.fValidationHandler).reset(this.fConfiguration); 
    } 
    this.fErrorHandler = (DOMErrorHandler)this.fConfiguration.getParameter("error-handler");
    if (this.fValidationHandler != null) {
      this.fValidationHandler.setDocumentHandler(this);
      this.fValidationHandler.startDocument(new SimpleLocator(this.fDocument.fDocumentURI, this.fDocument.fDocumentURI, -1, -1), this.fDocument.encoding, this.fNamespaceContext, null);
    } 
    try {
      for (Node node = this.fDocument.getFirstChild(); node != null; node = node1) {
        Node node1 = node.getNextSibling();
        node = normalizeNode(node);
        if (node != null)
          node1 = node; 
      } 
      if (this.fValidationHandler != null) {
        this.fValidationHandler.endDocument(null);
        CoreDOMImplementationImpl.singleton.releaseValidator("http://www.w3.org/2001/XMLSchema", this.fValidationHandler);
        this.fValidationHandler = null;
      } 
    } catch (AbortException abortException) {
      return;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } 
  }
  
  protected Node normalizeNode(Node paramNode) {
    Node node2;
    short s2;
    AttributeMap attributeMap;
    String str;
    Node node1;
    DocumentTypeImpl documentTypeImpl;
    ElementImpl elementImpl;
    short s1 = paramNode.getNodeType();
    this.fLocator.fRelatedNode = paramNode;
    switch (s1) {
      case 10:
        documentTypeImpl = (DocumentTypeImpl)paramNode;
        this.fDTDValidator = (XMLDTDValidator)CoreDOMImplementationImpl.singleton.getValidator("http://www.w3.org/TR/REC-xml");
        this.fDTDValidator.setDocumentHandler(this);
        this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/grammar-pool", createGrammarPool(documentTypeImpl));
        this.fDTDValidator.reset(this.fConfiguration);
        this.fDTDValidator.startDocument(new SimpleLocator(this.fDocument.fDocumentURI, this.fDocument.fDocumentURI, -1, -1), this.fDocument.encoding, this.fNamespaceContext, null);
        this.fDTDValidator.doctypeDecl(documentTypeImpl.getName(), documentTypeImpl.getPublicId(), documentTypeImpl.getSystemId(), null);
        break;
      case 1:
        if (this.fDocument.errorChecking && (this.fConfiguration.features & 0x100) != 0 && this.fDocument.isXMLVersionChanged()) {
          boolean bool;
          if (this.fNamespaceValidation) {
            bool = CoreDocumentImpl.isValidQName(paramNode.getPrefix(), paramNode.getLocalName(), this.fDocument.isXML11Version());
          } else {
            bool = CoreDocumentImpl.isXMLName(paramNode.getNodeName(), this.fDocument.isXML11Version());
          } 
          if (!bool) {
            String str1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[] { "Element", paramNode.getNodeName() });
            reportDOMError(this.fErrorHandler, this.fError, this.fLocator, str1, (short)2, "wf-invalid-character-in-node-name");
          } 
        } 
        this.fNamespaceContext.pushContext();
        this.fLocalNSBinder.reset();
        elementImpl = (ElementImpl)paramNode;
        if (elementImpl.needsSyncChildren())
          elementImpl.synchronizeChildren(); 
        attributeMap = elementImpl.hasAttributes() ? (AttributeMap)elementImpl.getAttributes() : null;
        if ((this.fConfiguration.features & true) != 0) {
          namespaceFixUp(elementImpl, attributeMap);
          if ((this.fConfiguration.features & 0x200) == 0 && attributeMap != null)
            for (byte b = 0; b < attributeMap.getLength(); b++) {
              Attr attr = (Attr)attributeMap.getItem(b);
              if (XMLSymbols.PREFIX_XMLNS.equals(attr.getPrefix()) || XMLSymbols.PREFIX_XMLNS.equals(attr.getName())) {
                elementImpl.removeAttributeNode(attr);
                b--;
              } 
            }  
        } else if (attributeMap != null) {
          for (byte b = 0; b < attributeMap.getLength(); b++) {
            Attr attr = (Attr)attributeMap.item(b);
            attr.normalize();
            if (this.fDocument.errorChecking && (this.fConfiguration.features & 0x100) != 0) {
              isAttrValueWF(this.fErrorHandler, this.fError, this.fLocator, attributeMap, (AttrImpl)attr, attr.getValue(), this.fDocument.isXML11Version());
              if (this.fDocument.isXMLVersionChanged()) {
                boolean bool = CoreDocumentImpl.isXMLName(paramNode.getNodeName(), this.fDocument.isXML11Version());
                if (!bool) {
                  String str1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[] { "Attr", paramNode.getNodeName() });
                  reportDOMError(this.fErrorHandler, this.fError, this.fLocator, str1, (short)2, "wf-invalid-character-in-node-name");
                } 
              } 
            } 
          } 
        } 
        if (this.fValidationHandler != null) {
          this.fAttrProxy.setAttributes(attributeMap, this.fDocument, elementImpl);
          updateQName(elementImpl, this.fQName);
          this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = paramNode;
          this.fCurrentNode = paramNode;
          this.fValidationHandler.startElement(this.fQName, this.fAttrProxy, null);
        } 
        if (this.fDTDValidator != null) {
          this.fAttrProxy.setAttributes(attributeMap, this.fDocument, elementImpl);
          updateQName(elementImpl, this.fQName);
          this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = paramNode;
          this.fCurrentNode = paramNode;
          this.fDTDValidator.startElement(this.fQName, this.fAttrProxy, null);
        } 
        for (node2 = elementImpl.getFirstChild(); node2 != null; node2 = node) {
          Node node = node2.getNextSibling();
          node2 = normalizeNode(node2);
          if (node2 != null)
            node = node2; 
        } 
        if (this.fValidationHandler != null) {
          updateQName(elementImpl, this.fQName);
          this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = paramNode;
          this.fCurrentNode = paramNode;
          this.fValidationHandler.endElement(this.fQName, null);
        } 
        if (this.fDTDValidator != null) {
          updateQName(elementImpl, this.fQName);
          this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = paramNode;
          this.fCurrentNode = paramNode;
          this.fDTDValidator.endElement(this.fQName, null);
        } 
        this.fNamespaceContext.popContext();
        break;
      case 8:
        if ((this.fConfiguration.features & 0x20) == 0) {
          Node node3 = paramNode.getPreviousSibling();
          Node node4 = paramNode.getParentNode();
          node4.removeChild(paramNode);
          if (node3 != null && node3.getNodeType() == 3) {
            node2 = node3.getNextSibling();
            if (node2 != null && node2.getNodeType() == 3) {
              ((TextImpl)node2).insertData(0, node3.getNodeValue());
              node4.removeChild(node3);
              return node2;
            } 
          } 
          break;
        } 
        if (this.fDocument.errorChecking && (this.fConfiguration.features & 0x100) != 0) {
          String str1 = ((Comment)paramNode).getData();
          isCommentWF(this.fErrorHandler, this.fError, this.fLocator, str1, this.fDocument.isXML11Version());
        } 
        break;
      case 5:
        if ((this.fConfiguration.features & 0x4) == 0) {
          Node node3 = paramNode.getPreviousSibling();
          Node node4 = paramNode.getParentNode();
          ((EntityReferenceImpl)paramNode).setReadOnly(false, true);
          expandEntityRef(node4, paramNode);
          node4.removeChild(paramNode);
          node2 = (node3 != null) ? node3.getNextSibling() : node4.getFirstChild();
          return (node3 != null && node2 != null && node3.getNodeType() == 3 && node2.getNodeType() == 3) ? node3 : node2;
        } 
        if (this.fDocument.errorChecking && (this.fConfiguration.features & 0x100) != 0 && this.fDocument.isXMLVersionChanged())
          CoreDocumentImpl.isXMLName(paramNode.getNodeName(), this.fDocument.isXML11Version()); 
        break;
      case 4:
        if ((this.fConfiguration.features & 0x8) == 0) {
          Node node = paramNode.getPreviousSibling();
          if (node != null && node.getNodeType() == 3) {
            ((Text)node).appendData(paramNode.getNodeValue());
            paramNode.getParentNode().removeChild(paramNode);
            return node;
          } 
          Text text = this.fDocument.createTextNode(paramNode.getNodeValue());
          node2 = paramNode.getParentNode();
          paramNode = node2.replaceChild(text, paramNode);
          return text;
        } 
        if (this.fValidationHandler != null) {
          this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = paramNode;
          this.fCurrentNode = paramNode;
          this.fValidationHandler.startCDATA(null);
          this.fValidationHandler.characterData(paramNode.getNodeValue(), null);
          this.fValidationHandler.endCDATA(null);
        } 
        if (this.fDTDValidator != null) {
          this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = paramNode;
          this.fCurrentNode = paramNode;
          this.fDTDValidator.startCDATA(null);
          this.fDTDValidator.characterData(paramNode.getNodeValue(), null);
          this.fDTDValidator.endCDATA(null);
        } 
        str = paramNode.getNodeValue();
        if ((this.fConfiguration.features & 0x10) != 0) {
          node2 = paramNode.getParentNode();
          if (this.fDocument.errorChecking)
            isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, paramNode.getNodeValue(), this.fDocument.isXML11Version()); 
          int i;
          while ((i = str.indexOf("]]>")) >= 0) {
            paramNode.setNodeValue(str.substring(0, i + 2));
            str = str.substring(i + 2);
            Node node = paramNode;
            CDATASection cDATASection = this.fDocument.createCDATASection(str);
            node2.insertBefore(cDATASection, paramNode.getNextSibling());
            paramNode = cDATASection;
            this.fLocator.fRelatedNode = node;
            String str1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "cdata-sections-splitted", null);
            reportDOMError(this.fErrorHandler, this.fError, this.fLocator, str1, (short)1, "cdata-sections-splitted");
          } 
          break;
        } 
        if (this.fDocument.errorChecking)
          isCDataWF(this.fErrorHandler, this.fError, this.fLocator, str, this.fDocument.isXML11Version()); 
        break;
      case 3:
        node1 = paramNode.getNextSibling();
        if (node1 != null && node1.getNodeType() == 3) {
          ((Text)paramNode).appendData(node1.getNodeValue());
          paramNode.getParentNode().removeChild(node1);
          return paramNode;
        } 
        if (paramNode.getNodeValue().length() == 0) {
          paramNode.getParentNode().removeChild(paramNode);
          break;
        } 
        s2 = (node1 != null) ? node1.getNodeType() : -1;
        if (s2 == -1 || (((this.fConfiguration.features & 0x4) != 0 || s2 != 6) && ((this.fConfiguration.features & 0x20) != 0 || s2 != 8) && ((this.fConfiguration.features & 0x8) != 0 || s2 != 4))) {
          if (this.fDocument.errorChecking && (this.fConfiguration.features & 0x100) != 0)
            isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, paramNode.getNodeValue(), this.fDocument.isXML11Version()); 
          if (this.fValidationHandler != null) {
            this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = paramNode;
            this.fCurrentNode = paramNode;
            this.fValidationHandler.characterData(paramNode.getNodeValue(), null);
          } 
          if (this.fDTDValidator != null) {
            this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = paramNode;
            this.fCurrentNode = paramNode;
            this.fDTDValidator.characterData(paramNode.getNodeValue(), null);
            if (this.allWhitespace) {
              this.allWhitespace = false;
              ((TextImpl)paramNode).setIgnorableWhitespace(true);
            } 
          } 
        } 
        break;
      case 7:
        if (this.fDocument.errorChecking && (this.fConfiguration.features & 0x100) != 0) {
          boolean bool;
          node1 = (ProcessingInstruction)paramNode;
          String str1 = node1.getTarget();
          if (this.fDocument.isXML11Version()) {
            bool = XML11Char.isXML11ValidName(str1);
          } else {
            bool = XMLChar.isValidName(str1);
          } 
          if (!bool) {
            String str2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[] { "Element", paramNode.getNodeName() });
            reportDOMError(this.fErrorHandler, this.fError, this.fLocator, str2, (short)2, "wf-invalid-character-in-node-name");
          } 
          isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, node1.getData(), this.fDocument.isXML11Version());
        } 
        break;
    } 
    return null;
  }
  
  private XMLGrammarPool createGrammarPool(DocumentTypeImpl paramDocumentTypeImpl) {
    XMLGrammarPoolImpl xMLGrammarPoolImpl = new XMLGrammarPoolImpl();
    XMLGrammarPreparser xMLGrammarPreparser = new XMLGrammarPreparser(this.fSymbolTable);
    xMLGrammarPreparser.registerPreparser("http://www.w3.org/TR/REC-xml", null);
    xMLGrammarPreparser.setFeature("http://apache.org/xml/features/namespaces", true);
    xMLGrammarPreparser.setFeature("http://apache.org/xml/features/validation", true);
    xMLGrammarPreparser.setProperty("http://apache.org/xml/properties/internal/grammar-pool", xMLGrammarPoolImpl);
    String str = paramDocumentTypeImpl.getInternalSubset();
    XMLInputSource xMLInputSource = new XMLInputSource(paramDocumentTypeImpl.getPublicId(), paramDocumentTypeImpl.getSystemId(), null);
    if (str != null)
      xMLInputSource.setCharacterStream(new StringReader(str)); 
    try {
      DTDGrammar dTDGrammar = (DTDGrammar)xMLGrammarPreparser.preparseGrammar("http://www.w3.org/TR/REC-xml", xMLInputSource);
      ((XMLDTDDescription)dTDGrammar.getGrammarDescription()).setRootName(paramDocumentTypeImpl.getName());
      xMLInputSource.setCharacterStream(null);
      dTDGrammar = (DTDGrammar)xMLGrammarPreparser.preparseGrammar("http://www.w3.org/TR/REC-xml", xMLInputSource);
      ((XMLDTDDescription)dTDGrammar.getGrammarDescription()).setRootName(paramDocumentTypeImpl.getName());
    } catch (XNIException xNIException) {
    
    } catch (IOException iOException) {}
    return xMLGrammarPoolImpl;
  }
  
  protected final void expandEntityRef(Node paramNode1, Node paramNode2) {
    for (Node node = paramNode2.getFirstChild(); node != null; node = node1) {
      Node node1 = node.getNextSibling();
      paramNode1.insertBefore(node, paramNode2);
    } 
  }
  
  protected final void namespaceFixUp(ElementImpl paramElementImpl, AttributeMap paramAttributeMap) {
    if (paramAttributeMap != null)
      for (byte b = 0; b < paramAttributeMap.getLength(); b++) {
        Attr attr = (Attr)paramAttributeMap.getItem(b);
        if (this.fDocument.errorChecking && (this.fConfiguration.features & 0x100) != 0 && this.fDocument.isXMLVersionChanged())
          this.fDocument.checkQName(attr.getPrefix(), attr.getLocalName()); 
        String str = attr.getNamespaceURI();
        if (str != null && str.equals(NamespaceContext.XMLNS_URI) && (this.fConfiguration.features & 0x200) != 0) {
          String str3 = attr.getNodeValue();
          if (str3 == null)
            str3 = XMLSymbols.EMPTY_STRING; 
          if (this.fDocument.errorChecking && str3.equals(NamespaceContext.XMLNS_URI)) {
            this.fLocator.fRelatedNode = attr;
            String str4 = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "CantBindXMLNS", null);
            reportDOMError(this.fErrorHandler, this.fError, this.fLocator, str4, (short)2, "CantBindXMLNS");
          } else {
            String str4 = attr.getPrefix();
            str4 = (str4 == null || str4.length() == 0) ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(str4);
            String str5 = this.fSymbolTable.addSymbol(attr.getLocalName());
            if (str4 == XMLSymbols.PREFIX_XMLNS) {
              str3 = this.fSymbolTable.addSymbol(str3);
              if (str3.length() != 0)
                this.fNamespaceContext.declarePrefix(str5, str3); 
            } else {
              str3 = this.fSymbolTable.addSymbol(str3);
              this.fNamespaceContext.declarePrefix(XMLSymbols.EMPTY_STRING, str3);
            } 
          } 
        } 
      }  
    String str1 = paramElementImpl.getNamespaceURI();
    String str2 = paramElementImpl.getPrefix();
    if ((this.fConfiguration.features & 0x200) == 0) {
      str1 = null;
    } else if (str1 != null) {
      str1 = this.fSymbolTable.addSymbol(str1);
      str2 = (str2 == null || str2.length() == 0) ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(str2);
      if (this.fNamespaceContext.getURI(str2) != str1) {
        addNamespaceDecl(str2, str1, paramElementImpl);
        this.fLocalNSBinder.declarePrefix(str2, str1);
        this.fNamespaceContext.declarePrefix(str2, str1);
      } 
    } else if (paramElementImpl.getLocalName() == null) {
      if (this.fNamespaceValidation) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NullLocalElementName", new Object[] { paramElementImpl.getNodeName() });
        reportDOMError(this.fErrorHandler, this.fError, this.fLocator, str, (short)3, "NullLocalElementName");
      } else {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NullLocalElementName", new Object[] { paramElementImpl.getNodeName() });
        reportDOMError(this.fErrorHandler, this.fError, this.fLocator, str, (short)2, "NullLocalElementName");
      } 
    } else {
      str1 = this.fNamespaceContext.getURI(XMLSymbols.EMPTY_STRING);
      if (str1 != null && str1.length() > 0) {
        addNamespaceDecl(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING, paramElementImpl);
        this.fLocalNSBinder.declarePrefix(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING);
        this.fNamespaceContext.declarePrefix(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING);
      } 
    } 
    if (paramAttributeMap != null) {
      paramAttributeMap.cloneMap(this.fAttributeList);
      for (byte b = 0; b < this.fAttributeList.size(); b++) {
        Attr attr = (Attr)this.fAttributeList.get(b);
        this.fLocator.fRelatedNode = attr;
        attr.normalize();
        String str3 = attr.getValue();
        String str4 = attr.getNodeName();
        str1 = attr.getNamespaceURI();
        if (str3 == null)
          str3 = XMLSymbols.EMPTY_STRING; 
        if (str1 != null) {
          str2 = attr.getPrefix();
          str2 = (str2 == null || str2.length() == 0) ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(str2);
          this.fSymbolTable.addSymbol(attr.getLocalName());
          if (str1 == null || !str1.equals(NamespaceContext.XMLNS_URI)) {
            if (this.fDocument.errorChecking && (this.fConfiguration.features & 0x100) != 0) {
              isAttrValueWF(this.fErrorHandler, this.fError, this.fLocator, paramAttributeMap, (AttrImpl)attr, attr.getValue(), this.fDocument.isXML11Version());
              if (this.fDocument.isXMLVersionChanged()) {
                boolean bool = CoreDocumentImpl.isXMLName(attr.getNodeName(), this.fDocument.isXML11Version());
                if (!bool) {
                  String str5 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[] { "Attribute", attr.getNodeName() });
                  reportDOMError(this.fErrorHandler, this.fError, this.fLocator, str5, (short)2, "wf-invalid-character-in-node-name");
                } 
              } 
            } 
            ((AttrImpl)attr).setIdAttribute(false);
            str1 = this.fSymbolTable.addSymbol(str1);
            String str = this.fNamespaceContext.getURI(str2);
            if (str2 == XMLSymbols.EMPTY_STRING || str != str1) {
              str4 = attr.getNodeName();
              String str5 = this.fNamespaceContext.getPrefix(str1);
              if (str5 != null && str5 != XMLSymbols.EMPTY_STRING) {
                str2 = str5;
              } else {
                if (str2 == XMLSymbols.EMPTY_STRING || this.fLocalNSBinder.getURI(str2) != null) {
                  byte b1 = 1;
                  for (str2 = this.fSymbolTable.addSymbol("NS" + b1++); this.fLocalNSBinder.getURI(str2) != null; str2 = this.fSymbolTable.addSymbol("NS" + b1++));
                } 
                addNamespaceDecl(str2, str1, paramElementImpl);
                str3 = this.fSymbolTable.addSymbol(str3);
                this.fLocalNSBinder.declarePrefix(str2, str3);
                this.fNamespaceContext.declarePrefix(str2, str1);
              } 
              attr.setPrefix(str2);
            } 
          } 
        } else {
          ((AttrImpl)attr).setIdAttribute(false);
          if (attr.getLocalName() == null)
            if (this.fNamespaceValidation) {
              String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NullLocalAttrName", new Object[] { attr.getNodeName() });
              reportDOMError(this.fErrorHandler, this.fError, this.fLocator, str, (short)3, "NullLocalAttrName");
            } else {
              String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NullLocalAttrName", new Object[] { attr.getNodeName() });
              reportDOMError(this.fErrorHandler, this.fError, this.fLocator, str, (short)2, "NullLocalAttrName");
            }  
        } 
      } 
    } 
  }
  
  protected final void addNamespaceDecl(String paramString1, String paramString2, ElementImpl paramElementImpl) {
    if (paramString1 == XMLSymbols.EMPTY_STRING) {
      paramElementImpl.setAttributeNS(NamespaceContext.XMLNS_URI, XMLSymbols.PREFIX_XMLNS, paramString2);
    } else {
      paramElementImpl.setAttributeNS(NamespaceContext.XMLNS_URI, "xmlns:" + paramString1, paramString2);
    } 
  }
  
  public static final void isCDataWF(DOMErrorHandler paramDOMErrorHandler, DOMErrorImpl paramDOMErrorImpl, DOMLocatorImpl paramDOMLocatorImpl, String paramString, boolean paramBoolean) {
    if (paramString == null || paramString.length() == 0)
      return; 
    char[] arrayOfChar = paramString.toCharArray();
    int i = arrayOfChar.length;
    if (paramBoolean) {
      byte b = 0;
      while (b < i) {
        char c = arrayOfChar[b++];
        if (XML11Char.isXML11Invalid(c)) {
          if (XMLChar.isHighSurrogate(c) && b < i) {
            char c1 = arrayOfChar[b++];
            if (XMLChar.isLowSurrogate(c1) && XMLChar.isSupplemental(XMLChar.supplemental(c, c1)))
              continue; 
          } 
          String str = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInCDSect", new Object[] { Integer.toString(c, 16) });
          reportDOMError(paramDOMErrorHandler, paramDOMErrorImpl, paramDOMLocatorImpl, str, (short)2, "wf-invalid-character");
          continue;
        } 
        if (c == ']') {
          byte b1 = b;
          if (b1 < i && arrayOfChar[b1] == ']') {
            while (++b1 < i && arrayOfChar[b1] == ']');
            if (b1 < i && arrayOfChar[b1] == '>') {
              String str = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "CDEndInContent", null);
              reportDOMError(paramDOMErrorHandler, paramDOMErrorImpl, paramDOMLocatorImpl, str, (short)2, "wf-invalid-character");
            } 
          } 
        } 
      } 
    } else {
      byte b = 0;
      while (b < i) {
        char c = arrayOfChar[b++];
        if (XMLChar.isInvalid(c)) {
          if (XMLChar.isHighSurrogate(c) && b < i) {
            char c1 = arrayOfChar[b++];
            if (XMLChar.isLowSurrogate(c1) && XMLChar.isSupplemental(XMLChar.supplemental(c, c1)))
              continue; 
          } 
          String str = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInCDSect", new Object[] { Integer.toString(c, 16) });
          reportDOMError(paramDOMErrorHandler, paramDOMErrorImpl, paramDOMLocatorImpl, str, (short)2, "wf-invalid-character");
          continue;
        } 
        if (c == ']') {
          byte b1 = b;
          if (b1 < i && arrayOfChar[b1] == ']') {
            while (++b1 < i && arrayOfChar[b1] == ']');
            if (b1 < i && arrayOfChar[b1] == '>') {
              String str = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "CDEndInContent", null);
              reportDOMError(paramDOMErrorHandler, paramDOMErrorImpl, paramDOMLocatorImpl, str, (short)2, "wf-invalid-character");
            } 
          } 
        } 
      } 
    } 
  }
  
  public static final void isXMLCharWF(DOMErrorHandler paramDOMErrorHandler, DOMErrorImpl paramDOMErrorImpl, DOMLocatorImpl paramDOMLocatorImpl, String paramString, boolean paramBoolean) {
    if (paramString == null || paramString.length() == 0)
      return; 
    char[] arrayOfChar = paramString.toCharArray();
    int i = arrayOfChar.length;
    if (paramBoolean) {
      byte b = 0;
      while (b < i) {
        if (XML11Char.isXML11Invalid(arrayOfChar[b++])) {
          char c = arrayOfChar[b - 1];
          if (XMLChar.isHighSurrogate(c) && b < i) {
            char c1 = arrayOfChar[b++];
            if (XMLChar.isLowSurrogate(c1) && XMLChar.isSupplemental(XMLChar.supplemental(c, c1)))
              continue; 
          } 
          String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "InvalidXMLCharInDOM", new Object[] { Integer.toString(arrayOfChar[b - 1], 16) });
          reportDOMError(paramDOMErrorHandler, paramDOMErrorImpl, paramDOMLocatorImpl, str, (short)2, "wf-invalid-character");
        } 
      } 
    } else {
      byte b = 0;
      while (b < i) {
        if (XMLChar.isInvalid(arrayOfChar[b++])) {
          char c = arrayOfChar[b - 1];
          if (XMLChar.isHighSurrogate(c) && b < i) {
            char c1 = arrayOfChar[b++];
            if (XMLChar.isLowSurrogate(c1) && XMLChar.isSupplemental(XMLChar.supplemental(c, c1)))
              continue; 
          } 
          String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "InvalidXMLCharInDOM", new Object[] { Integer.toString(arrayOfChar[b - 1], 16) });
          reportDOMError(paramDOMErrorHandler, paramDOMErrorImpl, paramDOMLocatorImpl, str, (short)2, "wf-invalid-character");
        } 
      } 
    } 
  }
  
  public static final void isCommentWF(DOMErrorHandler paramDOMErrorHandler, DOMErrorImpl paramDOMErrorImpl, DOMLocatorImpl paramDOMLocatorImpl, String paramString, boolean paramBoolean) {
    if (paramString == null || paramString.length() == 0)
      return; 
    char[] arrayOfChar = paramString.toCharArray();
    int i = arrayOfChar.length;
    if (paramBoolean) {
      byte b = 0;
      while (b < i) {
        char c = arrayOfChar[b++];
        if (XML11Char.isXML11Invalid(c)) {
          if (XMLChar.isHighSurrogate(c) && b < i) {
            char c1 = arrayOfChar[b++];
            if (XMLChar.isLowSurrogate(c1) && XMLChar.isSupplemental(XMLChar.supplemental(c, c1)))
              continue; 
          } 
          String str = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInComment", new Object[] { Integer.toString(arrayOfChar[b - 1], 16) });
          reportDOMError(paramDOMErrorHandler, paramDOMErrorImpl, paramDOMLocatorImpl, str, (short)2, "wf-invalid-character");
          continue;
        } 
        if (c == '-' && b < i && arrayOfChar[b] == '-') {
          String str = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "DashDashInComment", null);
          reportDOMError(paramDOMErrorHandler, paramDOMErrorImpl, paramDOMLocatorImpl, str, (short)2, "wf-invalid-character");
        } 
      } 
    } else {
      byte b = 0;
      while (b < i) {
        char c = arrayOfChar[b++];
        if (XMLChar.isInvalid(c)) {
          if (XMLChar.isHighSurrogate(c) && b < i) {
            char c1 = arrayOfChar[b++];
            if (XMLChar.isLowSurrogate(c1) && XMLChar.isSupplemental(XMLChar.supplemental(c, c1)))
              continue; 
          } 
          String str = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInComment", new Object[] { Integer.toString(arrayOfChar[b - 1], 16) });
          reportDOMError(paramDOMErrorHandler, paramDOMErrorImpl, paramDOMLocatorImpl, str, (short)2, "wf-invalid-character");
          continue;
        } 
        if (c == '-' && b < i && arrayOfChar[b] == '-') {
          String str = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "DashDashInComment", null);
          reportDOMError(paramDOMErrorHandler, paramDOMErrorImpl, paramDOMLocatorImpl, str, (short)2, "wf-invalid-character");
        } 
      } 
    } 
  }
  
  public static final void isAttrValueWF(DOMErrorHandler paramDOMErrorHandler, DOMErrorImpl paramDOMErrorImpl, DOMLocatorImpl paramDOMLocatorImpl, NamedNodeMap paramNamedNodeMap, Attr paramAttr, String paramString, boolean paramBoolean) {
    if (paramAttr instanceof AttrImpl && ((AttrImpl)paramAttr).hasStringValue()) {
      isXMLCharWF(paramDOMErrorHandler, paramDOMErrorImpl, paramDOMLocatorImpl, paramString, paramBoolean);
    } else {
      NodeList nodeList = paramAttr.getChildNodes();
      for (byte b = 0; b < nodeList.getLength(); b++) {
        Node node = nodeList.item(b);
        if (node.getNodeType() == 5) {
          Document document = paramAttr.getOwnerDocument();
          Entity entity = null;
          if (document != null) {
            DocumentType documentType = document.getDoctype();
            if (documentType != null) {
              NamedNodeMap namedNodeMap = documentType.getEntities();
              entity = (Entity)namedNodeMap.getNamedItemNS("*", node.getNodeName());
            } 
          } 
          if (entity == null) {
            String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "UndeclaredEntRefInAttrValue", new Object[] { paramAttr.getNodeName() });
            reportDOMError(paramDOMErrorHandler, paramDOMErrorImpl, paramDOMLocatorImpl, str, (short)2, "UndeclaredEntRefInAttrValue");
          } 
        } else {
          isXMLCharWF(paramDOMErrorHandler, paramDOMErrorImpl, paramDOMLocatorImpl, node.getNodeValue(), paramBoolean);
        } 
      } 
    } 
  }
  
  public static final void reportDOMError(DOMErrorHandler paramDOMErrorHandler, DOMErrorImpl paramDOMErrorImpl, DOMLocatorImpl paramDOMLocatorImpl, String paramString1, short paramShort, String paramString2) {
    if (paramDOMErrorHandler != null) {
      paramDOMErrorImpl.reset();
      paramDOMErrorImpl.fMessage = paramString1;
      paramDOMErrorImpl.fSeverity = paramShort;
      paramDOMErrorImpl.fLocator = paramDOMLocatorImpl;
      paramDOMErrorImpl.fType = paramString2;
      paramDOMErrorImpl.fRelatedData = paramDOMLocatorImpl.fRelatedNode;
      if (!paramDOMErrorHandler.handleError(paramDOMErrorImpl))
        throw new AbortException(); 
    } 
    if (paramShort == 3)
      throw new AbortException(); 
  }
  
  protected final void updateQName(Node paramNode, QName paramQName) {
    String str1 = paramNode.getPrefix();
    String str2 = paramNode.getNamespaceURI();
    String str3 = paramNode.getLocalName();
    paramQName.prefix = (str1 != null && str1.length() != 0) ? this.fSymbolTable.addSymbol(str1) : null;
    paramQName.localpart = (str3 != null) ? this.fSymbolTable.addSymbol(str3) : null;
    paramQName.rawname = this.fSymbolTable.addSymbol(paramNode.getNodeName());
    paramQName.uri = (str2 != null) ? this.fSymbolTable.addSymbol(str2) : null;
  }
  
  final String normalizeAttributeValue(String paramString, Attr paramAttr) {
    if (!paramAttr.getSpecified())
      return paramString; 
    int i = paramString.length();
    if (this.fNormalizedValue.ch.length < i)
      this.fNormalizedValue.ch = new char[i]; 
    this.fNormalizedValue.length = 0;
    boolean bool = false;
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (c == '\t' || c == '\n') {
        this.fNormalizedValue.ch[this.fNormalizedValue.length++] = ' ';
        bool = true;
      } else if (c == '\r') {
        bool = true;
        this.fNormalizedValue.ch[this.fNormalizedValue.length++] = ' ';
        byte b1 = b + 1;
        if (b1 < i && paramString.charAt(b1) == '\n')
          b = b1; 
      } else {
        this.fNormalizedValue.ch[this.fNormalizedValue.length++] = c;
      } 
    } 
    if (bool) {
      paramString = this.fNormalizedValue.toString();
      paramAttr.setValue(paramString);
    } 
    return paramString;
  }
  
  public void startDocument(XMLLocator paramXMLLocator, String paramString, NamespaceContext paramNamespaceContext, Augmentations paramAugmentations) throws XNIException {}
  
  public void xmlDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations) throws XNIException {}
  
  public void doctypeDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations) throws XNIException {}
  
  public void comment(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {}
  
  public void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {}
  
  public void startElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {
    Element element = (Element)this.fCurrentNode;
    int i = paramXMLAttributes.getLength();
    for (byte b = 0; b < i; b++) {
      paramXMLAttributes.getName(b, this.fAttrQName);
      Attr attr = null;
      attr = element.getAttributeNodeNS(this.fAttrQName.uri, this.fAttrQName.localpart);
      AttributePSVI attributePSVI = (AttributePSVI)paramXMLAttributes.getAugmentations(b).getItem("ATTRIBUTE_PSVI");
      if (attributePSVI != null) {
        XSSimpleTypeDefinition xSSimpleTypeDefinition = attributePSVI.getMemberTypeDefinition();
        boolean bool = false;
        if (xSSimpleTypeDefinition != null) {
          bool = ((XSSimpleType)xSSimpleTypeDefinition).isIDType();
        } else {
          XSTypeDefinition xSTypeDefinition = attributePSVI.getTypeDefinition();
          if (xSTypeDefinition != null)
            bool = ((XSSimpleType)xSTypeDefinition).isIDType(); 
        } 
        if (bool)
          ((ElementImpl)element).setIdAttributeNode(attr, true); 
        if (this.fPSVI)
          ((PSVIAttrNSImpl)attr).setPSVI(attributePSVI); 
        if ((this.fConfiguration.features & 0x2) != 0) {
          boolean bool1 = attr.getSpecified();
          attr.setValue(attributePSVI.getSchemaNormalizedValue());
          if (!bool1)
            ((AttrImpl)attr).setSpecified(bool1); 
        } 
      } 
    } 
  }
  
  public void emptyElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {
    startElement(paramQName, paramXMLAttributes, paramAugmentations);
    endElement(paramQName, paramAugmentations);
  }
  
  public void startGeneralEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException {}
  
  public void textDecl(String paramString1, String paramString2, Augmentations paramAugmentations) throws XNIException {}
  
  public void endGeneralEntity(String paramString, Augmentations paramAugmentations) throws XNIException {}
  
  public void characters(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {}
  
  public void ignorableWhitespace(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException { this.allWhitespace = true; }
  
  public void endElement(QName paramQName, Augmentations paramAugmentations) throws XNIException {
    if (paramAugmentations != null) {
      ElementPSVI elementPSVI = (ElementPSVI)paramAugmentations.getItem("ELEMENT_PSVI");
      if (elementPSVI != null) {
        ElementImpl elementImpl = (ElementImpl)this.fCurrentNode;
        if (this.fPSVI)
          ((PSVIElementNSImpl)this.fCurrentNode).setPSVI(elementPSVI); 
        String str = elementPSVI.getSchemaNormalizedValue();
        if ((this.fConfiguration.features & 0x2) != 0) {
          if (str != null)
            elementImpl.setTextContent(str); 
        } else {
          String str1 = elementImpl.getTextContent();
          if (str1.length() == 0 && str != null)
            elementImpl.setTextContent(str); 
        } 
      } 
    } 
  }
  
  public void startCDATA(Augmentations paramAugmentations) throws XNIException {}
  
  public void endCDATA(Augmentations paramAugmentations) throws XNIException {}
  
  public void endDocument(Augmentations paramAugmentations) throws XNIException {}
  
  public void setDocumentSource(XMLDocumentSource paramXMLDocumentSource) {}
  
  public XMLDocumentSource getDocumentSource() { return null; }
  
  protected final class XMLAttributesProxy implements XMLAttributes {
    protected AttributeMap fAttributes;
    
    protected CoreDocumentImpl fDocument;
    
    protected ElementImpl fElement;
    
    protected final Vector fAugmentations = new Vector(5);
    
    public void setAttributes(AttributeMap param1AttributeMap, CoreDocumentImpl param1CoreDocumentImpl, ElementImpl param1ElementImpl) {
      this.fDocument = param1CoreDocumentImpl;
      this.fAttributes = param1AttributeMap;
      this.fElement = param1ElementImpl;
      if (param1AttributeMap != null) {
        int i = param1AttributeMap.getLength();
        this.fAugmentations.setSize(i);
        for (byte b = 0; b < i; b++)
          this.fAugmentations.setElementAt(new AugmentationsImpl(), b); 
      } else {
        this.fAugmentations.setSize(0);
      } 
    }
    
    public int addAttribute(QName param1QName, String param1String1, String param1String2) {
      int i = this.fElement.getXercesAttribute(param1QName.uri, param1QName.localpart);
      if (i < 0) {
        AttrImpl attrImpl = (AttrImpl)((CoreDocumentImpl)this.fElement.getOwnerDocument()).createAttributeNS(param1QName.uri, param1QName.rawname, param1QName.localpart);
        attrImpl.setNodeValue(param1String2);
        i = this.fElement.setXercesAttributeNode(attrImpl);
        this.fAugmentations.insertElementAt(new AugmentationsImpl(), i);
        attrImpl.setSpecified(false);
      } 
      return i;
    }
    
    public void removeAllAttributes() {}
    
    public void removeAttributeAt(int param1Int) {}
    
    public int getLength() { return (this.fAttributes != null) ? this.fAttributes.getLength() : 0; }
    
    public int getIndex(String param1String) { return -1; }
    
    public int getIndex(String param1String1, String param1String2) { return -1; }
    
    public void setName(int param1Int, QName param1QName) {}
    
    public void getName(int param1Int, QName param1QName) {
      if (this.fAttributes != null)
        DOMNormalizer.this.updateQName((Node)this.fAttributes.getItem(param1Int), param1QName); 
    }
    
    public String getPrefix(int param1Int) { return null; }
    
    public String getURI(int param1Int) { return null; }
    
    public String getLocalName(int param1Int) { return null; }
    
    public String getQName(int param1Int) { return null; }
    
    public QName getQualifiedName(int param1Int) { return null; }
    
    public void setType(int param1Int, String param1String) {}
    
    public String getType(int param1Int) { return "CDATA"; }
    
    public String getType(String param1String) { return "CDATA"; }
    
    public String getType(String param1String1, String param1String2) { return "CDATA"; }
    
    public void setValue(int param1Int, String param1String) {
      if (this.fAttributes != null) {
        AttrImpl attrImpl = (AttrImpl)this.fAttributes.getItem(param1Int);
        boolean bool = attrImpl.getSpecified();
        attrImpl.setValue(param1String);
        attrImpl.setSpecified(bool);
      } 
    }
    
    public void setValue(int param1Int, String param1String, XMLString param1XMLString) { setValue(param1Int, param1XMLString.toString()); }
    
    public String getValue(int param1Int) { return (this.fAttributes != null) ? this.fAttributes.item(param1Int).getNodeValue() : ""; }
    
    public String getValue(String param1String) { return null; }
    
    public String getValue(String param1String1, String param1String2) {
      if (this.fAttributes != null) {
        Node node = this.fAttributes.getNamedItemNS(param1String1, param1String2);
        return (node != null) ? node.getNodeValue() : null;
      } 
      return null;
    }
    
    public void setNonNormalizedValue(int param1Int, String param1String) {}
    
    public String getNonNormalizedValue(int param1Int) { return null; }
    
    public void setSpecified(int param1Int, boolean param1Boolean) {
      AttrImpl attrImpl = (AttrImpl)this.fAttributes.getItem(param1Int);
      attrImpl.setSpecified(param1Boolean);
    }
    
    public boolean isSpecified(int param1Int) { return ((Attr)this.fAttributes.getItem(param1Int)).getSpecified(); }
    
    public Augmentations getAugmentations(int param1Int) { return (Augmentations)this.fAugmentations.elementAt(param1Int); }
    
    public Augmentations getAugmentations(String param1String1, String param1String2) { return null; }
    
    public Augmentations getAugmentations(String param1String) { return null; }
    
    public void setAugmentations(int param1Int, Augmentations param1Augmentations) { this.fAugmentations.setElementAt(param1Augmentations, param1Int); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\DOMNormalizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */