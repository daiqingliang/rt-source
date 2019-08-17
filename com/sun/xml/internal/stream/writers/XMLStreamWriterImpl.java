package com.sun.xml.internal.stream.writers;

import com.sun.org.apache.xerces.internal.impl.PropertyManager;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.xml.internal.stream.util.ReadOnlyIterator;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamResult;

public final class XMLStreamWriterImpl extends AbstractMap implements XMLStreamWriter {
  public static final String START_COMMENT = "<!--";
  
  public static final String END_COMMENT = "-->";
  
  public static final String DEFAULT_ENCODING = " encoding=\"utf-8\"";
  
  public static final String DEFAULT_XMLDECL = "<?xml version=\"1.0\" ?>";
  
  public static final String DEFAULT_XML_VERSION = "1.0";
  
  public static final char CLOSE_START_TAG = '>';
  
  public static final char OPEN_START_TAG = '<';
  
  public static final String OPEN_END_TAG = "</";
  
  public static final char CLOSE_END_TAG = '>';
  
  public static final String START_CDATA = "<![CDATA[";
  
  public static final String END_CDATA = "]]>";
  
  public static final String CLOSE_EMPTY_ELEMENT = "/>";
  
  public static final String SPACE = " ";
  
  public static final String UTF_8 = "UTF-8";
  
  public static final String OUTPUTSTREAM_PROPERTY = "sjsxp-outputstream";
  
  boolean fEscapeCharacters = true;
  
  private boolean fIsRepairingNamespace = false;
  
  private Writer fWriter;
  
  private OutputStream fOutputStream = null;
  
  private ArrayList fAttributeCache;
  
  private ArrayList fNamespaceDecls;
  
  private NamespaceContextImpl fNamespaceContext = null;
  
  private NamespaceSupport fInternalNamespaceContext = null;
  
  private Random fPrefixGen = null;
  
  private PropertyManager fPropertyManager = null;
  
  private boolean fStartTagOpened = false;
  
  private boolean fReuse;
  
  private SymbolTable fSymbolTable = new SymbolTable();
  
  private ElementStack fElementStack = new ElementStack();
  
  private final String DEFAULT_PREFIX = this.fSymbolTable.addSymbol("");
  
  private final ReadOnlyIterator fReadOnlyIterator = new ReadOnlyIterator();
  
  private CharsetEncoder fEncoder = null;
  
  HashMap fAttrNamespace = null;
  
  public XMLStreamWriterImpl(OutputStream paramOutputStream, PropertyManager paramPropertyManager) throws IOException { this(new OutputStreamWriter(paramOutputStream), paramPropertyManager); }
  
  public XMLStreamWriterImpl(OutputStream paramOutputStream, String paramString, PropertyManager paramPropertyManager) throws IOException { this(new StreamResult(paramOutputStream), paramString, paramPropertyManager); }
  
  public XMLStreamWriterImpl(Writer paramWriter, PropertyManager paramPropertyManager) throws IOException { this(new StreamResult(paramWriter), null, paramPropertyManager); }
  
  public XMLStreamWriterImpl(StreamResult paramStreamResult, String paramString, PropertyManager paramPropertyManager) throws IOException {
    setOutput(paramStreamResult, paramString);
    this.fPropertyManager = paramPropertyManager;
    init();
  }
  
  private void init() {
    this.fReuse = false;
    this.fNamespaceDecls = new ArrayList();
    this.fPrefixGen = new Random();
    this.fAttributeCache = new ArrayList();
    this.fInternalNamespaceContext = new NamespaceSupport();
    this.fInternalNamespaceContext.reset();
    this.fNamespaceContext = new NamespaceContextImpl();
    this.fNamespaceContext.internalContext = this.fInternalNamespaceContext;
    Boolean bool = (Boolean)this.fPropertyManager.getProperty("javax.xml.stream.isRepairingNamespaces");
    this.fIsRepairingNamespace = bool.booleanValue();
    bool = (Boolean)this.fPropertyManager.getProperty("escapeCharacters");
    setEscapeCharacters(bool.booleanValue());
  }
  
  public void reset() { reset(false); }
  
  void reset(boolean paramBoolean) {
    if (!this.fReuse)
      throw new IllegalStateException("close() Must be called before calling reset()"); 
    this.fReuse = false;
    this.fNamespaceDecls.clear();
    this.fAttributeCache.clear();
    this.fElementStack.clear();
    this.fInternalNamespaceContext.reset();
    this.fStartTagOpened = false;
    this.fNamespaceContext.userContext = null;
    if (paramBoolean) {
      Boolean bool = (Boolean)this.fPropertyManager.getProperty("javax.xml.stream.isRepairingNamespaces");
      this.fIsRepairingNamespace = bool.booleanValue();
      bool = (Boolean)this.fPropertyManager.getProperty("escapeCharacters");
      setEscapeCharacters(bool.booleanValue());
    } 
  }
  
  public void setOutput(StreamResult paramStreamResult, String paramString) throws IOException {
    if (paramStreamResult.getOutputStream() != null) {
      setOutputUsingStream(paramStreamResult.getOutputStream(), paramString);
    } else if (paramStreamResult.getWriter() != null) {
      setOutputUsingWriter(paramStreamResult.getWriter());
    } else if (paramStreamResult.getSystemId() != null) {
      setOutputUsingStream(new FileOutputStream(paramStreamResult.getSystemId()), paramString);
    } 
  }
  
  private void setOutputUsingWriter(Writer paramWriter) throws IOException {
    this.fWriter = paramWriter;
    if (paramWriter instanceof OutputStreamWriter) {
      String str = ((OutputStreamWriter)paramWriter).getEncoding();
      if (str != null && !str.equalsIgnoreCase("utf-8"))
        this.fEncoder = Charset.forName(str).newEncoder(); 
    } 
  }
  
  private void setOutputUsingStream(OutputStream paramOutputStream, String paramString) throws IOException {
    this.fOutputStream = paramOutputStream;
    if (paramString != null) {
      if (paramString.equalsIgnoreCase("utf-8")) {
        this.fWriter = new UTF8OutputStreamWriter(paramOutputStream);
      } else {
        this.fWriter = new XMLWriter(new OutputStreamWriter(paramOutputStream, paramString));
        this.fEncoder = Charset.forName(paramString).newEncoder();
      } 
    } else {
      paramString = SecuritySupport.getSystemProperty("file.encoding");
      if (paramString != null && paramString.equalsIgnoreCase("utf-8")) {
        this.fWriter = new UTF8OutputStreamWriter(paramOutputStream);
      } else {
        this.fWriter = new XMLWriter(new OutputStreamWriter(paramOutputStream));
      } 
    } 
  }
  
  public boolean canReuse() { return this.fReuse; }
  
  public void setEscapeCharacters(boolean paramBoolean) { this.fEscapeCharacters = paramBoolean; }
  
  public boolean getEscapeCharacters() { return this.fEscapeCharacters; }
  
  public void close() {
    if (this.fWriter != null)
      try {
        this.fWriter.flush();
      } catch (IOException iOException) {
        throw new XMLStreamException(iOException);
      }  
    this.fWriter = null;
    this.fOutputStream = null;
    this.fNamespaceDecls.clear();
    this.fAttributeCache.clear();
    this.fElementStack.clear();
    this.fInternalNamespaceContext.reset();
    this.fReuse = true;
    this.fStartTagOpened = false;
    this.fNamespaceContext.userContext = null;
  }
  
  public void flush() {
    try {
      this.fWriter.flush();
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  public NamespaceContext getNamespaceContext() { return this.fNamespaceContext; }
  
  public String getPrefix(String paramString) throws XMLStreamException { return this.fNamespaceContext.getPrefix(paramString); }
  
  public Object getProperty(String paramString) throws IllegalArgumentException {
    if (paramString == null)
      throw new NullPointerException(); 
    if (!this.fPropertyManager.containsProperty(paramString))
      throw new IllegalArgumentException("Property '" + paramString + "' is not supported"); 
    return this.fPropertyManager.getProperty(paramString);
  }
  
  public void setDefaultNamespace(String paramString) throws XMLStreamException {
    if (paramString != null)
      paramString = this.fSymbolTable.addSymbol(paramString); 
    if (this.fIsRepairingNamespace) {
      if (isDefaultNamespace(paramString))
        return; 
      QName qName = new QName();
      qName.setValues(this.DEFAULT_PREFIX, "xmlns", null, paramString);
      this.fNamespaceDecls.add(qName);
    } else {
      this.fInternalNamespaceContext.declarePrefix(this.DEFAULT_PREFIX, paramString);
    } 
  }
  
  public void setNamespaceContext(NamespaceContext paramNamespaceContext) throws XMLStreamException { this.fNamespaceContext.userContext = paramNamespaceContext; }
  
  public void setPrefix(String paramString1, String paramString2) throws XMLStreamException {
    if (paramString1 == null)
      throw new XMLStreamException("Prefix cannot be null"); 
    if (paramString2 == null)
      throw new XMLStreamException("URI cannot be null"); 
    paramString1 = this.fSymbolTable.addSymbol(paramString1);
    paramString2 = this.fSymbolTable.addSymbol(paramString2);
    if (this.fIsRepairingNamespace) {
      String str = this.fInternalNamespaceContext.getURI(paramString1);
      if (str != null && str == paramString2)
        return; 
      if (checkUserNamespaceContext(paramString1, paramString2))
        return; 
      QName qName = new QName();
      qName.setValues(paramString1, "xmlns", null, paramString2);
      this.fNamespaceDecls.add(qName);
      return;
    } 
    this.fInternalNamespaceContext.declarePrefix(paramString1, paramString2);
  }
  
  public void writeAttribute(String paramString1, String paramString2) throws XMLStreamException {
    try {
      if (!this.fStartTagOpened)
        throw new XMLStreamException("Attribute not associated with any element"); 
      if (this.fIsRepairingNamespace) {
        Attribute attribute = new Attribute(paramString2);
        attribute.setValues(null, paramString1, null, null);
        this.fAttributeCache.add(attribute);
        return;
      } 
      this.fWriter.write(" ");
      this.fWriter.write(paramString1);
      this.fWriter.write("=\"");
      writeXMLContent(paramString2, true, true);
      this.fWriter.write("\"");
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3) throws XMLStreamException {
    try {
      if (!this.fStartTagOpened)
        throw new XMLStreamException("Attribute not associated with any element"); 
      if (paramString1 == null)
        throw new XMLStreamException("NamespaceURI cannot be null"); 
      paramString1 = this.fSymbolTable.addSymbol(paramString1);
      String str = this.fInternalNamespaceContext.getPrefix(paramString1);
      if (!this.fIsRepairingNamespace) {
        if (str == null)
          throw new XMLStreamException("Prefix cannot be null"); 
        writeAttributeWithPrefix(str, paramString2, paramString3);
      } else {
        Attribute attribute = new Attribute(paramString3);
        attribute.setValues(null, paramString2, null, paramString1);
        this.fAttributeCache.add(attribute);
      } 
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  private void writeAttributeWithPrefix(String paramString1, String paramString2, String paramString3) throws XMLStreamException {
    this.fWriter.write(" ");
    if (paramString1 != null && paramString1 != "") {
      this.fWriter.write(paramString1);
      this.fWriter.write(":");
    } 
    this.fWriter.write(paramString2);
    this.fWriter.write("=\"");
    writeXMLContent(paramString3, true, true);
    this.fWriter.write("\"");
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3, String paramString4) throws XMLStreamException {
    try {
      if (!this.fStartTagOpened)
        throw new XMLStreamException("Attribute not associated with any element"); 
      if (paramString2 == null)
        throw new XMLStreamException("NamespaceURI cannot be null"); 
      if (paramString3 == null)
        throw new XMLStreamException("Local name cannot be null"); 
      if (!this.fIsRepairingNamespace) {
        if (paramString1 == null || paramString1.equals("")) {
          if (!paramString2.equals(""))
            throw new XMLStreamException("prefix cannot be null or empty"); 
          writeAttributeWithPrefix(null, paramString3, paramString4);
          return;
        } 
        if (!paramString1.equals("xml") || !paramString2.equals("http://www.w3.org/XML/1998/namespace")) {
          paramString1 = this.fSymbolTable.addSymbol(paramString1);
          paramString2 = this.fSymbolTable.addSymbol(paramString2);
          if (this.fInternalNamespaceContext.containsPrefixInCurrentContext(paramString1)) {
            String str = this.fInternalNamespaceContext.getURI(paramString1);
            if (str != null && str != paramString2)
              throw new XMLStreamException("Prefix " + paramString1 + " is already bound to " + str + ". Trying to rebind it to " + paramString2 + " is an error."); 
          } 
          this.fInternalNamespaceContext.declarePrefix(paramString1, paramString2);
        } 
        writeAttributeWithPrefix(paramString1, paramString3, paramString4);
      } else {
        if (paramString1 != null)
          paramString1 = this.fSymbolTable.addSymbol(paramString1); 
        paramString2 = this.fSymbolTable.addSymbol(paramString2);
        Attribute attribute = new Attribute(paramString4);
        attribute.setValues(paramString1, paramString3, null, paramString2);
        this.fAttributeCache.add(attribute);
      } 
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  public void writeCData(String paramString) throws XMLStreamException {
    try {
      if (paramString == null)
        throw new XMLStreamException("cdata cannot be null"); 
      if (this.fStartTagOpened)
        closeStartTag(); 
      this.fWriter.write("<![CDATA[");
      this.fWriter.write(paramString);
      this.fWriter.write("]]>");
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  public void writeCharacters(String paramString) throws XMLStreamException {
    try {
      if (this.fStartTagOpened)
        closeStartTag(); 
      writeXMLContent(paramString);
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  public void writeCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws XMLStreamException {
    try {
      if (this.fStartTagOpened)
        closeStartTag(); 
      writeXMLContent(paramArrayOfChar, paramInt1, paramInt2, this.fEscapeCharacters);
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  public void writeComment(String paramString) throws XMLStreamException {
    try {
      if (this.fStartTagOpened)
        closeStartTag(); 
      this.fWriter.write("<!--");
      if (paramString != null)
        this.fWriter.write(paramString); 
      this.fWriter.write("-->");
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  public void writeDTD(String paramString) throws XMLStreamException {
    try {
      if (this.fStartTagOpened)
        closeStartTag(); 
      this.fWriter.write(paramString);
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  public void writeDefaultNamespace(String paramString) throws XMLStreamException {
    String str = null;
    if (paramString == null) {
      str = "";
    } else {
      str = paramString;
    } 
    try {
      if (!this.fStartTagOpened)
        throw new IllegalStateException("Namespace Attribute not associated with any element"); 
      if (this.fIsRepairingNamespace) {
        QName qName = new QName();
        qName.setValues("", "xmlns", null, str);
        this.fNamespaceDecls.add(qName);
        return;
      } 
      str = this.fSymbolTable.addSymbol(str);
      if (this.fInternalNamespaceContext.containsPrefixInCurrentContext("")) {
        String str1 = this.fInternalNamespaceContext.getURI("");
        if (str1 != null && str1 != str)
          throw new XMLStreamException("xmlns has been already bound to " + str1 + ". Rebinding it to " + str + " is an error"); 
      } 
      this.fInternalNamespaceContext.declarePrefix("", str);
      writenamespace(null, str);
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  public void writeEmptyElement(String paramString) throws XMLStreamException {
    try {
      if (this.fStartTagOpened)
        closeStartTag(); 
      openStartTag();
      this.fElementStack.push(null, paramString, null, null, true);
      this.fInternalNamespaceContext.pushContext();
      if (!this.fIsRepairingNamespace)
        this.fWriter.write(paramString); 
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  public void writeEmptyElement(String paramString1, String paramString2) throws XMLStreamException {
    if (paramString1 == null)
      throw new XMLStreamException("NamespaceURI cannot be null"); 
    paramString1 = this.fSymbolTable.addSymbol(paramString1);
    String str = this.fNamespaceContext.getPrefix(paramString1);
    writeEmptyElement(str, paramString2, paramString1);
  }
  
  public void writeEmptyElement(String paramString1, String paramString2, String paramString3) throws XMLStreamException {
    try {
      if (paramString2 == null)
        throw new XMLStreamException("Local Name cannot be null"); 
      if (paramString3 == null)
        throw new XMLStreamException("NamespaceURI cannot be null"); 
      if (paramString1 != null)
        paramString1 = this.fSymbolTable.addSymbol(paramString1); 
      paramString3 = this.fSymbolTable.addSymbol(paramString3);
      if (this.fStartTagOpened)
        closeStartTag(); 
      openStartTag();
      this.fElementStack.push(paramString1, paramString2, null, paramString3, true);
      this.fInternalNamespaceContext.pushContext();
      if (!this.fIsRepairingNamespace) {
        if (paramString1 == null)
          throw new XMLStreamException("NamespaceURI " + paramString3 + " has not been bound to any prefix"); 
      } else {
        return;
      } 
      if (paramString1 != null && paramString1 != "") {
        this.fWriter.write(paramString1);
        this.fWriter.write(":");
      } 
      this.fWriter.write(paramString2);
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  public void writeEndDocument() {
    try {
      if (this.fStartTagOpened)
        closeStartTag(); 
      ElementState elementState = null;
      while (!this.fElementStack.empty()) {
        elementState = this.fElementStack.pop();
        this.fInternalNamespaceContext.popContext();
        if (elementState.isEmpty)
          continue; 
        this.fWriter.write("</");
        if (elementState.prefix != null && !elementState.prefix.equals("")) {
          this.fWriter.write(elementState.prefix);
          this.fWriter.write(":");
        } 
        this.fWriter.write(elementState.localpart);
        this.fWriter.write(62);
      } 
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new XMLStreamException("No more elements to write");
    } 
  }
  
  public void writeEndElement() {
    try {
      if (this.fStartTagOpened)
        closeStartTag(); 
      ElementState elementState = this.fElementStack.pop();
      if (elementState == null)
        throw new XMLStreamException("No element was found to write"); 
      if (elementState.isEmpty)
        return; 
      this.fWriter.write("</");
      if (elementState.prefix != null && !elementState.prefix.equals("")) {
        this.fWriter.write(elementState.prefix);
        this.fWriter.write(":");
      } 
      this.fWriter.write(elementState.localpart);
      this.fWriter.write(62);
      this.fInternalNamespaceContext.popContext();
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new XMLStreamException("No element was found to write: " + arrayIndexOutOfBoundsException.toString(), arrayIndexOutOfBoundsException);
    } 
  }
  
  public void writeEntityRef(String paramString) throws XMLStreamException {
    try {
      if (this.fStartTagOpened)
        closeStartTag(); 
      this.fWriter.write(38);
      this.fWriter.write(paramString);
      this.fWriter.write(59);
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  public void writeNamespace(String paramString1, String paramString2) throws XMLStreamException {
    String str = null;
    if (paramString2 == null) {
      str = "";
    } else {
      str = paramString2;
    } 
    try {
      QName qName = null;
      if (!this.fStartTagOpened)
        throw new IllegalStateException("Invalid state: start tag is not opened at writeNamespace(" + paramString1 + ", " + str + ")"); 
      if (paramString1 == null || paramString1.equals("") || paramString1.equals("xmlns")) {
        writeDefaultNamespace(str);
        return;
      } 
      if (paramString1.equals("xml") && str.equals("http://www.w3.org/XML/1998/namespace"))
        return; 
      paramString1 = this.fSymbolTable.addSymbol(paramString1);
      str = this.fSymbolTable.addSymbol(str);
      if (this.fIsRepairingNamespace) {
        String str1 = this.fInternalNamespaceContext.getURI(paramString1);
        if (str1 != null && str1 == str)
          return; 
        qName = new QName();
        qName.setValues(paramString1, "xmlns", null, str);
        this.fNamespaceDecls.add(qName);
        return;
      } 
      if (this.fInternalNamespaceContext.containsPrefixInCurrentContext(paramString1)) {
        String str1 = this.fInternalNamespaceContext.getURI(paramString1);
        if (str1 != null && str1 != str)
          throw new XMLStreamException("prefix " + paramString1 + " has been already bound to " + str1 + ". Rebinding it to " + str + " is an error"); 
      } 
      this.fInternalNamespaceContext.declarePrefix(paramString1, str);
      writenamespace(paramString1, str);
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  private void writenamespace(String paramString1, String paramString2) throws XMLStreamException {
    this.fWriter.write(" xmlns");
    if (paramString1 != null && paramString1 != "") {
      this.fWriter.write(":");
      this.fWriter.write(paramString1);
    } 
    this.fWriter.write("=\"");
    writeXMLContent(paramString2, true, true);
    this.fWriter.write("\"");
  }
  
  public void writeProcessingInstruction(String paramString) throws XMLStreamException {
    try {
      if (this.fStartTagOpened)
        closeStartTag(); 
      if (paramString != null) {
        this.fWriter.write("<?");
        this.fWriter.write(paramString);
        this.fWriter.write("?>");
        return;
      } 
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
    throw new XMLStreamException("PI target cannot be null");
  }
  
  public void writeProcessingInstruction(String paramString1, String paramString2) throws XMLStreamException {
    try {
      if (this.fStartTagOpened)
        closeStartTag(); 
      if (paramString1 == null || paramString2 == null)
        throw new XMLStreamException("PI target cannot be null"); 
      this.fWriter.write("<?");
      this.fWriter.write(paramString1);
      this.fWriter.write(" ");
      this.fWriter.write(paramString2);
      this.fWriter.write("?>");
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  public void writeStartDocument() {
    try {
      this.fWriter.write("<?xml version=\"1.0\" ?>");
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  public void writeStartDocument(String paramString) throws XMLStreamException {
    try {
      if (paramString == null || paramString.equals("")) {
        writeStartDocument();
        return;
      } 
      this.fWriter.write("<?xml version=\"");
      this.fWriter.write(paramString);
      this.fWriter.write("\"");
      this.fWriter.write("?>");
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  public void writeStartDocument(String paramString1, String paramString2) throws XMLStreamException {
    try {
      if (paramString1 == null && paramString2 == null) {
        writeStartDocument();
        return;
      } 
      if (paramString1 == null) {
        writeStartDocument(paramString2);
        return;
      } 
      String str = null;
      if (this.fWriter instanceof OutputStreamWriter) {
        str = ((OutputStreamWriter)this.fWriter).getEncoding();
      } else if (this.fWriter instanceof UTF8OutputStreamWriter) {
        str = ((UTF8OutputStreamWriter)this.fWriter).getEncoding();
      } else if (this.fWriter instanceof XMLWriter) {
        str = ((OutputStreamWriter)((XMLWriter)this.fWriter).getWriter()).getEncoding();
      } 
      if (str != null && !str.equalsIgnoreCase(paramString1)) {
        boolean bool = false;
        Set set = Charset.forName(paramString1).aliases();
        Iterator iterator = set.iterator();
        while (!bool && iterator.hasNext()) {
          if (str.equalsIgnoreCase((String)iterator.next()))
            bool = true; 
        } 
        if (!bool)
          throw new XMLStreamException("Underlying stream encoding '" + str + "' and input paramter for writeStartDocument() method '" + paramString1 + "' do not match."); 
      } 
      this.fWriter.write("<?xml version=\"");
      if (paramString2 == null || paramString2.equals("")) {
        this.fWriter.write("1.0");
      } else {
        this.fWriter.write(paramString2);
      } 
      if (!paramString1.equals("")) {
        this.fWriter.write("\" encoding=\"");
        this.fWriter.write(paramString1);
      } 
      this.fWriter.write("\"?>");
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  public void writeStartElement(String paramString) throws XMLStreamException {
    try {
      if (paramString == null)
        throw new XMLStreamException("Local Name cannot be null"); 
      if (this.fStartTagOpened)
        closeStartTag(); 
      openStartTag();
      this.fElementStack.push(null, paramString, null, null, false);
      this.fInternalNamespaceContext.pushContext();
      if (this.fIsRepairingNamespace)
        return; 
      this.fWriter.write(paramString);
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  public void writeStartElement(String paramString1, String paramString2) throws XMLStreamException {
    if (paramString2 == null)
      throw new XMLStreamException("Local Name cannot be null"); 
    if (paramString1 == null)
      throw new XMLStreamException("NamespaceURI cannot be null"); 
    paramString1 = this.fSymbolTable.addSymbol(paramString1);
    String str = null;
    if (!this.fIsRepairingNamespace) {
      str = this.fNamespaceContext.getPrefix(paramString1);
      if (str != null)
        str = this.fSymbolTable.addSymbol(str); 
    } 
    writeStartElement(str, paramString2, paramString1);
  }
  
  public void writeStartElement(String paramString1, String paramString2, String paramString3) throws XMLStreamException {
    try {
      if (paramString2 == null)
        throw new XMLStreamException("Local Name cannot be null"); 
      if (paramString3 == null)
        throw new XMLStreamException("NamespaceURI cannot be null"); 
      if (!this.fIsRepairingNamespace && paramString1 == null)
        throw new XMLStreamException("Prefix cannot be null"); 
      if (this.fStartTagOpened)
        closeStartTag(); 
      openStartTag();
      paramString3 = this.fSymbolTable.addSymbol(paramString3);
      if (paramString1 != null)
        paramString1 = this.fSymbolTable.addSymbol(paramString1); 
      this.fElementStack.push(paramString1, paramString2, null, paramString3, false);
      this.fInternalNamespaceContext.pushContext();
      String str = this.fNamespaceContext.getPrefix(paramString3);
      if (paramString1 != null && (str == null || !paramString1.equals(str)))
        this.fInternalNamespaceContext.declarePrefix(paramString1, paramString3); 
      if (this.fIsRepairingNamespace) {
        if (paramString1 == null || (str != null && paramString1.equals(str)))
          return; 
        QName qName = new QName();
        qName.setValues(paramString1, "xmlns", null, paramString3);
        this.fNamespaceDecls.add(qName);
        return;
      } 
      if (paramString1 != null && paramString1 != "") {
        this.fWriter.write(paramString1);
        this.fWriter.write(":");
      } 
      this.fWriter.write(paramString2);
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  private void writeCharRef(int paramInt) throws IOException {
    this.fWriter.write("&#x");
    this.fWriter.write(Integer.toHexString(paramInt));
    this.fWriter.write(59);
  }
  
  private void writeXMLContent(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean) throws IOException {
    if (!paramBoolean) {
      this.fWriter.write(paramArrayOfChar, paramInt1, paramInt2);
      return;
    } 
    int i = paramInt1;
    int j = paramInt1 + paramInt2;
    for (int k = paramInt1; k < j; k++) {
      char c = paramArrayOfChar[k];
      if (this.fEncoder != null && !this.fEncoder.canEncode(c)) {
        this.fWriter.write(paramArrayOfChar, i, k - i);
        if (k != j - 1 && Character.isSurrogatePair(c, paramArrayOfChar[k + 1])) {
          writeCharRef(Character.toCodePoint(c, paramArrayOfChar[k + 1]));
          k++;
        } else {
          writeCharRef(c);
        } 
        i = k + 1;
      } else {
        switch (c) {
          case '<':
            this.fWriter.write(paramArrayOfChar, i, k - i);
            this.fWriter.write("&lt;");
            i = k + 1;
            break;
          case '&':
            this.fWriter.write(paramArrayOfChar, i, k - i);
            this.fWriter.write("&amp;");
            i = k + 1;
            break;
          case '>':
            this.fWriter.write(paramArrayOfChar, i, k - i);
            this.fWriter.write("&gt;");
            i = k + 1;
            break;
        } 
      } 
    } 
    this.fWriter.write(paramArrayOfChar, i, j - i);
  }
  
  private void writeXMLContent(String paramString) throws XMLStreamException {
    if (paramString != null && paramString.length() > 0)
      writeXMLContent(paramString, this.fEscapeCharacters, false); 
  }
  
  private void writeXMLContent(String paramString, boolean paramBoolean1, boolean paramBoolean2) throws IOException {
    if (!paramBoolean1) {
      this.fWriter.write(paramString);
      return;
    } 
    int i = 0;
    int j = paramString.length();
    for (byte b = 0; b < j; b++) {
      char c = paramString.charAt(b);
      if (this.fEncoder != null && !this.fEncoder.canEncode(c)) {
        this.fWriter.write(paramString, i, b - i);
        if (b != j - 1 && Character.isSurrogatePair(c, paramString.charAt(b + 1))) {
          writeCharRef(Character.toCodePoint(c, paramString.charAt(b + 1)));
          b++;
        } else {
          writeCharRef(c);
        } 
        i = b + 1;
      } else {
        switch (c) {
          case '<':
            this.fWriter.write(paramString, i, b - i);
            this.fWriter.write("&lt;");
            i = b + 1;
            break;
          case '&':
            this.fWriter.write(paramString, i, b - i);
            this.fWriter.write("&amp;");
            i = b + 1;
            break;
          case '>':
            this.fWriter.write(paramString, i, b - i);
            this.fWriter.write("&gt;");
            i = b + 1;
            break;
          case '"':
            this.fWriter.write(paramString, i, b - i);
            if (paramBoolean2) {
              this.fWriter.write("&quot;");
            } else {
              this.fWriter.write(34);
            } 
            i = b + 1;
            break;
        } 
      } 
    } 
    this.fWriter.write(paramString, i, j - i);
  }
  
  private void closeStartTag() {
    try {
      ElementState elementState = this.fElementStack.peek();
      if (this.fIsRepairingNamespace) {
        repair();
        correctPrefix(elementState, 1);
        if (elementState.prefix != null && elementState.prefix != "") {
          this.fWriter.write(elementState.prefix);
          this.fWriter.write(":");
        } 
        this.fWriter.write(elementState.localpart);
        int i = this.fNamespaceDecls.size();
        QName qName = null;
        for (byte b1 = 0; b1 < i; b1++) {
          qName = (QName)this.fNamespaceDecls.get(b1);
          if (qName != null && this.fInternalNamespaceContext.declarePrefix(qName.prefix, qName.uri))
            writenamespace(qName.prefix, qName.uri); 
        } 
        this.fNamespaceDecls.clear();
        Attribute attribute = null;
        for (byte b2 = 0; b2 < this.fAttributeCache.size(); b2++) {
          attribute = (Attribute)this.fAttributeCache.get(b2);
          if (attribute.prefix != null && attribute.uri != null && !attribute.prefix.equals("") && !attribute.uri.equals("")) {
            String str = this.fInternalNamespaceContext.getPrefix(attribute.uri);
            if (str == null || str != attribute.prefix) {
              str = getAttrPrefix(attribute.uri);
              if (str == null) {
                if (this.fInternalNamespaceContext.declarePrefix(attribute.prefix, attribute.uri))
                  writenamespace(attribute.prefix, attribute.uri); 
              } else {
                writenamespace(attribute.prefix, attribute.uri);
              } 
            } 
          } 
          writeAttributeWithPrefix(attribute.prefix, attribute.localpart, attribute.value);
        } 
        this.fAttrNamespace = null;
        this.fAttributeCache.clear();
      } 
      if (elementState.isEmpty) {
        this.fElementStack.pop();
        this.fInternalNamespaceContext.popContext();
        this.fWriter.write("/>");
      } else {
        this.fWriter.write(62);
      } 
      this.fStartTagOpened = false;
    } catch (IOException iOException) {
      this.fStartTagOpened = false;
      throw new XMLStreamException(iOException);
    } 
  }
  
  private void openStartTag() {
    this.fStartTagOpened = true;
    this.fWriter.write(60);
  }
  
  private void correctPrefix(QName paramQName, int paramInt) {
    String str1 = null;
    String str2 = paramQName.prefix;
    String str3 = paramQName.uri;
    boolean bool = false;
    if (str2 == null || str2.equals("")) {
      if (str3 == null)
        return; 
      if (str2 == "" && str3 == "")
        return; 
      str3 = this.fSymbolTable.addSymbol(str3);
      QName qName = null;
      for (byte b = 0; b < this.fNamespaceDecls.size(); b++) {
        qName = (QName)this.fNamespaceDecls.get(b);
        if (qName != null && qName.uri == paramQName.uri) {
          paramQName.prefix = qName.prefix;
          return;
        } 
      } 
      str1 = this.fNamespaceContext.getPrefix(str3);
      if (str1 == "") {
        if (paramInt == 1)
          return; 
        if (paramInt == 10) {
          str1 = getAttrPrefix(str3);
          bool = true;
        } 
      } 
      if (str1 == null) {
        StringBuffer stringBuffer = new StringBuffer("zdef");
        for (byte b1 = 0; b1 < 1; b1++)
          stringBuffer.append(this.fPrefixGen.nextInt()); 
        str2 = stringBuffer.toString();
        str2 = this.fSymbolTable.addSymbol(str2);
      } else {
        str2 = this.fSymbolTable.addSymbol(str1);
      } 
      if (str1 == null)
        if (bool) {
          addAttrNamespace(str2, str3);
        } else {
          QName qName1 = new QName();
          qName1.setValues(str2, "xmlns", null, str3);
          this.fNamespaceDecls.add(qName1);
          this.fInternalNamespaceContext.declarePrefix(this.fSymbolTable.addSymbol(str2), str3);
        }  
    } 
    paramQName.prefix = str2;
  }
  
  private String getAttrPrefix(String paramString) throws XMLStreamException { return (this.fAttrNamespace != null) ? (String)this.fAttrNamespace.get(paramString) : null; }
  
  private void addAttrNamespace(String paramString1, String paramString2) throws XMLStreamException {
    if (this.fAttrNamespace == null)
      this.fAttrNamespace = new HashMap(); 
    this.fAttrNamespace.put(paramString1, paramString2);
  }
  
  private boolean isDefaultNamespace(String paramString) {
    String str = this.fInternalNamespaceContext.getURI(this.DEFAULT_PREFIX);
    return (paramString == str);
  }
  
  private boolean checkUserNamespaceContext(String paramString1, String paramString2) {
    if (this.fNamespaceContext.userContext != null) {
      String str = this.fNamespaceContext.userContext.getNamespaceURI(paramString1);
      if (str != null && str.equals(paramString2))
        return true; 
    } 
    return false;
  }
  
  protected void repair() {
    Attribute attribute1 = null;
    Attribute attribute2 = null;
    ElementState elementState = this.fElementStack.peek();
    removeDuplicateDecls();
    byte b;
    for (b = 0; b < this.fAttributeCache.size(); b++) {
      attribute1 = (Attribute)this.fAttributeCache.get(b);
      if ((attribute1.prefix != null && !attribute1.prefix.equals("")) || (attribute1.uri != null && !attribute1.uri.equals("")))
        correctPrefix(elementState, attribute1); 
    } 
    if (!isDeclared(elementState) && elementState.prefix != null && elementState.uri != null && !elementState.prefix.equals("") && !elementState.uri.equals(""))
      this.fNamespaceDecls.add(elementState); 
    for (b = 0; b < this.fAttributeCache.size(); b++) {
      attribute1 = (Attribute)this.fAttributeCache.get(b);
      for (byte b1 = b + 1; b1 < this.fAttributeCache.size(); b1++) {
        attribute2 = (Attribute)this.fAttributeCache.get(b1);
        if (!"".equals(attribute1.prefix) && !"".equals(attribute2.prefix))
          correctPrefix(attribute1, attribute2); 
      } 
    } 
    repairNamespaceDecl(elementState);
    b = 0;
    for (b = 0; b < this.fAttributeCache.size(); b++) {
      attribute1 = (Attribute)this.fAttributeCache.get(b);
      if (attribute1.prefix != null && attribute1.prefix.equals("") && attribute1.uri != null && attribute1.uri.equals(""))
        repairNamespaceDecl(attribute1); 
    } 
    QName qName = null;
    for (b = 0; b < this.fNamespaceDecls.size(); b++) {
      qName = (QName)this.fNamespaceDecls.get(b);
      if (qName != null)
        this.fInternalNamespaceContext.declarePrefix(qName.prefix, qName.uri); 
    } 
    for (b = 0; b < this.fAttributeCache.size(); b++) {
      attribute1 = (Attribute)this.fAttributeCache.get(b);
      correctPrefix(attribute1, 10);
    } 
  }
  
  void correctPrefix(QName paramQName1, QName paramQName2) {
    String str = null;
    QName qName = null;
    boolean bool = false;
    checkForNull(paramQName1);
    checkForNull(paramQName2);
    if (paramQName1.prefix.equals(paramQName2.prefix) && !paramQName1.uri.equals(paramQName2.uri)) {
      str = this.fNamespaceContext.getPrefix(paramQName2.uri);
      if (str != null) {
        paramQName2.prefix = this.fSymbolTable.addSymbol(str);
      } else {
        qName = null;
        for (byte b1 = 0; b1 < this.fNamespaceDecls.size(); b1++) {
          qName = (QName)this.fNamespaceDecls.get(b1);
          if (qName != null && qName.uri == paramQName2.uri) {
            paramQName2.prefix = qName.prefix;
            return;
          } 
        } 
        StringBuffer stringBuffer = new StringBuffer("zdef");
        for (byte b2 = 0; b2 < 1; b2++)
          stringBuffer.append(this.fPrefixGen.nextInt()); 
        str = stringBuffer.toString();
        str = this.fSymbolTable.addSymbol(str);
        paramQName2.prefix = str;
        QName qName1 = new QName();
        qName1.setValues(str, "xmlns", null, paramQName2.uri);
        this.fNamespaceDecls.add(qName1);
      } 
    } 
  }
  
  void checkForNull(QName paramQName) {
    if (paramQName.prefix == null)
      paramQName.prefix = ""; 
    if (paramQName.uri == null)
      paramQName.uri = ""; 
  }
  
  void removeDuplicateDecls() {
    for (byte b = 0; b < this.fNamespaceDecls.size(); b++) {
      QName qName = (QName)this.fNamespaceDecls.get(b);
      if (qName != null)
        for (byte b1 = b + 1; b1 < this.fNamespaceDecls.size(); b1++) {
          QName qName1 = (QName)this.fNamespaceDecls.get(b1);
          if (qName1 != null && qName.prefix.equals(qName1.prefix) && qName.uri.equals(qName1.uri))
            this.fNamespaceDecls.remove(b1); 
        }  
    } 
  }
  
  void repairNamespaceDecl(QName paramQName) {
    QName qName = null;
    for (byte b = 0; b < this.fNamespaceDecls.size(); b++) {
      qName = (QName)this.fNamespaceDecls.get(b);
      if (qName != null && paramQName.prefix != null && paramQName.prefix.equals(qName.prefix) && !paramQName.uri.equals(qName.uri)) {
        String str = this.fNamespaceContext.getNamespaceURI(paramQName.prefix);
        if (str != null)
          if (str.equals(paramQName.uri)) {
            this.fNamespaceDecls.set(b, null);
          } else {
            qName.uri = paramQName.uri;
          }  
      } 
    } 
  }
  
  boolean isDeclared(QName paramQName) {
    QName qName = null;
    for (byte b = 0; b < this.fNamespaceDecls.size(); b++) {
      qName = (QName)this.fNamespaceDecls.get(b);
      if (paramQName.prefix != null && paramQName.prefix == qName.prefix && qName.uri == paramQName.uri)
        return true; 
    } 
    return (paramQName.uri != null && this.fNamespaceContext.getPrefix(paramQName.uri) != null);
  }
  
  public int size() { return 1; }
  
  public boolean isEmpty() { return false; }
  
  public boolean containsKey(Object paramObject) { return paramObject.equals("sjsxp-outputstream"); }
  
  public Object get(Object paramObject) { return paramObject.equals("sjsxp-outputstream") ? this.fOutputStream : null; }
  
  public Set entrySet() { throw new UnsupportedOperationException(); }
  
  public String toString() { return getClass().getName() + "@" + Integer.toHexString(hashCode()); }
  
  public int hashCode() { return this.fElementStack.hashCode(); }
  
  public boolean equals(Object paramObject) { return (this == paramObject); }
  
  class Attribute extends QName {
    String value;
    
    Attribute(String param1String) { this.value = param1String; }
  }
  
  protected class ElementStack {
    protected XMLStreamWriterImpl.ElementState[] fElements = new XMLStreamWriterImpl.ElementState[10];
    
    protected short fDepth;
    
    public ElementStack() {
      for (byte b = 0; b < this.fElements.length; b++)
        this.fElements[b] = new XMLStreamWriterImpl.ElementState(this$0); 
    }
    
    public XMLStreamWriterImpl.ElementState push(XMLStreamWriterImpl.ElementState param1ElementState) {
      if (this.fDepth == this.fElements.length) {
        XMLStreamWriterImpl.ElementState[] arrayOfElementState = new XMLStreamWriterImpl.ElementState[this.fElements.length * 2];
        System.arraycopy(this.fElements, 0, arrayOfElementState, 0, this.fDepth);
        this.fElements = arrayOfElementState;
        for (short s = this.fDepth; s < this.fElements.length; s++)
          this.fElements[s] = new XMLStreamWriterImpl.ElementState(XMLStreamWriterImpl.this); 
      } 
      this.fElements[this.fDepth].setValues(param1ElementState);
      this.fDepth = (short)(this.fDepth + 1);
      return this.fElements[this.fDepth];
    }
    
    public XMLStreamWriterImpl.ElementState push(String param1String1, String param1String2, String param1String3, String param1String4, boolean param1Boolean) {
      if (this.fDepth == this.fElements.length) {
        XMLStreamWriterImpl.ElementState[] arrayOfElementState = new XMLStreamWriterImpl.ElementState[this.fElements.length * 2];
        System.arraycopy(this.fElements, 0, arrayOfElementState, 0, this.fDepth);
        this.fElements = arrayOfElementState;
        for (short s = this.fDepth; s < this.fElements.length; s++)
          this.fElements[s] = new XMLStreamWriterImpl.ElementState(XMLStreamWriterImpl.this); 
      } 
      this.fElements[this.fDepth].setValues(param1String1, param1String2, param1String3, param1String4, param1Boolean);
      this.fDepth = (short)(this.fDepth + 1);
      return this.fElements[this.fDepth];
    }
    
    public XMLStreamWriterImpl.ElementState pop() { return this.fElements[this.fDepth = (short)(this.fDepth - 1)]; }
    
    public void clear() { this.fDepth = 0; }
    
    public XMLStreamWriterImpl.ElementState peek() { return this.fElements[this.fDepth - 1]; }
    
    public boolean empty() { return !(this.fDepth > 0); }
  }
  
  class ElementState extends QName {
    public boolean isEmpty = false;
    
    public ElementState() {}
    
    public ElementState(String param1String1, String param1String2, String param1String3, String param1String4) { super(param1String1, param1String2, param1String3, param1String4); }
    
    public void setValues(String param1String1, String param1String2, String param1String3, String param1String4, boolean param1Boolean) {
      setValues(param1String1, param1String2, param1String3, param1String4);
      this.isEmpty = param1Boolean;
    }
  }
  
  class NamespaceContextImpl implements NamespaceContext {
    NamespaceContext userContext = null;
    
    NamespaceSupport internalContext = null;
    
    public String getNamespaceURI(String param1String) throws XMLStreamException {
      String str = null;
      if (param1String != null)
        param1String = XMLStreamWriterImpl.this.fSymbolTable.addSymbol(param1String); 
      if (this.internalContext != null) {
        str = this.internalContext.getURI(param1String);
        if (str != null)
          return str; 
      } 
      return (this.userContext != null) ? this.userContext.getNamespaceURI(param1String) : null;
    }
    
    public String getPrefix(String param1String) throws XMLStreamException {
      String str = null;
      if (param1String != null)
        param1String = XMLStreamWriterImpl.this.fSymbolTable.addSymbol(param1String); 
      if (this.internalContext != null) {
        str = this.internalContext.getPrefix(param1String);
        if (str != null)
          return str; 
      } 
      return (this.userContext != null) ? this.userContext.getPrefix(param1String) : null;
    }
    
    public Iterator getPrefixes(String param1String) {
      Vector vector = null;
      Iterator iterator = null;
      if (param1String != null)
        param1String = XMLStreamWriterImpl.this.fSymbolTable.addSymbol(param1String); 
      if (this.userContext != null)
        iterator = this.userContext.getPrefixes(param1String); 
      if (this.internalContext != null)
        vector = this.internalContext.getPrefixes(param1String); 
      if (vector == null && iterator != null)
        return iterator; 
      if (vector != null && iterator == null)
        return new ReadOnlyIterator(vector.iterator()); 
      if (vector != null && iterator != null) {
        String str = null;
        while (iterator.hasNext()) {
          str = (String)iterator.next();
          if (str != null)
            str = XMLStreamWriterImpl.this.fSymbolTable.addSymbol(str); 
          if (!vector.contains(str))
            vector.add(str); 
        } 
        return new ReadOnlyIterator(vector.iterator());
      } 
      return XMLStreamWriterImpl.this.fReadOnlyIterator;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\writers\XMLStreamWriterImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */