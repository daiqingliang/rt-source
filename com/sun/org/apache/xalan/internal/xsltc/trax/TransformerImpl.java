package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.DOMCache;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.dom.DOMWSFilter;
import com.sun.org.apache.xalan.internal.xsltc.dom.SAXImpl;
import com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.runtime.MessageHandler;
import com.sun.org.apache.xalan.internal.xsltc.runtime.output.TransletOutputHandlerFactory;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import com.sun.org.apache.xml.internal.utils.XMLReaderManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownServiceException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import jdk.xml.internal.JdkXmlUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

public final class TransformerImpl extends Transformer implements DOMCache, ErrorListener {
  private static final String LEXICAL_HANDLER_PROPERTY = "http://xml.org/sax/properties/lexical-handler";
  
  private static final String NAMESPACE_PREFIXES_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
  
  private AbstractTranslet _translet = null;
  
  private String _method = null;
  
  private String _encoding = null;
  
  private String _sourceSystemId = null;
  
  private ErrorListener _errorListener = this;
  
  private URIResolver _uriResolver = null;
  
  private Properties _properties;
  
  private Properties _propertiesClone;
  
  private TransletOutputHandlerFactory _tohFactory = null;
  
  private DOM _dom = null;
  
  private int _indentNumber;
  
  private TransformerFactoryImpl _tfactory = null;
  
  private OutputStream _ostream = null;
  
  private XSLTCDTMManager _dtmManager = null;
  
  private XMLReaderManager _readerManager;
  
  private boolean _isIdentity = false;
  
  private boolean _isSecureProcessing = false;
  
  private boolean _overrideDefaultParser;
  
  private String _accessExternalDTD = "all";
  
  private XMLSecurityManager _securityManager;
  
  private Map<String, Object> _parameters = null;
  
  protected TransformerImpl(Properties paramProperties, int paramInt, TransformerFactoryImpl paramTransformerFactoryImpl) {
    this(null, paramProperties, paramInt, paramTransformerFactoryImpl);
    this._isIdentity = true;
  }
  
  protected TransformerImpl(Translet paramTranslet, Properties paramProperties, int paramInt, TransformerFactoryImpl paramTransformerFactoryImpl) {
    this._translet = (AbstractTranslet)paramTranslet;
    this._properties = createOutputProperties(paramProperties);
    this._propertiesClone = (Properties)this._properties.clone();
    this._indentNumber = paramInt;
    this._tfactory = paramTransformerFactoryImpl;
    this._overrideDefaultParser = this._tfactory.overrideDefaultParser();
    this._accessExternalDTD = (String)this._tfactory.getAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD");
    this._securityManager = (XMLSecurityManager)this._tfactory.getAttribute("http://apache.org/xml/properties/security-manager");
    this._readerManager = XMLReaderManager.getInstance(this._overrideDefaultParser);
    this._readerManager.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", this._accessExternalDTD);
    this._readerManager.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", this._isSecureProcessing);
    this._readerManager.setProperty("http://apache.org/xml/properties/security-manager", this._securityManager);
  }
  
  public boolean isSecureProcessing() { return this._isSecureProcessing; }
  
  public void setSecureProcessing(boolean paramBoolean) {
    this._isSecureProcessing = paramBoolean;
    this._readerManager.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", this._isSecureProcessing);
  }
  
  public boolean overrideDefaultParser() { return this._overrideDefaultParser; }
  
  public void setOverrideDefaultParser(boolean paramBoolean) { this._overrideDefaultParser = paramBoolean; }
  
  protected AbstractTranslet getTranslet() { return this._translet; }
  
  public boolean isIdentity() { return this._isIdentity; }
  
  public void transform(Source paramSource, Result paramResult) throws TransformerException {
    if (!this._isIdentity) {
      if (this._translet == null) {
        ErrorMsg errorMsg = new ErrorMsg("JAXP_NO_TRANSLET_ERR");
        throw new TransformerException(errorMsg.toString());
      } 
      transferOutputProperties(this._translet);
    } 
    SerializationHandler serializationHandler = getOutputHandler(paramResult);
    if (serializationHandler == null) {
      ErrorMsg errorMsg = new ErrorMsg("JAXP_NO_HANDLER_ERR");
      throw new TransformerException(errorMsg.toString());
    } 
    if (this._uriResolver != null && !this._isIdentity)
      this._translet.setDOMCache(this); 
    if (this._isIdentity)
      transferOutputProperties(serializationHandler); 
    transform(paramSource, serializationHandler, this._encoding);
    try {
      if (paramResult instanceof DOMResult) {
        ((DOMResult)paramResult).setNode(this._tohFactory.getNode());
      } else if (paramResult instanceof StAXResult) {
        if (((StAXResult)paramResult).getXMLEventWriter() != null) {
          this._tohFactory.getXMLEventWriter().flush();
        } else if (((StAXResult)paramResult).getXMLStreamWriter() != null) {
          this._tohFactory.getXMLStreamWriter().flush();
        } 
      } 
    } catch (Exception exception) {
      System.out.println("Result writing error");
    } 
  }
  
  public SerializationHandler getOutputHandler(Result paramResult) throws TransformerException {
    this._method = (String)this._properties.get("method");
    this._encoding = this._properties.getProperty("encoding");
    this._tohFactory = TransletOutputHandlerFactory.newInstance(this._overrideDefaultParser);
    this._tohFactory.setEncoding(this._encoding);
    if (this._method != null)
      this._tohFactory.setOutputMethod(this._method); 
    if (this._indentNumber >= 0)
      this._tohFactory.setIndentNumber(this._indentNumber); 
    try {
      if (paramResult instanceof SAXResult) {
        SAXResult sAXResult = (SAXResult)paramResult;
        ContentHandler contentHandler = sAXResult.getHandler();
        this._tohFactory.setHandler(contentHandler);
        LexicalHandler lexicalHandler = sAXResult.getLexicalHandler();
        if (lexicalHandler != null)
          this._tohFactory.setLexicalHandler(lexicalHandler); 
        this._tohFactory.setOutputType(1);
        return this._tohFactory.getSerializationHandler();
      } 
      if (paramResult instanceof StAXResult) {
        if (((StAXResult)paramResult).getXMLEventWriter() != null) {
          this._tohFactory.setXMLEventWriter(((StAXResult)paramResult).getXMLEventWriter());
        } else if (((StAXResult)paramResult).getXMLStreamWriter() != null) {
          this._tohFactory.setXMLStreamWriter(((StAXResult)paramResult).getXMLStreamWriter());
        } 
        this._tohFactory.setOutputType(3);
        return this._tohFactory.getSerializationHandler();
      } 
      if (paramResult instanceof DOMResult) {
        this._tohFactory.setNode(((DOMResult)paramResult).getNode());
        this._tohFactory.setNextSibling(((DOMResult)paramResult).getNextSibling());
        this._tohFactory.setOutputType(2);
        return this._tohFactory.getSerializationHandler();
      } 
      if (paramResult instanceof StreamResult) {
        StreamResult streamResult = (StreamResult)paramResult;
        this._tohFactory.setOutputType(0);
        Writer writer = streamResult.getWriter();
        if (writer != null) {
          this._tohFactory.setWriter(writer);
          return this._tohFactory.getSerializationHandler();
        } 
        OutputStream outputStream = streamResult.getOutputStream();
        if (outputStream != null) {
          this._tohFactory.setOutputStream(outputStream);
          return this._tohFactory.getSerializationHandler();
        } 
        String str = paramResult.getSystemId();
        if (str == null) {
          ErrorMsg errorMsg = new ErrorMsg("JAXP_NO_RESULT_ERR");
          throw new TransformerException(errorMsg.toString());
        } 
        if (str.startsWith("file:")) {
          try {
            URI uRI = new URI(str);
            str = "file:";
            String str1 = uRI.getHost();
            String str2 = uRI.getPath();
            if (str2 == null)
              str2 = ""; 
            if (str1 != null) {
              str = str + "//" + str1 + str2;
            } else {
              str = str + "//" + str2;
            } 
          } catch (Exception exception) {}
          URL uRL = new URL(str);
          this._ostream = new FileOutputStream(uRL.getFile());
          this._tohFactory.setOutputStream(this._ostream);
          return this._tohFactory.getSerializationHandler();
        } 
        if (str.startsWith("http:")) {
          URL uRL = new URL(str);
          URLConnection uRLConnection = uRL.openConnection();
          this._tohFactory.setOutputStream(this._ostream = uRLConnection.getOutputStream());
          return this._tohFactory.getSerializationHandler();
        } 
        this._tohFactory.setOutputStream(this._ostream = new FileOutputStream(new File(str)));
        return this._tohFactory.getSerializationHandler();
      } 
    } catch (UnknownServiceException unknownServiceException) {
      throw new TransformerException(unknownServiceException);
    } catch (ParserConfigurationException parserConfigurationException) {
      throw new TransformerException(parserConfigurationException);
    } catch (IOException iOException) {
      throw new TransformerException(iOException);
    } 
    return null;
  }
  
  protected void setDOM(DOM paramDOM) { this._dom = paramDOM; }
  
  private DOM getDOM(Source paramSource) throws TransformerException {
    try {
      DOM dOM;
      if (paramSource != null) {
        DTMWSFilter dTMWSFilter;
        if (this._translet != null && this._translet instanceof com.sun.org.apache.xalan.internal.xsltc.StripFilter) {
          dTMWSFilter = new DOMWSFilter(this._translet);
        } else {
          dTMWSFilter = null;
        } 
        boolean bool = (this._translet != null) ? this._translet.hasIdCall() : 0;
        if (this._dtmManager == null) {
          this._dtmManager = this._tfactory.createNewDTMManagerInstance();
          this._dtmManager.setOverrideDefaultParser(this._overrideDefaultParser);
        } 
        dOM = (DOM)this._dtmManager.getDTM(paramSource, false, dTMWSFilter, true, false, false, 0, bool);
      } else if (this._dom != null) {
        dOM = this._dom;
        this._dom = null;
      } else {
        return null;
      } 
      if (!this._isIdentity)
        this._translet.prepassDocument(dOM); 
      return dOM;
    } catch (Exception exception) {
      if (this._errorListener != null)
        postErrorToListener(exception.getMessage()); 
      throw new TransformerException(exception);
    } 
  }
  
  protected TransformerFactoryImpl getTransformerFactory() { return this._tfactory; }
  
  protected TransletOutputHandlerFactory getTransletOutputHandlerFactory() { return this._tohFactory; }
  
  private void transformIdentity(Source paramSource, SerializationHandler paramSerializationHandler) throws Exception {
    if (paramSource != null)
      this._sourceSystemId = paramSource.getSystemId(); 
    if (paramSource instanceof StreamSource) {
      StreamSource streamSource = (StreamSource)paramSource;
      InputStream inputStream = streamSource.getInputStream();
      Reader reader = streamSource.getReader();
      xMLReader = this._readerManager.getXMLReader();
      try {
        InputSource inputSource;
        try {
          xMLReader.setProperty("http://xml.org/sax/properties/lexical-handler", paramSerializationHandler);
          xMLReader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        } catch (SAXException null) {}
        xMLReader.setContentHandler(paramSerializationHandler);
        if (inputStream != null) {
          inputSource = new InputSource(inputStream);
          inputSource.setSystemId(this._sourceSystemId);
        } else if (reader != null) {
          inputSource = new InputSource(reader);
          inputSource.setSystemId(this._sourceSystemId);
        } else if (this._sourceSystemId != null) {
          inputSource = new InputSource(this._sourceSystemId);
        } else {
          ErrorMsg errorMsg = new ErrorMsg("JAXP_NO_SOURCE_ERR");
          throw new TransformerException(errorMsg.toString());
        } 
        xMLReader.parse(inputSource);
      } finally {
        this._readerManager.releaseXMLReader(xMLReader);
      } 
    } else if (paramSource instanceof SAXSource) {
      SAXSource sAXSource = (SAXSource)paramSource;
      xMLReader = sAXSource.getXMLReader();
      InputSource inputSource = sAXSource.getInputSource();
      bool = true;
      try {
        if (xMLReader == null) {
          xMLReader = this._readerManager.getXMLReader();
          bool = false;
        } 
        try {
          xMLReader.setProperty("http://xml.org/sax/properties/lexical-handler", paramSerializationHandler);
          xMLReader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        } catch (SAXException sAXException) {}
        xMLReader.setContentHandler(paramSerializationHandler);
        xMLReader.parse(inputSource);
      } finally {
        if (!bool)
          this._readerManager.releaseXMLReader(xMLReader); 
      } 
    } else if (paramSource instanceof StAXSource) {
      StAXSource stAXSource = (StAXSource)paramSource;
      if (stAXSource.getXMLEventReader() != null) {
        XMLEventReader xMLEventReader = stAXSource.getXMLEventReader();
        StAXEvent2SAX stAXEvent2SAX = new StAXEvent2SAX(xMLEventReader);
        stAXEvent2SAX.setContentHandler(paramSerializationHandler);
        stAXEvent2SAX.parse();
        paramSerializationHandler.flushPending();
      } else if (stAXSource.getXMLStreamReader() != null) {
        XMLStreamReader xMLStreamReader = stAXSource.getXMLStreamReader();
        StAXStream2SAX stAXStream2SAX = new StAXStream2SAX(xMLStreamReader);
        stAXStream2SAX.setContentHandler(paramSerializationHandler);
        stAXStream2SAX.parse();
        paramSerializationHandler.flushPending();
      } 
    } else if (paramSource instanceof DOMSource) {
      DOMSource dOMSource = (DOMSource)paramSource;
      (new DOM2TO(dOMSource.getNode(), paramSerializationHandler)).parse();
    } else if (paramSource instanceof XSLTCSource) {
      DOM dOM = ((XSLTCSource)paramSource).getDOM(null, this._translet);
      ((SAXImpl)dOM).copy(paramSerializationHandler);
    } else {
      ErrorMsg errorMsg = new ErrorMsg("JAXP_NO_SOURCE_ERR");
      throw new TransformerException(errorMsg.toString());
    } 
  }
  
  private void transform(Source paramSource, SerializationHandler paramSerializationHandler, String paramString) throws TransformerException {
    try {
      if ((paramSource instanceof StreamSource && paramSource.getSystemId() == null && ((StreamSource)paramSource).getInputStream() == null && ((StreamSource)paramSource).getReader() == null) || (paramSource instanceof SAXSource && ((SAXSource)paramSource).getInputSource() == null && ((SAXSource)paramSource).getXMLReader() == null) || (paramSource instanceof DOMSource && ((DOMSource)paramSource).getNode() == null)) {
        DocumentBuilderFactory documentBuilderFactory = JdkXmlUtils.getDOMFactory(this._overrideDefaultParser);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        String str = paramSource.getSystemId();
        paramSource = new DOMSource(documentBuilder.newDocument());
        if (str != null)
          paramSource.setSystemId(str); 
      } 
      if (this._isIdentity) {
        transformIdentity(paramSource, paramSerializationHandler);
      } else {
        this._translet.transform(getDOM(paramSource), paramSerializationHandler);
      } 
    } catch (TransletException transletException) {
      if (this._errorListener != null)
        postErrorToListener(transletException.getMessage()); 
      throw new TransformerException(transletException);
    } catch (RuntimeException runtimeException) {
      if (this._errorListener != null)
        postErrorToListener(runtimeException.getMessage()); 
      throw new TransformerException(runtimeException);
    } catch (Exception exception) {
      if (this._errorListener != null)
        postErrorToListener(exception.getMessage()); 
      throw new TransformerException(exception);
    } finally {
      this._dtmManager = null;
    } 
    if (this._ostream != null) {
      try {
        this._ostream.close();
      } catch (IOException iOException) {}
      this._ostream = null;
    } 
  }
  
  public ErrorListener getErrorListener() { return this._errorListener; }
  
  public void setErrorListener(ErrorListener paramErrorListener) throws IllegalArgumentException {
    if (paramErrorListener == null) {
      ErrorMsg errorMsg = new ErrorMsg("ERROR_LISTENER_NULL_ERR", "Transformer");
      throw new IllegalArgumentException(errorMsg.toString());
    } 
    this._errorListener = paramErrorListener;
    if (this._translet != null)
      this._translet.setMessageHandler(new MessageHandler(this._errorListener)); 
  }
  
  private void postErrorToListener(String paramString) {
    try {
      this._errorListener.error(new TransformerException(paramString));
    } catch (TransformerException transformerException) {}
  }
  
  private void postWarningToListener(String paramString) {
    try {
      this._errorListener.warning(new TransformerException(paramString));
    } catch (TransformerException transformerException) {}
  }
  
  public Properties getOutputProperties() { return (Properties)this._properties.clone(); }
  
  public String getOutputProperty(String paramString) throws IllegalArgumentException {
    if (!validOutputProperty(paramString)) {
      ErrorMsg errorMsg = new ErrorMsg("JAXP_UNKNOWN_PROP_ERR", paramString);
      throw new IllegalArgumentException(errorMsg.toString());
    } 
    return this._properties.getProperty(paramString);
  }
  
  public void setOutputProperties(Properties paramProperties) throws IllegalArgumentException {
    if (paramProperties != null) {
      Enumeration enumeration = paramProperties.propertyNames();
      while (enumeration.hasMoreElements()) {
        String str = (String)enumeration.nextElement();
        if (isDefaultProperty(str, paramProperties))
          continue; 
        if (validOutputProperty(str)) {
          this._properties.setProperty(str, paramProperties.getProperty(str));
          continue;
        } 
        ErrorMsg errorMsg = new ErrorMsg("JAXP_UNKNOWN_PROP_ERR", str);
        throw new IllegalArgumentException(errorMsg.toString());
      } 
    } else {
      this._properties = this._propertiesClone;
    } 
  }
  
  public void setOutputProperty(String paramString1, String paramString2) throws IllegalArgumentException {
    if (!validOutputProperty(paramString1)) {
      ErrorMsg errorMsg = new ErrorMsg("JAXP_UNKNOWN_PROP_ERR", paramString1);
      throw new IllegalArgumentException(errorMsg.toString());
    } 
    this._properties.setProperty(paramString1, paramString2);
  }
  
  private void transferOutputProperties(AbstractTranslet paramAbstractTranslet) {
    if (this._properties == null)
      return; 
    Enumeration enumeration = this._properties.propertyNames();
    while (enumeration.hasMoreElements()) {
      String str1 = (String)enumeration.nextElement();
      String str2 = (String)this._properties.get(str1);
      if (str2 == null)
        continue; 
      if (str1.equals("encoding")) {
        paramAbstractTranslet._encoding = str2;
        continue;
      } 
      if (str1.equals("method")) {
        paramAbstractTranslet._method = str2;
        continue;
      } 
      if (str1.equals("doctype-public")) {
        paramAbstractTranslet._doctypePublic = str2;
        continue;
      } 
      if (str1.equals("doctype-system")) {
        paramAbstractTranslet._doctypeSystem = str2;
        continue;
      } 
      if (str1.equals("media-type")) {
        paramAbstractTranslet._mediaType = str2;
        continue;
      } 
      if (str1.equals("standalone")) {
        paramAbstractTranslet._standalone = str2;
        continue;
      } 
      if (str1.equals("version")) {
        paramAbstractTranslet._version = str2;
        continue;
      } 
      if (str1.equals("omit-xml-declaration")) {
        paramAbstractTranslet._omitHeader = (str2 != null && str2.toLowerCase().equals("yes"));
        continue;
      } 
      if (str1.equals("indent")) {
        paramAbstractTranslet._indent = (str2 != null && str2.toLowerCase().equals("yes"));
        continue;
      } 
      if (str1.equals("{http://xml.apache.org/xslt}indent-amount")) {
        if (str2 != null)
          paramAbstractTranslet._indentamount = Integer.parseInt(str2); 
        continue;
      } 
      if (str1.equals("{http://xml.apache.org/xalan}indent-amount")) {
        if (str2 != null)
          paramAbstractTranslet._indentamount = Integer.parseInt(str2); 
        continue;
      } 
      if (str1.equals("cdata-section-elements")) {
        if (str2 != null) {
          paramAbstractTranslet._cdata = null;
          StringTokenizer stringTokenizer = new StringTokenizer(str2);
          while (stringTokenizer.hasMoreTokens())
            paramAbstractTranslet.addCdataElement(stringTokenizer.nextToken()); 
        } 
        continue;
      } 
      if (str1.equals("http://www.oracle.com/xml/is-standalone") && str2 != null && str2.equals("yes"))
        paramAbstractTranslet._isStandalone = true; 
    } 
  }
  
  public void transferOutputProperties(SerializationHandler paramSerializationHandler) {
    if (this._properties == null)
      return; 
    String str1 = null;
    String str2 = null;
    Enumeration enumeration = this._properties.propertyNames();
    while (enumeration.hasMoreElements()) {
      String str3 = (String)enumeration.nextElement();
      String str4 = (String)this._properties.get(str3);
      if (str4 == null)
        continue; 
      if (str3.equals("doctype-public")) {
        str1 = str4;
        continue;
      } 
      if (str3.equals("doctype-system")) {
        str2 = str4;
        continue;
      } 
      if (str3.equals("media-type")) {
        paramSerializationHandler.setMediaType(str4);
        continue;
      } 
      if (str3.equals("standalone")) {
        paramSerializationHandler.setStandalone(str4);
        continue;
      } 
      if (str3.equals("version")) {
        paramSerializationHandler.setVersion(str4);
        continue;
      } 
      if (str3.equals("omit-xml-declaration")) {
        paramSerializationHandler.setOmitXMLDeclaration((str4 != null && str4.toLowerCase().equals("yes")));
        continue;
      } 
      if (str3.equals("indent")) {
        paramSerializationHandler.setIndent((str4 != null && str4.toLowerCase().equals("yes")));
        continue;
      } 
      if (str3.equals("{http://xml.apache.org/xslt}indent-amount")) {
        if (str4 != null)
          paramSerializationHandler.setIndentAmount(Integer.parseInt(str4)); 
        continue;
      } 
      if (str3.equals("{http://xml.apache.org/xalan}indent-amount")) {
        if (str4 != null)
          paramSerializationHandler.setIndentAmount(Integer.parseInt(str4)); 
        continue;
      } 
      if (str3.equals("http://www.oracle.com/xml/is-standalone")) {
        if (str4 != null && str4.equals("yes"))
          paramSerializationHandler.setIsStandalone(true); 
        continue;
      } 
      if (str3.equals("cdata-section-elements") && str4 != null) {
        StringTokenizer stringTokenizer = new StringTokenizer(str4);
        Vector vector = null;
        while (stringTokenizer.hasMoreTokens()) {
          String str6;
          Object object;
          String str5 = stringTokenizer.nextToken();
          int i = str5.lastIndexOf(':');
          if (i > 0) {
            object = str5.substring(0, i);
            str6 = str5.substring(i + 1);
          } else {
            object = null;
            str6 = str5;
          } 
          if (vector == null)
            vector = new Vector(); 
          vector.addElement(object);
          vector.addElement(str6);
        } 
        paramSerializationHandler.setCdataSectionElements(vector);
      } 
    } 
    if (str1 != null || str2 != null)
      paramSerializationHandler.setDoctype(str2, str1); 
  }
  
  private Properties createOutputProperties(Properties paramProperties) {
    Properties properties1 = new Properties();
    setDefaults(properties1, "xml");
    Properties properties2 = new Properties(properties1);
    if (paramProperties != null) {
      Enumeration enumeration = paramProperties.propertyNames();
      while (enumeration.hasMoreElements()) {
        String str1 = (String)enumeration.nextElement();
        properties2.setProperty(str1, paramProperties.getProperty(str1));
      } 
    } else {
      properties2.setProperty("encoding", this._translet._encoding);
      if (this._translet._method != null)
        properties2.setProperty("method", this._translet._method); 
    } 
    String str = properties2.getProperty("method");
    if (str != null)
      if (str.equals("html")) {
        setDefaults(properties1, "html");
      } else if (str.equals("text")) {
        setDefaults(properties1, "text");
      }  
    return properties2;
  }
  
  private void setDefaults(Properties paramProperties, String paramString) {
    Properties properties = OutputPropertiesFactory.getDefaultMethodProperties(paramString);
    Enumeration enumeration = properties.propertyNames();
    while (enumeration.hasMoreElements()) {
      String str = (String)enumeration.nextElement();
      paramProperties.setProperty(str, properties.getProperty(str));
    } 
  }
  
  private boolean validOutputProperty(String paramString) { return (paramString.equals("encoding") || paramString.equals("method") || paramString.equals("indent") || paramString.equals("doctype-public") || paramString.equals("doctype-system") || paramString.equals("cdata-section-elements") || paramString.equals("media-type") || paramString.equals("omit-xml-declaration") || paramString.equals("standalone") || paramString.equals("version") || paramString.equals("http://www.oracle.com/xml/is-standalone") || paramString.charAt(0) == '{'); }
  
  private boolean isDefaultProperty(String paramString, Properties paramProperties) { return (paramProperties.get(paramString) == null); }
  
  public void setParameter(String paramString, Object paramObject) {
    if (paramObject == null) {
      ErrorMsg errorMsg = new ErrorMsg("JAXP_INVALID_SET_PARAM_VALUE", paramString);
      throw new IllegalArgumentException(errorMsg.toString());
    } 
    if (this._isIdentity) {
      if (this._parameters == null)
        this._parameters = new HashMap(); 
      this._parameters.put(paramString, paramObject);
    } else {
      this._translet.addParameter(paramString, paramObject);
    } 
  }
  
  public void clearParameters() {
    if (this._isIdentity && this._parameters != null) {
      this._parameters.clear();
    } else {
      this._translet.clearParameters();
    } 
  }
  
  public final Object getParameter(String paramString) { return this._isIdentity ? ((this._parameters != null) ? this._parameters.get(paramString) : null) : this._translet.getParameter(paramString); }
  
  public URIResolver getURIResolver() { return this._uriResolver; }
  
  public void setURIResolver(URIResolver paramURIResolver) { this._uriResolver = paramURIResolver; }
  
  public DOM retrieveDocument(String paramString1, String paramString2, Translet paramTranslet) {
    try {
      if (paramString2.length() == 0)
        paramString2 = paramString1; 
      Source source = this._uriResolver.resolve(paramString2, paramString1);
      if (source == null) {
        StreamSource streamSource = new StreamSource(SystemIDResolver.getAbsoluteURI(paramString2, paramString1));
        return getDOM(streamSource);
      } 
      return getDOM(source);
    } catch (TransformerException transformerException) {
      if (this._errorListener != null)
        postErrorToListener("File not found: " + transformerException.getMessage()); 
      return null;
    } 
  }
  
  public void error(TransformerException paramTransformerException) throws TransformerException {
    Throwable throwable = paramTransformerException.getException();
    if (throwable != null) {
      System.err.println(new ErrorMsg("ERROR_PLUS_WRAPPED_MSG", paramTransformerException.getMessageAndLocation(), throwable.getMessage()));
    } else {
      System.err.println(new ErrorMsg("ERROR_MSG", paramTransformerException.getMessageAndLocation()));
    } 
    throw paramTransformerException;
  }
  
  public void fatalError(TransformerException paramTransformerException) throws TransformerException {
    Throwable throwable = paramTransformerException.getException();
    if (throwable != null) {
      System.err.println(new ErrorMsg("FATAL_ERR_PLUS_WRAPPED_MSG", paramTransformerException.getMessageAndLocation(), throwable.getMessage()));
    } else {
      System.err.println(new ErrorMsg("FATAL_ERR_MSG", paramTransformerException.getMessageAndLocation()));
    } 
    throw paramTransformerException;
  }
  
  public void warning(TransformerException paramTransformerException) throws TransformerException {
    Throwable throwable = paramTransformerException.getException();
    if (throwable != null) {
      System.err.println(new ErrorMsg("WARNING_PLUS_WRAPPED_MSG", paramTransformerException.getMessageAndLocation(), throwable.getMessage()));
    } else {
      System.err.println(new ErrorMsg("WARNING_MSG", paramTransformerException.getMessageAndLocation()));
    } 
  }
  
  public void reset() {
    this._method = null;
    this._encoding = null;
    this._sourceSystemId = null;
    this._errorListener = this;
    this._uriResolver = null;
    this._dom = null;
    this._parameters = null;
    this._indentNumber = 0;
    setOutputProperties(null);
    this._tohFactory = null;
    this._ostream = null;
  }
  
  static class MessageHandler extends MessageHandler {
    private ErrorListener _errorListener;
    
    public MessageHandler(ErrorListener param1ErrorListener) throws IllegalArgumentException { this._errorListener = param1ErrorListener; }
    
    public void displayMessage(String param1String) {
      if (this._errorListener == null) {
        System.err.println(param1String);
      } else {
        try {
          this._errorListener.warning(new TransformerException(param1String));
        } catch (TransformerException transformerException) {}
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\TransformerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */