package com.sun.xml.internal.ws.encoding.policy;

import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator;
import java.util.ArrayList;
import javax.xml.namespace.QName;

public class EncodingPolicyValidator implements PolicyAssertionValidator {
  private static final ArrayList<QName> serverSideSupportedAssertions = new ArrayList(3);
  
  private static final ArrayList<QName> clientSideSupportedAssertions = new ArrayList(4);
  
  public PolicyAssertionValidator.Fitness validateClientSide(PolicyAssertion paramPolicyAssertion) { return clientSideSupportedAssertions.contains(paramPolicyAssertion.getName()) ? PolicyAssertionValidator.Fitness.SUPPORTED : PolicyAssertionValidator.Fitness.UNKNOWN; }
  
  public PolicyAssertionValidator.Fitness validateServerSide(PolicyAssertion paramPolicyAssertion) {
    QName qName = paramPolicyAssertion.getName();
    return serverSideSupportedAssertions.contains(qName) ? PolicyAssertionValidator.Fitness.SUPPORTED : (clientSideSupportedAssertions.contains(qName) ? PolicyAssertionValidator.Fitness.UNSUPPORTED : PolicyAssertionValidator.Fitness.UNKNOWN);
  }
  
  public String[] declareSupportedDomains() { return new String[] { "http://schemas.xmlsoap.org/ws/2004/09/policy/optimizedmimeserialization", "http://schemas.xmlsoap.org/ws/2004/09/policy/encoding", "http://java.sun.com/xml/ns/wsit/2006/09/policy/encoding/client", "http://java.sun.com/xml/ns/wsit/2006/09/policy/fastinfoset/service" }; }
  
  static  {
    serverSideSupportedAssertions.add(EncodingConstants.OPTIMIZED_MIME_SERIALIZATION_ASSERTION);
    serverSideSupportedAssertions.add(EncodingConstants.UTF816FFFE_CHARACTER_ENCODING_ASSERTION);
    serverSideSupportedAssertions.add(EncodingConstants.OPTIMIZED_FI_SERIALIZATION_ASSERTION);
    clientSideSupportedAssertions.add(EncodingConstants.SELECT_OPTIMAL_ENCODING_ASSERTION);
    clientSideSupportedAssertions.addAll(serverSideSupportedAssertions);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\policy\EncodingPolicyValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */