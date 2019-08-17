package com.sun.org.apache.xml.internal.resolver.tools;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.helpers.FileURL;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import jdk.xml.internal.JdkXmlUtils;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class CatalogResolver implements EntityResolver, URIResolver {
  public boolean namespaceAware = true;
  
  public boolean validating = false;
  
  private Catalog catalog = null;
  
  private CatalogManager catalogManager = CatalogManager.getStaticManager();
  
  public CatalogResolver() { initializeCatalogs(false); }
  
  public CatalogResolver(boolean paramBoolean) { initializeCatalogs(paramBoolean); }
  
  public CatalogResolver(CatalogManager paramCatalogManager) {
    this.catalogManager = paramCatalogManager;
    initializeCatalogs(!this.catalogManager.getUseStaticCatalog());
  }
  
  private void initializeCatalogs(boolean paramBoolean) { this.catalog = this.catalogManager.getCatalog(); }
  
  public Catalog getCatalog() { return this.catalog; }
  
  public String getResolvedEntity(String paramString1, String paramString2) {
    String str = null;
    if (this.catalog == null) {
      this.catalogManager.debug.message(1, "Catalog resolution attempted with null catalog; ignored");
      return null;
    } 
    if (paramString2 != null)
      try {
        str = this.catalog.resolveSystem(paramString2);
      } catch (MalformedURLException malformedURLException) {
        this.catalogManager.debug.message(1, "Malformed URL exception trying to resolve", paramString1);
        str = null;
      } catch (IOException iOException) {
        this.catalogManager.debug.message(1, "I/O exception trying to resolve", paramString1);
        str = null;
      }  
    if (str == null) {
      if (paramString1 != null)
        try {
          str = this.catalog.resolvePublic(paramString1, paramString2);
        } catch (MalformedURLException malformedURLException) {
          this.catalogManager.debug.message(1, "Malformed URL exception trying to resolve", paramString1);
        } catch (IOException iOException) {
          this.catalogManager.debug.message(1, "I/O exception trying to resolve", paramString1);
        }  
      if (str != null)
        this.catalogManager.debug.message(2, "Resolved public", paramString1, str); 
    } else {
      this.catalogManager.debug.message(2, "Resolved system", paramString2, str);
    } 
    return str;
  }
  
  public InputSource resolveEntity(String paramString1, String paramString2) {
    String str = getResolvedEntity(paramString1, paramString2);
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
  
  public Source resolve(String paramString1, String paramString2) throws TransformerException {
    String str1 = paramString1;
    String str2 = null;
    int i = paramString1.indexOf("#");
    if (i >= 0) {
      str1 = paramString1.substring(0, i);
      str2 = paramString1.substring(i + 1);
    } 
    String str3 = null;
    try {
      str3 = this.catalog.resolveURI(paramString1);
    } catch (Exception exception) {}
    if (str3 == null)
      try {
        URL uRL = null;
        if (paramString2 == null) {
          uRL = new URL(str1);
          str3 = uRL.toString();
        } else {
          URL uRL1 = new URL(paramString2);
          uRL = (paramString1.length() == 0) ? uRL1 : new URL(uRL1, str1);
          str3 = uRL.toString();
        } 
      } catch (MalformedURLException malformedURLException) {
        String str = makeAbsolute(paramString2);
        if (!str.equals(paramString2))
          return resolve(paramString1, str); 
        throw new TransformerException("Malformed URL " + paramString1 + "(base " + paramString2 + ")", malformedURLException);
      }  
    this.catalogManager.debug.message(2, "Resolved URI", paramString1, str3);
    SAXSource sAXSource = new SAXSource();
    sAXSource.setInputSource(new InputSource(str3));
    setEntityResolver(sAXSource);
    return sAXSource;
  }
  
  private void setEntityResolver(SAXSource paramSAXSource) throws TransformerException {
    XMLReader xMLReader = paramSAXSource.getXMLReader();
    if (xMLReader == null) {
      SAXParserFactory sAXParserFactory = JdkXmlUtils.getSAXFactory(this.catalogManager.overrideDefaultParser());
      try {
        xMLReader = sAXParserFactory.newSAXParser().getXMLReader();
      } catch (ParserConfigurationException parserConfigurationException) {
        throw new TransformerException(parserConfigurationException);
      } catch (SAXException sAXException) {
        throw new TransformerException(sAXException);
      } 
    } 
    xMLReader.setEntityResolver(this);
    paramSAXSource.setXMLReader(xMLReader);
  }
  
  private String makeAbsolute(String paramString) {
    if (paramString == null)
      paramString = ""; 
    try {
      URL uRL = new URL(paramString);
      return uRL.toString();
    } catch (MalformedURLException malformedURLException) {
      try {
        URL uRL = FileURL.makeURL(paramString);
        return uRL.toString();
      } catch (MalformedURLException malformedURLException1) {
        return paramString;
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\resolver\tools\CatalogResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */