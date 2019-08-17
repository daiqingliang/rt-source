package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Element;

public abstract class DetailImpl extends FaultElementImpl implements Detail {
  public DetailImpl(SOAPDocumentImpl paramSOAPDocumentImpl, NameImpl paramNameImpl) { super(paramSOAPDocumentImpl, paramNameImpl); }
  
  protected abstract DetailEntry createDetailEntry(Name paramName);
  
  protected abstract DetailEntry createDetailEntry(QName paramQName);
  
  public DetailEntry addDetailEntry(Name paramName) {
    DetailEntry detailEntry = createDetailEntry(paramName);
    addNode(detailEntry);
    return detailEntry;
  }
  
  public DetailEntry addDetailEntry(QName paramQName) {
    DetailEntry detailEntry = createDetailEntry(paramQName);
    addNode(detailEntry);
    return detailEntry;
  }
  
  protected SOAPElement addElement(Name paramName) throws SOAPException { return addDetailEntry(paramName); }
  
  protected SOAPElement addElement(QName paramQName) throws SOAPException { return addDetailEntry(paramQName); }
  
  protected SOAPElement convertToSoapElement(Element paramElement) {
    if (paramElement instanceof DetailEntry)
      return (SOAPElement)paramElement; 
    DetailEntry detailEntry = createDetailEntry(NameImpl.copyElementName(paramElement));
    return replaceElementWithSOAPElement(paramElement, (ElementImpl)detailEntry);
  }
  
  public Iterator getDetailEntries() { return new Iterator() {
        Iterator eachNode = DetailImpl.this.getChildElementNodes();
        
        SOAPElement next = null;
        
        SOAPElement last = null;
        
        public boolean hasNext() {
          if (this.next == null)
            while (this.eachNode.hasNext()) {
              this.next = (SOAPElement)this.eachNode.next();
              if (this.next instanceof DetailEntry)
                break; 
              this.next = null;
            }  
          return (this.next != null);
        }
        
        public Object next() {
          if (!hasNext())
            throw new NoSuchElementException(); 
          this.last = this.next;
          this.next = null;
          return this.last;
        }
        
        public void remove() {
          if (this.last == null)
            throw new IllegalStateException(); 
          SOAPElement sOAPElement = this.last;
          DetailImpl.this.removeChild(sOAPElement);
          this.last = null;
        }
      }; }
  
  protected boolean isStandardFaultElement() { return true; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\impl\DetailImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */