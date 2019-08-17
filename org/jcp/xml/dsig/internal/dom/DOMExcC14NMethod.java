package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.List;
import javax.xml.crypto.Data;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import org.w3c.dom.Element;

public final class DOMExcC14NMethod extends ApacheCanonicalizer {
  public void init(TransformParameterSpec paramTransformParameterSpec) throws InvalidAlgorithmParameterException {
    if (paramTransformParameterSpec != null) {
      if (!(paramTransformParameterSpec instanceof ExcC14NParameterSpec))
        throw new InvalidAlgorithmParameterException("params must be of type ExcC14NParameterSpec"); 
      this.params = (C14NMethodParameterSpec)paramTransformParameterSpec;
    } 
  }
  
  public void init(XMLStructure paramXMLStructure, XMLCryptoContext paramXMLCryptoContext) throws InvalidAlgorithmParameterException {
    super.init(paramXMLStructure, paramXMLCryptoContext);
    Element element = DOMUtils.getFirstChildElement(this.transformElem);
    if (element == null) {
      this.params = null;
      this.inclusiveNamespaces = null;
      return;
    } 
    unmarshalParams(element);
  }
  
  private void unmarshalParams(Element paramElement) {
    String str = paramElement.getAttributeNS(null, "PrefixList");
    this.inclusiveNamespaces = str;
    int i = 0;
    int j = str.indexOf(' ');
    ArrayList arrayList = new ArrayList();
    while (j != -1) {
      arrayList.add(str.substring(i, j));
      i = j + 1;
      j = str.indexOf(' ', i);
    } 
    if (i <= str.length())
      arrayList.add(str.substring(i)); 
    this.params = new ExcC14NParameterSpec(arrayList);
  }
  
  public void marshalParams(XMLStructure paramXMLStructure, XMLCryptoContext paramXMLCryptoContext) throws InvalidAlgorithmParameterException {
    super.marshalParams(paramXMLStructure, paramXMLCryptoContext);
    AlgorithmParameterSpec algorithmParameterSpec = getParameterSpec();
    if (algorithmParameterSpec == null)
      return; 
    String str = DOMUtils.getNSPrefix(paramXMLCryptoContext, "http://www.w3.org/2001/10/xml-exc-c14n#");
    Element element = DOMUtils.createElement(this.ownerDoc, "InclusiveNamespaces", "http://www.w3.org/2001/10/xml-exc-c14n#", str);
    if (str == null || str.length() == 0) {
      element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2001/10/xml-exc-c14n#");
    } else {
      element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + str, "http://www.w3.org/2001/10/xml-exc-c14n#");
    } 
    ExcC14NParameterSpec excC14NParameterSpec = (ExcC14NParameterSpec)algorithmParameterSpec;
    StringBuffer stringBuffer = new StringBuffer("");
    List list = excC14NParameterSpec.getPrefixList();
    byte b = 0;
    int i = list.size();
    while (b < i) {
      stringBuffer.append((String)list.get(b));
      if (b < i - 1)
        stringBuffer.append(" "); 
      b++;
    } 
    DOMUtils.setAttribute(element, "PrefixList", stringBuffer.toString());
    this.inclusiveNamespaces = stringBuffer.toString();
    this.transformElem.appendChild(element);
  }
  
  public String getParamsNSURI() { return "http://www.w3.org/2001/10/xml-exc-c14n#"; }
  
  public Data transform(Data paramData, XMLCryptoContext paramXMLCryptoContext) throws TransformException {
    if (paramData instanceof DOMSubTreeData) {
      DOMSubTreeData dOMSubTreeData = (DOMSubTreeData)paramData;
      if (dOMSubTreeData.excludeComments())
        try {
          this.apacheCanonicalizer = Canonicalizer.getInstance("http://www.w3.org/2001/10/xml-exc-c14n#");
        } catch (InvalidCanonicalizerException invalidCanonicalizerException) {
          throw new TransformException("Couldn't find Canonicalizer for: http://www.w3.org/2001/10/xml-exc-c14n#: " + invalidCanonicalizerException.getMessage(), invalidCanonicalizerException);
        }  
    } 
    return canonicalize(paramData, paramXMLCryptoContext);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMExcC14NMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */