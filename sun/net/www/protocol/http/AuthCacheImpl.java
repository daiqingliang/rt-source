package sun.net.www.protocol.http;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

public class AuthCacheImpl implements AuthCache {
  HashMap<String, LinkedList<AuthCacheValue>> hashtable = new HashMap();
  
  public void setMap(HashMap<String, LinkedList<AuthCacheValue>> paramHashMap) { this.hashtable = paramHashMap; }
  
  public void put(String paramString, AuthCacheValue paramAuthCacheValue) {
    LinkedList linkedList = (LinkedList)this.hashtable.get(paramString);
    String str = paramAuthCacheValue.getPath();
    if (linkedList == null) {
      linkedList = new LinkedList();
      this.hashtable.put(paramString, linkedList);
    } 
    ListIterator listIterator = linkedList.listIterator();
    while (listIterator.hasNext()) {
      AuthenticationInfo authenticationInfo = (AuthenticationInfo)listIterator.next();
      if (authenticationInfo.path == null || authenticationInfo.path.startsWith(str))
        listIterator.remove(); 
    } 
    listIterator.add(paramAuthCacheValue);
  }
  
  public AuthCacheValue get(String paramString1, String paramString2) {
    Object object = null;
    LinkedList linkedList = (LinkedList)this.hashtable.get(paramString1);
    if (linkedList == null || linkedList.size() == 0)
      return null; 
    if (paramString2 == null)
      return (AuthenticationInfo)linkedList.get(0); 
    ListIterator listIterator = linkedList.listIterator();
    while (listIterator.hasNext()) {
      AuthenticationInfo authenticationInfo = (AuthenticationInfo)listIterator.next();
      if (paramString2.startsWith(authenticationInfo.path))
        return authenticationInfo; 
    } 
    return null;
  }
  
  public void remove(String paramString, AuthCacheValue paramAuthCacheValue) {
    LinkedList linkedList = (LinkedList)this.hashtable.get(paramString);
    if (linkedList == null)
      return; 
    if (paramAuthCacheValue == null) {
      linkedList.clear();
      return;
    } 
    ListIterator listIterator = linkedList.listIterator();
    while (listIterator.hasNext()) {
      AuthenticationInfo authenticationInfo = (AuthenticationInfo)listIterator.next();
      if (paramAuthCacheValue.equals(authenticationInfo))
        listIterator.remove(); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\http\AuthCacheImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */