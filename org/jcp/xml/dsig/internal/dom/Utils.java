package org.jcp.xml.dsig.internal.dom;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.xml.crypto.XMLCryptoContext;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public final class Utils {
  public static byte[] readBytesFromStream(InputStream paramInputStream) throws IOException {
    int i;
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byte[] arrayOfByte = new byte[1024];
    do {
      i = paramInputStream.read(arrayOfByte);
      if (i == -1)
        break; 
      byteArrayOutputStream.write(arrayOfByte, 0, i);
    } while (i >= 1024);
    return byteArrayOutputStream.toByteArray();
  }
  
  static Set<Node> toNodeSet(Iterator<Node> paramIterator) {
    HashSet hashSet = new HashSet();
    while (paramIterator.hasNext()) {
      Node node = (Node)paramIterator.next();
      hashSet.add(node);
      if (node.getNodeType() == 1) {
        NamedNodeMap namedNodeMap = node.getAttributes();
        byte b = 0;
        int i = namedNodeMap.getLength();
        while (b < i) {
          hashSet.add(namedNodeMap.item(b));
          b++;
        } 
      } 
    } 
    return hashSet;
  }
  
  public static String parseIdFromSameDocumentURI(String paramString) {
    if (paramString.length() == 0)
      return null; 
    String str = paramString.substring(1);
    if (str != null && str.startsWith("xpointer(id(")) {
      int i = str.indexOf('\'');
      int j = str.indexOf('\'', i + 1);
      str = str.substring(i + 1, j);
    } 
    return str;
  }
  
  public static boolean sameDocumentURI(String paramString) { return (paramString != null && (paramString.length() == 0 || paramString.charAt(0) == '#')); }
  
  static boolean secureValidation(XMLCryptoContext paramXMLCryptoContext) { return (paramXMLCryptoContext == null) ? false : getBoolean(paramXMLCryptoContext, "org.jcp.xml.dsig.secureValidation"); }
  
  private static boolean getBoolean(XMLCryptoContext paramXMLCryptoContext, String paramString) {
    Boolean bool = (Boolean)paramXMLCryptoContext.getProperty(paramString);
    return (bool != null && bool.booleanValue());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\Utils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */