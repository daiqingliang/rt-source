package com.sun.org.apache.xml.internal.security.c14n.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.SAXException;

public class CanonicalizerPhysical extends CanonicalizerBase {
  private final SortedSet<Attr> result = new TreeSet(COMPARE);
  
  public CanonicalizerPhysical() { super(true); }
  
  public byte[] engineCanonicalizeXPathNodeSet(Set<Node> paramSet, String paramString) throws CanonicalizationException { throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation"); }
  
  public byte[] engineCanonicalizeSubTree(Node paramNode, String paramString) throws CanonicalizationException { throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation"); }
  
  protected Iterator<Attr> handleAttributesSubtree(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable) throws CanonicalizationException {
    if (!paramElement.hasAttributes())
      return null; 
    SortedSet sortedSet = this.result;
    sortedSet.clear();
    if (paramElement.hasAttributes()) {
      NamedNodeMap namedNodeMap = paramElement.getAttributes();
      int i = namedNodeMap.getLength();
      for (byte b = 0; b < i; b++) {
        Attr attr = (Attr)namedNodeMap.item(b);
        sortedSet.add(attr);
      } 
    } 
    return sortedSet.iterator();
  }
  
  protected Iterator<Attr> handleAttributes(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable) throws CanonicalizationException { throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation"); }
  
  protected void circumventBugIfNeeded(XMLSignatureInput paramXMLSignatureInput) throws CanonicalizationException, ParserConfigurationException, IOException, SAXException {}
  
  protected void handleParent(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable) {}
  
  public final String engineGetURI() { return "http://santuario.apache.org/c14n/physical"; }
  
  public final boolean engineGetIncludeComments() { return true; }
  
  protected void outputPItoWriter(ProcessingInstruction paramProcessingInstruction, OutputStream paramOutputStream, int paramInt) throws IOException { super.outputPItoWriter(paramProcessingInstruction, paramOutputStream, 0); }
  
  protected void outputCommentToWriter(Comment paramComment, OutputStream paramOutputStream, int paramInt) throws IOException { super.outputCommentToWriter(paramComment, paramOutputStream, 0); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\c14n\implementations\CanonicalizerPhysical.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */