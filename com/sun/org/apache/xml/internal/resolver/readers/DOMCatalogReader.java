package com.sun.org.apache.xml.internal.resolver.readers;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogException;
import com.sun.org.apache.xml.internal.resolver.helpers.Namespaces;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import sun.reflect.misc.ReflectUtil;

public class DOMCatalogReader implements CatalogReader {
  protected Map<String, String> namespaceMap = new HashMap();
  
  public void setCatalogParser(String paramString1, String paramString2, String paramString3) {
    if (paramString1 == null) {
      this.namespaceMap.put(paramString2, paramString3);
    } else {
      this.namespaceMap.put("{" + paramString1 + "}" + paramString2, paramString3);
    } 
  }
  
  public String getCatalogParser(String paramString1, String paramString2) { return (paramString1 == null) ? (String)this.namespaceMap.get(paramString2) : (String)this.namespaceMap.get("{" + paramString1 + "}" + paramString2); }
  
  public void readCatalog(Catalog paramCatalog, InputStream paramInputStream) throws IOException, CatalogException {
    DocumentBuilderFactory documentBuilderFactory = null;
    DocumentBuilder documentBuilder = null;
    documentBuilderFactory = DocumentBuilderFactory.newInstance();
    documentBuilderFactory.setNamespaceAware(false);
    documentBuilderFactory.setValidating(false);
    try {
      documentBuilder = documentBuilderFactory.newDocumentBuilder();
    } catch (ParserConfigurationException parserConfigurationException) {
      throw new CatalogException(6);
    } 
    Document document = null;
    try {
      document = documentBuilder.parse(paramInputStream);
    } catch (SAXException sAXException) {
      throw new CatalogException(5);
    } 
    Element element = document.getDocumentElement();
    String str1 = Namespaces.getNamespaceURI(element);
    String str2 = Namespaces.getLocalName(element);
    String str3 = getCatalogParser(str1, str2);
    if (str3 == null) {
      if (str1 == null) {
        (paramCatalog.getCatalogManager()).debug.message(1, "No Catalog parser for " + str2);
      } else {
        (paramCatalog.getCatalogManager()).debug.message(1, "No Catalog parser for {" + str1 + "}" + str2);
      } 
      return;
    } 
    DOMCatalogParser dOMCatalogParser = null;
    try {
      dOMCatalogParser = (DOMCatalogParser)ReflectUtil.forName(str3).newInstance();
    } catch (ClassNotFoundException classNotFoundException) {
      (paramCatalog.getCatalogManager()).debug.message(1, "Cannot load XML Catalog Parser class", str3);
      throw new CatalogException(6);
    } catch (InstantiationException instantiationException) {
      (paramCatalog.getCatalogManager()).debug.message(1, "Cannot instantiate XML Catalog Parser class", str3);
      throw new CatalogException(6);
    } catch (IllegalAccessException illegalAccessException) {
      (paramCatalog.getCatalogManager()).debug.message(1, "Cannot access XML Catalog Parser class", str3);
      throw new CatalogException(6);
    } catch (ClassCastException classCastException) {
      (paramCatalog.getCatalogManager()).debug.message(1, "Cannot cast XML Catalog Parser class", str3);
      throw new CatalogException(6);
    } 
    for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling())
      dOMCatalogParser.parseCatalogEntry(paramCatalog, node); 
  }
  
  public void readCatalog(Catalog paramCatalog, String paramString) throws MalformedURLException, IOException, CatalogException {
    URL uRL = new URL(paramString);
    URLConnection uRLConnection = uRL.openConnection();
    readCatalog(paramCatalog, uRLConnection.getInputStream());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\resolver\readers\DOMCatalogReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */