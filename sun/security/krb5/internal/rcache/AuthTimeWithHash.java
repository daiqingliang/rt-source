package sun.security.krb5.internal.rcache;

import java.util.Objects;

public class AuthTimeWithHash extends AuthTime implements Comparable<AuthTimeWithHash> {
  final String hash;
  
  public AuthTimeWithHash(String paramString1, String paramString2, int paramInt1, int paramInt2, String paramString3) {
    super(paramString1, paramString2, paramInt1, paramInt2);
    this.hash = paramString3;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof AuthTimeWithHash))
      return false; 
    AuthTimeWithHash authTimeWithHash = (AuthTimeWithHash)paramObject;
    return (Objects.equals(this.hash, authTimeWithHash.hash) && Objects.equals(this.client, authTimeWithHash.client) && Objects.equals(this.server, authTimeWithHash.server) && this.ctime == authTimeWithHash.ctime && this.cusec == authTimeWithHash.cusec);
  }
  
  public int hashCode() { return Objects.hash(new Object[] { this.hash }); }
  
  public String toString() { return String.format("%d/%06d/%s/%s", new Object[] { Integer.valueOf(this.ctime), Integer.valueOf(this.cusec), this.hash, this.client }); }
  
  public int compareTo(AuthTimeWithHash paramAuthTimeWithHash) {
    int i = 0;
    if (this.ctime != paramAuthTimeWithHash.ctime) {
      i = Integer.compare(this.ctime, paramAuthTimeWithHash.ctime);
    } else if (this.cusec != paramAuthTimeWithHash.cusec) {
      i = Integer.compare(this.cusec, paramAuthTimeWithHash.cusec);
    } else {
      i = this.hash.compareTo(paramAuthTimeWithHash.hash);
    } 
    return i;
  }
  
  public boolean isSameIgnoresHash(AuthTime paramAuthTime) { return (this.client.equals(paramAuthTime.client) && this.server.equals(paramAuthTime.server) && this.ctime == paramAuthTime.ctime && this.cusec == paramAuthTime.cusec); }
  
  public byte[] encode(boolean paramBoolean) {
    String str2;
    String str1;
    if (paramBoolean) {
      str2 = (str1 = "").format("HASH:%s %d:%s %d:%s", new Object[] { this.hash, Integer.valueOf(this.client.length()), this.client, Integer.valueOf(this.server.length()), this.server });
    } else {
      str1 = this.client;
      str2 = this.server;
    } 
    return encode0(str1, str2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\rcache\AuthTimeWithHash.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */