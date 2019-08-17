package com.sun.xml.internal.ws.policy.jaxws.spi;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicySubject;
import java.util.Collection;

public interface PolicyMapConfigurator {
  Collection<PolicySubject> update(PolicyMap paramPolicyMap, SEIModel paramSEIModel, WSBinding paramWSBinding) throws PolicyException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\jaxws\spi\PolicyMapConfigurator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */