package com.sun.xml.internal.bind.v2.model.core;

public static enum PropertyKind {
  VALUE(true, false, 2147483647),
  ATTRIBUTE(false, false, 2147483647),
  ELEMENT(true, true, 0),
  REFERENCE(false, true, 1),
  MAP(false, true, 2);
  
  public final boolean canHaveXmlMimeType;
  
  public final boolean isOrdered;
  
  public final int propertyIndex;
  
  PropertyKind(boolean paramBoolean1, int paramInt1, int paramInt2) {
    this.canHaveXmlMimeType = paramBoolean1;
    this.isOrdered = paramInt1;
    this.propertyIndex = paramInt2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\core\PropertyKind.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */