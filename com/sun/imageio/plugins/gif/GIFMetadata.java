package com.sun.imageio.plugins.gif;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import org.w3c.dom.Node;

abstract class GIFMetadata extends IIOMetadata {
  static final int UNDEFINED_INTEGER_VALUE = -1;
  
  protected static void fatal(Node paramNode, String paramString) throws IIOInvalidTreeException { throw new IIOInvalidTreeException(paramString, paramNode); }
  
  protected static String getStringAttribute(Node paramNode, String paramString1, String paramString2, boolean paramBoolean, String[] paramArrayOfString) throws IIOInvalidTreeException {
    Node node = paramNode.getAttributes().getNamedItem(paramString1);
    if (node == null) {
      if (!paramBoolean)
        return paramString2; 
      fatal(paramNode, "Required attribute " + paramString1 + " not present!");
    } 
    String str = node.getNodeValue();
    if (paramArrayOfString != null) {
      if (str == null)
        fatal(paramNode, "Null value for " + paramNode.getNodeName() + " attribute " + paramString1 + "!"); 
      boolean bool = false;
      int i = paramArrayOfString.length;
      for (byte b = 0; b < i; b++) {
        if (str.equals(paramArrayOfString[b])) {
          bool = true;
          break;
        } 
      } 
      if (!bool)
        fatal(paramNode, "Bad value for " + paramNode.getNodeName() + " attribute " + paramString1 + "!"); 
    } 
    return str;
  }
  
  protected static int getIntAttribute(Node paramNode, String paramString, int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2, int paramInt3) throws IIOInvalidTreeException {
    String str = getStringAttribute(paramNode, paramString, null, paramBoolean1, null);
    if (str == null || "".equals(str))
      return paramInt1; 
    int i = paramInt1;
    try {
      i = Integer.parseInt(str);
    } catch (NumberFormatException numberFormatException) {
      fatal(paramNode, "Bad value for " + paramNode.getNodeName() + " attribute " + paramString + "!");
    } 
    if (paramBoolean2 && (i < paramInt2 || i > paramInt3))
      fatal(paramNode, "Bad value for " + paramNode.getNodeName() + " attribute " + paramString + "!"); 
    return i;
  }
  
  protected static float getFloatAttribute(Node paramNode, String paramString, float paramFloat, boolean paramBoolean) throws IIOInvalidTreeException {
    String str = getStringAttribute(paramNode, paramString, null, paramBoolean, null);
    return (str == null) ? paramFloat : Float.parseFloat(str);
  }
  
  protected static int getIntAttribute(Node paramNode, String paramString, boolean paramBoolean, int paramInt1, int paramInt2) throws IIOInvalidTreeException { return getIntAttribute(paramNode, paramString, -1, true, paramBoolean, paramInt1, paramInt2); }
  
  protected static float getFloatAttribute(Node paramNode, String paramString) throws IIOInvalidTreeException { return getFloatAttribute(paramNode, paramString, -1.0F, true); }
  
  protected static boolean getBooleanAttribute(Node paramNode, String paramString, boolean paramBoolean1, boolean paramBoolean2) throws IIOInvalidTreeException {
    Node node = paramNode.getAttributes().getNamedItem(paramString);
    if (node == null) {
      if (!paramBoolean2)
        return paramBoolean1; 
      fatal(paramNode, "Required attribute " + paramString + " not present!");
    } 
    String str = node.getNodeValue();
    if (str.equals("TRUE") || str.equals("true"))
      return true; 
    if (str.equals("FALSE") || str.equals("false"))
      return false; 
    fatal(paramNode, "Attribute " + paramString + " must be 'TRUE' or 'FALSE'!");
    return false;
  }
  
  protected static boolean getBooleanAttribute(Node paramNode, String paramString) throws IIOInvalidTreeException { return getBooleanAttribute(paramNode, paramString, false, true); }
  
  protected static int getEnumeratedAttribute(Node paramNode, String paramString, String[] paramArrayOfString, int paramInt, boolean paramBoolean) throws IIOInvalidTreeException {
    Node node = paramNode.getAttributes().getNamedItem(paramString);
    if (node == null) {
      if (!paramBoolean)
        return paramInt; 
      fatal(paramNode, "Required attribute " + paramString + " not present!");
    } 
    String str = node.getNodeValue();
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (str.equals(paramArrayOfString[b]))
        return b; 
    } 
    fatal(paramNode, "Illegal value for attribute " + paramString + "!");
    return -1;
  }
  
  protected static int getEnumeratedAttribute(Node paramNode, String paramString, String[] paramArrayOfString) throws IIOInvalidTreeException { return getEnumeratedAttribute(paramNode, paramString, paramArrayOfString, -1, true); }
  
  protected static String getAttribute(Node paramNode, String paramString1, String paramString2, boolean paramBoolean) throws IIOInvalidTreeException {
    Node node = paramNode.getAttributes().getNamedItem(paramString1);
    if (node == null) {
      if (!paramBoolean)
        return paramString2; 
      fatal(paramNode, "Required attribute " + paramString1 + " not present!");
    } 
    return node.getNodeValue();
  }
  
  protected static String getAttribute(Node paramNode, String paramString) throws IIOInvalidTreeException { return getAttribute(paramNode, paramString, null, true); }
  
  protected GIFMetadata(boolean paramBoolean, String paramString1, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2) { super(paramBoolean, paramString1, paramString2, paramArrayOfString1, paramArrayOfString2); }
  
  public void mergeTree(String paramString, Node paramNode) throws IIOInvalidTreeException {
    if (paramString.equals(this.nativeMetadataFormatName)) {
      if (paramNode == null)
        throw new IllegalArgumentException("root == null!"); 
      mergeNativeTree(paramNode);
    } else if (paramString.equals("javax_imageio_1.0")) {
      if (paramNode == null)
        throw new IllegalArgumentException("root == null!"); 
      mergeStandardTree(paramNode);
    } else {
      throw new IllegalArgumentException("Not a recognized format!");
    } 
  }
  
  protected byte[] getColorTable(Node paramNode, String paramString, boolean paramBoolean, int paramInt) throws IIOInvalidTreeException {
    byte[] arrayOfByte1 = new byte[256];
    byte[] arrayOfByte2 = new byte[256];
    byte[] arrayOfByte3 = new byte[256];
    int i = -1;
    Node node = paramNode.getFirstChild();
    if (node == null)
      fatal(paramNode, "Palette has no entries!"); 
    while (node != null) {
      if (!node.getNodeName().equals(paramString))
        fatal(paramNode, "Only a " + paramString + " may be a child of a " + node.getNodeName() + "!"); 
      int k = getIntAttribute(node, "index", true, 0, 255);
      if (k > i)
        i = k; 
      arrayOfByte1[k] = (byte)getIntAttribute(node, "red", true, 0, 255);
      arrayOfByte2[k] = (byte)getIntAttribute(node, "green", true, 0, 255);
      arrayOfByte3[k] = (byte)getIntAttribute(node, "blue", true, 0, 255);
      node = node.getNextSibling();
    } 
    int j = i + 1;
    if (paramBoolean && j != paramInt)
      fatal(paramNode, "Unexpected length for palette!"); 
    byte[] arrayOfByte4 = new byte[3 * j];
    byte b1 = 0;
    byte b2 = 0;
    while (b1 < j) {
      arrayOfByte4[b2++] = arrayOfByte1[b1];
      arrayOfByte4[b2++] = arrayOfByte2[b1];
      arrayOfByte4[b2++] = arrayOfByte3[b1];
      b1++;
    } 
    return arrayOfByte4;
  }
  
  protected abstract void mergeNativeTree(Node paramNode) throws IIOInvalidTreeException;
  
  protected abstract void mergeStandardTree(Node paramNode) throws IIOInvalidTreeException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\gif\GIFMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */