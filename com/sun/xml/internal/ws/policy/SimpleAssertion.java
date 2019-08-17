package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import java.util.Collection;

public abstract class SimpleAssertion extends PolicyAssertion {
  protected SimpleAssertion() {}
  
  protected SimpleAssertion(AssertionData paramAssertionData, Collection<? extends PolicyAssertion> paramCollection) { super(paramAssertionData, paramCollection); }
  
  public final boolean hasNestedPolicy() { return false; }
  
  public final NestedPolicy getNestedPolicy() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\SimpleAssertion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */