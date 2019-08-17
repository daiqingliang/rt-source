package com.sun.xml.internal.fastinfoset.dom;

import com.sun.xml.internal.fastinfoset.Encoder;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.internal.fastinfoset.util.NamespaceContextImplementation;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import java.io.IOException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMDocumentSerializer extends Encoder {
  protected NamespaceContextImplementation _namespaceScopeContext = new NamespaceContextImplementation();
  
  protected Node[] _attributes = new Node[32];
  
  public final void serialize(Node paramNode) throws IOException {
    switch (paramNode.getNodeType()) {
      case 9:
        serialize((Document)paramNode);
        break;
      case 1:
        serializeElementAsDocument(paramNode);
        break;
      case 8:
        serializeComment(paramNode);
        break;
      case 7:
        serializeProcessingInstruction(paramNode);
        break;
    } 
  }
  
  public final void serialize(Document paramDocument) throws IOException {
    reset();
    encodeHeader(false);
    encodeInitialVocabulary();
    NodeList nodeList = paramDocument.getChildNodes();
    for (byte b = 0; b < nodeList.getLength(); b++) {
      Node node = nodeList.item(b);
      switch (node.getNodeType()) {
        case 1:
          serializeElement(node);
          break;
        case 8:
          serializeComment(node);
          break;
        case 7:
          serializeProcessingInstruction(node);
          break;
      } 
    } 
    encodeDocumentTermination();
  }
  
  protected final void serializeElementAsDocument(Node paramNode) throws IOException {
    reset();
    encodeHeader(false);
    encodeInitialVocabulary();
    serializeElement(paramNode);
    encodeDocumentTermination();
  }
  
  protected final void serializeElement(Node paramNode) throws IOException {
    encodeTermination();
    byte b = 0;
    this._namespaceScopeContext.pushContext();
    if (paramNode.hasAttributes()) {
      NamedNodeMap namedNodeMap = paramNode.getAttributes();
      for (byte b1 = 0; b1 < namedNodeMap.getLength(); b1++) {
        Node node = namedNodeMap.item(b1);
        String str = node.getNamespaceURI();
        if (str != null && str.equals("http://www.w3.org/2000/xmlns/")) {
          String str4 = node.getLocalName();
          String str5 = node.getNodeValue();
          if (str4 == "xmlns" || str4.equals("xmlns"))
            str4 = ""; 
          this._namespaceScopeContext.declarePrefix(str4, str5);
        } else {
          if (b == this._attributes.length) {
            Node[] arrayOfNode = new Node[b * 3 / 2 + 1];
            System.arraycopy(this._attributes, 0, arrayOfNode, 0, b);
            this._attributes = arrayOfNode;
          } 
          this._attributes[b++] = node;
          String str4 = node.getNamespaceURI();
          String str5 = node.getPrefix();
          if (str5 != null && !this._namespaceScopeContext.getNamespaceURI(str5).equals(str4))
            this._namespaceScopeContext.declarePrefix(str5, str4); 
        } 
      } 
    } 
    String str1 = paramNode.getNamespaceURI();
    String str2 = paramNode.getPrefix();
    if (str2 == null)
      str2 = ""; 
    if (str1 != null && !this._namespaceScopeContext.getNamespaceURI(str2).equals(str1))
      this._namespaceScopeContext.declarePrefix(str2, str1); 
    if (!this._namespaceScopeContext.isCurrentContextEmpty()) {
      if (b > 0) {
        write(120);
      } else {
        write(56);
      } 
      for (int i = this._namespaceScopeContext.getCurrentContextStartIndex(); i < this._namespaceScopeContext.getCurrentContextEndIndex(); i++) {
        String str4 = this._namespaceScopeContext.getPrefix(i);
        String str5 = this._namespaceScopeContext.getNamespaceURI(i);
        encodeNamespaceAttribute(str4, str5);
      } 
      write(240);
      this._b = 0;
    } else {
      this._b = (b > 0) ? 64 : 0;
    } 
    String str3 = str1;
    str3 = (str3 == null) ? "" : str3;
    encodeElement(str3, paramNode.getNodeName(), paramNode.getLocalName());
    if (b > 0) {
      for (byte b1 = 0; b1 < b; b1++) {
        Node node = this._attributes[b1];
        this._attributes[b1] = null;
        str3 = node.getNamespaceURI();
        str3 = (str3 == null) ? "" : str3;
        encodeAttribute(str3, node.getNodeName(), node.getLocalName());
        String str = node.getNodeValue();
        boolean bool = isAttributeValueLengthMatchesLimit(str.length());
        encodeNonIdentifyingStringOnFirstBit(str, this._v.attributeValue, bool, false);
      } 
      this._b = 240;
      this._terminate = true;
    } 
    if (paramNode.hasChildNodes()) {
      NodeList nodeList = paramNode.getChildNodes();
      for (byte b1 = 0; b1 < nodeList.getLength(); b1++) {
        Node node = nodeList.item(b1);
        switch (node.getNodeType()) {
          case 1:
            serializeElement(node);
            break;
          case 3:
            serializeText(node);
            break;
          case 4:
            serializeCDATA(node);
            break;
          case 8:
            serializeComment(node);
            break;
          case 7:
            serializeProcessingInstruction(node);
            break;
        } 
      } 
    } 
    encodeElementTermination();
    this._namespaceScopeContext.popContext();
  }
  
  protected final void serializeText(Node paramNode) throws IOException {
    String str = paramNode.getNodeValue();
    int i = (str != null) ? str.length() : 0;
    if (i == 0)
      return; 
    if (i < this._charBuffer.length) {
      str.getChars(0, i, this._charBuffer, 0);
      if (getIgnoreWhiteSpaceTextContent() && isWhiteSpace(this._charBuffer, 0, i))
        return; 
      encodeTermination();
      encodeCharacters(this._charBuffer, 0, i);
    } else {
      char[] arrayOfChar = str.toCharArray();
      if (getIgnoreWhiteSpaceTextContent() && isWhiteSpace(arrayOfChar, 0, i))
        return; 
      encodeTermination();
      encodeCharactersNoClone(arrayOfChar, 0, i);
    } 
  }
  
  protected final void serializeCDATA(Node paramNode) throws IOException {
    String str = paramNode.getNodeValue();
    int i = (str != null) ? str.length() : 0;
    if (i == 0)
      return; 
    char[] arrayOfChar = str.toCharArray();
    if (getIgnoreWhiteSpaceTextContent() && isWhiteSpace(arrayOfChar, 0, i))
      return; 
    encodeTermination();
    try {
      encodeCIIBuiltInAlgorithmDataAsCDATA(arrayOfChar, 0, i);
    } catch (FastInfosetException fastInfosetException) {
      throw new IOException("");
    } 
  }
  
  protected final void serializeComment(Node paramNode) throws IOException {
    if (getIgnoreComments())
      return; 
    encodeTermination();
    String str = paramNode.getNodeValue();
    int i = (str != null) ? str.length() : 0;
    if (i == 0) {
      encodeComment(this._charBuffer, 0, 0);
    } else if (i < this._charBuffer.length) {
      str.getChars(0, i, this._charBuffer, 0);
      encodeComment(this._charBuffer, 0, i);
    } else {
      char[] arrayOfChar = str.toCharArray();
      encodeCommentNoClone(arrayOfChar, 0, i);
    } 
  }
  
  protected final void serializeProcessingInstruction(Node paramNode) throws IOException {
    if (getIgnoreProcesingInstructions())
      return; 
    encodeTermination();
    String str1 = paramNode.getNodeName();
    String str2 = paramNode.getNodeValue();
    encodeProcessingInstruction(str1, str2);
  }
  
  protected final void encodeElement(String paramString1, String paramString2, String paramString3) throws IOException {
    LocalNameQualifiedNamesMap.Entry entry = this._v.elementName.obtainEntry(paramString2);
    if (entry._valueIndex > 0) {
      QualifiedName[] arrayOfQualifiedName = entry._value;
      for (byte b = 0; b < entry._valueIndex; b++) {
        if (paramString1 == (arrayOfQualifiedName[b]).namespaceName || paramString1.equals((arrayOfQualifiedName[b]).namespaceName)) {
          encodeNonZeroIntegerOnThirdBit((arrayOfQualifiedName[b]).index);
          return;
        } 
      } 
    } 
    if (paramString3 != null) {
      encodeLiteralElementQualifiedNameOnThirdBit(paramString1, getPrefixFromQualifiedName(paramString2), paramString3, entry);
    } else {
      encodeLiteralElementQualifiedNameOnThirdBit(paramString1, "", paramString2, entry);
    } 
  }
  
  protected final void encodeAttribute(String paramString1, String paramString2, String paramString3) throws IOException {
    LocalNameQualifiedNamesMap.Entry entry = this._v.attributeName.obtainEntry(paramString2);
    if (entry._valueIndex > 0) {
      QualifiedName[] arrayOfQualifiedName = entry._value;
      for (byte b = 0; b < entry._valueIndex; b++) {
        if (paramString1 == (arrayOfQualifiedName[b]).namespaceName || paramString1.equals((arrayOfQualifiedName[b]).namespaceName)) {
          encodeNonZeroIntegerOnSecondBitFirstBitZero((arrayOfQualifiedName[b]).index);
          return;
        } 
      } 
    } 
    if (paramString3 != null) {
      encodeLiteralAttributeQualifiedNameOnSecondBit(paramString1, getPrefixFromQualifiedName(paramString2), paramString3, entry);
    } else {
      encodeLiteralAttributeQualifiedNameOnSecondBit(paramString1, "", paramString2, entry);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\dom\DOMDocumentSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */