package com.sun.xml.internal.ws.api.databinding;

public static enum SoapBodyStyle {
  DocumentBare, DocumentWrapper, RpcLiteral, RpcEncoded, Unspecificed;
  
  public boolean isDocument() { return (equals(DocumentBare) || equals(DocumentWrapper)); }
  
  public boolean isRpc() { return (equals(RpcLiteral) || equals(RpcEncoded)); }
  
  public boolean isLiteral() { return (equals(RpcLiteral) || isDocument()); }
  
  public boolean isBare() { return equals(DocumentBare); }
  
  public boolean isDocumentWrapper() { return equals(DocumentWrapper); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\databinding\SoapBodyStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */