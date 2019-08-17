package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.impl.io.ASCIIReader;
import com.sun.org.apache.xerces.internal.impl.io.UCSReader;
import com.sun.org.apache.xerces.internal.impl.io.UTF8Reader;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
import com.sun.org.apache.xerces.internal.util.EncodingMap;
import com.sun.org.apache.xerces.internal.util.HTTPInputSource;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLEntityDescriptionImpl;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.utils.XMLLimitAnalyzer;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.xml.internal.stream.Entity;
import com.sun.xml.internal.stream.StaxEntityResolverWrapper;
import com.sun.xml.internal.stream.StaxXMLInputSource;
import com.sun.xml.internal.stream.XMLEntityStorage;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

public class XMLEntityManager implements XMLComponent, XMLEntityResolver {
  public static final int DEFAULT_BUFFER_SIZE = 8192;
  
  public static final int DEFAULT_XMLDECL_BUFFER_SIZE = 64;
  
  public static final int DEFAULT_INTERNAL_BUFFER_SIZE = 1024;
  
  protected static final String VALIDATION = "http://xml.org/sax/features/validation";
  
  protected boolean fStrictURI;
  
  protected static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
  
  protected static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
  
  protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
  
  protected static final String WARN_ON_DUPLICATE_ENTITYDEF = "http://apache.org/xml/features/warn-on-duplicate-entitydef";
  
  protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
  
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  protected static final String STANDARD_URI_CONFORMANT = "http://apache.org/xml/features/standard-uri-conformant";
  
  protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  
  protected static final String STAX_ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/stax-entity-resolver";
  
  protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
  
  protected static final String BUFFER_SIZE = "http://apache.org/xml/properties/input-buffer-size";
  
  protected static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  
  protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
  
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  
  static final String EXTERNAL_ACCESS_DEFAULT = "all";
  
  private static final String[] RECOGNIZED_FEATURES = { "http://xml.org/sax/features/validation", "http://xml.org/sax/features/external-general-entities", "http://xml.org/sax/features/external-parameter-entities", "http://apache.org/xml/features/allow-java-encodings", "http://apache.org/xml/features/warn-on-duplicate-entitydef", "http://apache.org/xml/features/standard-uri-conformant" };
  
  private static final Boolean[] FEATURE_DEFAULTS = { null, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE };
  
  private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/input-buffer-size", "http://apache.org/xml/properties/security-manager", "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager" };
  
  private static final Object[] PROPERTY_DEFAULTS = { null, null, null, null, new Integer(8192), null, null };
  
  private static final String XMLEntity = "[xml]".intern();
  
  private static final String DTDEntity = "[dtd]".intern();
  
  private static final boolean DEBUG_BUFFER = false;
  
  protected boolean fWarnDuplicateEntityDef;
  
  private static final boolean DEBUG_ENTITIES = false;
  
  private static final boolean DEBUG_ENCODINGS = false;
  
  private static final boolean DEBUG_RESOLVER = false;
  
  protected boolean fValidation;
  
  protected boolean fExternalGeneralEntities;
  
  protected boolean fExternalParameterEntities;
  
  protected boolean fAllowJavaEncodings = true;
  
  protected boolean fLoadExternalDTD = true;
  
  protected SymbolTable fSymbolTable;
  
  protected XMLErrorReporter fErrorReporter;
  
  protected XMLEntityResolver fEntityResolver;
  
  protected StaxEntityResolverWrapper fStaxEntityResolver;
  
  protected PropertyManager fPropertyManager;
  
  boolean fSupportDTD = true;
  
  boolean fReplaceEntityReferences = true;
  
  boolean fSupportExternalEntities = true;
  
  protected String fAccessExternalDTD = "all";
  
  protected ValidationManager fValidationManager;
  
  protected int fBufferSize = 8192;
  
  protected XMLSecurityManager fSecurityManager = null;
  
  protected XMLLimitAnalyzer fLimitAnalyzer = null;
  
  protected int entityExpansionIndex;
  
  protected boolean fStandalone;
  
  protected boolean fInExternalSubset = false;
  
  protected XMLEntityHandler fEntityHandler;
  
  protected XMLEntityScanner fEntityScanner;
  
  protected XMLEntityScanner fXML10EntityScanner;
  
  protected XMLEntityScanner fXML11EntityScanner;
  
  protected int fEntityExpansionCount = 0;
  
  protected Map<String, Entity> fEntities = new HashMap();
  
  protected Stack<Entity> fEntityStack = new Stack();
  
  protected Entity.ScannedEntity fCurrentEntity = null;
  
  boolean fISCreatedByResolver = false;
  
  protected XMLEntityStorage fEntityStorage;
  
  protected final Object[] defaultEncoding = { "UTF-8", null };
  
  private final XMLResourceIdentifierImpl fResourceIdentifier = new XMLResourceIdentifierImpl();
  
  private final Augmentations fEntityAugs = new AugmentationsImpl();
  
  private CharacterBufferPool fBufferPool = new CharacterBufferPool(this.fBufferSize, 1024);
  
  private static String gUserDir;
  
  private static URI gUserDirURI;
  
  private static boolean[] gNeedEscaping = new boolean[128];
  
  private static char[] gAfterEscaping1 = new char[128];
  
  private static char[] gAfterEscaping2 = new char[128];
  
  private static char[] gHexChs = { 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      'A', 'B', 'C', 'D', 'E', 'F' };
  
  public XMLEntityManager() {
    this.fSecurityManager = new XMLSecurityManager(true);
    this.fEntityStorage = new XMLEntityStorage(this);
    setScannerVersion((short)1);
  }
  
  public XMLEntityManager(PropertyManager paramPropertyManager) {
    this.fPropertyManager = paramPropertyManager;
    this.fEntityStorage = new XMLEntityStorage(this);
    this.fEntityScanner = new XMLEntityScanner(paramPropertyManager, this);
    reset(paramPropertyManager);
  }
  
  public void addInternalEntity(String paramString1, String paramString2) {
    if (!this.fEntities.containsKey(paramString1)) {
      Entity.InternalEntity internalEntity = new Entity.InternalEntity(paramString1, paramString2, this.fInExternalSubset);
      this.fEntities.put(paramString1, internalEntity);
    } else if (this.fWarnDuplicateEntityDef) {
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { paramString1 }, (short)0);
    } 
  }
  
  public void addExternalEntity(String paramString1, String paramString2, String paramString3, String paramString4) throws IOException {
    if (!this.fEntities.containsKey(paramString1)) {
      if (paramString4 == null) {
        int i = this.fEntityStack.size();
        if (i == 0 && this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null)
          paramString4 = this.fCurrentEntity.entityLocation.getExpandedSystemId(); 
        for (int j = i - 1; j >= 0; j--) {
          Entity.ScannedEntity scannedEntity = (Entity.ScannedEntity)this.fEntityStack.elementAt(j);
          if (scannedEntity.entityLocation != null && scannedEntity.entityLocation.getExpandedSystemId() != null) {
            paramString4 = scannedEntity.entityLocation.getExpandedSystemId();
            break;
          } 
        } 
      } 
      Entity.ExternalEntity externalEntity = new Entity.ExternalEntity(paramString1, new XMLEntityDescriptionImpl(paramString1, paramString2, paramString3, paramString4, expandSystemId(paramString3, paramString4, false)), null, this.fInExternalSubset);
      this.fEntities.put(paramString1, externalEntity);
    } else if (this.fWarnDuplicateEntityDef) {
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { paramString1 }, (short)0);
    } 
  }
  
  public void addUnparsedEntity(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) {
    if (!this.fEntities.containsKey(paramString1)) {
      Entity.ExternalEntity externalEntity = new Entity.ExternalEntity(paramString1, new XMLEntityDescriptionImpl(paramString1, paramString2, paramString3, paramString4, null), paramString5, this.fInExternalSubset);
      this.fEntities.put(paramString1, externalEntity);
    } else if (this.fWarnDuplicateEntityDef) {
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { paramString1 }, (short)0);
    } 
  }
  
  public XMLEntityStorage getEntityStore() { return this.fEntityStorage; }
  
  public XMLEntityScanner getEntityScanner() {
    if (this.fEntityScanner == null) {
      if (this.fXML10EntityScanner == null)
        this.fXML10EntityScanner = new XMLEntityScanner(); 
      this.fXML10EntityScanner.reset(this.fSymbolTable, this, this.fErrorReporter);
      this.fEntityScanner = this.fXML10EntityScanner;
    } 
    return this.fEntityScanner;
  }
  
  public void setScannerVersion(short paramShort) {
    if (paramShort == 1) {
      if (this.fXML10EntityScanner == null)
        this.fXML10EntityScanner = new XMLEntityScanner(); 
      this.fXML10EntityScanner.reset(this.fSymbolTable, this, this.fErrorReporter);
      this.fEntityScanner = this.fXML10EntityScanner;
      this.fEntityScanner.setCurrentEntity(this.fCurrentEntity);
    } else {
      if (this.fXML11EntityScanner == null)
        this.fXML11EntityScanner = new XML11EntityScanner(); 
      this.fXML11EntityScanner.reset(this.fSymbolTable, this, this.fErrorReporter);
      this.fEntityScanner = this.fXML11EntityScanner;
      this.fEntityScanner.setCurrentEntity(this.fCurrentEntity);
    } 
  }
  
  public String setupCurrentEntity(boolean paramBoolean1, String paramString, XMLInputSource paramXMLInputSource, boolean paramBoolean2, boolean paramBoolean3) throws IOException, XNIException {
    String str1 = paramXMLInputSource.getPublicId();
    String str2 = paramXMLInputSource.getSystemId();
    String str3 = paramXMLInputSource.getBaseSystemId();
    String str4 = paramXMLInputSource.getEncoding();
    boolean bool = (str4 != null);
    Boolean bool1 = null;
    InputStream inputStream = null;
    Reader reader = paramXMLInputSource.getCharacterStream();
    String str5 = expandSystemId(str2, str3, this.fStrictURI);
    if (str3 == null)
      str3 = str5; 
    if (reader == null) {
      inputStream = paramXMLInputSource.getByteStream();
      if (inputStream == null) {
        URL uRL = new URL(str5);
        URLConnection uRLConnection = uRL.openConnection();
        if (!(uRLConnection instanceof HttpURLConnection)) {
          inputStream = uRLConnection.getInputStream();
        } else {
          boolean bool2 = true;
          if (paramXMLInputSource instanceof HTTPInputSource) {
            HttpURLConnection httpURLConnection = (HttpURLConnection)uRLConnection;
            HTTPInputSource hTTPInputSource = (HTTPInputSource)paramXMLInputSource;
            Iterator iterator = hTTPInputSource.getHTTPRequestProperties();
            while (iterator.hasNext()) {
              Map.Entry entry = (Map.Entry)iterator.next();
              httpURLConnection.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
            } 
            bool2 = hTTPInputSource.getFollowHTTPRedirects();
            if (!bool2)
              setInstanceFollowRedirects(httpURLConnection, bool2); 
          } 
          inputStream = uRLConnection.getInputStream();
          if (bool2) {
            String str = uRLConnection.getURL().toString();
            if (!str.equals(str5)) {
              str2 = str;
              str5 = str;
            } 
          } 
        } 
      } 
      inputStream = new RewindableInputStream(inputStream);
      if (str4 == null) {
        byte[] arrayOfByte = new byte[4];
        byte b;
        for (b = 0; b < 4; b++)
          arrayOfByte[b] = (byte)inputStream.read(); 
        if (b == 4) {
          Object[] arrayOfObject = getEncodingName(arrayOfByte, b);
          str4 = (String)arrayOfObject[0];
          bool1 = (Boolean)arrayOfObject[1];
          inputStream.reset();
          if (b > 2 && str4.equals("UTF-8")) {
            byte b1 = arrayOfByte[0] & 0xFF;
            byte b2 = arrayOfByte[1] & 0xFF;
            byte b3 = arrayOfByte[2] & 0xFF;
            if (b1 == 239 && b2 == 187 && b3 == 191)
              inputStream.skip(3L); 
          } 
          reader = createReader(inputStream, str4, bool1);
        } else {
          reader = createReader(inputStream, str4, bool1);
        } 
      } else {
        str4 = str4.toUpperCase(Locale.ENGLISH);
        if (str4.equals("UTF-8")) {
          int[] arrayOfInt = new int[3];
          byte b;
          for (b = 0; b < 3; b++) {
            arrayOfInt[b] = inputStream.read();
            if (arrayOfInt[b] == -1)
              break; 
          } 
          if (b == 3) {
            if (arrayOfInt[0] != 239 || arrayOfInt[1] != 187 || arrayOfInt[2] != 191)
              inputStream.reset(); 
          } else {
            inputStream.reset();
          } 
        } else if (str4.equals("UTF-16")) {
          int[] arrayOfInt = new int[4];
          byte b;
          for (b = 0; b < 4; b++) {
            arrayOfInt[b] = inputStream.read();
            if (arrayOfInt[b] == -1)
              break; 
          } 
          inputStream.reset();
          String str = "UTF-16";
          if (b >= 2) {
            int i = arrayOfInt[0];
            int j = arrayOfInt[1];
            if (i == 254 && j == 255) {
              str = "UTF-16BE";
              bool1 = Boolean.TRUE;
            } else if (i == 255 && j == 254) {
              str = "UTF-16LE";
              bool1 = Boolean.FALSE;
            } else if (b == 4) {
              int k = arrayOfInt[2];
              int m = arrayOfInt[3];
              if (i == 0 && j == 60 && k == 0 && m == 63) {
                str = "UTF-16BE";
                bool1 = Boolean.TRUE;
              } 
              if (i == 60 && j == 0 && k == 63 && m == 0) {
                str = "UTF-16LE";
                bool1 = Boolean.FALSE;
              } 
            } 
          } 
          reader = createReader(inputStream, str, bool1);
        } else if (str4.equals("ISO-10646-UCS-4")) {
          int[] arrayOfInt = new int[4];
          byte b;
          for (b = 0; b < 4; b++) {
            arrayOfInt[b] = inputStream.read();
            if (arrayOfInt[b] == -1)
              break; 
          } 
          inputStream.reset();
          if (b == 4)
            if (arrayOfInt[0] == 0 && arrayOfInt[1] == 0 && arrayOfInt[2] == 0 && arrayOfInt[3] == 60) {
              bool1 = Boolean.TRUE;
            } else if (arrayOfInt[0] == 60 && arrayOfInt[1] == 0 && arrayOfInt[2] == 0 && arrayOfInt[3] == 0) {
              bool1 = Boolean.FALSE;
            }  
        } else if (str4.equals("ISO-10646-UCS-2")) {
          int[] arrayOfInt = new int[4];
          byte b;
          for (b = 0; b < 4; b++) {
            arrayOfInt[b] = inputStream.read();
            if (arrayOfInt[b] == -1)
              break; 
          } 
          inputStream.reset();
          if (b == 4)
            if (arrayOfInt[0] == 0 && arrayOfInt[1] == 60 && arrayOfInt[2] == 0 && arrayOfInt[3] == 63) {
              bool1 = Boolean.TRUE;
            } else if (arrayOfInt[0] == 60 && arrayOfInt[1] == 0 && arrayOfInt[2] == 63 && arrayOfInt[3] == 0) {
              bool1 = Boolean.FALSE;
            }  
        } 
        reader = createReader(inputStream, str4, bool1);
      } 
    } 
    if (this.fCurrentEntity != null)
      this.fEntityStack.push(this.fCurrentEntity); 
    this.fCurrentEntity = new Entity.ScannedEntity(paramBoolean1, paramString, new XMLResourceIdentifierImpl(str1, str2, str3, str5), inputStream, reader, str4, paramBoolean2, bool, paramBoolean3);
    this.fCurrentEntity.setEncodingExternallySpecified(bool);
    this.fEntityScanner.setCurrentEntity(this.fCurrentEntity);
    this.fResourceIdentifier.setValues(str1, str2, str3, str5);
    if (this.fLimitAnalyzer != null)
      this.fLimitAnalyzer.startEntity(paramString); 
    return str4;
  }
  
  public boolean isExternalEntity(String paramString) {
    Entity entity = (Entity)this.fEntities.get(paramString);
    return (entity == null) ? false : entity.isExternal();
  }
  
  public boolean isEntityDeclInExternalSubset(String paramString) {
    Entity entity = (Entity)this.fEntities.get(paramString);
    return (entity == null) ? false : entity.isEntityDeclInExternalSubset();
  }
  
  public void setStandalone(boolean paramBoolean) { this.fStandalone = paramBoolean; }
  
  public boolean isStandalone() { return this.fStandalone; }
  
  public boolean isDeclaredEntity(String paramString) {
    Entity entity = (Entity)this.fEntities.get(paramString);
    return (entity != null);
  }
  
  public boolean isUnparsedEntity(String paramString) {
    Entity entity = (Entity)this.fEntities.get(paramString);
    return (entity == null) ? false : entity.isUnparsed();
  }
  
  public XMLResourceIdentifier getCurrentResourceIdentifier() { return this.fResourceIdentifier; }
  
  public void setEntityHandler(XMLEntityHandler paramXMLEntityHandler) { this.fEntityHandler = paramXMLEntityHandler; }
  
  public StaxXMLInputSource resolveEntityAsPerStax(XMLResourceIdentifier paramXMLResourceIdentifier) throws IOException {
    if (paramXMLResourceIdentifier == null)
      return null; 
    String str1 = paramXMLResourceIdentifier.getPublicId();
    String str2 = paramXMLResourceIdentifier.getLiteralSystemId();
    String str3 = paramXMLResourceIdentifier.getBaseSystemId();
    String str4 = paramXMLResourceIdentifier.getExpandedSystemId();
    boolean bool = (str4 == null) ? 1 : 0;
    if (str3 == null && this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) {
      str3 = this.fCurrentEntity.entityLocation.getExpandedSystemId();
      if (str3 != null)
        bool = true; 
    } 
    if (bool)
      str4 = expandSystemId(str2, str3, false); 
    StaxXMLInputSource staxXMLInputSource = null;
    XMLInputSource xMLInputSource = null;
    XMLResourceIdentifierImpl xMLResourceIdentifierImpl = null;
    if (paramXMLResourceIdentifier instanceof XMLResourceIdentifierImpl) {
      xMLResourceIdentifierImpl = (XMLResourceIdentifierImpl)paramXMLResourceIdentifier;
    } else {
      this.fResourceIdentifier.clear();
      xMLResourceIdentifierImpl = this.fResourceIdentifier;
    } 
    xMLResourceIdentifierImpl.setValues(str1, str2, str3, str4);
    this.fISCreatedByResolver = false;
    if (this.fStaxEntityResolver != null) {
      staxXMLInputSource = this.fStaxEntityResolver.resolveEntity(xMLResourceIdentifierImpl);
      if (staxXMLInputSource != null)
        this.fISCreatedByResolver = true; 
    } 
    if (this.fEntityResolver != null) {
      xMLInputSource = this.fEntityResolver.resolveEntity(xMLResourceIdentifierImpl);
      if (xMLInputSource != null)
        this.fISCreatedByResolver = true; 
    } 
    if (xMLInputSource != null)
      staxXMLInputSource = new StaxXMLInputSource(xMLInputSource, this.fISCreatedByResolver); 
    if (staxXMLInputSource == null) {
      staxXMLInputSource = new StaxXMLInputSource(new XMLInputSource(str1, str2, str3));
    } else if (staxXMLInputSource.hasXMLStreamOrXMLEventReader()) {
    
    } 
    return staxXMLInputSource;
  }
  
  public XMLInputSource resolveEntity(XMLResourceIdentifier paramXMLResourceIdentifier) throws IOException, XNIException {
    if (paramXMLResourceIdentifier == null)
      return null; 
    String str1 = paramXMLResourceIdentifier.getPublicId();
    String str2 = paramXMLResourceIdentifier.getLiteralSystemId();
    String str3 = paramXMLResourceIdentifier.getBaseSystemId();
    String str4 = paramXMLResourceIdentifier.getExpandedSystemId();
    boolean bool = (str4 == null) ? 1 : 0;
    if (str3 == null && this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) {
      str3 = this.fCurrentEntity.entityLocation.getExpandedSystemId();
      if (str3 != null)
        bool = true; 
    } 
    if (bool)
      str4 = expandSystemId(str2, str3, false); 
    XMLInputSource xMLInputSource = null;
    if (this.fEntityResolver != null) {
      paramXMLResourceIdentifier.setBaseSystemId(str3);
      paramXMLResourceIdentifier.setExpandedSystemId(str4);
      xMLInputSource = this.fEntityResolver.resolveEntity(paramXMLResourceIdentifier);
    } 
    if (xMLInputSource == null)
      xMLInputSource = new XMLInputSource(str1, str2, str3); 
    return xMLInputSource;
  }
  
  public void startEntity(boolean paramBoolean1, String paramString, boolean paramBoolean2) throws IOException, XNIException {
    Entity entity = this.fEntityStorage.getEntity(paramString);
    if (entity == null) {
      if (this.fEntityHandler != null) {
        String str = null;
        this.fResourceIdentifier.clear();
        this.fEntityAugs.removeAllItems();
        this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
        this.fEntityHandler.startEntity(paramString, this.fResourceIdentifier, str, this.fEntityAugs);
        this.fEntityAugs.removeAllItems();
        this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
        this.fEntityHandler.endEntity(paramString, this.fEntityAugs);
      } 
      return;
    } 
    boolean bool = entity.isExternal();
    Entity.ExternalEntity externalEntity = null;
    String str1 = null;
    String str2 = null;
    String str3 = null;
    if (bool) {
      externalEntity = (Entity.ExternalEntity)entity;
      str1 = (externalEntity.entityLocation != null) ? externalEntity.entityLocation.getLiteralSystemId() : null;
      str2 = (externalEntity.entityLocation != null) ? externalEntity.entityLocation.getBaseSystemId() : null;
      str3 = expandSystemId(str1, str2);
      boolean bool1 = entity.isUnparsed();
      boolean bool2 = paramString.startsWith("%");
      boolean bool3 = !bool2 ? 1 : 0;
      if (bool1 || (bool3 && !this.fExternalGeneralEntities) || (bool2 && !this.fExternalParameterEntities) || !this.fSupportDTD || !this.fSupportExternalEntities) {
        if (this.fEntityHandler != null) {
          this.fResourceIdentifier.clear();
          String str = null;
          this.fResourceIdentifier.setValues((externalEntity.entityLocation != null) ? externalEntity.entityLocation.getPublicId() : null, str1, str2, str3);
          this.fEntityAugs.removeAllItems();
          this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
          this.fEntityHandler.startEntity(paramString, this.fResourceIdentifier, str, this.fEntityAugs);
          this.fEntityAugs.removeAllItems();
          this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
          this.fEntityHandler.endEntity(paramString, this.fEntityAugs);
        } 
        return;
      } 
    } 
    int i = this.fEntityStack.size();
    for (int j = i; j >= 0; j--) {
      Entity.ScannedEntity scannedEntity = (j == i) ? this.fCurrentEntity : (Entity)this.fEntityStack.elementAt(j);
      if (scannedEntity.name == paramString) {
        String str = paramString;
        for (int k = j + 1; k < i; k++) {
          Entity entity1 = (Entity)this.fEntityStack.elementAt(k);
          str = str + " -> " + entity1.name;
        } 
        str = str + " -> " + this.fCurrentEntity.name;
        str = str + " -> " + paramString;
        this.fErrorReporter.reportError(getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "RecursiveReference", new Object[] { paramString, str }, (short)2);
        if (this.fEntityHandler != null) {
          this.fResourceIdentifier.clear();
          String str4 = null;
          if (bool)
            this.fResourceIdentifier.setValues((externalEntity.entityLocation != null) ? externalEntity.entityLocation.getPublicId() : null, str1, str2, str3); 
          this.fEntityAugs.removeAllItems();
          this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
          this.fEntityHandler.startEntity(paramString, this.fResourceIdentifier, str4, this.fEntityAugs);
          this.fEntityAugs.removeAllItems();
          this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
          this.fEntityHandler.endEntity(paramString, this.fEntityAugs);
        } 
        return;
      } 
    } 
    StaxXMLInputSource staxXMLInputSource = null;
    XMLInputSource xMLInputSource = null;
    if (bool) {
      staxXMLInputSource = resolveEntityAsPerStax(externalEntity.entityLocation);
      xMLInputSource = staxXMLInputSource.getXMLInputSource();
      if (!this.fISCreatedByResolver && this.fLoadExternalDTD) {
        String str = SecuritySupport.checkAccess(str3, this.fAccessExternalDTD, "all");
        if (str != null)
          this.fErrorReporter.reportError(getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "AccessExternalEntity", new Object[] { SecuritySupport.sanitizePath(str3), str }, (short)2); 
      } 
    } else {
      Entity.InternalEntity internalEntity = (Entity.InternalEntity)entity;
      StringReader stringReader = new StringReader(internalEntity.text);
      xMLInputSource = new XMLInputSource(null, null, null, stringReader, null);
    } 
    startEntity(paramBoolean1, paramString, xMLInputSource, paramBoolean2, bool);
  }
  
  public void startDocumentEntity(XMLInputSource paramXMLInputSource) throws IOException, XNIException { startEntity(false, XMLEntity, paramXMLInputSource, false, true); }
  
  public void startDTDEntity(XMLInputSource paramXMLInputSource) throws IOException, XNIException { startEntity(false, DTDEntity, paramXMLInputSource, false, true); }
  
  public void startExternalSubset() { this.fInExternalSubset = true; }
  
  public void endExternalSubset() { this.fInExternalSubset = false; }
  
  public void startEntity(boolean paramBoolean1, String paramString, XMLInputSource paramXMLInputSource, boolean paramBoolean2, boolean paramBoolean3) throws IOException, XNIException {
    String str = setupCurrentEntity(paramBoolean1, paramString, paramXMLInputSource, paramBoolean2, paramBoolean3);
    this.fEntityExpansionCount++;
    if (this.fLimitAnalyzer != null)
      this.fLimitAnalyzer.addValue(this.entityExpansionIndex, paramString, 1); 
    if (this.fSecurityManager != null && this.fSecurityManager.isOverLimit(this.entityExpansionIndex, this.fLimitAnalyzer)) {
      this.fSecurityManager.debugPrint(this.fLimitAnalyzer);
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EntityExpansionLimit", new Object[] { this.fSecurityManager.getLimitValueByIndex(this.entityExpansionIndex) }, (short)2);
      this.fEntityExpansionCount = 0;
    } 
    if (this.fEntityHandler != null)
      this.fEntityHandler.startEntity(paramString, this.fResourceIdentifier, str, null); 
  }
  
  public Entity.ScannedEntity getCurrentEntity() { return this.fCurrentEntity; }
  
  public Entity.ScannedEntity getTopLevelEntity() { return (Entity.ScannedEntity)(this.fEntityStack.empty() ? null : (Entity)this.fEntityStack.elementAt(0)); }
  
  public void closeReaders() {}
  
  public void endEntity() {
    Entity.ScannedEntity scannedEntity = (this.fEntityStack.size() > 0) ? (Entity.ScannedEntity)this.fEntityStack.pop() : null;
    if (this.fCurrentEntity != null)
      try {
        if (this.fLimitAnalyzer != null) {
          this.fLimitAnalyzer.endEntity(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT, this.fCurrentEntity.name);
          if (this.fCurrentEntity.name.equals("[xml]"))
            this.fSecurityManager.debugPrint(this.fLimitAnalyzer); 
        } 
        this.fCurrentEntity.close();
      } catch (IOException iOException) {
        throw new XNIException(iOException);
      }  
    if (this.fEntityHandler != null)
      if (scannedEntity == null) {
        this.fEntityAugs.removeAllItems();
        this.fEntityAugs.putItem("LAST_ENTITY", Boolean.TRUE);
        this.fEntityHandler.endEntity(this.fCurrentEntity.name, this.fEntityAugs);
        this.fEntityAugs.removeAllItems();
      } else {
        this.fEntityHandler.endEntity(this.fCurrentEntity.name, null);
      }  
    boolean bool = (this.fCurrentEntity.name == XMLEntity) ? 1 : 0;
    this.fCurrentEntity = scannedEntity;
    this.fEntityScanner.setCurrentEntity(this.fCurrentEntity);
    if (((this.fCurrentEntity == null) ? 1 : 0) & (!bool ? 1 : 0))
      throw new EOFException(); 
  }
  
  public void reset(PropertyManager paramPropertyManager) {
    this.fSymbolTable = (SymbolTable)paramPropertyManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
    this.fErrorReporter = (XMLErrorReporter)paramPropertyManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    try {
      this.fStaxEntityResolver = (StaxEntityResolverWrapper)paramPropertyManager.getProperty("http://apache.org/xml/properties/internal/stax-entity-resolver");
    } catch (XMLConfigurationException xMLConfigurationException) {
      this.fStaxEntityResolver = null;
    } 
    this.fSupportDTD = ((Boolean)paramPropertyManager.getProperty("javax.xml.stream.supportDTD")).booleanValue();
    this.fReplaceEntityReferences = ((Boolean)paramPropertyManager.getProperty("javax.xml.stream.isReplacingEntityReferences")).booleanValue();
    this.fSupportExternalEntities = ((Boolean)paramPropertyManager.getProperty("javax.xml.stream.isSupportingExternalEntities")).booleanValue();
    this.fLoadExternalDTD = !((Boolean)paramPropertyManager.getProperty("http://java.sun.com/xml/stream/properties/ignore-external-dtd")).booleanValue();
    XMLSecurityPropertyManager xMLSecurityPropertyManager = (XMLSecurityPropertyManager)paramPropertyManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager");
    this.fAccessExternalDTD = xMLSecurityPropertyManager.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
    this.fSecurityManager = (XMLSecurityManager)paramPropertyManager.getProperty("http://apache.org/xml/properties/security-manager");
    this.fLimitAnalyzer = new XMLLimitAnalyzer();
    this.fEntityStorage.reset(paramPropertyManager);
    this.fEntityScanner.reset(paramPropertyManager);
    this.fEntities.clear();
    this.fEntityStack.removeAllElements();
    this.fCurrentEntity = null;
    this.fValidation = false;
    this.fExternalGeneralEntities = true;
    this.fExternalParameterEntities = true;
    this.fAllowJavaEncodings = true;
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager) throws XMLConfigurationException {
    boolean bool = paramXMLComponentManager.getFeature("http://apache.org/xml/features/internal/parser-settings", true);
    if (!bool) {
      reset();
      if (this.fEntityScanner != null)
        this.fEntityScanner.reset(paramXMLComponentManager); 
      if (this.fEntityStorage != null)
        this.fEntityStorage.reset(paramXMLComponentManager); 
      return;
    } 
    this.fValidation = paramXMLComponentManager.getFeature("http://xml.org/sax/features/validation", false);
    this.fExternalGeneralEntities = paramXMLComponentManager.getFeature("http://xml.org/sax/features/external-general-entities", true);
    this.fExternalParameterEntities = paramXMLComponentManager.getFeature("http://xml.org/sax/features/external-parameter-entities", true);
    this.fAllowJavaEncodings = paramXMLComponentManager.getFeature("http://apache.org/xml/features/allow-java-encodings", false);
    this.fWarnDuplicateEntityDef = paramXMLComponentManager.getFeature("http://apache.org/xml/features/warn-on-duplicate-entitydef", false);
    this.fStrictURI = paramXMLComponentManager.getFeature("http://apache.org/xml/features/standard-uri-conformant", false);
    this.fLoadExternalDTD = paramXMLComponentManager.getFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);
    this.fSymbolTable = (SymbolTable)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
    this.fErrorReporter = (XMLErrorReporter)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    this.fEntityResolver = (XMLEntityResolver)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver", null);
    this.fStaxEntityResolver = (StaxEntityResolverWrapper)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/stax-entity-resolver", null);
    this.fValidationManager = (ValidationManager)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager", null);
    this.fSecurityManager = (XMLSecurityManager)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/security-manager", null);
    this.entityExpansionIndex = this.fSecurityManager.getIndex("http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit");
    this.fSupportDTD = true;
    this.fReplaceEntityReferences = true;
    this.fSupportExternalEntities = true;
    XMLSecurityPropertyManager xMLSecurityPropertyManager = (XMLSecurityPropertyManager)paramXMLComponentManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", null);
    if (xMLSecurityPropertyManager == null)
      xMLSecurityPropertyManager = new XMLSecurityPropertyManager(); 
    this.fAccessExternalDTD = xMLSecurityPropertyManager.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
    reset();
    this.fEntityScanner.reset(paramXMLComponentManager);
    this.fEntityStorage.reset(paramXMLComponentManager);
  }
  
  public void reset() {
    this.fLimitAnalyzer = new XMLLimitAnalyzer();
    this.fStandalone = false;
    this.fEntities.clear();
    this.fEntityStack.removeAllElements();
    this.fEntityExpansionCount = 0;
    this.fCurrentEntity = null;
    if (this.fXML10EntityScanner != null)
      this.fXML10EntityScanner.reset(this.fSymbolTable, this, this.fErrorReporter); 
    if (this.fXML11EntityScanner != null)
      this.fXML11EntityScanner.reset(this.fSymbolTable, this, this.fErrorReporter); 
    this.fEntityHandler = null;
  }
  
  public String[] getRecognizedFeatures() { return (String[])RECOGNIZED_FEATURES.clone(); }
  
  public void setFeature(String paramString, boolean paramBoolean) throws XMLConfigurationException {
    if (paramString.startsWith("http://apache.org/xml/features/")) {
      int i = paramString.length() - "http://apache.org/xml/features/".length();
      if (i == "allow-java-encodings".length() && paramString.endsWith("allow-java-encodings"))
        this.fAllowJavaEncodings = paramBoolean; 
      if (i == "nonvalidating/load-external-dtd".length() && paramString.endsWith("nonvalidating/load-external-dtd")) {
        this.fLoadExternalDTD = paramBoolean;
        return;
      } 
    } 
  }
  
  public void setProperty(String paramString, Object paramObject) {
    if (paramString.startsWith("http://apache.org/xml/properties/")) {
      int i = paramString.length() - "http://apache.org/xml/properties/".length();
      if (i == "internal/symbol-table".length() && paramString.endsWith("internal/symbol-table")) {
        this.fSymbolTable = (SymbolTable)paramObject;
        return;
      } 
      if (i == "internal/error-reporter".length() && paramString.endsWith("internal/error-reporter")) {
        this.fErrorReporter = (XMLErrorReporter)paramObject;
        return;
      } 
      if (i == "internal/entity-resolver".length() && paramString.endsWith("internal/entity-resolver")) {
        this.fEntityResolver = (XMLEntityResolver)paramObject;
        return;
      } 
      if (i == "input-buffer-size".length() && paramString.endsWith("input-buffer-size")) {
        Integer integer = (Integer)paramObject;
        if (integer != null && integer.intValue() > 64) {
          this.fBufferSize = integer.intValue();
          this.fEntityScanner.setBufferSize(this.fBufferSize);
          this.fBufferPool.setExternalBufferSize(this.fBufferSize);
        } 
      } 
      if (i == "security-manager".length() && paramString.endsWith("security-manager"))
        this.fSecurityManager = (XMLSecurityManager)paramObject; 
    } 
    if (paramString.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager")) {
      XMLSecurityPropertyManager xMLSecurityPropertyManager = (XMLSecurityPropertyManager)paramObject;
      this.fAccessExternalDTD = xMLSecurityPropertyManager.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
    } 
  }
  
  public void setLimitAnalyzer(XMLLimitAnalyzer paramXMLLimitAnalyzer) { this.fLimitAnalyzer = paramXMLLimitAnalyzer; }
  
  public String[] getRecognizedProperties() { return (String[])RECOGNIZED_PROPERTIES.clone(); }
  
  public Boolean getFeatureDefault(String paramString) {
    for (byte b = 0; b < RECOGNIZED_FEATURES.length; b++) {
      if (RECOGNIZED_FEATURES[b].equals(paramString))
        return FEATURE_DEFAULTS[b]; 
    } 
    return null;
  }
  
  public Object getPropertyDefault(String paramString) {
    for (byte b = 0; b < RECOGNIZED_PROPERTIES.length; b++) {
      if (RECOGNIZED_PROPERTIES[b].equals(paramString))
        return PROPERTY_DEFAULTS[b]; 
    } 
    return null;
  }
  
  public static String expandSystemId(String paramString) { return expandSystemId(paramString, null); }
  
  private static URI getUserDir() throws URI.MalformedURIException {
    String str = "";
    try {
      str = SecuritySupport.getSystemProperty("user.dir");
    } catch (SecurityException securityException) {}
    if (str.length() == 0)
      return new URI("file", "", "", null, null); 
    if (gUserDirURI != null && str.equals(gUserDir))
      return gUserDirURI; 
    gUserDir = str;
    char c = File.separatorChar;
    str = str.replace(c, '/');
    int i = str.length();
    StringBuffer stringBuffer = new StringBuffer(i * 3);
    if (i >= 2 && str.charAt(1) == ':') {
      char c1 = Character.toUpperCase(str.charAt(0));
      if (c1 >= 'A' && c1 <= 'Z')
        stringBuffer.append('/'); 
    } 
    byte b;
    for (b = 0; b < i; b++) {
      char c1 = str.charAt(b);
      if (c1 >= '')
        break; 
      if (gNeedEscaping[c1]) {
        stringBuffer.append('%');
        stringBuffer.append(gAfterEscaping1[c1]);
        stringBuffer.append(gAfterEscaping2[c1]);
      } else {
        stringBuffer.append((char)c1);
      } 
    } 
    if (b < i) {
      byte[] arrayOfByte = null;
      try {
        arrayOfByte = str.substring(b).getBytes("UTF-8");
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        return new URI("file", "", str, null, null);
      } 
      i = arrayOfByte.length;
      for (b = 0; b < i; b++) {
        byte b1 = arrayOfByte[b];
        if (b1 < 0) {
          byte b2 = b1 + 256;
          stringBuffer.append('%');
          stringBuffer.append(gHexChs[b2 >> 4]);
          stringBuffer.append(gHexChs[b2 & 0xF]);
        } else if (gNeedEscaping[b1]) {
          stringBuffer.append('%');
          stringBuffer.append(gAfterEscaping1[b1]);
          stringBuffer.append(gAfterEscaping2[b1]);
        } else {
          stringBuffer.append((char)b1);
        } 
      } 
    } 
    if (!str.endsWith("/"))
      stringBuffer.append('/'); 
    gUserDirURI = new URI("file", "", stringBuffer.toString(), null, null);
    return gUserDirURI;
  }
  
  public static void absolutizeAgainstUserDir(URI paramURI) throws URI.MalformedURIException { paramURI.absolutize(getUserDir()); }
  
  public static String expandSystemId(String paramString1, String paramString2) {
    if (paramString1 == null || paramString1.length() == 0)
      return paramString1; 
    try {
      URI uRI = new URI(paramString1);
      if (uRI != null)
        return paramString1; 
    } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException malformedURIException) {}
    String str = fixURI(paramString1);
    URI uRI1 = null;
    URI uRI2 = null;
    try {
      if (paramString2 == null || paramString2.length() == 0 || paramString2.equals(paramString1)) {
        String str1 = getUserDir().toString();
        uRI1 = new URI("file", "", str1, null, null);
      } else {
        try {
          uRI1 = new URI(fixURI(paramString2));
        } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException malformedURIException) {
          if (paramString2.indexOf(':') != -1) {
            uRI1 = new URI("file", "", fixURI(paramString2), null, null);
          } else {
            String str1 = getUserDir().toString();
            str1 = str1 + fixURI(paramString2);
            uRI1 = new URI("file", "", str1, null, null);
          } 
        } 
      } 
      uRI2 = new URI(uRI1, str);
    } catch (Exception exception) {}
    return (uRI2 == null) ? paramString1 : uRI2.toString();
  }
  
  public static String expandSystemId(String paramString1, String paramString2, boolean paramBoolean) throws URI.MalformedURIException {
    if (paramString1 == null)
      return null; 
    if (paramBoolean)
      try {
        new URI(paramString1);
        return paramString1;
      } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException null) {
        URI uRI1 = null;
        if (paramString2 == null || paramString2.length() == 0) {
          URI uRI = new URI("file", "", getUserDir().toString(), null, null);
        } else {
          try {
            URI uRI = new URI(paramString2);
          } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException malformedURIException) {
            String str = getUserDir().toString();
            str = str + paramString2;
            uRI1 = new URI("file", "", str, null, null);
          } 
        } 
        URI uRI2 = new URI(uRI1, paramString1);
        return uRI2.toString();
      }  
    try {
      return expandSystemIdStrictOff(paramString1, paramString2);
    } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException malformedURIException) {
      try {
        return expandSystemIdStrictOff1(paramString1, paramString2);
      } catch (URISyntaxException uRISyntaxException) {
        if (paramString1.length() == 0)
          return paramString1; 
        String str = fixURI(paramString1);
        uRISyntaxException = null;
        URI uRI = null;
        try {
          URI uRI1;
          if (paramString2 == null || paramString2.length() == 0 || paramString2.equals(paramString1)) {
            uRI1 = getUserDir();
          } else {
            try {
              uRI1 = new URI(fixURI(paramString2).trim());
            } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException malformedURIException1) {
              if (paramString2.indexOf(':') != -1) {
                uRI1 = new URI("file", "", fixURI(paramString2).trim(), null, null);
              } else {
                uRI1 = new URI(getUserDir(), fixURI(paramString2));
              } 
            } 
          } 
          uRI = new URI(uRI1, str.trim());
        } catch (Exception exception) {}
        return (uRI == null) ? paramString1 : uRI.toString();
      } 
    } 
  }
  
  private static String expandSystemIdStrictOn(String paramString1, String paramString2) {
    URI uRI1 = new URI(paramString1, true);
    if (uRI1.isAbsoluteURI())
      return paramString1; 
    URI uRI2 = null;
    if (paramString2 == null || paramString2.length() == 0) {
      uRI2 = getUserDir();
    } else {
      uRI2 = new URI(paramString2, true);
      if (!uRI2.isAbsoluteURI())
        uRI2.absolutize(getUserDir()); 
    } 
    uRI1.absolutize(uRI2);
    return uRI1.toString();
  }
  
  public static void setInstanceFollowRedirects(HttpURLConnection paramHttpURLConnection, boolean paramBoolean) {
    try {
      Method method = HttpURLConnection.class.getMethod("setInstanceFollowRedirects", new Class[] { boolean.class });
      method.invoke(paramHttpURLConnection, new Object[] { paramBoolean ? Boolean.TRUE : Boolean.FALSE });
    } catch (Exception exception) {}
  }
  
  private static String expandSystemIdStrictOff(String paramString1, String paramString2) {
    URI uRI1 = new URI(paramString1, true);
    if (uRI1.isAbsoluteURI()) {
      if (uRI1.getScheme().length() > 1)
        return paramString1; 
      throw new URI.MalformedURIException();
    } 
    URI uRI2 = null;
    if (paramString2 == null || paramString2.length() == 0) {
      uRI2 = getUserDir();
    } else {
      uRI2 = new URI(paramString2, true);
      if (!uRI2.isAbsoluteURI())
        uRI2.absolutize(getUserDir()); 
    } 
    uRI1.absolutize(uRI2);
    return uRI1.toString();
  }
  
  private static String expandSystemIdStrictOff1(String paramString1, String paramString2) {
    URI uRI = new URI(paramString1);
    if (uRI.isAbsolute()) {
      if (uRI.getScheme().length() > 1)
        return paramString1; 
      throw new URISyntaxException(paramString1, "the scheme's length is only one character");
    } 
    URI uRI1 = null;
    if (paramString2 == null || paramString2.length() == 0) {
      uRI1 = getUserDir();
    } else {
      uRI1 = new URI(paramString2, true);
      if (!uRI1.isAbsoluteURI())
        uRI1.absolutize(getUserDir()); 
    } 
    uRI = (new URI(uRI1.toString())).resolve(uRI);
    return uRI.toString();
  }
  
  protected Object[] getEncodingName(byte[] paramArrayOfByte, int paramInt) {
    if (paramInt < 2)
      return this.defaultEncoding; 
    byte b1 = paramArrayOfByte[0] & 0xFF;
    byte b2 = paramArrayOfByte[1] & 0xFF;
    if (b1 == 254 && b2 == 255)
      return new Object[] { "UTF-16BE", new Boolean(true) }; 
    if (b1 == 255 && b2 == 254)
      return new Object[] { "UTF-16LE", new Boolean(false) }; 
    if (paramInt < 3)
      return this.defaultEncoding; 
    byte b3 = paramArrayOfByte[2] & 0xFF;
    if (b1 == 239 && b2 == 187 && b3 == 191)
      return this.defaultEncoding; 
    if (paramInt < 4)
      return this.defaultEncoding; 
    byte b4 = paramArrayOfByte[3] & 0xFF;
    return (b1 == 0 && b2 == 0 && b3 == 0 && b4 == 60) ? new Object[] { "ISO-10646-UCS-4", new Boolean(true) } : ((b1 == 60 && b2 == 0 && b3 == 0 && b4 == 0) ? new Object[] { "ISO-10646-UCS-4", new Boolean(false) } : ((b1 == 0 && b2 == 0 && b3 == 60 && b4 == 0) ? new Object[] { "ISO-10646-UCS-4", null } : ((b1 == 0 && b2 == 60 && b3 == 0 && b4 == 0) ? new Object[] { "ISO-10646-UCS-4", null } : ((b1 == 0 && b2 == 60 && b3 == 0 && b4 == 63) ? new Object[] { "UTF-16BE", new Boolean(true) } : ((b1 == 60 && b2 == 0 && b3 == 63 && b4 == 0) ? new Object[] { "UTF-16LE", new Boolean(false) } : ((b1 == 76 && b2 == 111 && b3 == 167 && b4 == 148) ? new Object[] { "CP037", null } : this.defaultEncoding))))));
  }
  
  protected Reader createReader(InputStream paramInputStream, String paramString, Boolean paramBoolean) throws IOException {
    if (paramString == null)
      paramString = "UTF-8"; 
    String str1 = paramString.toUpperCase(Locale.ENGLISH);
    if (str1.equals("UTF-8"))
      return new UTF8Reader(paramInputStream, this.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale()); 
    if (str1.equals("US-ASCII"))
      return new ASCIIReader(paramInputStream, this.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale()); 
    if (str1.equals("ISO-10646-UCS-4")) {
      if (paramBoolean != null) {
        boolean bool = paramBoolean.booleanValue();
        return bool ? new UCSReader(paramInputStream, (short)8) : new UCSReader(paramInputStream, (short)4);
      } 
      this.fErrorReporter.reportError(getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[] { paramString }, (short)2);
    } 
    if (str1.equals("ISO-10646-UCS-2")) {
      if (paramBoolean != null) {
        boolean bool = paramBoolean.booleanValue();
        return bool ? new UCSReader(paramInputStream, (short)2) : new UCSReader(paramInputStream, (short)1);
      } 
      this.fErrorReporter.reportError(getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[] { paramString }, (short)2);
    } 
    boolean bool1 = XMLChar.isValidIANAEncoding(paramString);
    boolean bool2 = XMLChar.isValidJavaEncoding(paramString);
    if (!bool1 || (this.fAllowJavaEncodings && !bool2)) {
      this.fErrorReporter.reportError(getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[] { paramString }, (short)2);
      paramString = "ISO-8859-1";
    } 
    String str2 = EncodingMap.getIANA2JavaMapping(str1);
    if (str2 == null)
      if (this.fAllowJavaEncodings) {
        str2 = paramString;
      } else {
        this.fErrorReporter.reportError(getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[] { paramString }, (short)2);
        str2 = "ISO8859_1";
      }  
    return new BufferedReader(new InputStreamReader(paramInputStream, str2));
  }
  
  public String getPublicId() { return (this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getPublicId() : null; }
  
  public String getExpandedSystemId() {
    if (this.fCurrentEntity != null) {
      if (this.fCurrentEntity.entityLocation != null && this.fCurrentEntity.entityLocation.getExpandedSystemId() != null)
        return this.fCurrentEntity.entityLocation.getExpandedSystemId(); 
      int i = this.fEntityStack.size();
      for (int j = i - 1; j >= 0; j--) {
        Entity.ScannedEntity scannedEntity = (Entity.ScannedEntity)this.fEntityStack.elementAt(j);
        if (scannedEntity.entityLocation != null && scannedEntity.entityLocation.getExpandedSystemId() != null)
          return scannedEntity.entityLocation.getExpandedSystemId(); 
      } 
    } 
    return null;
  }
  
  public String getLiteralSystemId() {
    if (this.fCurrentEntity != null) {
      if (this.fCurrentEntity.entityLocation != null && this.fCurrentEntity.entityLocation.getLiteralSystemId() != null)
        return this.fCurrentEntity.entityLocation.getLiteralSystemId(); 
      int i = this.fEntityStack.size();
      for (int j = i - 1; j >= 0; j--) {
        Entity.ScannedEntity scannedEntity = (Entity.ScannedEntity)this.fEntityStack.elementAt(j);
        if (scannedEntity.entityLocation != null && scannedEntity.entityLocation.getLiteralSystemId() != null)
          return scannedEntity.entityLocation.getLiteralSystemId(); 
      } 
    } 
    return null;
  }
  
  public int getLineNumber() {
    if (this.fCurrentEntity != null) {
      if (this.fCurrentEntity.isExternal())
        return this.fCurrentEntity.lineNumber; 
      int i = this.fEntityStack.size();
      for (int j = i - 1; j > 0; j--) {
        Entity.ScannedEntity scannedEntity = (Entity.ScannedEntity)this.fEntityStack.elementAt(j);
        if (scannedEntity.isExternal())
          return scannedEntity.lineNumber; 
      } 
    } 
    return -1;
  }
  
  public int getColumnNumber() {
    if (this.fCurrentEntity != null) {
      if (this.fCurrentEntity.isExternal())
        return this.fCurrentEntity.columnNumber; 
      int i = this.fEntityStack.size();
      for (int j = i - 1; j > 0; j--) {
        Entity.ScannedEntity scannedEntity = (Entity.ScannedEntity)this.fEntityStack.elementAt(j);
        if (scannedEntity.isExternal())
          return scannedEntity.columnNumber; 
      } 
    } 
    return -1;
  }
  
  protected static String fixURI(String paramString) {
    paramString = paramString.replace(File.separatorChar, '/');
    if (paramString.length() >= 2) {
      char c = paramString.charAt(1);
      if (c == ':') {
        char c1 = Character.toUpperCase(paramString.charAt(0));
        if (c1 >= 'A' && c1 <= 'Z')
          paramString = "/" + paramString; 
      } else if (c == '/' && paramString.charAt(0) == '/') {
        paramString = "file:" + paramString;
      } 
    } 
    int i = paramString.indexOf(' ');
    if (i >= 0) {
      StringBuilder stringBuilder = new StringBuilder(paramString.length());
      int j;
      for (j = 0; j < i; j++)
        stringBuilder.append(paramString.charAt(j)); 
      stringBuilder.append("%20");
      for (j = i + 1; j < paramString.length(); j++) {
        if (paramString.charAt(j) == ' ') {
          stringBuilder.append("%20");
        } else {
          stringBuilder.append(paramString.charAt(j));
        } 
      } 
      paramString = stringBuilder.toString();
    } 
    return paramString;
  }
  
  final void print() {}
  
  public void test() {
    this.fEntityStorage.addExternalEntity("entityUsecase1", null, "/space/home/stax/sun/6thJan2004/zephyr/data/test.txt", "/space/home/stax/sun/6thJan2004/zephyr/data/entity.xml");
    this.fEntityStorage.addInternalEntity("entityUsecase2", "<Test>value</Test>");
    this.fEntityStorage.addInternalEntity("entityUsecase3", "value3");
    this.fEntityStorage.addInternalEntity("text", "Hello World.");
    this.fEntityStorage.addInternalEntity("empty-element", "<foo/>");
    this.fEntityStorage.addInternalEntity("balanced-element", "<foo></foo>");
    this.fEntityStorage.addInternalEntity("balanced-element-with-text", "<foo>Hello, World</foo>");
    this.fEntityStorage.addInternalEntity("balanced-element-with-entity", "<foo>&text;</foo>");
    this.fEntityStorage.addInternalEntity("unbalanced-entity", "<foo>");
    this.fEntityStorage.addInternalEntity("recursive-entity", "<foo>&recursive-entity2;</foo>");
    this.fEntityStorage.addInternalEntity("recursive-entity2", "<bar>&recursive-entity3;</bar>");
    this.fEntityStorage.addInternalEntity("recursive-entity3", "<baz>&recursive-entity;</baz>");
    this.fEntityStorage.addInternalEntity("ch", "&#x00A9;");
    this.fEntityStorage.addInternalEntity("ch1", "&#84;");
    this.fEntityStorage.addInternalEntity("% ch2", "param");
  }
  
  static  {
    for (byte b = 0; b <= 31; b++) {
      gNeedEscaping[b] = true;
      gAfterEscaping1[b] = gHexChs[b >> 4];
      gAfterEscaping2[b] = gHexChs[b & 0xF];
    } 
    gNeedEscaping[127] = true;
    gAfterEscaping1[127] = '7';
    gAfterEscaping2[127] = 'F';
    for (char c : new char[] { 
        ' ', '<', '>', '#', '%', '"', '{', '}', '|', '\\', 
        '^', '~', '[', ']', '`' }) {
      gNeedEscaping[c] = true;
      gAfterEscaping1[c] = gHexChs[c >> '\004'];
      gAfterEscaping2[c] = gHexChs[c & 0xF];
    } 
  }
  
  private static class CharacterBuffer {
    private char[] ch;
    
    private boolean isExternal;
    
    public CharacterBuffer(boolean param1Boolean, int param1Int) {
      this.isExternal = param1Boolean;
      this.ch = new char[param1Int];
    }
  }
  
  private static class CharacterBufferPool {
    private static final int DEFAULT_POOL_SIZE = 3;
    
    private XMLEntityManager.CharacterBuffer[] fInternalBufferPool;
    
    private XMLEntityManager.CharacterBuffer[] fExternalBufferPool;
    
    private int fExternalBufferSize;
    
    private int fInternalBufferSize;
    
    private int poolSize;
    
    private int fInternalTop;
    
    private int fExternalTop;
    
    public CharacterBufferPool(int param1Int1, int param1Int2) { this(3, param1Int1, param1Int2); }
    
    public CharacterBufferPool(int param1Int1, int param1Int2, int param1Int3) {
      this.fExternalBufferSize = param1Int2;
      this.fInternalBufferSize = param1Int3;
      this.poolSize = param1Int1;
      init();
    }
    
    private void init() {
      this.fInternalBufferPool = new XMLEntityManager.CharacterBuffer[this.poolSize];
      this.fExternalBufferPool = new XMLEntityManager.CharacterBuffer[this.poolSize];
      this.fInternalTop = -1;
      this.fExternalTop = -1;
    }
    
    public XMLEntityManager.CharacterBuffer getBuffer(boolean param1Boolean) { return param1Boolean ? ((this.fExternalTop > -1) ? this.fExternalBufferPool[this.fExternalTop--] : new XMLEntityManager.CharacterBuffer(true, this.fExternalBufferSize)) : ((this.fInternalTop > -1) ? this.fInternalBufferPool[this.fInternalTop--] : new XMLEntityManager.CharacterBuffer(false, this.fInternalBufferSize)); }
    
    public void returnToPool(XMLEntityManager.CharacterBuffer param1CharacterBuffer) {
      if (param1CharacterBuffer.isExternal) {
        if (this.fExternalTop < this.fExternalBufferPool.length - 1)
          this.fExternalBufferPool[++this.fExternalTop] = param1CharacterBuffer; 
      } else if (this.fInternalTop < this.fInternalBufferPool.length - 1) {
        this.fInternalBufferPool[++this.fInternalTop] = param1CharacterBuffer;
      } 
    }
    
    public void setExternalBufferSize(int param1Int) {
      this.fExternalBufferSize = param1Int;
      this.fExternalBufferPool = new XMLEntityManager.CharacterBuffer[this.poolSize];
      this.fExternalTop = -1;
    }
  }
  
  protected final class RewindableInputStream extends InputStream {
    private InputStream fInputStream;
    
    private byte[] fData = new byte[64];
    
    private int fStartOffset;
    
    private int fEndOffset;
    
    private int fOffset;
    
    private int fLength;
    
    private int fMark;
    
    public RewindableInputStream(InputStream param1InputStream) {
      this.fInputStream = param1InputStream;
      this.fStartOffset = 0;
      this.fEndOffset = -1;
      this.fOffset = 0;
      this.fLength = 0;
      this.fMark = 0;
    }
    
    public void setStartOffset(int param1Int) { this.fStartOffset = param1Int; }
    
    public void rewind() { this.fOffset = this.fStartOffset; }
    
    public int read() {
      int i = 0;
      if (this.fOffset < this.fLength)
        return this.fData[this.fOffset++] & 0xFF; 
      if (this.fOffset == this.fEndOffset)
        return -1; 
      if (this.fOffset == this.fData.length) {
        byte[] arrayOfByte = new byte[this.fOffset << 1];
        System.arraycopy(this.fData, 0, arrayOfByte, 0, this.fOffset);
        this.fData = arrayOfByte;
      } 
      i = this.fInputStream.read();
      if (i == -1) {
        this.fEndOffset = this.fOffset;
        return -1;
      } 
      this.fData[this.fLength++] = (byte)i;
      this.fOffset++;
      return i & 0xFF;
    }
    
    public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      int i = this.fLength - this.fOffset;
      if (i == 0) {
        if (this.fOffset == this.fEndOffset)
          return -1; 
        if (this.this$0.fCurrentEntity.mayReadChunks || !this.this$0.fCurrentEntity.xmlDeclChunkRead) {
          if (!this.this$0.fCurrentEntity.xmlDeclChunkRead) {
            this.this$0.fCurrentEntity.xmlDeclChunkRead = true;
            param1Int2 = 28;
          } 
          return this.fInputStream.read(param1ArrayOfByte, param1Int1, param1Int2);
        } 
        int j = read();
        if (j == -1) {
          this.fEndOffset = this.fOffset;
          return -1;
        } 
        param1ArrayOfByte[param1Int1] = (byte)j;
        return 1;
      } 
      if (param1Int2 < i) {
        if (param1Int2 <= 0)
          return 0; 
      } else {
        param1Int2 = i;
      } 
      if (param1ArrayOfByte != null)
        System.arraycopy(this.fData, this.fOffset, param1ArrayOfByte, param1Int1, param1Int2); 
      this.fOffset += param1Int2;
      return param1Int2;
    }
    
    public long skip(long param1Long) throws IOException {
      if (param1Long <= 0L)
        return 0L; 
      int i = this.fLength - this.fOffset;
      if (i == 0)
        return (this.fOffset == this.fEndOffset) ? 0L : this.fInputStream.skip(param1Long); 
      if (param1Long <= i) {
        this.fOffset = (int)(this.fOffset + param1Long);
        return param1Long;
      } 
      this.fOffset += i;
      if (this.fOffset == this.fEndOffset)
        return i; 
      param1Long -= i;
      return this.fInputStream.skip(param1Long) + i;
    }
    
    public int available() {
      int i = this.fLength - this.fOffset;
      return (i == 0) ? ((this.fOffset == this.fEndOffset) ? -1 : (this.this$0.fCurrentEntity.mayReadChunks ? this.fInputStream.available() : 0)) : i;
    }
    
    public void mark(int param1Int) { this.fMark = this.fOffset; }
    
    public void reset() { this.fOffset = this.fMark; }
    
    public boolean markSupported() { return true; }
    
    public void close() {
      if (this.fInputStream != null) {
        this.fInputStream.close();
        this.fInputStream = null;
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\XMLEntityManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */