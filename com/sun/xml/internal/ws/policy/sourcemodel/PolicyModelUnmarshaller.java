package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.PolicyException;

public abstract class PolicyModelUnmarshaller {
  private static final PolicyModelUnmarshaller xmlUnmarshaller = new XmlPolicyModelUnmarshaller();
  
  public abstract PolicySourceModel unmarshalModel(Object paramObject) throws PolicyException;
  
  public static PolicyModelUnmarshaller getXmlUnmarshaller() { return xmlUnmarshaller; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\PolicyModelUnmarshaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */