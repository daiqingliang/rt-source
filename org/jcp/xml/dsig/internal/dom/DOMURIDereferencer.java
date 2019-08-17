package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.Init;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import javax.xml.crypto.Data;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dom.DOMURIReference;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public class DOMURIDereferencer implements URIDereferencer {
  static final URIDereferencer INSTANCE = new DOMURIDereferencer();
  
  private DOMURIDereferencer() { Init.init(); }
  
  public Data dereference(URIReference paramURIReference, XMLCryptoContext paramXMLCryptoContext) throws URIReferenceException {
    if (paramURIReference == null)
      throw new NullPointerException("uriRef cannot be null"); 
    if (paramXMLCryptoContext == null)
      throw new NullPointerException("context cannot be null"); 
    DOMURIReference dOMURIReference = (DOMURIReference)paramURIReference;
    Attr attr = (Attr)dOMURIReference.getHere();
    String str1 = paramURIReference.getURI();
    DOMCryptoContext dOMCryptoContext = (DOMCryptoContext)paramXMLCryptoContext;
    String str2 = paramXMLCryptoContext.getBaseURI();
    boolean bool = Utils.secureValidation(paramXMLCryptoContext);
    if (bool && Policy.restrictReferenceUriScheme(str1))
      throw new URIReferenceException("Uri " + str1 + " is forbidden when secure validation is enabled"); 
    if (str1 != null && str1.length() != 0 && str1.charAt(0) == '#') {
      String str = str1.substring(1);
      if (str.startsWith("xpointer(id(")) {
        int i = str.indexOf('\'');
        int j = str.indexOf('\'', i + 1);
        str = str.substring(i + 1, j);
      } 
      Element element = attr.getOwnerDocument().getElementById(str);
      if (element == null)
        element = dOMCryptoContext.getElementById(str); 
      if (element != null) {
        if (bool && Policy.restrictDuplicateIds()) {
          Element element1 = element.getOwnerDocument().getDocumentElement();
          if (!XMLUtils.protectAgainstWrappingAttack(element1, (Element)element, str)) {
            String str3 = "Multiple Elements with the same ID " + str + " detected when secure validation is enabled";
            throw new URIReferenceException(str3);
          } 
        } 
        XMLSignatureInput xMLSignatureInput = new XMLSignatureInput(element);
        if (!str1.substring(1).startsWith("xpointer(id("))
          xMLSignatureInput.setExcludeComments(true); 
        xMLSignatureInput.setMIMEType("text/xml");
        if (str2 != null && str2.length() > 0) {
          xMLSignatureInput.setSourceURI(str2.concat(attr.getNodeValue()));
        } else {
          xMLSignatureInput.setSourceURI(attr.getNodeValue());
        } 
        return new ApacheNodeSetData(xMLSignatureInput);
      } 
    } 
    try {
      ResourceResolver resourceResolver = ResourceResolver.getInstance(attr, str2, false);
      XMLSignatureInput xMLSignatureInput = resourceResolver.resolve(attr, str2, false);
      return xMLSignatureInput.isOctetStream() ? new ApacheOctetStreamData(xMLSignatureInput) : new ApacheNodeSetData(xMLSignatureInput);
    } catch (Exception exception) {
      throw new URIReferenceException(exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMURIDereferencer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */