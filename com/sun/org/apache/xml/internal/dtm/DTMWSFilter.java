package com.sun.org.apache.xml.internal.dtm;

public interface DTMWSFilter {
  public static final short NOTSTRIP = 1;
  
  public static final short STRIP = 2;
  
  public static final short INHERIT = 3;
  
  short getShouldStripSpace(int paramInt, DTM paramDTM);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\DTMWSFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */