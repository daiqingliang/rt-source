package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315ExclOmitComments;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.OutputStream;
import org.w3c.dom.Element;

public class TransformC14NExclusive extends TransformSpi {
  public static final String implementedTransformURI = "http://www.w3.org/2001/10/xml-exc-c14n#";
  
  protected String engineGetURI() { return "http://www.w3.org/2001/10/xml-exc-c14n#"; }
  
  protected XMLSignatureInput enginePerformTransform(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream, Transform paramTransform) throws CanonicalizationException {
    try {
      String str = null;
      if (paramTransform.length("http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces") == 1) {
        Element element = XMLUtils.selectNode(paramTransform.getElement().getFirstChild(), "http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces", 0);
        str = (new InclusiveNamespaces(element, paramTransform.getBaseURI())).getInclusiveNamespaces();
      } 
      Canonicalizer20010315ExclOmitComments canonicalizer20010315ExclOmitComments = new Canonicalizer20010315ExclOmitComments();
      if (paramOutputStream != null)
        canonicalizer20010315ExclOmitComments.setWriter(paramOutputStream); 
      byte[] arrayOfByte = canonicalizer20010315ExclOmitComments.engineCanonicalize(paramXMLSignatureInput, str);
      XMLSignatureInput xMLSignatureInput = new XMLSignatureInput(arrayOfByte);
      if (paramOutputStream != null)
        xMLSignatureInput.setOutputStream(paramOutputStream); 
      return xMLSignatureInput;
    } catch (XMLSecurityException xMLSecurityException) {
      throw new CanonicalizationException("empty", xMLSecurityException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\implementations\TransformC14NExclusive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */