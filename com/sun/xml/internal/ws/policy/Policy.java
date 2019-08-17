package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.namespace.QName;

public class Policy extends Object implements Iterable<AssertionSet> {
  private static final String POLICY_TOSTRING_NAME = "policy";
  
  private static final List<AssertionSet> NULL_POLICY_ASSERTION_SETS = Collections.unmodifiableList(new LinkedList());
  
  private static final List<AssertionSet> EMPTY_POLICY_ASSERTION_SETS = Collections.unmodifiableList(new LinkedList(Arrays.asList(new AssertionSet[] { AssertionSet.emptyAssertionSet() })));
  
  private static final Set<QName> EMPTY_VOCABULARY = Collections.unmodifiableSet(new TreeSet(PolicyUtils.Comparison.QNAME_COMPARATOR));
  
  private static final Policy ANONYMOUS_NULL_POLICY = new Policy(null, null, NULL_POLICY_ASSERTION_SETS, EMPTY_VOCABULARY);
  
  private static final Policy ANONYMOUS_EMPTY_POLICY = new Policy(null, null, EMPTY_POLICY_ASSERTION_SETS, EMPTY_VOCABULARY);
  
  private String policyId;
  
  private String name;
  
  private NamespaceVersion nsVersion = NamespaceVersion.getLatestVersion();
  
  private final List<AssertionSet> assertionSets;
  
  private final Set<QName> vocabulary;
  
  private final Collection<QName> immutableVocabulary;
  
  private final String toStringName = "policy";
  
  public static Policy createNullPolicy() { return ANONYMOUS_NULL_POLICY; }
  
  public static Policy createEmptyPolicy() { return ANONYMOUS_EMPTY_POLICY; }
  
  public static Policy createNullPolicy(String paramString1, String paramString2) { return (paramString1 == null && paramString2 == null) ? ANONYMOUS_NULL_POLICY : new Policy(paramString1, paramString2, NULL_POLICY_ASSERTION_SETS, EMPTY_VOCABULARY); }
  
  public static Policy createNullPolicy(NamespaceVersion paramNamespaceVersion, String paramString1, String paramString2) { return ((paramNamespaceVersion == null || paramNamespaceVersion == NamespaceVersion.getLatestVersion()) && paramString1 == null && paramString2 == null) ? ANONYMOUS_NULL_POLICY : new Policy(paramNamespaceVersion, paramString1, paramString2, NULL_POLICY_ASSERTION_SETS, EMPTY_VOCABULARY); }
  
  public static Policy createEmptyPolicy(String paramString1, String paramString2) { return (paramString1 == null && paramString2 == null) ? ANONYMOUS_EMPTY_POLICY : new Policy(paramString1, paramString2, EMPTY_POLICY_ASSERTION_SETS, EMPTY_VOCABULARY); }
  
  public static Policy createEmptyPolicy(NamespaceVersion paramNamespaceVersion, String paramString1, String paramString2) { return ((paramNamespaceVersion == null || paramNamespaceVersion == NamespaceVersion.getLatestVersion()) && paramString1 == null && paramString2 == null) ? ANONYMOUS_EMPTY_POLICY : new Policy(paramNamespaceVersion, paramString1, paramString2, EMPTY_POLICY_ASSERTION_SETS, EMPTY_VOCABULARY); }
  
  public static Policy createPolicy(Collection<AssertionSet> paramCollection) { return (paramCollection == null || paramCollection.isEmpty()) ? createNullPolicy() : new Policy("policy", paramCollection); }
  
  public static Policy createPolicy(String paramString1, String paramString2, Collection<AssertionSet> paramCollection) { return (paramCollection == null || paramCollection.isEmpty()) ? createNullPolicy(paramString1, paramString2) : new Policy("policy", paramString1, paramString2, paramCollection); }
  
  public static Policy createPolicy(NamespaceVersion paramNamespaceVersion, String paramString1, String paramString2, Collection<AssertionSet> paramCollection) { return (paramCollection == null || paramCollection.isEmpty()) ? createNullPolicy(paramNamespaceVersion, paramString1, paramString2) : new Policy(paramNamespaceVersion, "policy", paramString1, paramString2, paramCollection); }
  
  private Policy(String paramString1, String paramString2, List<AssertionSet> paramList, Set<QName> paramSet) {
    this.name = paramString1;
    this.policyId = paramString2;
    this.assertionSets = paramList;
    this.vocabulary = paramSet;
    this.immutableVocabulary = Collections.unmodifiableCollection(this.vocabulary);
  }
  
  Policy(String paramString, Collection<AssertionSet> paramCollection) {
    if (paramCollection == null || paramCollection.isEmpty()) {
      this.assertionSets = NULL_POLICY_ASSERTION_SETS;
      this.vocabulary = EMPTY_VOCABULARY;
      this.immutableVocabulary = EMPTY_VOCABULARY;
    } else {
      this.assertionSets = new LinkedList();
      this.vocabulary = new TreeSet(PolicyUtils.Comparison.QNAME_COMPARATOR);
      this.immutableVocabulary = Collections.unmodifiableCollection(this.vocabulary);
      addAll(paramCollection);
    } 
  }
  
  Policy(String paramString1, String paramString2, String paramString3, Collection<AssertionSet> paramCollection) {
    this(paramString1, paramCollection);
    this.name = paramString2;
    this.policyId = paramString3;
  }
  
  private Policy(NamespaceVersion paramNamespaceVersion, String paramString1, String paramString2, List<AssertionSet> paramList, Set<QName> paramSet) {
    this.name = paramString1;
    this.policyId = paramString2;
    this.assertionSets = paramList;
    this.vocabulary = paramSet;
    this.immutableVocabulary = Collections.unmodifiableCollection(this.vocabulary);
  }
  
  Policy(NamespaceVersion paramNamespaceVersion, String paramString, Collection<AssertionSet> paramCollection) {
    if (paramCollection == null || paramCollection.isEmpty()) {
      this.assertionSets = NULL_POLICY_ASSERTION_SETS;
      this.vocabulary = EMPTY_VOCABULARY;
      this.immutableVocabulary = EMPTY_VOCABULARY;
    } else {
      this.assertionSets = new LinkedList();
      this.vocabulary = new TreeSet(PolicyUtils.Comparison.QNAME_COMPARATOR);
      this.immutableVocabulary = Collections.unmodifiableCollection(this.vocabulary);
      addAll(paramCollection);
    } 
  }
  
  Policy(NamespaceVersion paramNamespaceVersion, String paramString1, String paramString2, String paramString3, Collection<AssertionSet> paramCollection) {
    this(paramNamespaceVersion, paramString1, paramCollection);
    this.name = paramString2;
    this.policyId = paramString3;
  }
  
  private boolean add(AssertionSet paramAssertionSet) {
    if (paramAssertionSet == null)
      return false; 
    if (this.assertionSets.contains(paramAssertionSet))
      return false; 
    this.assertionSets.add(paramAssertionSet);
    this.vocabulary.addAll(paramAssertionSet.getVocabulary());
    return true;
  }
  
  private boolean addAll(Collection<AssertionSet> paramCollection) {
    assert paramCollection != null && !paramCollection.isEmpty() : LocalizationMessages.WSP_0036_PRIVATE_METHOD_DOES_NOT_ACCEPT_NULL_OR_EMPTY_COLLECTION();
    boolean bool = true;
    for (AssertionSet assertionSet : paramCollection)
      bool &= add(assertionSet); 
    Collections.sort(this.assertionSets);
    return bool;
  }
  
  Collection<AssertionSet> getContent() { return this.assertionSets; }
  
  public String getId() { return this.policyId; }
  
  public String getName() { return this.name; }
  
  public NamespaceVersion getNamespaceVersion() { return this.nsVersion; }
  
  public String getIdOrName() { return (this.policyId != null) ? this.policyId : this.name; }
  
  public int getNumberOfAssertionSets() { return this.assertionSets.size(); }
  
  public Iterator<AssertionSet> iterator() { return this.assertionSets.iterator(); }
  
  public boolean isNull() { return (this.assertionSets.size() == 0); }
  
  public boolean isEmpty() { return (this.assertionSets.size() == 1 && ((AssertionSet)this.assertionSets.get(0)).isEmpty()); }
  
  public boolean contains(String paramString) {
    for (QName qName : this.vocabulary) {
      if (qName.getNamespaceURI().equals(paramString))
        return true; 
    } 
    return false;
  }
  
  public Collection<QName> getVocabulary() { return this.immutableVocabulary; }
  
  public boolean contains(QName paramQName) { return this.vocabulary.contains(paramQName); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof Policy))
      return false; 
    Policy policy = (Policy)paramObject;
    null = true;
    null = (null && this.vocabulary.equals(policy.vocabulary));
    return (null && this.assertionSets.size() == policy.assertionSets.size() && this.assertionSets.containsAll(policy.assertionSets));
  }
  
  public int hashCode() {
    null = 17;
    null = 37 * null + this.vocabulary.hashCode();
    return 37 * null + this.assertionSets.hashCode();
  }
  
  public String toString() { return toString(0, new StringBuffer()).toString(); }
  
  StringBuffer toString(int paramInt, StringBuffer paramStringBuffer) {
    String str1 = PolicyUtils.Text.createIndent(paramInt);
    String str2 = PolicyUtils.Text.createIndent(paramInt + 1);
    String str3 = PolicyUtils.Text.createIndent(paramInt + 2);
    paramStringBuffer.append(str1).append(this.toStringName).append(" {").append(PolicyUtils.Text.NEW_LINE);
    paramStringBuffer.append(str2).append("namespace version = '").append(this.nsVersion.name()).append('\'').append(PolicyUtils.Text.NEW_LINE);
    paramStringBuffer.append(str2).append("id = '").append(this.policyId).append('\'').append(PolicyUtils.Text.NEW_LINE);
    paramStringBuffer.append(str2).append("name = '").append(this.name).append('\'').append(PolicyUtils.Text.NEW_LINE);
    paramStringBuffer.append(str2).append("vocabulary {").append(PolicyUtils.Text.NEW_LINE);
    if (this.vocabulary.isEmpty()) {
      paramStringBuffer.append(str3).append("no entries").append(PolicyUtils.Text.NEW_LINE);
    } else {
      byte b = 1;
      for (QName qName : this.vocabulary)
        paramStringBuffer.append(str3).append(b++).append(". entry = '").append(qName.getNamespaceURI()).append(':').append(qName.getLocalPart()).append('\'').append(PolicyUtils.Text.NEW_LINE); 
    } 
    paramStringBuffer.append(str2).append('}').append(PolicyUtils.Text.NEW_LINE);
    if (this.assertionSets.isEmpty()) {
      paramStringBuffer.append(str2).append("no assertion sets").append(PolicyUtils.Text.NEW_LINE);
    } else {
      for (AssertionSet assertionSet : this.assertionSets)
        assertionSet.toString(paramInt + 1, paramStringBuffer).append(PolicyUtils.Text.NEW_LINE); 
    } 
    paramStringBuffer.append(str1).append('}');
    return paramStringBuffer;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\Policy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */