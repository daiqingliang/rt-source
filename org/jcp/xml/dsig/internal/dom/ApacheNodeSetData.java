package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.signature.NodeFilter;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.xml.crypto.NodeSetData;
import org.w3c.dom.Node;

public class ApacheNodeSetData implements ApacheData, NodeSetData {
  private XMLSignatureInput xi;
  
  public ApacheNodeSetData(XMLSignatureInput paramXMLSignatureInput) { this.xi = paramXMLSignatureInput; }
  
  public Iterator iterator() {
    if (this.xi.getNodeFilters() != null && !this.xi.getNodeFilters().isEmpty())
      return Collections.unmodifiableSet(getNodeSet(this.xi.getNodeFilters())).iterator(); 
    try {
      return Collections.unmodifiableSet(this.xi.getNodeSet()).iterator();
    } catch (Exception exception) {
      throw new RuntimeException("unrecoverable error retrieving nodeset", exception);
    } 
  }
  
  public XMLSignatureInput getXMLSignatureInput() { return this.xi; }
  
  private Set<Node> getNodeSet(List<NodeFilter> paramList) {
    if (this.xi.isNeedsToBeExpanded())
      XMLUtils.circumventBug2650(XMLUtils.getOwnerDocument(this.xi.getSubNode())); 
    LinkedHashSet linkedHashSet1 = new LinkedHashSet();
    XMLUtils.getSet(this.xi.getSubNode(), linkedHashSet1, null, !this.xi.isExcludeComments());
    LinkedHashSet linkedHashSet2 = new LinkedHashSet();
    for (Node node : linkedHashSet1) {
      Iterator iterator = paramList.iterator();
      boolean bool = false;
      while (iterator.hasNext() && !bool) {
        NodeFilter nodeFilter = (NodeFilter)iterator.next();
        if (nodeFilter.isNodeInclude(node) != 1)
          bool = true; 
      } 
      if (!bool)
        linkedHashSet2.add(node); 
    } 
    return linkedHashSet2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\ApacheNodeSetData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */