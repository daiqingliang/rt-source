package java.net;

final class UrlDeserializedState {
  private final String protocol;
  
  private final String host;
  
  private final int port;
  
  private final String authority;
  
  private final String file;
  
  private final String ref;
  
  private final int hashCode;
  
  public UrlDeserializedState(String paramString1, String paramString2, int paramInt1, String paramString3, String paramString4, String paramString5, int paramInt2) {
    this.protocol = paramString1;
    this.host = paramString2;
    this.port = paramInt1;
    this.authority = paramString3;
    this.file = paramString4;
    this.ref = paramString5;
    this.hashCode = paramInt2;
  }
  
  String getProtocol() { return this.protocol; }
  
  String getHost() { return this.host; }
  
  String getAuthority() { return this.authority; }
  
  int getPort() { return this.port; }
  
  String getFile() { return this.file; }
  
  String getRef() { return this.ref; }
  
  int getHashCode() { return this.hashCode; }
  
  String reconstituteUrlString() {
    int i = this.protocol.length() + 1;
    if (this.authority != null && this.authority.length() > 0)
      i += 2 + this.authority.length(); 
    if (this.file != null)
      i += this.file.length(); 
    if (this.ref != null)
      i += 1 + this.ref.length(); 
    StringBuilder stringBuilder = new StringBuilder(i);
    stringBuilder.append(this.protocol);
    stringBuilder.append(":");
    if (this.authority != null && this.authority.length() > 0) {
      stringBuilder.append("//");
      stringBuilder.append(this.authority);
    } 
    if (this.file != null)
      stringBuilder.append(this.file); 
    if (this.ref != null) {
      stringBuilder.append("#");
      stringBuilder.append(this.ref);
    } 
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\UrlDeserializedState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */