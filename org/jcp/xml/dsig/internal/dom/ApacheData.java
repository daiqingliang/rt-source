package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import javax.xml.crypto.Data;

public interface ApacheData extends Data {
  XMLSignatureInput getXMLSignatureInput();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\ApacheData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */