package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.PolicyException;
import java.util.Collection;

public abstract class PolicyModelMarshaller {
  private static final PolicyModelMarshaller defaultXmlMarshaller = new XmlPolicyModelMarshaller(false);
  
  private static final PolicyModelMarshaller invisibleAssertionXmlMarshaller = new XmlPolicyModelMarshaller(true);
  
  public abstract void marshal(PolicySourceModel paramPolicySourceModel, Object paramObject) throws PolicyException;
  
  public abstract void marshal(Collection<PolicySourceModel> paramCollection, Object paramObject) throws PolicyException;
  
  public static PolicyModelMarshaller getXmlMarshaller(boolean paramBoolean) { return paramBoolean ? invisibleAssertionXmlMarshaller : defaultXmlMarshaller; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\PolicyModelMarshaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */