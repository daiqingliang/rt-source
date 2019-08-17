package com.sun.org.apache.xml.internal.serialize;

import com.sun.org.apache.xerces.internal.dom.DOMErrorImpl;
import com.sun.org.apache.xerces.internal.dom.DOMLocatorImpl;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.ls.LSSerializerFilter;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

public abstract class BaseMarkupSerializer implements ContentHandler, DocumentHandler, LexicalHandler, DTDHandler, DeclHandler, DOMSerializer, Serializer {
  protected short features = -1;
  
  protected DOMErrorHandler fDOMErrorHandler;
  
  protected final DOMErrorImpl fDOMError = new DOMErrorImpl();
  
  protected LSSerializerFilter fDOMFilter;
  
  protected EncodingInfo _encodingInfo;
  
  private ElementState[] _elementStates = new ElementState[10];
  
  private int _elementStateCount;
  
  private Vector _preRoot;
  
  protected boolean _started;
  
  private boolean _prepared;
  
  protected Map<String, String> _prefixes;
  
  protected String _docTypePublicId;
  
  protected String _docTypeSystemId;
  
  protected OutputFormat _format;
  
  protected Printer _printer;
  
  protected boolean _indenting;
  
  protected final StringBuffer fStrBuffer = new StringBuffer(40);
  
  private Writer _writer;
  
  private OutputStream _output;
  
  protected Node fCurrentNode = null;
  
  protected BaseMarkupSerializer(OutputFormat paramOutputFormat) {
    for (byte b = 0; b < this._elementStates.length; b++)
      this._elementStates[b] = new ElementState(); 
    this._format = paramOutputFormat;
  }
  
  public DocumentHandler asDocumentHandler() throws IOException {
    prepare();
    return this;
  }
  
  public ContentHandler asContentHandler() throws IOException {
    prepare();
    return this;
  }
  
  public DOMSerializer asDOMSerializer() throws IOException {
    prepare();
    return this;
  }
  
  public void setOutputByteStream(OutputStream paramOutputStream) {
    if (paramOutputStream == null) {
      String str = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "ArgumentIsNull", new Object[] { "output" });
      throw new NullPointerException(str);
    } 
    this._output = paramOutputStream;
    this._writer = null;
    reset();
  }
  
  public void setOutputCharStream(Writer paramWriter) {
    if (paramWriter == null) {
      String str = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "ArgumentIsNull", new Object[] { "writer" });
      throw new NullPointerException(str);
    } 
    this._writer = paramWriter;
    this._output = null;
    reset();
  }
  
  public void setOutputFormat(OutputFormat paramOutputFormat) {
    if (paramOutputFormat == null) {
      String str = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "ArgumentIsNull", new Object[] { "format" });
      throw new NullPointerException(str);
    } 
    this._format = paramOutputFormat;
    reset();
  }
  
  public boolean reset() {
    if (this._elementStateCount > 1) {
      String str = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "ResetInMiddle", null);
      throw new IllegalStateException(str);
    } 
    this._prepared = false;
    this.fCurrentNode = null;
    this.fStrBuffer.setLength(0);
    return true;
  }
  
  protected void prepare() throws IOException {
    if (this._prepared)
      return; 
    if (this._writer == null && this._output == null) {
      String str = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "NoWriterSupplied", null);
      throw new IOException(str);
    } 
    this._encodingInfo = this._format.getEncodingInfo();
    if (this._output != null)
      this._writer = this._encodingInfo.getWriter(this._output); 
    if (this._format.getIndenting()) {
      this._indenting = true;
      this._printer = new IndentPrinter(this._writer, this._format);
    } else {
      this._indenting = false;
      this._printer = new Printer(this._writer, this._format);
    } 
    this._elementStateCount = 0;
    ElementState elementState = this._elementStates[0];
    elementState.namespaceURI = null;
    elementState.localName = null;
    elementState.rawName = null;
    elementState.preserveSpace = this._format.getPreserveSpace();
    elementState.empty = true;
    elementState.afterElement = false;
    elementState.afterComment = false;
    elementState.doCData = elementState.inCData = false;
    elementState.prefixes = null;
    this._docTypePublicId = this._format.getDoctypePublic();
    this._docTypeSystemId = this._format.getDoctypeSystem();
    this._started = false;
    this._prepared = true;
  }
  
  public void serialize(Element paramElement) throws IOException {
    reset();
    prepare();
    serializeNode(paramElement);
    this._printer.flush();
    if (this._printer.getException() != null)
      throw this._printer.getException(); 
  }
  
  public void serialize(Node paramNode) throws IOException {
    reset();
    prepare();
    serializeNode(paramNode);
    serializePreRoot();
    this._printer.flush();
    if (this._printer.getException() != null)
      throw this._printer.getException(); 
  }
  
  public void serialize(DocumentFragment paramDocumentFragment) throws IOException {
    reset();
    prepare();
    serializeNode(paramDocumentFragment);
    this._printer.flush();
    if (this._printer.getException() != null)
      throw this._printer.getException(); 
  }
  
  public void serialize(Document paramDocument) throws IOException {
    reset();
    prepare();
    serializeNode(paramDocument);
    serializePreRoot();
    this._printer.flush();
    if (this._printer.getException() != null)
      throw this._printer.getException(); 
  }
  
  public void startDocument() throws IOException {
    try {
      prepare();
    } catch (IOException iOException) {
      throw new SAXException(iOException.toString());
    } 
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    try {
      ElementState elementState = content();
      if (elementState.inCData || elementState.doCData) {
        if (!elementState.inCData) {
          this._printer.printText("<![CDATA[");
          elementState.inCData = true;
        } 
        int i = this._printer.getNextIndent();
        this._printer.setNextIndent(0);
        int j = paramInt1 + paramInt2;
        for (int k = paramInt1; k < j; k++) {
          char c = paramArrayOfChar[k];
          if (c == ']' && k + 2 < j && paramArrayOfChar[k + 1] == ']' && paramArrayOfChar[k + 2] == '>') {
            this._printer.printText("]]]]><![CDATA[>");
            k += 2;
          } else if (!XMLChar.isValid(c)) {
            if (++k < j) {
              surrogates(c, paramArrayOfChar[k]);
            } else {
              fatalError("The character '" + c + "' is an invalid XML character");
            } 
          } else if ((c >= ' ' && this._encodingInfo.isPrintable(c) && c != '÷') || c == '\n' || c == '\r' || c == '\t') {
            this._printer.printText(c);
          } else {
            this._printer.printText("]]>&#x");
            this._printer.printText(Integer.toHexString(c));
            this._printer.printText(";<![CDATA[");
          } 
        } 
        this._printer.setNextIndent(i);
      } else if (elementState.preserveSpace) {
        int i = this._printer.getNextIndent();
        this._printer.setNextIndent(0);
        printText(paramArrayOfChar, paramInt1, paramInt2, true, elementState.unescaped);
        this._printer.setNextIndent(i);
      } else {
        printText(paramArrayOfChar, paramInt1, paramInt2, false, elementState.unescaped);
      } 
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    try {
      content();
      if (this._indenting) {
        this._printer.setThisIndent(0);
        for (int i = paramInt1; paramInt2-- > 0; i++)
          this._printer.printText(paramArrayOfChar[i]); 
      } 
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public final void processingInstruction(String paramString1, String paramString2) throws SAXException {
    try {
      processingInstructionIO(paramString1, paramString2);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void processingInstructionIO(String paramString1, String paramString2) throws SAXException {
    ElementState elementState = content();
    int i = paramString1.indexOf("?>");
    if (i >= 0) {
      this.fStrBuffer.append("<?").append(paramString1.substring(0, i));
    } else {
      this.fStrBuffer.append("<?").append(paramString1);
    } 
    if (paramString2 != null) {
      this.fStrBuffer.append(' ');
      i = paramString2.indexOf("?>");
      if (i >= 0) {
        this.fStrBuffer.append(paramString2.substring(0, i));
      } else {
        this.fStrBuffer.append(paramString2);
      } 
    } 
    this.fStrBuffer.append("?>");
    if (isDocumentState()) {
      if (this._preRoot == null)
        this._preRoot = new Vector(); 
      this._preRoot.addElement(this.fStrBuffer.toString());
    } else {
      this._printer.indent();
      printText(this.fStrBuffer.toString(), true, true);
      this._printer.unindent();
      if (this._indenting)
        elementState.afterElement = true; 
    } 
    this.fStrBuffer.setLength(0);
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    try {
      comment(new String(paramArrayOfChar, paramInt1, paramInt2));
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void comment(String paramString) throws IOException {
    if (this._format.getOmitComments())
      return; 
    ElementState elementState = content();
    int i = paramString.indexOf("-->");
    if (i >= 0) {
      this.fStrBuffer.append("<!--").append(paramString.substring(0, i)).append("-->");
    } else {
      this.fStrBuffer.append("<!--").append(paramString).append("-->");
    } 
    if (isDocumentState()) {
      if (this._preRoot == null)
        this._preRoot = new Vector(); 
      this._preRoot.addElement(this.fStrBuffer.toString());
    } else {
      if (this._indenting && !elementState.preserveSpace)
        this._printer.breakLine(); 
      this._printer.indent();
      printText(this.fStrBuffer.toString(), true, true);
      this._printer.unindent();
      if (this._indenting)
        elementState.afterElement = true; 
    } 
    this.fStrBuffer.setLength(0);
    elementState.afterComment = true;
    elementState.afterElement = false;
  }
  
  public void startCDATA() throws IOException {
    ElementState elementState = getElementState();
    elementState.doCData = true;
  }
  
  public void endCDATA() throws IOException {
    ElementState elementState = getElementState();
    elementState.doCData = false;
  }
  
  public void startNonEscaping() throws IOException {
    ElementState elementState = getElementState();
    elementState.unescaped = true;
  }
  
  public void endNonEscaping() throws IOException {
    ElementState elementState = getElementState();
    elementState.unescaped = false;
  }
  
  public void startPreserving() throws IOException {
    ElementState elementState = getElementState();
    elementState.preserveSpace = true;
  }
  
  public void endPreserving() throws IOException {
    ElementState elementState = getElementState();
    elementState.preserveSpace = false;
  }
  
  public void endDocument() throws IOException {
    try {
      serializePreRoot();
      this._printer.flush();
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void startEntity(String paramString) throws IOException {}
  
  public void endEntity(String paramString) throws IOException {}
  
  public void setDocumentLocator(Locator paramLocator) {}
  
  public void skippedEntity(String paramString) throws IOException {
    try {
      endCDATA();
      content();
      this._printer.printText('&');
      this._printer.printText(paramString);
      this._printer.printText(';');
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException {
    if (this._prefixes == null)
      this._prefixes = new HashMap(); 
    this._prefixes.put(paramString2, (paramString1 == null) ? "" : paramString1);
  }
  
  public void endPrefixMapping(String paramString) throws IOException {}
  
  public final void startDTD(String paramString1, String paramString2, String paramString3) throws SAXException {
    try {
      this._printer.enterDTD();
      this._docTypePublicId = paramString2;
      this._docTypeSystemId = paramString3;
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void endDTD() throws IOException {}
  
  public void elementDecl(String paramString1, String paramString2) throws SAXException {
    try {
      this._printer.enterDTD();
      this._printer.printText("<!ELEMENT ");
      this._printer.printText(paramString1);
      this._printer.printText(' ');
      this._printer.printText(paramString2);
      this._printer.printText('>');
      if (this._indenting)
        this._printer.breakLine(); 
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void attributeDecl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) throws SAXException {
    try {
      this._printer.enterDTD();
      this._printer.printText("<!ATTLIST ");
      this._printer.printText(paramString1);
      this._printer.printText(' ');
      this._printer.printText(paramString2);
      this._printer.printText(' ');
      this._printer.printText(paramString3);
      if (paramString4 != null) {
        this._printer.printText(' ');
        this._printer.printText(paramString4);
      } 
      if (paramString5 != null) {
        this._printer.printText(" \"");
        printEscaped(paramString5);
        this._printer.printText('"');
      } 
      this._printer.printText('>');
      if (this._indenting)
        this._printer.breakLine(); 
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void internalEntityDecl(String paramString1, String paramString2) throws SAXException {
    try {
      this._printer.enterDTD();
      this._printer.printText("<!ENTITY ");
      this._printer.printText(paramString1);
      this._printer.printText(" \"");
      printEscaped(paramString2);
      this._printer.printText("\">");
      if (this._indenting)
        this._printer.breakLine(); 
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void externalEntityDecl(String paramString1, String paramString2, String paramString3) throws SAXException {
    try {
      this._printer.enterDTD();
      unparsedEntityDecl(paramString1, paramString2, paramString3, null);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4) throws SAXException {
    try {
      this._printer.enterDTD();
      if (paramString2 == null) {
        this._printer.printText("<!ENTITY ");
        this._printer.printText(paramString1);
        this._printer.printText(" SYSTEM ");
        printDoctypeURL(paramString3);
      } else {
        this._printer.printText("<!ENTITY ");
        this._printer.printText(paramString1);
        this._printer.printText(" PUBLIC ");
        printDoctypeURL(paramString2);
        this._printer.printText(' ');
        printDoctypeURL(paramString3);
      } 
      if (paramString4 != null) {
        this._printer.printText(" NDATA ");
        this._printer.printText(paramString4);
      } 
      this._printer.printText('>');
      if (this._indenting)
        this._printer.breakLine(); 
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3) throws SAXException {
    try {
      this._printer.enterDTD();
      if (paramString2 != null) {
        this._printer.printText("<!NOTATION ");
        this._printer.printText(paramString1);
        this._printer.printText(" PUBLIC ");
        printDoctypeURL(paramString2);
        if (paramString3 != null) {
          this._printer.printText(' ');
          printDoctypeURL(paramString3);
        } 
      } else {
        this._printer.printText("<!NOTATION ");
        this._printer.printText(paramString1);
        this._printer.printText(" SYSTEM ");
        printDoctypeURL(paramString3);
      } 
      this._printer.printText('>');
      if (this._indenting)
        this._printer.breakLine(); 
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  protected void serializeNode(Node paramNode) throws IOException {
    String str;
    Node node;
    this.fCurrentNode = paramNode;
    switch (paramNode.getNodeType()) {
      case 3:
        str = paramNode.getNodeValue();
        if (str != null) {
          if (this.fDOMFilter != null && (this.fDOMFilter.getWhatToShow() & 0x4) != 0) {
            short s = this.fDOMFilter.acceptNode(paramNode);
            switch (s) {
              case 2:
              case 3:
                break;
            } 
            characters(str);
            break;
          } 
          if (!this._indenting || (getElementState()).preserveSpace || str.replace('\n', ' ').trim().length() != 0)
            characters(str); 
        } 
        break;
      case 4:
        str = paramNode.getNodeValue();
        if ((this.features & 0x8) != 0) {
          if (str != null) {
            if (this.fDOMFilter != null && (this.fDOMFilter.getWhatToShow() & 0x8) != 0) {
              short s = this.fDOMFilter.acceptNode(paramNode);
              switch (s) {
                case 2:
                case 3:
                  return;
              } 
            } 
            startCDATA();
            characters(str);
            endCDATA();
          } 
          break;
        } 
        characters(str);
        break;
      case 8:
        if (!this._format.getOmitComments()) {
          str = paramNode.getNodeValue();
          if (str != null) {
            if (this.fDOMFilter != null && (this.fDOMFilter.getWhatToShow() & 0x80) != 0) {
              short s = this.fDOMFilter.acceptNode(paramNode);
              switch (s) {
                case 2:
                case 3:
                  return;
              } 
            } 
            comment(str);
          } 
        } 
        break;
      case 5:
        endCDATA();
        content();
        if ((this.features & 0x4) != 0 || paramNode.getFirstChild() == null) {
          if (this.fDOMFilter != null && (this.fDOMFilter.getWhatToShow() & 0x10) != 0) {
            Node node1;
            short s = this.fDOMFilter.acceptNode(paramNode);
            switch (s) {
              case 2:
                return;
              case 3:
                for (node1 = paramNode.getFirstChild(); node1 != null; node1 = node1.getNextSibling())
                  serializeNode(node1); 
                return;
            } 
          } 
          checkUnboundNamespacePrefixedNode(paramNode);
          this._printer.printText("&");
          this._printer.printText(paramNode.getNodeName());
          this._printer.printText(";");
          break;
        } 
        for (node = paramNode.getFirstChild(); node != null; node = node.getNextSibling())
          serializeNode(node); 
        break;
      case 7:
        if (this.fDOMFilter != null && (this.fDOMFilter.getWhatToShow() & 0x40) != 0) {
          short s = this.fDOMFilter.acceptNode(paramNode);
          switch (s) {
            case 2:
            case 3:
              return;
          } 
        } 
        processingInstructionIO(paramNode.getNodeName(), paramNode.getNodeValue());
        break;
      case 1:
        if (this.fDOMFilter != null && (this.fDOMFilter.getWhatToShow() & true) != 0) {
          Node node1;
          short s = this.fDOMFilter.acceptNode(paramNode);
          switch (s) {
            case 2:
              return;
            case 3:
              for (node1 = paramNode.getFirstChild(); node1 != null; node1 = node1.getNextSibling())
                serializeNode(node1); 
              return;
          } 
        } 
        serializeElement((Element)paramNode);
        break;
      case 9:
        serializeDocument();
        node = ((Document)paramNode).getDoctype();
        if (node != null) {
          DOMImplementation dOMImplementation = ((Document)paramNode).getImplementation();
          try {
            this._printer.enterDTD();
            this._docTypePublicId = node.getPublicId();
            this._docTypeSystemId = node.getSystemId();
            String str1 = node.getInternalSubset();
            if (str1 != null && str1.length() > 0)
              this._printer.printText(str1); 
            endDTD();
          } catch (NoSuchMethodError noSuchMethodError) {
            Class clazz = node.getClass();
            String str1 = null;
            String str2 = null;
            try {
              Method method = clazz.getMethod("getPublicId", (Class[])null);
              if (method.getReturnType().equals(String.class))
                str1 = (String)method.invoke(node, (Object[])null); 
            } catch (Exception exception) {}
            try {
              Method method = clazz.getMethod("getSystemId", (Class[])null);
              if (method.getReturnType().equals(String.class))
                str2 = (String)method.invoke(node, (Object[])null); 
            } catch (Exception exception) {}
            this._printer.enterDTD();
            this._docTypePublicId = str1;
            this._docTypeSystemId = str2;
            endDTD();
          } 
          serializeDTD(node.getName());
        } 
        this._started = true;
      case 11:
        for (node = paramNode.getFirstChild(); node != null; node = node.getNextSibling())
          serializeNode(node); 
        break;
    } 
  }
  
  protected void serializeDocument() throws IOException {
    String str = this._printer.leaveDTD();
    if (!this._started && !this._format.getOmitXMLDeclaration()) {
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
    serializePreRoot();
  }
  
  protected void serializeDTD(String paramString) throws IOException {
    String str = this._printer.leaveDTD();
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
  
  protected ElementState content() throws IOException {
    ElementState elementState = getElementState();
    if (!isDocumentState()) {
      if (elementState.inCData && !elementState.doCData) {
        this._printer.printText("]]>");
        elementState.inCData = false;
      } 
      if (elementState.empty) {
        this._printer.printText('>');
        elementState.empty = false;
      } 
      elementState.afterElement = false;
      elementState.afterComment = false;
    } 
    return elementState;
  }
  
  protected void characters(String paramString) throws IOException {
    ElementState elementState = content();
    if (elementState.inCData || elementState.doCData) {
      if (!elementState.inCData) {
        this._printer.printText("<![CDATA[");
        elementState.inCData = true;
      } 
      int i = this._printer.getNextIndent();
      this._printer.setNextIndent(0);
      printCDATAText(paramString);
      this._printer.setNextIndent(i);
    } else if (elementState.preserveSpace) {
      int i = this._printer.getNextIndent();
      this._printer.setNextIndent(0);
      printText(paramString, true, elementState.unescaped);
      this._printer.setNextIndent(i);
    } else {
      printText(paramString, false, elementState.unescaped);
    } 
  }
  
  protected abstract String getEntityRef(int paramInt);
  
  protected abstract void serializeElement(Element paramElement) throws IOException;
  
  protected void serializePreRoot() throws IOException {
    if (this._preRoot != null) {
      for (byte b = 0; b < this._preRoot.size(); b++) {
        printText((String)this._preRoot.elementAt(b), true, true);
        if (this._indenting)
          this._printer.breakLine(); 
      } 
      this._preRoot.removeAllElements();
    } 
  }
  
  protected void printCDATAText(String paramString) throws IOException {
    int i = paramString.length();
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (c == ']' && b + 2 < i && paramString.charAt(b + 1) == ']' && paramString.charAt(b + 2) == '>') {
        if (this.fDOMErrorHandler != null)
          if ((this.features & 0x10) == 0) {
            String str = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "EndingCDATA", null);
            if ((this.features & 0x2) != 0) {
              modifyDOMError(str, (short)3, "wf-invalid-character", this.fCurrentNode);
              this.fDOMErrorHandler.handleError(this.fDOMError);
              throw new LSException((short)82, str);
            } 
            modifyDOMError(str, (short)2, "cdata-section-not-splitted", this.fCurrentNode);
            if (!this.fDOMErrorHandler.handleError(this.fDOMError))
              throw new LSException((short)82, str); 
          } else {
            String str = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "SplittingCDATA", null);
            modifyDOMError(str, (short)1, null, this.fCurrentNode);
            this.fDOMErrorHandler.handleError(this.fDOMError);
          }  
        this._printer.printText("]]]]><![CDATA[>");
        b += 2;
      } else if (!XMLChar.isValid(c)) {
        if (++b < i) {
          surrogates(c, paramString.charAt(b));
        } else {
          fatalError("The character '" + c + "' is an invalid XML character");
        } 
      } else if ((c >= ' ' && this._encodingInfo.isPrintable(c) && c != '÷') || c == '\n' || c == '\r' || c == '\t') {
        this._printer.printText(c);
      } else {
        this._printer.printText("]]>&#x");
        this._printer.printText(Integer.toHexString(c));
        this._printer.printText(";<![CDATA[");
      } 
    } 
  }
  
  protected void surrogates(int paramInt1, int paramInt2) throws IOException {
    if (XMLChar.isHighSurrogate(paramInt1)) {
      if (!XMLChar.isLowSurrogate(paramInt2)) {
        fatalError("The character '" + (char)paramInt2 + "' is an invalid XML character");
      } else {
        int i = XMLChar.supplemental((char)paramInt1, (char)paramInt2);
        if (!XMLChar.isValid(i)) {
          fatalError("The character '" + (char)i + "' is an invalid XML character");
        } else if ((content()).inCData) {
          this._printer.printText("]]>&#x");
          this._printer.printText(Integer.toHexString(i));
          this._printer.printText(";<![CDATA[");
        } else {
          printHex(i);
        } 
      } 
    } else {
      fatalError("The character '" + (char)paramInt1 + "' is an invalid XML character");
    } 
  }
  
  protected void printText(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2) throws IOException {
    if (paramBoolean1) {
      while (paramInt2-- > 0) {
        char c = paramArrayOfChar[paramInt1];
        paramInt1++;
        if (c == '\n' || c == '\r' || paramBoolean2) {
          this._printer.printText(c);
          continue;
        } 
        printEscaped(c);
      } 
    } else {
      while (paramInt2-- > 0) {
        char c = paramArrayOfChar[paramInt1];
        paramInt1++;
        if (c == ' ' || c == '\f' || c == '\t' || c == '\n' || c == '\r') {
          this._printer.printSpace();
          continue;
        } 
        if (paramBoolean2) {
          this._printer.printText(c);
          continue;
        } 
        printEscaped(c);
      } 
    } 
  }
  
  protected void printText(String paramString, boolean paramBoolean1, boolean paramBoolean2) throws IOException {
    if (paramBoolean1) {
      for (byte b = 0; b < paramString.length(); b++) {
        char c = paramString.charAt(b);
        if (c == '\n' || c == '\r' || paramBoolean2) {
          this._printer.printText(c);
        } else {
          printEscaped(c);
        } 
      } 
    } else {
      for (byte b = 0; b < paramString.length(); b++) {
        char c = paramString.charAt(b);
        if (c == ' ' || c == '\f' || c == '\t' || c == '\n' || c == '\r') {
          this._printer.printSpace();
        } else if (paramBoolean2) {
          this._printer.printText(c);
        } else {
          printEscaped(c);
        } 
      } 
    } 
  }
  
  protected void printDoctypeURL(String paramString) throws IOException {
    this._printer.printText('"');
    for (byte b = 0; b < paramString.length(); b++) {
      if (paramString.charAt(b) == '"' || paramString.charAt(b) < ' ' || paramString.charAt(b) > '') {
        this._printer.printText('%');
        this._printer.printText(Integer.toHexString(paramString.charAt(b)));
      } else {
        this._printer.printText(paramString.charAt(b));
      } 
    } 
    this._printer.printText('"');
  }
  
  protected void printEscaped(int paramInt) throws IOException {
    String str = getEntityRef(paramInt);
    if (str != null) {
      this._printer.printText('&');
      this._printer.printText(str);
      this._printer.printText(';');
    } else if ((paramInt >= 32 && this._encodingInfo.isPrintable((char)paramInt) && paramInt != 247) || paramInt == 10 || paramInt == 13 || paramInt == 9) {
      if (paramInt < 65536) {
        this._printer.printText((char)paramInt);
      } else {
        this._printer.printText((char)((paramInt - 65536 >> 10) + 55296));
        this._printer.printText((char)((paramInt - 65536 & 0x3FF) + 56320));
      } 
    } else {
      printHex(paramInt);
    } 
  }
  
  final void printHex(int paramInt) throws IOException {
    this._printer.printText("&#x");
    this._printer.printText(Integer.toHexString(paramInt));
    this._printer.printText(';');
  }
  
  protected void printEscaped(String paramString) throws IOException {
    for (byte b = 0; b < paramString.length(); b++) {
      int i = paramString.charAt(b);
      if ((i & 0xFC00) == '?' && b + 1 < paramString.length()) {
        char c = paramString.charAt(b + 1);
        if ((c & 0xFC00) == '?') {
          i = 65536 + (i - '?' << '\n') + c - 56320;
          b++;
        } 
      } 
      printEscaped(i);
    } 
  }
  
  protected ElementState getElementState() throws IOException { return this._elementStates[this._elementStateCount]; }
  
  protected ElementState enterElementState(String paramString1, String paramString2, String paramString3, boolean paramBoolean) {
    if (this._elementStateCount + 1 == this._elementStates.length) {
      ElementState[] arrayOfElementState = new ElementState[this._elementStates.length + 10];
      int i;
      for (i = 0; i < this._elementStates.length; i++)
        arrayOfElementState[i] = this._elementStates[i]; 
      for (i = this._elementStates.length; i < arrayOfElementState.length; i++)
        arrayOfElementState[i] = new ElementState(); 
      this._elementStates = arrayOfElementState;
    } 
    this._elementStateCount++;
    ElementState elementState = this._elementStates[this._elementStateCount];
    elementState.namespaceURI = paramString1;
    elementState.localName = paramString2;
    elementState.rawName = paramString3;
    elementState.preserveSpace = paramBoolean;
    elementState.empty = true;
    elementState.afterElement = false;
    elementState.afterComment = false;
    elementState.doCData = elementState.inCData = false;
    elementState.unescaped = false;
    elementState.prefixes = this._prefixes;
    this._prefixes = null;
    return elementState;
  }
  
  protected ElementState leaveElementState() throws IOException {
    if (this._elementStateCount > 0) {
      this._prefixes = null;
      this._elementStateCount--;
      return this._elementStates[this._elementStateCount];
    } 
    String str = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "Internal", null);
    throw new IllegalStateException(str);
  }
  
  protected boolean isDocumentState() { return (this._elementStateCount == 0); }
  
  protected String getPrefix(String paramString) {
    if (this._prefixes != null) {
      String str = (String)this._prefixes.get(paramString);
      if (str != null)
        return str; 
    } 
    if (this._elementStateCount == 0)
      return null; 
    for (int i = this._elementStateCount; i > 0; i--) {
      if ((this._elementStates[i]).prefixes != null) {
        String str = (String)(this._elementStates[i]).prefixes.get(paramString);
        if (str != null)
          return str; 
      } 
    } 
    return null;
  }
  
  protected DOMError modifyDOMError(String paramString1, short paramShort, String paramString2, Node paramNode) {
    this.fDOMError.reset();
    this.fDOMError.fMessage = paramString1;
    this.fDOMError.fType = paramString2;
    this.fDOMError.fSeverity = paramShort;
    this.fDOMError.fLocator = new DOMLocatorImpl(-1, -1, -1, paramNode, null);
    return this.fDOMError;
  }
  
  protected void fatalError(String paramString) throws IOException {
    if (this.fDOMErrorHandler != null) {
      modifyDOMError(paramString, (short)3, null, this.fCurrentNode);
      this.fDOMErrorHandler.handleError(this.fDOMError);
    } else {
      throw new IOException(paramString);
    } 
  }
  
  protected void checkUnboundNamespacePrefixedNode(Node paramNode) throws IOException {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serialize\BaseMarkupSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */