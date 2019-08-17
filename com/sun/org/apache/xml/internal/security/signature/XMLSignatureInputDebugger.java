package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.c14n.helper.AttrCompare;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Set;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

public class XMLSignatureInputDebugger {
  private Set<Node> xpathNodeSet;
  
  private Set<String> inclusiveNamespaces;
  
  private Document doc = null;
  
  private Writer writer = null;
  
  static final String HTMLPrefix = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n<html>\n<head>\n<title>Caninical XML node set</title>\n<style type=\"text/css\">\n<!-- \n.INCLUDED { \n   color: #000000; \n   background-color: \n   #FFFFFF; \n   font-weight: bold; } \n.EXCLUDED { \n   color: #666666; \n   background-color: \n   #999999; } \n.INCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #FFFFFF; \n   font-weight: bold; \n   font-style: italic; } \n.EXCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #999999; \n   font-style: italic; } \n--> \n</style> \n</head>\n<body bgcolor=\"#999999\">\n<h1>Explanation of the output</h1>\n<p>The following text contains the nodeset of the given Reference before it is canonicalized. There exist four different styles to indicate how a given node is treated.</p>\n<ul>\n<li class=\"INCLUDED\">A node which is in the node set is labeled using the INCLUDED style.</li>\n<li class=\"EXCLUDED\">A node which is <em>NOT</em> in the node set is labeled EXCLUDED style.</li>\n<li class=\"INCLUDEDINCLUSIVENAMESPACE\">A namespace which is in the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n<li class=\"EXCLUDEDINCLUSIVENAMESPACE\">A namespace which is in NOT the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n</ul>\n<h1>Output</h1>\n<pre>\n";
  
  static final String HTMLSuffix = "</pre></body></html>";
  
  static final String HTMLExcludePrefix = "<span class=\"EXCLUDED\">";
  
  static final String HTMLIncludePrefix = "<span class=\"INCLUDED\">";
  
  static final String HTMLIncludeOrExcludeSuffix = "</span>";
  
  static final String HTMLIncludedInclusiveNamespacePrefix = "<span class=\"INCLUDEDINCLUSIVENAMESPACE\">";
  
  static final String HTMLExcludedInclusiveNamespacePrefix = "<span class=\"EXCLUDEDINCLUSIVENAMESPACE\">";
  
  private static final int NODE_BEFORE_DOCUMENT_ELEMENT = -1;
  
  private static final int NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT = 0;
  
  private static final int NODE_AFTER_DOCUMENT_ELEMENT = 1;
  
  static final AttrCompare ATTR_COMPARE = new AttrCompare();
  
  public XMLSignatureInputDebugger(XMLSignatureInput paramXMLSignatureInput) {
    if (!paramXMLSignatureInput.isNodeSet()) {
      this.xpathNodeSet = null;
    } else {
      this.xpathNodeSet = paramXMLSignatureInput.getInputNodeSet();
    } 
  }
  
  public XMLSignatureInputDebugger(XMLSignatureInput paramXMLSignatureInput, Set<String> paramSet) {
    this(paramXMLSignatureInput);
    this.inclusiveNamespaces = paramSet;
  }
  
  public String getHTMLRepresentation() throws XMLSignatureException {
    if (this.xpathNodeSet == null || this.xpathNodeSet.size() == 0)
      return "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n<html>\n<head>\n<title>Caninical XML node set</title>\n<style type=\"text/css\">\n<!-- \n.INCLUDED { \n   color: #000000; \n   background-color: \n   #FFFFFF; \n   font-weight: bold; } \n.EXCLUDED { \n   color: #666666; \n   background-color: \n   #999999; } \n.INCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #FFFFFF; \n   font-weight: bold; \n   font-style: italic; } \n.EXCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #999999; \n   font-style: italic; } \n--> \n</style> \n</head>\n<body bgcolor=\"#999999\">\n<h1>Explanation of the output</h1>\n<p>The following text contains the nodeset of the given Reference before it is canonicalized. There exist four different styles to indicate how a given node is treated.</p>\n<ul>\n<li class=\"INCLUDED\">A node which is in the node set is labeled using the INCLUDED style.</li>\n<li class=\"EXCLUDED\">A node which is <em>NOT</em> in the node set is labeled EXCLUDED style.</li>\n<li class=\"INCLUDEDINCLUSIVENAMESPACE\">A namespace which is in the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n<li class=\"EXCLUDEDINCLUSIVENAMESPACE\">A namespace which is in NOT the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n</ul>\n<h1>Output</h1>\n<pre>\n<blink>no node set, sorry</blink></pre></body></html>"; 
    Node node = (Node)this.xpathNodeSet.iterator().next();
    this.doc = XMLUtils.getOwnerDocument(node);
    try {
      this.writer = new StringWriter();
      canonicalizeXPathNodeSet(this.doc);
      this.writer.close();
      return this.writer.toString();
    } catch (IOException iOException) {
      throw new XMLSignatureException("empty", iOException);
    } finally {
      this.xpathNodeSet = null;
      this.doc = null;
      this.writer = null;
    } 
  }
  
  private void canonicalizeXPathNodeSet(Node paramNode) throws XMLSignatureException, IOException {
    Node node3;
    byte b2;
    byte b1;
    Attr[] arrayOfAttr2;
    Attr[] arrayOfAttr1;
    int j;
    NamedNodeMap namedNodeMap;
    Node node2;
    Node node1;
    int i;
    short s = paramNode.getNodeType();
    switch (s) {
      case 2:
      case 6:
      case 11:
      case 12:
        throw new XMLSignatureException("empty");
      case 9:
        this.writer.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n<html>\n<head>\n<title>Caninical XML node set</title>\n<style type=\"text/css\">\n<!-- \n.INCLUDED { \n   color: #000000; \n   background-color: \n   #FFFFFF; \n   font-weight: bold; } \n.EXCLUDED { \n   color: #666666; \n   background-color: \n   #999999; } \n.INCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #FFFFFF; \n   font-weight: bold; \n   font-style: italic; } \n.EXCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #999999; \n   font-style: italic; } \n--> \n</style> \n</head>\n<body bgcolor=\"#999999\">\n<h1>Explanation of the output</h1>\n<p>The following text contains the nodeset of the given Reference before it is canonicalized. There exist four different styles to indicate how a given node is treated.</p>\n<ul>\n<li class=\"INCLUDED\">A node which is in the node set is labeled using the INCLUDED style.</li>\n<li class=\"EXCLUDED\">A node which is <em>NOT</em> in the node set is labeled EXCLUDED style.</li>\n<li class=\"INCLUDEDINCLUSIVENAMESPACE\">A namespace which is in the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n<li class=\"EXCLUDEDINCLUSIVENAMESPACE\">A namespace which is in NOT the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n</ul>\n<h1>Output</h1>\n<pre>\n");
        for (node1 = paramNode.getFirstChild(); node1 != null; node1 = node1.getNextSibling())
          canonicalizeXPathNodeSet(node1); 
        this.writer.write("</pre></body></html>");
        break;
      case 8:
        if (this.xpathNodeSet.contains(paramNode)) {
          this.writer.write("<span class=\"INCLUDED\">");
        } else {
          this.writer.write("<span class=\"EXCLUDED\">");
        } 
        i = getPositionRelativeToDocumentElement(paramNode);
        if (i == 1)
          this.writer.write("\n"); 
        outputCommentToWriter((Comment)paramNode);
        if (i == -1)
          this.writer.write("\n"); 
        this.writer.write("</span>");
        break;
      case 7:
        if (this.xpathNodeSet.contains(paramNode)) {
          this.writer.write("<span class=\"INCLUDED\">");
        } else {
          this.writer.write("<span class=\"EXCLUDED\">");
        } 
        i = getPositionRelativeToDocumentElement(paramNode);
        if (i == 1)
          this.writer.write("\n"); 
        outputPItoWriter((ProcessingInstruction)paramNode);
        if (i == -1)
          this.writer.write("\n"); 
        this.writer.write("</span>");
        break;
      case 3:
      case 4:
        if (this.xpathNodeSet.contains(paramNode)) {
          this.writer.write("<span class=\"INCLUDED\">");
        } else {
          this.writer.write("<span class=\"EXCLUDED\">");
        } 
        outputTextToWriter(paramNode.getNodeValue());
        for (node2 = paramNode.getNextSibling(); node2 != null && (node2.getNodeType() == 3 || node2.getNodeType() == 4); node2 = node2.getNextSibling())
          outputTextToWriter(node2.getNodeValue()); 
        this.writer.write("</span>");
        break;
      case 1:
        node2 = (Element)paramNode;
        if (this.xpathNodeSet.contains(paramNode)) {
          this.writer.write("<span class=\"INCLUDED\">");
        } else {
          this.writer.write("<span class=\"EXCLUDED\">");
        } 
        this.writer.write("&lt;");
        this.writer.write(node2.getTagName());
        this.writer.write("</span>");
        namedNodeMap = node2.getAttributes();
        j = namedNodeMap.getLength();
        arrayOfAttr1 = new Attr[j];
        for (b1 = 0; b1 < j; b1++)
          arrayOfAttr1[b1] = (Attr)namedNodeMap.item(b1); 
        Arrays.sort(arrayOfAttr1, ATTR_COMPARE);
        arrayOfAttr2 = arrayOfAttr1;
        for (b2 = 0; b2 < j; b2++) {
          Attr attr = (Attr)arrayOfAttr2[b2];
          boolean bool1 = this.xpathNodeSet.contains(attr);
          boolean bool2 = this.inclusiveNamespaces.contains(attr.getName());
          if (bool1) {
            if (bool2) {
              this.writer.write("<span class=\"INCLUDEDINCLUSIVENAMESPACE\">");
            } else {
              this.writer.write("<span class=\"INCLUDED\">");
            } 
          } else if (bool2) {
            this.writer.write("<span class=\"EXCLUDEDINCLUSIVENAMESPACE\">");
          } else {
            this.writer.write("<span class=\"EXCLUDED\">");
          } 
          outputAttrToWriter(attr.getNodeName(), attr.getNodeValue());
          this.writer.write("</span>");
        } 
        if (this.xpathNodeSet.contains(paramNode)) {
          this.writer.write("<span class=\"INCLUDED\">");
        } else {
          this.writer.write("<span class=\"EXCLUDED\">");
        } 
        this.writer.write("&gt;");
        this.writer.write("</span>");
        for (node3 = paramNode.getFirstChild(); node3 != null; node3 = node3.getNextSibling())
          canonicalizeXPathNodeSet(node3); 
        if (this.xpathNodeSet.contains(paramNode)) {
          this.writer.write("<span class=\"INCLUDED\">");
        } else {
          this.writer.write("<span class=\"EXCLUDED\">");
        } 
        this.writer.write("&lt;/");
        this.writer.write(node2.getTagName());
        this.writer.write("&gt;");
        this.writer.write("</span>");
        break;
    } 
  }
  
  private int getPositionRelativeToDocumentElement(Node paramNode) {
    if (paramNode == null)
      return 0; 
    Document document = paramNode.getOwnerDocument();
    if (paramNode.getParentNode() != document)
      return 0; 
    Element element = document.getDocumentElement();
    if (element == null)
      return 0; 
    if (element == paramNode)
      return 0; 
    for (Node node = paramNode; node != null; node = node.getNextSibling()) {
      if (node == element)
        return -1; 
    } 
    return 1;
  }
  
  private void outputAttrToWriter(String paramString1, String paramString2) throws IOException {
    this.writer.write(" ");
    this.writer.write(paramString1);
    this.writer.write("=\"");
    int i = paramString2.length();
    for (byte b = 0; b < i; b++) {
      char c = paramString2.charAt(b);
      switch (c) {
        case '&':
          this.writer.write("&amp;amp;");
          break;
        case '<':
          this.writer.write("&amp;lt;");
          break;
        case '"':
          this.writer.write("&amp;quot;");
          break;
        case '\t':
          this.writer.write("&amp;#x9;");
          break;
        case '\n':
          this.writer.write("&amp;#xA;");
          break;
        case '\r':
          this.writer.write("&amp;#xD;");
          break;
        default:
          this.writer.write(c);
          break;
      } 
    } 
    this.writer.write("\"");
  }
  
  private void outputPItoWriter(ProcessingInstruction paramProcessingInstruction) throws IOException {
    if (paramProcessingInstruction == null)
      return; 
    this.writer.write("&lt;?");
    String str1 = paramProcessingInstruction.getTarget();
    int i = str1.length();
    for (byte b = 0; b < i; b++) {
      char c = str1.charAt(b);
      switch (c) {
        case '\r':
          this.writer.write("&amp;#xD;");
          break;
        case ' ':
          this.writer.write("&middot;");
          break;
        case '\n':
          this.writer.write("&para;\n");
          break;
        default:
          this.writer.write(c);
          break;
      } 
    } 
    String str2 = paramProcessingInstruction.getData();
    i = str2.length();
    if (i > 0) {
      this.writer.write(" ");
      for (byte b1 = 0; b1 < i; b1++) {
        char c = str2.charAt(b1);
        switch (c) {
          case '\r':
            this.writer.write("&amp;#xD;");
            break;
          default:
            this.writer.write(c);
            break;
        } 
      } 
    } 
    this.writer.write("?&gt;");
  }
  
  private void outputCommentToWriter(Comment paramComment) throws IOException {
    if (paramComment == null)
      return; 
    this.writer.write("&lt;!--");
    String str = paramComment.getData();
    int i = str.length();
    for (byte b = 0; b < i; b++) {
      char c = str.charAt(b);
      switch (c) {
        case '\r':
          this.writer.write("&amp;#xD;");
          break;
        case ' ':
          this.writer.write("&middot;");
          break;
        case '\n':
          this.writer.write("&para;\n");
          break;
        default:
          this.writer.write(c);
          break;
      } 
    } 
    this.writer.write("--&gt;");
  }
  
  private void outputTextToWriter(String paramString) throws IOException {
    if (paramString == null)
      return; 
    int i = paramString.length();
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      switch (c) {
        case '&':
          this.writer.write("&amp;amp;");
          break;
        case '<':
          this.writer.write("&amp;lt;");
          break;
        case '>':
          this.writer.write("&amp;gt;");
          break;
        case '\r':
          this.writer.write("&amp;#xD;");
          break;
        case ' ':
          this.writer.write("&middot;");
          break;
        case '\n':
          this.writer.write("&para;\n");
          break;
        default:
          this.writer.write(c);
          break;
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\signature\XMLSignatureInputDebugger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */