package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator;
import java.util.LinkedList;

public class EffectiveAlternativeSelector {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(EffectiveAlternativeSelector.class);
  
  public static void doSelection(EffectivePolicyModifier paramEffectivePolicyModifier) throws PolicyException {
    AssertionValidationProcessor assertionValidationProcessor = AssertionValidationProcessor.getInstance();
    selectAlternatives(paramEffectivePolicyModifier, assertionValidationProcessor);
  }
  
  protected static void selectAlternatives(EffectivePolicyModifier paramEffectivePolicyModifier, AssertionValidationProcessor paramAssertionValidationProcessor) throws PolicyException {
    PolicyMap policyMap = paramEffectivePolicyModifier.getMap();
    for (PolicyMapKey policyMapKey : policyMap.getAllServiceScopeKeys()) {
      Policy policy = policyMap.getServiceEffectivePolicy(policyMapKey);
      paramEffectivePolicyModifier.setNewEffectivePolicyForServiceScope(policyMapKey, selectBestAlternative(policy, paramAssertionValidationProcessor));
    } 
    for (PolicyMapKey policyMapKey : policyMap.getAllEndpointScopeKeys()) {
      Policy policy = policyMap.getEndpointEffectivePolicy(policyMapKey);
      paramEffectivePolicyModifier.setNewEffectivePolicyForEndpointScope(policyMapKey, selectBestAlternative(policy, paramAssertionValidationProcessor));
    } 
    for (PolicyMapKey policyMapKey : policyMap.getAllOperationScopeKeys()) {
      Policy policy = policyMap.getOperationEffectivePolicy(policyMapKey);
      paramEffectivePolicyModifier.setNewEffectivePolicyForOperationScope(policyMapKey, selectBestAlternative(policy, paramAssertionValidationProcessor));
    } 
    for (PolicyMapKey policyMapKey : policyMap.getAllInputMessageScopeKeys()) {
      Policy policy = policyMap.getInputMessageEffectivePolicy(policyMapKey);
      paramEffectivePolicyModifier.setNewEffectivePolicyForInputMessageScope(policyMapKey, selectBestAlternative(policy, paramAssertionValidationProcessor));
    } 
    for (PolicyMapKey policyMapKey : policyMap.getAllOutputMessageScopeKeys()) {
      Policy policy = policyMap.getOutputMessageEffectivePolicy(policyMapKey);
      paramEffectivePolicyModifier.setNewEffectivePolicyForOutputMessageScope(policyMapKey, selectBestAlternative(policy, paramAssertionValidationProcessor));
    } 
    for (PolicyMapKey policyMapKey : policyMap.getAllFaultMessageScopeKeys()) {
      Policy policy = policyMap.getFaultMessageEffectivePolicy(policyMapKey);
      paramEffectivePolicyModifier.setNewEffectivePolicyForFaultMessageScope(policyMapKey, selectBestAlternative(policy, paramAssertionValidationProcessor));
    } 
  }
  
  private static Policy selectBestAlternative(Policy paramPolicy, AssertionValidationProcessor paramAssertionValidationProcessor) throws PolicyException {
    AssertionSet assertionSet = null;
    AlternativeFitness alternativeFitness = AlternativeFitness.UNEVALUATED;
    for (AssertionSet assertionSet1 : paramPolicy) {
      AlternativeFitness alternativeFitness1 = assertionSet1.isEmpty() ? AlternativeFitness.SUPPORTED_EMPTY : AlternativeFitness.UNEVALUATED;
      for (PolicyAssertion policyAssertion : assertionSet1) {
        PolicyAssertionValidator.Fitness fitness = paramAssertionValidationProcessor.validateClientSide(policyAssertion);
        switch (fitness) {
          case INVALID:
          case UNKNOWN:
          case PARTIALLY_SUPPORTED:
            LOGGER.warning(LocalizationMessages.WSP_0075_PROBLEMATIC_ASSERTION_STATE(policyAssertion.getName(), fitness));
            break;
        } 
        alternativeFitness1 = alternativeFitness1.combine(fitness);
      } 
      if (alternativeFitness.compareTo(alternativeFitness1) < 0) {
        assertionSet = assertionSet1;
        alternativeFitness = alternativeFitness1;
      } 
      if (alternativeFitness == AlternativeFitness.SUPPORTED)
        break; 
    } 
    switch (alternativeFitness) {
      case INVALID:
        throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0053_INVALID_CLIENT_SIDE_ALTERNATIVE()));
      case UNKNOWN:
      case UNSUPPORTED:
      case PARTIALLY_SUPPORTED:
        LOGGER.warning(LocalizationMessages.WSP_0019_SUBOPTIMAL_ALTERNATIVE_SELECTED(alternativeFitness));
        break;
    } 
    LinkedList linkedList = null;
    if (assertionSet != null) {
      linkedList = new LinkedList();
      linkedList.add(assertionSet);
    } 
    return Policy.createPolicy(paramPolicy.getNamespaceVersion(), paramPolicy.getName(), paramPolicy.getId(), linkedList);
  }
  
  private final abstract enum AlternativeFitness {
    UNEVALUATED, INVALID, UNKNOWN, UNSUPPORTED, PARTIALLY_SUPPORTED, SUPPORTED_EMPTY, SUPPORTED;
    
    abstract AlternativeFitness combine(PolicyAssertionValidator.Fitness param1Fitness);
    
    static  {
      // Byte code:
      //   0: new com/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness$1
      //   3: dup
      //   4: ldc 'UNEVALUATED'
      //   6: iconst_0
      //   7: invokespecial <init> : (Ljava/lang/String;I)V
      //   10: putstatic com/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness.UNEVALUATED : Lcom/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness;
      //   13: new com/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness$2
      //   16: dup
      //   17: ldc 'INVALID'
      //   19: iconst_1
      //   20: invokespecial <init> : (Ljava/lang/String;I)V
      //   23: putstatic com/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness.INVALID : Lcom/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness;
      //   26: new com/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness$3
      //   29: dup
      //   30: ldc 'UNKNOWN'
      //   32: iconst_2
      //   33: invokespecial <init> : (Ljava/lang/String;I)V
      //   36: putstatic com/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness.UNKNOWN : Lcom/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness;
      //   39: new com/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness$4
      //   42: dup
      //   43: ldc 'UNSUPPORTED'
      //   45: iconst_3
      //   46: invokespecial <init> : (Ljava/lang/String;I)V
      //   49: putstatic com/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness.UNSUPPORTED : Lcom/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness;
      //   52: new com/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness$5
      //   55: dup
      //   56: ldc 'PARTIALLY_SUPPORTED'
      //   58: iconst_4
      //   59: invokespecial <init> : (Ljava/lang/String;I)V
      //   62: putstatic com/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness.PARTIALLY_SUPPORTED : Lcom/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness;
      //   65: new com/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness$6
      //   68: dup
      //   69: ldc 'SUPPORTED_EMPTY'
      //   71: iconst_5
      //   72: invokespecial <init> : (Ljava/lang/String;I)V
      //   75: putstatic com/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness.SUPPORTED_EMPTY : Lcom/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness;
      //   78: new com/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness$7
      //   81: dup
      //   82: ldc 'SUPPORTED'
      //   84: bipush #6
      //   86: invokespecial <init> : (Ljava/lang/String;I)V
      //   89: putstatic com/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness.SUPPORTED : Lcom/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness;
      //   92: bipush #7
      //   94: anewarray com/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness
      //   97: dup
      //   98: iconst_0
      //   99: getstatic com/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness.UNEVALUATED : Lcom/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness;
      //   102: aastore
      //   103: dup
      //   104: iconst_1
      //   105: getstatic com/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness.INVALID : Lcom/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness;
      //   108: aastore
      //   109: dup
      //   110: iconst_2
      //   111: getstatic com/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness.UNKNOWN : Lcom/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness;
      //   114: aastore
      //   115: dup
      //   116: iconst_3
      //   117: getstatic com/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness.UNSUPPORTED : Lcom/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness;
      //   120: aastore
      //   121: dup
      //   122: iconst_4
      //   123: getstatic com/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness.PARTIALLY_SUPPORTED : Lcom/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness;
      //   126: aastore
      //   127: dup
      //   128: iconst_5
      //   129: getstatic com/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness.SUPPORTED_EMPTY : Lcom/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness;
      //   132: aastore
      //   133: dup
      //   134: bipush #6
      //   136: getstatic com/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness.SUPPORTED : Lcom/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness;
      //   139: aastore
      //   140: putstatic com/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness.$VALUES : [Lcom/sun/xml/internal/ws/policy/EffectiveAlternativeSelector$AlternativeFitness;
      //   143: return
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\EffectiveAlternativeSelector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */