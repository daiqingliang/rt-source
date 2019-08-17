package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;

public interface XMLEntityResolver {
  XMLInputSource resolveEntity(XMLResourceIdentifier paramXMLResourceIdentifier) throws XNIException, IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xni\parser\XMLEntityResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */