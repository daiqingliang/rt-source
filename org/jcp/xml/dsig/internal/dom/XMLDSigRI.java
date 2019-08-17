package org.jcp.xml.dsig.internal.dom;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.util.HashMap;
import java.util.Map;

public final class XMLDSigRI extends Provider {
  static final long serialVersionUID = -5049765099299494554L;
  
  private static final String INFO = "XMLDSig (DOM XMLSignatureFactory; DOM KeyInfoFactory; C14N 1.0, C14N 1.1, Exclusive C14N, Base64, Enveloped, XPath, XPath2, XSLT TransformServices)";
  
  public XMLDSigRI() {
    super("XMLDSig", 1.8D, "XMLDSig (DOM XMLSignatureFactory; DOM KeyInfoFactory; C14N 1.0, C14N 1.1, Exclusive C14N, Base64, Enveloped, XPath, XPath2, XSLT TransformServices)");
    final HashMap map = new HashMap();
    hashMap.put("XMLSignatureFactory.DOM", "org.jcp.xml.dsig.internal.dom.DOMXMLSignatureFactory");
    hashMap.put("KeyInfoFactory.DOM", "org.jcp.xml.dsig.internal.dom.DOMKeyInfoFactory");
    hashMap.put("TransformService.http://www.w3.org/TR/2001/REC-xml-c14n-20010315", "org.jcp.xml.dsig.internal.dom.DOMCanonicalXMLC14NMethod");
    hashMap.put("Alg.Alias.TransformService.INCLUSIVE", "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
    hashMap.put("TransformService.http://www.w3.org/TR/2001/REC-xml-c14n-20010315 MechanismType", "DOM");
    hashMap.put("TransformService.http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments", "org.jcp.xml.dsig.internal.dom.DOMCanonicalXMLC14NMethod");
    hashMap.put("Alg.Alias.TransformService.INCLUSIVE_WITH_COMMENTS", "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments");
    hashMap.put("TransformService.http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments MechanismType", "DOM");
    hashMap.put("TransformService.http://www.w3.org/2006/12/xml-c14n11", "org.jcp.xml.dsig.internal.dom.DOMCanonicalXMLC14N11Method");
    hashMap.put("TransformService.http://www.w3.org/2006/12/xml-c14n11 MechanismType", "DOM");
    hashMap.put("TransformService.http://www.w3.org/2006/12/xml-c14n11#WithComments", "org.jcp.xml.dsig.internal.dom.DOMCanonicalXMLC14N11Method");
    hashMap.put("TransformService.http://www.w3.org/2006/12/xml-c14n11#WithComments MechanismType", "DOM");
    hashMap.put("TransformService.http://www.w3.org/2001/10/xml-exc-c14n#", "org.jcp.xml.dsig.internal.dom.DOMExcC14NMethod");
    hashMap.put("Alg.Alias.TransformService.EXCLUSIVE", "http://www.w3.org/2001/10/xml-exc-c14n#");
    hashMap.put("TransformService.http://www.w3.org/2001/10/xml-exc-c14n# MechanismType", "DOM");
    hashMap.put("TransformService.http://www.w3.org/2001/10/xml-exc-c14n#WithComments", "org.jcp.xml.dsig.internal.dom.DOMExcC14NMethod");
    hashMap.put("Alg.Alias.TransformService.EXCLUSIVE_WITH_COMMENTS", "http://www.w3.org/2001/10/xml-exc-c14n#WithComments");
    hashMap.put("TransformService.http://www.w3.org/2001/10/xml-exc-c14n#WithComments MechanismType", "DOM");
    hashMap.put("TransformService.http://www.w3.org/2000/09/xmldsig#base64", "org.jcp.xml.dsig.internal.dom.DOMBase64Transform");
    hashMap.put("Alg.Alias.TransformService.BASE64", "http://www.w3.org/2000/09/xmldsig#base64");
    hashMap.put("TransformService.http://www.w3.org/2000/09/xmldsig#base64 MechanismType", "DOM");
    hashMap.put("TransformService.http://www.w3.org/2000/09/xmldsig#enveloped-signature", "org.jcp.xml.dsig.internal.dom.DOMEnvelopedTransform");
    hashMap.put("Alg.Alias.TransformService.ENVELOPED", "http://www.w3.org/2000/09/xmldsig#enveloped-signature");
    hashMap.put("TransformService.http://www.w3.org/2000/09/xmldsig#enveloped-signature MechanismType", "DOM");
    hashMap.put("TransformService.http://www.w3.org/2002/06/xmldsig-filter2", "org.jcp.xml.dsig.internal.dom.DOMXPathFilter2Transform");
    hashMap.put("Alg.Alias.TransformService.XPATH2", "http://www.w3.org/2002/06/xmldsig-filter2");
    hashMap.put("TransformService.http://www.w3.org/2002/06/xmldsig-filter2 MechanismType", "DOM");
    hashMap.put("TransformService.http://www.w3.org/TR/1999/REC-xpath-19991116", "org.jcp.xml.dsig.internal.dom.DOMXPathTransform");
    hashMap.put("Alg.Alias.TransformService.XPATH", "http://www.w3.org/TR/1999/REC-xpath-19991116");
    hashMap.put("TransformService.http://www.w3.org/TR/1999/REC-xpath-19991116 MechanismType", "DOM");
    hashMap.put("TransformService.http://www.w3.org/TR/1999/REC-xslt-19991116", "org.jcp.xml.dsig.internal.dom.DOMXSLTTransform");
    hashMap.put("Alg.Alias.TransformService.XSLT", "http://www.w3.org/TR/1999/REC-xslt-19991116");
    hashMap.put("TransformService.http://www.w3.org/TR/1999/REC-xslt-19991116 MechanismType", "DOM");
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            XMLDSigRI.this.putAll(map);
            return null;
          }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\XMLDSigRI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */