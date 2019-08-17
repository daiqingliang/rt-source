package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.marshaller.SAX2DOMEx;
import com.sun.xml.internal.bind.v2.runtime.AssociationMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public final class DOMOutput extends SAXOutput {
  private final AssociationMap assoc;
  
  public DOMOutput(Node paramNode, AssociationMap paramAssociationMap) {
    super(new SAX2DOMEx(paramNode));
    this.assoc = paramAssociationMap;
    assert paramAssociationMap != null;
  }
  
  private SAX2DOMEx getBuilder() { return (SAX2DOMEx)this.out; }
  
  public void endStartTag() throws SAXException {
    super.endStartTag();
    Object object1 = this.nsContext.getCurrent().getOuterPeer();
    if (object1 != null)
      this.assoc.addOuter(getBuilder().getCurrentElement(), object1); 
    Object object2 = this.nsContext.getCurrent().getInnerPeer();
    if (object2 != null)
      this.assoc.addInner(getBuilder().getCurrentElement(), object2); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\DOMOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */