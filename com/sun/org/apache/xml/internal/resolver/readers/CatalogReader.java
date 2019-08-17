package com.sun.org.apache.xml.internal.resolver.readers;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

public interface CatalogReader {
  void readCatalog(Catalog paramCatalog, String paramString) throws MalformedURLException, IOException, CatalogException;
  
  void readCatalog(Catalog paramCatalog, InputStream paramInputStream) throws IOException, CatalogException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\resolver\readers\CatalogReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */