package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.namespace.QName;

public final class AssertionSet extends Object implements Iterable<PolicyAssertion>, Comparable<AssertionSet> {
  private static final AssertionSet EMPTY_ASSERTION_SET = new AssertionSet(Collections.unmodifiableList(new LinkedList()));
  
  private static final Comparator<PolicyAssertion> ASSERTION_COMPARATOR = new Comparator<PolicyAssertion>() {
      public int compare(PolicyAssertion param1PolicyAssertion1, PolicyAssertion param1PolicyAssertion2) {
        if (param1PolicyAssertion1.equals(param1PolicyAssertion2))
          return 0; 
        int i = PolicyUtils.Comparison.QNAME_COMPARATOR.compare(param1PolicyAssertion1.getName(), param1PolicyAssertion2.getName());
        if (i != 0)
          return i; 
        i = PolicyUtils.Comparison.compareNullableStrings(param1PolicyAssertion1.getValue(), param1PolicyAssertion2.getValue());
        if (i != 0)
          return i; 
        i = PolicyUtils.Comparison.compareBoolean(param1PolicyAssertion1.hasNestedAssertions(), param1PolicyAssertion2.hasNestedAssertions());
        if (i != 0)
          return i; 
        i = PolicyUtils.Comparison.compareBoolean(param1PolicyAssertion1.hasNestedPolicy(), param1PolicyAssertion2.hasNestedPolicy());
        return (i != 0) ? i : Math.round(Math.signum((param1PolicyAssertion1.hashCode() - param1PolicyAssertion2.hashCode())));
      }
    };
  
  private final List<PolicyAssertion> assertions;
  
  private final Set<QName> vocabulary = new TreeSet(PolicyUtils.Comparison.QNAME_COMPARATOR);
  
  private final Collection<QName> immutableVocabulary = Collections.unmodifiableCollection(this.vocabulary);
  
  private AssertionSet(List<PolicyAssertion> paramList) {
    assert paramList != null : LocalizationMessages.WSP_0037_PRIVATE_CONSTRUCTOR_DOES_NOT_TAKE_NULL();
    this.assertions = paramList;
  }
  
  private AssertionSet(Collection<AssertionSet> paramCollection) {
    this.assertions = new LinkedList();
    for (AssertionSet assertionSet : paramCollection)
      addAll(assertionSet.assertions); 
  }
  
  private boolean add(PolicyAssertion paramPolicyAssertion) {
    if (paramPolicyAssertion == null)
      return false; 
    if (this.assertions.contains(paramPolicyAssertion))
      return false; 
    this.assertions.add(paramPolicyAssertion);
    this.vocabulary.add(paramPolicyAssertion.getName());
    return true;
  }
  
  private boolean addAll(Collection<? extends PolicyAssertion> paramCollection) {
    boolean bool = true;
    if (paramCollection != null)
      for (PolicyAssertion policyAssertion : paramCollection)
        bool &= add(policyAssertion);  
    return bool;
  }
  
  Collection<PolicyAssertion> getAssertions() { return this.assertions; }
  
  Collection<QName> getVocabulary() { return this.immutableVocabulary; }
  
  boolean isCompatibleWith(AssertionSet paramAssertionSet, PolicyIntersector.CompatibilityMode paramCompatibilityMode) {
    null = (paramCompatibilityMode == PolicyIntersector.CompatibilityMode.LAX || this.vocabulary.equals(paramAssertionSet.vocabulary));
    null = (null && areAssertionsCompatible(paramAssertionSet, paramCompatibilityMode));
    return (null && paramAssertionSet.areAssertionsCompatible(this, paramCompatibilityMode));
  }
  
  private boolean areAssertionsCompatible(AssertionSet paramAssertionSet, PolicyIntersector.CompatibilityMode paramCompatibilityMode) {
    label17: for (PolicyAssertion policyAssertion : this.assertions) {
      if (paramCompatibilityMode == PolicyIntersector.CompatibilityMode.STRICT || !policyAssertion.isIgnorable()) {
        for (PolicyAssertion policyAssertion1 : paramAssertionSet.assertions) {
          if (policyAssertion.isCompatibleWith(policyAssertion1, paramCompatibilityMode))
            continue label17; 
        } 
        return false;
      } 
    } 
    return true;
  }
  
  public static AssertionSet createMergedAssertionSet(Collection<AssertionSet> paramCollection) {
    if (paramCollection == null || paramCollection.isEmpty())
      return EMPTY_ASSERTION_SET; 
    AssertionSet assertionSet = new AssertionSet(paramCollection);
    Collections.sort(assertionSet.assertions, ASSERTION_COMPARATOR);
    return assertionSet;
  }
  
  public static AssertionSet createAssertionSet(Collection<? extends PolicyAssertion> paramCollection) {
    if (paramCollection == null || paramCollection.isEmpty())
      return EMPTY_ASSERTION_SET; 
    AssertionSet assertionSet = new AssertionSet(new LinkedList());
    assertionSet.addAll(paramCollection);
    Collections.sort(assertionSet.assertions, ASSERTION_COMPARATOR);
    return assertionSet;
  }
  
  public static AssertionSet emptyAssertionSet() { return EMPTY_ASSERTION_SET; }
  
  public Iterator<PolicyAssertion> iterator() { return this.assertions.iterator(); }
  
  public Collection<PolicyAssertion> get(QName paramQName) {
    LinkedList linkedList = new LinkedList();
    if (this.vocabulary.contains(paramQName))
      for (PolicyAssertion policyAssertion : this.assertions) {
        if (policyAssertion.getName().equals(paramQName))
          linkedList.add(policyAssertion); 
      }  
    return linkedList;
  }
  
  public boolean isEmpty() { return this.assertions.isEmpty(); }
  
  public boolean contains(QName paramQName) { return this.vocabulary.contains(paramQName); }
  
  public int compareTo(AssertionSet paramAssertionSet) {
    if (equals(paramAssertionSet))
      return 0; 
    Iterator iterator1 = getVocabulary().iterator();
    Iterator iterator2 = paramAssertionSet.getVocabulary().iterator();
    while (iterator1.hasNext()) {
      QName qName = (QName)iterator1.next();
      if (iterator2.hasNext()) {
        QName qName1 = (QName)iterator2.next();
        int i = PolicyUtils.Comparison.QNAME_COMPARATOR.compare(qName, qName1);
        if (i != 0)
          return i; 
        continue;
      } 
      return 1;
    } 
    if (iterator2.hasNext())
      return -1; 
    Iterator iterator3 = getAssertions().iterator();
    Iterator iterator4 = paramAssertionSet.getAssertions().iterator();
    while (iterator3.hasNext()) {
      PolicyAssertion policyAssertion = (PolicyAssertion)iterator3.next();
      if (iterator4.hasNext()) {
        PolicyAssertion policyAssertion1 = (PolicyAssertion)iterator4.next();
        int i = ASSERTION_COMPARATOR.compare(policyAssertion, policyAssertion1);
        if (i != 0)
          return i; 
        continue;
      } 
      return 1;
    } 
    return iterator4.hasNext() ? -1 : 1;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof AssertionSet))
      return false; 
    AssertionSet assertionSet = (AssertionSet)paramObject;
    null = true;
    null = (null && this.vocabulary.equals(assertionSet.vocabulary));
    return (null && this.assertions.size() == assertionSet.assertions.size() && this.assertions.containsAll(assertionSet.assertions));
  }
  
  public int hashCode() {
    null = 17;
    null = 37 * null + this.vocabulary.hashCode();
    return 37 * null + this.assertions.hashCode();
  }
  
  public String toString() { return toString(0, new StringBuffer()).toString(); }
  
  StringBuffer toString(int paramInt, StringBuffer paramStringBuffer) {
    String str1 = PolicyUtils.Text.createIndent(paramInt);
    String str2 = PolicyUtils.Text.createIndent(paramInt + 1);
    paramStringBuffer.append(str1).append("assertion set {").append(PolicyUtils.Text.NEW_LINE);
    if (this.assertions.isEmpty()) {
      paramStringBuffer.append(str2).append("no assertions").append(PolicyUtils.Text.NEW_LINE);
    } else {
      for (PolicyAssertion policyAssertion : this.assertions)
        policyAssertion.toString(paramInt + 1, paramStringBuffer).append(PolicyUtils.Text.NEW_LINE); 
    } 
    paramStringBuffer.append(str1).append('}');
    return paramStringBuffer;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\AssertionSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */