package java.time.temporal;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.DateTimeException;

public final class ValueRange implements Serializable {
  private static final long serialVersionUID = -7317881728594519368L;
  
  private final long minSmallest;
  
  private final long minLargest;
  
  private final long maxSmallest;
  
  private final long maxLargest;
  
  public static ValueRange of(long paramLong1, long paramLong2) {
    if (paramLong1 > paramLong2)
      throw new IllegalArgumentException("Minimum value must be less than maximum value"); 
    return new ValueRange(paramLong1, paramLong1, paramLong2, paramLong2);
  }
  
  public static ValueRange of(long paramLong1, long paramLong2, long paramLong3) { return of(paramLong1, paramLong1, paramLong2, paramLong3); }
  
  public static ValueRange of(long paramLong1, long paramLong2, long paramLong3, long paramLong4) {
    if (paramLong1 > paramLong2)
      throw new IllegalArgumentException("Smallest minimum value must be less than largest minimum value"); 
    if (paramLong3 > paramLong4)
      throw new IllegalArgumentException("Smallest maximum value must be less than largest maximum value"); 
    if (paramLong2 > paramLong4)
      throw new IllegalArgumentException("Minimum value must be less than maximum value"); 
    return new ValueRange(paramLong1, paramLong2, paramLong3, paramLong4);
  }
  
  private ValueRange(long paramLong1, long paramLong2, long paramLong3, long paramLong4) {
    this.minSmallest = paramLong1;
    this.minLargest = paramLong2;
    this.maxSmallest = paramLong3;
    this.maxLargest = paramLong4;
  }
  
  public boolean isFixed() { return (this.minSmallest == this.minLargest && this.maxSmallest == this.maxLargest); }
  
  public long getMinimum() { return this.minSmallest; }
  
  public long getLargestMinimum() { return this.minLargest; }
  
  public long getSmallestMaximum() { return this.maxSmallest; }
  
  public long getMaximum() { return this.maxLargest; }
  
  public boolean isIntValue() { return (getMinimum() >= -2147483648L && getMaximum() <= 2147483647L); }
  
  public boolean isValidValue(long paramLong) { return (paramLong >= getMinimum() && paramLong <= getMaximum()); }
  
  public boolean isValidIntValue(long paramLong) { return (isIntValue() && isValidValue(paramLong)); }
  
  public long checkValidValue(long paramLong, TemporalField paramTemporalField) {
    if (!isValidValue(paramLong))
      throw new DateTimeException(genInvalidFieldMessage(paramTemporalField, paramLong)); 
    return paramLong;
  }
  
  public int checkValidIntValue(long paramLong, TemporalField paramTemporalField) {
    if (!isValidIntValue(paramLong))
      throw new DateTimeException(genInvalidFieldMessage(paramTemporalField, paramLong)); 
    return (int)paramLong;
  }
  
  private String genInvalidFieldMessage(TemporalField paramTemporalField, long paramLong) { return (paramTemporalField != null) ? ("Invalid value for " + paramTemporalField + " (valid values " + this + "): " + paramLong) : ("Invalid value (valid values " + this + "): " + paramLong); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException, InvalidObjectException {
    paramObjectInputStream.defaultReadObject();
    if (this.minSmallest > this.minLargest)
      throw new InvalidObjectException("Smallest minimum value must be less than largest minimum value"); 
    if (this.maxSmallest > this.maxLargest)
      throw new InvalidObjectException("Smallest maximum value must be less than largest maximum value"); 
    if (this.minLargest > this.maxLargest)
      throw new InvalidObjectException("Minimum value must be less than maximum value"); 
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (paramObject instanceof ValueRange) {
      ValueRange valueRange = (ValueRange)paramObject;
      return (this.minSmallest == valueRange.minSmallest && this.minLargest == valueRange.minLargest && this.maxSmallest == valueRange.maxSmallest && this.maxLargest == valueRange.maxLargest);
    } 
    return false;
  }
  
  public int hashCode() {
    long l = this.minSmallest + (this.minLargest << 16) + (this.minLargest >> 48) + (this.maxSmallest << 32) + (this.maxSmallest >> 32) + (this.maxLargest << 48) + (this.maxLargest >> 16);
    return (int)(l ^ l >>> 32);
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(this.minSmallest);
    if (this.minSmallest != this.minLargest)
      stringBuilder.append('/').append(this.minLargest); 
    stringBuilder.append(" - ").append(this.maxSmallest);
    if (this.maxSmallest != this.maxLargest)
      stringBuilder.append('/').append(this.maxLargest); 
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\temporal\ValueRange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */