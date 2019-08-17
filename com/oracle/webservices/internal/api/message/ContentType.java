package com.oracle.webservices.internal.api.message;

import com.sun.xml.internal.ws.encoding.ContentTypeImpl;

public interface ContentType {
  String getContentType();
  
  String getSOAPActionHeader();
  
  String getAcceptHeader();
  
  public static class Builder {
    private String contentType;
    
    private String soapAction;
    
    private String accept;
    
    private String charset;
    
    public Builder contentType(String param1String) {
      this.contentType = param1String;
      return this;
    }
    
    public Builder soapAction(String param1String) {
      this.soapAction = param1String;
      return this;
    }
    
    public Builder accept(String param1String) {
      this.accept = param1String;
      return this;
    }
    
    public Builder charset(String param1String) {
      this.charset = param1String;
      return this;
    }
    
    public ContentType build() { return new ContentTypeImpl(this.contentType, this.soapAction, this.accept, this.charset); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\webservices\internal\api\message\ContentType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */