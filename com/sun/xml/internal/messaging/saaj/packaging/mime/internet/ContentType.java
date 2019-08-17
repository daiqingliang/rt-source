package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

public final class ContentType {
  private String primaryType;
  
  private String subType;
  
  private ParameterList list;
  
  public ContentType() {}
  
  public ContentType(String paramString1, String paramString2, ParameterList paramParameterList) {
    this.primaryType = paramString1;
    this.subType = paramString2;
    if (paramParameterList == null)
      paramParameterList = new ParameterList(); 
    this.list = paramParameterList;
  }
  
  public ContentType(String paramString) throws ParseException {
    HeaderTokenizer headerTokenizer = new HeaderTokenizer(paramString, "()<>@,;:\\\"\t []/?=");
    HeaderTokenizer.Token token = headerTokenizer.next();
    if (token.getType() != -1)
      throw new ParseException(); 
    this.primaryType = token.getValue();
    token = headerTokenizer.next();
    if ((char)token.getType() != '/')
      throw new ParseException(); 
    token = headerTokenizer.next();
    if (token.getType() != -1)
      throw new ParseException(); 
    this.subType = token.getValue();
    String str = headerTokenizer.getRemainder();
    if (str != null)
      this.list = new ParameterList(str); 
  }
  
  public ContentType copy() { return new ContentType(this.primaryType, this.subType, this.list.copy()); }
  
  public String getPrimaryType() { return this.primaryType; }
  
  public String getSubType() { return this.subType; }
  
  public String getBaseType() { return this.primaryType + '/' + this.subType; }
  
  public String getParameter(String paramString) { return (this.list == null) ? null : this.list.get(paramString); }
  
  public ParameterList getParameterList() { return this.list; }
  
  public void setPrimaryType(String paramString) throws ParseException { this.primaryType = paramString; }
  
  public void setSubType(String paramString) throws ParseException { this.subType = paramString; }
  
  public void setParameter(String paramString1, String paramString2) {
    if (this.list == null)
      this.list = new ParameterList(); 
    this.list.set(paramString1, paramString2);
  }
  
  public void setParameterList(ParameterList paramParameterList) { this.list = paramParameterList; }
  
  public String toString() {
    if (this.primaryType == null || this.subType == null)
      return null; 
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(this.primaryType).append('/').append(this.subType);
    if (this.list != null)
      stringBuffer.append(this.list.toString()); 
    return stringBuffer.toString();
  }
  
  public boolean match(ContentType paramContentType) {
    if (!this.primaryType.equalsIgnoreCase(paramContentType.getPrimaryType()))
      return false; 
    String str = paramContentType.getSubType();
    return (this.subType.charAt(0) == '*' || str.charAt(0) == '*') ? true : (!!this.subType.equalsIgnoreCase(str));
  }
  
  public boolean match(String paramString) {
    try {
      return match(new ContentType(paramString));
    } catch (ParseException parseException) {
      return false;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\ContentType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */