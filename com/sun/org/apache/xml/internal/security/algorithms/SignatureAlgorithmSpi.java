package com.sun.org.apache.xml.internal.security.algorithms;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.w3c.dom.Element;

public abstract class SignatureAlgorithmSpi {
  protected abstract String engineGetURI();
  
  protected abstract String engineGetJCEAlgorithmString();
  
  protected abstract String engineGetJCEProviderName();
  
  protected abstract void engineUpdate(byte[] paramArrayOfByte) throws XMLSignatureException;
  
  protected abstract void engineUpdate(byte paramByte) throws XMLSignatureException;
  
  protected abstract void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws XMLSignatureException;
  
  protected abstract void engineInitSign(Key paramKey) throws XMLSignatureException;
  
  protected abstract void engineInitSign(Key paramKey, SecureRandom paramSecureRandom) throws XMLSignatureException;
  
  protected abstract void engineInitSign(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec) throws XMLSignatureException;
  
  protected abstract byte[] engineSign() throws XMLSignatureException;
  
  protected abstract void engineInitVerify(Key paramKey) throws XMLSignatureException;
  
  protected abstract boolean engineVerify(byte[] paramArrayOfByte) throws XMLSignatureException;
  
  protected abstract void engineSetParameter(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws XMLSignatureException;
  
  protected void engineGetContextFromElement(Element paramElement) {}
  
  protected abstract void engineSetHMACOutputLength(int paramInt) throws XMLSignatureException;
  
  public void reset() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\algorithms\SignatureAlgorithmSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */