package com.sun.org.apache.xml.internal.resolver;

import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.resolver.readers.SAXCatalogReader;
import com.sun.org.apache.xml.internal.resolver.readers.TR9401CatalogReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Vector;
import javax.xml.parsers.SAXParserFactory;
import jdk.xml.internal.JdkXmlUtils;

public class Resolver extends Catalog {
  public static final int URISUFFIX = CatalogEntry.addEntryType("URISUFFIX", 2);
  
  public static final int SYSTEMSUFFIX = CatalogEntry.addEntryType("SYSTEMSUFFIX", 2);
  
  public static final int RESOLVER = CatalogEntry.addEntryType("RESOLVER", 1);
  
  public static final int SYSTEMREVERSE = CatalogEntry.addEntryType("SYSTEMREVERSE", 1);
  
  public void setupReaders() {
    SAXParserFactory sAXParserFactory = JdkXmlUtils.getSAXFactory(this.catalogManager.overrideDefaultParser());
    sAXParserFactory.setValidating(false);
    SAXCatalogReader sAXCatalogReader = new SAXCatalogReader(sAXParserFactory);
    sAXCatalogReader.setCatalogParser(null, "XMLCatalog", "com.sun.org.apache.xml.internal.resolver.readers.XCatalogReader");
    sAXCatalogReader.setCatalogParser("urn:oasis:names:tc:entity:xmlns:xml:catalog", "catalog", "com.sun.org.apache.xml.internal.resolver.readers.ExtendedXMLCatalogReader");
    addReader("application/xml", sAXCatalogReader);
    TR9401CatalogReader tR9401CatalogReader = new TR9401CatalogReader();
    addReader("text/plain", tR9401CatalogReader);
  }
  
  public void addEntry(CatalogEntry paramCatalogEntry) {
    int i = paramCatalogEntry.getEntryType();
    if (i == URISUFFIX) {
      String str1 = normalizeURI(paramCatalogEntry.getEntryArg(0));
      String str2 = makeAbsolute(normalizeURI(paramCatalogEntry.getEntryArg(1)));
      paramCatalogEntry.setEntryArg(1, str2);
      this.catalogManager.debug.message(4, "URISUFFIX", str1, str2);
    } else if (i == SYSTEMSUFFIX) {
      String str1 = normalizeURI(paramCatalogEntry.getEntryArg(0));
      String str2 = makeAbsolute(normalizeURI(paramCatalogEntry.getEntryArg(1)));
      paramCatalogEntry.setEntryArg(1, str2);
      this.catalogManager.debug.message(4, "SYSTEMSUFFIX", str1, str2);
    } 
    super.addEntry(paramCatalogEntry);
  }
  
  public String resolveURI(String paramString) throws MalformedURLException, IOException {
    String str = super.resolveURI(paramString);
    if (str != null)
      return str; 
    Enumeration enumeration = this.catalogEntries.elements();
    while (enumeration.hasMoreElements()) {
      CatalogEntry catalogEntry = (CatalogEntry)enumeration.nextElement();
      if (catalogEntry.getEntryType() == RESOLVER) {
        str = resolveExternalSystem(paramString, catalogEntry.getEntryArg(0));
        if (str != null)
          return str; 
        continue;
      } 
      if (catalogEntry.getEntryType() == URISUFFIX) {
        String str1 = catalogEntry.getEntryArg(0);
        String str2 = catalogEntry.getEntryArg(1);
        if (str1.length() <= paramString.length() && paramString.substring(paramString.length() - str1.length()).equals(str1))
          return str2; 
      } 
    } 
    return resolveSubordinateCatalogs(Catalog.URI, null, null, paramString);
  }
  
  public String resolveSystem(String paramString) throws MalformedURLException, IOException {
    String str = super.resolveSystem(paramString);
    if (str != null)
      return str; 
    Enumeration enumeration = this.catalogEntries.elements();
    while (enumeration.hasMoreElements()) {
      CatalogEntry catalogEntry = (CatalogEntry)enumeration.nextElement();
      if (catalogEntry.getEntryType() == RESOLVER) {
        str = resolveExternalSystem(paramString, catalogEntry.getEntryArg(0));
        if (str != null)
          return str; 
        continue;
      } 
      if (catalogEntry.getEntryType() == SYSTEMSUFFIX) {
        String str1 = catalogEntry.getEntryArg(0);
        String str2 = catalogEntry.getEntryArg(1);
        if (str1.length() <= paramString.length() && paramString.substring(paramString.length() - str1.length()).equals(str1))
          return str2; 
      } 
    } 
    return resolveSubordinateCatalogs(Catalog.SYSTEM, null, null, paramString);
  }
  
  public String resolvePublic(String paramString1, String paramString2) throws MalformedURLException, IOException {
    String str = super.resolvePublic(paramString1, paramString2);
    if (str != null)
      return str; 
    Enumeration enumeration = this.catalogEntries.elements();
    while (enumeration.hasMoreElements()) {
      CatalogEntry catalogEntry = (CatalogEntry)enumeration.nextElement();
      if (catalogEntry.getEntryType() == RESOLVER) {
        if (paramString2 != null) {
          str = resolveExternalSystem(paramString2, catalogEntry.getEntryArg(0));
          if (str != null)
            return str; 
        } 
        str = resolveExternalPublic(paramString1, catalogEntry.getEntryArg(0));
        if (str != null)
          return str; 
      } 
    } 
    return resolveSubordinateCatalogs(Catalog.PUBLIC, null, paramString1, paramString2);
  }
  
  protected String resolveExternalSystem(String paramString1, String paramString2) throws MalformedURLException, IOException {
    Resolver resolver = queryResolver(paramString2, "i2l", paramString1, null);
    return (resolver != null) ? resolver.resolveSystem(paramString1) : null;
  }
  
  protected String resolveExternalPublic(String paramString1, String paramString2) throws MalformedURLException, IOException {
    Resolver resolver = queryResolver(paramString2, "fpi2l", paramString1, null);
    return (resolver != null) ? resolver.resolvePublic(paramString1, null) : null;
  }
  
  protected Resolver queryResolver(String paramString1, String paramString2, String paramString3, String paramString4) {
    Object object1 = null;
    String str = paramString1 + "?command=" + paramString2 + "&format=tr9401&uri=" + paramString3 + "&uri2=" + paramString4;
    Object object2 = null;
    try {
      URL uRL = new URL(str);
      URLConnection uRLConnection = uRL.openConnection();
      uRLConnection.setUseCaches(false);
      Resolver resolver = (Resolver)newCatalog();
      String str1 = uRLConnection.getContentType();
      if (str1.indexOf(";") > 0)
        str1 = str1.substring(0, str1.indexOf(";")); 
      resolver.parseCatalog(str1, uRLConnection.getInputStream());
      return resolver;
    } catch (CatalogException catalogException) {
      if (catalogException.getExceptionType() == 6) {
        this.catalogManager.debug.message(1, "Unparseable catalog: " + str);
      } else if (catalogException.getExceptionType() == 5) {
        this.catalogManager.debug.message(1, "Unknown catalog format: " + str);
      } 
      return null;
    } catch (MalformedURLException malformedURLException) {
      this.catalogManager.debug.message(1, "Malformed resolver URL: " + str);
      return null;
    } catch (IOException iOException) {
      this.catalogManager.debug.message(1, "I/O Exception opening resolver: " + str);
      return null;
    } 
  }
  
  private Vector appendVector(Vector paramVector1, Vector paramVector2) {
    if (paramVector2 != null)
      for (byte b = 0; b < paramVector2.size(); b++)
        paramVector1.addElement(paramVector2.elementAt(b));  
    return paramVector1;
  }
  
  public Vector resolveAllSystemReverse(String paramString) throws MalformedURLException, IOException {
    Vector vector1 = new Vector();
    if (paramString != null) {
      Vector vector = resolveLocalSystemReverse(paramString);
      vector1 = appendVector(vector1, vector);
    } 
    Vector vector2 = resolveAllSubordinateCatalogs(SYSTEMREVERSE, null, null, paramString);
    return appendVector(vector1, vector2);
  }
  
  public String resolveSystemReverse(String paramString) throws MalformedURLException, IOException {
    Vector vector = resolveAllSystemReverse(paramString);
    return (vector != null && vector.size() > 0) ? (String)vector.elementAt(0) : null;
  }
  
  public Vector resolveAllSystem(String paramString) throws MalformedURLException, IOException {
    Vector vector1 = new Vector();
    if (paramString != null) {
      Vector vector = resolveAllLocalSystem(paramString);
      vector1 = appendVector(vector1, vector);
    } 
    Vector vector2 = resolveAllSubordinateCatalogs(SYSTEM, null, null, paramString);
    vector1 = appendVector(vector1, vector2);
    return (vector1.size() > 0) ? vector1 : null;
  }
  
  private Vector resolveAllLocalSystem(String paramString) throws MalformedURLException, IOException {
    Vector vector = new Vector();
    String str = SecuritySupport.getSystemProperty("os.name");
    boolean bool = (str.indexOf("Windows") >= 0) ? 1 : 0;
    Enumeration enumeration = this.catalogEntries.elements();
    while (enumeration.hasMoreElements()) {
      CatalogEntry catalogEntry = (CatalogEntry)enumeration.nextElement();
      if (catalogEntry.getEntryType() == SYSTEM && (catalogEntry.getEntryArg(0).equals(paramString) || (bool && catalogEntry.getEntryArg(0).equalsIgnoreCase(paramString))))
        vector.addElement(catalogEntry.getEntryArg(1)); 
    } 
    return (vector.size() == 0) ? null : vector;
  }
  
  private Vector resolveLocalSystemReverse(String paramString) throws MalformedURLException, IOException {
    Vector vector = new Vector();
    String str = SecuritySupport.getSystemProperty("os.name");
    boolean bool = (str.indexOf("Windows") >= 0) ? 1 : 0;
    Enumeration enumeration = this.catalogEntries.elements();
    while (enumeration.hasMoreElements()) {
      CatalogEntry catalogEntry = (CatalogEntry)enumeration.nextElement();
      if (catalogEntry.getEntryType() == SYSTEM && (catalogEntry.getEntryArg(1).equals(paramString) || (bool && catalogEntry.getEntryArg(1).equalsIgnoreCase(paramString))))
        vector.addElement(catalogEntry.getEntryArg(0)); 
    } 
    return (vector.size() == 0) ? null : vector;
  }
  
  private Vector resolveAllSubordinateCatalogs(int paramInt, String paramString1, String paramString2, String paramString3) throws MalformedURLException, IOException {
    Vector vector = new Vector();
    for (byte b = 0; b < this.catalogs.size(); b++) {
      Resolver resolver = null;
      try {
        resolver = (Resolver)this.catalogs.elementAt(b);
      } catch (ClassCastException classCastException) {
        String str1 = (String)this.catalogs.elementAt(b);
        resolver = (Resolver)newCatalog();
        try {
          resolver.parseCatalog(str1);
        } catch (MalformedURLException malformedURLException) {
          this.catalogManager.debug.message(1, "Malformed Catalog URL", str1);
        } catch (FileNotFoundException fileNotFoundException) {
          this.catalogManager.debug.message(1, "Failed to load catalog, file not found", str1);
        } catch (IOException iOException) {
          this.catalogManager.debug.message(1, "Failed to load catalog, I/O error", str1);
        } 
        this.catalogs.setElementAt(resolver, b);
      } 
      String str = null;
      if (paramInt == DOCTYPE) {
        str = resolver.resolveDoctype(paramString1, paramString2, paramString3);
        if (str != null) {
          vector.addElement(str);
          return vector;
        } 
      } else if (paramInt == DOCUMENT) {
        str = resolver.resolveDocument();
        if (str != null) {
          vector.addElement(str);
          return vector;
        } 
      } else if (paramInt == ENTITY) {
        str = resolver.resolveEntity(paramString1, paramString2, paramString3);
        if (str != null) {
          vector.addElement(str);
          return vector;
        } 
      } else if (paramInt == NOTATION) {
        str = resolver.resolveNotation(paramString1, paramString2, paramString3);
        if (str != null) {
          vector.addElement(str);
          return vector;
        } 
      } else if (paramInt == PUBLIC) {
        str = resolver.resolvePublic(paramString2, paramString3);
        if (str != null) {
          vector.addElement(str);
          return vector;
        } 
      } else {
        if (paramInt == SYSTEM) {
          Vector vector1 = resolver.resolveAllSystem(paramString3);
          vector = appendVector(vector, vector1);
          break;
        } 
        if (paramInt == SYSTEMREVERSE) {
          Vector vector1 = resolver.resolveAllSystemReverse(paramString3);
          vector = appendVector(vector, vector1);
        } 
      } 
    } 
    return (vector != null) ? vector : null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\resolver\Resolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */