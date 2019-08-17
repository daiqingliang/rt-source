package com.sun.xml.internal.ws.streaming;

import com.sun.istack.internal.FinalArrayList;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.XMLStreamException2;
import com.sun.xml.internal.ws.util.xml.DummyLocation;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class DOMStreamReader implements XMLStreamReader, NamespaceContext {
  protected Node _current;
  
  private Node _start;
  
  private NamedNodeMap _namedNodeMap;
  
  protected String wholeText;
  
  private final FinalArrayList<Attr> _currentAttributes = new FinalArrayList();
  
  protected Scope[] scopes = new Scope[8];
  
  protected int depth = 0;
  
  protected int _state;
  
  public DOMStreamReader() {}
  
  public DOMStreamReader(Node paramNode) { setCurrentNode(paramNode); }
  
  public void setCurrentNode(Node paramNode) {
    this.scopes[0] = new Scope(null);
    this.depth = 0;
    this._start = this._current = paramNode;
    this._state = 7;
  }
  
  public void close() {}
  
  protected void splitAttributes() {
    this._currentAttributes.clear();
    Scope scope = allocateScope();
    this._namedNodeMap = this._current.getAttributes();
    if (this._namedNodeMap != null) {
      int j = this._namedNodeMap.getLength();
      for (byte b = 0; b < j; b++) {
        Attr attr = (Attr)this._namedNodeMap.item(b);
        String str = attr.getNodeName();
        if (str.startsWith("xmlns:") || str.equals("xmlns")) {
          scope.currentNamespaces.add(attr);
        } else {
          this._currentAttributes.add(attr);
        } 
      } 
    } 
    ensureNs(this._current);
    for (int i = this._currentAttributes.size() - 1; i >= 0; i--) {
      Attr attr = (Attr)this._currentAttributes.get(i);
      if (fixNull(attr.getNamespaceURI()).length() > 0)
        ensureNs(attr); 
    } 
  }
  
  private void ensureNs(Node paramNode) {
    String str1 = fixNull(paramNode.getPrefix());
    String str2 = fixNull(paramNode.getNamespaceURI());
    Scope scope = this.scopes[this.depth];
    String str3 = scope.getNamespaceURI(str1);
    if (str1.length() == 0) {
      str3 = fixNull(str3);
      if (str3.equals(str2))
        return; 
    } else if (str3 != null && str3.equals(str2)) {
      return;
    } 
    if (str1.equals("xml") || str1.equals("xmlns"))
      return; 
    scope.additionalNamespaces.add(str1);
    scope.additionalNamespaces.add(str2);
  }
  
  private Scope allocateScope() {
    if (this.scopes.length == ++this.depth) {
      Scope[] arrayOfScope = new Scope[this.scopes.length * 2];
      System.arraycopy(this.scopes, 0, arrayOfScope, 0, this.scopes.length);
      this.scopes = arrayOfScope;
    } 
    Scope scope = this.scopes[this.depth];
    if (scope == null) {
      scope = this.scopes[this.depth] = new Scope(this.scopes[this.depth - 1]);
    } else {
      scope.reset();
    } 
    return scope;
  }
  
  public int getAttributeCount() {
    if (this._state == 1)
      return this._currentAttributes.size(); 
    throw new IllegalStateException("DOMStreamReader: getAttributeCount() called in illegal state");
  }
  
  public String getAttributeLocalName(int paramInt) {
    if (this._state == 1) {
      String str = ((Attr)this._currentAttributes.get(paramInt)).getLocalName();
      return (str != null) ? str : QName.valueOf(((Attr)this._currentAttributes.get(paramInt)).getNodeName()).getLocalPart();
    } 
    throw new IllegalStateException("DOMStreamReader: getAttributeLocalName() called in illegal state");
  }
  
  public QName getAttributeName(int paramInt) {
    if (this._state == 1) {
      Node node = (Node)this._currentAttributes.get(paramInt);
      String str = node.getLocalName();
      if (str != null) {
        String str1 = node.getPrefix();
        String str2 = node.getNamespaceURI();
        return new QName(fixNull(str2), str, fixNull(str1));
      } 
      return QName.valueOf(node.getNodeName());
    } 
    throw new IllegalStateException("DOMStreamReader: getAttributeName() called in illegal state");
  }
  
  public String getAttributeNamespace(int paramInt) {
    if (this._state == 1) {
      String str = ((Attr)this._currentAttributes.get(paramInt)).getNamespaceURI();
      return fixNull(str);
    } 
    throw new IllegalStateException("DOMStreamReader: getAttributeNamespace() called in illegal state");
  }
  
  public String getAttributePrefix(int paramInt) {
    if (this._state == 1) {
      String str = ((Attr)this._currentAttributes.get(paramInt)).getPrefix();
      return fixNull(str);
    } 
    throw new IllegalStateException("DOMStreamReader: getAttributePrefix() called in illegal state");
  }
  
  public String getAttributeType(int paramInt) {
    if (this._state == 1)
      return "CDATA"; 
    throw new IllegalStateException("DOMStreamReader: getAttributeType() called in illegal state");
  }
  
  public String getAttributeValue(int paramInt) {
    if (this._state == 1)
      return ((Attr)this._currentAttributes.get(paramInt)).getNodeValue(); 
    throw new IllegalStateException("DOMStreamReader: getAttributeValue() called in illegal state");
  }
  
  public String getAttributeValue(String paramString1, String paramString2) {
    if (this._state == 1) {
      if (this._namedNodeMap != null) {
        Node node = this._namedNodeMap.getNamedItemNS(paramString1, paramString2);
        return (node != null) ? node.getNodeValue() : null;
      } 
      return null;
    } 
    throw new IllegalStateException("DOMStreamReader: getAttributeValue() called in illegal state");
  }
  
  public String getCharacterEncodingScheme() { return null; }
  
  public String getElementText() { throw new RuntimeException("DOMStreamReader: getElementText() not implemented"); }
  
  public String getEncoding() { return null; }
  
  public int getEventType() { return this._state; }
  
  public String getLocalName() {
    if (this._state == 1 || this._state == 2) {
      String str = this._current.getLocalName();
      return (str != null) ? str : QName.valueOf(this._current.getNodeName()).getLocalPart();
    } 
    if (this._state == 9)
      return this._current.getNodeName(); 
    throw new IllegalStateException("DOMStreamReader: getAttributeValue() called in illegal state");
  }
  
  public Location getLocation() { return DummyLocation.INSTANCE; }
  
  public QName getName() {
    if (this._state == 1 || this._state == 2) {
      String str = this._current.getLocalName();
      if (str != null) {
        String str1 = this._current.getPrefix();
        String str2 = this._current.getNamespaceURI();
        return new QName(fixNull(str2), str, fixNull(str1));
      } 
      return QName.valueOf(this._current.getNodeName());
    } 
    throw new IllegalStateException("DOMStreamReader: getName() called in illegal state");
  }
  
  public NamespaceContext getNamespaceContext() { return this; }
  
  private Scope getCheckedScope() {
    if (this._state == 1 || this._state == 2)
      return this.scopes[this.depth]; 
    throw new IllegalStateException("DOMStreamReader: neither on START_ELEMENT nor END_ELEMENT");
  }
  
  public int getNamespaceCount() { return getCheckedScope().getNamespaceCount(); }
  
  public String getNamespacePrefix(int paramInt) { return getCheckedScope().getNamespacePrefix(paramInt); }
  
  public String getNamespaceURI(int paramInt) { return getCheckedScope().getNamespaceURI(paramInt); }
  
  public String getNamespaceURI() {
    if (this._state == 1 || this._state == 2) {
      String str = this._current.getNamespaceURI();
      return fixNull(str);
    } 
    return null;
  }
  
  public String getNamespaceURI(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("DOMStreamReader: getNamespaceURI(String) call with a null prefix"); 
    if (paramString.equals("xml"))
      return "http://www.w3.org/XML/1998/namespace"; 
    if (paramString.equals("xmlns"))
      return "http://www.w3.org/2000/xmlns/"; 
    String str1 = this.scopes[this.depth].getNamespaceURI(paramString);
    if (str1 != null)
      return str1; 
    Node node = findRootElement();
    String str2 = (paramString.length() == 0) ? "xmlns" : ("xmlns:" + paramString);
    while (node.getNodeType() != 9) {
      NamedNodeMap namedNodeMap = node.getAttributes();
      Attr attr = (Attr)namedNodeMap.getNamedItem(str2);
      if (attr != null)
        return attr.getValue(); 
      node = node.getParentNode();
    } 
    return null;
  }
  
  public String getPrefix(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("DOMStreamReader: getPrefix(String) call with a null namespace URI"); 
    if (paramString.equals("http://www.w3.org/XML/1998/namespace"))
      return "xml"; 
    if (paramString.equals("http://www.w3.org/2000/xmlns/"))
      return "xmlns"; 
    String str = this.scopes[this.depth].getPrefix(paramString);
    if (str != null)
      return str; 
    for (Node node = findRootElement(); node.getNodeType() != 9; node = node.getParentNode()) {
      NamedNodeMap namedNodeMap = node.getAttributes();
      for (int i = namedNodeMap.getLength() - 1; i >= 0; i--) {
        Attr attr = (Attr)namedNodeMap.item(i);
        str = getPrefixForAttr(attr, paramString);
        if (str != null)
          return str; 
      } 
    } 
    return null;
  }
  
  private Node findRootElement() {
    short s;
    Node node;
    for (node = this._start; (s = node.getNodeType()) != 9 && s != 1; node = node.getParentNode());
    return node;
  }
  
  private static String getPrefixForAttr(Attr paramAttr, String paramString) {
    String str = paramAttr.getNodeName();
    if (!str.startsWith("xmlns:") && !str.equals("xmlns"))
      return null; 
    if (paramAttr.getValue().equals(paramString)) {
      if (str.equals("xmlns"))
        return ""; 
      String str1 = paramAttr.getLocalName();
      return (str1 != null) ? str1 : QName.valueOf(str).getLocalPart();
    } 
    return null;
  }
  
  public Iterator getPrefixes(String paramString) {
    String str = getPrefix(paramString);
    return (str == null) ? Collections.emptyList().iterator() : Collections.singletonList(str).iterator();
  }
  
  public String getPIData() { return (this._state == 3) ? ((ProcessingInstruction)this._current).getData() : null; }
  
  public String getPITarget() { return (this._state == 3) ? ((ProcessingInstruction)this._current).getTarget() : null; }
  
  public String getPrefix() {
    if (this._state == 1 || this._state == 2) {
      String str = this._current.getPrefix();
      return fixNull(str);
    } 
    return null;
  }
  
  public Object getProperty(String paramString) throws IllegalArgumentException { return null; }
  
  public String getText() {
    if (this._state == 4)
      return this.wholeText; 
    if (this._state == 12 || this._state == 5 || this._state == 9)
      return this._current.getNodeValue(); 
    throw new IllegalStateException("DOMStreamReader: getTextLength() called in illegal state");
  }
  
  public char[] getTextCharacters() { return getText().toCharArray(); }
  
  public int getTextCharacters(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3) throws XMLStreamException {
    String str = getText();
    int i = Math.min(paramInt3, str.length() - paramInt1);
    str.getChars(paramInt1, paramInt1 + i, paramArrayOfChar, paramInt2);
    return i;
  }
  
  public int getTextLength() { return getText().length(); }
  
  public int getTextStart() {
    if (this._state == 4 || this._state == 12 || this._state == 5 || this._state == 9)
      return 0; 
    throw new IllegalStateException("DOMStreamReader: getTextStart() called in illegal state");
  }
  
  public String getVersion() { return null; }
  
  public boolean hasName() { return (this._state == 1 || this._state == 2); }
  
  public boolean hasNext() { return (this._state != 8); }
  
  public boolean hasText() { return (this._state == 4 || this._state == 12 || this._state == 5 || this._state == 9) ? ((getText().trim().length() > 0)) : false; }
  
  public boolean isAttributeSpecified(int paramInt) { return false; }
  
  public boolean isCharacters() { return (this._state == 4); }
  
  public boolean isEndElement() { return (this._state == 2); }
  
  public boolean isStandalone() { return true; }
  
  public boolean isStartElement() { return (this._state == 1); }
  
  public boolean isWhiteSpace() { return (this._state == 4 || this._state == 12) ? ((getText().trim().length() == 0)) : false; }
  
  private static int mapNodeTypeToState(int paramInt) {
    switch (paramInt) {
      case 4:
        return 12;
      case 8:
        return 5;
      case 1:
        return 1;
      case 6:
        return 15;
      case 5:
        return 9;
      case 12:
        return 14;
      case 7:
        return 3;
      case 3:
        return 4;
    } 
    throw new RuntimeException("DOMStreamReader: Unexpected node type");
  }
  
  public int next() {
    int i;
    while (true) {
      Text text;
      Node node;
      i = _next();
      switch (i) {
        case 4:
          node = this._current.getPreviousSibling();
          if (node != null && node.getNodeType() == 3)
            continue; 
          text = (Text)this._current;
          this.wholeText = text.getWholeText();
          if (this.wholeText.length() == 0)
            continue; 
          return 4;
        case 1:
          splitAttributes();
          return 1;
      } 
      break;
    } 
    return i;
  }
  
  protected int _next() {
    Node node2;
    Node node1;
    switch (this._state) {
      case 8:
        throw new IllegalStateException("DOMStreamReader: Calling next() at END_DOCUMENT");
      case 7:
        if (this._current.getNodeType() == 1)
          return this._state = 1; 
        node1 = this._current.getFirstChild();
        if (node1 == null)
          return this._state = 8; 
        this._current = node1;
        return this._state = mapNodeTypeToState(this._current.getNodeType());
      case 1:
        node1 = this._current.getFirstChild();
        if (node1 == null)
          return this._state = 2; 
        this._current = node1;
        return this._state = mapNodeTypeToState(this._current.getNodeType());
      case 2:
        this.depth--;
      case 3:
      case 4:
      case 5:
      case 9:
      case 12:
        if (this._current == this._start)
          return this._state = 8; 
        node2 = this._current.getNextSibling();
        if (node2 == null) {
          this._current = this._current.getParentNode();
          this._state = (this._current == null || this._current.getNodeType() == 9) ? 8 : 2;
          return this._state;
        } 
        this._current = node2;
        return this._state = mapNodeTypeToState(this._current.getNodeType());
    } 
    throw new RuntimeException("DOMStreamReader: Unexpected internal state");
  }
  
  public int nextTag() {
    int i;
    for (i = next(); (i == 4 && isWhiteSpace()) || (i == 12 && isWhiteSpace()) || i == 6 || i == 3 || i == 5; i = next());
    if (i != 1 && i != 2)
      throw new XMLStreamException2("DOMStreamReader: Expected start or end tag"); 
    return i;
  }
  
  public void require(int paramInt, String paramString1, String paramString2) throws XMLStreamException {
    if (paramInt != this._state)
      throw new XMLStreamException2("DOMStreamReader: Required event type not found"); 
    if (paramString1 != null && !paramString1.equals(getNamespaceURI()))
      throw new XMLStreamException2("DOMStreamReader: Required namespaceURI not found"); 
    if (paramString2 != null && !paramString2.equals(getLocalName()))
      throw new XMLStreamException2("DOMStreamReader: Required localName not found"); 
  }
  
  public boolean standaloneSet() { return true; }
  
  private static void displayDOM(Node paramNode, OutputStream paramOutputStream) {
    try {
      System.out.println("\n====\n");
      XmlUtil.newTransformer().transform(new DOMSource(paramNode), new StreamResult(paramOutputStream));
      System.out.println("\n====\n");
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  private static void verifyDOMIntegrity(Node paramNode) {
    byte b2;
    byte b1;
    NodeList nodeList;
    NamedNodeMap namedNodeMap;
    switch (paramNode.getNodeType()) {
      case 1:
      case 2:
        if (paramNode.getLocalName() == null) {
          System.out.println("WARNING: DOM level 1 node found");
          System.out.println(" -> node.getNodeName() = " + paramNode.getNodeName());
          System.out.println(" -> node.getNamespaceURI() = " + paramNode.getNamespaceURI());
          System.out.println(" -> node.getLocalName() = " + paramNode.getLocalName());
          System.out.println(" -> node.getPrefix() = " + paramNode.getPrefix());
        } 
        if (paramNode.getNodeType() == 2)
          return; 
        namedNodeMap = paramNode.getAttributes();
        for (b1 = 0; b1 < namedNodeMap.getLength(); b1++)
          verifyDOMIntegrity(namedNodeMap.item(b1)); 
      case 9:
        nodeList = paramNode.getChildNodes();
        for (b2 = 0; b2 < nodeList.getLength(); b2++)
          verifyDOMIntegrity(nodeList.item(b2)); 
        break;
    } 
  }
  
  private static String fixNull(String paramString) { return (paramString == null) ? "" : paramString; }
  
  protected static final class Scope {
    final Scope parent;
    
    final FinalArrayList<Attr> currentNamespaces = new FinalArrayList();
    
    final FinalArrayList<String> additionalNamespaces = new FinalArrayList();
    
    Scope(Scope param1Scope) { this.parent = param1Scope; }
    
    void reset() {
      this.currentNamespaces.clear();
      this.additionalNamespaces.clear();
    }
    
    int getNamespaceCount() { return this.currentNamespaces.size() + this.additionalNamespaces.size() / 2; }
    
    String getNamespacePrefix(int param1Int) {
      int i = this.currentNamespaces.size();
      if (param1Int < i) {
        Attr attr = (Attr)this.currentNamespaces.get(param1Int);
        String str = attr.getLocalName();
        if (str == null)
          str = QName.valueOf(attr.getNodeName()).getLocalPart(); 
        return str.equals("xmlns") ? null : str;
      } 
      return (String)this.additionalNamespaces.get((param1Int - i) * 2);
    }
    
    String getNamespaceURI(int param1Int) {
      int i = this.currentNamespaces.size();
      return (param1Int < i) ? ((Attr)this.currentNamespaces.get(param1Int)).getValue() : (String)this.additionalNamespaces.get((param1Int - i) * 2 + 1);
    }
    
    String getPrefix(String param1String) {
      for (Scope scope = this; scope != null; scope = scope.parent) {
        int i;
        for (i = scope.currentNamespaces.size() - 1; i >= 0; i--) {
          String str = DOMStreamReader.getPrefixForAttr((Attr)scope.currentNamespaces.get(i), param1String);
          if (str != null)
            return str; 
        } 
        for (i = scope.additionalNamespaces.size() - 2; i >= 0; i -= 2) {
          if (((String)scope.additionalNamespaces.get(i + 1)).equals(param1String))
            return (String)scope.additionalNamespaces.get(i); 
        } 
      } 
      return null;
    }
    
    String getNamespaceURI(@NotNull String param1String) {
      String str = (param1String.length() == 0) ? "xmlns" : ("xmlns:" + param1String);
      for (Scope scope = this; scope != null; scope = scope.parent) {
        int i;
        for (i = scope.currentNamespaces.size() - 1; i >= 0; i--) {
          Attr attr = (Attr)scope.currentNamespaces.get(i);
          if (attr.getNodeName().equals(str))
            return attr.getValue(); 
        } 
        for (i = scope.additionalNamespaces.size() - 2; i >= 0; i -= 2) {
          if (((String)scope.additionalNamespaces.get(i)).equals(param1String))
            return (String)scope.additionalNamespaces.get(i + 1); 
        } 
      } 
      return null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\streaming\DOMStreamReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */