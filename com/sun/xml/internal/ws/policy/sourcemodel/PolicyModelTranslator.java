package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.internal.ws.policy.spi.AssertionCreationException;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionCreator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PolicyModelTranslator {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyModelTranslator.class);
  
  private static final PolicyAssertionCreator defaultCreator = new DefaultPolicyAssertionCreator();
  
  private final Map<String, PolicyAssertionCreator> assertionCreators;
  
  private PolicyModelTranslator() throws PolicyException { this(null); }
  
  protected PolicyModelTranslator(Collection<PolicyAssertionCreator> paramCollection) throws PolicyException {
    LOGGER.entering(new Object[] { paramCollection });
    LinkedList linkedList = new LinkedList();
    PolicyAssertionCreator[] arrayOfPolicyAssertionCreator = (PolicyAssertionCreator[])PolicyUtils.ServiceProvider.load(PolicyAssertionCreator.class);
    for (PolicyAssertionCreator policyAssertionCreator : arrayOfPolicyAssertionCreator)
      linkedList.add(policyAssertionCreator); 
    if (paramCollection != null)
      for (PolicyAssertionCreator policyAssertionCreator : paramCollection)
        linkedList.add(policyAssertionCreator);  
    HashMap hashMap = new HashMap();
    for (PolicyAssertionCreator policyAssertionCreator : linkedList) {
      String[] arrayOfString = policyAssertionCreator.getSupportedDomainNamespaceURIs();
      String str = policyAssertionCreator.getClass().getName();
      if (arrayOfString == null || arrayOfString.length == 0) {
        LOGGER.warning(LocalizationMessages.WSP_0077_ASSERTION_CREATOR_DOES_NOT_SUPPORT_ANY_URI(str));
        continue;
      } 
      for (String str1 : arrayOfString) {
        LOGGER.config(LocalizationMessages.WSP_0078_ASSERTION_CREATOR_DISCOVERED(str, str1));
        if (str1 == null || str1.length() == 0)
          throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0070_ERROR_REGISTERING_ASSERTION_CREATOR(str))); 
        PolicyAssertionCreator policyAssertionCreator1 = (PolicyAssertionCreator)hashMap.put(str1, policyAssertionCreator);
        if (policyAssertionCreator1 != null)
          throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0071_ERROR_MULTIPLE_ASSERTION_CREATORS_FOR_NAMESPACE(str1, policyAssertionCreator1.getClass().getName(), policyAssertionCreator.getClass().getName()))); 
      } 
    } 
    this.assertionCreators = Collections.unmodifiableMap(hashMap);
    LOGGER.exiting();
  }
  
  public static PolicyModelTranslator getTranslator() throws PolicyException { return new PolicyModelTranslator(); }
  
  public Policy translate(PolicySourceModel paramPolicySourceModel) throws PolicyException {
    PolicySourceModel policySourceModel;
    LOGGER.entering(new Object[] { paramPolicySourceModel });
    if (paramPolicySourceModel == null)
      throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0043_POLICY_MODEL_TRANSLATION_ERROR_INPUT_PARAM_NULL())); 
    try {
      policySourceModel = paramPolicySourceModel.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0016_UNABLE_TO_CLONE_POLICY_SOURCE_MODEL(), cloneNotSupportedException));
    } 
    String str1 = policySourceModel.getPolicyId();
    String str2 = policySourceModel.getPolicyName();
    Collection collection = createPolicyAlternatives(policySourceModel);
    LOGGER.finest(LocalizationMessages.WSP_0052_NUMBER_OF_ALTERNATIVE_COMBINATIONS_CREATED(Integer.valueOf(collection.size())));
    Policy policy = null;
    if (collection.size() == 0) {
      policy = Policy.createNullPolicy(paramPolicySourceModel.getNamespaceVersion(), str2, str1);
      LOGGER.finest(LocalizationMessages.WSP_0055_NO_ALTERNATIVE_COMBINATIONS_CREATED());
    } else if (collection.size() == 1 && ((AssertionSet)collection.iterator().next()).isEmpty()) {
      policy = Policy.createEmptyPolicy(paramPolicySourceModel.getNamespaceVersion(), str2, str1);
      LOGGER.finest(LocalizationMessages.WSP_0026_SINGLE_EMPTY_ALTERNATIVE_COMBINATION_CREATED());
    } else {
      policy = Policy.createPolicy(paramPolicySourceModel.getNamespaceVersion(), str2, str1, collection);
      LOGGER.finest(LocalizationMessages.WSP_0057_N_ALTERNATIVE_COMBINATIONS_M_POLICY_ALTERNATIVES_CREATED(Integer.valueOf(collection.size()), Integer.valueOf(policy.getNumberOfAssertionSets())));
    } 
    LOGGER.exiting(policy);
    return policy;
  }
  
  private Collection<AssertionSet> createPolicyAlternatives(PolicySourceModel paramPolicySourceModel) throws PolicyException {
    ContentDecomposition contentDecomposition = new ContentDecomposition(null);
    LinkedList linkedList1 = new LinkedList();
    LinkedList linkedList2 = new LinkedList();
    RawPolicy rawPolicy1 = new RawPolicy(paramPolicySourceModel.getRootNode(), new LinkedList());
    RawPolicy rawPolicy2 = rawPolicy1;
    do {
      Collection collection = rawPolicy2.originalContent;
      do {
        decompose(collection, contentDecomposition);
        if (contentDecomposition.exactlyOneContents.isEmpty()) {
          RawAlternative rawAlternative = new RawAlternative(contentDecomposition.assertions);
          rawPolicy2.alternatives.add(rawAlternative);
          if (!rawAlternative.allNestedPolicies.isEmpty())
            linkedList1.addAll(rawAlternative.allNestedPolicies); 
        } else {
          Collection collection1 = PolicyUtils.Collections.combine(contentDecomposition.assertions, contentDecomposition.exactlyOneContents, false);
          if (collection1 != null && !collection1.isEmpty())
            linkedList2.addAll(collection1); 
        } 
      } while ((collection = (Collection)linkedList2.poll()) != null);
    } while ((rawPolicy2 = (RawPolicy)linkedList1.poll()) != null);
    LinkedList linkedList3 = new LinkedList();
    for (RawAlternative rawAlternative : rawPolicy1.alternatives) {
      List list = normalizeRawAlternative(rawAlternative);
      linkedList3.addAll(list);
    } 
    return linkedList3;
  }
  
  private void decompose(Collection<ModelNode> paramCollection, ContentDecomposition paramContentDecomposition) throws PolicyException {
    paramContentDecomposition.reset();
    LinkedList linkedList = new LinkedList(paramCollection);
    ModelNode modelNode;
    while ((modelNode = (ModelNode)linkedList.poll()) != null) {
      switch (modelNode.getType()) {
        case POLICY:
        case ALL:
          linkedList.addAll(modelNode.getChildren());
          continue;
        case POLICY_REFERENCE:
          linkedList.addAll(getReferencedModelRootNode(modelNode).getChildren());
          continue;
        case EXACTLY_ONE:
          paramContentDecomposition.exactlyOneContents.add(expandsExactlyOneContent(modelNode.getChildren()));
          continue;
        case ASSERTION:
          paramContentDecomposition.assertions.add(modelNode);
          continue;
      } 
      throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0007_UNEXPECTED_MODEL_NODE_TYPE_FOUND(modelNode.getType())));
    } 
  }
  
  private static ModelNode getReferencedModelRootNode(ModelNode paramModelNode) throws PolicyException {
    PolicySourceModel policySourceModel = paramModelNode.getReferencedModel();
    if (policySourceModel == null) {
      PolicyReferenceData policyReferenceData = paramModelNode.getPolicyReferenceData();
      if (policyReferenceData == null)
        throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0041_POLICY_REFERENCE_NODE_FOUND_WITH_NO_POLICY_REFERENCE_IN_IT())); 
      throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0010_UNEXPANDED_POLICY_REFERENCE_NODE_FOUND_REFERENCING(policyReferenceData.getReferencedModelUri())));
    } 
    return policySourceModel.getRootNode();
  }
  
  private Collection<ModelNode> expandsExactlyOneContent(Collection<ModelNode> paramCollection) throws PolicyException {
    LinkedList linkedList1 = new LinkedList();
    LinkedList linkedList2 = new LinkedList(paramCollection);
    ModelNode modelNode;
    while ((modelNode = (ModelNode)linkedList2.poll()) != null) {
      switch (modelNode.getType()) {
        case POLICY:
        case ALL:
        case ASSERTION:
          linkedList1.add(modelNode);
          continue;
        case POLICY_REFERENCE:
          linkedList1.add(getReferencedModelRootNode(modelNode));
          continue;
        case EXACTLY_ONE:
          linkedList2.addAll(modelNode.getChildren());
          continue;
      } 
      throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0001_UNSUPPORTED_MODEL_NODE_TYPE(modelNode.getType())));
    } 
    return linkedList1;
  }
  
  private List<AssertionSet> normalizeRawAlternative(RawAlternative paramRawAlternative) throws AssertionCreationException, PolicyException {
    LinkedList linkedList1 = new LinkedList();
    LinkedList linkedList2 = new LinkedList();
    if (!paramRawAlternative.nestedAssertions.isEmpty()) {
      LinkedList linkedList = new LinkedList(paramRawAlternative.nestedAssertions);
      RawAssertion rawAssertion;
      while ((rawAssertion = (RawAssertion)linkedList.poll()) != null) {
        List list = normalizeRawAssertion(rawAssertion);
        if (list.size() == 1) {
          linkedList1.addAll(list);
          continue;
        } 
        linkedList2.add(list);
      } 
    } 
    LinkedList linkedList3 = new LinkedList();
    if (linkedList2.isEmpty()) {
      linkedList3.add(AssertionSet.createAssertionSet(linkedList1));
    } else {
      Collection collection = PolicyUtils.Collections.combine(linkedList1, linkedList2, true);
      for (Collection collection1 : collection)
        linkedList3.add(AssertionSet.createAssertionSet(collection1)); 
    } 
    return linkedList3;
  }
  
  private List<PolicyAssertion> normalizeRawAssertion(RawAssertion paramRawAssertion) throws AssertionCreationException, PolicyException {
    ArrayList arrayList;
    if (paramRawAssertion.parameters.isEmpty()) {
      arrayList = null;
    } else {
      arrayList = new ArrayList(paramRawAssertion.parameters.size());
      for (ModelNode modelNode : paramRawAssertion.parameters)
        arrayList.add(createPolicyAssertionParameter(modelNode)); 
    } 
    LinkedList linkedList1 = new LinkedList();
    if (paramRawAssertion.nestedAlternatives != null && !paramRawAssertion.nestedAlternatives.isEmpty()) {
      LinkedList linkedList = new LinkedList(paramRawAssertion.nestedAlternatives);
      RawAlternative rawAlternative;
      while ((rawAlternative = (RawAlternative)linkedList.poll()) != null)
        linkedList1.addAll(normalizeRawAlternative(rawAlternative)); 
    } 
    LinkedList linkedList2 = new LinkedList();
    boolean bool = !linkedList1.isEmpty() ? 1 : 0;
    if (bool) {
      for (AssertionSet assertionSet : linkedList1)
        linkedList2.add(createPolicyAssertion(paramRawAssertion.originalNode.getNodeData(), arrayList, assertionSet)); 
    } else {
      linkedList2.add(createPolicyAssertion(paramRawAssertion.originalNode.getNodeData(), arrayList, null));
    } 
    return linkedList2;
  }
  
  private PolicyAssertion createPolicyAssertionParameter(ModelNode paramModelNode) throws AssertionCreationException, PolicyException {
    if (paramModelNode.getType() != ModelNode.Type.ASSERTION_PARAMETER_NODE)
      throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0065_INCONSISTENCY_IN_POLICY_SOURCE_MODEL(paramModelNode.getType()))); 
    ArrayList arrayList = null;
    if (paramModelNode.hasChildren()) {
      arrayList = new ArrayList(paramModelNode.childrenSize());
      for (ModelNode modelNode : paramModelNode)
        arrayList.add(createPolicyAssertionParameter(modelNode)); 
    } 
    return createPolicyAssertion(paramModelNode.getNodeData(), arrayList, null);
  }
  
  private PolicyAssertion createPolicyAssertion(AssertionData paramAssertionData, Collection<PolicyAssertion> paramCollection, AssertionSet paramAssertionSet) throws AssertionCreationException {
    String str = paramAssertionData.getName().getNamespaceURI();
    PolicyAssertionCreator policyAssertionCreator = (PolicyAssertionCreator)this.assertionCreators.get(str);
    return (policyAssertionCreator == null) ? defaultCreator.createAssertion(paramAssertionData, paramCollection, paramAssertionSet, null) : policyAssertionCreator.createAssertion(paramAssertionData, paramCollection, paramAssertionSet, defaultCreator);
  }
  
  private static final class ContentDecomposition {
    final List<Collection<ModelNode>> exactlyOneContents = new LinkedList();
    
    final List<ModelNode> assertions = new LinkedList();
    
    private ContentDecomposition() throws PolicyException {}
    
    void reset() throws PolicyException {
      this.exactlyOneContents.clear();
      this.assertions.clear();
    }
  }
  
  private static final class RawAlternative {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(RawAlternative.class);
    
    final List<PolicyModelTranslator.RawPolicy> allNestedPolicies = new LinkedList();
    
    final Collection<PolicyModelTranslator.RawAssertion> nestedAssertions = new LinkedList();
    
    RawAlternative(Collection<ModelNode> param1Collection) throws PolicyException {
      for (ModelNode modelNode : param1Collection) {
        PolicyModelTranslator.RawAssertion rawAssertion = new PolicyModelTranslator.RawAssertion(modelNode, new LinkedList());
        this.nestedAssertions.add(rawAssertion);
        for (ModelNode modelNode1 : rawAssertion.originalNode.getChildren()) {
          switch (PolicyModelTranslator.null.$SwitchMap$com$sun$xml$internal$ws$policy$sourcemodel$ModelNode$Type[modelNode1.getType().ordinal()]) {
            case 1:
              rawAssertion.parameters.add(modelNode1);
              continue;
            case 2:
            case 3:
              if (rawAssertion.nestedAlternatives == null) {
                PolicyModelTranslator.RawPolicy rawPolicy;
                rawAssertion.nestedAlternatives = new LinkedList();
                if (modelNode1.getType() == ModelNode.Type.POLICY) {
                  rawPolicy = new PolicyModelTranslator.RawPolicy(modelNode1, rawAssertion.nestedAlternatives);
                } else {
                  rawPolicy = new PolicyModelTranslator.RawPolicy(PolicyModelTranslator.getReferencedModelRootNode(modelNode1), rawAssertion.nestedAlternatives);
                } 
                this.allNestedPolicies.add(rawPolicy);
                continue;
              } 
              throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0006_UNEXPECTED_MULTIPLE_POLICY_NODES()));
          } 
          throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0008_UNEXPECTED_CHILD_MODEL_TYPE(modelNode1.getType())));
        } 
      } 
    }
  }
  
  private static final class RawAssertion {
    ModelNode originalNode;
    
    Collection<PolicyModelTranslator.RawAlternative> nestedAlternatives = null;
    
    final Collection<ModelNode> parameters;
    
    RawAssertion(ModelNode param1ModelNode, Collection<ModelNode> param1Collection) {
      this.parameters = param1Collection;
      this.originalNode = param1ModelNode;
    }
  }
  
  private static final class RawPolicy {
    final Collection<ModelNode> originalContent;
    
    final Collection<PolicyModelTranslator.RawAlternative> alternatives;
    
    RawPolicy(ModelNode param1ModelNode, Collection<PolicyModelTranslator.RawAlternative> param1Collection) {
      this.originalContent = param1ModelNode.getChildren();
      this.alternatives = param1Collection;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\PolicyModelTranslator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */