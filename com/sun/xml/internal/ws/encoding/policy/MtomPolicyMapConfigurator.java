package com.sun.xml.internal.ws.encoding.policy;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicySubject;
import com.sun.xml.internal.ws.policy.jaxws.spi.PolicyMapConfigurator;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.internal.ws.policy.subject.WsdlBindingSubject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.MTOMFeature;

public class MtomPolicyMapConfigurator implements PolicyMapConfigurator {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(MtomPolicyMapConfigurator.class);
  
  public Collection<PolicySubject> update(PolicyMap paramPolicyMap, SEIModel paramSEIModel, WSBinding paramWSBinding) throws PolicyException {
    LOGGER.entering(new Object[] { paramPolicyMap, paramSEIModel, paramWSBinding });
    ArrayList arrayList = new ArrayList();
    if (paramPolicyMap != null) {
      MTOMFeature mTOMFeature = (MTOMFeature)paramWSBinding.getFeature(MTOMFeature.class);
      if (LOGGER.isLoggable(Level.FINEST))
        LOGGER.finest("mtomFeature = " + mTOMFeature); 
      if (mTOMFeature != null && mTOMFeature.isEnabled()) {
        QName qName = paramSEIModel.getBoundPortTypeName();
        WsdlBindingSubject wsdlBindingSubject = WsdlBindingSubject.createBindingSubject(qName);
        Policy policy = createMtomPolicy(qName);
        PolicySubject policySubject = new PolicySubject(wsdlBindingSubject, policy);
        arrayList.add(policySubject);
        if (LOGGER.isLoggable(Level.FINEST))
          LOGGER.fine("Added MTOM policy with ID \"" + policy.getIdOrName() + "\" to binding element \"" + qName + "\""); 
      } 
    } 
    LOGGER.exiting(arrayList);
    return arrayList;
  }
  
  private Policy createMtomPolicy(QName paramQName) {
    ArrayList arrayList1 = new ArrayList(1);
    ArrayList arrayList2 = new ArrayList(1);
    arrayList2.add(new MtomAssertion());
    arrayList1.add(AssertionSet.createAssertionSet(arrayList2));
    return Policy.createPolicy(null, paramQName.getLocalPart() + "_MTOM_Policy", arrayList1);
  }
  
  static class MtomAssertion extends PolicyAssertion {
    private static final AssertionData mtomData = AssertionData.createAssertionData(EncodingConstants.OPTIMIZED_MIME_SERIALIZATION_ASSERTION);
    
    MtomAssertion() { super(mtomData, null, null); }
    
    static  {
      mtomData.setOptionalAttribute(true);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\policy\MtomPolicyMapConfigurator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */