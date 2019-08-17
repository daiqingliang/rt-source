package com.sun.org.apache.xml.internal.security.c14n.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.helper.C14nHelper;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public abstract class Canonicalizer11 extends CanonicalizerBase {
  private static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
  
  private static final String XML_LANG_URI = "http://www.w3.org/XML/1998/namespace";
  
  private static Logger log = Logger.getLogger(Canonicalizer11.class.getName());
  
  private final SortedSet<Attr> result = new TreeSet(COMPARE);
  
  private boolean firstCall = true;
  
  private XmlAttrStack xmlattrStack = new XmlAttrStack(null);
  
  public Canonicalizer11(boolean paramBoolean) { super(paramBoolean); }
  
  public byte[] engineCanonicalizeXPathNodeSet(Set<Node> paramSet, String paramString) throws CanonicalizationException { throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation"); }
  
  public byte[] engineCanonicalizeSubTree(Node paramNode, String paramString) throws CanonicalizationException { throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation"); }
  
  protected Iterator<Attr> handleAttributesSubtree(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable) throws CanonicalizationException {
    if (!paramElement.hasAttributes() && !this.firstCall)
      return null; 
    SortedSet sortedSet = this.result;
    sortedSet.clear();
    if (paramElement.hasAttributes()) {
      NamedNodeMap namedNodeMap = paramElement.getAttributes();
      int i = namedNodeMap.getLength();
      for (byte b = 0; b < i; b++) {
        Attr attr = (Attr)namedNodeMap.item(b);
        String str1 = attr.getNamespaceURI();
        String str2 = attr.getLocalName();
        String str3 = attr.getValue();
        if (!"http://www.w3.org/2000/xmlns/".equals(str1)) {
          sortedSet.add(attr);
        } else if (!"xml".equals(str2) || !"http://www.w3.org/XML/1998/namespace".equals(str3)) {
          Node node = paramNameSpaceSymbTable.addMappingAndRender(str2, str3, attr);
          if (node != null) {
            sortedSet.add((Attr)node);
            if (C14nHelper.namespaceIsRelative(attr)) {
              Object[] arrayOfObject = { paramElement.getTagName(), str2, attr.getNodeValue() };
              throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", arrayOfObject);
            } 
          } 
        } 
      } 
    } 
    if (this.firstCall) {
      paramNameSpaceSymbTable.getUnrenderedNodes(sortedSet);
      this.xmlattrStack.getXmlnsAttr(sortedSet);
      this.firstCall = false;
    } 
    return sortedSet.iterator();
  }
  
  protected Iterator<Attr> handleAttributes(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable) throws CanonicalizationException {
    this.xmlattrStack.push(paramNameSpaceSymbTable.getLevel());
    boolean bool = (isVisibleDO(paramElement, paramNameSpaceSymbTable.getLevel()) == 1) ? 1 : 0;
    SortedSet sortedSet = this.result;
    sortedSet.clear();
    if (paramElement.hasAttributes()) {
      NamedNodeMap namedNodeMap = paramElement.getAttributes();
      int i = namedNodeMap.getLength();
      for (byte b = 0; b < i; b++) {
        Attr attr = (Attr)namedNodeMap.item(b);
        String str1 = attr.getNamespaceURI();
        String str2 = attr.getLocalName();
        String str3 = attr.getValue();
        if (!"http://www.w3.org/2000/xmlns/".equals(str1)) {
          if ("http://www.w3.org/XML/1998/namespace".equals(str1)) {
            if (str2.equals("id")) {
              if (bool)
                sortedSet.add(attr); 
            } else {
              this.xmlattrStack.addXmlnsAttr(attr);
            } 
          } else if (bool) {
            sortedSet.add(attr);
          } 
        } else if (!"xml".equals(str2) || !"http://www.w3.org/XML/1998/namespace".equals(str3)) {
          if (isVisible(attr)) {
            if (bool || !paramNameSpaceSymbTable.removeMappingIfRender(str2)) {
              Node node = paramNameSpaceSymbTable.addMappingAndRender(str2, str3, attr);
              if (node != null) {
                sortedSet.add((Attr)node);
                if (C14nHelper.namespaceIsRelative(attr)) {
                  Object[] arrayOfObject = { paramElement.getTagName(), str2, attr.getNodeValue() };
                  throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", arrayOfObject);
                } 
              } 
            } 
          } else if (bool && !"xmlns".equals(str2)) {
            paramNameSpaceSymbTable.removeMapping(str2);
          } else {
            paramNameSpaceSymbTable.addMapping(str2, str3, attr);
          } 
        } 
      } 
    } 
    if (bool) {
      Attr attr = paramElement.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", "xmlns");
      Node node = null;
      if (attr == null) {
        node = paramNameSpaceSymbTable.getMapping("xmlns");
      } else if (!isVisible(attr)) {
        node = paramNameSpaceSymbTable.addMappingAndRender("xmlns", "", getNullNode(attr.getOwnerDocument()));
      } 
      if (node != null)
        sortedSet.add((Attr)node); 
      this.xmlattrStack.getXmlnsAttr(sortedSet);
      paramNameSpaceSymbTable.getUnrenderedNodes(sortedSet);
    } 
    return sortedSet.iterator();
  }
  
  protected void circumventBugIfNeeded(XMLSignatureInput paramXMLSignatureInput) throws CanonicalizationException, ParserConfigurationException, IOException, SAXException {
    if (!paramXMLSignatureInput.isNeedsToBeExpanded())
      return; 
    Document document = null;
    if (paramXMLSignatureInput.getSubNode() != null) {
      document = XMLUtils.getOwnerDocument(paramXMLSignatureInput.getSubNode());
    } else {
      document = XMLUtils.getOwnerDocument(paramXMLSignatureInput.getNodeSet());
    } 
    XMLUtils.circumventBug2650(document);
  }
  
  protected void handleParent(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable) {
    if (!paramElement.hasAttributes() && paramElement.getNamespaceURI() == null)
      return; 
    this.xmlattrStack.push(-1);
    NamedNodeMap namedNodeMap = paramElement.getAttributes();
    int i = namedNodeMap.getLength();
    for (byte b = 0; b < i; b++) {
      Attr attr = (Attr)namedNodeMap.item(b);
      String str1 = attr.getLocalName();
      String str2 = attr.getNodeValue();
      if ("http://www.w3.org/2000/xmlns/".equals(attr.getNamespaceURI())) {
        if (!"xml".equals(str1) || !"http://www.w3.org/XML/1998/namespace".equals(str2))
          paramNameSpaceSymbTable.addMapping(str1, str2, attr); 
      } else if (!"id".equals(str1) && "http://www.w3.org/XML/1998/namespace".equals(attr.getNamespaceURI())) {
        this.xmlattrStack.addXmlnsAttr(attr);
      } 
    } 
    if (paramElement.getNamespaceURI() != null) {
      String str3;
      String str1 = paramElement.getPrefix();
      String str2 = paramElement.getNamespaceURI();
      if (str1 == null || str1.equals("")) {
        str1 = "xmlns";
        str3 = "xmlns";
      } else {
        str3 = "xmlns:" + str1;
      } 
      Attr attr = paramElement.getOwnerDocument().createAttributeNS("http://www.w3.org/2000/xmlns/", str3);
      attr.setValue(str2);
      paramNameSpaceSymbTable.addMapping(str1, str2, attr);
    } 
  }
  
  private static String joinURI(String paramString1, String paramString2) throws URISyntaxException {
    String str12;
    String str11;
    String str10;
    String str9;
    String str1 = null;
    String str2 = null;
    String str3 = "";
    String str4 = null;
    if (paramString1 != null) {
      if (paramString1.endsWith(".."))
        paramString1 = paramString1 + "/"; 
      URI uRI1 = new URI(paramString1);
      str1 = uRI1.getScheme();
      str2 = uRI1.getAuthority();
      str3 = uRI1.getPath();
      str4 = uRI1.getQuery();
    } 
    URI uRI = new URI(paramString2);
    String str5 = uRI.getScheme();
    String str6 = uRI.getAuthority();
    String str7 = uRI.getPath();
    String str8 = uRI.getQuery();
    if (str5 != null && str5.equals(str1))
      str5 = null; 
    if (str5 != null) {
      str9 = str5;
      str10 = str6;
      str11 = removeDotSegments(str7);
      str12 = str8;
    } else {
      if (str6 != null) {
        str10 = str6;
        str11 = removeDotSegments(str7);
        str12 = str8;
      } else {
        if (str7.length() == 0) {
          str11 = str3;
          if (str8 != null) {
            str12 = str8;
          } else {
            str12 = str4;
          } 
        } else {
          if (str7.startsWith("/")) {
            str11 = removeDotSegments(str7);
          } else {
            if (str2 != null && str3.length() == 0) {
              str11 = "/" + str7;
            } else {
              int i = str3.lastIndexOf('/');
              if (i == -1) {
                str11 = str7;
              } else {
                str11 = str3.substring(0, i + 1) + str7;
              } 
            } 
            str11 = removeDotSegments(str11);
          } 
          str12 = str8;
        } 
        str10 = str2;
      } 
      str9 = str1;
    } 
    return (new URI(str9, str10, str11, str12, null)).toString();
  }
  
  private static String removeDotSegments(String paramString) {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "STEP   OUTPUT BUFFER\t\tINPUT BUFFER"); 
    String str;
    for (str = paramString; str.indexOf("//") > -1; str = str.replaceAll("//", "/"));
    StringBuilder stringBuilder = new StringBuilder();
    if (str.charAt(0) == '/') {
      stringBuilder.append("/");
      str = str.substring(1);
    } 
    printStep("1 ", stringBuilder.toString(), str);
    while (str.length() != 0) {
      String str1;
      if (str.startsWith("./")) {
        str = str.substring(2);
        printStep("2A", stringBuilder.toString(), str);
        continue;
      } 
      if (str.startsWith("../")) {
        str = str.substring(3);
        if (!stringBuilder.toString().equals("/"))
          stringBuilder.append("../"); 
        printStep("2A", stringBuilder.toString(), str);
        continue;
      } 
      if (str.startsWith("/./")) {
        str = str.substring(2);
        printStep("2B", stringBuilder.toString(), str);
        continue;
      } 
      if (str.equals("/.")) {
        str = str.replaceFirst("/.", "/");
        printStep("2B", stringBuilder.toString(), str);
        continue;
      } 
      if (str.startsWith("/../")) {
        str = str.substring(3);
        if (stringBuilder.length() == 0) {
          stringBuilder.append("/");
        } else if (stringBuilder.toString().endsWith("../")) {
          stringBuilder.append("..");
        } else if (stringBuilder.toString().endsWith("..")) {
          stringBuilder.append("/..");
        } else {
          int k = stringBuilder.lastIndexOf("/");
          if (k == -1) {
            stringBuilder = new StringBuilder();
            if (str.charAt(0) == '/')
              str = str.substring(1); 
          } else {
            stringBuilder = stringBuilder.delete(k, stringBuilder.length());
          } 
        } 
        printStep("2C", stringBuilder.toString(), str);
        continue;
      } 
      if (str.equals("/..")) {
        str = str.replaceFirst("/..", "/");
        if (stringBuilder.length() == 0) {
          stringBuilder.append("/");
        } else if (stringBuilder.toString().endsWith("../")) {
          stringBuilder.append("..");
        } else if (stringBuilder.toString().endsWith("..")) {
          stringBuilder.append("/..");
        } else {
          int k = stringBuilder.lastIndexOf("/");
          if (k == -1) {
            stringBuilder = new StringBuilder();
            if (str.charAt(0) == '/')
              str = str.substring(1); 
          } else {
            stringBuilder = stringBuilder.delete(k, stringBuilder.length());
          } 
        } 
        printStep("2C", stringBuilder.toString(), str);
        continue;
      } 
      if (str.equals(".")) {
        str = "";
        printStep("2D", stringBuilder.toString(), str);
        continue;
      } 
      if (str.equals("..")) {
        if (!stringBuilder.toString().equals("/"))
          stringBuilder.append(".."); 
        str = "";
        printStep("2D", stringBuilder.toString(), str);
        continue;
      } 
      int i = -1;
      int j = str.indexOf('/');
      if (j == 0) {
        i = str.indexOf('/', 1);
      } else {
        i = j;
        j = 0;
      } 
      if (i == -1) {
        str1 = str.substring(j);
        str = "";
      } else {
        str1 = str.substring(j, i);
        str = str.substring(i);
      } 
      stringBuilder.append(str1);
      printStep("2E", stringBuilder.toString(), str);
    } 
    if (stringBuilder.toString().endsWith("..")) {
      stringBuilder.append("/");
      printStep("3 ", stringBuilder.toString(), str);
    } 
    return stringBuilder.toString();
  }
  
  private static void printStep(String paramString1, String paramString2, String paramString3) {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, " " + paramString1 + ":   " + paramString2);
      if (paramString2.length() == 0) {
        log.log(Level.FINE, "\t\t\t\t" + paramString3);
      } else {
        log.log(Level.FINE, "\t\t\t" + paramString3);
      } 
    } 
  }
  
  private static class XmlAttrStack {
    int currentLevel = 0;
    
    int lastlevel = 0;
    
    XmlsStackElement cur;
    
    List<XmlsStackElement> levels = new ArrayList();
    
    private XmlAttrStack() {}
    
    void push(int param1Int) {
      this.currentLevel = param1Int;
      if (this.currentLevel == -1)
        return; 
      this.cur = null;
      while (this.lastlevel >= this.currentLevel) {
        this.levels.remove(this.levels.size() - 1);
        int i = this.levels.size();
        if (i == 0) {
          this.lastlevel = 0;
          return;
        } 
        this.lastlevel = ((XmlsStackElement)this.levels.get(i - 1)).level;
      } 
    }
    
    void addXmlnsAttr(Attr param1Attr) {
      if (this.cur == null) {
        this.cur = new XmlsStackElement();
        this.cur.level = this.currentLevel;
        this.levels.add(this.cur);
        this.lastlevel = this.currentLevel;
      } 
      this.cur.nodes.add(param1Attr);
    }
    
    void getXmlnsAttr(Collection<Attr> param1Collection) {
      int i = this.levels.size() - 1;
      if (this.cur == null) {
        this.cur = new XmlsStackElement();
        this.cur.level = this.currentLevel;
        this.lastlevel = this.currentLevel;
        this.levels.add(this.cur);
      } 
      boolean bool1 = false;
      XmlsStackElement xmlsStackElement = null;
      if (i == -1) {
        bool1 = true;
      } else {
        xmlsStackElement = (XmlsStackElement)this.levels.get(i);
        if (xmlsStackElement.rendered && xmlsStackElement.level + 1 == this.currentLevel)
          bool1 = true; 
      } 
      if (bool1) {
        param1Collection.addAll(this.cur.nodes);
        this.cur.rendered = true;
        return;
      } 
      HashMap hashMap = new HashMap();
      ArrayList arrayList = new ArrayList();
      boolean bool2 = true;
      while (i >= 0) {
        xmlsStackElement = (XmlsStackElement)this.levels.get(i);
        if (xmlsStackElement.rendered)
          bool2 = false; 
        Iterator iterator = xmlsStackElement.nodes.iterator();
        while (iterator.hasNext() && bool2) {
          Attr attr = (Attr)iterator.next();
          if (attr.getLocalName().equals("base") && !xmlsStackElement.rendered) {
            arrayList.add(attr);
            continue;
          } 
          if (!hashMap.containsKey(attr.getName()))
            hashMap.put(attr.getName(), attr); 
        } 
        i--;
      } 
      if (!arrayList.isEmpty()) {
        null = param1Collection.iterator();
        String str = null;
        Attr attr = null;
        while (null.hasNext()) {
          Attr attr1 = (Attr)null.next();
          if (attr1.getLocalName().equals("base")) {
            str = attr1.getValue();
            attr = attr1;
            break;
          } 
        } 
        for (Attr attr1 : arrayList) {
          if (str == null) {
            str = attr1.getValue();
            attr = attr1;
            continue;
          } 
          try {
            str = Canonicalizer11.joinURI(attr1.getValue(), str);
          } catch (URISyntaxException uRISyntaxException) {
            if (log.isLoggable(Level.FINE))
              log.log(Level.FINE, uRISyntaxException.getMessage(), uRISyntaxException); 
          } 
        } 
        if (str != null && str.length() != 0) {
          attr.setValue(str);
          param1Collection.add(attr);
        } 
      } 
      this.cur.rendered = true;
      param1Collection.addAll(hashMap.values());
    }
    
    static class XmlsStackElement {
      int level;
      
      boolean rendered = false;
      
      List<Attr> nodes = new ArrayList();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\c14n\implementations\Canonicalizer11.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */