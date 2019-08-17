package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public final class PolicyIntersector {
  private static final PolicyIntersector STRICT_INTERSECTOR = new PolicyIntersector(CompatibilityMode.STRICT);
  
  private static final PolicyIntersector LAX_INTERSECTOR = new PolicyIntersector(CompatibilityMode.LAX);
  
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyIntersector.class);
  
  private CompatibilityMode mode;
  
  private PolicyIntersector(CompatibilityMode paramCompatibilityMode) { this.mode = paramCompatibilityMode; }
  
  public static PolicyIntersector createStrictPolicyIntersector() { return STRICT_INTERSECTOR; }
  
  public static PolicyIntersector createLaxPolicyIntersector() { return LAX_INTERSECTOR; }
  
  public Policy intersect(Policy... paramVarArgs) {
    if (paramVarArgs == null || paramVarArgs.length == 0)
      throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0056_NEITHER_NULL_NOR_EMPTY_POLICY_COLLECTION_EXPECTED())); 
    if (paramVarArgs.length == 1)
      return paramVarArgs[0]; 
    boolean bool1 = false;
    boolean bool2 = true;
    NamespaceVersion namespaceVersion = null;
    for (Policy policy : paramVarArgs) {
      if (policy.isEmpty()) {
        bool1 = true;
      } else {
        if (policy.isNull())
          bool1 = true; 
        bool2 = false;
      } 
      if (namespaceVersion == null) {
        namespaceVersion = policy.getNamespaceVersion();
      } else if (namespaceVersion.compareTo(policy.getNamespaceVersion()) < 0) {
        namespaceVersion = policy.getNamespaceVersion();
      } 
      if (bool1 && !bool2)
        return Policy.createNullPolicy(namespaceVersion, null, null); 
    } 
    namespaceVersion = (namespaceVersion != null) ? namespaceVersion : NamespaceVersion.getLatestVersion();
    if (bool2)
      return Policy.createEmptyPolicy(namespaceVersion, null, null); 
    LinkedList linkedList1 = new LinkedList(paramVarArgs[0].getContent());
    LinkedList linkedList2 = new LinkedList();
    ArrayList arrayList = new ArrayList(2);
    for (byte b = 1; b < paramVarArgs.length; b++) {
      Collection collection = paramVarArgs[b].getContent();
      linkedList2.clear();
      linkedList2.addAll(linkedList1);
      linkedList1.clear();
      AssertionSet assertionSet;
      while ((assertionSet = (AssertionSet)linkedList2.poll()) != null) {
        for (AssertionSet assertionSet1 : collection) {
          if (assertionSet.isCompatibleWith(assertionSet1, this.mode)) {
            arrayList.add(assertionSet);
            arrayList.add(assertionSet1);
            linkedList1.add(AssertionSet.createMergedAssertionSet(arrayList));
            arrayList.clear();
          } 
        } 
      } 
    } 
    return Policy.createPolicy(namespaceVersion, null, null, linkedList1);
  }
  
  enum CompatibilityMode {
    STRICT, LAX;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\PolicyIntersector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */