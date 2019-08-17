package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

public class ContentDisposition {
  private String disposition;
  
  private ParameterList list;
  
  public ContentDisposition() {}
  
  public ContentDisposition(String paramString, ParameterList paramParameterList) {
    this.disposition = paramString;
    this.list = paramParameterList;
  }
  
  public ContentDisposition(String paramString) throws ParseException {
    HeaderTokenizer headerTokenizer = new HeaderTokenizer(paramString, "()<>@,;:\\\"\t []/?=");
    HeaderTokenizer.Token token = headerTokenizer.next();
    if (token.getType() != -1)
      throw new ParseException(); 
    this.disposition = token.getValue();
    String str = headerTokenizer.getRemainder();
    if (str != null)
      this.list = new ParameterList(str); 
  }
  
  public String getDisposition() { return this.disposition; }
  
  public String getParameter(String paramString) { return (this.list == null) ? null : this.list.get(paramString); }
  
  public ParameterList getParameterList() { return this.list; }
  
  public void setDisposition(String paramString) throws ParseException { this.disposition = paramString; }
  
  public void setParameter(String paramString1, String paramString2) {
    if (this.list == null)
      this.list = new ParameterList(); 
    this.list.set(paramString1, paramString2);
  }
  
  public void setParameterList(ParameterList paramParameterList) { this.list = paramParameterList; }
  
  public String toString() {
    if (this.disposition == null)
      return null; 
    if (this.list == null)
      return this.disposition; 
    StringBuffer stringBuffer = new StringBuffer(this.disposition);
    stringBuffer.append(this.list.toString(stringBuffer.length() + 21));
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\ContentDisposition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */