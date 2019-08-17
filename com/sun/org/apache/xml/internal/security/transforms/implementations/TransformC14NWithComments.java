package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315WithComments;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import java.io.OutputStream;

public class TransformC14NWithComments extends TransformSpi {
  public static final String implementedTransformURI = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
  
  protected String engineGetURI() { return "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments"; }
  
  protected XMLSignatureInput enginePerformTransform(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream, Transform paramTransform) throws CanonicalizationException {
    Canonicalizer20010315WithComments canonicalizer20010315WithComments = new Canonicalizer20010315WithComments();
    if (paramOutputStream != null)
      canonicalizer20010315WithComments.setWriter(paramOutputStream); 
    byte[] arrayOfByte = null;
    arrayOfByte = canonicalizer20010315WithComments.engineCanonicalize(paramXMLSignatureInput);
    XMLSignatureInput xMLSignatureInput = new XMLSignatureInput(arrayOfByte);
    if (paramOutputStream != null)
      xMLSignatureInput.setOutputStream(paramOutputStream); 
    return xMLSignatureInput;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\implementations\TransformC14NWithComments.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */