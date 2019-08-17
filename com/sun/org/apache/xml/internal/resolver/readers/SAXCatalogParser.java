package com.sun.org.apache.xml.internal.resolver.readers;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import org.xml.sax.ContentHandler;
import org.xml.sax.DocumentHandler;

public interface SAXCatalogParser extends ContentHandler, DocumentHandler {
  void setCatalog(Catalog paramCatalog);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\resolver\readers\SAXCatalogParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */