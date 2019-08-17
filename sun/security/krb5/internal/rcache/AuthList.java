package sun.security.krb5.internal.rcache;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.KrbApErrException;

public class AuthList {
  private final LinkedList<AuthTimeWithHash> entries;
  
  private final int lifespan;
  
  public AuthList(int paramInt) {
    this.lifespan = paramInt;
    this.entries = new LinkedList();
  }
  
  public void put(AuthTimeWithHash paramAuthTimeWithHash, KerberosTime paramKerberosTime) throws KrbApErrException {
    if (this.entries.isEmpty()) {
      this.entries.addFirst(paramAuthTimeWithHash);
      this.oldestTime = paramAuthTimeWithHash.ctime;
      return;
    } 
    AuthTimeWithHash authTimeWithHash = (AuthTimeWithHash)this.entries.getFirst();
    int i = authTimeWithHash.compareTo(paramAuthTimeWithHash);
    if (i < 0) {
      this.entries.addFirst(paramAuthTimeWithHash);
    } else {
      if (i == 0)
        throw new KrbApErrException(34); 
      ListIterator listIterator = this.entries.listIterator(1);
      boolean bool = false;
      while (listIterator.hasNext()) {
        authTimeWithHash = (AuthTimeWithHash)listIterator.next();
        i = authTimeWithHash.compareTo(paramAuthTimeWithHash);
        if (i < 0) {
          this.entries.add(this.entries.indexOf(authTimeWithHash), paramAuthTimeWithHash);
          bool = true;
          break;
        } 
        if (i == 0)
          throw new KrbApErrException(34); 
      } 
      if (!bool)
        this.entries.addLast(paramAuthTimeWithHash); 
    } 
    long l = (paramKerberosTime.getSeconds() - this.lifespan);
    if (this.oldestTime > l - 5L)
      return; 
    while (!this.entries.isEmpty()) {
      AuthTimeWithHash authTimeWithHash1 = (AuthTimeWithHash)this.entries.removeLast();
      if (authTimeWithHash1.ctime >= l) {
        this.entries.addLast(authTimeWithHash1);
        this.oldestTime = authTimeWithHash1.ctime;
        return;
      } 
    } 
    this.oldestTime = Integer.MIN_VALUE;
  }
  
  public boolean isEmpty() { return this.entries.isEmpty(); }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    Iterator iterator = this.entries.descendingIterator();
    int i = this.entries.size();
    while (iterator.hasNext()) {
      AuthTimeWithHash authTimeWithHash = (AuthTimeWithHash)iterator.next();
      stringBuilder.append('#').append(i--).append(": ").append(authTimeWithHash.toString()).append('\n');
    } 
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\rcache\AuthList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */