package com.sun.org.apache.xml.internal.resolver.tools;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.helpers.FileURL;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

public class ResolvingXMLFilter extends XMLFilterImpl {
  public static boolean suppressExplanation = false;
  
  CatalogManager catalogManager = CatalogManager.getStaticManager();
  
  private CatalogResolver catalogResolver = null;
  
  private CatalogResolver piCatalogResolver = null;
  
  private boolean allowXMLCatalogPI = false;
  
  private boolean oasisXMLCatalogPI = false;
  
  private URL baseURL = null;
  
  public ResolvingXMLFilter() { this.catalogResolver = new CatalogResolver(this.catalogManager); }
  
  public ResolvingXMLFilter(XMLReader paramXMLReader) {
    super(paramXMLReader);
    this.catalogResolver = new CatalogResolver(this.catalogManager);
  }
  
  public ResolvingXMLFilter(CatalogManager paramCatalogManager) {
    this.catalogManager = paramCatalogManager;
    this.catalogResolver = new CatalogResolver(this.catalogManager);
  }
  
  public ResolvingXMLFilter(XMLReader paramXMLReader, CatalogManager paramCatalogManager) {
    super(paramXMLReader);
    this.catalogManager = paramCatalogManager;
    this.catalogResolver = new CatalogResolver(this.catalogManager);
  }
  
  public Catalog getCatalog() { return this.catalogResolver.getCatalog(); }
  
  public void parse(InputSource paramInputSource) throws IOException, SAXException {
    this.allowXMLCatalogPI = true;
    setupBaseURI(paramInputSource.getSystemId());
    try {
      super.parse(paramInputSource);
    } catch (InternalError internalError) {
      explain(paramInputSource.getSystemId());
      throw internalError;
    } 
  }
  
  public void parse(String paramString) throws IOException, SAXException {
    this.allowXMLCatalogPI = true;
    setupBaseURI(paramString);
    try {
      super.parse(paramString);
    } catch (InternalError internalError) {
      explain(paramString);
      throw internalError;
    } 
  }
  
  public InputSource resolveEntity(String paramString1, String paramString2) {
    this.allowXMLCatalogPI = false;
    String str = this.catalogResolver.getResolvedEntity(paramString1, paramString2);
    if (str == null && this.piCatalogResolver != null)
      str = this.piCatalogResolver.getResolvedEntity(paramString1, paramString2); 
    if (str != null)
      try {
        InputSource inputSource = new InputSource(str);
        inputSource.setPublicId(paramString1);
        URL uRL = new URL(str);
        InputStream inputStream = uRL.openStream();
        inputSource.setByteStream(inputStream);
        return inputSource;
      } catch (Exception exception) {
        this.catalogManager.debug.message(1, "Failed to create InputSource", str);
        return null;
      }  
    return null;
  }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3) throws SAXException {
    this.allowXMLCatalogPI = false;
    super.notationDecl(paramString1, paramString2, paramString3);
  }
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4) throws SAXException {
    this.allowXMLCatalogPI = false;
    super.unparsedEntityDecl(paramString1, paramString2, paramString3, paramString4);
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    this.allowXMLCatalogPI = false;
    super.startElement(paramString1, paramString2, paramString3, paramAttributes);
  }
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {
    if (paramString1.equals("oasis-xml-catalog")) {
      URL uRL = null;
      String str = paramString2;
      int i = str.indexOf("catalog=");
      if (i >= 0) {
        str = str.substring(i + 8);
        if (str.length() > 1) {
          String str1 = str.substring(0, 1);
          str = str.substring(1);
          i = str.indexOf(str1);
          if (i >= 0) {
            str = str.substring(0, i);
            try {
              if (this.baseURL != null) {
                uRL = new URL(this.baseURL, str);
              } else {
                uRL = new URL(str);
              } 
            } catch (MalformedURLException malformedURLException) {}
          } 
        } 
      } 
      if (this.allowXMLCatalogPI) {
        if (this.catalogManager.getAllowOasisXMLCatalogPI()) {
          this.catalogManager.debug.message(4, "oasis-xml-catalog PI", paramString2);
          if (uRL != null) {
            try {
              this.catalogManager.debug.message(4, "oasis-xml-catalog", uRL.toString());
              this.oasisXMLCatalogPI = true;
              if (this.piCatalogResolver == null)
                this.piCatalogResolver = new CatalogResolver(true); 
              this.piCatalogResolver.getCatalog().parseCatalog(uRL.toString());
            } catch (Exception exception) {
              this.catalogManager.debug.message(3, "Exception parsing oasis-xml-catalog: " + uRL.toString());
            } 
          } else {
            this.catalogManager.debug.message(3, "PI oasis-xml-catalog unparseable: " + paramString2);
          } 
        } else {
          this.catalogManager.debug.message(4, "PI oasis-xml-catalog ignored: " + paramString2);
        } 
      } else {
        this.catalogManager.debug.message(3, "PI oasis-xml-catalog occurred in an invalid place: " + paramString2);
      } 
    } else {
      super.processingInstruction(paramString1, paramString2);
    } 
  }
  
  private void setupBaseURI(String paramString) throws IOException, SAXException {
    URL uRL = null;
    try {
      uRL = FileURL.makeURL("basename");
    } catch (MalformedURLException malformedURLException) {
      uRL = null;
    } 
    try {
      this.baseURL = new URL(paramString);
    } catch (MalformedURLException malformedURLException) {
      if (uRL != null) {
        try {
          this.baseURL = new URL(uRL, paramString);
        } catch (MalformedURLException malformedURLException1) {
          this.baseURL = null;
        } 
      } else {
        this.baseURL = null;
      } 
    } 
  }
  
  private void explain(String paramString) throws IOException, SAXException {
    if (!suppressExplanation) {
      System.out.println("XMLReader probably encountered bad URI in " + paramString);
      System.out.println("For example, replace '/some/uri' with 'file:/some/uri'.");
    } 
    suppressExplanation = true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\resolver\tools\ResolvingXMLFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */