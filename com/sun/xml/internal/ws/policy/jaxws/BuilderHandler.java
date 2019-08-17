package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.api.policy.ModelTranslator;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMapExtender;
import com.sun.xml.internal.ws.policy.PolicySubject;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.internal.ws.resources.PolicyMessages;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

abstract class BuilderHandler {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(BuilderHandler.class);
  
  Map<String, PolicySourceModel> policyStore;
  
  Collection<String> policyURIs;
  
  Object policySubject;
  
  BuilderHandler(Collection<String> paramCollection, Map<String, PolicySourceModel> paramMap, Object paramObject) {
    this.policyStore = paramMap;
    this.policyURIs = paramCollection;
    this.policySubject = paramObject;
  }
  
  final void populate(PolicyMapExtender paramPolicyMapExtender) throws PolicyException {
    if (null == paramPolicyMapExtender)
      throw (PolicyException)LOGGER.logSevereException(new PolicyException(PolicyMessages.WSP_1006_POLICY_MAP_EXTENDER_CAN_NOT_BE_NULL())); 
    doPopulate(paramPolicyMapExtender);
  }
  
  protected abstract void doPopulate(PolicyMapExtender paramPolicyMapExtender) throws PolicyException;
  
  final Collection<Policy> getPolicies() throws PolicyException {
    if (null == this.policyURIs)
      throw (PolicyException)LOGGER.logSevereException(new PolicyException(PolicyMessages.WSP_1004_POLICY_URIS_CAN_NOT_BE_NULL())); 
    if (null == this.policyStore)
      throw (PolicyException)LOGGER.logSevereException(new PolicyException(PolicyMessages.WSP_1010_NO_POLICIES_DEFINED())); 
    ArrayList arrayList = new ArrayList(this.policyURIs.size());
    for (String str : this.policyURIs) {
      PolicySourceModel policySourceModel = (PolicySourceModel)this.policyStore.get(str);
      if (policySourceModel == null)
        throw (PolicyException)LOGGER.logSevereException(new PolicyException(PolicyMessages.WSP_1005_POLICY_REFERENCE_DOES_NOT_EXIST(str))); 
      arrayList.add(ModelTranslator.getTranslator().translate(policySourceModel));
    } 
    return arrayList;
  }
  
  final Collection<PolicySubject> getPolicySubjects() throws PolicyException {
    Collection collection = getPolicies();
    ArrayList arrayList = new ArrayList(collection.size());
    for (Policy policy : collection)
      arrayList.add(new PolicySubject(this.policySubject, policy)); 
    return arrayList;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\jaxws\BuilderHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */