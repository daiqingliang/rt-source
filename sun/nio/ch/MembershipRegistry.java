package sun.nio.ch;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.channels.MembershipKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class MembershipRegistry {
  private Map<InetAddress, List<MembershipKeyImpl>> groups = null;
  
  MembershipKey checkMembership(InetAddress paramInetAddress1, NetworkInterface paramNetworkInterface, InetAddress paramInetAddress2) {
    if (this.groups != null) {
      List list = (List)this.groups.get(paramInetAddress1);
      if (list != null)
        for (MembershipKeyImpl membershipKeyImpl : list) {
          if (membershipKeyImpl.networkInterface().equals(paramNetworkInterface)) {
            if (paramInetAddress2 == null) {
              if (membershipKeyImpl.sourceAddress() == null)
                return membershipKeyImpl; 
              throw new IllegalStateException("Already a member to receive all packets");
            } 
            if (membershipKeyImpl.sourceAddress() == null)
              throw new IllegalStateException("Already have source-specific membership"); 
            if (paramInetAddress2.equals(membershipKeyImpl.sourceAddress()))
              return membershipKeyImpl; 
          } 
        }  
    } 
    return null;
  }
  
  void add(MembershipKeyImpl paramMembershipKeyImpl) {
    List list;
    InetAddress inetAddress = paramMembershipKeyImpl.group();
    if (this.groups == null) {
      this.groups = new HashMap();
      list = null;
    } else {
      list = (List)this.groups.get(inetAddress);
    } 
    if (list == null) {
      list = new LinkedList();
      this.groups.put(inetAddress, list);
    } 
    list.add(paramMembershipKeyImpl);
  }
  
  void remove(MembershipKeyImpl paramMembershipKeyImpl) {
    InetAddress inetAddress = paramMembershipKeyImpl.group();
    List list = (List)this.groups.get(inetAddress);
    if (list != null) {
      Iterator iterator = list.iterator();
      while (iterator.hasNext()) {
        if (iterator.next() == paramMembershipKeyImpl) {
          iterator.remove();
          break;
        } 
      } 
      if (list.isEmpty())
        this.groups.remove(inetAddress); 
    } 
  }
  
  void invalidateAll() {
    if (this.groups != null)
      for (InetAddress inetAddress : this.groups.keySet()) {
        for (MembershipKeyImpl membershipKeyImpl : (List)this.groups.get(inetAddress))
          membershipKeyImpl.invalidate(); 
      }  
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\MembershipRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */