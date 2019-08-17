package com.sun.xml.internal.ws.policy.sourcemodel.wspolicy;

public static enum XmlToken {
  Policy("Policy", true),
  ExactlyOne("ExactlyOne", true),
  All("All", true),
  PolicyReference("PolicyReference", true),
  UsingPolicy("UsingPolicy", true),
  Name("Name", false),
  Optional("Optional", false),
  Ignorable("Ignorable", false),
  PolicyUris("PolicyURIs", false),
  Uri("URI", false),
  Digest("Digest", false),
  DigestAlgorithm("DigestAlgorithm", false),
  UNKNOWN("", true);
  
  private String tokenName;
  
  private boolean element;
  
  public static XmlToken resolveToken(String paramString) {
    for (XmlToken xmlToken : values()) {
      if (xmlToken.toString().equals(paramString))
        return xmlToken; 
    } 
    return UNKNOWN;
  }
  
  XmlToken(boolean paramBoolean1, boolean paramBoolean2) {
    this.tokenName = paramBoolean1;
    this.element = paramBoolean2;
  }
  
  public boolean isElement() { return this.element; }
  
  public String toString() { return this.tokenName; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\wspolicy\XmlToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */