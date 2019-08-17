package com.sun.xml.internal.ws.addressing.policy;

import com.sun.xml.internal.ws.addressing.W3CAddressingMetadataConstants;
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
import java.util.Collections;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.AddressingFeature;

public class AddressingPolicyMapConfigurator implements PolicyMapConfigurator {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(AddressingPolicyMapConfigurator.class);
  
  public Collection<PolicySubject> update(PolicyMap paramPolicyMap, SEIModel paramSEIModel, WSBinding paramWSBinding) throws PolicyException {
    LOGGER.entering(new Object[] { paramPolicyMap, paramSEIModel, paramWSBinding });
    ArrayList arrayList = new ArrayList();
    if (paramPolicyMap != null) {
      AddressingFeature addressingFeature = (AddressingFeature)paramWSBinding.getFeature(AddressingFeature.class);
      if (LOGGER.isLoggable(Level.FINEST))
        LOGGER.finest("addressingFeature = " + addressingFeature); 
      if (addressingFeature != null && addressingFeature.isEnabled())
        addWsamAddressing(arrayList, paramPolicyMap, paramSEIModel, addressingFeature); 
    } 
    LOGGER.exiting(arrayList);
    return arrayList;
  }
  
  private void addWsamAddressing(Collection<PolicySubject> paramCollection, PolicyMap paramPolicyMap, SEIModel paramSEIModel, AddressingFeature paramAddressingFeature) throws PolicyException {
    QName qName = paramSEIModel.getBoundPortTypeName();
    WsdlBindingSubject wsdlBindingSubject = WsdlBindingSubject.createBindingSubject(qName);
    Policy policy = createWsamAddressingPolicy(qName, paramAddressingFeature);
    PolicySubject policySubject = new PolicySubject(wsdlBindingSubject, policy);
    paramCollection.add(policySubject);
    if (LOGGER.isLoggable(Level.FINE))
      LOGGER.fine("Added addressing policy with ID \"" + policy.getIdOrName() + "\" to binding element \"" + qName + "\""); 
  }
  
  private Policy createWsamAddressingPolicy(QName paramQName, AddressingFeature paramAddressingFeature) {
    ArrayList arrayList1 = new ArrayList(1);
    ArrayList arrayList2 = new ArrayList(1);
    AssertionData assertionData = AssertionData.createAssertionData(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION);
    if (!paramAddressingFeature.isRequired())
      assertionData.setOptionalAttribute(true); 
    try {
      AddressingFeature.Responses responses = paramAddressingFeature.getResponses();
      if (responses == AddressingFeature.Responses.ANONYMOUS) {
        AssertionData assertionData1 = AssertionData.createAssertionData(W3CAddressingMetadataConstants.WSAM_ANONYMOUS_NESTED_ASSERTION);
        AddressingAssertion addressingAssertion = new AddressingAssertion(assertionData1, null);
        arrayList2.add(new AddressingAssertion(assertionData, AssertionSet.createAssertionSet(Collections.singleton(addressingAssertion))));
      } else if (responses == AddressingFeature.Responses.NON_ANONYMOUS) {
        AssertionData assertionData1 = AssertionData.createAssertionData(W3CAddressingMetadataConstants.WSAM_NONANONYMOUS_NESTED_ASSERTION);
        AddressingAssertion addressingAssertion = new AddressingAssertion(assertionData1, null);
        arrayList2.add(new AddressingAssertion(assertionData, AssertionSet.createAssertionSet(Collections.singleton(addressingAssertion))));
      } else {
        arrayList2.add(new AddressingAssertion(assertionData, AssertionSet.createAssertionSet(null)));
      } 
    } catch (NoSuchMethodError noSuchMethodError) {
      arrayList2.add(new AddressingAssertion(assertionData, AssertionSet.createAssertionSet(null)));
    } 
    arrayList1.add(AssertionSet.createAssertionSet(arrayList2));
    return Policy.createPolicy(null, paramQName.getLocalPart() + "_WSAM_Addressing_Policy", arrayList1);
  }
  
  private static final class AddressingAssertion extends PolicyAssertion {
    AddressingAssertion(AssertionData param1AssertionData, AssertionSet param1AssertionSet) { super(param1AssertionData, null, param1AssertionSet); }
    
    AddressingAssertion(AssertionData param1AssertionData) { super(param1AssertionData, null, null); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\addressing\policy\AddressingPolicyMapConfigurator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */