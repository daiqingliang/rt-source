package sun.security.jgss.spi;

import com.sun.security.jgss.InquireType;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Provider;
import org.ietf.jgss.ChannelBinding;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import org.ietf.jgss.Oid;

public interface GSSContextSpi {
  Provider getProvider();
  
  void requestLifetime(int paramInt) throws GSSException;
  
  void requestMutualAuth(boolean paramBoolean) throws GSSException;
  
  void requestReplayDet(boolean paramBoolean) throws GSSException;
  
  void requestSequenceDet(boolean paramBoolean) throws GSSException;
  
  void requestCredDeleg(boolean paramBoolean) throws GSSException;
  
  void requestAnonymity(boolean paramBoolean) throws GSSException;
  
  void requestConf(boolean paramBoolean) throws GSSException;
  
  void requestInteg(boolean paramBoolean) throws GSSException;
  
  void requestDelegPolicy(boolean paramBoolean) throws GSSException;
  
  void setChannelBinding(ChannelBinding paramChannelBinding) throws GSSException;
  
  boolean getCredDelegState();
  
  boolean getMutualAuthState();
  
  boolean getReplayDetState();
  
  boolean getSequenceDetState();
  
  boolean getAnonymityState();
  
  boolean getDelegPolicyState();
  
  boolean isTransferable();
  
  boolean isProtReady();
  
  boolean isInitiator();
  
  boolean getConfState();
  
  boolean getIntegState();
  
  int getLifetime();
  
  boolean isEstablished();
  
  GSSNameSpi getSrcName() throws GSSException;
  
  GSSNameSpi getTargName() throws GSSException;
  
  Oid getMech() throws GSSException;
  
  GSSCredentialSpi getDelegCred() throws GSSException;
  
  byte[] initSecContext(InputStream paramInputStream, int paramInt) throws GSSException;
  
  byte[] acceptSecContext(InputStream paramInputStream, int paramInt) throws GSSException;
  
  int getWrapSizeLimit(int paramInt1, boolean paramBoolean, int paramInt2) throws GSSException;
  
  void wrap(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp) throws GSSException;
  
  byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp) throws GSSException;
  
  void unwrap(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp) throws GSSException;
  
  byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp) throws GSSException;
  
  void getMIC(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp) throws GSSException;
  
  byte[] getMIC(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp) throws GSSException;
  
  void verifyMIC(InputStream paramInputStream1, InputStream paramInputStream2, MessageProp paramMessageProp) throws GSSException;
  
  void verifyMIC(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4, MessageProp paramMessageProp) throws GSSException;
  
  byte[] export() throws GSSException;
  
  void dispose() throws GSSException;
  
  Object inquireSecContext(InquireType paramInquireType) throws GSSException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\spi\GSSContextSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */