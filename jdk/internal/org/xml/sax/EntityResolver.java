package jdk.internal.org.xml.sax;

import java.io.IOException;

public interface EntityResolver {
  InputSource resolveEntity(String paramString1, String paramString2) throws SAXException, IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\xml\sax\EntityResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */