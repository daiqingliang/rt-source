package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import jdk.xml.internal.JdkXmlFeatures;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public final class XSLTC {
  private Parser _parser;
  
  private XMLReader _reader = null;
  
  private SourceLoader _loader = null;
  
  private Stylesheet _stylesheet;
  
  private int _modeSerial = 1;
  
  private int _stylesheetSerial = 1;
  
  private int _stepPatternSerial = 1;
  
  private int _helperClassSerial = 0;
  
  private int _attributeSetSerial = 0;
  
  private int[] _numberFieldIndexes;
  
  private int _nextGType;
  
  private Vector _namesIndex;
  
  private Map<String, Integer> _elements;
  
  private Map<String, Integer> _attributes;
  
  private int _nextNSType;
  
  private Vector _namespaceIndex;
  
  private Map<String, Integer> _namespaces;
  
  private Map<String, Integer> _namespacePrefixes;
  
  private Vector m_characterData;
  
  public static final int FILE_OUTPUT = 0;
  
  public static final int JAR_OUTPUT = 1;
  
  public static final int BYTEARRAY_OUTPUT = 2;
  
  public static final int CLASSLOADER_OUTPUT = 3;
  
  public static final int BYTEARRAY_AND_FILE_OUTPUT = 4;
  
  public static final int BYTEARRAY_AND_JAR_OUTPUT = 5;
  
  private boolean _debug = false;
  
  private String _jarFileName = null;
  
  private String _className = null;
  
  private String _packageName = null;
  
  private File _destDir = null;
  
  private int _outputType = 0;
  
  private Vector _classes;
  
  private Vector _bcelClasses;
  
  private boolean _callsNodeset = false;
  
  private boolean _multiDocument = false;
  
  private boolean _hasIdCall = false;
  
  private boolean _templateInlining = false;
  
  private boolean _isSecureProcessing = false;
  
  private boolean _overrideDefaultParser;
  
  private String _accessExternalStylesheet = "all";
  
  private String _accessExternalDTD = "all";
  
  private XMLSecurityManager _xmlSecurityManager;
  
  private final JdkXmlFeatures _xmlFeatures;
  
  private ClassLoader _extensionClassLoader;
  
  private final Map<String, Class> _externalExtensionFunctions;
  
  public XSLTC(JdkXmlFeatures paramJdkXmlFeatures) {
    this._overrideDefaultParser = paramJdkXmlFeatures.getFeature(JdkXmlFeatures.XmlFeature.JDK_OVERRIDE_PARSER);
    this._parser = new Parser(this, this._overrideDefaultParser);
    this._xmlFeatures = paramJdkXmlFeatures;
    this._extensionClassLoader = null;
    this._externalExtensionFunctions = new HashMap();
  }
  
  public void setSecureProcessing(boolean paramBoolean) { this._isSecureProcessing = paramBoolean; }
  
  public boolean isSecureProcessing() { return this._isSecureProcessing; }
  
  public boolean getFeature(JdkXmlFeatures.XmlFeature paramXmlFeature) { return this._xmlFeatures.getFeature(paramXmlFeature); }
  
  public Object getProperty(String paramString) { return paramString.equals("http://javax.xml.XMLConstants/property/accessExternalStylesheet") ? this._accessExternalStylesheet : (paramString.equals("http://javax.xml.XMLConstants/property/accessExternalDTD") ? this._accessExternalDTD : (paramString.equals("http://apache.org/xml/properties/security-manager") ? this._xmlSecurityManager : (paramString.equals("jdk.xml.transform.extensionClassLoader") ? this._extensionClassLoader : null))); }
  
  public void setProperty(String paramString, Object paramObject) {
    if (paramString.equals("http://javax.xml.XMLConstants/property/accessExternalStylesheet")) {
      this._accessExternalStylesheet = (String)paramObject;
    } else if (paramString.equals("http://javax.xml.XMLConstants/property/accessExternalDTD")) {
      this._accessExternalDTD = (String)paramObject;
    } else if (paramString.equals("http://apache.org/xml/properties/security-manager")) {
      this._xmlSecurityManager = (XMLSecurityManager)paramObject;
    } else if (paramString.equals("jdk.xml.transform.extensionClassLoader")) {
      this._extensionClassLoader = (ClassLoader)paramObject;
      this._externalExtensionFunctions.clear();
    } 
  }
  
  public Parser getParser() { return this._parser; }
  
  public void setOutputType(int paramInt) { this._outputType = paramInt; }
  
  public Properties getOutputProperties() { return this._parser.getOutputProperties(); }
  
  public void init() {
    reset();
    this._reader = null;
    this._classes = new Vector();
    this._bcelClasses = new Vector();
  }
  
  private void setExternalExtensionFunctions(String paramString, Class paramClass) {
    if (this._isSecureProcessing && paramClass != null && !this._externalExtensionFunctions.containsKey(paramString))
      this._externalExtensionFunctions.put(paramString, paramClass); 
  }
  
  Class loadExternalFunction(String paramString) throws ClassNotFoundException {
    Class clazz = null;
    if (this._externalExtensionFunctions.containsKey(paramString)) {
      clazz = (Class)this._externalExtensionFunctions.get(paramString);
    } else if (this._extensionClassLoader != null) {
      clazz = Class.forName(paramString, true, this._extensionClassLoader);
      setExternalExtensionFunctions(paramString, clazz);
    } 
    if (clazz == null)
      throw new ClassNotFoundException(paramString); 
    return clazz;
  }
  
  public Map<String, Class> getExternalExtensionFunctions() { return Collections.unmodifiableMap(this._externalExtensionFunctions); }
  
  private void reset() {
    this._nextGType = 14;
    this._elements = new HashMap();
    this._attributes = new HashMap();
    this._namespaces = new HashMap();
    this._namespaces.put("", new Integer(this._nextNSType));
    this._namesIndex = new Vector(128);
    this._namespaceIndex = new Vector(32);
    this._namespacePrefixes = new HashMap();
    this._stylesheet = null;
    this._parser.init();
    this._modeSerial = 1;
    this._stylesheetSerial = 1;
    this._stepPatternSerial = 1;
    this._helperClassSerial = 0;
    this._attributeSetSerial = 0;
    this._multiDocument = false;
    this._hasIdCall = false;
    this._numberFieldIndexes = new int[] { -1, -1, -1 };
    this._externalExtensionFunctions.clear();
  }
  
  public void setSourceLoader(SourceLoader paramSourceLoader) { this._loader = paramSourceLoader; }
  
  public void setTemplateInlining(boolean paramBoolean) { this._templateInlining = paramBoolean; }
  
  public boolean getTemplateInlining() { return this._templateInlining; }
  
  public void setPIParameters(String paramString1, String paramString2, String paramString3) { this._parser.setPIParameters(paramString1, paramString2, paramString3); }
  
  public boolean compile(URL paramURL) {
    try {
      InputStream inputStream = paramURL.openStream();
      InputSource inputSource = new InputSource(inputStream);
      inputSource.setSystemId(paramURL.toString());
      return compile(inputSource, this._className);
    } catch (IOException iOException) {
      this._parser.reportError(2, new ErrorMsg("JAXP_COMPILE_ERR", iOException));
      return false;
    } 
  }
  
  public boolean compile(URL paramURL, String paramString) {
    try {
      InputStream inputStream = paramURL.openStream();
      InputSource inputSource = new InputSource(inputStream);
      inputSource.setSystemId(paramURL.toString());
      return compile(inputSource, paramString);
    } catch (IOException iOException) {
      this._parser.reportError(2, new ErrorMsg("JAXP_COMPILE_ERR", iOException));
      return false;
    } 
  }
  
  public boolean compile(InputStream paramInputStream, String paramString) {
    InputSource inputSource = new InputSource(paramInputStream);
    inputSource.setSystemId(paramString);
    return compile(inputSource, paramString);
  }
  
  public boolean compile(InputSource paramInputSource, String paramString) {
    try {
      reset();
      String str = null;
      if (paramInputSource != null)
        str = paramInputSource.getSystemId(); 
      if (this._className == null) {
        if (paramString != null) {
          setClassName(paramString);
        } else if (str != null && !str.equals("")) {
          setClassName(Util.baseName(str));
        } 
        if (this._className == null || this._className.length() == 0)
          setClassName("GregorSamsa"); 
      } 
      SyntaxTreeNode syntaxTreeNode = null;
      if (this._reader == null) {
        syntaxTreeNode = this._parser.parse(paramInputSource);
      } else {
        syntaxTreeNode = this._parser.parse(this._reader, paramInputSource);
      } 
      if (!this._parser.errorsFound() && syntaxTreeNode != null) {
        this._stylesheet = this._parser.makeStylesheet(syntaxTreeNode);
        this._stylesheet.setSourceLoader(this._loader);
        this._stylesheet.setSystemId(str);
        this._stylesheet.setParentStylesheet(null);
        this._stylesheet.setTemplateInlining(this._templateInlining);
        this._parser.setCurrentStylesheet(this._stylesheet);
        this._parser.createAST(this._stylesheet);
      } 
      if (!this._parser.errorsFound() && this._stylesheet != null) {
        this._stylesheet.setCallsNodeset(this._callsNodeset);
        this._stylesheet.setMultiDocument(this._multiDocument);
        this._stylesheet.setHasIdCall(this._hasIdCall);
        synchronized (getClass()) {
          this._stylesheet.translate();
        } 
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
      this._parser.reportError(2, new ErrorMsg("JAXP_COMPILE_ERR", exception));
    } catch (Error error) {
      if (this._debug)
        error.printStackTrace(); 
      this._parser.reportError(2, new ErrorMsg("JAXP_COMPILE_ERR", error));
    } finally {
      this._reader = null;
    } 
    return !this._parser.errorsFound();
  }
  
  public boolean compile(Vector paramVector) {
    int i = paramVector.size();
    if (i == 0)
      return true; 
    if (i == 1) {
      Object object = paramVector.firstElement();
      return (object instanceof URL) ? compile((URL)object) : 0;
    } 
    Enumeration enumeration = paramVector.elements();
    while (enumeration.hasMoreElements()) {
      this._className = null;
      Object object = enumeration.nextElement();
      if (object instanceof URL && !compile((URL)object))
        return false; 
    } 
    return true;
  }
  
  public byte[][] getBytecodes() {
    int i = this._classes.size();
    byte[][] arrayOfByte = new byte[i][1];
    for (byte b = 0; b < i; b++)
      arrayOfByte[b] = (byte[])this._classes.elementAt(b); 
    return arrayOfByte;
  }
  
  public byte[][] compile(String paramString, InputSource paramInputSource, int paramInt) {
    this._outputType = paramInt;
    return compile(paramInputSource, paramString) ? getBytecodes() : (byte[][])null;
  }
  
  public byte[][] compile(String paramString, InputSource paramInputSource) { return compile(paramString, paramInputSource, 2); }
  
  public void setXMLReader(XMLReader paramXMLReader) { this._reader = paramXMLReader; }
  
  public XMLReader getXMLReader() { return this._reader; }
  
  public ArrayList<ErrorMsg> getErrors() { return this._parser.getErrors(); }
  
  public ArrayList<ErrorMsg> getWarnings() { return this._parser.getWarnings(); }
  
  public void printErrors() { this._parser.printErrors(); }
  
  public void printWarnings() { this._parser.printWarnings(); }
  
  protected void setMultiDocument(boolean paramBoolean) { this._multiDocument = paramBoolean; }
  
  public boolean isMultiDocument() { return this._multiDocument; }
  
  protected void setCallsNodeset(boolean paramBoolean) {
    if (paramBoolean)
      setMultiDocument(paramBoolean); 
    this._callsNodeset = paramBoolean;
  }
  
  public boolean callsNodeset() { return this._callsNodeset; }
  
  protected void setHasIdCall(boolean paramBoolean) { this._hasIdCall = paramBoolean; }
  
  public boolean hasIdCall() { return this._hasIdCall; }
  
  public void setClassName(String paramString) {
    String str1 = Util.baseName(paramString);
    String str2 = Util.noExtName(str1);
    String str3 = Util.toJavaName(str2);
    if (this._packageName == null) {
      this._className = str3;
    } else {
      this._className = this._packageName + '.' + str3;
    } 
  }
  
  public String getClassName() { return this._className; }
  
  private String classFileName(String paramString) { return paramString.replace('.', File.separatorChar) + ".class"; }
  
  private File getOutputFile(String paramString) { return (this._destDir != null) ? new File(this._destDir, classFileName(paramString)) : new File(classFileName(paramString)); }
  
  public boolean setDestDirectory(String paramString) {
    File file = new File(paramString);
    if (SecuritySupport.getFileExists(file) || file.mkdirs()) {
      this._destDir = file;
      return true;
    } 
    this._destDir = null;
    return false;
  }
  
  public void setPackageName(String paramString) {
    this._packageName = paramString;
    if (this._className != null)
      setClassName(this._className); 
  }
  
  public void setJarFileName(String paramString) {
    String str = ".jar";
    if (paramString.endsWith(".jar")) {
      this._jarFileName = paramString;
    } else {
      this._jarFileName = paramString + ".jar";
    } 
    this._outputType = 1;
  }
  
  public String getJarFileName() { return this._jarFileName; }
  
  public void setStylesheet(Stylesheet paramStylesheet) {
    if (this._stylesheet == null)
      this._stylesheet = paramStylesheet; 
  }
  
  public Stylesheet getStylesheet() { return this._stylesheet; }
  
  public int registerAttribute(QName paramQName) {
    Integer integer = (Integer)this._attributes.get(paramQName.toString());
    if (integer == null) {
      integer = Integer.valueOf(this._nextGType++);
      this._attributes.put(paramQName.toString(), integer);
      String str1 = paramQName.getNamespace();
      String str2 = "@" + paramQName.getLocalPart();
      if (str1 != null && !str1.equals("")) {
        this._namesIndex.addElement(str1 + ":" + str2);
      } else {
        this._namesIndex.addElement(str2);
      } 
      if (paramQName.getLocalPart().equals("*"))
        registerNamespace(paramQName.getNamespace()); 
    } 
    return integer.intValue();
  }
  
  public int registerElement(QName paramQName) {
    Integer integer = (Integer)this._elements.get(paramQName.toString());
    if (integer == null) {
      this._elements.put(paramQName.toString(), integer = Integer.valueOf(this._nextGType++));
      this._namesIndex.addElement(paramQName.toString());
    } 
    if (paramQName.getLocalPart().equals("*"))
      registerNamespace(paramQName.getNamespace()); 
    return integer.intValue();
  }
  
  public int registerNamespacePrefix(QName paramQName) {
    Integer integer = (Integer)this._namespacePrefixes.get(paramQName.toString());
    if (integer == null) {
      integer = Integer.valueOf(this._nextGType++);
      this._namespacePrefixes.put(paramQName.toString(), integer);
      String str = paramQName.getNamespace();
      if (str != null && !str.equals("")) {
        this._namesIndex.addElement("?");
      } else {
        this._namesIndex.addElement("?" + paramQName.getLocalPart());
      } 
    } 
    return integer.intValue();
  }
  
  public int registerNamespace(String paramString) {
    Integer integer = (Integer)this._namespaces.get(paramString);
    if (integer == null) {
      integer = Integer.valueOf(this._nextNSType++);
      this._namespaces.put(paramString, integer);
      this._namespaceIndex.addElement(paramString);
    } 
    return integer.intValue();
  }
  
  public int nextModeSerial() { return this._modeSerial++; }
  
  public int nextStylesheetSerial() { return this._stylesheetSerial++; }
  
  public int nextStepPatternSerial() { return this._stepPatternSerial++; }
  
  public int[] getNumberFieldIndexes() { return this._numberFieldIndexes; }
  
  public int nextHelperClassSerial() { return this._helperClassSerial++; }
  
  public int nextAttributeSetSerial() { return this._attributeSetSerial++; }
  
  public Vector getNamesIndex() { return this._namesIndex; }
  
  public Vector getNamespaceIndex() { return this._namespaceIndex; }
  
  public String getHelperClassName() { return getClassName() + '$' + this._helperClassSerial++; }
  
  public void dumpClass(JavaClass paramJavaClass) {
    if (this._outputType == 0 || this._outputType == 4) {
      File file = getOutputFile(paramJavaClass.getClassName());
      String str = file.getParent();
      if (str != null) {
        File file1 = new File(str);
        if (!SecuritySupport.getFileExists(file1))
          file1.mkdirs(); 
      } 
    } 
    try {
      ByteArrayOutputStream byteArrayOutputStream;
      switch (this._outputType) {
        case 0:
          paramJavaClass.dump(new BufferedOutputStream(new FileOutputStream(getOutputFile(paramJavaClass.getClassName()))));
          break;
        case 1:
          this._bcelClasses.addElement(paramJavaClass);
          break;
        case 2:
        case 3:
        case 4:
        case 5:
          byteArrayOutputStream = new ByteArrayOutputStream(2048);
          paramJavaClass.dump(byteArrayOutputStream);
          this._classes.addElement(byteArrayOutputStream.toByteArray());
          if (this._outputType == 4) {
            paramJavaClass.dump(new BufferedOutputStream(new FileOutputStream(getOutputFile(paramJavaClass.getClassName()))));
            break;
          } 
          if (this._outputType == 5)
            this._bcelClasses.addElement(paramJavaClass); 
          break;
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  private String entryName(File paramFile) throws IOException { return paramFile.getName().replace(File.separatorChar, '/'); }
  
  public void outputToJar() {
    Manifest manifest = new Manifest();
    Attributes attributes = manifest.getMainAttributes();
    attributes.put(Attributes.Name.MANIFEST_VERSION, "1.2");
    Map map = manifest.getEntries();
    Enumeration enumeration = this._bcelClasses.elements();
    String str = (new Date()).toString();
    Attributes.Name name = new Attributes.Name("Date");
    while (enumeration.hasMoreElements()) {
      JavaClass javaClass = (JavaClass)enumeration.nextElement();
      String str1 = javaClass.getClassName().replace('.', '/');
      Attributes attributes1 = new Attributes();
      attributes1.put(name, str);
      map.put(str1 + ".class", attributes1);
    } 
    File file = new File(this._destDir, this._jarFileName);
    JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(file), manifest);
    enumeration = this._bcelClasses.elements();
    while (enumeration.hasMoreElements()) {
      JavaClass javaClass = (JavaClass)enumeration.nextElement();
      String str1 = javaClass.getClassName().replace('.', '/');
      jarOutputStream.putNextEntry(new JarEntry(str1 + ".class"));
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(2048);
      javaClass.dump(byteArrayOutputStream);
      byteArrayOutputStream.writeTo(jarOutputStream);
    } 
    jarOutputStream.close();
  }
  
  public void setDebug(boolean paramBoolean) { this._debug = paramBoolean; }
  
  public boolean debug() { return this._debug; }
  
  public String getCharacterData(int paramInt) { return ((StringBuffer)this.m_characterData.elementAt(paramInt)).toString(); }
  
  public int getCharacterDataCount() { return (this.m_characterData != null) ? this.m_characterData.size() : 0; }
  
  public int addCharacterData(String paramString) {
    StringBuffer stringBuffer;
    if (this.m_characterData == null) {
      this.m_characterData = new Vector();
      stringBuffer = new StringBuffer();
      this.m_characterData.addElement(stringBuffer);
    } else {
      stringBuffer = (StringBuffer)this.m_characterData.elementAt(this.m_characterData.size() - 1);
    } 
    if (paramString.length() + stringBuffer.length() > 21845) {
      stringBuffer = new StringBuffer();
      this.m_characterData.addElement(stringBuffer);
    } 
    int i = stringBuffer.length();
    stringBuffer.append(paramString);
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\XSLTC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */