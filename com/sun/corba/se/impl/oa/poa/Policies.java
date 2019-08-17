package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.spi.extension.CopyObjectPolicy;
import com.sun.corba.se.spi.extension.ServantCachingPolicy;
import com.sun.corba.se.spi.extension.ZeroPortPolicy;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.IdAssignmentPolicy;
import org.omg.PortableServer.IdUniquenessPolicy;
import org.omg.PortableServer.ImplicitActivationPolicy;
import org.omg.PortableServer.LifespanPolicy;
import org.omg.PortableServer.POAPackage.InvalidPolicy;
import org.omg.PortableServer.RequestProcessingPolicy;
import org.omg.PortableServer.ServantRetentionPolicy;
import org.omg.PortableServer.ThreadPolicy;

public final class Policies {
  private static final int MIN_POA_POLICY_ID = 16;
  
  private static final int MAX_POA_POLICY_ID = 22;
  
  private static final int POLICY_TABLE_SIZE = 7;
  
  int defaultObjectCopierFactoryId;
  
  private HashMap policyMap = new HashMap();
  
  public static final Policies defaultPolicies = new Policies();
  
  public static final Policies rootPOAPolicies = new Policies(0, 0, 0, 1, 0, 0, 0);
  
  private int[] poaPolicyValues;
  
  private int getPolicyValue(int paramInt) { return this.poaPolicyValues[paramInt - 16]; }
  
  private void setPolicyValue(int paramInt1, int paramInt2) { this.poaPolicyValues[paramInt1 - 16] = paramInt2; }
  
  private Policies(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) { this.poaPolicyValues = new int[] { paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7 }; }
  
  private Policies() { this(0, 0, 0, 1, 1, 0, 0); }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("Policies[");
    boolean bool = true;
    Iterator iterator = this.policyMap.values().iterator();
    while (iterator.hasNext()) {
      if (bool) {
        bool = false;
      } else {
        stringBuffer.append(",");
      } 
      stringBuffer.append(iterator.next().toString());
    } 
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  private int getPOAPolicyValue(Policy paramPolicy) { return (paramPolicy instanceof ThreadPolicy) ? ((ThreadPolicy)paramPolicy).value().value() : ((paramPolicy instanceof LifespanPolicy) ? ((LifespanPolicy)paramPolicy).value().value() : ((paramPolicy instanceof IdUniquenessPolicy) ? ((IdUniquenessPolicy)paramPolicy).value().value() : ((paramPolicy instanceof IdAssignmentPolicy) ? ((IdAssignmentPolicy)paramPolicy).value().value() : ((paramPolicy instanceof ServantRetentionPolicy) ? ((ServantRetentionPolicy)paramPolicy).value().value() : ((paramPolicy instanceof RequestProcessingPolicy) ? ((RequestProcessingPolicy)paramPolicy).value().value() : ((paramPolicy instanceof ImplicitActivationPolicy) ? ((ImplicitActivationPolicy)paramPolicy).value().value() : -1)))))); }
  
  private void checkForPolicyError(BitSet paramBitSet) throws InvalidPolicy {
    for (short s = 0; s < paramBitSet.length(); s = (short)(s + 1)) {
      if (paramBitSet.get(s))
        throw new InvalidPolicy(s); 
    } 
  }
  
  private void addToErrorSet(Policy[] paramArrayOfPolicy, int paramInt, BitSet paramBitSet) {
    for (byte b = 0; b < paramArrayOfPolicy.length; b++) {
      if (paramArrayOfPolicy[b].policy_type() == paramInt) {
        paramBitSet.set(b);
        return;
      } 
    } 
  }
  
  Policies(Policy[] paramArrayOfPolicy, int paramInt) throws InvalidPolicy {
    this();
    this.defaultObjectCopierFactoryId = paramInt;
    if (paramArrayOfPolicy == null)
      return; 
    BitSet bitSet = new BitSet(paramArrayOfPolicy.length);
    short s;
    for (s = 0; s < paramArrayOfPolicy.length; s = (short)(s + 1)) {
      Policy policy1 = paramArrayOfPolicy[s];
      int i = getPOAPolicyValue(policy1);
      Integer integer = new Integer(policy1.policy_type());
      Policy policy2 = (Policy)this.policyMap.get(integer);
      if (policy2 == null)
        this.policyMap.put(integer, policy1); 
      if (i >= 0) {
        setPolicyValue(integer.intValue(), i);
        if (policy2 != null && getPOAPolicyValue(policy2) != i)
          bitSet.set(s); 
      } 
    } 
    if (!retainServants() && useActiveMapOnly()) {
      addToErrorSet(paramArrayOfPolicy, 21, bitSet);
      addToErrorSet(paramArrayOfPolicy, 22, bitSet);
    } 
    if (isImplicitlyActivated()) {
      if (!retainServants()) {
        addToErrorSet(paramArrayOfPolicy, 20, bitSet);
        addToErrorSet(paramArrayOfPolicy, 21, bitSet);
      } 
      if (!isSystemAssignedIds()) {
        addToErrorSet(paramArrayOfPolicy, 20, bitSet);
        addToErrorSet(paramArrayOfPolicy, 19, bitSet);
      } 
    } 
    checkForPolicyError(bitSet);
  }
  
  public Policy get_effective_policy(int paramInt) {
    Integer integer = new Integer(paramInt);
    return (Policy)this.policyMap.get(integer);
  }
  
  public final boolean isOrbControlledThreads() { return (getPolicyValue(16) == 0); }
  
  public final boolean isSingleThreaded() { return (getPolicyValue(16) == 1); }
  
  public final boolean isTransient() { return (getPolicyValue(17) == 0); }
  
  public final boolean isPersistent() { return (getPolicyValue(17) == 1); }
  
  public final boolean isUniqueIds() { return (getPolicyValue(18) == 0); }
  
  public final boolean isMultipleIds() { return (getPolicyValue(18) == 1); }
  
  public final boolean isUserAssignedIds() { return (getPolicyValue(19) == 0); }
  
  public final boolean isSystemAssignedIds() { return (getPolicyValue(19) == 1); }
  
  public final boolean retainServants() { return (getPolicyValue(21) == 0); }
  
  public final boolean useActiveMapOnly() { return (getPolicyValue(22) == 0); }
  
  public final boolean useDefaultServant() { return (getPolicyValue(22) == 1); }
  
  public final boolean useServantManager() { return (getPolicyValue(22) == 2); }
  
  public final boolean isImplicitlyActivated() { return (getPolicyValue(20) == 0); }
  
  public final int servantCachingLevel() {
    Integer integer = new Integer(1398079488);
    ServantCachingPolicy servantCachingPolicy = (ServantCachingPolicy)this.policyMap.get(integer);
    return (servantCachingPolicy == null) ? 0 : servantCachingPolicy.getType();
  }
  
  public final boolean forceZeroPort() {
    Integer integer = new Integer(1398079489);
    ZeroPortPolicy zeroPortPolicy = (ZeroPortPolicy)this.policyMap.get(integer);
    return (zeroPortPolicy == null) ? false : zeroPortPolicy.forceZeroPort();
  }
  
  public final int getCopierId() {
    Integer integer = new Integer(1398079490);
    CopyObjectPolicy copyObjectPolicy = (CopyObjectPolicy)this.policyMap.get(integer);
    return (copyObjectPolicy != null) ? copyObjectPolicy.getValue() : this.defaultObjectCopierFactoryId;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\oa\poa\Policies.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */