package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.ProcessingInstruction;

public class ProcessingInstructionImpl extends CharacterDataImpl implements ProcessingInstruction {
  static final long serialVersionUID = 7554435174099981510L;
  
  protected String target;
  
  public ProcessingInstructionImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString1, String paramString2) {
    super(paramCoreDocumentImpl, paramString2);
    this.target = paramString1;
  }
  
  public short getNodeType() { return 7; }
  
  public String getNodeName() {
    if (needsSyncData())
      synchronizeData(); 
    return this.target;
  }
  
  public String getTarget() {
    if (needsSyncData())
      synchronizeData(); 
    return this.target;
  }
  
  public String getData() {
    if (needsSyncData())
      synchronizeData(); 
    return this.data;
  }
  
  public void setData(String paramString) { setNodeValue(paramString); }
  
  public String getBaseURI() {
    if (needsSyncData())
      synchronizeData(); 
    return this.ownerNode.getBaseURI();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\ProcessingInstructionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */