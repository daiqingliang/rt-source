package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.dom.DOMErrorImpl;
import com.sun.org.apache.xerces.internal.dom.DOMLocatorImpl;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMLocator;
import org.w3c.dom.Node;

public class DOMErrorHandlerWrapper implements XMLErrorHandler, DOMErrorHandler {
  protected DOMErrorHandler fDomErrorHandler;
  
  boolean eStatus = true;
  
  protected PrintWriter fOut;
  
  public Node fCurrentNode;
  
  protected final XMLErrorCode fErrorCode = new XMLErrorCode(null, null);
  
  protected final DOMErrorImpl fDOMError = new DOMErrorImpl();
  
  public DOMErrorHandlerWrapper() { this.fOut = new PrintWriter(System.err); }
  
  public DOMErrorHandlerWrapper(DOMErrorHandler paramDOMErrorHandler) { this.fDomErrorHandler = paramDOMErrorHandler; }
  
  public void setErrorHandler(DOMErrorHandler paramDOMErrorHandler) { this.fDomErrorHandler = paramDOMErrorHandler; }
  
  public DOMErrorHandler getErrorHandler() { return this.fDomErrorHandler; }
  
  public void warning(String paramString1, String paramString2, XMLParseException paramXMLParseException) throws XNIException {
    this.fDOMError.fSeverity = 1;
    this.fDOMError.fException = paramXMLParseException;
    this.fDOMError.fType = paramString2;
    this.fDOMError.fRelatedData = this.fDOMError.fMessage = paramXMLParseException.getMessage();
    DOMLocatorImpl dOMLocatorImpl = this.fDOMError.fLocator;
    if (dOMLocatorImpl != null) {
      dOMLocatorImpl.fColumnNumber = paramXMLParseException.getColumnNumber();
      dOMLocatorImpl.fLineNumber = paramXMLParseException.getLineNumber();
      dOMLocatorImpl.fUtf16Offset = paramXMLParseException.getCharacterOffset();
      dOMLocatorImpl.fUri = paramXMLParseException.getExpandedSystemId();
      dOMLocatorImpl.fRelatedNode = this.fCurrentNode;
    } 
    if (this.fDomErrorHandler != null)
      this.fDomErrorHandler.handleError(this.fDOMError); 
  }
  
  public void error(String paramString1, String paramString2, XMLParseException paramXMLParseException) throws XNIException {
    this.fDOMError.fSeverity = 2;
    this.fDOMError.fException = paramXMLParseException;
    this.fDOMError.fType = paramString2;
    this.fDOMError.fRelatedData = this.fDOMError.fMessage = paramXMLParseException.getMessage();
    DOMLocatorImpl dOMLocatorImpl = this.fDOMError.fLocator;
    if (dOMLocatorImpl != null) {
      dOMLocatorImpl.fColumnNumber = paramXMLParseException.getColumnNumber();
      dOMLocatorImpl.fLineNumber = paramXMLParseException.getLineNumber();
      dOMLocatorImpl.fUtf16Offset = paramXMLParseException.getCharacterOffset();
      dOMLocatorImpl.fUri = paramXMLParseException.getExpandedSystemId();
      dOMLocatorImpl.fRelatedNode = this.fCurrentNode;
    } 
    if (this.fDomErrorHandler != null)
      this.fDomErrorHandler.handleError(this.fDOMError); 
  }
  
  public void fatalError(String paramString1, String paramString2, XMLParseException paramXMLParseException) throws XNIException {
    this.fDOMError.fSeverity = 3;
    this.fDOMError.fException = paramXMLParseException;
    this.fErrorCode.setValues(paramString1, paramString2);
    String str = DOMErrorTypeMap.getDOMErrorType(this.fErrorCode);
    this.fDOMError.fType = (str != null) ? str : paramString2;
    this.fDOMError.fRelatedData = this.fDOMError.fMessage = paramXMLParseException.getMessage();
    DOMLocatorImpl dOMLocatorImpl = this.fDOMError.fLocator;
    if (dOMLocatorImpl != null) {
      dOMLocatorImpl.fColumnNumber = paramXMLParseException.getColumnNumber();
      dOMLocatorImpl.fLineNumber = paramXMLParseException.getLineNumber();
      dOMLocatorImpl.fUtf16Offset = paramXMLParseException.getCharacterOffset();
      dOMLocatorImpl.fUri = paramXMLParseException.getExpandedSystemId();
      dOMLocatorImpl.fRelatedNode = this.fCurrentNode;
    } 
    if (this.fDomErrorHandler != null)
      this.fDomErrorHandler.handleError(this.fDOMError); 
  }
  
  public boolean handleError(DOMError paramDOMError) {
    printError(paramDOMError);
    return this.eStatus;
  }
  
  private void printError(DOMError paramDOMError) {
    short s = paramDOMError.getSeverity();
    this.fOut.print("[");
    if (s == 1) {
      this.fOut.print("Warning");
    } else if (s == 2) {
      this.fOut.print("Error");
    } else {
      this.fOut.print("FatalError");
      this.eStatus = false;
    } 
    this.fOut.print("] ");
    DOMLocator dOMLocator = paramDOMError.getLocation();
    if (dOMLocator != null) {
      this.fOut.print(dOMLocator.getLineNumber());
      this.fOut.print(":");
      this.fOut.print(dOMLocator.getColumnNumber());
      this.fOut.print(":");
      this.fOut.print(dOMLocator.getByteOffset());
      this.fOut.print(",");
      this.fOut.print(dOMLocator.getUtf16Offset());
      Node node = dOMLocator.getRelatedNode();
      if (node != null) {
        this.fOut.print("[");
        this.fOut.print(node.getNodeName());
        this.fOut.print("]");
      } 
      String str = dOMLocator.getUri();
      if (str != null) {
        int i = str.lastIndexOf('/');
        if (i != -1)
          str = str.substring(i + 1); 
        this.fOut.print(": ");
        this.fOut.print(str);
      } 
    } 
    this.fOut.print(":");
    this.fOut.print(paramDOMError.getMessage());
    this.fOut.println();
    this.fOut.flush();
  }
  
  private static class DOMErrorTypeMap {
    private static final Map<XMLErrorCode, String> fgDOMErrorTypeTable;
    
    public static String getDOMErrorType(XMLErrorCode param1XMLErrorCode) { return (String)fgDOMErrorTypeTable.get(param1XMLErrorCode); }
    
    static  {
      HashMap hashMap = new HashMap();
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInCDSect"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInContent"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "TwoColonsInQName"), "wf-invalid-character-in-node-name");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ColonNotLegalWithNS"), "wf-invalid-character-in-node-name");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInProlog"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "CDEndInContent"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "CDSectUnterminated"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "DoctypeNotAllowed"), "doctype-not-allowed");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ETagRequired"), "wf-invalid-character-in-node-name");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ElementUnterminated"), "wf-invalid-character-in-node-name");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "EqRequiredInAttribute"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "OpenQuoteExpected"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "CloseQuoteExpected"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ETagUnterminated"), "wf-invalid-character-in-node-name");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MarkupNotRecognizedInContent"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "DoctypeIllegalInContent"), "doctype-not-allowed");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInAttValue"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInPI"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInInternalSubset"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "QuoteRequiredInAttValue"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "LessthanInAttValue"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "AttributeValueUnterminated"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "PITargetRequired"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "SpaceRequiredInPI"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "PIUnterminated"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ReservedPITarget"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "PI_NOT_IN_ONE_ENTITY"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "PINotInOneEntity"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid"), "unsupported-encoding");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported"), "unsupported-encoding");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInEntityValue"), "wf-invalid-character-in-node-name");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInExternalSubset"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInIgnoreSect"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInPublicID"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInSystemID"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "SpaceRequiredAfterSYSTEM"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "QuoteRequiredInSystemID"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "SystemIDUnterminated"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "SpaceRequiredAfterPUBLIC"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "QuoteRequiredInPublicID"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "PublicIDUnterminated"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "PubidCharIllegal"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "SpaceRequiredBetweenPublicAndSystem"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_ROOT_ELEMENT_TYPE_IN_DOCTYPEDECL"), "wf-invalid-character-in-node-name");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ROOT_ELEMENT_TYPE_REQUIRED"), "wf-invalid-character-in-node-name");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "DoctypedeclUnterminated"), "wf-invalid-character-in-node-name");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "PEReferenceWithinMarkup"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_MARKUP_NOT_RECOGNIZED_IN_DTD"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_ELEMENT_TYPE_IN_ELEMENTDECL"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ELEMENT_TYPE_REQUIRED_IN_ELEMENTDECL"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_CONTENTSPEC_IN_ELEMENTDECL"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CONTENTSPEC_REQUIRED_IN_ELEMENTDECL"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ElementDeclUnterminated"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_OPEN_PAREN_OR_ELEMENT_TYPE_REQUIRED_IN_CHILDREN"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CLOSE_PAREN_REQUIRED_IN_CHILDREN"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ELEMENT_TYPE_REQUIRED_IN_MIXED_CONTENT"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CLOSE_PAREN_REQUIRED_IN_MIXED"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MixedContentUnterminated"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_ELEMENT_TYPE_IN_ATTLISTDECL"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ELEMENT_TYPE_REQUIRED_IN_ATTLISTDECL"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_ATTRIBUTE_NAME_IN_ATTDEF"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "AttNameRequiredInAttDef"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_ATTTYPE_IN_ATTDEF"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "AttTypeRequiredInAttDef"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_DEFAULTDECL_IN_ATTDEF"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ATTRIBUTE_DEFINITION"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_AFTER_NOTATION_IN_NOTATIONTYPE"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_OPEN_PAREN_REQUIRED_IN_NOTATIONTYPE"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_NAME_REQUIRED_IN_NOTATIONTYPE"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "NotationTypeUnterminated"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_NMTOKEN_REQUIRED_IN_ENUMERATION"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "EnumerationUnterminated"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DISTINCT_TOKENS_IN_ENUMERATION"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DISTINCT_NOTATION_IN_ENUMERATION"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_AFTER_FIXED_IN_DEFAULTDECL"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "IncludeSectUnterminated"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "IgnoreSectUnterminated"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "NameRequiredInPEReference"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "SemicolonRequiredInPEReference"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_ENTITY_NAME_IN_ENTITYDECL"), "wf-invalid-character-in-node-name");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_PERCENT_IN_PEDECL"), "wf-invalid-character-in-node-name");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_ENTITY_NAME_IN_PEDECL"), "wf-invalid-character-in-node-name");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ENTITY_NAME_REQUIRED_IN_ENTITYDECL"), "wf-invalid-character-in-node-name");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_AFTER_ENTITY_NAME_IN_ENTITYDECL"), "wf-invalid-character-in-node-name");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_NOTATION_NAME_IN_UNPARSED_ENTITYDECL"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_NDATA_IN_UNPARSED_ENTITYDECL"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_NOTATION_NAME_REQUIRED_FOR_UNPARSED_ENTITYDECL"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "EntityDeclUnterminated"), "wf-invalid-character-in-node-name");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION"), "wf-invalid-character-in-node-name");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ExternalIDRequired"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_PUBIDLITERAL_IN_EXTERNALID"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_AFTER_PUBIDLITERAL_IN_EXTERNALID"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_SYSTEMLITERAL_IN_EXTERNALID"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_URI_FRAGMENT_IN_SYSTEMID"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_NOTATION_NAME_IN_NOTATIONDECL"), "wf-invalid-character-in-node-name");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_NOTATION_NAME_REQUIRED_IN_NOTATIONDECL"), "wf-invalid-character-in-node-name");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_AFTER_NOTATION_NAME_IN_NOTATIONDECL"), "wf-invalid-character-in-node-name");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ExternalIDorPublicIDRequired"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "NotationDeclUnterminated"), "wf-invalid-character-in-node-name");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ReferenceToExternalEntity"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ReferenceToUnparsedEntity"), "wf-invalid-character");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingNotSupported"), "unsupported-encoding");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingRequired"), "unsupported-encoding");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "IllegalQName"), "wf-invalid-character-in-node-name");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ElementXMLNSPrefix"), "wf-invalid-character-in-node-name");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ElementPrefixUnbound"), "wf-invalid-character-in-node-name");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "AttributePrefixUnbound"), "wf-invalid-character-in-node-name");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "EmptyPrefixedAttName"), "wf-invalid-character-in-node-name");
      hashMap.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "PrefixDeclared"), "wf-invalid-character-in-node-name");
      fgDOMErrorTypeTable = Collections.unmodifiableMap(hashMap);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\DOMErrorHandlerWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */