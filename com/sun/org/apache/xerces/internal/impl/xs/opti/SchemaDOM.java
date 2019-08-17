package com.sun.org.apache.xerces.internal.impl.xs.opti;

import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import java.util.ArrayList;
import java.util.Enumeration;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class SchemaDOM extends DefaultDocument {
  static final int relationsRowResizeFactor = 15;
  
  static final int relationsColResizeFactor = 10;
  
  NodeImpl[][] relations;
  
  ElementImpl parent;
  
  int currLoc;
  
  int nextFreeLoc;
  
  boolean hidden;
  
  boolean inCDATA;
  
  private StringBuffer fAnnotationBuffer = null;
  
  public SchemaDOM() { reset(); }
  
  public ElementImpl startElement(QName paramQName, XMLAttributes paramXMLAttributes, int paramInt1, int paramInt2, int paramInt3) {
    ElementImpl elementImpl = new ElementImpl(paramInt1, paramInt2, paramInt3);
    processElement(paramQName, paramXMLAttributes, elementImpl);
    this.parent = elementImpl;
    return elementImpl;
  }
  
  public ElementImpl emptyElement(QName paramQName, XMLAttributes paramXMLAttributes, int paramInt1, int paramInt2, int paramInt3) {
    ElementImpl elementImpl = new ElementImpl(paramInt1, paramInt2, paramInt3);
    processElement(paramQName, paramXMLAttributes, elementImpl);
    return elementImpl;
  }
  
  public ElementImpl startElement(QName paramQName, XMLAttributes paramXMLAttributes, int paramInt1, int paramInt2) { return startElement(paramQName, paramXMLAttributes, paramInt1, paramInt2, -1); }
  
  public ElementImpl emptyElement(QName paramQName, XMLAttributes paramXMLAttributes, int paramInt1, int paramInt2) { return emptyElement(paramQName, paramXMLAttributes, paramInt1, paramInt2, -1); }
  
  private void processElement(QName paramQName, XMLAttributes paramXMLAttributes, ElementImpl paramElementImpl) {
    paramElementImpl.prefix = paramQName.prefix;
    paramElementImpl.localpart = paramQName.localpart;
    paramElementImpl.rawname = paramQName.rawname;
    paramElementImpl.uri = paramQName.uri;
    paramElementImpl.schemaDOM = this;
    Attr[] arrayOfAttr = new Attr[paramXMLAttributes.getLength()];
    byte b1;
    for (b1 = 0; b1 < paramXMLAttributes.getLength(); b1++)
      arrayOfAttr[b1] = new AttrImpl(paramElementImpl, paramXMLAttributes.getPrefix(b1), paramXMLAttributes.getLocalName(b1), paramXMLAttributes.getQName(b1), paramXMLAttributes.getURI(b1), paramXMLAttributes.getValue(b1)); 
    paramElementImpl.attrs = arrayOfAttr;
    if (this.nextFreeLoc == this.relations.length)
      resizeRelations(); 
    if (this.relations[this.currLoc][false] != this.parent) {
      this.relations[this.nextFreeLoc][0] = this.parent;
      this.currLoc = this.nextFreeLoc++;
    } 
    b1 = 0;
    byte b2 = 1;
    for (b2 = 1; b2 < this.relations[this.currLoc].length; b2++) {
      if (this.relations[this.currLoc][b2] == null) {
        b1 = 1;
        break;
      } 
    } 
    if (b1 == 0)
      resizeRelations(this.currLoc); 
    this.relations[this.currLoc][b2] = paramElementImpl;
    this.parent.parentRow = this.currLoc;
    paramElementImpl.row = this.currLoc;
    paramElementImpl.col = b2;
  }
  
  public void endElement() {
    this.currLoc = this.parent.row;
    this.parent = (ElementImpl)this.relations[this.currLoc][0];
  }
  
  void comment(XMLString paramXMLString) {
    this.fAnnotationBuffer.append("<!--");
    if (paramXMLString.length > 0)
      this.fAnnotationBuffer.append(paramXMLString.ch, paramXMLString.offset, paramXMLString.length); 
    this.fAnnotationBuffer.append("-->");
  }
  
  void processingInstruction(String paramString, XMLString paramXMLString) {
    this.fAnnotationBuffer.append("<?").append(paramString);
    if (paramXMLString.length > 0)
      this.fAnnotationBuffer.append(' ').append(paramXMLString.ch, paramXMLString.offset, paramXMLString.length); 
    this.fAnnotationBuffer.append("?>");
  }
  
  void characters(XMLString paramXMLString) {
    if (!this.inCDATA) {
      StringBuffer stringBuffer = this.fAnnotationBuffer;
      for (int i = paramXMLString.offset; i < paramXMLString.offset + paramXMLString.length; i++) {
        char c = paramXMLString.ch[i];
        if (c == '&') {
          stringBuffer.append("&amp;");
        } else if (c == '<') {
          stringBuffer.append("&lt;");
        } else if (c == '>') {
          stringBuffer.append("&gt;");
        } else if (c == '\r') {
          stringBuffer.append("&#xD;");
        } else {
          stringBuffer.append(c);
        } 
      } 
    } else {
      this.fAnnotationBuffer.append(paramXMLString.ch, paramXMLString.offset, paramXMLString.length);
    } 
  }
  
  void charactersRaw(String paramString) { this.fAnnotationBuffer.append(paramString); }
  
  void endAnnotation(QName paramQName, ElementImpl paramElementImpl) {
    this.fAnnotationBuffer.append("\n</").append(paramQName.rawname).append(">");
    paramElementImpl.fAnnotation = this.fAnnotationBuffer.toString();
    this.fAnnotationBuffer = null;
  }
  
  void endAnnotationElement(QName paramQName) { endAnnotationElement(paramQName.rawname); }
  
  void endAnnotationElement(String paramString) { this.fAnnotationBuffer.append("</").append(paramString).append(">"); }
  
  void endSyntheticAnnotationElement(QName paramQName, boolean paramBoolean) { endSyntheticAnnotationElement(paramQName.rawname, paramBoolean); }
  
  void endSyntheticAnnotationElement(String paramString, boolean paramBoolean) {
    if (paramBoolean) {
      this.fAnnotationBuffer.append("\n</").append(paramString).append(">");
      this.parent.fSyntheticAnnotation = this.fAnnotationBuffer.toString();
      this.fAnnotationBuffer = null;
    } else {
      this.fAnnotationBuffer.append("</").append(paramString).append(">");
    } 
  }
  
  void startAnnotationCDATA() {
    this.inCDATA = true;
    this.fAnnotationBuffer.append("<![CDATA[");
  }
  
  void endAnnotationCDATA() {
    this.fAnnotationBuffer.append("]]>");
    this.inCDATA = false;
  }
  
  private void resizeRelations() {
    NodeImpl[][] arrayOfNodeImpl = new NodeImpl[this.relations.length + 15][];
    System.arraycopy(this.relations, 0, arrayOfNodeImpl, 0, this.relations.length);
    for (int i = this.relations.length; i < arrayOfNodeImpl.length; i++)
      arrayOfNodeImpl[i] = new NodeImpl[10]; 
    this.relations = arrayOfNodeImpl;
  }
  
  private void resizeRelations(int paramInt) {
    NodeImpl[] arrayOfNodeImpl = new NodeImpl[this.relations[paramInt].length + 10];
    System.arraycopy(this.relations[paramInt], 0, arrayOfNodeImpl, 0, this.relations[paramInt].length);
    this.relations[paramInt] = arrayOfNodeImpl;
  }
  
  public void reset() {
    if (this.relations != null)
      for (byte b1 = 0; b1 < this.relations.length; b1++) {
        for (byte b2 = 0; b2 < this.relations[b1].length; b2++)
          this.relations[b1][b2] = null; 
      }  
    this.relations = new NodeImpl[15][];
    this.parent = new ElementImpl(0, 0, 0);
    this.parent.rawname = "DOCUMENT_NODE";
    this.currLoc = 0;
    this.nextFreeLoc = 1;
    this.inCDATA = false;
    for (byte b = 0; b < 15; b++)
      this.relations[b] = new NodeImpl[10]; 
    this.relations[this.currLoc][0] = this.parent;
  }
  
  public void printDOM() {}
  
  public static void traverse(Node paramNode, int paramInt) {
    indent(paramInt);
    System.out.print("<" + paramNode.getNodeName());
    if (paramNode.hasAttributes()) {
      NamedNodeMap namedNodeMap = paramNode.getAttributes();
      for (byte b = 0; b < namedNodeMap.getLength(); b++)
        System.out.print("  " + ((Attr)namedNodeMap.item(b)).getName() + "=\"" + ((Attr)namedNodeMap.item(b)).getValue() + "\""); 
    } 
    if (paramNode.hasChildNodes()) {
      System.out.println(">");
      paramInt += 4;
      for (Node node = paramNode.getFirstChild(); node != null; node = node.getNextSibling())
        traverse(node, paramInt); 
      paramInt -= 4;
      indent(paramInt);
      System.out.println("</" + paramNode.getNodeName() + ">");
    } else {
      System.out.println("/>");
    } 
  }
  
  public static void indent(int paramInt) {
    for (byte b = 0; b < paramInt; b++)
      System.out.print(' '); 
  }
  
  public Element getDocumentElement() { return (ElementImpl)this.relations[0][1]; }
  
  public DOMImplementation getImplementation() { return SchemaDOMImplementation.getDOMImplementation(); }
  
  void startAnnotation(QName paramQName, XMLAttributes paramXMLAttributes, NamespaceContext paramNamespaceContext) { startAnnotation(paramQName.rawname, paramXMLAttributes, paramNamespaceContext); }
  
  void startAnnotation(String paramString, XMLAttributes paramXMLAttributes, NamespaceContext paramNamespaceContext) {
    if (this.fAnnotationBuffer == null)
      this.fAnnotationBuffer = new StringBuffer(256); 
    this.fAnnotationBuffer.append("<").append(paramString).append(" ");
    ArrayList arrayList = new ArrayList();
    for (byte b = 0; b < paramXMLAttributes.getLength(); b++) {
      String str1 = paramXMLAttributes.getValue(b);
      String str2 = paramXMLAttributes.getPrefix(b);
      String str3 = paramXMLAttributes.getQName(b);
      if (str2 == XMLSymbols.PREFIX_XMLNS || str3 == XMLSymbols.PREFIX_XMLNS)
        arrayList.add((str2 == XMLSymbols.PREFIX_XMLNS) ? paramXMLAttributes.getLocalName(b) : XMLSymbols.EMPTY_STRING); 
      this.fAnnotationBuffer.append(str3).append("=\"").append(processAttValue(str1)).append("\" ");
    } 
    Enumeration enumeration = paramNamespaceContext.getAllPrefixes();
    while (enumeration.hasMoreElements()) {
      String str1 = (String)enumeration.nextElement();
      String str2 = paramNamespaceContext.getURI(str1);
      if (str2 == null)
        str2 = XMLSymbols.EMPTY_STRING; 
      if (!arrayList.contains(str1)) {
        if (str1 == XMLSymbols.EMPTY_STRING) {
          this.fAnnotationBuffer.append("xmlns").append("=\"").append(processAttValue(str2)).append("\" ");
          continue;
        } 
        this.fAnnotationBuffer.append("xmlns:").append(str1).append("=\"").append(processAttValue(str2)).append("\" ");
      } 
    } 
    this.fAnnotationBuffer.append(">\n");
  }
  
  void startAnnotationElement(QName paramQName, XMLAttributes paramXMLAttributes) { startAnnotationElement(paramQName.rawname, paramXMLAttributes); }
  
  void startAnnotationElement(String paramString, XMLAttributes paramXMLAttributes) {
    this.fAnnotationBuffer.append("<").append(paramString);
    for (byte b = 0; b < paramXMLAttributes.getLength(); b++) {
      String str = paramXMLAttributes.getValue(b);
      this.fAnnotationBuffer.append(" ").append(paramXMLAttributes.getQName(b)).append("=\"").append(processAttValue(str)).append("\"");
    } 
    this.fAnnotationBuffer.append(">");
  }
  
  private static String processAttValue(String paramString) {
    int i = paramString.length();
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (c == '"' || c == '<' || c == '&' || c == '\t' || c == '\n' || c == '\r')
        return escapeAttValue(paramString, b); 
    } 
    return paramString;
  }
  
  private static String escapeAttValue(String paramString, int paramInt) {
    int j = paramString.length();
    StringBuffer stringBuffer = new StringBuffer(j);
    stringBuffer.append(paramString.substring(0, paramInt));
    for (int i = paramInt; i < j; i++) {
      char c = paramString.charAt(i);
      if (c == '"') {
        stringBuffer.append("&quot;");
      } else if (c == '<') {
        stringBuffer.append("&lt;");
      } else if (c == '&') {
        stringBuffer.append("&amp;");
      } else if (c == '\t') {
        stringBuffer.append("&#x9;");
      } else if (c == '\n') {
        stringBuffer.append("&#xA;");
      } else if (c == '\r') {
        stringBuffer.append("&#xD;");
      } else {
        stringBuffer.append(c);
      } 
    } 
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\opti\SchemaDOM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */