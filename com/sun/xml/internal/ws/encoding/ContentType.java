package com.sun.xml.internal.ws.encoding;

import javax.xml.ws.WebServiceException;

public final class ContentType {
  private String primaryType;
  
  private String subType;
  
  private ParameterList list;
  
  public ContentType(String paramString) throws WebServiceException {
    HeaderTokenizer headerTokenizer = new HeaderTokenizer(paramString, "()<>@,;:\\\"\t []/?=");
    HeaderTokenizer.Token token = headerTokenizer.next();
    if (token.getType() != -1)
      throw new WebServiceException(); 
    this.primaryType = token.getValue();
    token = headerTokenizer.next();
    if ((char)token.getType() != '/')
      throw new WebServiceException(); 
    token = headerTokenizer.next();
    if (token.getType() != -1)
      throw new WebServiceException(); 
    this.subType = token.getValue();
    String str = headerTokenizer.getRemainder();
    if (str != null)
      this.list = new ParameterList(str); 
  }
  
  public String getPrimaryType() { return this.primaryType; }
  
  public String getSubType() { return this.subType; }
  
  public String getBaseType() { return this.primaryType + '/' + this.subType; }
  
  public String getParameter(String paramString) { return (this.list == null) ? null : this.list.get(paramString); }
  
  public ParameterList getParameterList() { return this.list; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\ContentType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */