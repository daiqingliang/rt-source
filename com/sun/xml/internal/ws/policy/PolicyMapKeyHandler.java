package com.sun.xml.internal.ws.policy;

interface PolicyMapKeyHandler {
  boolean areEqual(PolicyMapKey paramPolicyMapKey1, PolicyMapKey paramPolicyMapKey2);
  
  int generateHashCode(PolicyMapKey paramPolicyMapKey);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\PolicyMapKeyHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */