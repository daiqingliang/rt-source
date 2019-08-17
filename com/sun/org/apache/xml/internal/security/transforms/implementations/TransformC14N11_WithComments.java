package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer11_WithComments;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import java.io.OutputStream;

public class TransformC14N11_WithComments extends TransformSpi {
  protected String engineGetURI() { return "http://www.w3.org/2006/12/xml-c14n11#WithComments"; }
  
  protected XMLSignatureInput enginePerformTransform(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream, Transform paramTransform) throws CanonicalizationException {
    Canonicalizer11_WithComments canonicalizer11_WithComments = new Canonicalizer11_WithComments();
    if (paramOutputStream != null)
      canonicalizer11_WithComments.setWriter(paramOutputStream); 
    byte[] arrayOfByte = null;
    arrayOfByte = canonicalizer11_WithComments.engineCanonicalize(paramXMLSignatureInput);
    XMLSignatureInput xMLSignatureInput = new XMLSignatureInput(arrayOfByte);
    if (paramOutputStream != null)
      xMLSignatureInput.setOutputStream(paramOutputStream); 
    return xMLSignatureInput;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\implementations\TransformC14N11_WithComments.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */