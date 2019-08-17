package com.sun.org.apache.xml.internal.resolver;

import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.resolver.helpers.FileURL;
import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;
import com.sun.org.apache.xml.internal.resolver.readers.CatalogReader;
import com.sun.org.apache.xml.internal.resolver.readers.SAXCatalogReader;
import com.sun.org.apache.xml.internal.resolver.readers.TR9401CatalogReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.xml.parsers.SAXParserFactory;
import jdk.xml.internal.JdkXmlUtils;

public class Catalog {
  public static final int BASE = CatalogEntry.addEntryType("BASE", 1);
  
  public static final int CATALOG = CatalogEntry.addEntryType("CATALOG", 1);
  
  public static final int DOCUMENT = CatalogEntry.addEntryType("DOCUMENT", 1);
  
  public static final int OVERRIDE = CatalogEntry.addEntryType("OVERRIDE", 1);
  
  public static final int SGMLDECL = CatalogEntry.addEntryType("SGMLDECL", 1);
  
  public static final int DELEGATE_PUBLIC = CatalogEntry.addEntryType("DELEGATE_PUBLIC", 2);
  
  public static final int DELEGATE_SYSTEM = CatalogEntry.addEntryType("DELEGATE_SYSTEM", 2);
  
  public static final int DELEGATE_URI = CatalogEntry.addEntryType("DELEGATE_URI", 2);
  
  public static final int DOCTYPE = CatalogEntry.addEntryType("DOCTYPE", 2);
  
  public static final int DTDDECL = CatalogEntry.addEntryType("DTDDECL", 2);
  
  public static final int ENTITY = CatalogEntry.addEntryType("ENTITY", 2);
  
  public static final int LINKTYPE = CatalogEntry.addEntryType("LINKTYPE", 2);
  
  public static final int NOTATION = CatalogEntry.addEntryType("NOTATION", 2);
  
  public static final int PUBLIC = CatalogEntry.addEntryType("PUBLIC", 2);
  
  public static final int SYSTEM = CatalogEntry.addEntryType("SYSTEM", 2);
  
  public static final int URI = CatalogEntry.addEntryType("URI", 2);
  
  public static final int REWRITE_SYSTEM = CatalogEntry.addEntryType("REWRITE_SYSTEM", 2);
  
  public static final int REWRITE_URI = CatalogEntry.addEntryType("REWRITE_URI", 2);
  
  public static final int SYSTEM_SUFFIX = CatalogEntry.addEntryType("SYSTEM_SUFFIX", 2);
  
  public static final int URI_SUFFIX = CatalogEntry.addEntryType("URI_SUFFIX", 2);
  
  protected URL base;
  
  protected URL catalogCwd;
  
  protected Vector catalogEntries = new Vector();
  
  protected boolean default_override = true;
  
  protected CatalogManager catalogManager = CatalogManager.getStaticManager();
  
  protected Vector catalogFiles = new Vector();
  
  protected Vector localCatalogFiles = new Vector();
  
  protected Vector catalogs = new Vector();
  
  protected Vector localDelegate = new Vector();
  
  protected Map<String, Integer> readerMap = new HashMap();
  
  protected Vector readerArr = new Vector();
  
  public Catalog() {}
  
  public Catalog(CatalogManager paramCatalogManager) { this.catalogManager = paramCatalogManager; }
  
  public CatalogManager getCatalogManager() { return this.catalogManager; }
  
  public void setCatalogManager(CatalogManager paramCatalogManager) { this.catalogManager = paramCatalogManager; }
  
  public void setupReaders() {
    SAXParserFactory sAXParserFactory = JdkXmlUtils.getSAXFactory(this.catalogManager.overrideDefaultParser());
    sAXParserFactory.setValidating(false);
    SAXCatalogReader sAXCatalogReader = new SAXCatalogReader(sAXParserFactory);
    sAXCatalogReader.setCatalogParser(null, "XMLCatalog", "com.sun.org.apache.xml.internal.resolver.readers.XCatalogReader");
    sAXCatalogReader.setCatalogParser("urn:oasis:names:tc:entity:xmlns:xml:catalog", "catalog", "com.sun.org.apache.xml.internal.resolver.readers.OASISXMLCatalogReader");
    addReader("application/xml", sAXCatalogReader);
    TR9401CatalogReader tR9401CatalogReader = new TR9401CatalogReader();
    addReader("text/plain", tR9401CatalogReader);
  }
  
  public void addReader(String paramString, CatalogReader paramCatalogReader) {
    if (this.readerMap.containsKey(paramString)) {
      Integer integer = (Integer)this.readerMap.get(paramString);
      this.readerArr.set(integer.intValue(), paramCatalogReader);
    } else {
      this.readerArr.add(paramCatalogReader);
      Integer integer = Integer.valueOf(this.readerArr.size() - 1);
      this.readerMap.put(paramString, integer);
    } 
  }
  
  protected void copyReaders(Catalog paramCatalog) {
    Vector vector = new Vector(this.readerMap.size());
    byte b;
    for (b = 0; b < this.readerMap.size(); b++)
      vector.add(null); 
    for (Map.Entry entry : this.readerMap.entrySet())
      vector.set(((Integer)entry.getValue()).intValue(), entry.getKey()); 
    for (b = 0; b < vector.size(); b++) {
      String str = (String)vector.get(b);
      Integer integer = (Integer)this.readerMap.get(str);
      paramCatalog.addReader(str, (CatalogReader)this.readerArr.get(integer.intValue()));
    } 
  }
  
  protected Catalog newCatalog() {
    String str = getClass().getName();
    try {
      Catalog catalog1 = (Catalog)Class.forName(str).newInstance();
      catalog1.setCatalogManager(this.catalogManager);
      copyReaders(catalog1);
      return catalog1;
    } catch (ClassNotFoundException classNotFoundException) {
      this.catalogManager.debug.message(1, "Class Not Found Exception: " + str);
    } catch (IllegalAccessException illegalAccessException) {
      this.catalogManager.debug.message(1, "Illegal Access Exception: " + str);
    } catch (InstantiationException instantiationException) {
      this.catalogManager.debug.message(1, "Instantiation Exception: " + str);
    } catch (ClassCastException classCastException) {
      this.catalogManager.debug.message(1, "Class Cast Exception: " + str);
    } catch (Exception exception) {
      this.catalogManager.debug.message(1, "Other Exception: " + str);
    } 
    Catalog catalog = new Catalog();
    catalog.setCatalogManager(this.catalogManager);
    copyReaders(catalog);
    return catalog;
  }
  
  public String getCurrentBase() { return this.base.toString(); }
  
  public String getDefaultOverride() { return this.default_override ? "yes" : "no"; }
  
  public void loadSystemCatalogs() {
    Vector vector = this.catalogManager.getCatalogFiles();
    if (vector != null)
      for (byte b = 0; b < vector.size(); b++)
        this.catalogFiles.addElement(vector.elementAt(b));  
    if (this.catalogFiles.size() > 0) {
      String str = (String)this.catalogFiles.lastElement();
      this.catalogFiles.removeElement(str);
      parseCatalog(str);
    } 
  }
  
  public void parseCatalog(String paramString) throws MalformedURLException, IOException {
    this.default_override = this.catalogManager.getPreferPublic();
    this.catalogManager.debug.message(4, "Parse catalog: " + paramString);
    this.catalogFiles.addElement(paramString);
    parsePendingCatalogs();
  }
  
  public void parseCatalog(String paramString, InputStream paramInputStream) throws IOException, CatalogException {
    this.default_override = this.catalogManager.getPreferPublic();
    this.catalogManager.debug.message(4, "Parse " + paramString + " catalog on input stream");
    CatalogReader catalogReader = null;
    if (this.readerMap.containsKey(paramString)) {
      int i = ((Integer)this.readerMap.get(paramString)).intValue();
      catalogReader = (CatalogReader)this.readerArr.get(i);
    } 
    if (catalogReader == null) {
      String str = "No CatalogReader for MIME type: " + paramString;
      this.catalogManager.debug.message(2, str);
      throw new CatalogException(6, str);
    } 
    catalogReader.readCatalog(this, paramInputStream);
    parsePendingCatalogs();
  }
  
  public void parseCatalog(URL paramURL) throws IOException {
    this.catalogCwd = paramURL;
    this.base = paramURL;
    this.default_override = this.catalogManager.getPreferPublic();
    this.catalogManager.debug.message(4, "Parse catalog: " + paramURL.toString());
    DataInputStream dataInputStream = null;
    boolean bool = false;
    for (byte b = 0; !bool && b < this.readerArr.size(); b++) {
      CatalogReader catalogReader = (CatalogReader)this.readerArr.get(b);
      try {
        dataInputStream = new DataInputStream(paramURL.openStream());
      } catch (FileNotFoundException fileNotFoundException) {
        break;
      } 
      try {
        catalogReader.readCatalog(this, dataInputStream);
        bool = true;
      } catch (CatalogException catalogException) {
        if (catalogException.getExceptionType() == 7)
          break; 
      } 
      try {
        dataInputStream.close();
      } catch (IOException iOException) {}
    } 
    if (bool)
      parsePendingCatalogs(); 
  }
  
  protected void parsePendingCatalogs() {
    if (!this.localCatalogFiles.isEmpty()) {
      Vector vector = new Vector();
      Enumeration enumeration = this.localCatalogFiles.elements();
      while (enumeration.hasMoreElements())
        vector.addElement(enumeration.nextElement()); 
      for (byte b = 0; b < this.catalogFiles.size(); b++) {
        String str = (String)this.catalogFiles.elementAt(b);
        vector.addElement(str);
      } 
      this.catalogFiles = vector;
      this.localCatalogFiles.clear();
    } 
    if (this.catalogFiles.isEmpty() && !this.localDelegate.isEmpty()) {
      Enumeration enumeration = this.localDelegate.elements();
      while (enumeration.hasMoreElements())
        this.catalogEntries.addElement(enumeration.nextElement()); 
      this.localDelegate.clear();
    } 
    while (!this.catalogFiles.isEmpty()) {
      String str = (String)this.catalogFiles.elementAt(0);
      try {
        this.catalogFiles.remove(0);
      } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {}
      if (this.catalogEntries.size() == 0 && this.catalogs.size() == 0) {
        try {
          parseCatalogFile(str);
        } catch (CatalogException catalogException) {
          System.out.println("FIXME: " + catalogException.toString());
        } 
      } else {
        this.catalogs.addElement(str);
      } 
      if (!this.localCatalogFiles.isEmpty()) {
        Vector vector = new Vector();
        Enumeration enumeration = this.localCatalogFiles.elements();
        while (enumeration.hasMoreElements())
          vector.addElement(enumeration.nextElement()); 
        for (byte b = 0; b < this.catalogFiles.size(); b++) {
          str = (String)this.catalogFiles.elementAt(b);
          vector.addElement(str);
        } 
        this.catalogFiles = vector;
        this.localCatalogFiles.clear();
      } 
      if (!this.localDelegate.isEmpty()) {
        Enumeration enumeration = this.localDelegate.elements();
        while (enumeration.hasMoreElements())
          this.catalogEntries.addElement(enumeration.nextElement()); 
        this.localDelegate.clear();
      } 
    } 
    this.catalogFiles.clear();
  }
  
  protected void parseCatalogFile(String paramString) throws MalformedURLException, IOException {
    try {
      this.catalogCwd = FileURL.makeURL("basename");
    } catch (MalformedURLException malformedURLException) {
      this.catalogManager.debug.message(1, "Malformed URL on cwd", "user.dir");
      this.catalogCwd = null;
    } 
    try {
      this.base = new URL(this.catalogCwd, fixSlashes(paramString));
    } catch (MalformedURLException malformedURLException) {
      try {
        this.base = new URL("file:" + fixSlashes(paramString));
      } catch (MalformedURLException malformedURLException1) {
        this.catalogManager.debug.message(1, "Malformed URL on catalog filename", fixSlashes(paramString));
        this.base = null;
      } 
    } 
    this.catalogManager.debug.message(2, "Loading catalog", paramString);
    this.catalogManager.debug.message(4, "Default BASE", this.base.toString());
    paramString = this.base.toString();
    DataInputStream dataInputStream = null;
    boolean bool1 = false;
    boolean bool2 = false;
    for (byte b = 0; !bool1 && b < this.readerArr.size(); b++) {
      CatalogReader catalogReader = (CatalogReader)this.readerArr.get(b);
      try {
        bool2 = false;
        dataInputStream = new DataInputStream(this.base.openStream());
      } catch (FileNotFoundException fileNotFoundException) {
        bool2 = true;
        break;
      } 
      try {
        catalogReader.readCatalog(this, dataInputStream);
        bool1 = true;
      } catch (CatalogException catalogException) {
        if (catalogException.getExceptionType() == 7)
          break; 
      } 
      try {
        dataInputStream.close();
      } catch (IOException iOException) {}
    } 
    if (!bool1)
      if (bool2) {
        this.catalogManager.debug.message(3, "Catalog does not exist", paramString);
      } else {
        this.catalogManager.debug.message(1, "Failed to parse catalog", paramString);
      }  
  }
  
  public void addEntry(CatalogEntry paramCatalogEntry) {
    int i = paramCatalogEntry.getEntryType();
    if (i == BASE) {
      String str = paramCatalogEntry.getEntryArg(0);
      URL uRL = null;
      if (this.base == null) {
        this.catalogManager.debug.message(5, "BASE CUR", "null");
      } else {
        this.catalogManager.debug.message(5, "BASE CUR", this.base.toString());
      } 
      this.catalogManager.debug.message(4, "BASE STR", str);
      try {
        str = fixSlashes(str);
        uRL = new URL(this.base, str);
      } catch (MalformedURLException malformedURLException) {
        try {
          uRL = new URL("file:" + str);
        } catch (MalformedURLException malformedURLException1) {
          this.catalogManager.debug.message(1, "Malformed URL on base", str);
          uRL = null;
        } 
      } 
      if (uRL != null)
        this.base = uRL; 
      this.catalogManager.debug.message(5, "BASE NEW", this.base.toString());
    } else if (i == CATALOG) {
      String str = makeAbsolute(paramCatalogEntry.getEntryArg(0));
      this.catalogManager.debug.message(4, "CATALOG", str);
      this.localCatalogFiles.addElement(str);
    } else if (i == PUBLIC) {
      String str1 = PublicId.normalize(paramCatalogEntry.getEntryArg(0));
      String str2 = makeAbsolute(normalizeURI(paramCatalogEntry.getEntryArg(1)));
      paramCatalogEntry.setEntryArg(0, str1);
      paramCatalogEntry.setEntryArg(1, str2);
      this.catalogManager.debug.message(4, "PUBLIC", str1, str2);
      this.catalogEntries.addElement(paramCatalogEntry);
    } else if (i == SYSTEM) {
      String str1 = normalizeURI(paramCatalogEntry.getEntryArg(0));
      String str2 = makeAbsolute(normalizeURI(paramCatalogEntry.getEntryArg(1)));
      paramCatalogEntry.setEntryArg(1, str2);
      this.catalogManager.debug.message(4, "SYSTEM", str1, str2);
      this.catalogEntries.addElement(paramCatalogEntry);
    } else if (i == URI) {
      String str1 = normalizeURI(paramCatalogEntry.getEntryArg(0));
      String str2 = makeAbsolute(normalizeURI(paramCatalogEntry.getEntryArg(1)));
      paramCatalogEntry.setEntryArg(1, str2);
      this.catalogManager.debug.message(4, "URI", str1, str2);
      this.catalogEntries.addElement(paramCatalogEntry);
    } else if (i == DOCUMENT) {
      String str = makeAbsolute(normalizeURI(paramCatalogEntry.getEntryArg(0)));
      paramCatalogEntry.setEntryArg(0, str);
      this.catalogManager.debug.message(4, "DOCUMENT", str);
      this.catalogEntries.addElement(paramCatalogEntry);
    } else if (i == OVERRIDE) {
      this.catalogManager.debug.message(4, "OVERRIDE", paramCatalogEntry.getEntryArg(0));
      this.catalogEntries.addElement(paramCatalogEntry);
    } else if (i == SGMLDECL) {
      String str = makeAbsolute(normalizeURI(paramCatalogEntry.getEntryArg(0)));
      paramCatalogEntry.setEntryArg(0, str);
      this.catalogManager.debug.message(4, "SGMLDECL", str);
      this.catalogEntries.addElement(paramCatalogEntry);
    } else if (i == DELEGATE_PUBLIC) {
      String str1 = PublicId.normalize(paramCatalogEntry.getEntryArg(0));
      String str2 = makeAbsolute(normalizeURI(paramCatalogEntry.getEntryArg(1)));
      paramCatalogEntry.setEntryArg(0, str1);
      paramCatalogEntry.setEntryArg(1, str2);
      this.catalogManager.debug.message(4, "DELEGATE_PUBLIC", str1, str2);
      addDelegate(paramCatalogEntry);
    } else if (i == DELEGATE_SYSTEM) {
      String str1 = normalizeURI(paramCatalogEntry.getEntryArg(0));
      String str2 = makeAbsolute(normalizeURI(paramCatalogEntry.getEntryArg(1)));
      paramCatalogEntry.setEntryArg(0, str1);
      paramCatalogEntry.setEntryArg(1, str2);
      this.catalogManager.debug.message(4, "DELEGATE_SYSTEM", str1, str2);
      addDelegate(paramCatalogEntry);
    } else if (i == DELEGATE_URI) {
      String str1 = normalizeURI(paramCatalogEntry.getEntryArg(0));
      String str2 = makeAbsolute(normalizeURI(paramCatalogEntry.getEntryArg(1)));
      paramCatalogEntry.setEntryArg(0, str1);
      paramCatalogEntry.setEntryArg(1, str2);
      this.catalogManager.debug.message(4, "DELEGATE_URI", str1, str2);
      addDelegate(paramCatalogEntry);
    } else if (i == REWRITE_SYSTEM) {
      String str1 = normalizeURI(paramCatalogEntry.getEntryArg(0));
      String str2 = makeAbsolute(normalizeURI(paramCatalogEntry.getEntryArg(1)));
      paramCatalogEntry.setEntryArg(0, str1);
      paramCatalogEntry.setEntryArg(1, str2);
      this.catalogManager.debug.message(4, "REWRITE_SYSTEM", str1, str2);
      this.catalogEntries.addElement(paramCatalogEntry);
    } else if (i == REWRITE_URI) {
      String str1 = normalizeURI(paramCatalogEntry.getEntryArg(0));
      String str2 = makeAbsolute(normalizeURI(paramCatalogEntry.getEntryArg(1)));
      paramCatalogEntry.setEntryArg(0, str1);
      paramCatalogEntry.setEntryArg(1, str2);
      this.catalogManager.debug.message(4, "REWRITE_URI", str1, str2);
      this.catalogEntries.addElement(paramCatalogEntry);
    } else if (i == SYSTEM_SUFFIX) {
      String str1 = normalizeURI(paramCatalogEntry.getEntryArg(0));
      String str2 = makeAbsolute(normalizeURI(paramCatalogEntry.getEntryArg(1)));
      paramCatalogEntry.setEntryArg(0, str1);
      paramCatalogEntry.setEntryArg(1, str2);
      this.catalogManager.debug.message(4, "SYSTEM_SUFFIX", str1, str2);
      this.catalogEntries.addElement(paramCatalogEntry);
    } else if (i == URI_SUFFIX) {
      String str1 = normalizeURI(paramCatalogEntry.getEntryArg(0));
      String str2 = makeAbsolute(normalizeURI(paramCatalogEntry.getEntryArg(1)));
      paramCatalogEntry.setEntryArg(0, str1);
      paramCatalogEntry.setEntryArg(1, str2);
      this.catalogManager.debug.message(4, "URI_SUFFIX", str1, str2);
      this.catalogEntries.addElement(paramCatalogEntry);
    } else if (i == DOCTYPE) {
      String str = makeAbsolute(normalizeURI(paramCatalogEntry.getEntryArg(1)));
      paramCatalogEntry.setEntryArg(1, str);
      this.catalogManager.debug.message(4, "DOCTYPE", paramCatalogEntry.getEntryArg(0), str);
      this.catalogEntries.addElement(paramCatalogEntry);
    } else if (i == DTDDECL) {
      String str1 = PublicId.normalize(paramCatalogEntry.getEntryArg(0));
      paramCatalogEntry.setEntryArg(0, str1);
      String str2 = makeAbsolute(normalizeURI(paramCatalogEntry.getEntryArg(1)));
      paramCatalogEntry.setEntryArg(1, str2);
      this.catalogManager.debug.message(4, "DTDDECL", str1, str2);
      this.catalogEntries.addElement(paramCatalogEntry);
    } else if (i == ENTITY) {
      String str = makeAbsolute(normalizeURI(paramCatalogEntry.getEntryArg(1)));
      paramCatalogEntry.setEntryArg(1, str);
      this.catalogManager.debug.message(4, "ENTITY", paramCatalogEntry.getEntryArg(0), str);
      this.catalogEntries.addElement(paramCatalogEntry);
    } else if (i == LINKTYPE) {
      String str = makeAbsolute(normalizeURI(paramCatalogEntry.getEntryArg(1)));
      paramCatalogEntry.setEntryArg(1, str);
      this.catalogManager.debug.message(4, "LINKTYPE", paramCatalogEntry.getEntryArg(0), str);
      this.catalogEntries.addElement(paramCatalogEntry);
    } else if (i == NOTATION) {
      String str = makeAbsolute(normalizeURI(paramCatalogEntry.getEntryArg(1)));
      paramCatalogEntry.setEntryArg(1, str);
      this.catalogManager.debug.message(4, "NOTATION", paramCatalogEntry.getEntryArg(0), str);
      this.catalogEntries.addElement(paramCatalogEntry);
    } else {
      this.catalogEntries.addElement(paramCatalogEntry);
    } 
  }
  
  public void unknownEntry(Vector paramVector) {
    if (paramVector != null && paramVector.size() > 0) {
      String str = (String)paramVector.elementAt(0);
      this.catalogManager.debug.message(2, "Unrecognized token parsing catalog", str);
    } 
  }
  
  public void parseAllCatalogs() {
    for (byte b = 0; b < this.catalogs.size(); b++) {
      Catalog catalog = null;
      try {
        catalog = (Catalog)this.catalogs.elementAt(b);
      } catch (ClassCastException classCastException) {
        String str = (String)this.catalogs.elementAt(b);
        catalog = newCatalog();
        catalog.parseCatalog(str);
        this.catalogs.setElementAt(catalog, b);
        catalog.parseAllCatalogs();
      } 
    } 
    Enumeration enumeration = this.catalogEntries.elements();
    while (enumeration.hasMoreElements()) {
      CatalogEntry catalogEntry = (CatalogEntry)enumeration.nextElement();
      if (catalogEntry.getEntryType() == DELEGATE_PUBLIC || catalogEntry.getEntryType() == DELEGATE_SYSTEM || catalogEntry.getEntryType() == DELEGATE_URI) {
        Catalog catalog = newCatalog();
        catalog.parseCatalog(catalogEntry.getEntryArg(1));
      } 
    } 
  }
  
  public String resolveDoctype(String paramString1, String paramString2, String paramString3) throws MalformedURLException, IOException {
    String str = null;
    this.catalogManager.debug.message(3, "resolveDoctype(" + paramString1 + "," + paramString2 + "," + paramString3 + ")");
    paramString3 = normalizeURI(paramString3);
    if (paramString2 != null && paramString2.startsWith("urn:publicid:"))
      paramString2 = PublicId.decodeURN(paramString2); 
    if (paramString3 != null && paramString3.startsWith("urn:publicid:")) {
      paramString3 = PublicId.decodeURN(paramString3);
      if (paramString2 != null && !paramString2.equals(paramString3)) {
        this.catalogManager.debug.message(1, "urn:publicid: system identifier differs from public identifier; using public identifier");
        paramString3 = null;
      } else {
        paramString2 = paramString3;
        paramString3 = null;
      } 
    } 
    if (paramString3 != null) {
      str = resolveLocalSystem(paramString3);
      if (str != null)
        return str; 
    } 
    if (paramString2 != null) {
      str = resolveLocalPublic(DOCTYPE, paramString1, paramString2, paramString3);
      if (str != null)
        return str; 
    } 
    boolean bool = this.default_override;
    Enumeration enumeration = this.catalogEntries.elements();
    while (enumeration.hasMoreElements()) {
      CatalogEntry catalogEntry = (CatalogEntry)enumeration.nextElement();
      if (catalogEntry.getEntryType() == OVERRIDE) {
        bool = catalogEntry.getEntryArg(0).equalsIgnoreCase("YES");
        continue;
      } 
      if (catalogEntry.getEntryType() == DOCTYPE && catalogEntry.getEntryArg(0).equals(paramString1) && (bool || paramString3 == null))
        return catalogEntry.getEntryArg(1); 
    } 
    return resolveSubordinateCatalogs(DOCTYPE, paramString1, paramString2, paramString3);
  }
  
  public String resolveDocument() {
    this.catalogManager.debug.message(3, "resolveDocument");
    Enumeration enumeration = this.catalogEntries.elements();
    while (enumeration.hasMoreElements()) {
      CatalogEntry catalogEntry = (CatalogEntry)enumeration.nextElement();
      if (catalogEntry.getEntryType() == DOCUMENT)
        return catalogEntry.getEntryArg(0); 
    } 
    return resolveSubordinateCatalogs(DOCUMENT, null, null, null);
  }
  
  public String resolveEntity(String paramString1, String paramString2, String paramString3) throws MalformedURLException, IOException {
    String str = null;
    this.catalogManager.debug.message(3, "resolveEntity(" + paramString1 + "," + paramString2 + "," + paramString3 + ")");
    paramString3 = normalizeURI(paramString3);
    if (paramString2 != null && paramString2.startsWith("urn:publicid:"))
      paramString2 = PublicId.decodeURN(paramString2); 
    if (paramString3 != null && paramString3.startsWith("urn:publicid:")) {
      paramString3 = PublicId.decodeURN(paramString3);
      if (paramString2 != null && !paramString2.equals(paramString3)) {
        this.catalogManager.debug.message(1, "urn:publicid: system identifier differs from public identifier; using public identifier");
        paramString3 = null;
      } else {
        paramString2 = paramString3;
        paramString3 = null;
      } 
    } 
    if (paramString3 != null) {
      str = resolveLocalSystem(paramString3);
      if (str != null)
        return str; 
    } 
    if (paramString2 != null) {
      str = resolveLocalPublic(ENTITY, paramString1, paramString2, paramString3);
      if (str != null)
        return str; 
    } 
    boolean bool = this.default_override;
    Enumeration enumeration = this.catalogEntries.elements();
    while (enumeration.hasMoreElements()) {
      CatalogEntry catalogEntry = (CatalogEntry)enumeration.nextElement();
      if (catalogEntry.getEntryType() == OVERRIDE) {
        bool = catalogEntry.getEntryArg(0).equalsIgnoreCase("YES");
        continue;
      } 
      if (catalogEntry.getEntryType() == ENTITY && catalogEntry.getEntryArg(0).equals(paramString1) && (bool || paramString3 == null))
        return catalogEntry.getEntryArg(1); 
    } 
    return resolveSubordinateCatalogs(ENTITY, paramString1, paramString2, paramString3);
  }
  
  public String resolveNotation(String paramString1, String paramString2, String paramString3) throws MalformedURLException, IOException {
    String str = null;
    this.catalogManager.debug.message(3, "resolveNotation(" + paramString1 + "," + paramString2 + "," + paramString3 + ")");
    paramString3 = normalizeURI(paramString3);
    if (paramString2 != null && paramString2.startsWith("urn:publicid:"))
      paramString2 = PublicId.decodeURN(paramString2); 
    if (paramString3 != null && paramString3.startsWith("urn:publicid:")) {
      paramString3 = PublicId.decodeURN(paramString3);
      if (paramString2 != null && !paramString2.equals(paramString3)) {
        this.catalogManager.debug.message(1, "urn:publicid: system identifier differs from public identifier; using public identifier");
        paramString3 = null;
      } else {
        paramString2 = paramString3;
        paramString3 = null;
      } 
    } 
    if (paramString3 != null) {
      str = resolveLocalSystem(paramString3);
      if (str != null)
        return str; 
    } 
    if (paramString2 != null) {
      str = resolveLocalPublic(NOTATION, paramString1, paramString2, paramString3);
      if (str != null)
        return str; 
    } 
    boolean bool = this.default_override;
    Enumeration enumeration = this.catalogEntries.elements();
    while (enumeration.hasMoreElements()) {
      CatalogEntry catalogEntry = (CatalogEntry)enumeration.nextElement();
      if (catalogEntry.getEntryType() == OVERRIDE) {
        bool = catalogEntry.getEntryArg(0).equalsIgnoreCase("YES");
        continue;
      } 
      if (catalogEntry.getEntryType() == NOTATION && catalogEntry.getEntryArg(0).equals(paramString1) && (bool || paramString3 == null))
        return catalogEntry.getEntryArg(1); 
    } 
    return resolveSubordinateCatalogs(NOTATION, paramString1, paramString2, paramString3);
  }
  
  public String resolvePublic(String paramString1, String paramString2) throws MalformedURLException, IOException {
    this.catalogManager.debug.message(3, "resolvePublic(" + paramString1 + "," + paramString2 + ")");
    paramString2 = normalizeURI(paramString2);
    if (paramString1 != null && paramString1.startsWith("urn:publicid:"))
      paramString1 = PublicId.decodeURN(paramString1); 
    if (paramString2 != null && paramString2.startsWith("urn:publicid:")) {
      paramString2 = PublicId.decodeURN(paramString2);
      if (paramString1 != null && !paramString1.equals(paramString2)) {
        this.catalogManager.debug.message(1, "urn:publicid: system identifier differs from public identifier; using public identifier");
        paramString2 = null;
      } else {
        paramString1 = paramString2;
        paramString2 = null;
      } 
    } 
    if (paramString2 != null) {
      String str1 = resolveLocalSystem(paramString2);
      if (str1 != null)
        return str1; 
    } 
    String str = resolveLocalPublic(PUBLIC, null, paramString1, paramString2);
    return (str != null) ? str : resolveSubordinateCatalogs(PUBLIC, null, paramString1, paramString2);
  }
  
  protected String resolveLocalPublic(int paramInt, String paramString1, String paramString2, String paramString3) throws MalformedURLException, IOException {
    paramString2 = PublicId.normalize(paramString2);
    if (paramString3 != null) {
      String str = resolveLocalSystem(paramString3);
      if (str != null)
        return str; 
    } 
    boolean bool = this.default_override;
    Enumeration enumeration = this.catalogEntries.elements();
    while (enumeration.hasMoreElements()) {
      CatalogEntry catalogEntry = (CatalogEntry)enumeration.nextElement();
      if (catalogEntry.getEntryType() == OVERRIDE) {
        bool = catalogEntry.getEntryArg(0).equalsIgnoreCase("YES");
        continue;
      } 
      if (catalogEntry.getEntryType() == PUBLIC && catalogEntry.getEntryArg(0).equals(paramString2) && (bool || paramString3 == null))
        return catalogEntry.getEntryArg(1); 
    } 
    bool = this.default_override;
    enumeration = this.catalogEntries.elements();
    Vector vector = new Vector();
    while (enumeration.hasMoreElements()) {
      CatalogEntry catalogEntry = (CatalogEntry)enumeration.nextElement();
      if (catalogEntry.getEntryType() == OVERRIDE) {
        bool = catalogEntry.getEntryArg(0).equalsIgnoreCase("YES");
        continue;
      } 
      if (catalogEntry.getEntryType() == DELEGATE_PUBLIC && (bool || paramString3 == null)) {
        String str = catalogEntry.getEntryArg(0);
        if (str.length() <= paramString2.length() && str.equals(paramString2.substring(0, str.length())))
          vector.addElement(catalogEntry.getEntryArg(1)); 
      } 
    } 
    if (vector.size() > 0) {
      Enumeration enumeration1 = vector.elements();
      if (this.catalogManager.debug.getDebug() > 1) {
        this.catalogManager.debug.message(2, "Switching to delegated catalog(s):");
        while (enumeration1.hasMoreElements()) {
          String str = (String)enumeration1.nextElement();
          this.catalogManager.debug.message(2, "\t" + str);
        } 
      } 
      Catalog catalog = newCatalog();
      enumeration1 = vector.elements();
      while (enumeration1.hasMoreElements()) {
        String str = (String)enumeration1.nextElement();
        catalog.parseCatalog(str);
      } 
      return catalog.resolvePublic(paramString2, null);
    } 
    return null;
  }
  
  public String resolveSystem(String paramString) throws MalformedURLException, IOException {
    this.catalogManager.debug.message(3, "resolveSystem(" + paramString + ")");
    paramString = normalizeURI(paramString);
    if (paramString != null && paramString.startsWith("urn:publicid:")) {
      paramString = PublicId.decodeURN(paramString);
      return resolvePublic(paramString, null);
    } 
    if (paramString != null) {
      String str = resolveLocalSystem(paramString);
      if (str != null)
        return str; 
    } 
    return resolveSubordinateCatalogs(SYSTEM, null, null, paramString);
  }
  
  protected String resolveLocalSystem(String paramString) throws MalformedURLException, IOException {
    String str1 = SecuritySupport.getSystemProperty("os.name");
    boolean bool = (str1.indexOf("Windows") >= 0) ? 1 : 0;
    Enumeration enumeration = this.catalogEntries.elements();
    while (enumeration.hasMoreElements()) {
      CatalogEntry catalogEntry = (CatalogEntry)enumeration.nextElement();
      if (catalogEntry.getEntryType() == SYSTEM && (catalogEntry.getEntryArg(0).equals(paramString) || (bool && catalogEntry.getEntryArg(0).equalsIgnoreCase(paramString))))
        return catalogEntry.getEntryArg(1); 
    } 
    enumeration = this.catalogEntries.elements();
    String str2 = null;
    String str3 = null;
    while (enumeration.hasMoreElements()) {
      CatalogEntry catalogEntry = (CatalogEntry)enumeration.nextElement();
      if (catalogEntry.getEntryType() == REWRITE_SYSTEM) {
        String str = catalogEntry.getEntryArg(0);
        if (str.length() <= paramString.length() && str.equals(paramString.substring(0, str.length())) && (str2 == null || str.length() > str2.length())) {
          str2 = str;
          str3 = catalogEntry.getEntryArg(1);
        } 
      } 
    } 
    if (str3 != null)
      return str3 + paramString.substring(str2.length()); 
    enumeration = this.catalogEntries.elements();
    String str4 = null;
    String str5 = null;
    while (enumeration.hasMoreElements()) {
      CatalogEntry catalogEntry = (CatalogEntry)enumeration.nextElement();
      if (catalogEntry.getEntryType() == SYSTEM_SUFFIX) {
        String str = catalogEntry.getEntryArg(0);
        if (str.length() <= paramString.length() && paramString.endsWith(str) && (str4 == null || str.length() > str4.length())) {
          str4 = str;
          str5 = catalogEntry.getEntryArg(1);
        } 
      } 
    } 
    if (str5 != null)
      return str5; 
    enumeration = this.catalogEntries.elements();
    Vector vector = new Vector();
    while (enumeration.hasMoreElements()) {
      CatalogEntry catalogEntry = (CatalogEntry)enumeration.nextElement();
      if (catalogEntry.getEntryType() == DELEGATE_SYSTEM) {
        String str = catalogEntry.getEntryArg(0);
        if (str.length() <= paramString.length() && str.equals(paramString.substring(0, str.length())))
          vector.addElement(catalogEntry.getEntryArg(1)); 
      } 
    } 
    if (vector.size() > 0) {
      Enumeration enumeration1 = vector.elements();
      if (this.catalogManager.debug.getDebug() > 1) {
        this.catalogManager.debug.message(2, "Switching to delegated catalog(s):");
        while (enumeration1.hasMoreElements()) {
          String str = (String)enumeration1.nextElement();
          this.catalogManager.debug.message(2, "\t" + str);
        } 
      } 
      Catalog catalog = newCatalog();
      enumeration1 = vector.elements();
      while (enumeration1.hasMoreElements()) {
        String str = (String)enumeration1.nextElement();
        catalog.parseCatalog(str);
      } 
      return catalog.resolveSystem(paramString);
    } 
    return null;
  }
  
  public String resolveURI(String paramString) throws MalformedURLException, IOException {
    this.catalogManager.debug.message(3, "resolveURI(" + paramString + ")");
    paramString = normalizeURI(paramString);
    if (paramString != null && paramString.startsWith("urn:publicid:")) {
      paramString = PublicId.decodeURN(paramString);
      return resolvePublic(paramString, null);
    } 
    if (paramString != null) {
      String str = resolveLocalURI(paramString);
      if (str != null)
        return str; 
    } 
    return resolveSubordinateCatalogs(URI, null, null, paramString);
  }
  
  protected String resolveLocalURI(String paramString) throws MalformedURLException, IOException {
    Enumeration enumeration = this.catalogEntries.elements();
    while (enumeration.hasMoreElements()) {
      CatalogEntry catalogEntry = (CatalogEntry)enumeration.nextElement();
      if (catalogEntry.getEntryType() == URI && catalogEntry.getEntryArg(0).equals(paramString))
        return catalogEntry.getEntryArg(1); 
    } 
    enumeration = this.catalogEntries.elements();
    String str1 = null;
    String str2 = null;
    while (enumeration.hasMoreElements()) {
      CatalogEntry catalogEntry = (CatalogEntry)enumeration.nextElement();
      if (catalogEntry.getEntryType() == REWRITE_URI) {
        String str = catalogEntry.getEntryArg(0);
        if (str.length() <= paramString.length() && str.equals(paramString.substring(0, str.length())) && (str1 == null || str.length() > str1.length())) {
          str1 = str;
          str2 = catalogEntry.getEntryArg(1);
        } 
      } 
    } 
    if (str2 != null)
      return str2 + paramString.substring(str1.length()); 
    enumeration = this.catalogEntries.elements();
    String str3 = null;
    String str4 = null;
    while (enumeration.hasMoreElements()) {
      CatalogEntry catalogEntry = (CatalogEntry)enumeration.nextElement();
      if (catalogEntry.getEntryType() == URI_SUFFIX) {
        String str = catalogEntry.getEntryArg(0);
        if (str.length() <= paramString.length() && paramString.endsWith(str) && (str3 == null || str.length() > str3.length())) {
          str3 = str;
          str4 = catalogEntry.getEntryArg(1);
        } 
      } 
    } 
    if (str4 != null)
      return str4; 
    enumeration = this.catalogEntries.elements();
    Vector vector = new Vector();
    while (enumeration.hasMoreElements()) {
      CatalogEntry catalogEntry = (CatalogEntry)enumeration.nextElement();
      if (catalogEntry.getEntryType() == DELEGATE_URI) {
        String str = catalogEntry.getEntryArg(0);
        if (str.length() <= paramString.length() && str.equals(paramString.substring(0, str.length())))
          vector.addElement(catalogEntry.getEntryArg(1)); 
      } 
    } 
    if (vector.size() > 0) {
      Enumeration enumeration1 = vector.elements();
      if (this.catalogManager.debug.getDebug() > 1) {
        this.catalogManager.debug.message(2, "Switching to delegated catalog(s):");
        while (enumeration1.hasMoreElements()) {
          String str = (String)enumeration1.nextElement();
          this.catalogManager.debug.message(2, "\t" + str);
        } 
      } 
      Catalog catalog = newCatalog();
      enumeration1 = vector.elements();
      while (enumeration1.hasMoreElements()) {
        String str = (String)enumeration1.nextElement();
        catalog.parseCatalog(str);
      } 
      return catalog.resolveURI(paramString);
    } 
    return null;
  }
  
  protected String resolveSubordinateCatalogs(int paramInt, String paramString1, String paramString2, String paramString3) throws MalformedURLException, IOException {
    for (byte b = 0; b < this.catalogs.size(); b++) {
      Catalog catalog = null;
      try {
        catalog = (Catalog)this.catalogs.elementAt(b);
      } catch (ClassCastException classCastException) {
        String str1 = (String)this.catalogs.elementAt(b);
        catalog = newCatalog();
        try {
          catalog.parseCatalog(str1);
        } catch (MalformedURLException malformedURLException) {
          this.catalogManager.debug.message(1, "Malformed Catalog URL", str1);
        } catch (FileNotFoundException fileNotFoundException) {
          this.catalogManager.debug.message(1, "Failed to load catalog, file not found", str1);
        } catch (IOException iOException) {
          this.catalogManager.debug.message(1, "Failed to load catalog, I/O error", str1);
        } 
        this.catalogs.setElementAt(catalog, b);
      } 
      String str = null;
      if (paramInt == DOCTYPE) {
        str = catalog.resolveDoctype(paramString1, paramString2, paramString3);
      } else if (paramInt == DOCUMENT) {
        str = catalog.resolveDocument();
      } else if (paramInt == ENTITY) {
        str = catalog.resolveEntity(paramString1, paramString2, paramString3);
      } else if (paramInt == NOTATION) {
        str = catalog.resolveNotation(paramString1, paramString2, paramString3);
      } else if (paramInt == PUBLIC) {
        str = catalog.resolvePublic(paramString2, paramString3);
      } else if (paramInt == SYSTEM) {
        str = catalog.resolveSystem(paramString3);
      } else if (paramInt == URI) {
        str = catalog.resolveURI(paramString3);
      } 
      if (str != null)
        return str; 
    } 
    return null;
  }
  
  protected String fixSlashes(String paramString) throws MalformedURLException, IOException { return paramString.replace('\\', '/'); }
  
  protected String makeAbsolute(String paramString) throws MalformedURLException, IOException {
    URL uRL = null;
    paramString = fixSlashes(paramString);
    try {
      uRL = new URL(this.base, paramString);
    } catch (MalformedURLException malformedURLException) {
      this.catalogManager.debug.message(1, "Malformed URL on system identifier", paramString);
    } 
    return (uRL != null) ? uRL.toString() : paramString;
  }
  
  protected String normalizeURI(String paramString) throws MalformedURLException, IOException {
    byte[] arrayOfByte;
    if (paramString == null)
      return null; 
    try {
      arrayOfByte = paramString.getBytes("UTF-8");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      this.catalogManager.debug.message(1, "UTF-8 is an unsupported encoding!?");
      return paramString;
    } 
    StringBuilder stringBuilder = new StringBuilder(arrayOfByte.length);
    for (byte b = 0; b < arrayOfByte.length; b++) {
      byte b1 = arrayOfByte[b] & 0xFF;
      if (b1 <= 32 || b1 > Byte.MAX_VALUE || b1 == 34 || b1 == 60 || b1 == 62 || b1 == 92 || b1 == 94 || b1 == 96 || b1 == 123 || b1 == 124 || b1 == 125 || b1 == Byte.MAX_VALUE) {
        stringBuilder.append(encodedByte(b1));
      } else {
        stringBuilder.append((char)arrayOfByte[b]);
      } 
    } 
    return stringBuilder.toString();
  }
  
  protected String encodedByte(int paramInt) {
    String str = Integer.toHexString(paramInt).toUpperCase();
    return (str.length() < 2) ? ("%0" + str) : ("%" + str);
  }
  
  protected void addDelegate(CatalogEntry paramCatalogEntry) {
    byte b = 0;
    String str = paramCatalogEntry.getEntryArg(0);
    Enumeration enumeration = this.localDelegate.elements();
    while (enumeration.hasMoreElements()) {
      CatalogEntry catalogEntry = (CatalogEntry)enumeration.nextElement();
      String str1 = catalogEntry.getEntryArg(0);
      if (str1.equals(str))
        return; 
      if (str1.length() > str.length())
        b++; 
      if (str1.length() < str.length())
        break; 
    } 
    if (this.localDelegate.size() == 0) {
      this.localDelegate.addElement(paramCatalogEntry);
    } else {
      this.localDelegate.insertElementAt(paramCatalogEntry, b);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\resolver\Catalog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */