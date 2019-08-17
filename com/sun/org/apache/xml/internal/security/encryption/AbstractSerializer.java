package com.sun.org.apache.xml.internal.security.encryption;

import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractSerializer implements Serializer {
  protected Canonicalizer canon;
  
  public void setCanonicalizer(Canonicalizer paramCanonicalizer) { this.canon = paramCanonicalizer; }
  
  public String serialize(Element paramElement) throws Exception { return canonSerialize(paramElement); }
  
  public byte[] serializeToByteArray(Element paramElement) throws Exception { return canonSerializeToByteArray(paramElement); }
  
  public String serialize(NodeList paramNodeList) throws Exception {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    this.canon.setWriter(byteArrayOutputStream);
    this.canon.notReset();
    for (byte b = 0; b < paramNodeList.getLength(); b++)
      this.canon.canonicalizeSubtree(paramNodeList.item(b)); 
    String str = byteArrayOutputStream.toString("UTF-8");
    byteArrayOutputStream.reset();
    return str;
  }
  
  public byte[] serializeToByteArray(NodeList paramNodeList) throws Exception {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    this.canon.setWriter(byteArrayOutputStream);
    this.canon.notReset();
    for (byte b = 0; b < paramNodeList.getLength(); b++)
      this.canon.canonicalizeSubtree(paramNodeList.item(b)); 
    return byteArrayOutputStream.toByteArray();
  }
  
  public String canonSerialize(Node paramNode) throws Exception {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    this.canon.setWriter(byteArrayOutputStream);
    this.canon.notReset();
    this.canon.canonicalizeSubtree(paramNode);
    String str = byteArrayOutputStream.toString("UTF-8");
    byteArrayOutputStream.reset();
    return str;
  }
  
  public byte[] canonSerializeToByteArray(Node paramNode) throws Exception {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    this.canon.setWriter(byteArrayOutputStream);
    this.canon.notReset();
    this.canon.canonicalizeSubtree(paramNode);
    return byteArrayOutputStream.toByteArray();
  }
  
  public abstract Node deserialize(String paramString, Node paramNode) throws XMLEncryptionException;
  
  public abstract Node deserialize(byte[] paramArrayOfByte, Node paramNode) throws XMLEncryptionException;
  
  protected static byte[] createContext(byte[] paramArrayOfByte, Node paramNode) throws XMLEncryptionException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try {
      OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream, "UTF-8");
      outputStreamWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><dummy");
      HashMap hashMap = new HashMap();
      for (Node node = paramNode; node != null; node = node.getParentNode()) {
        NamedNodeMap namedNodeMap = node.getAttributes();
        if (namedNodeMap != null)
          for (byte b = 0; b < namedNodeMap.getLength(); b++) {
            Node node1 = namedNodeMap.item(b);
            String str = node1.getNodeName();
            if ((str.equals("xmlns") || str.startsWith("xmlns:")) && !hashMap.containsKey(node1.getNodeName())) {
              outputStreamWriter.write(" ");
              outputStreamWriter.write(str);
              outputStreamWriter.write("=\"");
              outputStreamWriter.write(node1.getNodeValue());
              outputStreamWriter.write("\"");
              hashMap.put(str, node1.getNodeValue());
            } 
          }  
      } 
      outputStreamWriter.write(">");
      outputStreamWriter.flush();
      byteArrayOutputStream.write(paramArrayOfByte);
      outputStreamWriter.write("</dummy>");
      outputStreamWriter.close();
      return byteArrayOutputStream.toByteArray();
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new XMLEncryptionException("empty", unsupportedEncodingException);
    } catch (IOException iOException) {
      throw new XMLEncryptionException("empty", iOException);
    } 
  }
  
  protected static String createContext(String paramString, Node paramNode) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><dummy");
    HashMap hashMap = new HashMap();
    for (Node node = paramNode; node != null; node = node.getParentNode()) {
      NamedNodeMap namedNodeMap = node.getAttributes();
      if (namedNodeMap != null)
        for (byte b = 0; b < namedNodeMap.getLength(); b++) {
          Node node1 = namedNodeMap.item(b);
          String str = node1.getNodeName();
          if ((str.equals("xmlns") || str.startsWith("xmlns:")) && !hashMap.containsKey(node1.getNodeName())) {
            stringBuilder.append(" " + str + "=\"" + node1.getNodeValue() + "\"");
            hashMap.put(str, node1.getNodeValue());
          } 
        }  
    } 
    stringBuilder.append(">" + paramString + "</dummy>");
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\encryption\AbstractSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */