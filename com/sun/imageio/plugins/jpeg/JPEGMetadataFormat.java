package com.sun.imageio.plugins.jpeg;

import java.util.ArrayList;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormatImpl;

abstract class JPEGMetadataFormat extends IIOMetadataFormatImpl {
  private static final int MAX_JPEG_DATA_SIZE = 65533;
  
  String resourceBaseName = getClass().getName() + "Resources";
  
  JPEGMetadataFormat(String paramString, int paramInt) {
    super(paramString, paramInt);
    setResourceBaseName(this.resourceBaseName);
  }
  
  void addStreamElements(String paramString) {
    addElement("dqt", paramString, 1, 4);
    addElement("dqtable", "dqt", 0);
    addAttribute("dqtable", "elementPrecision", 2, false, "0");
    ArrayList arrayList1 = new ArrayList();
    arrayList1.add("0");
    arrayList1.add("1");
    arrayList1.add("2");
    arrayList1.add("3");
    addAttribute("dqtable", "qtableId", 2, true, null, arrayList1);
    addObjectValue("dqtable", javax.imageio.plugins.jpeg.JPEGQTable.class, true, null);
    addElement("dht", paramString, 1, 4);
    addElement("dhtable", "dht", 0);
    ArrayList arrayList2 = new ArrayList();
    arrayList2.add("0");
    arrayList2.add("1");
    addAttribute("dhtable", "class", 2, true, null, arrayList2);
    addAttribute("dhtable", "htableId", 2, true, null, arrayList1);
    addObjectValue("dhtable", javax.imageio.plugins.jpeg.JPEGHuffmanTable.class, true, null);
    addElement("dri", paramString, 0);
    addAttribute("dri", "interval", 2, true, null, "0", "65535", true, true);
    addElement("com", paramString, 0);
    addAttribute("com", "comment", 0, false, null);
    addObjectValue("com", byte[].class, 1, 65533);
    addElement("unknown", paramString, 0);
    addAttribute("unknown", "MarkerTag", 2, true, null, "0", "255", true, true);
    addObjectValue("unknown", byte[].class, 1, 65533);
  }
  
  public boolean canNodeAppear(String paramString, ImageTypeSpecifier paramImageTypeSpecifier) { return isInSubtree(paramString, getRootName()); }
  
  protected boolean isInSubtree(String paramString1, String paramString2) {
    if (paramString1.equals(paramString2))
      return true; 
    String[] arrayOfString = getChildNames(paramString1);
    for (byte b = 0; b < arrayOfString.length; b++) {
      if (isInSubtree(paramString1, arrayOfString[b]))
        return true; 
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\jpeg\JPEGMetadataFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */