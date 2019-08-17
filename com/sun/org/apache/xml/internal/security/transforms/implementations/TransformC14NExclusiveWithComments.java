package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315ExclWithComments;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.OutputStream;
import org.w3c.dom.Element;

public class TransformC14NExclusiveWithComments extends TransformSpi {
  public static final String implementedTransformURI = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
  
  protected String engineGetURI() { return "http://www.w3.org/2001/10/xml-exc-c14n#WithComments"; }
  
  protected XMLSignatureInput enginePerformTransform(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream, Transform paramTransform) throws CanonicalizationException {
    try {
      String str = null;
      if (paramTransform.length("http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces") == 1) {
        Element element = XMLUtils.selectNode(paramTransform.getElement().getFirstChild(), "http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces", 0);
        str = (new InclusiveNamespaces(element, paramTransform.getBaseURI())).getInclusiveNamespaces();
      } 
      Canonicalizer20010315ExclWithComments canonicalizer20010315ExclWithComments = new Canonicalizer20010315ExclWithComments();
      if (paramOutputStream != null)
        canonicalizer20010315ExclWithComments.setWriter(paramOutputStream); 
      byte[] arrayOfByte = canonicalizer20010315ExclWithComments.engineCanonicalize(paramXMLSignatureInput, str);
      return new XMLSignatureInput(arrayOfByte);
    } catch (XMLSecurityException xMLSecurityException) {
      throw new CanonicalizationException("empty", xMLSecurityException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\implementations\TransformC14NExclusiveWithComments.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */