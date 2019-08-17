package com.sun.xml.internal.ws.api.policy;

import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelGenerator;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;

public abstract class ModelGenerator extends PolicyModelGenerator {
  private static final SourceModelCreator CREATOR = new SourceModelCreator();
  
  public static PolicyModelGenerator getGenerator() { return PolicyModelGenerator.getCompactGenerator(CREATOR); }
  
  protected static class SourceModelCreator extends PolicyModelGenerator.PolicySourceModelCreator {
    protected PolicySourceModel create(Policy param1Policy) { return SourceModel.createPolicySourceModel(param1Policy.getNamespaceVersion(), param1Policy.getId(), param1Policy.getName()); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\policy\ModelGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */