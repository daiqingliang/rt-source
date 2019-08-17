package com.sun.xml.internal.ws.api.model;

public final class ParameterBinding {
  public static final ParameterBinding BODY = new ParameterBinding(Kind.BODY, null);
  
  public static final ParameterBinding HEADER = new ParameterBinding(Kind.HEADER, null);
  
  public static final ParameterBinding UNBOUND = new ParameterBinding(Kind.UNBOUND, null);
  
  public final Kind kind;
  
  private String mimeType;
  
  public static ParameterBinding createAttachment(String paramString) { return new ParameterBinding(Kind.ATTACHMENT, paramString); }
  
  private ParameterBinding(Kind paramKind, String paramString) {
    this.kind = paramKind;
    this.mimeType = paramString;
  }
  
  public String toString() { return this.kind.toString(); }
  
  public String getMimeType() {
    if (!isAttachment())
      throw new IllegalStateException(); 
    return this.mimeType;
  }
  
  public boolean isBody() { return (this == BODY); }
  
  public boolean isHeader() { return (this == HEADER); }
  
  public boolean isUnbound() { return (this == UNBOUND); }
  
  public boolean isAttachment() { return (this.kind == Kind.ATTACHMENT); }
  
  public enum Kind {
    BODY, HEADER, UNBOUND, ATTACHMENT;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\model\ParameterBinding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */