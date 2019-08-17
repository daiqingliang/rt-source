package java.time.chrono;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

final class ChronoPeriodImpl implements ChronoPeriod, Serializable {
  private static final long serialVersionUID = 57387258289L;
  
  private static final List<TemporalUnit> SUPPORTED_UNITS = Collections.unmodifiableList(Arrays.asList(new TemporalUnit[] { ChronoUnit.YEARS, ChronoUnit.MONTHS, ChronoUnit.DAYS }));
  
  private final Chronology chrono;
  
  final int years;
  
  final int months;
  
  final int days;
  
  ChronoPeriodImpl(Chronology paramChronology, int paramInt1, int paramInt2, int paramInt3) {
    Objects.requireNonNull(paramChronology, "chrono");
    this.chrono = paramChronology;
    this.years = paramInt1;
    this.months = paramInt2;
    this.days = paramInt3;
  }
  
  public long get(TemporalUnit paramTemporalUnit) {
    if (paramTemporalUnit == ChronoUnit.YEARS)
      return this.years; 
    if (paramTemporalUnit == ChronoUnit.MONTHS)
      return this.months; 
    if (paramTemporalUnit == ChronoUnit.DAYS)
      return this.days; 
    throw new UnsupportedTemporalTypeException("Unsupported unit: " + paramTemporalUnit);
  }
  
  public List<TemporalUnit> getUnits() { return SUPPORTED_UNITS; }
  
  public Chronology getChronology() { return this.chrono; }
  
  public boolean isZero() { return (this.years == 0 && this.months == 0 && this.days == 0); }
  
  public boolean isNegative() { return (this.years < 0 || this.months < 0 || this.days < 0); }
  
  public ChronoPeriod plus(TemporalAmount paramTemporalAmount) {
    ChronoPeriodImpl chronoPeriodImpl = validateAmount(paramTemporalAmount);
    return new ChronoPeriodImpl(this.chrono, Math.addExact(this.years, chronoPeriodImpl.years), Math.addExact(this.months, chronoPeriodImpl.months), Math.addExact(this.days, chronoPeriodImpl.days));
  }
  
  public ChronoPeriod minus(TemporalAmount paramTemporalAmount) {
    ChronoPeriodImpl chronoPeriodImpl = validateAmount(paramTemporalAmount);
    return new ChronoPeriodImpl(this.chrono, Math.subtractExact(this.years, chronoPeriodImpl.years), Math.subtractExact(this.months, chronoPeriodImpl.months), Math.subtractExact(this.days, chronoPeriodImpl.days));
  }
  
  private ChronoPeriodImpl validateAmount(TemporalAmount paramTemporalAmount) {
    Objects.requireNonNull(paramTemporalAmount, "amount");
    if (!(paramTemporalAmount instanceof ChronoPeriodImpl))
      throw new DateTimeException("Unable to obtain ChronoPeriod from TemporalAmount: " + paramTemporalAmount.getClass()); 
    ChronoPeriodImpl chronoPeriodImpl = (ChronoPeriodImpl)paramTemporalAmount;
    if (!this.chrono.equals(chronoPeriodImpl.getChronology()))
      throw new ClassCastException("Chronology mismatch, expected: " + this.chrono.getId() + ", actual: " + chronoPeriodImpl.getChronology().getId()); 
    return chronoPeriodImpl;
  }
  
  public ChronoPeriod multipliedBy(int paramInt) { return (isZero() || paramInt == 1) ? this : new ChronoPeriodImpl(this.chrono, Math.multiplyExact(this.years, paramInt), Math.multiplyExact(this.months, paramInt), Math.multiplyExact(this.days, paramInt)); }
  
  public ChronoPeriod normalized() {
    long l = monthRange();
    if (l > 0L) {
      long l1 = this.years * l + this.months;
      long l2 = l1 / l;
      int i = (int)(l1 % l);
      return (l2 == this.years && i == this.months) ? this : new ChronoPeriodImpl(this.chrono, Math.toIntExact(l2), i, this.days);
    } 
    return this;
  }
  
  private long monthRange() {
    ValueRange valueRange = this.chrono.range(ChronoField.MONTH_OF_YEAR);
    return (valueRange.isFixed() && valueRange.isIntValue()) ? (valueRange.getMaximum() - valueRange.getMinimum() + 1L) : -1L;
  }
  
  public Temporal addTo(Temporal paramTemporal) {
    validateChrono(paramTemporal);
    if (this.months == 0) {
      if (this.years != 0)
        paramTemporal = paramTemporal.plus(this.years, ChronoUnit.YEARS); 
    } else {
      long l = monthRange();
      if (l > 0L) {
        paramTemporal = paramTemporal.plus(this.years * l + this.months, ChronoUnit.MONTHS);
      } else {
        if (this.years != 0)
          paramTemporal = paramTemporal.plus(this.years, ChronoUnit.YEARS); 
        paramTemporal = paramTemporal.plus(this.months, ChronoUnit.MONTHS);
      } 
    } 
    if (this.days != 0)
      paramTemporal = paramTemporal.plus(this.days, ChronoUnit.DAYS); 
    return paramTemporal;
  }
  
  public Temporal subtractFrom(Temporal paramTemporal) {
    validateChrono(paramTemporal);
    if (this.months == 0) {
      if (this.years != 0)
        paramTemporal = paramTemporal.minus(this.years, ChronoUnit.YEARS); 
    } else {
      long l = monthRange();
      if (l > 0L) {
        paramTemporal = paramTemporal.minus(this.years * l + this.months, ChronoUnit.MONTHS);
      } else {
        if (this.years != 0)
          paramTemporal = paramTemporal.minus(this.years, ChronoUnit.YEARS); 
        paramTemporal = paramTemporal.minus(this.months, ChronoUnit.MONTHS);
      } 
    } 
    if (this.days != 0)
      paramTemporal = paramTemporal.minus(this.days, ChronoUnit.DAYS); 
    return paramTemporal;
  }
  
  private void validateChrono(TemporalAccessor paramTemporalAccessor) {
    Objects.requireNonNull(paramTemporalAccessor, "temporal");
    Chronology chronology = (Chronology)paramTemporalAccessor.query(TemporalQueries.chronology());
    if (chronology != null && !this.chrono.equals(chronology))
      throw new DateTimeException("Chronology mismatch, expected: " + this.chrono.getId() + ", actual: " + chronology.getId()); 
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof ChronoPeriodImpl) {
      ChronoPeriodImpl chronoPeriodImpl = (ChronoPeriodImpl)paramObject;
      return (this.years == chronoPeriodImpl.years && this.months == chronoPeriodImpl.months && this.days == chronoPeriodImpl.days && this.chrono.equals(chronoPeriodImpl.chrono));
    } 
    return false;
  }
  
  public int hashCode() { return this.years + Integer.rotateLeft(this.months, 8) + Integer.rotateLeft(this.days, 16) ^ this.chrono.hashCode(); }
  
  public String toString() {
    if (isZero())
      return getChronology().toString() + " P0D"; 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(getChronology().toString()).append(' ').append('P');
    if (this.years != 0)
      stringBuilder.append(this.years).append('Y'); 
    if (this.months != 0)
      stringBuilder.append(this.months).append('M'); 
    if (this.days != 0)
      stringBuilder.append(this.days).append('D'); 
    return stringBuilder.toString();
  }
  
  protected Object writeReplace() { return new Ser((byte)9, this); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ObjectStreamException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  void writeExternal(DataOutput paramDataOutput) throws IOException {
    paramDataOutput.writeUTF(this.chrono.getId());
    paramDataOutput.writeInt(this.years);
    paramDataOutput.writeInt(this.months);
    paramDataOutput.writeInt(this.days);
  }
  
  static ChronoPeriodImpl readExternal(DataInput paramDataInput) throws IOException {
    Chronology chronology = Chronology.of(paramDataInput.readUTF());
    int i = paramDataInput.readInt();
    int j = paramDataInput.readInt();
    int k = paramDataInput.readInt();
    return new ChronoPeriodImpl(chronology, i, j, k);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\chrono\ChronoPeriodImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */