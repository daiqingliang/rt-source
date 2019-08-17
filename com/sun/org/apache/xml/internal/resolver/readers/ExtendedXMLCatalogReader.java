package com.sun.org.apache.xml.internal.resolver.readers;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogEntry;
import com.sun.org.apache.xml.internal.resolver.CatalogException;
import com.sun.org.apache.xml.internal.resolver.Resolver;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ExtendedXMLCatalogReader extends OASISXMLCatalogReader {
  public static final String extendedNamespaceName = "http://nwalsh.com/xcatalog/1.0";
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    boolean bool = inExtensionNamespace();
    super.startElement(paramString1, paramString2, paramString3, paramAttributes);
    int i = -1;
    Vector vector = new Vector();
    if (paramString1 != null && "http://nwalsh.com/xcatalog/1.0".equals(paramString1) && !bool) {
      if (paramAttributes.getValue("xml:base") != null) {
        String str = paramAttributes.getValue("xml:base");
        i = Catalog.BASE;
        vector.add(str);
        this.baseURIStack.push(str);
        this.debug.message(4, "xml:base", str);
        try {
          CatalogEntry catalogEntry = new CatalogEntry(i, vector);
          this.catalog.addEntry(catalogEntry);
        } catch (CatalogException catalogException) {
          if (catalogException.getExceptionType() == 3) {
            this.debug.message(1, "Invalid catalog entry type", paramString2);
          } else if (catalogException.getExceptionType() == 2) {
            this.debug.message(1, "Invalid catalog entry (base)", paramString2);
          } 
        } 
        i = -1;
        vector = new Vector();
      } else {
        this.baseURIStack.push(this.baseURIStack.peek());
      } 
      if (paramString2.equals("uriSuffix")) {
        if (checkAttributes(paramAttributes, "suffix", "uri")) {
          i = Resolver.URISUFFIX;
          vector.add(paramAttributes.getValue("suffix"));
          vector.add(paramAttributes.getValue("uri"));
          this.debug.message(4, "uriSuffix", paramAttributes.getValue("suffix"), paramAttributes.getValue("uri"));
        } 
      } else if (paramString2.equals("systemSuffix")) {
        if (checkAttributes(paramAttributes, "suffix", "uri")) {
          i = Resolver.SYSTEMSUFFIX;
          vector.add(paramAttributes.getValue("suffix"));
          vector.add(paramAttributes.getValue("uri"));
          this.debug.message(4, "systemSuffix", paramAttributes.getValue("suffix"), paramAttributes.getValue("uri"));
        } 
      } else {
        this.debug.message(1, "Invalid catalog entry type", paramString2);
      } 
      if (i >= 0)
        try {
          CatalogEntry catalogEntry = new CatalogEntry(i, vector);
          this.catalog.addEntry(catalogEntry);
        } catch (CatalogException catalogException) {
          if (catalogException.getExceptionType() == 3) {
            this.debug.message(1, "Invalid catalog entry type", paramString2);
          } else if (catalogException.getExceptionType() == 2) {
            this.debug.message(1, "Invalid catalog entry", paramString2);
          } 
        }  
    } 
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    super.endElement(paramString1, paramString2, paramString3);
    boolean bool = inExtensionNamespace();
    int i = -1;
    Vector vector = new Vector();
    if (paramString1 != null && "http://nwalsh.com/xcatalog/1.0".equals(paramString1) && !bool) {
      String str1 = (String)this.baseURIStack.pop();
      String str2 = (String)this.baseURIStack.peek();
      if (!str2.equals(str1)) {
        this.catalog;
        i = Catalog.BASE;
        vector.add(str2);
        this.debug.message(4, "(reset) xml:base", str2);
        try {
          CatalogEntry catalogEntry = new CatalogEntry(i, vector);
          this.catalog.addEntry(catalogEntry);
        } catch (CatalogException catalogException) {
          if (catalogException.getExceptionType() == 3) {
            this.debug.message(1, "Invalid catalog entry type", paramString2);
          } else if (catalogException.getExceptionType() == 2) {
            this.debug.message(1, "Invalid catalog entry (rbase)", paramString2);
          } 
        } 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\resolver\readers\ExtendedXMLCatalogReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */