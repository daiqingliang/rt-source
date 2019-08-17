package com.sun.security.sasl.digest;

import javax.security.sasl.SaslException;

interface SecurityCtx {
  byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SaslException;
  
  byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SaslException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\sasl\digest\SecurityCtx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */