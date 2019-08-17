package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.math.BigInteger;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public final class DOMCryptoBinary extends DOMStructure {
  private final BigInteger bigNum;
  
  private final String value;
  
  public DOMCryptoBinary(BigInteger paramBigInteger) {
    if (paramBigInteger == null)
      throw new NullPointerException("bigNum is null"); 
    this.bigNum = paramBigInteger;
    this.value = Base64.encode(paramBigInteger);
  }
  
  public DOMCryptoBinary(Node paramNode) throws MarshalException {
    this.value = paramNode.getNodeValue();
    try {
      this.bigNum = Base64.decodeBigIntegerFromText((Text)paramNode);
    } catch (Exception exception) {
      throw new MarshalException(exception);
    } 
  }
  
  public BigInteger getBigNum() { return this.bigNum; }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException { paramNode.appendChild(DOMUtils.getOwnerDocument(paramNode).createTextNode(this.value)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMCryptoBinary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */