package java.time.zone;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ZoneRules implements Serializable {
  private static final long serialVersionUID = 3044319355680032515L;
  
  private static final int LAST_CACHED_YEAR = 2100;
  
  private final long[] standardTransitions;
  
  private final ZoneOffset[] standardOffsets;
  
  private final long[] savingsInstantTransitions;
  
  private final LocalDateTime[] savingsLocalTransitions;
  
  private final ZoneOffset[] wallOffsets;
  
  private final ZoneOffsetTransitionRule[] lastRules;
  
  private final ConcurrentMap<Integer, ZoneOffsetTransition[]> lastRulesCache = new ConcurrentHashMap();
  
  private static final long[] EMPTY_LONG_ARRAY = new long[0];
  
  private static final ZoneOffsetTransitionRule[] EMPTY_LASTRULES = new ZoneOffsetTransitionRule[0];
  
  private static final LocalDateTime[] EMPTY_LDT_ARRAY = new LocalDateTime[0];
  
  public static ZoneRules of(ZoneOffset paramZoneOffset1, ZoneOffset paramZoneOffset2, List<ZoneOffsetTransition> paramList1, List<ZoneOffsetTransition> paramList2, List<ZoneOffsetTransitionRule> paramList3) {
    Objects.requireNonNull(paramZoneOffset1, "baseStandardOffset");
    Objects.requireNonNull(paramZoneOffset2, "baseWallOffset");
    Objects.requireNonNull(paramList1, "standardOffsetTransitionList");
    Objects.requireNonNull(paramList2, "transitionList");
    Objects.requireNonNull(paramList3, "lastRules");
    return new ZoneRules(paramZoneOffset1, paramZoneOffset2, paramList1, paramList2, paramList3);
  }
  
  public static ZoneRules of(ZoneOffset paramZoneOffset) {
    Objects.requireNonNull(paramZoneOffset, "offset");
    return new ZoneRules(paramZoneOffset);
  }
  
  ZoneRules(ZoneOffset paramZoneOffset1, ZoneOffset paramZoneOffset2, List<ZoneOffsetTransition> paramList1, List<ZoneOffsetTransition> paramList2, List<ZoneOffsetTransitionRule> paramList3) {
    this.standardTransitions = new long[paramList1.size()];
    this.standardOffsets = new ZoneOffset[paramList1.size() + 1];
    this.standardOffsets[0] = paramZoneOffset1;
    for (byte b1 = 0; b1 < paramList1.size(); b1++) {
      this.standardTransitions[b1] = ((ZoneOffsetTransition)paramList1.get(b1)).toEpochSecond();
      this.standardOffsets[b1 + 1] = ((ZoneOffsetTransition)paramList1.get(b1)).getOffsetAfter();
    } 
    ArrayList arrayList1 = new ArrayList();
    ArrayList arrayList2 = new ArrayList();
    arrayList2.add(paramZoneOffset2);
    for (ZoneOffsetTransition zoneOffsetTransition : paramList2) {
      if (zoneOffsetTransition.isGap()) {
        arrayList1.add(zoneOffsetTransition.getDateTimeBefore());
        arrayList1.add(zoneOffsetTransition.getDateTimeAfter());
      } else {
        arrayList1.add(zoneOffsetTransition.getDateTimeAfter());
        arrayList1.add(zoneOffsetTransition.getDateTimeBefore());
      } 
      arrayList2.add(zoneOffsetTransition.getOffsetAfter());
    } 
    this.savingsLocalTransitions = (LocalDateTime[])arrayList1.toArray(new LocalDateTime[arrayList1.size()]);
    this.wallOffsets = (ZoneOffset[])arrayList2.toArray(new ZoneOffset[arrayList2.size()]);
    this.savingsInstantTransitions = new long[paramList2.size()];
    for (byte b2 = 0; b2 < paramList2.size(); b2++)
      this.savingsInstantTransitions[b2] = ((ZoneOffsetTransition)paramList2.get(b2)).toEpochSecond(); 
    if (paramList3.size() > 16)
      throw new IllegalArgumentException("Too many transition rules"); 
    this.lastRules = (ZoneOffsetTransitionRule[])paramList3.toArray(new ZoneOffsetTransitionRule[paramList3.size()]);
  }
  
  private ZoneRules(long[] paramArrayOfLong1, ZoneOffset[] paramArrayOfZoneOffset1, long[] paramArrayOfLong2, ZoneOffset[] paramArrayOfZoneOffset2, ZoneOffsetTransitionRule[] paramArrayOfZoneOffsetTransitionRule) {
    this.standardTransitions = paramArrayOfLong1;
    this.standardOffsets = paramArrayOfZoneOffset1;
    this.savingsInstantTransitions = paramArrayOfLong2;
    this.wallOffsets = paramArrayOfZoneOffset2;
    this.lastRules = paramArrayOfZoneOffsetTransitionRule;
    if (paramArrayOfLong2.length == 0) {
      this.savingsLocalTransitions = EMPTY_LDT_ARRAY;
    } else {
      ArrayList arrayList = new ArrayList();
      for (byte b = 0; b < paramArrayOfLong2.length; b++) {
        ZoneOffset zoneOffset1 = paramArrayOfZoneOffset2[b];
        ZoneOffset zoneOffset2 = paramArrayOfZoneOffset2[b + true];
        ZoneOffsetTransition zoneOffsetTransition = new ZoneOffsetTransition(paramArrayOfLong2[b], zoneOffset1, zoneOffset2);
        if (zoneOffsetTransition.isGap()) {
          arrayList.add(zoneOffsetTransition.getDateTimeBefore());
          arrayList.add(zoneOffsetTransition.getDateTimeAfter());
        } else {
          arrayList.add(zoneOffsetTransition.getDateTimeAfter());
          arrayList.add(zoneOffsetTransition.getDateTimeBefore());
        } 
      } 
      this.savingsLocalTransitions = (LocalDateTime[])arrayList.toArray(new LocalDateTime[arrayList.size()]);
    } 
  }
  
  private ZoneRules(ZoneOffset paramZoneOffset) {
    this.standardOffsets = new ZoneOffset[1];
    this.standardOffsets[0] = paramZoneOffset;
    this.standardTransitions = EMPTY_LONG_ARRAY;
    this.savingsInstantTransitions = EMPTY_LONG_ARRAY;
    this.savingsLocalTransitions = EMPTY_LDT_ARRAY;
    this.wallOffsets = this.standardOffsets;
    this.lastRules = EMPTY_LASTRULES;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  private Object writeReplace() { return new Ser((byte)1, this); }
  
  void writeExternal(DataOutput paramDataOutput) throws IOException {
    paramDataOutput.writeInt(this.standardTransitions.length);
    for (long l : this.standardTransitions)
      Ser.writeEpochSec(l, paramDataOutput); 
    for (ZoneOffset zoneOffset : this.standardOffsets)
      Ser.writeOffset(zoneOffset, paramDataOutput); 
    paramDataOutput.writeInt(this.savingsInstantTransitions.length);
    for (long l : this.savingsInstantTransitions)
      Ser.writeEpochSec(l, paramDataOutput); 
    for (ZoneOffset zoneOffset : this.wallOffsets)
      Ser.writeOffset(zoneOffset, paramDataOutput); 
    paramDataOutput.writeByte(this.lastRules.length);
    for (ZoneOffsetTransitionRule zoneOffsetTransitionRule : this.lastRules)
      zoneOffsetTransitionRule.writeExternal(paramDataOutput); 
  }
  
  static ZoneRules readExternal(DataInput paramDataInput) throws IOException, ClassNotFoundException {
    int i = paramDataInput.readInt();
    long[] arrayOfLong1 = (i == 0) ? EMPTY_LONG_ARRAY : new long[i];
    for (byte b1 = 0; b1 < i; b1++)
      arrayOfLong1[b1] = Ser.readEpochSec(paramDataInput); 
    ZoneOffset[] arrayOfZoneOffset1 = new ZoneOffset[i + 1];
    int j;
    for (j = 0; j < arrayOfZoneOffset1.length; j++)
      arrayOfZoneOffset1[j] = Ser.readOffset(paramDataInput); 
    j = paramDataInput.readInt();
    long[] arrayOfLong2 = (j == 0) ? EMPTY_LONG_ARRAY : new long[j];
    for (byte b2 = 0; b2 < j; b2++)
      arrayOfLong2[b2] = Ser.readEpochSec(paramDataInput); 
    ZoneOffset[] arrayOfZoneOffset2 = new ZoneOffset[j + 1];
    byte b;
    for (b = 0; b < arrayOfZoneOffset2.length; b++)
      arrayOfZoneOffset2[b] = Ser.readOffset(paramDataInput); 
    b = paramDataInput.readByte();
    ZoneOffsetTransitionRule[] arrayOfZoneOffsetTransitionRule = (b == 0) ? EMPTY_LASTRULES : new ZoneOffsetTransitionRule[b];
    for (byte b3 = 0; b3 < b; b3++)
      arrayOfZoneOffsetTransitionRule[b3] = ZoneOffsetTransitionRule.readExternal(paramDataInput); 
    return new ZoneRules(arrayOfLong1, arrayOfZoneOffset1, arrayOfLong2, arrayOfZoneOffset2, arrayOfZoneOffsetTransitionRule);
  }
  
  public boolean isFixedOffset() { return (this.savingsInstantTransitions.length == 0); }
  
  public ZoneOffset getOffset(Instant paramInstant) {
    if (this.savingsInstantTransitions.length == 0)
      return this.standardOffsets[0]; 
    long l = paramInstant.getEpochSecond();
    if (this.lastRules.length > 0 && l > this.savingsInstantTransitions[this.savingsInstantTransitions.length - 1]) {
      int j = findYear(l, this.wallOffsets[this.wallOffsets.length - 1]);
      ZoneOffsetTransition[] arrayOfZoneOffsetTransition = findTransitionArray(j);
      ZoneOffsetTransition zoneOffsetTransition = null;
      for (byte b = 0; b < arrayOfZoneOffsetTransition.length; b++) {
        zoneOffsetTransition = arrayOfZoneOffsetTransition[b];
        if (l < zoneOffsetTransition.toEpochSecond())
          return zoneOffsetTransition.getOffsetBefore(); 
      } 
      return zoneOffsetTransition.getOffsetAfter();
    } 
    int i = Arrays.binarySearch(this.savingsInstantTransitions, l);
    if (i < 0)
      i = -i - 2; 
    return this.wallOffsets[i + 1];
  }
  
  public ZoneOffset getOffset(LocalDateTime paramLocalDateTime) {
    Object object = getOffsetInfo(paramLocalDateTime);
    return (object instanceof ZoneOffsetTransition) ? ((ZoneOffsetTransition)object).getOffsetBefore() : (ZoneOffset)object;
  }
  
  public List<ZoneOffset> getValidOffsets(LocalDateTime paramLocalDateTime) {
    Object object = getOffsetInfo(paramLocalDateTime);
    return (object instanceof ZoneOffsetTransition) ? ((ZoneOffsetTransition)object).getValidOffsets() : Collections.singletonList((ZoneOffset)object);
  }
  
  public ZoneOffsetTransition getTransition(LocalDateTime paramLocalDateTime) {
    Object object = getOffsetInfo(paramLocalDateTime);
    return (object instanceof ZoneOffsetTransition) ? (ZoneOffsetTransition)object : null;
  }
  
  private Object getOffsetInfo(LocalDateTime paramLocalDateTime) {
    if (this.savingsInstantTransitions.length == 0)
      return this.standardOffsets[0]; 
    if (this.lastRules.length > 0 && paramLocalDateTime.isAfter(this.savingsLocalTransitions[this.savingsLocalTransitions.length - 1])) {
      ZoneOffsetTransition[] arrayOfZoneOffsetTransition = findTransitionArray(paramLocalDateTime.getYear());
      Object object = null;
      for (ZoneOffsetTransition zoneOffsetTransition : arrayOfZoneOffsetTransition) {
        object = findOffsetInfo(paramLocalDateTime, zoneOffsetTransition);
        if (object instanceof ZoneOffsetTransition || object.equals(zoneOffsetTransition.getOffsetBefore()))
          return object; 
      } 
      return object;
    } 
    int i = Arrays.binarySearch(this.savingsLocalTransitions, paramLocalDateTime);
    if (i == -1)
      return this.wallOffsets[0]; 
    if (i < 0) {
      i = -i - 2;
    } else if (i < this.savingsLocalTransitions.length - 1 && this.savingsLocalTransitions[i].equals(this.savingsLocalTransitions[i + 1])) {
      i++;
    } 
    if ((i & true) == 0) {
      LocalDateTime localDateTime1 = this.savingsLocalTransitions[i];
      LocalDateTime localDateTime2 = this.savingsLocalTransitions[i + 1];
      ZoneOffset zoneOffset1 = this.wallOffsets[i / 2];
      ZoneOffset zoneOffset2 = this.wallOffsets[i / 2 + 1];
      return (zoneOffset2.getTotalSeconds() > zoneOffset1.getTotalSeconds()) ? new ZoneOffsetTransition(localDateTime1, zoneOffset1, zoneOffset2) : new ZoneOffsetTransition(localDateTime2, zoneOffset1, zoneOffset2);
    } 
    return this.wallOffsets[i / 2 + 1];
  }
  
  private Object findOffsetInfo(LocalDateTime paramLocalDateTime, ZoneOffsetTransition paramZoneOffsetTransition) {
    LocalDateTime localDateTime = paramZoneOffsetTransition.getDateTimeBefore();
    return paramZoneOffsetTransition.isGap() ? (paramLocalDateTime.isBefore(localDateTime) ? paramZoneOffsetTransition.getOffsetBefore() : (paramLocalDateTime.isBefore(paramZoneOffsetTransition.getDateTimeAfter()) ? paramZoneOffsetTransition : paramZoneOffsetTransition.getOffsetAfter())) : (!paramLocalDateTime.isBefore(localDateTime) ? paramZoneOffsetTransition.getOffsetAfter() : (paramLocalDateTime.isBefore(paramZoneOffsetTransition.getDateTimeAfter()) ? paramZoneOffsetTransition.getOffsetBefore() : paramZoneOffsetTransition));
  }
  
  private ZoneOffsetTransition[] findTransitionArray(int paramInt) {
    Integer integer = Integer.valueOf(paramInt);
    ZoneOffsetTransition[] arrayOfZoneOffsetTransition = (ZoneOffsetTransition[])this.lastRulesCache.get(integer);
    if (arrayOfZoneOffsetTransition != null)
      return arrayOfZoneOffsetTransition; 
    ZoneOffsetTransitionRule[] arrayOfZoneOffsetTransitionRule = this.lastRules;
    arrayOfZoneOffsetTransition = new ZoneOffsetTransition[arrayOfZoneOffsetTransitionRule.length];
    for (byte b = 0; b < arrayOfZoneOffsetTransitionRule.length; b++)
      arrayOfZoneOffsetTransition[b] = arrayOfZoneOffsetTransitionRule[b].createTransition(paramInt); 
    if (paramInt < 2100)
      this.lastRulesCache.putIfAbsent(integer, arrayOfZoneOffsetTransition); 
    return arrayOfZoneOffsetTransition;
  }
  
  public ZoneOffset getStandardOffset(Instant paramInstant) {
    if (this.savingsInstantTransitions.length == 0)
      return this.standardOffsets[0]; 
    long l = paramInstant.getEpochSecond();
    int i = Arrays.binarySearch(this.standardTransitions, l);
    if (i < 0)
      i = -i - 2; 
    return this.standardOffsets[i + 1];
  }
  
  public Duration getDaylightSavings(Instant paramInstant) {
    if (this.savingsInstantTransitions.length == 0)
      return Duration.ZERO; 
    ZoneOffset zoneOffset1 = getStandardOffset(paramInstant);
    ZoneOffset zoneOffset2 = getOffset(paramInstant);
    return Duration.ofSeconds((zoneOffset2.getTotalSeconds() - zoneOffset1.getTotalSeconds()));
  }
  
  public boolean isDaylightSavings(Instant paramInstant) { return !getStandardOffset(paramInstant).equals(getOffset(paramInstant)); }
  
  public boolean isValidOffset(LocalDateTime paramLocalDateTime, ZoneOffset paramZoneOffset) { return getValidOffsets(paramLocalDateTime).contains(paramZoneOffset); }
  
  public ZoneOffsetTransition nextTransition(Instant paramInstant) {
    if (this.savingsInstantTransitions.length == 0)
      return null; 
    long l = paramInstant.getEpochSecond();
    if (l >= this.savingsInstantTransitions[this.savingsInstantTransitions.length - 1]) {
      if (this.lastRules.length == 0)
        return null; 
      int j = findYear(l, this.wallOffsets[this.wallOffsets.length - 1]);
      ZoneOffsetTransition[] arrayOfZoneOffsetTransition = findTransitionArray(j);
      for (ZoneOffsetTransition zoneOffsetTransition : arrayOfZoneOffsetTransition) {
        if (l < zoneOffsetTransition.toEpochSecond())
          return zoneOffsetTransition; 
      } 
      if (j < 999999999) {
        arrayOfZoneOffsetTransition = findTransitionArray(j + 1);
        return arrayOfZoneOffsetTransition[0];
      } 
      return null;
    } 
    int i = Arrays.binarySearch(this.savingsInstantTransitions, l);
    if (i < 0) {
      i = -i - 1;
    } else {
      i++;
    } 
    return new ZoneOffsetTransition(this.savingsInstantTransitions[i], this.wallOffsets[i], this.wallOffsets[i + 1]);
  }
  
  public ZoneOffsetTransition previousTransition(Instant paramInstant) {
    if (this.savingsInstantTransitions.length == 0)
      return null; 
    long l1 = paramInstant.getEpochSecond();
    if (paramInstant.getNano() > 0 && l1 < Float.MAX_VALUE)
      l1++; 
    long l2 = this.savingsInstantTransitions[this.savingsInstantTransitions.length - 1];
    if (this.lastRules.length > 0 && l1 > l2) {
      ZoneOffset zoneOffset = this.wallOffsets[this.wallOffsets.length - 1];
      int j = findYear(l1, zoneOffset);
      ZoneOffsetTransition[] arrayOfZoneOffsetTransition = findTransitionArray(j);
      int k;
      for (k = arrayOfZoneOffsetTransition.length - 1; k >= 0; k--) {
        if (l1 > arrayOfZoneOffsetTransition[k].toEpochSecond())
          return arrayOfZoneOffsetTransition[k]; 
      } 
      k = findYear(l2, zoneOffset);
      if (--j > k) {
        arrayOfZoneOffsetTransition = findTransitionArray(j);
        return arrayOfZoneOffsetTransition[arrayOfZoneOffsetTransition.length - 1];
      } 
    } 
    int i = Arrays.binarySearch(this.savingsInstantTransitions, l1);
    if (i < 0)
      i = -i - 1; 
    return (i <= 0) ? null : new ZoneOffsetTransition(this.savingsInstantTransitions[i - 1], this.wallOffsets[i - 1], this.wallOffsets[i]);
  }
  
  private int findYear(long paramLong, ZoneOffset paramZoneOffset) {
    long l1 = paramLong + paramZoneOffset.getTotalSeconds();
    long l2 = Math.floorDiv(l1, 86400L);
    return LocalDate.ofEpochDay(l2).getYear();
  }
  
  public List<ZoneOffsetTransition> getTransitions() {
    ArrayList arrayList = new ArrayList();
    for (byte b = 0; b < this.savingsInstantTransitions.length; b++)
      arrayList.add(new ZoneOffsetTransition(this.savingsInstantTransitions[b], this.wallOffsets[b], this.wallOffsets[b + true])); 
    return Collections.unmodifiableList(arrayList);
  }
  
  public List<ZoneOffsetTransitionRule> getTransitionRules() { return Collections.unmodifiableList(Arrays.asList(this.lastRules)); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof ZoneRules) {
      ZoneRules zoneRules = (ZoneRules)paramObject;
      return (Arrays.equals(this.standardTransitions, zoneRules.standardTransitions) && Arrays.equals(this.standardOffsets, zoneRules.standardOffsets) && Arrays.equals(this.savingsInstantTransitions, zoneRules.savingsInstantTransitions) && Arrays.equals(this.wallOffsets, zoneRules.wallOffsets) && Arrays.equals(this.lastRules, zoneRules.lastRules));
    } 
    return false;
  }
  
  public int hashCode() { return Arrays.hashCode(this.standardTransitions) ^ Arrays.hashCode(this.standardOffsets) ^ Arrays.hashCode(this.savingsInstantTransitions) ^ Arrays.hashCode(this.wallOffsets) ^ Arrays.hashCode(this.lastRules); }
  
  public String toString() { return "ZoneRules[currentStandardOffset=" + this.standardOffsets[this.standardOffsets.length - 1] + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\zone\ZoneRules.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */