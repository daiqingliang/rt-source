package com.sun.xml.internal.ws.encoding;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.pipe.ContentType;

public final class ContentTypeImpl implements ContentType {
  @NotNull
  private final String contentType;
  
  @NotNull
  private final String soapAction;
  
  private String accept;
  
  @Nullable
  private final String charset;
  
  private String boundary;
  
  private String boundaryParameter;
  
  private String rootId;
  
  private ContentType internalContentType;
  
  public ContentTypeImpl(String paramString) { this(paramString, null, null); }
  
  public ContentTypeImpl(String paramString1, @Nullable String paramString2) { this(paramString1, paramString2, null); }
  
  public ContentTypeImpl(String paramString1, @Nullable String paramString2, @Nullable String paramString3) { this(paramString1, paramString2, paramString3, null); }
  
  public ContentTypeImpl(String paramString1, @Nullable String paramString2, @Nullable String paramString3, String paramString4) {
    this.contentType = paramString1;
    this.accept = paramString3;
    this.soapAction = getQuotedSOAPAction(paramString2);
    if (paramString4 == null) {
      String str = null;
      try {
        this.internalContentType = new ContentType(paramString1);
        str = this.internalContentType.getParameter("charset");
      } catch (Exception exception) {}
      this.charset = str;
    } else {
      this.charset = paramString4;
    } 
  }
  
  @Nullable
  public String getCharSet() { return this.charset; }
  
  private String getQuotedSOAPAction(String paramString) { return (paramString == null || paramString.length() == 0) ? "\"\"" : ((paramString.charAt(0) != '"' && paramString.charAt(paramString.length() - 1) != '"') ? ("\"" + paramString + "\"") : paramString); }
  
  public String getContentType() { return this.contentType; }
  
  public String getSOAPActionHeader() { return this.soapAction; }
  
  public String getAcceptHeader() { return this.accept; }
  
  public void setAcceptHeader(String paramString) { this.accept = paramString; }
  
  public String getBoundary() {
    if (this.boundary == null) {
      if (this.internalContentType == null)
        this.internalContentType = new ContentType(this.contentType); 
      this.boundary = this.internalContentType.getParameter("boundary");
    } 
    return this.boundary;
  }
  
  public void setBoundary(String paramString) { this.boundary = paramString; }
  
  public String getBoundaryParameter() { return this.boundaryParameter; }
  
  public void setBoundaryParameter(String paramString) { this.boundaryParameter = paramString; }
  
  public String getRootId() {
    if (this.rootId == null) {
      if (this.internalContentType == null)
        this.internalContentType = new ContentType(this.contentType); 
      this.rootId = this.internalContentType.getParameter("start");
    } 
    return this.rootId;
  }
  
  public void setRootId(String paramString) { this.rootId = paramString; }
  
  public static class Builder {
    public String contentType;
    
    public String soapAction;
    
    public String accept;
    
    public String charset;
    
    public ContentTypeImpl build() { return new ContentTypeImpl(this.contentType, this.soapAction, this.accept, this.charset); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\ContentTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */