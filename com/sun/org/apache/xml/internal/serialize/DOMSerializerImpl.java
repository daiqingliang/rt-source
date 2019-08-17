package com.sun.org.apache.xml.internal.serialize;

import com.sun.org.apache.xerces.internal.dom.AbortException;
import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.DOMErrorImpl;
import com.sun.org.apache.xerces.internal.dom.DOMLocatorImpl;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.dom.DOMNormalizer;
import com.sun.org.apache.xerces.internal.dom.DOMStringListImpl;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;
import java.util.Vector;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.ls.LSSerializerFilter;

public class DOMSerializerImpl implements LSSerializer, DOMConfiguration {
  private XMLSerializer serializer;
  
  private XML11Serializer xml11Serializer;
  
  private DOMStringList fRecognizedParameters;
  
  protected short features = 0;
  
  protected static final short NAMESPACES = 1;
  
  protected static final short WELLFORMED = 2;
  
  protected static final short ENTITIES = 4;
  
  protected static final short CDATA = 8;
  
  protected static final short SPLITCDATA = 16;
  
  protected static final short COMMENTS = 32;
  
  protected static final short DISCARDDEFAULT = 64;
  
  protected static final short INFOSET = 128;
  
  protected static final short XMLDECL = 256;
  
  protected static final short NSDECL = 512;
  
  protected static final short DOM_ELEMENT_CONTENT_WHITESPACE = 1024;
  
  protected static final short FORMAT_PRETTY_PRINT = 2048;
  
  private DOMErrorHandler fErrorHandler = null;
  
  private final DOMErrorImpl fError = new DOMErrorImpl();
  
  private final DOMLocatorImpl fLocator = new DOMLocatorImpl();
  
  public DOMSerializerImpl() {
    this.features = (short)(this.features | true);
    this.features = (short)(this.features | 0x4);
    this.features = (short)(this.features | 0x20);
    this.features = (short)(this.features | 0x8);
    this.features = (short)(this.features | 0x10);
    this.features = (short)(this.features | 0x2);
    this.features = (short)(this.features | 0x200);
    this.features = (short)(this.features | 0x400);
    this.features = (short)(this.features | 0x40);
    this.features = (short)(this.features | 0x100);
    this.serializer = new XMLSerializer();
    initSerializer(this.serializer);
  }
  
  public DOMConfiguration getDomConfig() { return this; }
  
  public void setParameter(String paramString, Object paramObject) throws DOMException {
    if (paramObject instanceof Boolean) {
      boolean bool = ((Boolean)paramObject).booleanValue();
      if (paramString.equalsIgnoreCase("infoset")) {
        if (bool) {
          this.features = (short)(this.features & 0xFFFFFFFB);
          this.features = (short)(this.features & 0xFFFFFFF7);
          this.features = (short)(this.features | true);
          this.features = (short)(this.features | 0x200);
          this.features = (short)(this.features | 0x2);
          this.features = (short)(this.features | 0x20);
        } 
      } else if (paramString.equalsIgnoreCase("xml-declaration")) {
        this.features = (short)(bool ? (this.features | 0x100) : (this.features & 0xFFFFFEFF));
      } else if (paramString.equalsIgnoreCase("namespaces")) {
        this.features = (short)(bool ? (this.features | true) : (this.features & 0xFFFFFFFE));
        this.serializer.fNamespaces = bool;
      } else if (paramString.equalsIgnoreCase("split-cdata-sections")) {
        this.features = (short)(bool ? (this.features | 0x10) : (this.features & 0xFFFFFFEF));
      } else if (paramString.equalsIgnoreCase("discard-default-content")) {
        this.features = (short)(bool ? (this.features | 0x40) : (this.features & 0xFFFFFFBF));
      } else if (paramString.equalsIgnoreCase("well-formed")) {
        this.features = (short)(bool ? (this.features | 0x2) : (this.features & 0xFFFFFFFD));
      } else if (paramString.equalsIgnoreCase("entities")) {
        this.features = (short)(bool ? (this.features | 0x4) : (this.features & 0xFFFFFFFB));
      } else if (paramString.equalsIgnoreCase("cdata-sections")) {
        this.features = (short)(bool ? (this.features | 0x8) : (this.features & 0xFFFFFFF7));
      } else if (paramString.equalsIgnoreCase("comments")) {
        this.features = (short)(bool ? (this.features | 0x20) : (this.features & 0xFFFFFFDF));
      } else if (paramString.equalsIgnoreCase("format-pretty-print")) {
        this.features = (short)(bool ? (this.features | 0x800) : (this.features & 0xFFFFF7FF));
      } else if (paramString.equalsIgnoreCase("canonical-form") || paramString.equalsIgnoreCase("validate-if-schema") || paramString.equalsIgnoreCase("validate") || paramString.equalsIgnoreCase("check-character-normalization") || paramString.equalsIgnoreCase("datatype-normalization")) {
        if (bool) {
          String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { paramString });
          throw new DOMException((short)9, str);
        } 
      } else if (paramString.equalsIgnoreCase("namespace-declarations")) {
        this.features = (short)(bool ? (this.features | 0x200) : (this.features & 0xFFFFFDFF));
        this.serializer.fNamespacePrefixes = bool;
      } else if (paramString.equalsIgnoreCase("element-content-whitespace") || paramString.equalsIgnoreCase("ignore-unknown-character-denormalizations")) {
        if (!bool) {
          String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { paramString });
          throw new DOMException((short)9, str);
        } 
      } else {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[] { paramString });
        throw new DOMException((short)9, str);
      } 
    } else if (paramString.equalsIgnoreCase("error-handler")) {
      if (paramObject == null || paramObject instanceof DOMErrorHandler) {
        this.fErrorHandler = (DOMErrorHandler)paramObject;
      } else {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { paramString });
        throw new DOMException((short)17, str);
      } 
    } else {
      if (paramString.equalsIgnoreCase("resource-resolver") || paramString.equalsIgnoreCase("schema-location") || paramString.equalsIgnoreCase("schema-type") || (paramString.equalsIgnoreCase("normalize-characters") && paramObject != null)) {
        String str1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { paramString });
        throw new DOMException((short)9, str1);
      } 
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[] { paramString });
      throw new DOMException((short)8, str);
    } 
  }
  
  public boolean canSetParameter(String paramString, Object paramObject) {
    if (paramObject == null)
      return true; 
    if (paramObject instanceof Boolean) {
      boolean bool = ((Boolean)paramObject).booleanValue();
      if (paramString.equalsIgnoreCase("namespaces") || paramString.equalsIgnoreCase("split-cdata-sections") || paramString.equalsIgnoreCase("discard-default-content") || paramString.equalsIgnoreCase("xml-declaration") || paramString.equalsIgnoreCase("well-formed") || paramString.equalsIgnoreCase("infoset") || paramString.equalsIgnoreCase("entities") || paramString.equalsIgnoreCase("cdata-sections") || paramString.equalsIgnoreCase("comments") || paramString.equalsIgnoreCase("namespace-declarations") || paramString.equalsIgnoreCase("format-pretty-print"))
        return true; 
      if (paramString.equalsIgnoreCase("canonical-form") || paramString.equalsIgnoreCase("validate-if-schema") || paramString.equalsIgnoreCase("validate") || paramString.equalsIgnoreCase("check-character-normalization") || paramString.equalsIgnoreCase("datatype-normalization"))
        return !bool; 
      if (paramString.equalsIgnoreCase("element-content-whitespace") || paramString.equalsIgnoreCase("ignore-unknown-character-denormalizations"))
        return bool; 
    } else if ((paramString.equalsIgnoreCase("error-handler") && paramObject == null) || paramObject instanceof DOMErrorHandler) {
      return true;
    } 
    return false;
  }
  
  public DOMStringList getParameterNames() {
    if (this.fRecognizedParameters == null) {
      Vector vector = new Vector();
      vector.add("namespaces");
      vector.add("split-cdata-sections");
      vector.add("discard-default-content");
      vector.add("xml-declaration");
      vector.add("canonical-form");
      vector.add("validate-if-schema");
      vector.add("validate");
      vector.add("check-character-normalization");
      vector.add("datatype-normalization");
      vector.add("format-pretty-print");
      vector.add("well-formed");
      vector.add("infoset");
      vector.add("namespace-declarations");
      vector.add("element-content-whitespace");
      vector.add("entities");
      vector.add("cdata-sections");
      vector.add("comments");
      vector.add("ignore-unknown-character-denormalizations");
      vector.add("error-handler");
      this.fRecognizedParameters = new DOMStringListImpl(vector);
    } 
    return this.fRecognizedParameters;
  }
  
  public Object getParameter(String paramString) throws DOMException {
    if (paramString.equalsIgnoreCase("normalize-characters"))
      return null; 
    if (paramString.equalsIgnoreCase("comments"))
      return ((this.features & 0x20) != 0) ? Boolean.TRUE : Boolean.FALSE; 
    if (paramString.equalsIgnoreCase("namespaces"))
      return ((this.features & true) != 0) ? Boolean.TRUE : Boolean.FALSE; 
    if (paramString.equalsIgnoreCase("xml-declaration"))
      return ((this.features & 0x100) != 0) ? Boolean.TRUE : Boolean.FALSE; 
    if (paramString.equalsIgnoreCase("cdata-sections"))
      return ((this.features & 0x8) != 0) ? Boolean.TRUE : Boolean.FALSE; 
    if (paramString.equalsIgnoreCase("entities"))
      return ((this.features & 0x4) != 0) ? Boolean.TRUE : Boolean.FALSE; 
    if (paramString.equalsIgnoreCase("split-cdata-sections"))
      return ((this.features & 0x10) != 0) ? Boolean.TRUE : Boolean.FALSE; 
    if (paramString.equalsIgnoreCase("well-formed"))
      return ((this.features & 0x2) != 0) ? Boolean.TRUE : Boolean.FALSE; 
    if (paramString.equalsIgnoreCase("namespace-declarations"))
      return ((this.features & 0x200) != 0) ? Boolean.TRUE : Boolean.FALSE; 
    if (paramString.equalsIgnoreCase("format-pretty-print"))
      return ((this.features & 0x800) != 0) ? Boolean.TRUE : Boolean.FALSE; 
    if (paramString.equalsIgnoreCase("element-content-whitespace") || paramString.equalsIgnoreCase("ignore-unknown-character-denormalizations"))
      return Boolean.TRUE; 
    if (paramString.equalsIgnoreCase("discard-default-content"))
      return ((this.features & 0x40) != 0) ? Boolean.TRUE : Boolean.FALSE; 
    if (paramString.equalsIgnoreCase("infoset"))
      return ((this.features & 0x4) == 0 && (this.features & 0x8) == 0 && (this.features & true) != 0 && (this.features & 0x200) != 0 && (this.features & 0x2) != 0 && (this.features & 0x20) != 0) ? Boolean.TRUE : Boolean.FALSE; 
    if (paramString.equalsIgnoreCase("canonical-form") || paramString.equalsIgnoreCase("validate-if-schema") || paramString.equalsIgnoreCase("check-character-normalization") || paramString.equalsIgnoreCase("validate") || paramString.equalsIgnoreCase("validate-if-schema") || paramString.equalsIgnoreCase("datatype-normalization"))
      return Boolean.FALSE; 
    if (paramString.equalsIgnoreCase("error-handler"))
      return this.fErrorHandler; 
    if (paramString.equalsIgnoreCase("resource-resolver") || paramString.equalsIgnoreCase("schema-location") || paramString.equalsIgnoreCase("schema-type")) {
      String str1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { paramString });
      throw new DOMException((short)9, str1);
    } 
    String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[] { paramString });
    throw new DOMException((short)8, str);
  }
  
  public String writeToString(Node paramNode) throws DOMException, LSException {
    Document document = (paramNode.getNodeType() == 9) ? (Document)paramNode : paramNode.getOwnerDocument();
    Method method = null;
    XMLSerializer xMLSerializer = null;
    String str = null;
    try {
      method = document.getClass().getMethod("getXmlVersion", new Class[0]);
      if (method != null)
        str = (String)method.invoke(document, (Object[])null); 
    } catch (Exception exception) {}
    if (str != null && str.equals("1.1")) {
      if (this.xml11Serializer == null) {
        this.xml11Serializer = new XML11Serializer();
        initSerializer(this.xml11Serializer);
      } 
      copySettings(this.serializer, this.xml11Serializer);
      xMLSerializer = this.xml11Serializer;
    } else {
      xMLSerializer = this.serializer;
    } 
    StringWriter stringWriter = new StringWriter();
    try {
      prepareForSerialization(xMLSerializer, paramNode);
      xMLSerializer._format.setEncoding("UTF-16");
      xMLSerializer.setOutputCharStream(stringWriter);
      if (paramNode.getNodeType() == 9) {
        xMLSerializer.serialize((Document)paramNode);
      } else if (paramNode.getNodeType() == 11) {
        xMLSerializer.serialize((DocumentFragment)paramNode);
      } else if (paramNode.getNodeType() == 1) {
        xMLSerializer.serialize((Element)paramNode);
      } else if (paramNode.getNodeType() == 3 || paramNode.getNodeType() == 8 || paramNode.getNodeType() == 5 || paramNode.getNodeType() == 4 || paramNode.getNodeType() == 7) {
        xMLSerializer.serialize(paramNode);
      } else {
        String str1 = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "unable-to-serialize-node", null);
        if (xMLSerializer.fDOMErrorHandler != null) {
          DOMErrorImpl dOMErrorImpl = new DOMErrorImpl();
          dOMErrorImpl.fType = "unable-to-serialize-node";
          dOMErrorImpl.fMessage = str1;
          dOMErrorImpl.fSeverity = 3;
          xMLSerializer.fDOMErrorHandler.handleError(dOMErrorImpl);
        } 
        throw new LSException((short)82, str1);
      } 
    } catch (LSException lSException) {
      throw lSException;
    } catch (AbortException abortException) {
      return null;
    } catch (RuntimeException runtimeException) {
      throw (LSException)(new LSException((short)82, runtimeException.toString())).initCause(runtimeException);
    } catch (IOException iOException) {
      String str1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "STRING_TOO_LONG", new Object[] { iOException.getMessage() });
      throw (DOMException)(new DOMException((short)2, str1)).initCause(iOException);
    } 
    return stringWriter.toString();
  }
  
  public void setNewLine(String paramString) { this.serializer._format.setLineSeparator(paramString); }
  
  public String getNewLine() { return this.serializer._format.getLineSeparator(); }
  
  public LSSerializerFilter getFilter() { return this.serializer.fDOMFilter; }
  
  public void setFilter(LSSerializerFilter paramLSSerializerFilter) { this.serializer.fDOMFilter = paramLSSerializerFilter; }
  
  private void initSerializer(XMLSerializer paramXMLSerializer) {
    paramXMLSerializer.fNSBinder = new NamespaceSupport();
    paramXMLSerializer.fLocalNSBinder = new NamespaceSupport();
    paramXMLSerializer.fSymbolTable = new SymbolTable();
  }
  
  private void copySettings(XMLSerializer paramXMLSerializer1, XMLSerializer paramXMLSerializer2) {
    paramXMLSerializer2.fDOMErrorHandler = this.fErrorHandler;
    paramXMLSerializer2._format.setEncoding(paramXMLSerializer1._format.getEncoding());
    paramXMLSerializer2._format.setLineSeparator(paramXMLSerializer1._format.getLineSeparator());
    paramXMLSerializer2.fDOMFilter = paramXMLSerializer1.fDOMFilter;
  }
  
  public boolean write(Node paramNode, LSOutput paramLSOutput) throws LSException {
    if (paramNode == null)
      return false; 
    Method method = null;
    XMLSerializer xMLSerializer = null;
    String str1 = null;
    Document document = (paramNode.getNodeType() == 9) ? (Document)paramNode : paramNode.getOwnerDocument();
    try {
      method = document.getClass().getMethod("getXmlVersion", new Class[0]);
      if (method != null)
        str1 = (String)method.invoke(document, (Object[])null); 
    } catch (Exception exception) {}
    if (str1 != null && str1.equals("1.1")) {
      if (this.xml11Serializer == null) {
        this.xml11Serializer = new XML11Serializer();
        initSerializer(this.xml11Serializer);
      } 
      copySettings(this.serializer, this.xml11Serializer);
      xMLSerializer = this.xml11Serializer;
    } else {
      xMLSerializer = this.serializer;
    } 
    String str2 = null;
    if ((str2 = paramLSOutput.getEncoding()) == null) {
      try {
        Method method1 = document.getClass().getMethod("getInputEncoding", new Class[0]);
        if (method1 != null)
          str2 = (String)method1.invoke(document, (Object[])null); 
      } catch (Exception exception) {}
      if (str2 == null) {
        try {
          Method method1 = document.getClass().getMethod("getXmlEncoding", new Class[0]);
          if (method1 != null)
            str2 = (String)method1.invoke(document, (Object[])null); 
        } catch (Exception exception) {}
        if (str2 == null)
          str2 = "UTF-8"; 
      } 
    } 
    try {
      prepareForSerialization(xMLSerializer, paramNode);
      xMLSerializer._format.setEncoding(str2);
      OutputStream outputStream = paramLSOutput.getByteStream();
      Writer writer = paramLSOutput.getCharacterStream();
      String str = paramLSOutput.getSystemId();
      if (writer == null) {
        if (outputStream == null) {
          if (str == null) {
            String str6 = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "no-output-specified", null);
            if (xMLSerializer.fDOMErrorHandler != null) {
              DOMErrorImpl dOMErrorImpl = new DOMErrorImpl();
              dOMErrorImpl.fType = "no-output-specified";
              dOMErrorImpl.fMessage = str6;
              dOMErrorImpl.fSeverity = 3;
              xMLSerializer.fDOMErrorHandler.handleError(dOMErrorImpl);
            } 
            throw new LSException((short)82, str6);
          } 
          String str3 = XMLEntityManager.expandSystemId(str, null, true);
          URL uRL = new URL((str3 != null) ? str3 : str);
          OutputStream outputStream1 = null;
          String str4 = uRL.getProtocol();
          String str5 = uRL.getHost();
          if (str4.equals("file") && (str5 == null || str5.length() == 0 || str5.equals("localhost"))) {
            outputStream1 = new FileOutputStream(getPathWithoutEscapes(uRL.getFile()));
          } else {
            URLConnection uRLConnection = uRL.openConnection();
            uRLConnection.setDoInput(false);
            uRLConnection.setDoOutput(true);
            uRLConnection.setUseCaches(false);
            if (uRLConnection instanceof HttpURLConnection) {
              HttpURLConnection httpURLConnection = (HttpURLConnection)uRLConnection;
              httpURLConnection.setRequestMethod("PUT");
            } 
            outputStream1 = uRLConnection.getOutputStream();
          } 
          xMLSerializer.setOutputByteStream(outputStream1);
        } else {
          xMLSerializer.setOutputByteStream(outputStream);
        } 
      } else {
        xMLSerializer.setOutputCharStream(writer);
      } 
      if (paramNode.getNodeType() == 9) {
        xMLSerializer.serialize((Document)paramNode);
      } else if (paramNode.getNodeType() == 11) {
        xMLSerializer.serialize((DocumentFragment)paramNode);
      } else if (paramNode.getNodeType() == 1) {
        xMLSerializer.serialize((Element)paramNode);
      } else if (paramNode.getNodeType() == 3 || paramNode.getNodeType() == 8 || paramNode.getNodeType() == 5 || paramNode.getNodeType() == 4 || paramNode.getNodeType() == 7) {
        xMLSerializer.serialize(paramNode);
      } else {
        return false;
      } 
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      if (xMLSerializer.fDOMErrorHandler != null) {
        DOMErrorImpl dOMErrorImpl = new DOMErrorImpl();
        dOMErrorImpl.fException = unsupportedEncodingException;
        dOMErrorImpl.fType = "unsupported-encoding";
        dOMErrorImpl.fMessage = unsupportedEncodingException.getMessage();
        dOMErrorImpl.fSeverity = 3;
        xMLSerializer.fDOMErrorHandler.handleError(dOMErrorImpl);
      } 
      throw new LSException((short)82, DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "unsupported-encoding", null));
    } catch (LSException lSException) {
      throw lSException;
    } catch (AbortException abortException) {
      return false;
    } catch (RuntimeException runtimeException) {
      throw (LSException)DOMUtil.createLSException((short)82, runtimeException).fillInStackTrace();
    } catch (Exception exception) {
      if (xMLSerializer.fDOMErrorHandler != null) {
        DOMErrorImpl dOMErrorImpl = new DOMErrorImpl();
        dOMErrorImpl.fException = exception;
        dOMErrorImpl.fMessage = exception.getMessage();
        dOMErrorImpl.fSeverity = 2;
        xMLSerializer.fDOMErrorHandler.handleError(dOMErrorImpl);
      } 
      throw (LSException)DOMUtil.createLSException((short)82, exception).fillInStackTrace();
    } 
    return true;
  }
  
  public boolean writeToURI(Node paramNode, String paramString) throws LSException {
    if (paramNode == null)
      return false; 
    Method method = null;
    XMLSerializer xMLSerializer = null;
    String str1 = null;
    String str2 = null;
    Document document = (paramNode.getNodeType() == 9) ? (Document)paramNode : paramNode.getOwnerDocument();
    try {
      method = document.getClass().getMethod("getXmlVersion", new Class[0]);
      if (method != null)
        str1 = (String)method.invoke(document, (Object[])null); 
    } catch (Exception exception) {}
    if (str1 != null && str1.equals("1.1")) {
      if (this.xml11Serializer == null) {
        this.xml11Serializer = new XML11Serializer();
        initSerializer(this.xml11Serializer);
      } 
      copySettings(this.serializer, this.xml11Serializer);
      xMLSerializer = this.xml11Serializer;
    } else {
      xMLSerializer = this.serializer;
    } 
    try {
      Method method1 = document.getClass().getMethod("getInputEncoding", new Class[0]);
      if (method1 != null)
        str2 = (String)method1.invoke(document, (Object[])null); 
    } catch (Exception exception) {}
    if (str2 == null) {
      try {
        Method method1 = document.getClass().getMethod("getXmlEncoding", new Class[0]);
        if (method1 != null)
          str2 = (String)method1.invoke(document, (Object[])null); 
      } catch (Exception exception) {}
      if (str2 == null)
        str2 = "UTF-8"; 
    } 
    try {
      prepareForSerialization(xMLSerializer, paramNode);
      xMLSerializer._format.setEncoding(str2);
      String str3 = XMLEntityManager.expandSystemId(paramString, null, true);
      URL uRL = new URL((str3 != null) ? str3 : paramString);
      OutputStream outputStream = null;
      String str4 = uRL.getProtocol();
      String str5 = uRL.getHost();
      if (str4.equals("file") && (str5 == null || str5.length() == 0 || str5.equals("localhost"))) {
        outputStream = new FileOutputStream(getPathWithoutEscapes(uRL.getFile()));
      } else {
        URLConnection uRLConnection = uRL.openConnection();
        uRLConnection.setDoInput(false);
        uRLConnection.setDoOutput(true);
        uRLConnection.setUseCaches(false);
        if (uRLConnection instanceof HttpURLConnection) {
          HttpURLConnection httpURLConnection = (HttpURLConnection)uRLConnection;
          httpURLConnection.setRequestMethod("PUT");
        } 
        outputStream = uRLConnection.getOutputStream();
      } 
      xMLSerializer.setOutputByteStream(outputStream);
      if (paramNode.getNodeType() == 9) {
        xMLSerializer.serialize((Document)paramNode);
      } else if (paramNode.getNodeType() == 11) {
        xMLSerializer.serialize((DocumentFragment)paramNode);
      } else if (paramNode.getNodeType() == 1) {
        xMLSerializer.serialize((Element)paramNode);
      } else if (paramNode.getNodeType() == 3 || paramNode.getNodeType() == 8 || paramNode.getNodeType() == 5 || paramNode.getNodeType() == 4 || paramNode.getNodeType() == 7) {
        xMLSerializer.serialize(paramNode);
      } else {
        return false;
      } 
    } catch (LSException lSException) {
      throw lSException;
    } catch (AbortException abortException) {
      return false;
    } catch (RuntimeException runtimeException) {
      throw (LSException)DOMUtil.createLSException((short)82, runtimeException).fillInStackTrace();
    } catch (Exception exception) {
      if (xMLSerializer.fDOMErrorHandler != null) {
        DOMErrorImpl dOMErrorImpl = new DOMErrorImpl();
        dOMErrorImpl.fException = exception;
        dOMErrorImpl.fMessage = exception.getMessage();
        dOMErrorImpl.fSeverity = 2;
        xMLSerializer.fDOMErrorHandler.handleError(dOMErrorImpl);
      } 
      throw (LSException)DOMUtil.createLSException((short)82, exception).fillInStackTrace();
    } 
    return true;
  }
  
  private void prepareForSerialization(XMLSerializer paramXMLSerializer, Node paramNode) {
    paramXMLSerializer.reset();
    paramXMLSerializer.features = this.features;
    paramXMLSerializer.fDOMErrorHandler = this.fErrorHandler;
    paramXMLSerializer.fNamespaces = ((this.features & true) != 0);
    paramXMLSerializer.fNamespacePrefixes = ((this.features & 0x200) != 0);
    paramXMLSerializer._format.setOmitComments(((this.features & 0x20) == 0));
    paramXMLSerializer._format.setOmitXMLDeclaration(((this.features & 0x100) == 0));
    paramXMLSerializer._format.setIndenting(((this.features & 0x800) != 0));
    if ((this.features & 0x2) != 0) {
      Node node = paramNode;
      boolean bool = true;
      Document document = (paramNode.getNodeType() == 9) ? (Document)paramNode : paramNode.getOwnerDocument();
      try {
        Method method = document.getClass().getMethod("isXMLVersionChanged()", new Class[0]);
        if (method != null)
          bool = ((Boolean)method.invoke(document, (Object[])null)).booleanValue(); 
      } catch (Exception exception) {}
      if (paramNode.getFirstChild() != null) {
        while (paramNode != null) {
          verify(paramNode, bool, false);
          Node node1 = paramNode.getFirstChild();
          while (node1 == null) {
            node1 = paramNode.getNextSibling();
            if (node1 == null) {
              paramNode = paramNode.getParentNode();
              if (node == paramNode) {
                node1 = null;
                break;
              } 
              node1 = paramNode.getNextSibling();
            } 
          } 
          paramNode = node1;
        } 
      } else {
        verify(paramNode, bool, false);
      } 
    } 
  }
  
  private void verify(Node paramNode, boolean paramBoolean1, boolean paramBoolean2) {
    String str;
    NamedNodeMap namedNodeMap;
    ProcessingInstruction processingInstruction;
    short s = paramNode.getNodeType();
    this.fLocator.fRelatedNode = paramNode;
    switch (s) {
      case 1:
        if (paramBoolean1) {
          boolean bool;
          if ((this.features & true) != 0) {
            bool = CoreDocumentImpl.isValidQName(paramNode.getPrefix(), paramNode.getLocalName(), paramBoolean2);
          } else {
            bool = CoreDocumentImpl.isXMLName(paramNode.getNodeName(), paramBoolean2);
          } 
          if (!bool && !bool && this.fErrorHandler != null) {
            String str1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[] { "Element", paramNode.getNodeName() });
            DOMNormalizer.reportDOMError(this.fErrorHandler, this.fError, this.fLocator, str1, (short)3, "wf-invalid-character-in-node-name");
          } 
        } 
        namedNodeMap = paramNode.hasAttributes() ? paramNode.getAttributes() : null;
        if (namedNodeMap != null)
          for (byte b = 0; b < namedNodeMap.getLength(); b++) {
            Attr attr = (Attr)namedNodeMap.item(b);
            this.fLocator.fRelatedNode = attr;
            DOMNormalizer.isAttrValueWF(this.fErrorHandler, this.fError, this.fLocator, namedNodeMap, attr, attr.getValue(), paramBoolean2);
            if (paramBoolean1) {
              boolean bool = CoreDocumentImpl.isXMLName(attr.getNodeName(), paramBoolean2);
              if (!bool) {
                String str1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[] { "Attr", paramNode.getNodeName() });
                DOMNormalizer.reportDOMError(this.fErrorHandler, this.fError, this.fLocator, str1, (short)3, "wf-invalid-character-in-node-name");
              } 
            } 
          }  
        break;
      case 8:
        if ((this.features & 0x20) != 0)
          DOMNormalizer.isCommentWF(this.fErrorHandler, this.fError, this.fLocator, ((Comment)paramNode).getData(), paramBoolean2); 
        break;
      case 5:
        if (paramBoolean1 && (this.features & 0x4) != 0)
          CoreDocumentImpl.isXMLName(paramNode.getNodeName(), paramBoolean2); 
        break;
      case 4:
        DOMNormalizer.isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, paramNode.getNodeValue(), paramBoolean2);
        break;
      case 3:
        DOMNormalizer.isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, paramNode.getNodeValue(), paramBoolean2);
        break;
      case 7:
        processingInstruction = (ProcessingInstruction)paramNode;
        str = processingInstruction.getTarget();
        if (paramBoolean1) {
          boolean bool;
          if (paramBoolean2) {
            bool = XML11Char.isXML11ValidName(str);
          } else {
            bool = XMLChar.isValidName(str);
          } 
          if (!bool) {
            String str1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[] { "Element", paramNode.getNodeName() });
            DOMNormalizer.reportDOMError(this.fErrorHandler, this.fError, this.fLocator, str1, (short)3, "wf-invalid-character-in-node-name");
          } 
        } 
        DOMNormalizer.isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, processingInstruction.getData(), paramBoolean2);
        break;
    } 
  }
  
  private String getPathWithoutEscapes(String paramString) {
    if (paramString != null && paramString.length() != 0 && paramString.indexOf('%') != -1) {
      StringTokenizer stringTokenizer = new StringTokenizer(paramString, "%");
      StringBuffer stringBuffer = new StringBuffer(paramString.length());
      int i = stringTokenizer.countTokens();
      stringBuffer.append(stringTokenizer.nextToken());
      for (byte b = 1; b < i; b++) {
        String str = stringTokenizer.nextToken();
        stringBuffer.append((char)Integer.valueOf(str.substring(0, 2), 16).intValue());
        stringBuffer.append(str.substring(2));
      } 
      return stringBuffer.toString();
    } 
    return paramString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serialize\DOMSerializerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */