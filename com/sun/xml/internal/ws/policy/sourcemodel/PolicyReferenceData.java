package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import java.net.URI;
import java.net.URISyntaxException;

final class PolicyReferenceData {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyReferenceData.class);
  
  private static final URI DEFAULT_DIGEST_ALGORITHM_URI;
  
  private static final URISyntaxException CLASS_INITIALIZATION_EXCEPTION;
  
  private final URI referencedModelUri;
  
  private final String digest;
  
  private final URI digestAlgorithmUri;
  
  public PolicyReferenceData(URI paramURI) {
    this.referencedModelUri = paramURI;
    this.digest = null;
    this.digestAlgorithmUri = null;
  }
  
  public PolicyReferenceData(URI paramURI1, String paramString, URI paramURI2) {
    if (CLASS_INITIALIZATION_EXCEPTION != null)
      throw (IllegalStateException)LOGGER.logSevereException(new IllegalStateException(LocalizationMessages.WSP_0015_UNABLE_TO_INSTANTIATE_DIGEST_ALG_URI_FIELD(), CLASS_INITIALIZATION_EXCEPTION)); 
    if (paramURI2 != null && paramString == null)
      throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0072_DIGEST_MUST_NOT_BE_NULL_WHEN_ALG_DEFINED())); 
    this.referencedModelUri = paramURI1;
    if (paramString == null) {
      this.digest = null;
      this.digestAlgorithmUri = null;
    } else {
      this.digest = paramString;
      if (paramURI2 == null) {
        this.digestAlgorithmUri = DEFAULT_DIGEST_ALGORITHM_URI;
      } else {
        this.digestAlgorithmUri = paramURI2;
      } 
    } 
  }
  
  public URI getReferencedModelUri() { return this.referencedModelUri; }
  
  public String getDigest() { return this.digest; }
  
  public URI getDigestAlgorithmUri() { return this.digestAlgorithmUri; }
  
  public String toString() { return toString(0, new StringBuffer()).toString(); }
  
  public StringBuffer toString(int paramInt, StringBuffer paramStringBuffer) {
    String str1 = PolicyUtils.Text.createIndent(paramInt);
    String str2 = PolicyUtils.Text.createIndent(paramInt + 1);
    paramStringBuffer.append(str1).append("reference data {").append(PolicyUtils.Text.NEW_LINE);
    paramStringBuffer.append(str2).append("referenced policy model URI = '").append(this.referencedModelUri).append('\'').append(PolicyUtils.Text.NEW_LINE);
    if (this.digest == null) {
      paramStringBuffer.append(str2).append("no digest specified").append(PolicyUtils.Text.NEW_LINE);
    } else {
      paramStringBuffer.append(str2).append("digest algorith URI = '").append(this.digestAlgorithmUri).append('\'').append(PolicyUtils.Text.NEW_LINE);
      paramStringBuffer.append(str2).append("digest = '").append(this.digest).append('\'').append(PolicyUtils.Text.NEW_LINE);
    } 
    paramStringBuffer.append(str1).append('}');
    return paramStringBuffer;
  }
  
  static  {
    uRISyntaxException = null;
    uRI = null;
    try {
      uRI = new URI("http://schemas.xmlsoap.org/ws/2004/09/policy/Sha1Exc");
    } catch (URISyntaxException uRISyntaxException1) {
      uRISyntaxException = uRISyntaxException1;
    } finally {
      DEFAULT_DIGEST_ALGORITHM_URI = uRI;
      CLASS_INITIALIZATION_EXCEPTION = uRISyntaxException;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\PolicyReferenceData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */