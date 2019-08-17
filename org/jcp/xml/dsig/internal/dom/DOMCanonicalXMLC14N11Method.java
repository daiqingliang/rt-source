package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import java.security.InvalidAlgorithmParameterException;
import javax.xml.crypto.Data;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

public final class DOMCanonicalXMLC14N11Method extends ApacheCanonicalizer {
  public static final String C14N_11 = "http://www.w3.org/2006/12/xml-c14n11";
  
  public static final String C14N_11_WITH_COMMENTS = "http://www.w3.org/2006/12/xml-c14n11#WithComments";
  
  public void init(TransformParameterSpec paramTransformParameterSpec) throws InvalidAlgorithmParameterException {
    if (paramTransformParameterSpec != null)
      throw new InvalidAlgorithmParameterException("no parameters should be specified for Canonical XML 1.1 algorithm"); 
  }
  
  public Data transform(Data paramData, XMLCryptoContext paramXMLCryptoContext) throws TransformException {
    if (paramData instanceof DOMSubTreeData) {
      DOMSubTreeData dOMSubTreeData = (DOMSubTreeData)paramData;
      if (dOMSubTreeData.excludeComments())
        try {
          this.apacheCanonicalizer = Canonicalizer.getInstance("http://www.w3.org/2006/12/xml-c14n11");
        } catch (InvalidCanonicalizerException invalidCanonicalizerException) {
          throw new TransformException("Couldn't find Canonicalizer for: http://www.w3.org/2006/12/xml-c14n11: " + invalidCanonicalizerException.getMessage(), invalidCanonicalizerException);
        }  
    } 
    return canonicalize(paramData, paramXMLCryptoContext);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMCanonicalXMLC14N11Method.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */