package com.sun.xml.internal.ws.policy.sourcemodel;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public final class PolicySourceModelContext {
  Map<URI, PolicySourceModel> policyModels;
  
  private Map<URI, PolicySourceModel> getModels() {
    if (null == this.policyModels)
      this.policyModels = new HashMap(); 
    return this.policyModels;
  }
  
  public void addModel(URI paramURI, PolicySourceModel paramPolicySourceModel) { getModels().put(paramURI, paramPolicySourceModel); }
  
  public static PolicySourceModelContext createContext() { return new PolicySourceModelContext(); }
  
  public boolean containsModel(URI paramURI) { return getModels().containsKey(paramURI); }
  
  PolicySourceModel retrieveModel(URI paramURI) { return (PolicySourceModel)getModels().get(paramURI); }
  
  PolicySourceModel retrieveModel(URI paramURI1, URI paramURI2, String paramString) { throw new UnsupportedOperationException(); }
  
  public String toString() { return "PolicySourceModelContext: policyModels = " + this.policyModels; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\PolicySourceModelContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */