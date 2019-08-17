package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.impl.PropertyManager;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class XMLEntityStorage {
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  protected static final String WARN_ON_DUPLICATE_ENTITYDEF = "http://apache.org/xml/features/warn-on-duplicate-entitydef";
  
  protected boolean fWarnDuplicateEntityDef;
  
  protected Map<String, Entity> fEntities = new HashMap();
  
  protected Entity.ScannedEntity fCurrentEntity;
  
  private XMLEntityManager fEntityManager;
  
  protected XMLErrorReporter fErrorReporter;
  
  protected PropertyManager fPropertyManager;
  
  protected boolean fInExternalSubset = false;
  
  private static String gUserDir;
  
  private static String gEscapedUserDir;
  
  private static boolean[] gNeedEscaping = new boolean[128];
  
  private static char[] gAfterEscaping1 = new char[128];
  
  private static char[] gAfterEscaping2 = new char[128];
  
  private static char[] gHexChs = { 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      'A', 'B', 'C', 'D', 'E', 'F' };
  
  public XMLEntityStorage(PropertyManager paramPropertyManager) { this.fPropertyManager = paramPropertyManager; }
  
  public XMLEntityStorage(XMLEntityManager paramXMLEntityManager) { this.fEntityManager = paramXMLEntityManager; }
  
  public void reset(PropertyManager paramPropertyManager) {
    this.fErrorReporter = (XMLErrorReporter)paramPropertyManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    this.fEntities.clear();
    this.fCurrentEntity = null;
  }
  
  public void reset() {
    this.fEntities.clear();
    this.fCurrentEntity = null;
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager) throws XMLConfigurationException {
    this.fWarnDuplicateEntityDef = paramXMLComponentManager.getFeature("http://apache.org/xml/features/warn-on-duplicate-entitydef", false);
    this.fErrorReporter = (XMLErrorReporter)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    this.fEntities.clear();
    this.fCurrentEntity = null;
  }
  
  public Entity getEntity(String paramString) { return (Entity)this.fEntities.get(paramString); }
  
  public boolean hasEntities() { return (this.fEntities != null); }
  
  public int getEntitySize() { return this.fEntities.size(); }
  
  public Enumeration getEntityKeys() { return Collections.enumeration(this.fEntities.keySet()); }
  
  public void addInternalEntity(String paramString1, String paramString2) {
    if (!this.fEntities.containsKey(paramString1)) {
      Entity.InternalEntity internalEntity = new Entity.InternalEntity(paramString1, paramString2, this.fInExternalSubset);
      this.fEntities.put(paramString1, internalEntity);
    } else if (this.fWarnDuplicateEntityDef) {
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { paramString1 }, (short)0);
    } 
  }
  
  public void addExternalEntity(String paramString1, String paramString2, String paramString3, String paramString4) {
    if (!this.fEntities.containsKey(paramString1)) {
      if (paramString4 == null && this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null)
        paramString4 = this.fCurrentEntity.entityLocation.getExpandedSystemId(); 
      this.fCurrentEntity = this.fEntityManager.getCurrentEntity();
      Entity.ExternalEntity externalEntity = new Entity.ExternalEntity(paramString1, new XMLResourceIdentifierImpl(paramString2, paramString3, paramString4, expandSystemId(paramString3, paramString4)), null, this.fInExternalSubset);
      this.fEntities.put(paramString1, externalEntity);
    } else if (this.fWarnDuplicateEntityDef) {
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { paramString1 }, (short)0);
    } 
  }
  
  public boolean isExternalEntity(String paramString) {
    Entity entity = (Entity)this.fEntities.get(paramString);
    return (entity == null) ? false : entity.isExternal();
  }
  
  public boolean isEntityDeclInExternalSubset(String paramString) {
    Entity entity = (Entity)this.fEntities.get(paramString);
    return (entity == null) ? false : entity.isEntityDeclInExternalSubset();
  }
  
  public void addUnparsedEntity(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) {
    this.fCurrentEntity = this.fEntityManager.getCurrentEntity();
    if (!this.fEntities.containsKey(paramString1)) {
      Entity.ExternalEntity externalEntity = new Entity.ExternalEntity(paramString1, new XMLResourceIdentifierImpl(paramString2, paramString3, paramString4, null), paramString5, this.fInExternalSubset);
      this.fEntities.put(paramString1, externalEntity);
    } else if (this.fWarnDuplicateEntityDef) {
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { paramString1 }, (short)0);
    } 
  }
  
  public boolean isUnparsedEntity(String paramString) {
    Entity entity = (Entity)this.fEntities.get(paramString);
    return (entity == null) ? false : entity.isUnparsed();
  }
  
  public boolean isDeclaredEntity(String paramString) {
    Entity entity = (Entity)this.fEntities.get(paramString);
    return (entity != null);
  }
  
  public static String expandSystemId(String paramString) { return expandSystemId(paramString, null); }
  
  private static String getUserDir() {
    String str = "";
    try {
      str = SecuritySupport.getSystemProperty("user.dir");
    } catch (SecurityException securityException) {}
    if (str.length() == 0)
      return ""; 
    if (str.equals(gUserDir))
      return gEscapedUserDir; 
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
        return str;
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
    gEscapedUserDir = stringBuffer.toString();
    return gEscapedUserDir;
  }
  
  public static String expandSystemId(String paramString1, String paramString2) {
    if (paramString1 == null || paramString1.length() == 0)
      return paramString1; 
    try {
      new URI(paramString1);
      return paramString1;
    } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException malformedURIException) {
      String str = fixURI(paramString1);
      URI uRI1 = null;
      URI uRI2 = null;
      try {
        if (paramString2 == null || paramString2.length() == 0 || paramString2.equals(paramString1)) {
          String str1 = getUserDir();
          uRI1 = new URI("file", "", str1, null, null);
        } else {
          try {
            uRI1 = new URI(fixURI(paramString2));
          } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException malformedURIException1) {
            if (paramString2.indexOf(':') != -1) {
              uRI1 = new URI("file", "", fixURI(paramString2), null, null);
            } else {
              String str1 = getUserDir();
              str1 = str1 + fixURI(paramString2);
              uRI1 = new URI("file", "", str1, null, null);
            } 
          } 
        } 
        uRI2 = new URI(uRI1, str);
      } catch (Exception exception) {}
      return (uRI2 == null) ? paramString1 : uRI2.toString();
    } 
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
    return paramString;
  }
  
  public void startExternalSubset() { this.fInExternalSubset = true; }
  
  public void endExternalSubset() { this.fInExternalSubset = false; }
  
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
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\XMLEntityStorage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */