package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.XalanConstants;
import com.sun.org.apache.xalan.internal.utils.FeaturePropertyBase;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xalan.internal.xsltc.compiler.SourceLoader;
import com.sun.org.apache.xalan.internal.xsltc.compiler.XSLTC;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager;
import com.sun.org.apache.xml.internal.utils.StopParseException;
import com.sun.org.apache.xml.internal.utils.StylesheetPIHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;
import jdk.xml.internal.JdkXmlFeatures;
import jdk.xml.internal.JdkXmlUtils;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

public class TransformerFactoryImpl extends SAXTransformerFactory implements SourceLoader, ErrorListener {
  public static final String TRANSLET_NAME = "translet-name";
  
  public static final String DESTINATION_DIRECTORY = "destination-directory";
  
  public static final String PACKAGE_NAME = "package-name";
  
  public static final String JAR_NAME = "jar-name";
  
  public static final String GENERATE_TRANSLET = "generate-translet";
  
  public static final String AUTO_TRANSLET = "auto-translet";
  
  public static final String USE_CLASSPATH = "use-classpath";
  
  public static final String DEBUG = "debug";
  
  public static final String ENABLE_INLINING = "enable-inlining";
  
  public static final String INDENT_NUMBER = "indent-number";
  
  private ErrorListener _errorListener = this;
  
  private URIResolver _uriResolver = null;
  
  protected static final String DEFAULT_TRANSLET_NAME = "GregorSamsa";
  
  private String _transletName = "GregorSamsa";
  
  private String _destinationDirectory = null;
  
  private String _packageName = null;
  
  private String _jarFileName = null;
  
  private Map<Source, PIParamWrapper> _piParams = null;
  
  private boolean _debug = false;
  
  private boolean _enableInlining = false;
  
  private boolean _generateTranslet = false;
  
  private boolean _autoTranslet = false;
  
  private boolean _useClasspath = false;
  
  private int _indentNumber = -1;
  
  private boolean _isNotSecureProcessing = true;
  
  private boolean _isSecureMode = false;
  
  private boolean _overrideDefaultParser;
  
  private String _accessExternalStylesheet = "all";
  
  private String _accessExternalDTD = "all";
  
  private XMLSecurityPropertyManager _xmlSecurityPropertyMgr;
  
  private XMLSecurityManager _xmlSecurityManager;
  
  private final JdkXmlFeatures _xmlFeatures;
  
  private ClassLoader _extensionClassLoader = null;
  
  private Map<String, Class> _xsltcExtensionFunctions;
  
  public TransformerFactoryImpl() {
    if (System.getSecurityManager() != null) {
      this._isSecureMode = true;
      this._isNotSecureProcessing = false;
    } 
    this._xmlFeatures = new JdkXmlFeatures(!this._isNotSecureProcessing);
    this._overrideDefaultParser = this._xmlFeatures.getFeature(JdkXmlFeatures.XmlFeature.JDK_OVERRIDE_PARSER);
    this._xmlSecurityPropertyMgr = new XMLSecurityPropertyManager();
    this._accessExternalDTD = this._xmlSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
    this._accessExternalStylesheet = this._xmlSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_STYLESHEET);
    this._xmlSecurityManager = new XMLSecurityManager(true);
    this._xsltcExtensionFunctions = null;
  }
  
  public Map<String, Class> getExternalExtensionsMap() { return this._xsltcExtensionFunctions; }
  
  public void setErrorListener(ErrorListener paramErrorListener) throws IllegalArgumentException {
    if (paramErrorListener == null) {
      ErrorMsg errorMsg = new ErrorMsg("ERROR_LISTENER_NULL_ERR", "TransformerFactory");
      throw new IllegalArgumentException(errorMsg.toString());
    } 
    this._errorListener = paramErrorListener;
  }
  
  public ErrorListener getErrorListener() { return this._errorListener; }
  
  public Object getAttribute(String paramString) throws IllegalArgumentException {
    if (paramString.equals("translet-name"))
      return this._transletName; 
    if (paramString.equals("generate-translet"))
      return new Boolean(this._generateTranslet); 
    if (paramString.equals("auto-translet"))
      return new Boolean(this._autoTranslet); 
    if (paramString.equals("enable-inlining"))
      return this._enableInlining ? Boolean.TRUE : Boolean.FALSE; 
    if (paramString.equals("http://apache.org/xml/properties/security-manager"))
      return this._xmlSecurityManager; 
    if (paramString.equals("jdk.xml.transform.extensionClassLoader"))
      return this._extensionClassLoader; 
    String str = (this._xmlSecurityManager != null) ? this._xmlSecurityManager.getLimitAsString(paramString) : null;
    if (str != null)
      return str; 
    str = (this._xmlSecurityPropertyMgr != null) ? this._xmlSecurityPropertyMgr.getValue(paramString) : null;
    if (str != null)
      return str; 
    ErrorMsg errorMsg = new ErrorMsg("JAXP_INVALID_ATTR_ERR", paramString);
    throw new IllegalArgumentException(errorMsg.toString());
  }
  
  public void setAttribute(String paramString, Object paramObject) throws IllegalArgumentException {
    if (paramString.equals("translet-name") && paramObject instanceof String) {
      this._transletName = (String)paramObject;
      return;
    } 
    if (paramString.equals("destination-directory") && paramObject instanceof String) {
      this._destinationDirectory = (String)paramObject;
      return;
    } 
    if (paramString.equals("package-name") && paramObject instanceof String) {
      this._packageName = (String)paramObject;
      return;
    } 
    if (paramString.equals("jar-name") && paramObject instanceof String) {
      this._jarFileName = (String)paramObject;
      return;
    } 
    if (paramString.equals("generate-translet")) {
      if (paramObject instanceof Boolean) {
        this._generateTranslet = ((Boolean)paramObject).booleanValue();
        return;
      } 
      if (paramObject instanceof String) {
        this._generateTranslet = ((String)paramObject).equalsIgnoreCase("true");
        return;
      } 
    } else if (paramString.equals("auto-translet")) {
      if (paramObject instanceof Boolean) {
        this._autoTranslet = ((Boolean)paramObject).booleanValue();
        return;
      } 
      if (paramObject instanceof String) {
        this._autoTranslet = ((String)paramObject).equalsIgnoreCase("true");
        return;
      } 
    } else if (paramString.equals("use-classpath")) {
      if (paramObject instanceof Boolean) {
        this._useClasspath = ((Boolean)paramObject).booleanValue();
        return;
      } 
      if (paramObject instanceof String) {
        this._useClasspath = ((String)paramObject).equalsIgnoreCase("true");
        return;
      } 
    } else if (paramString.equals("debug")) {
      if (paramObject instanceof Boolean) {
        this._debug = ((Boolean)paramObject).booleanValue();
        return;
      } 
      if (paramObject instanceof String) {
        this._debug = ((String)paramObject).equalsIgnoreCase("true");
        return;
      } 
    } else if (paramString.equals("enable-inlining")) {
      if (paramObject instanceof Boolean) {
        this._enableInlining = ((Boolean)paramObject).booleanValue();
        return;
      } 
      if (paramObject instanceof String) {
        this._enableInlining = ((String)paramObject).equalsIgnoreCase("true");
        return;
      } 
    } else if (paramString.equals("indent-number")) {
      if (paramObject instanceof String) {
        try {
          this._indentNumber = Integer.parseInt((String)paramObject);
          return;
        } catch (NumberFormatException numberFormatException) {}
      } else if (paramObject instanceof Integer) {
        this._indentNumber = ((Integer)paramObject).intValue();
        return;
      } 
    } else if (paramString.equals("jdk.xml.transform.extensionClassLoader")) {
      if (paramObject instanceof ClassLoader) {
        this._extensionClassLoader = (ClassLoader)paramObject;
        return;
      } 
      ErrorMsg errorMsg1 = new ErrorMsg("JAXP_INVALID_ATTR_VALUE_ERR", "Extension Functions ClassLoader");
      throw new IllegalArgumentException(errorMsg1.toString());
    } 
    if (this._xmlSecurityManager != null && this._xmlSecurityManager.setLimit(paramString, XMLSecurityManager.State.APIPROPERTY, paramObject))
      return; 
    if (this._xmlSecurityPropertyMgr != null && this._xmlSecurityPropertyMgr.setValue(paramString, FeaturePropertyBase.State.APIPROPERTY, paramObject)) {
      this._accessExternalDTD = this._xmlSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
      this._accessExternalStylesheet = this._xmlSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_STYLESHEET);
      return;
    } 
    ErrorMsg errorMsg = new ErrorMsg("JAXP_INVALID_ATTR_ERR", paramString);
    throw new IllegalArgumentException(errorMsg.toString());
  }
  
  public void setFeature(String paramString, boolean paramBoolean) throws TransformerConfigurationException {
    if (paramString == null) {
      ErrorMsg errorMsg = new ErrorMsg("JAXP_SET_FEATURE_NULL_NAME");
      throw new NullPointerException(errorMsg.toString());
    } 
    if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
      if (this._isSecureMode && !paramBoolean) {
        ErrorMsg errorMsg = new ErrorMsg("JAXP_SECUREPROCESSING_FEATURE");
        throw new TransformerConfigurationException(errorMsg.toString());
      } 
      this._isNotSecureProcessing = !paramBoolean;
      this._xmlSecurityManager.setSecureProcessing(paramBoolean);
      if (paramBoolean && XalanConstants.IS_JDK8_OR_ABOVE) {
        this._xmlSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD, FeaturePropertyBase.State.FSP, "");
        this._xmlSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_STYLESHEET, FeaturePropertyBase.State.FSP, "");
        this._accessExternalDTD = this._xmlSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
        this._accessExternalStylesheet = this._xmlSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_STYLESHEET);
      } 
      if (paramBoolean && this._xmlFeatures != null)
        this._xmlFeatures.setFeature(JdkXmlFeatures.XmlFeature.ENABLE_EXTENSION_FUNCTION, JdkXmlFeatures.State.FSP, false); 
    } else {
      if (paramString.equals("http://www.oracle.com/feature/use-service-mechanism") && this._isSecureMode)
        return; 
      if (this._xmlFeatures != null && this._xmlFeatures.setFeature(paramString, JdkXmlFeatures.State.APIPROPERTY, Boolean.valueOf(paramBoolean))) {
        if (paramString.equals("jdk.xml.overrideDefaultParser") || paramString.equals("http://www.oracle.com/feature/use-service-mechanism"))
          this._overrideDefaultParser = this._xmlFeatures.getFeature(JdkXmlFeatures.XmlFeature.JDK_OVERRIDE_PARSER); 
        return;
      } 
      ErrorMsg errorMsg = new ErrorMsg("JAXP_UNSUPPORTED_FEATURE", paramString);
      throw new TransformerConfigurationException(errorMsg.toString());
    } 
  }
  
  public boolean getFeature(String paramString) {
    String[] arrayOfString = { 
        "http://javax.xml.transform.dom.DOMSource/feature", "http://javax.xml.transform.dom.DOMResult/feature", "http://javax.xml.transform.sax.SAXSource/feature", "http://javax.xml.transform.sax.SAXResult/feature", "http://javax.xml.transform.stax.StAXSource/feature", "http://javax.xml.transform.stax.StAXResult/feature", "http://javax.xml.transform.stream.StreamSource/feature", "http://javax.xml.transform.stream.StreamResult/feature", "http://javax.xml.transform.sax.SAXTransformerFactory/feature", "http://javax.xml.transform.sax.SAXTransformerFactory/feature/xmlfilter", 
        "http://www.oracle.com/feature/use-service-mechanism" };
    if (paramString == null) {
      ErrorMsg errorMsg = new ErrorMsg("JAXP_GET_FEATURE_NULL_NAME");
      throw new NullPointerException(errorMsg.toString());
    } 
    int i;
    for (i = 0; i < arrayOfString.length; i++) {
      if (paramString.equals(arrayOfString[i]))
        return true; 
    } 
    if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing"))
      return !this._isNotSecureProcessing; 
    i = this._xmlFeatures.getIndex(paramString);
    return (i > -1) ? this._xmlFeatures.getFeature(i) : 0;
  }
  
  public boolean overrideDefaultParser() { return this._overrideDefaultParser; }
  
  public JdkXmlFeatures getJdkXmlFeatures() { return this._xmlFeatures; }
  
  public URIResolver getURIResolver() { return this._uriResolver; }
  
  public void setURIResolver(URIResolver paramURIResolver) { this._uriResolver = paramURIResolver; }
  
  public Source getAssociatedStylesheet(Source paramSource, String paramString1, String paramString2, String paramString3) throws TransformerConfigurationException {
    XMLReader xMLReader = null;
    StylesheetPIHandler stylesheetPIHandler = new StylesheetPIHandler(null, paramString1, paramString2, paramString3);
    try {
      if (paramSource instanceof DOMSource) {
        DOMSource dOMSource = (DOMSource)paramSource;
        String str = dOMSource.getSystemId();
        Node node = dOMSource.getNode();
        DOM2SAX dOM2SAX = new DOM2SAX(node);
        stylesheetPIHandler.setBaseId(str);
        dOM2SAX.setContentHandler(stylesheetPIHandler);
        dOM2SAX.parse();
      } else {
        if (paramSource instanceof SAXSource)
          xMLReader = ((SAXSource)paramSource).getXMLReader(); 
        InputSource inputSource = SAXSource.sourceToInputSource(paramSource);
        String str = inputSource.getSystemId();
        if (xMLReader == null)
          xMLReader = JdkXmlUtils.getXMLReader(this._overrideDefaultParser, !this._isNotSecureProcessing); 
        stylesheetPIHandler.setBaseId(str);
        xMLReader.setContentHandler(stylesheetPIHandler);
        xMLReader.parse(inputSource);
      } 
      if (this._uriResolver != null)
        stylesheetPIHandler.setURIResolver(this._uriResolver); 
    } catch (StopParseException stopParseException) {
    
    } catch (SAXException sAXException) {
      throw new TransformerConfigurationException("getAssociatedStylesheets failed", sAXException);
    } catch (IOException iOException) {
      throw new TransformerConfigurationException("getAssociatedStylesheets failed", iOException);
    } 
    return stylesheetPIHandler.getAssociatedStylesheet();
  }
  
  public Transformer newTransformer() throws TransformerConfigurationException {
    TransformerImpl transformerImpl = new TransformerImpl(new Properties(), this._indentNumber, this);
    if (this._uriResolver != null)
      transformerImpl.setURIResolver(this._uriResolver); 
    if (!this._isNotSecureProcessing)
      transformerImpl.setSecureProcessing(true); 
    return transformerImpl;
  }
  
  public Transformer newTransformer(Source paramSource) throws TransformerConfigurationException {
    Templates templates = newTemplates(paramSource);
    Transformer transformer = templates.newTransformer();
    if (this._uriResolver != null)
      transformer.setURIResolver(this._uriResolver); 
    return transformer;
  }
  
  private void passWarningsToListener(ArrayList<ErrorMsg> paramArrayList) throws TransformerException {
    if (this._errorListener == null || paramArrayList == null)
      return; 
    int i = paramArrayList.size();
    for (byte b = 0; b < i; b++) {
      ErrorMsg errorMsg = (ErrorMsg)paramArrayList.get(b);
      if (errorMsg.isWarningError()) {
        this._errorListener.error(new TransformerConfigurationException(errorMsg.toString()));
      } else {
        this._errorListener.warning(new TransformerConfigurationException(errorMsg.toString()));
      } 
    } 
  }
  
  private void passErrorsToListener(ArrayList<ErrorMsg> paramArrayList) throws TransformerException {
    try {
      if (this._errorListener == null || paramArrayList == null)
        return; 
      int i = paramArrayList.size();
      for (byte b = 0; b < i; b++) {
        String str = ((ErrorMsg)paramArrayList.get(b)).toString();
        this._errorListener.error(new TransformerException(str));
      } 
    } catch (TransformerException transformerException) {}
  }
  
  public Templates newTemplates(Source paramSource) throws TransformerConfigurationException {
    if (this._useClasspath) {
      String str1 = getTransletBaseName(paramSource);
      if (this._packageName != null)
        str1 = this._packageName + "." + str1; 
      try {
        Class clazz = ObjectFactory.findProviderClass(str1, true);
        resetTransientAttributes();
        return new TemplatesImpl(new Class[] { clazz }, str1, null, this._indentNumber, this);
      } catch (ClassNotFoundException classNotFoundException) {
        ErrorMsg errorMsg = new ErrorMsg("CLASS_NOT_FOUND_ERR", str1);
        throw new TransformerConfigurationException(errorMsg.toString());
      } catch (Exception exception) {
        ErrorMsg errorMsg = new ErrorMsg(new ErrorMsg("RUNTIME_ERROR_KEY") + exception.getMessage());
        throw new TransformerConfigurationException(errorMsg.toString());
      } 
    } 
    if (this._autoTranslet) {
      byte[][] arrayOfByte1;
      String str1 = getTransletBaseName(paramSource);
      if (this._packageName != null)
        str1 = this._packageName + "." + str1; 
      if (this._jarFileName != null) {
        arrayOfByte1 = getBytecodesFromJar(paramSource, str1);
      } else {
        arrayOfByte1 = getBytecodesFromClasses(paramSource, str1);
      } 
      if (arrayOfByte1 != null) {
        if (this._debug)
          if (this._jarFileName != null) {
            System.err.println(new ErrorMsg("TRANSFORM_WITH_JAR_STR", str1, this._jarFileName));
          } else {
            System.err.println(new ErrorMsg("TRANSFORM_WITH_TRANSLET_STR", str1));
          }  
        resetTransientAttributes();
        return new TemplatesImpl(arrayOfByte1, str1, null, this._indentNumber, this);
      } 
    } 
    XSLTC xSLTC = new XSLTC(this._xmlFeatures);
    if (this._debug)
      xSLTC.setDebug(true); 
    if (this._enableInlining) {
      xSLTC.setTemplateInlining(true);
    } else {
      xSLTC.setTemplateInlining(false);
    } 
    if (!this._isNotSecureProcessing)
      xSLTC.setSecureProcessing(true); 
    xSLTC.setProperty("http://javax.xml.XMLConstants/property/accessExternalStylesheet", this._accessExternalStylesheet);
    xSLTC.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", this._accessExternalDTD);
    xSLTC.setProperty("http://apache.org/xml/properties/security-manager", this._xmlSecurityManager);
    xSLTC.setProperty("jdk.xml.transform.extensionClassLoader", this._extensionClassLoader);
    xSLTC.init();
    if (!this._isNotSecureProcessing)
      this._xsltcExtensionFunctions = xSLTC.getExternalExtensionFunctions(); 
    if (this._uriResolver != null)
      xSLTC.setSourceLoader(this); 
    if (this._piParams != null && this._piParams.get(paramSource) != null) {
      PIParamWrapper pIParamWrapper = (PIParamWrapper)this._piParams.get(paramSource);
      if (pIParamWrapper != null)
        xSLTC.setPIParameters(pIParamWrapper._media, pIParamWrapper._title, pIParamWrapper._charset); 
    } 
    byte b = 2;
    if (this._generateTranslet || this._autoTranslet) {
      xSLTC.setClassName(getTransletBaseName(paramSource));
      if (this._destinationDirectory != null) {
        xSLTC.setDestDirectory(this._destinationDirectory);
      } else {
        String str1 = getStylesheetFileName(paramSource);
        if (str1 != null) {
          File file = new File(str1);
          String str2 = file.getParent();
          if (str2 != null)
            xSLTC.setDestDirectory(str2); 
        } 
      } 
      if (this._packageName != null)
        xSLTC.setPackageName(this._packageName); 
      if (this._jarFileName != null) {
        xSLTC.setJarFileName(this._jarFileName);
        b = 5;
      } else {
        b = 4;
      } 
    } 
    InputSource inputSource = Util.getInputSource(xSLTC, paramSource);
    byte[][] arrayOfByte = xSLTC.compile(null, inputSource, b);
    String str = xSLTC.getClassName();
    if ((this._generateTranslet || this._autoTranslet) && arrayOfByte != null && this._jarFileName != null)
      try {
        xSLTC.outputToJar();
      } catch (IOException iOException) {} 
    resetTransientAttributes();
    if (this._errorListener != this) {
      try {
        passWarningsToListener(xSLTC.getWarnings());
      } catch (TransformerException transformerException) {
        throw new TransformerConfigurationException(transformerException);
      } 
    } else {
      xSLTC.printWarnings();
    } 
    if (arrayOfByte == null) {
      TransformerConfigurationException transformerConfigurationException;
      ErrorMsg errorMsg;
      ArrayList arrayList = xSLTC.getErrors();
      if (arrayList != null) {
        errorMsg = (ErrorMsg)arrayList.get(arrayList.size() - 1);
      } else {
        errorMsg = new ErrorMsg("JAXP_COMPILE_ERR");
      } 
      Throwable throwable = errorMsg.getCause();
      if (throwable != null) {
        transformerConfigurationException = new TransformerConfigurationException(throwable.getMessage(), throwable);
      } else {
        transformerConfigurationException = new TransformerConfigurationException(errorMsg.toString());
      } 
      if (this._errorListener != null) {
        passErrorsToListener(xSLTC.getErrors());
        try {
          this._errorListener.fatalError(transformerConfigurationException);
        } catch (TransformerException transformerException) {}
      } else {
        xSLTC.printErrors();
      } 
      throw transformerConfigurationException;
    } 
    return new TemplatesImpl(arrayOfByte, str, xSLTC.getOutputProperties(), this._indentNumber, this);
  }
  
  public TemplatesHandler newTemplatesHandler() throws TransformerConfigurationException {
    TemplatesHandlerImpl templatesHandlerImpl = new TemplatesHandlerImpl(this._indentNumber, this);
    if (this._uriResolver != null)
      templatesHandlerImpl.setURIResolver(this._uriResolver); 
    return templatesHandlerImpl;
  }
  
  public TransformerHandler newTransformerHandler() throws TransformerConfigurationException {
    Transformer transformer = newTransformer();
    if (this._uriResolver != null)
      transformer.setURIResolver(this._uriResolver); 
    return new TransformerHandlerImpl((TransformerImpl)transformer);
  }
  
  public TransformerHandler newTransformerHandler(Source paramSource) throws TransformerConfigurationException {
    Transformer transformer = newTransformer(paramSource);
    if (this._uriResolver != null)
      transformer.setURIResolver(this._uriResolver); 
    return new TransformerHandlerImpl((TransformerImpl)transformer);
  }
  
  public TransformerHandler newTransformerHandler(Templates paramTemplates) throws TransformerConfigurationException {
    Transformer transformer = paramTemplates.newTransformer();
    TransformerImpl transformerImpl = (TransformerImpl)transformer;
    return new TransformerHandlerImpl(transformerImpl);
  }
  
  public XMLFilter newXMLFilter(Source paramSource) throws TransformerConfigurationException {
    Templates templates = newTemplates(paramSource);
    return (templates == null) ? null : newXMLFilter(templates);
  }
  
  public XMLFilter newXMLFilter(Templates paramTemplates) throws TransformerConfigurationException {
    try {
      return new TrAXFilter(paramTemplates);
    } catch (TransformerConfigurationException transformerConfigurationException) {
      if (this._errorListener != null)
        try {
          this._errorListener.fatalError(transformerConfigurationException);
          return null;
        } catch (TransformerException transformerException) {
          new TransformerConfigurationException(transformerException);
        }  
      throw transformerConfigurationException;
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
  
  public InputSource loadSource(String paramString1, String paramString2, XSLTC paramXSLTC) {
    try {
      if (this._uriResolver != null) {
        Source source = this._uriResolver.resolve(paramString1, paramString2);
        if (source != null)
          return Util.getInputSource(paramXSLTC, source); 
      } 
    } catch (TransformerException transformerException) {
      ErrorMsg errorMsg = new ErrorMsg("INVALID_URI_ERR", paramString1 + "\n" + transformerException.getMessage(), this);
      paramXSLTC.getParser().reportError(2, errorMsg);
    } 
    return null;
  }
  
  private void resetTransientAttributes() {
    this._transletName = "GregorSamsa";
    this._destinationDirectory = null;
    this._packageName = null;
    this._jarFileName = null;
  }
  
  private byte[][] getBytecodesFromClasses(Source paramSource, String paramString) {
    String str2;
    if (paramString == null)
      return (byte[][])null; 
    String str1 = getStylesheetFileName(paramSource);
    File file1 = null;
    if (str1 != null)
      file1 = new File(str1); 
    int i = paramString.lastIndexOf('.');
    if (i > 0) {
      str2 = paramString.substring(i + 1);
    } else {
      str2 = paramString;
    } 
    String str3 = paramString.replace('.', '/');
    if (this._destinationDirectory != null) {
      str3 = this._destinationDirectory + "/" + str3 + ".class";
    } else if (file1 != null && file1.getParent() != null) {
      str3 = file1.getParent() + "/" + str3 + ".class";
    } else {
      str3 = str3 + ".class";
    } 
    File file2 = new File(str3);
    if (!file2.exists())
      return (byte[][])null; 
    if (file1 != null && file1.exists()) {
      long l1 = file1.lastModified();
      long l2 = file2.lastModified();
      if (l2 < l1)
        return (byte[][])null; 
    } 
    Vector vector = new Vector();
    int j = (int)file2.length();
    if (j > 0) {
      FileInputStream fileInputStream;
      try {
        fileInputStream = new FileInputStream(file2);
      } catch (FileNotFoundException fileNotFoundException) {
        return (byte[][])null;
      } 
      byte[] arrayOfByte = new byte[j];
      try {
        readFromInputStream(arrayOfByte, fileInputStream, j);
        fileInputStream.close();
      } catch (IOException iOException) {
        return (byte[][])null;
      } 
      vector.addElement(arrayOfByte);
    } else {
      return (byte[][])null;
    } 
    String str4 = file2.getParent();
    if (str4 == null)
      str4 = SecuritySupport.getSystemProperty("user.dir"); 
    File file3 = new File(str4);
    final String transletAuxPrefix = str2 + "$";
    File[] arrayOfFile = file3.listFiles(new FilenameFilter() {
          public boolean accept(File param1File, String param1String) { return (param1String.endsWith(".class") && param1String.startsWith(transletAuxPrefix)); }
        });
    int k;
    for (k = 0; k < arrayOfFile.length; k++) {
      File file = arrayOfFile[k];
      int m = (int)file.length();
      if (m > 0) {
        FileInputStream fileInputStream = null;
        try {
          fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException fileNotFoundException) {}
        byte[] arrayOfByte = new byte[m];
        try {
          readFromInputStream(arrayOfByte, fileInputStream, m);
          fileInputStream.close();
        } catch (IOException iOException) {}
        vector.addElement(arrayOfByte);
      } 
    } 
    k = vector.size();
    if (k > 0) {
      byte[][] arrayOfByte = new byte[k][1];
      for (byte b = 0; b < k; b++)
        arrayOfByte[b] = (byte[])vector.elementAt(b); 
      return arrayOfByte;
    } 
    return (byte[][])null;
  }
  
  private byte[][] getBytecodesFromJar(Source paramSource, String paramString) {
    ZipFile zipFile;
    String str2;
    String str1 = getStylesheetFileName(paramSource);
    File file1 = null;
    if (str1 != null)
      file1 = new File(str1); 
    if (this._destinationDirectory != null) {
      str2 = this._destinationDirectory + "/" + this._jarFileName;
    } else if (file1 != null && file1.getParent() != null) {
      str2 = file1.getParent() + "/" + this._jarFileName;
    } else {
      str2 = this._jarFileName;
    } 
    File file2 = new File(str2);
    if (!file2.exists())
      return (byte[][])null; 
    if (file1 != null && file1.exists()) {
      long l1 = file1.lastModified();
      long l2 = file2.lastModified();
      if (l2 < l1)
        return (byte[][])null; 
    } 
    try {
      zipFile = new ZipFile(file2);
    } catch (IOException iOException) {
      return (byte[][])null;
    } 
    String str3 = paramString.replace('.', '/');
    String str4 = str3 + "$";
    String str5 = str3 + ".class";
    Vector vector = new Vector();
    Enumeration enumeration = zipFile.entries();
    while (enumeration.hasMoreElements()) {
      ZipEntry zipEntry = (ZipEntry)enumeration.nextElement();
      String str = zipEntry.getName();
      if (zipEntry.getSize() > 0L && (str.equals(str5) || (str.endsWith(".class") && str.startsWith(str4))))
        try {
          InputStream inputStream = zipFile.getInputStream(zipEntry);
          int j = (int)zipEntry.getSize();
          byte[] arrayOfByte = new byte[j];
          readFromInputStream(arrayOfByte, inputStream, j);
          inputStream.close();
          vector.addElement(arrayOfByte);
        } catch (IOException iOException) {
          return (byte[][])null;
        }  
    } 
    int i = vector.size();
    if (i > 0) {
      byte[][] arrayOfByte = new byte[i][1];
      for (byte b = 0; b < i; b++)
        arrayOfByte[b] = (byte[])vector.elementAt(b); 
      return arrayOfByte;
    } 
    return (byte[][])null;
  }
  
  private void readFromInputStream(byte[] paramArrayOfByte, InputStream paramInputStream, int paramInt) throws IOException {
    int i = 0;
    int j = 0;
    int k;
    for (k = paramInt; k > 0 && (i = paramInputStream.read(paramArrayOfByte, j, k)) > 0; k -= i)
      j += i; 
  }
  
  private String getTransletBaseName(Source paramSource) {
    String str1 = null;
    if (!this._transletName.equals("GregorSamsa"))
      return this._transletName; 
    String str2 = paramSource.getSystemId();
    if (str2 != null) {
      String str = Util.baseName(str2);
      if (str != null) {
        str = Util.noExtName(str);
        str1 = Util.toJavaName(str);
      } 
    } 
    return (str1 != null) ? str1 : "GregorSamsa";
  }
  
  private String getStylesheetFileName(Source paramSource) {
    String str = paramSource.getSystemId();
    if (str != null) {
      URL uRL;
      File file = new File(str);
      if (file.exists())
        return str; 
      try {
        uRL = new URL(str);
      } catch (MalformedURLException malformedURLException) {
        return null;
      } 
      return "file".equals(uRL.getProtocol()) ? uRL.getFile() : null;
    } 
    return null;
  }
  
  protected final XSLTCDTMManager createNewDTMManagerInstance() { return XSLTCDTMManager.createNewDTMManagerInstance(); }
  
  private static class PIParamWrapper {
    public String _media = null;
    
    public String _title = null;
    
    public String _charset = null;
    
    public PIParamWrapper(String param1String1, String param1String2, String param1String3) {
      this._media = param1String1;
      this._title = param1String2;
      this._charset = param1String3;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\TransformerFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */