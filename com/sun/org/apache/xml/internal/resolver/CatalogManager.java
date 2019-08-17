package com.sun.org.apache.xml.internal.resolver;

import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.resolver.helpers.BootstrapResolver;
import com.sun.org.apache.xml.internal.resolver.helpers.Debug;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.reflect.misc.ReflectUtil;

public class CatalogManager {
  private static String pFiles = "xml.catalog.files";
  
  private static String pVerbosity = "xml.catalog.verbosity";
  
  private static String pPrefer = "xml.catalog.prefer";
  
  private static String pStatic = "xml.catalog.staticCatalog";
  
  private static String pAllowPI = "xml.catalog.allowPI";
  
  private static String pClassname = "xml.catalog.className";
  
  private static String pIgnoreMissing = "xml.catalog.ignoreMissing";
  
  private static CatalogManager staticManager = new CatalogManager();
  
  private BootstrapResolver bResolver = new BootstrapResolver();
  
  private boolean ignoreMissingProperties = (SecuritySupport.getSystemProperty(pIgnoreMissing) != null || SecuritySupport.getSystemProperty(pFiles) != null);
  
  private ResourceBundle resources;
  
  private String propertyFile = "CatalogManager.properties";
  
  private URL propertyFileURI = null;
  
  private String defaultCatalogFiles = "./xcatalog";
  
  private String catalogFiles = null;
  
  private boolean fromPropertiesFile = false;
  
  private int defaultVerbosity = 1;
  
  private Integer verbosity = null;
  
  private boolean defaultPreferPublic = true;
  
  private Boolean preferPublic = null;
  
  private boolean defaultUseStaticCatalog = true;
  
  private Boolean useStaticCatalog = null;
  
  private static Catalog staticCatalog = null;
  
  private boolean defaultOasisXMLCatalogPI = true;
  
  private Boolean oasisXMLCatalogPI = null;
  
  private boolean defaultRelativeCatalogs = true;
  
  private Boolean relativeCatalogs = null;
  
  private String catalogClassName = null;
  
  private boolean overrideDefaultParser;
  
  public Debug debug = null;
  
  public CatalogManager() { init(); }
  
  public CatalogManager(String paramString) {
    this.propertyFile = paramString;
    init();
  }
  
  private void init() {
    this.debug = new Debug();
    if (System.getSecurityManager() == null)
      this.overrideDefaultParser = true; 
  }
  
  public void setBootstrapResolver(BootstrapResolver paramBootstrapResolver) { this.bResolver = paramBootstrapResolver; }
  
  public BootstrapResolver getBootstrapResolver() { return this.bResolver; }
  
  private void readProperties() {
    try {
      this.propertyFileURI = CatalogManager.class.getResource("/" + this.propertyFile);
      InputStream inputStream = CatalogManager.class.getResourceAsStream("/" + this.propertyFile);
      if (inputStream == null) {
        if (!this.ignoreMissingProperties) {
          System.err.println("Cannot find " + this.propertyFile);
          this.ignoreMissingProperties = true;
        } 
        return;
      } 
      this.resources = new PropertyResourceBundle(inputStream);
    } catch (MissingResourceException missingResourceException) {
      if (!this.ignoreMissingProperties)
        System.err.println("Cannot read " + this.propertyFile); 
    } catch (IOException iOException) {
      if (!this.ignoreMissingProperties)
        System.err.println("Failure trying to read " + this.propertyFile); 
    } 
    if (this.verbosity == null)
      try {
        String str = this.resources.getString("verbosity");
        int i = Integer.parseInt(str.trim());
        this.debug.setDebug(i);
        this.verbosity = new Integer(i);
      } catch (Exception exception) {} 
  }
  
  public static CatalogManager getStaticManager() { return staticManager; }
  
  public boolean getIgnoreMissingProperties() { return this.ignoreMissingProperties; }
  
  public void setIgnoreMissingProperties(boolean paramBoolean) { this.ignoreMissingProperties = paramBoolean; }
  
  public void ignoreMissingProperties(boolean paramBoolean) { setIgnoreMissingProperties(paramBoolean); }
  
  private int queryVerbosity() {
    String str1 = Integer.toString(this.defaultVerbosity);
    String str2 = SecuritySupport.getSystemProperty(pVerbosity);
    if (str2 == null) {
      if (this.resources == null)
        readProperties(); 
      if (this.resources != null) {
        try {
          str2 = this.resources.getString("verbosity");
        } catch (MissingResourceException missingResourceException) {
          str2 = str1;
        } 
      } else {
        str2 = str1;
      } 
    } 
    int i = this.defaultVerbosity;
    try {
      i = Integer.parseInt(str2.trim());
    } catch (Exception exception) {
      System.err.println("Cannot parse verbosity: \"" + str2 + "\"");
    } 
    if (this.verbosity == null) {
      this.debug.setDebug(i);
      this.verbosity = new Integer(i);
    } 
    return i;
  }
  
  public int getVerbosity() {
    if (this.verbosity == null)
      this.verbosity = new Integer(queryVerbosity()); 
    return this.verbosity.intValue();
  }
  
  public void setVerbosity(int paramInt) {
    this.verbosity = new Integer(paramInt);
    this.debug.setDebug(paramInt);
  }
  
  public int verbosity() { return getVerbosity(); }
  
  private boolean queryRelativeCatalogs() {
    if (this.resources == null)
      readProperties(); 
    if (this.resources == null)
      return this.defaultRelativeCatalogs; 
    try {
      String str = this.resources.getString("relative-catalogs");
      return (str.equalsIgnoreCase("true") || str.equalsIgnoreCase("yes") || str.equalsIgnoreCase("1"));
    } catch (MissingResourceException missingResourceException) {
      return this.defaultRelativeCatalogs;
    } 
  }
  
  public boolean getRelativeCatalogs() {
    if (this.relativeCatalogs == null)
      this.relativeCatalogs = new Boolean(queryRelativeCatalogs()); 
    return this.relativeCatalogs.booleanValue();
  }
  
  public void setRelativeCatalogs(boolean paramBoolean) { this.relativeCatalogs = new Boolean(paramBoolean); }
  
  public boolean relativeCatalogs() { return getRelativeCatalogs(); }
  
  private String queryCatalogFiles() {
    String str = SecuritySupport.getSystemProperty(pFiles);
    this.fromPropertiesFile = false;
    if (str == null) {
      if (this.resources == null)
        readProperties(); 
      if (this.resources != null)
        try {
          str = this.resources.getString("catalogs");
          this.fromPropertiesFile = true;
        } catch (MissingResourceException missingResourceException) {
          System.err.println(this.propertyFile + ": catalogs not found.");
          str = null;
        }  
    } 
    if (str == null)
      str = this.defaultCatalogFiles; 
    return str;
  }
  
  public Vector getCatalogFiles() {
    if (this.catalogFiles == null)
      this.catalogFiles = queryCatalogFiles(); 
    StringTokenizer stringTokenizer = new StringTokenizer(this.catalogFiles, ";");
    Vector vector = new Vector();
    while (stringTokenizer.hasMoreTokens()) {
      String str = stringTokenizer.nextToken();
      URL uRL = null;
      if (this.fromPropertiesFile && !relativeCatalogs())
        try {
          uRL = new URL(this.propertyFileURI, str);
          str = uRL.toString();
        } catch (MalformedURLException malformedURLException) {
          uRL = null;
        }  
      vector.add(str);
    } 
    return vector;
  }
  
  public void setCatalogFiles(String paramString) {
    this.catalogFiles = paramString;
    this.fromPropertiesFile = false;
  }
  
  public Vector catalogFiles() { return getCatalogFiles(); }
  
  private boolean queryPreferPublic() {
    String str = SecuritySupport.getSystemProperty(pPrefer);
    if (str == null) {
      if (this.resources == null)
        readProperties(); 
      if (this.resources == null)
        return this.defaultPreferPublic; 
      try {
        str = this.resources.getString("prefer");
      } catch (MissingResourceException missingResourceException) {
        return this.defaultPreferPublic;
      } 
    } 
    return (str == null) ? this.defaultPreferPublic : str.equalsIgnoreCase("public");
  }
  
  public boolean getPreferPublic() {
    if (this.preferPublic == null)
      this.preferPublic = new Boolean(queryPreferPublic()); 
    return this.preferPublic.booleanValue();
  }
  
  public void setPreferPublic(boolean paramBoolean) { this.preferPublic = new Boolean(paramBoolean); }
  
  public boolean preferPublic() { return getPreferPublic(); }
  
  private boolean queryUseStaticCatalog() {
    String str = SecuritySupport.getSystemProperty(pStatic);
    if (str == null) {
      if (this.resources == null)
        readProperties(); 
      if (this.resources == null)
        return this.defaultUseStaticCatalog; 
      try {
        str = this.resources.getString("static-catalog");
      } catch (MissingResourceException missingResourceException) {
        return this.defaultUseStaticCatalog;
      } 
    } 
    return (str == null) ? this.defaultUseStaticCatalog : ((str.equalsIgnoreCase("true") || str.equalsIgnoreCase("yes") || str.equalsIgnoreCase("1")) ? 1 : 0);
  }
  
  public boolean getUseStaticCatalog() {
    if (this.useStaticCatalog == null)
      this.useStaticCatalog = new Boolean(queryUseStaticCatalog()); 
    return this.useStaticCatalog.booleanValue();
  }
  
  public void setUseStaticCatalog(boolean paramBoolean) { this.useStaticCatalog = new Boolean(paramBoolean); }
  
  public boolean staticCatalog() { return getUseStaticCatalog(); }
  
  public Catalog getPrivateCatalog() {
    Catalog catalog = staticCatalog;
    if (this.useStaticCatalog == null)
      this.useStaticCatalog = new Boolean(getUseStaticCatalog()); 
    if (catalog == null || !this.useStaticCatalog.booleanValue()) {
      try {
        String str = getCatalogClassName();
        if (str == null) {
          catalog = new Catalog();
        } else {
          try {
            catalog = (Catalog)ReflectUtil.forName(str).newInstance();
          } catch (ClassNotFoundException classNotFoundException) {
            this.debug.message(1, "Catalog class named '" + str + "' could not be found. Using default.");
            catalog = new Catalog();
          } catch (ClassCastException classCastException) {
            this.debug.message(1, "Class named '" + str + "' is not a Catalog. Using default.");
            catalog = new Catalog();
          } 
        } 
        catalog.setCatalogManager(this);
        catalog.setupReaders();
        catalog.loadSystemCatalogs();
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
      if (this.useStaticCatalog.booleanValue())
        staticCatalog = catalog; 
    } 
    return catalog;
  }
  
  public Catalog getCatalog() {
    Catalog catalog = staticCatalog;
    if (this.useStaticCatalog == null)
      this.useStaticCatalog = new Boolean(getUseStaticCatalog()); 
    if (catalog == null || !this.useStaticCatalog.booleanValue()) {
      catalog = getPrivateCatalog();
      if (this.useStaticCatalog.booleanValue())
        staticCatalog = catalog; 
    } 
    return catalog;
  }
  
  public boolean queryAllowOasisXMLCatalogPI() {
    String str = SecuritySupport.getSystemProperty(pAllowPI);
    if (str == null) {
      if (this.resources == null)
        readProperties(); 
      if (this.resources == null)
        return this.defaultOasisXMLCatalogPI; 
      try {
        str = this.resources.getString("allow-oasis-xml-catalog-pi");
      } catch (MissingResourceException missingResourceException) {
        return this.defaultOasisXMLCatalogPI;
      } 
    } 
    return (str == null) ? this.defaultOasisXMLCatalogPI : ((str.equalsIgnoreCase("true") || str.equalsIgnoreCase("yes") || str.equalsIgnoreCase("1")) ? 1 : 0);
  }
  
  public boolean getAllowOasisXMLCatalogPI() {
    if (this.oasisXMLCatalogPI == null)
      this.oasisXMLCatalogPI = new Boolean(queryAllowOasisXMLCatalogPI()); 
    return this.oasisXMLCatalogPI.booleanValue();
  }
  
  public boolean overrideDefaultParser() { return this.overrideDefaultParser; }
  
  public void setAllowOasisXMLCatalogPI(boolean paramBoolean) { this.oasisXMLCatalogPI = new Boolean(paramBoolean); }
  
  public boolean allowOasisXMLCatalogPI() { return getAllowOasisXMLCatalogPI(); }
  
  public String queryCatalogClassName() {
    String str = SecuritySupport.getSystemProperty(pClassname);
    if (str == null) {
      if (this.resources == null)
        readProperties(); 
      if (this.resources == null)
        return null; 
      try {
        return this.resources.getString("catalog-class-name");
      } catch (MissingResourceException missingResourceException) {
        return null;
      } 
    } 
    return str;
  }
  
  public String getCatalogClassName() {
    if (this.catalogClassName == null)
      this.catalogClassName = queryCatalogClassName(); 
    return this.catalogClassName;
  }
  
  public void setCatalogClassName(String paramString) { this.catalogClassName = paramString; }
  
  public String catalogClassName() { return getCatalogClassName(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\resolver\CatalogManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */