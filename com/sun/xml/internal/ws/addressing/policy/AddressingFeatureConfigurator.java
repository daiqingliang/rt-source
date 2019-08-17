package com.sun.xml.internal.ws.addressing.policy;

import com.sun.xml.internal.bind.util.Which;
import com.sun.xml.internal.ws.addressing.W3CAddressingMetadataConstants;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.NestedPolicy;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import com.sun.xml.internal.ws.policy.jaxws.spi.PolicyFeatureConfigurator;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.resources.ModelerMessages;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.AddressingFeature;

public class AddressingFeatureConfigurator implements PolicyFeatureConfigurator {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(AddressingFeatureConfigurator.class);
  
  private static final QName[] ADDRESSING_ASSERTIONS = { new QName(AddressingVersion.MEMBER.policyNsUri, "UsingAddressing") };
  
  public Collection<WebServiceFeature> getFeatures(PolicyMapKey paramPolicyMapKey, PolicyMap paramPolicyMap) throws PolicyException {
    LOGGER.entering(new Object[] { paramPolicyMapKey, paramPolicyMap });
    LinkedList linkedList = new LinkedList();
    if (paramPolicyMapKey != null && paramPolicyMap != null) {
      Policy policy = paramPolicyMap.getEndpointEffectivePolicy(paramPolicyMapKey);
      for (QName qName : ADDRESSING_ASSERTIONS) {
        if (policy != null && policy.contains(qName))
          for (AssertionSet assertionSet : policy) {
            for (PolicyAssertion policyAssertion : assertionSet) {
              if (policyAssertion.getName().equals(qName)) {
                WebServiceFeature webServiceFeature = AddressingVersion.getFeature(qName.getNamespaceURI(), true, !policyAssertion.isOptional());
                if (LOGGER.isLoggable(Level.FINE))
                  LOGGER.fine("Added addressing feature \"" + webServiceFeature + "\" for element \"" + paramPolicyMapKey + "\""); 
                linkedList.add(webServiceFeature);
              } 
            } 
          }  
      } 
      if (policy != null && policy.contains(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION))
        for (AssertionSet assertionSet : policy) {
          for (PolicyAssertion policyAssertion : assertionSet) {
            if (policyAssertion.getName().equals(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION)) {
              AddressingFeature addressingFeature;
              NestedPolicy nestedPolicy = policyAssertion.getNestedPolicy();
              boolean bool1 = false;
              boolean bool2 = false;
              if (nestedPolicy != null) {
                bool1 = nestedPolicy.contains(W3CAddressingMetadataConstants.WSAM_ANONYMOUS_NESTED_ASSERTION);
                bool2 = nestedPolicy.contains(W3CAddressingMetadataConstants.WSAM_NONANONYMOUS_NESTED_ASSERTION);
              } 
              if (bool1 && bool2)
                throw new WebServiceException("Only one among AnonymousResponses and NonAnonymousResponses can be nested in an Addressing assertion"); 
              try {
                if (bool1) {
                  addressingFeature = new AddressingFeature(true, !policyAssertion.isOptional(), AddressingFeature.Responses.ANONYMOUS);
                } else if (bool2) {
                  addressingFeature = new AddressingFeature(true, !policyAssertion.isOptional(), AddressingFeature.Responses.NON_ANONYMOUS);
                } else {
                  addressingFeature = new AddressingFeature(true, !policyAssertion.isOptional());
                } 
              } catch (NoSuchMethodError noSuchMethodError) {
                throw (PolicyException)LOGGER.logSevereException(new PolicyException(ModelerMessages.RUNTIME_MODELER_ADDRESSING_RESPONSES_NOSUCHMETHOD(toJar(Which.which(AddressingFeature.class))), noSuchMethodError));
              } 
              if (LOGGER.isLoggable(Level.FINE))
                LOGGER.fine("Added addressing feature \"" + addressingFeature + "\" for element \"" + paramPolicyMapKey + "\""); 
              linkedList.add(addressingFeature);
            } 
          } 
        }  
    } 
    LOGGER.exiting(linkedList);
    return linkedList;
  }
  
  private static String toJar(String paramString) {
    if (!paramString.startsWith("jar:"))
      return paramString; 
    paramString = paramString.substring(4);
    return paramString.substring(0, paramString.lastIndexOf('!'));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\addressing\policy\AddressingFeatureConfigurator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */