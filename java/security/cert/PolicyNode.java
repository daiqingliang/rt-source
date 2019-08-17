package java.security.cert;

import java.util.Iterator;
import java.util.Set;

public interface PolicyNode {
  PolicyNode getParent();
  
  Iterator<? extends PolicyNode> getChildren();
  
  int getDepth();
  
  String getValidPolicy();
  
  Set<? extends PolicyQualifierInfo> getPolicyQualifiers();
  
  Set<String> getExpectedPolicies();
  
  boolean isCritical();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\PolicyNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */