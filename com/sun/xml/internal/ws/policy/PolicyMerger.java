package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public final class PolicyMerger {
  private static final PolicyMerger merger = new PolicyMerger();
  
  public static PolicyMerger getMerger() { return merger; }
  
  public Policy merge(Collection<Policy> paramCollection) {
    if (paramCollection == null || paramCollection.isEmpty())
      return null; 
    if (paramCollection.size() == 1)
      return (Policy)paramCollection.iterator().next(); 
    LinkedList linkedList = new LinkedList();
    StringBuilder stringBuilder = new StringBuilder();
    NamespaceVersion namespaceVersion = ((Policy)paramCollection.iterator().next()).getNamespaceVersion();
    for (Policy policy : paramCollection) {
      linkedList.add(policy.getContent());
      if (namespaceVersion.compareTo(policy.getNamespaceVersion()) < 0)
        namespaceVersion = policy.getNamespaceVersion(); 
      String str = policy.getId();
      if (str != null) {
        if (stringBuilder.length() > 0)
          stringBuilder.append('-'); 
        stringBuilder.append(str);
      } 
    } 
    Collection collection = PolicyUtils.Collections.combine(null, linkedList, false);
    if (collection == null || collection.isEmpty())
      return Policy.createNullPolicy(namespaceVersion, null, (stringBuilder.length() == 0) ? null : stringBuilder.toString()); 
    ArrayList arrayList = new ArrayList(collection.size());
    for (Collection collection1 : collection)
      arrayList.add(AssertionSet.createMergedAssertionSet(collection1)); 
    return Policy.createPolicy(namespaceVersion, null, (stringBuilder.length() == 0) ? null : stringBuilder.toString(), arrayList);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\PolicyMerger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */