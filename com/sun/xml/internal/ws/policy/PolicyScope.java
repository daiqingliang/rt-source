package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

final class PolicyScope {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyScope.class);
  
  private final List<PolicySubject> subjects = new LinkedList();
  
  PolicyScope(List<PolicySubject> paramList) {
    if (paramList != null && !paramList.isEmpty())
      this.subjects.addAll(paramList); 
  }
  
  void attach(PolicySubject paramPolicySubject) {
    if (paramPolicySubject == null)
      throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0020_SUBJECT_PARAM_MUST_NOT_BE_NULL())); 
    this.subjects.add(paramPolicySubject);
  }
  
  void dettachAllSubjects() { this.subjects.clear(); }
  
  Policy getEffectivePolicy(PolicyMerger paramPolicyMerger) throws PolicyException {
    LinkedList linkedList = new LinkedList();
    for (PolicySubject policySubject : this.subjects)
      linkedList.add(policySubject.getEffectivePolicy(paramPolicyMerger)); 
    return paramPolicyMerger.merge(linkedList);
  }
  
  Collection<PolicySubject> getPolicySubjects() { return this.subjects; }
  
  public String toString() { return toString(0, new StringBuffer()).toString(); }
  
  StringBuffer toString(int paramInt, StringBuffer paramStringBuffer) {
    String str = PolicyUtils.Text.createIndent(paramInt);
    paramStringBuffer.append(str).append("policy scope {").append(PolicyUtils.Text.NEW_LINE);
    for (PolicySubject policySubject : this.subjects)
      policySubject.toString(paramInt + 1, paramStringBuffer).append(PolicyUtils.Text.NEW_LINE); 
    paramStringBuffer.append(str).append('}');
    return paramStringBuffer;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\PolicyScope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */