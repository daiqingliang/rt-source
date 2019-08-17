package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;

public class CMNodeFactory {
  private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  
  private static final boolean DEBUG = false;
  
  private static final int MULTIPLICITY = 1;
  
  private int nodeCount = 0;
  
  private int maxNodeLimit;
  
  private XMLErrorReporter fErrorReporter;
  
  private XMLSecurityManager fSecurityManager = null;
  
  public void reset(XMLComponentManager paramXMLComponentManager) {
    this.fErrorReporter = (XMLErrorReporter)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    try {
      this.fSecurityManager = (XMLSecurityManager)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/security-manager");
      if (this.fSecurityManager != null)
        this.maxNodeLimit = this.fSecurityManager.getLimit(XMLSecurityManager.Limit.MAX_OCCUR_NODE_LIMIT) * 1; 
    } catch (XMLConfigurationException xMLConfigurationException) {
      this.fSecurityManager = null;
    } 
  }
  
  public CMNode getCMLeafNode(int paramInt1, Object paramObject, int paramInt2, int paramInt3) { return new XSCMLeaf(paramInt1, paramObject, paramInt2, paramInt3); }
  
  public CMNode getCMRepeatingLeafNode(int paramInt1, Object paramObject, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    nodeCountCheck();
    return new XSCMRepeatingLeaf(paramInt1, paramObject, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public CMNode getCMUniOpNode(int paramInt, CMNode paramCMNode) {
    nodeCountCheck();
    return new XSCMUniOp(paramInt, paramCMNode);
  }
  
  public CMNode getCMBinOpNode(int paramInt, CMNode paramCMNode1, CMNode paramCMNode2) { return new XSCMBinOp(paramInt, paramCMNode1, paramCMNode2); }
  
  public void nodeCountCheck() {
    if (this.fSecurityManager != null && !this.fSecurityManager.isNoLimit(this.maxNodeLimit) && this.nodeCount++ > this.maxNodeLimit) {
      this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "MaxOccurLimit", new Object[] { new Integer(this.maxNodeLimit) }, (short)2);
      this.nodeCount = 0;
    } 
  }
  
  public void resetNodeCount() { this.nodeCount = 0; }
  
  public void setProperty(String paramString, Object paramObject) throws XMLConfigurationException {
    if (paramString.startsWith("http://apache.org/xml/properties/")) {
      int i = paramString.length() - "http://apache.org/xml/properties/".length();
      if (i == "security-manager".length() && paramString.endsWith("security-manager")) {
        this.fSecurityManager = (XMLSecurityManager)paramObject;
        this.maxNodeLimit = (this.fSecurityManager != null) ? (this.fSecurityManager.getLimit(XMLSecurityManager.Limit.MAX_OCCUR_NODE_LIMIT) * 1) : 0;
        return;
      } 
      if (i == "internal/error-reporter".length() && paramString.endsWith("internal/error-reporter")) {
        this.fErrorReporter = (XMLErrorReporter)paramObject;
        return;
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\models\CMNodeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */