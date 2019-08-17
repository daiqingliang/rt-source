package com.sun.org.apache.xml.internal.resolver.readers;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogEntry;
import com.sun.org.apache.xml.internal.resolver.CatalogException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Vector;

public class TR9401CatalogReader extends TextCatalogReader {
  public void readCatalog(Catalog paramCatalog, InputStream paramInputStream) throws MalformedURLException, IOException {
    this.catfile = paramInputStream;
    if (this.catfile == null)
      return; 
    Vector vector = null;
    try {
      while (true) {
        String str1 = nextToken();
        if (str1 == null) {
          if (vector != null) {
            paramCatalog.unknownEntry(vector);
            vector = null;
          } 
          this.catfile.close();
          this.catfile = null;
          return;
        } 
        String str2 = null;
        if (this.caseSensitive) {
          str2 = str1;
        } else {
          str2 = str1.toUpperCase();
        } 
        if (str2.equals("DELEGATE"))
          str2 = "DELEGATE_PUBLIC"; 
        try {
          int i = CatalogEntry.getEntryType(str2);
          int j = CatalogEntry.getEntryArgCount(i);
          Vector vector1 = new Vector();
          if (vector != null) {
            paramCatalog.unknownEntry(vector);
            vector = null;
          } 
          for (byte b = 0; b < j; b++)
            vector1.addElement(nextToken()); 
          paramCatalog.addEntry(new CatalogEntry(str2, vector1));
        } catch (CatalogException catalogException) {
          if (catalogException.getExceptionType() == 3) {
            if (vector == null)
              vector = new Vector(); 
            vector.addElement(str1);
            continue;
          } 
          if (catalogException.getExceptionType() == 2) {
            (paramCatalog.getCatalogManager()).debug.message(1, "Invalid catalog entry", str1);
            vector = null;
            continue;
          } 
          if (catalogException.getExceptionType() == 8)
            (paramCatalog.getCatalogManager()).debug.message(1, catalogException.getMessage()); 
        } 
      } 
    } catch (CatalogException catalogException) {
      if (catalogException.getExceptionType() == 8)
        (paramCatalog.getCatalogManager()).debug.message(1, catalogException.getMessage()); 
      return;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\resolver\readers\TR9401CatalogReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */