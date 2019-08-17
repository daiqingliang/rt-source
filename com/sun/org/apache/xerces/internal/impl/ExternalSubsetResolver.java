package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLDTDDescription;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;

public interface ExternalSubsetResolver extends XMLEntityResolver {
  XMLInputSource getExternalSubset(XMLDTDDescription paramXMLDTDDescription) throws XNIException, IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\ExternalSubsetResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */