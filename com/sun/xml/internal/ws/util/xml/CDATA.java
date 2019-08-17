package com.sun.xml.internal.ws.util.xml;

public final class CDATA {
  private String _text;
  
  public CDATA(String paramString) { this._text = paramString; }
  
  public String getText() { return this._text; }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (!(paramObject instanceof CDATA))
      return false; 
    CDATA cDATA = (CDATA)paramObject;
    return this._text.equals(cDATA._text);
  }
  
  public int hashCode() { return this._text.hashCode(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\xml\CDATA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */