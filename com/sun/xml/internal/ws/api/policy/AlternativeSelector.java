package com.sun.xml.internal.ws.api.policy;

import com.sun.xml.internal.ws.policy.EffectiveAlternativeSelector;
import com.sun.xml.internal.ws.policy.EffectivePolicyModifier;
import com.sun.xml.internal.ws.policy.PolicyException;

public class AlternativeSelector extends EffectiveAlternativeSelector {
  public static void doSelection(EffectivePolicyModifier paramEffectivePolicyModifier) throws PolicyException {
    ValidationProcessor validationProcessor = ValidationProcessor.getInstance();
    selectAlternatives(paramEffectivePolicyModifier, validationProcessor);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\policy\AlternativeSelector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */