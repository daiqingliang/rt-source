package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.keyinfo.PGPData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class DOMPGPData extends DOMStructure implements PGPData {
  private final byte[] keyId;
  
  private final byte[] keyPacket;
  
  private final List<XMLStructure> externalElements;
  
  public DOMPGPData(byte[] paramArrayOfByte, List<? extends XMLStructure> paramList) {
    if (paramArrayOfByte == null)
      throw new NullPointerException("keyPacket cannot be null"); 
    if (paramList == null || paramList.isEmpty()) {
      this.externalElements = Collections.emptyList();
    } else {
      this.externalElements = Collections.unmodifiableList(new ArrayList(paramList));
      byte b = 0;
      int i = this.externalElements.size();
      while (b < i) {
        if (!(this.externalElements.get(b) instanceof XMLStructure))
          throw new ClassCastException("other[" + b + "] is not a valid PGPData type"); 
        b++;
      } 
    } 
    this.keyPacket = (byte[])paramArrayOfByte.clone();
    checkKeyPacket(paramArrayOfByte);
    this.keyId = null;
  }
  
  public DOMPGPData(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, List<? extends XMLStructure> paramList) {
    if (paramArrayOfByte1 == null)
      throw new NullPointerException("keyId cannot be null"); 
    if (paramArrayOfByte1.length != 8)
      throw new IllegalArgumentException("keyId must be 8 bytes long"); 
    if (paramList == null || paramList.isEmpty()) {
      this.externalElements = Collections.emptyList();
    } else {
      this.externalElements = Collections.unmodifiableList(new ArrayList(paramList));
      byte b = 0;
      int i = this.externalElements.size();
      while (b < i) {
        if (!(this.externalElements.get(b) instanceof XMLStructure))
          throw new ClassCastException("other[" + b + "] is not a valid PGPData type"); 
        b++;
      } 
    } 
    this.keyId = (byte[])paramArrayOfByte1.clone();
    this.keyPacket = (paramArrayOfByte2 == null) ? null : (byte[])paramArrayOfByte2.clone();
    if (paramArrayOfByte2 != null)
      checkKeyPacket(paramArrayOfByte2); 
  }
  
  public DOMPGPData(Element paramElement) throws MarshalException {
    byte[] arrayOfByte1 = null;
    byte[] arrayOfByte2 = null;
    NodeList nodeList = paramElement.getChildNodes();
    int i = nodeList.getLength();
    ArrayList arrayList = new ArrayList(i);
    for (byte b = 0; b < i; b++) {
      Node node = nodeList.item(b);
      if (node.getNodeType() == 1) {
        Element element = (Element)node;
        String str = element.getLocalName();
        try {
          if (str.equals("PGPKeyID")) {
            arrayOfByte1 = Base64.decode(element);
          } else if (str.equals("PGPKeyPacket")) {
            arrayOfByte2 = Base64.decode(element);
          } else {
            arrayList.add(new DOMStructure(element));
          } 
        } catch (Base64DecodingException base64DecodingException) {
          throw new MarshalException(base64DecodingException);
        } 
      } 
    } 
    this.keyId = arrayOfByte1;
    this.keyPacket = arrayOfByte2;
    this.externalElements = Collections.unmodifiableList(arrayList);
  }
  
  public byte[] getKeyId() { return (this.keyId == null) ? null : (byte[])this.keyId.clone(); }
  
  public byte[] getKeyPacket() { return (this.keyPacket == null) ? null : (byte[])this.keyPacket.clone(); }
  
  public List getExternalElements() { return this.externalElements; }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException {
    Document document = DOMUtils.getOwnerDocument(paramNode);
    Element element = DOMUtils.createElement(document, "PGPData", "http://www.w3.org/2000/09/xmldsig#", paramString);
    if (this.keyId != null) {
      Element element1 = DOMUtils.createElement(document, "PGPKeyID", "http://www.w3.org/2000/09/xmldsig#", paramString);
      element1.appendChild(document.createTextNode(Base64.encode(this.keyId)));
      element.appendChild(element1);
    } 
    if (this.keyPacket != null) {
      Element element1 = DOMUtils.createElement(document, "PGPKeyPacket", "http://www.w3.org/2000/09/xmldsig#", paramString);
      element1.appendChild(document.createTextNode(Base64.encode(this.keyPacket)));
      element.appendChild(element1);
    } 
    for (XMLStructure xMLStructure : this.externalElements)
      DOMUtils.appendChild(element, ((DOMStructure)xMLStructure).getNode()); 
    paramNode.appendChild(element);
  }
  
  private void checkKeyPacket(byte[] paramArrayOfByte) {
    if (paramArrayOfByte.length < 3)
      throw new IllegalArgumentException("keypacket must be at least 3 bytes long"); 
    byte b = paramArrayOfByte[0];
    if ((b & 0x80) != 128)
      throw new IllegalArgumentException("keypacket tag is invalid: bit 7 is not set"); 
    if ((b & 0x40) != 64)
      throw new IllegalArgumentException("old keypacket tag format is unsupported"); 
    if ((b & 0x6) != 6 && (b & 0xE) != 14 && (b & 0x5) != 5 && (b & 0x7) != 7)
      throw new IllegalArgumentException("keypacket tag is invalid: must be 6, 14, 5, or 7"); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMPGPData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */