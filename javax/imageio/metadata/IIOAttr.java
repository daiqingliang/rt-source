package javax.imageio.metadata;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

class IIOAttr extends IIOMetadataNode implements Attr {
  Element owner;
  
  String name;
  
  String value;
  
  public IIOAttr(Element paramElement, String paramString1, String paramString2) {
    this.owner = paramElement;
    this.name = paramString1;
    this.value = paramString2;
  }
  
  public String getName() { return this.name; }
  
  public String getNodeName() { return this.name; }
  
  public short getNodeType() { return 2; }
  
  public boolean getSpecified() { return true; }
  
  public String getValue() { return this.value; }
  
  public String getNodeValue() { return this.value; }
  
  public void setValue(String paramString) { this.value = paramString; }
  
  public void setNodeValue(String paramString) { this.value = paramString; }
  
  public Element getOwnerElement() { return this.owner; }
  
  public void setOwnerElement(Element paramElement) { this.owner = paramElement; }
  
  public boolean isId() { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\metadata\IIOAttr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */