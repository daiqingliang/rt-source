package javax.security.sasl;

public interface SaslClient {
  String getMechanismName();
  
  boolean hasInitialResponse();
  
  byte[] evaluateChallenge(byte[] paramArrayOfByte) throws SaslException;
  
  boolean isComplete();
  
  byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SaslException;
  
  byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SaslException;
  
  Object getNegotiatedProperty(String paramString);
  
  void dispose() throws SaslException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\sasl\SaslClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */