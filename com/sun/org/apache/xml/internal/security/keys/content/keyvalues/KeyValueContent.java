package com.sun.org.apache.xml.internal.security.keys.content.keyvalues;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import java.security.PublicKey;

public interface KeyValueContent {
  PublicKey getPublicKey() throws XMLSecurityException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\content\keyvalues\KeyValueContent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */