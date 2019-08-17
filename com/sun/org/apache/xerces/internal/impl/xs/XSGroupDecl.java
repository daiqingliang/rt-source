package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSModelGroup;
import com.sun.org.apache.xerces.internal.xs.XSModelGroupDefinition;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;

public class XSGroupDecl implements XSModelGroupDefinition {
  public String fName = null;
  
  public String fTargetNamespace = null;
  
  public XSModelGroupImpl fModelGroup = null;
  
  public XSObjectList fAnnotations = null;
  
  private XSNamespaceItem fNamespaceItem = null;
  
  public short getType() { return 6; }
  
  public String getName() { return this.fName; }
  
  public String getNamespace() { return this.fTargetNamespace; }
  
  public XSModelGroup getModelGroup() { return this.fModelGroup; }
  
  public XSAnnotation getAnnotation() { return (this.fAnnotations != null) ? (XSAnnotation)this.fAnnotations.item(0) : null; }
  
  public XSObjectList getAnnotations() { return (this.fAnnotations != null) ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST; }
  
  public XSNamespaceItem getNamespaceItem() { return this.fNamespaceItem; }
  
  void setNamespaceItem(XSNamespaceItem paramXSNamespaceItem) { this.fNamespaceItem = paramXSNamespaceItem; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\XSGroupDecl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */