package com.sun.xml.internal.ws.policy;

import java.util.Arrays;
import java.util.Iterator;

public final class NestedPolicy extends Policy {
  private static final String NESTED_POLICY_TOSTRING_NAME = "nested policy";
  
  private NestedPolicy(AssertionSet paramAssertionSet) { super("nested policy", Arrays.asList(new AssertionSet[] { paramAssertionSet })); }
  
  private NestedPolicy(String paramString1, String paramString2, AssertionSet paramAssertionSet) { super("nested policy", paramString1, paramString2, Arrays.asList(new AssertionSet[] { paramAssertionSet })); }
  
  static NestedPolicy createNestedPolicy(AssertionSet paramAssertionSet) { return new NestedPolicy(paramAssertionSet); }
  
  static NestedPolicy createNestedPolicy(String paramString1, String paramString2, AssertionSet paramAssertionSet) { return new NestedPolicy(paramString1, paramString2, paramAssertionSet); }
  
  public AssertionSet getAssertionSet() {
    Iterator iterator = iterator();
    return iterator.hasNext() ? (AssertionSet)iterator.next() : null;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof NestedPolicy))
      return false; 
    NestedPolicy nestedPolicy = (NestedPolicy)paramObject;
    return super.equals(nestedPolicy);
  }
  
  public int hashCode() { return super.hashCode(); }
  
  public String toString() { return toString(0, new StringBuffer()).toString(); }
  
  StringBuffer toString(int paramInt, StringBuffer paramStringBuffer) { return super.toString(paramInt, paramStringBuffer); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\NestedPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */