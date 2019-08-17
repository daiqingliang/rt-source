package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import java.util.Collection;

public abstract class ComplexAssertion extends PolicyAssertion {
  private final NestedPolicy nestedPolicy;
  
  protected ComplexAssertion() { this.nestedPolicy = NestedPolicy.createNestedPolicy(AssertionSet.emptyAssertionSet()); }
  
  protected ComplexAssertion(AssertionData paramAssertionData, Collection<? extends PolicyAssertion> paramCollection, AssertionSet paramAssertionSet) {
    super(paramAssertionData, paramCollection);
    AssertionSet assertionSet = (paramAssertionSet != null) ? paramAssertionSet : AssertionSet.emptyAssertionSet();
    this.nestedPolicy = NestedPolicy.createNestedPolicy(assertionSet);
  }
  
  public final boolean hasNestedPolicy() { return true; }
  
  public final NestedPolicy getNestedPolicy() { return this.nestedPolicy; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\ComplexAssertion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */