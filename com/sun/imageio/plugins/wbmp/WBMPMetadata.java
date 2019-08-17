package com.sun.imageio.plugins.wbmp;

import com.sun.imageio.plugins.common.I18N;
import com.sun.imageio.plugins.common.ImageUtil;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

public class WBMPMetadata extends IIOMetadata {
  static final String nativeMetadataFormatName = "javax_imageio_wbmp_1.0";
  
  public int wbmpType;
  
  public int width;
  
  public int height;
  
  public WBMPMetadata() { super(true, "javax_imageio_wbmp_1.0", "com.sun.imageio.plugins.wbmp.WBMPMetadataFormat", null, null); }
  
  public boolean isReadOnly() { return true; }
  
  public Node getAsTree(String paramString) {
    if (paramString.equals("javax_imageio_wbmp_1.0"))
      return getNativeTree(); 
    if (paramString.equals("javax_imageio_1.0"))
      return getStandardTree(); 
    throw new IllegalArgumentException(I18N.getString("WBMPMetadata0"));
  }
  
  private Node getNativeTree() {
    IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("javax_imageio_wbmp_1.0");
    addChildNode(iIOMetadataNode, "WBMPType", new Integer(this.wbmpType));
    addChildNode(iIOMetadataNode, "Width", new Integer(this.width));
    addChildNode(iIOMetadataNode, "Height", new Integer(this.height));
    return iIOMetadataNode;
  }
  
  public void setFromTree(String paramString, Node paramNode) { throw new IllegalStateException(I18N.getString("WBMPMetadata1")); }
  
  public void mergeTree(String paramString, Node paramNode) { throw new IllegalStateException(I18N.getString("WBMPMetadata1")); }
  
  public void reset() { throw new IllegalStateException(I18N.getString("WBMPMetadata1")); }
  
  private IIOMetadataNode addChildNode(IIOMetadataNode paramIIOMetadataNode, String paramString, Object paramObject) {
    IIOMetadataNode iIOMetadataNode = new IIOMetadataNode(paramString);
    if (paramObject != null) {
      iIOMetadataNode.setUserObject(paramObject);
      iIOMetadataNode.setNodeValue(ImageUtil.convertObjectToString(paramObject));
    } 
    paramIIOMetadataNode.appendChild(iIOMetadataNode);
    return iIOMetadataNode;
  }
  
  protected IIOMetadataNode getStandardChromaNode() {
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Chroma");
    IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("BlackIsZero");
    iIOMetadataNode2.setAttribute("value", "TRUE");
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    return iIOMetadataNode1;
  }
  
  protected IIOMetadataNode getStandardDimensionNode() {
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Dimension");
    IIOMetadataNode iIOMetadataNode2 = null;
    iIOMetadataNode2 = new IIOMetadataNode("ImageOrientation");
    iIOMetadataNode2.setAttribute("value", "Normal");
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    return iIOMetadataNode1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\wbmp\WBMPMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */