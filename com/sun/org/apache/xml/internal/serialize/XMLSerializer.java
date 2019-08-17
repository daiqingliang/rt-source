package com.sun.org.apache.xml.internal.serialize;

import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class XMLSerializer extends BaseMarkupSerializer {
  protected static final boolean DEBUG = false;
  
  protected NamespaceSupport fNSBinder;
  
  protected NamespaceSupport fLocalNSBinder;
  
  protected SymbolTable fSymbolTable;
  
  protected static final String PREFIX = "NS";
  
  protected boolean fNamespaces = false;
  
  protected boolean fNamespacePrefixes = true;
  
  private boolean fPreserveSpace;
  
  public XMLSerializer() { super(new OutputFormat("xml", null, false)); }
  
  public XMLSerializer(OutputFormat paramOutputFormat) {
    super((paramOutputFormat != null) ? paramOutputFormat : new OutputFormat("xml", null, false));
    this._format.setMethod("xml");
  }
  
  public XMLSerializer(Writer paramWriter, OutputFormat paramOutputFormat) {
    super((paramOutputFormat != null) ? paramOutputFormat : new OutputFormat("xml", null, false));
    this._format.setMethod("xml");
    setOutputCharStream(paramWriter);
  }
  
  public XMLSerializer(OutputStream paramOutputStream, OutputFormat paramOutputFormat) {
    super((paramOutputFormat != null) ? paramOutputFormat : new OutputFormat("xml", null, false));
    this._format.setMethod("xml");
    setOutputByteStream(paramOutputStream);
  }
  
  public void setOutputFormat(OutputFormat paramOutputFormat) { super.setOutputFormat((paramOutputFormat != null) ? paramOutputFormat : new OutputFormat("xml", null, false)); }
  
  public void setNamespaces(boolean paramBoolean) {
    this.fNamespaces = paramBoolean;
    if (this.fNSBinder == null) {
      this.fNSBinder = new NamespaceSupport();
      this.fLocalNSBinder = new NamespaceSupport();
      this.fSymbolTable = new SymbolTable();
    } 
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    boolean bool = false;
    try {
      if (this._printer == null) {
        String str1 = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "NoWriterSupplied", null);
        throw new IllegalStateException(str1);
      } 
      ElementState elementState = getElementState();
      if (isDocumentState()) {
        if (!this._started)
          startDocument((paramString2 == null || paramString2.length() == 0) ? paramString3 : paramString2); 
      } else {
        if (elementState.empty)
          this._printer.printText('>'); 
        if (elementState.inCData) {
          this._printer.printText("]]>");
          elementState.inCData = false;
        } 
        if (this._indenting && !elementState.preserveSpace && (elementState.empty || elementState.afterElement || elementState.afterComment))
          this._printer.breakLine(); 
      } 
      boolean bool1 = elementState.preserveSpace;
      paramAttributes = extractNamespaces(paramAttributes);
      if (paramString3 == null || paramString3.length() == 0) {
        if (paramString2 == null) {
          String str1 = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "NoName", null);
          throw new SAXException(str1);
        } 
        if (paramString1 != null && !paramString1.equals("")) {
          String str1 = getPrefix(paramString1);
          if (str1 != null && str1.length() > 0) {
            paramString3 = str1 + ":" + paramString2;
          } else {
            paramString3 = paramString2;
          } 
        } else {
          paramString3 = paramString2;
        } 
        bool = true;
      } 
      this._printer.printText('<');
      this._printer.printText(paramString3);
      this._printer.indent();
      if (paramAttributes != null)
        for (byte b = 0; b < paramAttributes.getLength(); b++) {
          this._printer.printSpace();
          String str1 = paramAttributes.getQName(b);
          if (str1 != null && str1.length() == 0) {
            str1 = paramAttributes.getLocalName(b);
            String str3 = paramAttributes.getURI(b);
            if (str3 != null && str3.length() != 0 && (paramString1 == null || paramString1.length() == 0 || !str3.equals(paramString1))) {
              String str4 = getPrefix(str3);
              if (str4 != null && str4.length() > 0)
                str1 = str4 + ":" + str1; 
            } 
          } 
          String str2 = paramAttributes.getValue(b);
          if (str2 == null)
            str2 = ""; 
          this._printer.printText(str1);
          this._printer.printText("=\"");
          printEscaped(str2);
          this._printer.printText('"');
          if (str1.equals("xml:space"))
            if (str2.equals("preserve")) {
              bool1 = true;
            } else {
              bool1 = this._format.getPreserveSpace();
            }  
        }  
      if (this._prefixes != null)
        for (Map.Entry entry : this._prefixes.entrySet()) {
          this._printer.printSpace();
          String str2 = (String)entry.getKey();
          String str1 = (String)entry.getValue();
          if (str1.length() == 0) {
            this._printer.printText("xmlns=\"");
            printEscaped(str2);
            this._printer.printText('"');
            continue;
          } 
          this._printer.printText("xmlns:");
          this._printer.printText(str1);
          this._printer.printText("=\"");
          printEscaped(str2);
          this._printer.printText('"');
        }  
      elementState = enterElementState(paramString1, paramString2, paramString3, bool1);
      String str = (paramString2 == null || paramString2.length() == 0) ? paramString3 : (paramString1 + "^" + paramString2);
      elementState.doCData = this._format.isCDataElement(str);
      elementState.unescaped = this._format.isNonEscapingElement(str);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    try {
      endElementIO(paramString1, paramString2, paramString3);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void endElementIO(String paramString1, String paramString2, String paramString3) throws SAXException {
    this._printer.unindent();
    ElementState elementState = getElementState();
    if (elementState.empty) {
      this._printer.printText("/>");
    } else {
      if (elementState.inCData)
        this._printer.printText("]]>"); 
      if (this._indenting && !elementState.preserveSpace && (elementState.afterElement || elementState.afterComment))
        this._printer.breakLine(); 
      this._printer.printText("</");
      this._printer.printText(elementState.rawName);
      this._printer.printText('>');
    } 
    elementState = leaveElementState();
    elementState.afterElement = true;
    elementState.afterComment = false;
    elementState.empty = false;
    if (isDocumentState())
      this._printer.flush(); 
  }
  
  public void startElement(String paramString, AttributeList paramAttributeList) throws SAXException {
    try {
      if (this._printer == null) {
        String str = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "NoWriterSupplied", null);
        throw new IllegalStateException(str);
      } 
      ElementState elementState = getElementState();
      if (isDocumentState()) {
        if (!this._started)
          startDocument(paramString); 
      } else {
        if (elementState.empty)
          this._printer.printText('>'); 
        if (elementState.inCData) {
          this._printer.printText("]]>");
          elementState.inCData = false;
        } 
        if (this._indenting && !elementState.preserveSpace && (elementState.empty || elementState.afterElement || elementState.afterComment))
          this._printer.breakLine(); 
      } 
      boolean bool = elementState.preserveSpace;
      this._printer.printText('<');
      this._printer.printText(paramString);
      this._printer.indent();
      if (paramAttributeList != null)
        for (byte b = 0; b < paramAttributeList.getLength(); b++) {
          this._printer.printSpace();
          String str1 = paramAttributeList.getName(b);
          String str2 = paramAttributeList.getValue(b);
          if (str2 != null) {
            this._printer.printText(str1);
            this._printer.printText("=\"");
            printEscaped(str2);
            this._printer.printText('"');
          } 
          if (str1.equals("xml:space"))
            if (str2.equals("preserve")) {
              bool = true;
            } else {
              bool = this._format.getPreserveSpace();
            }  
        }  
      elementState = enterElementState(null, null, paramString, bool);
      elementState.doCData = this._format.isCDataElement(paramString);
      elementState.unescaped = this._format.isNonEscapingElement(paramString);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void endElement(String paramString) throws SAXException { endElement(null, null, paramString); }
  
  protected void startDocument(String paramString) throws SAXException {
    String str = this._printer.leaveDTD();
    if (!this._started) {
      if (!this._format.getOmitXMLDeclaration()) {
        StringBuffer stringBuffer = new StringBuffer("<?xml version=\"");
        if (this._format.getVersion() != null) {
          stringBuffer.append(this._format.getVersion());
        } else {
          stringBuffer.append("1.0");
        } 
        stringBuffer.append('"');
        String str1 = this._format.getEncoding();
        if (str1 != null) {
          stringBuffer.append(" encoding=\"");
          stringBuffer.append(str1);
          stringBuffer.append('"');
        } 
        if (this._format.getStandalone() && this._docTypeSystemId == null && this._docTypePublicId == null)
          stringBuffer.append(" standalone=\"yes\""); 
        stringBuffer.append("?>");
        this._printer.printText(stringBuffer);
        this._printer.breakLine();
      } 
      if (!this._format.getOmitDocumentType())
        if (this._docTypeSystemId != null) {
          this._printer.printText("<!DOCTYPE ");
          this._printer.printText(paramString);
          if (this._docTypePublicId != null) {
            this._printer.printText(" PUBLIC ");
            printDoctypeURL(this._docTypePublicId);
            if (this._indenting) {
              this._printer.breakLine();
              for (byte b = 0; b < 18 + paramString.length(); b++)
                this._printer.printText(" "); 
            } else {
              this._printer.printText(" ");
            } 
            printDoctypeURL(this._docTypeSystemId);
          } else {
            this._printer.printText(" SYSTEM ");
            printDoctypeURL(this._docTypeSystemId);
          } 
          if (str != null && str.length() > 0) {
            this._printer.printText(" [");
            printText(str, true, true);
            this._printer.printText(']');
          } 
          this._printer.printText(">");
          this._printer.breakLine();
        } else if (str != null && str.length() > 0) {
          this._printer.printText("<!DOCTYPE ");
          this._printer.printText(paramString);
          this._printer.printText(" [");
          printText(str, true, true);
          this._printer.printText("]>");
          this._printer.breakLine();
        }  
    } 
    this._started = true;
    serializePreRoot();
  }
  
  protected void serializeElement(Element paramElement) throws IOException {
    if (this.fNamespaces) {
      this.fLocalNSBinder.reset();
      this.fNSBinder.pushContext();
    } 
    String str = paramElement.getTagName();
    ElementState elementState = getElementState();
    if (isDocumentState()) {
      if (!this._started)
        startDocument(str); 
    } else {
      if (elementState.empty)
        this._printer.printText('>'); 
      if (elementState.inCData) {
        this._printer.printText("]]>");
        elementState.inCData = false;
      } 
      if (this._indenting && !elementState.preserveSpace && (elementState.empty || elementState.afterElement || elementState.afterComment))
        this._printer.breakLine(); 
    } 
    this.fPreserveSpace = elementState.preserveSpace;
    int i = 0;
    NamedNodeMap namedNodeMap = null;
    if (paramElement.hasAttributes()) {
      namedNodeMap = paramElement.getAttributes();
      i = namedNodeMap.getLength();
    } 
    if (!this.fNamespaces) {
      this._printer.printText('<');
      this._printer.printText(str);
      this._printer.indent();
      for (byte b = 0; b < i; b++) {
        Attr attr = (Attr)namedNodeMap.item(b);
        String str1 = attr.getName();
        String str2 = attr.getValue();
        if (str2 == null)
          str2 = ""; 
        printAttribute(str1, str2, attr.getSpecified(), attr);
      } 
    } else {
      byte b;
      for (b = 0; b < i; b++) {
        Attr attr = (Attr)namedNodeMap.item(b);
        String str3 = attr.getNamespaceURI();
        if (str3 != null && str3.equals(NamespaceContext.XMLNS_URI)) {
          String str4 = attr.getNodeValue();
          if (str4 == null)
            str4 = XMLSymbols.EMPTY_STRING; 
          if (str4.equals(NamespaceContext.XMLNS_URI)) {
            if (this.fDOMErrorHandler != null) {
              String str5 = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "CantBindXMLNS", null);
              modifyDOMError(str5, (short)2, null, attr);
              boolean bool = this.fDOMErrorHandler.handleError(this.fDOMError);
              if (!bool)
                throw new RuntimeException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "SerializationStopped", null)); 
            } 
          } else {
            String str5 = attr.getPrefix();
            str5 = (str5 == null || str5.length() == 0) ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(str5);
            String str6 = this.fSymbolTable.addSymbol(attr.getLocalName());
            if (str5 == XMLSymbols.PREFIX_XMLNS) {
              str4 = this.fSymbolTable.addSymbol(str4);
              if (str4.length() != 0)
                this.fNSBinder.declarePrefix(str6, str4); 
            } else {
              str4 = this.fSymbolTable.addSymbol(str4);
              this.fNSBinder.declarePrefix(XMLSymbols.EMPTY_STRING, str4);
            } 
          } 
        } 
      } 
      String str2 = paramElement.getNamespaceURI();
      String str1 = paramElement.getPrefix();
      if (str2 != null && str1 != null && str2.length() == 0 && str1.length() != 0) {
        str1 = null;
        this._printer.printText('<');
        this._printer.printText(paramElement.getLocalName());
        this._printer.indent();
      } else {
        this._printer.printText('<');
        this._printer.printText(str);
        this._printer.indent();
      } 
      if (str2 != null) {
        str2 = this.fSymbolTable.addSymbol(str2);
        str1 = (str1 == null || str1.length() == 0) ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(str1);
        if (this.fNSBinder.getURI(str1) != str2) {
          if (this.fNamespacePrefixes)
            printNamespaceAttr(str1, str2); 
          this.fLocalNSBinder.declarePrefix(str1, str2);
          this.fNSBinder.declarePrefix(str1, str2);
        } 
      } else if (paramElement.getLocalName() == null) {
        if (this.fDOMErrorHandler != null) {
          String str3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NullLocalElementName", new Object[] { paramElement.getNodeName() });
          modifyDOMError(str3, (short)2, null, paramElement);
          boolean bool = this.fDOMErrorHandler.handleError(this.fDOMError);
          if (!bool)
            throw new RuntimeException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "SerializationStopped", null)); 
        } 
      } else {
        str2 = this.fNSBinder.getURI(XMLSymbols.EMPTY_STRING);
        if (str2 != null && str2.length() > 0) {
          if (this.fNamespacePrefixes)
            printNamespaceAttr(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING); 
          this.fLocalNSBinder.declarePrefix(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING);
          this.fNSBinder.declarePrefix(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING);
        } 
      } 
      for (b = 0; b < i; b++) {
        Attr attr = (Attr)namedNodeMap.item(b);
        String str4 = attr.getValue();
        String str3 = attr.getNodeName();
        str2 = attr.getNamespaceURI();
        if (str2 != null && str2.length() == 0) {
          str2 = null;
          str3 = attr.getLocalName();
        } 
        if (str4 == null)
          str4 = XMLSymbols.EMPTY_STRING; 
        if (str2 != null) {
          str1 = attr.getPrefix();
          str1 = (str1 == null) ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(str1);
          String str5 = this.fSymbolTable.addSymbol(attr.getLocalName());
          if (str2 != null && str2.equals(NamespaceContext.XMLNS_URI)) {
            str1 = attr.getPrefix();
            str1 = (str1 == null || str1.length() == 0) ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(str1);
            str5 = this.fSymbolTable.addSymbol(attr.getLocalName());
            if (str1 == XMLSymbols.PREFIX_XMLNS) {
              String str6 = this.fLocalNSBinder.getURI(str5);
              str4 = this.fSymbolTable.addSymbol(str4);
              if (str4.length() != 0 && str6 == null) {
                if (this.fNamespacePrefixes)
                  printNamespaceAttr(str5, str4); 
                this.fLocalNSBinder.declarePrefix(str5, str4);
              } 
            } else {
              str2 = this.fNSBinder.getURI(XMLSymbols.EMPTY_STRING);
              String str6 = this.fLocalNSBinder.getURI(XMLSymbols.EMPTY_STRING);
              str4 = this.fSymbolTable.addSymbol(str4);
              if (str6 == null && this.fNamespacePrefixes)
                printNamespaceAttr(XMLSymbols.EMPTY_STRING, str4); 
            } 
          } else {
            str2 = this.fSymbolTable.addSymbol(str2);
            String str6 = this.fNSBinder.getURI(str1);
            if (str1 == XMLSymbols.EMPTY_STRING || str6 != str2) {
              str3 = attr.getNodeName();
              String str7 = this.fNSBinder.getPrefix(str2);
              if (str7 != null && str7 != XMLSymbols.EMPTY_STRING) {
                str1 = str7;
                str3 = str1 + ":" + str5;
              } else {
                if (str1 == XMLSymbols.EMPTY_STRING || this.fLocalNSBinder.getURI(str1) != null) {
                  byte b1 = 1;
                  for (str1 = this.fSymbolTable.addSymbol("NS" + b1++); this.fLocalNSBinder.getURI(str1) != null; str1 = this.fSymbolTable.addSymbol("NS" + b1++));
                  str3 = str1 + ":" + str5;
                } 
                if (this.fNamespacePrefixes)
                  printNamespaceAttr(str1, str2); 
                str4 = this.fSymbolTable.addSymbol(str4);
                this.fLocalNSBinder.declarePrefix(str1, str4);
                this.fNSBinder.declarePrefix(str1, str2);
              } 
            } 
            printAttribute(str3, (str4 == null) ? XMLSymbols.EMPTY_STRING : str4, attr.getSpecified(), attr);
          } 
        } else if (attr.getLocalName() == null) {
          if (this.fDOMErrorHandler != null) {
            String str5 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NullLocalAttrName", new Object[] { attr.getNodeName() });
            modifyDOMError(str5, (short)2, null, attr);
            boolean bool = this.fDOMErrorHandler.handleError(this.fDOMError);
            if (!bool)
              throw new RuntimeException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "SerializationStopped", null)); 
          } 
          printAttribute(str3, str4, attr.getSpecified(), attr);
        } else {
          printAttribute(str3, str4, attr.getSpecified(), attr);
        } 
      } 
    } 
    if (paramElement.hasChildNodes()) {
      elementState = enterElementState(null, null, str, this.fPreserveSpace);
      elementState.doCData = this._format.isCDataElement(str);
      elementState.unescaped = this._format.isNonEscapingElement(str);
      for (Node node = paramElement.getFirstChild(); node != null; node = node.getNextSibling())
        serializeNode(node); 
      if (this.fNamespaces)
        this.fNSBinder.popContext(); 
      endElementIO(null, null, str);
    } else {
      if (this.fNamespaces)
        this.fNSBinder.popContext(); 
      this._printer.unindent();
      this._printer.printText("/>");
      elementState.afterElement = true;
      elementState.afterComment = false;
      elementState.empty = false;
      if (isDocumentState())
        this._printer.flush(); 
    } 
  }
  
  private void printNamespaceAttr(String paramString1, String paramString2) throws IOException {
    this._printer.printSpace();
    if (paramString1 == XMLSymbols.EMPTY_STRING) {
      this._printer.printText(XMLSymbols.PREFIX_XMLNS);
    } else {
      this._printer.printText("xmlns:" + paramString1);
    } 
    this._printer.printText("=\"");
    printEscaped(paramString2);
    this._printer.printText('"');
  }
  
  private void printAttribute(String paramString1, String paramString2, boolean paramBoolean, Attr paramAttr) throws IOException {
    if (paramBoolean || (this.features & 0x40) == 0) {
      if (this.fDOMFilter != null && (this.fDOMFilter.getWhatToShow() & 0x2) != 0) {
        short s = this.fDOMFilter.acceptNode(paramAttr);
        switch (s) {
          case 2:
          case 3:
            return;
        } 
      } 
      this._printer.printSpace();
      this._printer.printText(paramString1);
      this._printer.printText("=\"");
      printEscaped(paramString2);
      this._printer.printText('"');
    } 
    if (paramString1.equals("xml:space"))
      if (paramString2.equals("preserve")) {
        this.fPreserveSpace = true;
      } else {
        this.fPreserveSpace = this._format.getPreserveSpace();
      }  
  }
  
  protected String getEntityRef(int paramInt) {
    switch (paramInt) {
      case 60:
        return "lt";
      case 62:
        return "gt";
      case 34:
        return "quot";
      case 39:
        return "apos";
      case 38:
        return "amp";
    } 
    return null;
  }
  
  private Attributes extractNamespaces(Attributes paramAttributes) throws SAXException {
    if (paramAttributes == null)
      return null; 
    int j = paramAttributes.getLength();
    AttributesImpl attributesImpl = new AttributesImpl(paramAttributes);
    for (int i = j - 1; i >= 0; i--) {
      String str = attributesImpl.getQName(i);
      if (str.startsWith("xmlns"))
        if (str.length() == 5) {
          startPrefixMapping("", paramAttributes.getValue(i));
          attributesImpl.removeAttribute(i);
        } else if (str.charAt(5) == ':') {
          startPrefixMapping(str.substring(6), paramAttributes.getValue(i));
          attributesImpl.removeAttribute(i);
        }  
    } 
    return attributesImpl;
  }
  
  protected void printEscaped(String paramString) throws SAXException {
    int i = paramString.length();
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (!XMLChar.isValid(c)) {
        if (++b < i) {
          surrogates(c, paramString.charAt(b));
        } else {
          fatalError("The character '" + (char)c + "' is an invalid XML character");
        } 
      } else if (c == '\n' || c == '\r' || c == '\t') {
        printHex(c);
      } else if (c == '<') {
        this._printer.printText("&lt;");
      } else if (c == '&') {
        this._printer.printText("&amp;");
      } else if (c == '"') {
        this._printer.printText("&quot;");
      } else if (c >= ' ' && this._encodingInfo.isPrintable((char)c)) {
        this._printer.printText((char)c);
      } else {
        printHex(c);
      } 
    } 
  }
  
  protected void printXMLChar(int paramInt) throws IOException {
    if (paramInt == 13) {
      printHex(paramInt);
    } else if (paramInt == 60) {
      this._printer.printText("&lt;");
    } else if (paramInt == 38) {
      this._printer.printText("&amp;");
    } else if (paramInt == 62) {
      this._printer.printText("&gt;");
    } else if (paramInt == 10 || paramInt == 9 || (paramInt >= 32 && this._encodingInfo.isPrintable((char)paramInt))) {
      this._printer.printText((char)paramInt);
    } else {
      printHex(paramInt);
    } 
  }
  
  protected void printText(String paramString, boolean paramBoolean1, boolean paramBoolean2) throws IOException {
    int i = paramString.length();
    if (paramBoolean1) {
      for (byte b = 0; b < i; b++) {
        char c = paramString.charAt(b);
        if (!XMLChar.isValid(c)) {
          if (++b < i) {
            surrogates(c, paramString.charAt(b));
          } else {
            fatalError("The character '" + c + "' is an invalid XML character");
          } 
        } else if (paramBoolean2) {
          this._printer.printText(c);
        } else {
          printXMLChar(c);
        } 
      } 
    } else {
      for (byte b = 0; b < i; b++) {
        char c = paramString.charAt(b);
        if (!XMLChar.isValid(c)) {
          if (++b < i) {
            surrogates(c, paramString.charAt(b));
          } else {
            fatalError("The character '" + c + "' is an invalid XML character");
          } 
        } else if (paramBoolean2) {
          this._printer.printText(c);
        } else {
          printXMLChar(c);
        } 
      } 
    } 
  }
  
  protected void printText(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2) throws IOException {
    if (paramBoolean1) {
      while (paramInt2-- > 0) {
        char c = paramArrayOfChar[paramInt1++];
        if (!XMLChar.isValid(c)) {
          if (paramInt2-- > 0) {
            surrogates(c, paramArrayOfChar[paramInt1++]);
            continue;
          } 
          fatalError("The character '" + c + "' is an invalid XML character");
          continue;
        } 
        if (paramBoolean2) {
          this._printer.printText(c);
          continue;
        } 
        printXMLChar(c);
      } 
    } else {
      while (paramInt2-- > 0) {
        char c = paramArrayOfChar[paramInt1++];
        if (!XMLChar.isValid(c)) {
          if (paramInt2-- > 0) {
            surrogates(c, paramArrayOfChar[paramInt1++]);
            continue;
          } 
          fatalError("The character '" + c + "' is an invalid XML character");
          continue;
        } 
        if (paramBoolean2) {
          this._printer.printText(c);
          continue;
        } 
        printXMLChar(c);
      } 
    } 
  }
  
  protected void checkUnboundNamespacePrefixedNode(Node paramNode) throws IOException {
    if (this.fNamespaces)
      for (Node node = paramNode.getFirstChild(); node != null; node = node1) {
        Node node1 = node.getNextSibling();
        String str = node.getPrefix();
        str = (str == null || str.length() == 0) ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(str);
        if (this.fNSBinder.getURI(str) == null && str != null)
          fatalError("The replacement text of the entity node '" + paramNode.getNodeName() + "' contains an element node '" + node.getNodeName() + "' with an undeclared prefix '" + str + "'."); 
        if (node.getNodeType() == 1) {
          NamedNodeMap namedNodeMap = node.getAttributes();
          for (byte b = 0; b < namedNodeMap.getLength(); b++) {
            String str1 = namedNodeMap.item(b).getPrefix();
            str1 = (str1 == null || str1.length() == 0) ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(str1);
            if (this.fNSBinder.getURI(str1) == null && str1 != null)
              fatalError("The replacement text of the entity node '" + paramNode.getNodeName() + "' contains an element node '" + node.getNodeName() + "' with an attribute '" + namedNodeMap.item(b).getNodeName() + "' an undeclared prefix '" + str1 + "'."); 
          } 
        } 
        if (node.hasChildNodes())
          checkUnboundNamespacePrefixedNode(node); 
      }  
  }
  
  public boolean reset() {
    super.reset();
    if (this.fNSBinder != null) {
      this.fNSBinder.reset();
      this.fNSBinder.declarePrefix(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING);
    } 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serialize\XMLSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */