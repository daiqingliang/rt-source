package sun.security.jgss.wrapper;

import java.util.Hashtable;
import org.ietf.jgss.ChannelBinding;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import org.ietf.jgss.Oid;

class GSSLibStub {
  private Oid mech;
  
  private long pMech;
  
  private static Hashtable<Oid, GSSLibStub> table = new Hashtable(5);
  
  static native boolean init(String paramString, boolean paramBoolean);
  
  private static native long getMechPtr(byte[] paramArrayOfByte);
  
  static native Oid[] indicateMechs();
  
  native Oid[] inquireNamesForMech();
  
  native void releaseName(long paramLong);
  
  native long importName(byte[] paramArrayOfByte, Oid paramOid);
  
  native boolean compareName(long paramLong1, long paramLong2);
  
  native long canonicalizeName(long paramLong);
  
  native byte[] exportName(long paramLong) throws GSSException;
  
  native Object[] displayName(long paramLong) throws GSSException;
  
  native long acquireCred(long paramLong, int paramInt1, int paramInt2) throws GSSException;
  
  native long releaseCred(long paramLong);
  
  native long getCredName(long paramLong);
  
  native int getCredTime(long paramLong);
  
  native int getCredUsage(long paramLong);
  
  native NativeGSSContext importContext(byte[] paramArrayOfByte);
  
  native byte[] initContext(long paramLong1, long paramLong2, ChannelBinding paramChannelBinding, byte[] paramArrayOfByte, NativeGSSContext paramNativeGSSContext);
  
  native byte[] acceptContext(long paramLong, ChannelBinding paramChannelBinding, byte[] paramArrayOfByte, NativeGSSContext paramNativeGSSContext);
  
  native long[] inquireContext(long paramLong);
  
  native Oid getContextMech(long paramLong);
  
  native long getContextName(long paramLong, boolean paramBoolean);
  
  native int getContextTime(long paramLong);
  
  native long deleteContext(long paramLong);
  
  native int wrapSizeLimit(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  native byte[] exportContext(long paramLong) throws GSSException;
  
  native byte[] getMic(long paramLong, int paramInt, byte[] paramArrayOfByte);
  
  native void verifyMic(long paramLong, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, MessageProp paramMessageProp);
  
  native byte[] wrap(long paramLong, byte[] paramArrayOfByte, MessageProp paramMessageProp);
  
  native byte[] unwrap(long paramLong, byte[] paramArrayOfByte, MessageProp paramMessageProp);
  
  static GSSLibStub getInstance(Oid paramOid) throws GSSException {
    GSSLibStub gSSLibStub = (GSSLibStub)table.get(paramOid);
    if (gSSLibStub == null) {
      gSSLibStub = new GSSLibStub(paramOid);
      table.put(paramOid, gSSLibStub);
    } 
    return gSSLibStub;
  }
  
  private GSSLibStub(Oid paramOid) throws GSSException {
    SunNativeProvider.debug("Created GSSLibStub for mech " + paramOid);
    this.mech = paramOid;
    this.pMech = getMechPtr(paramOid.getDER());
  }
  
  public boolean equals(Object paramObject) { return (paramObject == this) ? true : (!(paramObject instanceof GSSLibStub) ? false : this.mech.equals(((GSSLibStub)paramObject).getMech())); }
  
  public int hashCode() { return this.mech.hashCode(); }
  
  Oid getMech() { return this.mech; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\wrapper\GSSLibStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */