package com.sun.xml.internal.ws.api.policy;

import com.sun.xml.internal.ws.config.management.policy.ManagementAssertionCreator;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelTranslator;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionCreator;
import com.sun.xml.internal.ws.resources.ManagementMessages;
import java.util.Arrays;

public class ModelTranslator extends PolicyModelTranslator {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(ModelTranslator.class);
  
  private static final PolicyAssertionCreator[] JAXWS_ASSERTION_CREATORS = { new ManagementAssertionCreator() };
  
  private static final ModelTranslator translator;
  
  private static final PolicyException creationException;
  
  private ModelTranslator() throws PolicyException { super(Arrays.asList(JAXWS_ASSERTION_CREATORS)); }
  
  public static ModelTranslator getTranslator() throws PolicyException {
    if (creationException != null)
      throw (PolicyException)LOGGER.logSevereException(creationException); 
    return translator;
  }
  
  static  {
    modelTranslator = null;
    policyException = null;
    try {
      modelTranslator = new ModelTranslator();
    } catch (PolicyException policyException1) {
      policyException = policyException1;
      LOGGER.warning(ManagementMessages.WSM_1007_FAILED_MODEL_TRANSLATOR_INSTANTIATION(), policyException1);
    } finally {
      translator = modelTranslator;
      creationException = policyException;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\policy\ModelTranslator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */