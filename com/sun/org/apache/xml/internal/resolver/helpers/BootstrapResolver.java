package com.sun.org.apache.xml.internal.resolver.helpers;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class BootstrapResolver implements EntityResolver, URIResolver {
  public static final String xmlCatalogXSD = "http://www.oasis-open.org/committees/entity/release/1.0/catalog.xsd";
  
  public static final String xmlCatalogRNG = "http://www.oasis-open.org/committees/entity/release/1.0/catalog.rng";
  
  public static final String xmlCatalogPubId = "-//OASIS//DTD XML Catalogs V1.0//EN";
  
  public static final String xmlCatalogSysId = "http://www.oasis-open.org/committees/entity/release/1.0/catalog.dtd";
  
  private final Map<String, String> publicMap = new HashMap();
  
  private final Map<String, String> systemMap = new HashMap();
  
  private final Map<String, String> uriMap = new HashMap();
  
  public BootstrapResolver() {
    URL uRL = getClass().getResource("/com/sun/org/apache/xml/internal/resolver/etc/catalog.dtd");
    if (uRL != null) {
      this.publicMap.put("-//OASIS//DTD XML Catalogs V1.0//EN", uRL.toString());
      this.systemMap.put("http://www.oasis-open.org/committees/entity/release/1.0/catalog.dtd", uRL.toString());
    } 
    uRL = getClass().getResource("/com/sun/org/apache/xml/internal/resolver/etc/catalog.rng");
    if (uRL != null)
      this.uriMap.put("http://www.oasis-open.org/committees/entity/release/1.0/catalog.rng", uRL.toString()); 
    uRL = getClass().getResource("/com/sun/org/apache/xml/internal/resolver/etc/catalog.xsd");
    if (uRL != null)
      this.uriMap.put("http://www.oasis-open.org/committees/entity/release/1.0/catalog.xsd", uRL.toString()); 
  }
  
  public InputSource resolveEntity(String paramString1, String paramString2) {
    String str = null;
    if (paramString2 != null && this.systemMap.containsKey(paramString2)) {
      str = (String)this.systemMap.get(paramString2);
    } else if (paramString1 != null && this.publicMap.containsKey(paramString1)) {
      str = (String)this.publicMap.get(paramString1);
    } 
    if (str != null)
      try {
        InputSource inputSource = new InputSource(str);
        inputSource.setPublicId(paramString1);
        URL uRL = new URL(str);
        InputStream inputStream = uRL.openStream();
        inputSource.setByteStream(inputStream);
        return inputSource;
      } catch (Exception exception) {
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
    if (paramString1 != null && this.uriMap.containsKey(paramString1))
      str3 = (String)this.uriMap.get(paramString1); 
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
    SAXSource sAXSource = new SAXSource();
    sAXSource.setInputSource(new InputSource(str3));
    return sAXSource;
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\resolver\helpers\BootstrapResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */