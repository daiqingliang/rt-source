package java.sql;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class BatchUpdateException extends SQLException {
  private int[] updateCounts;
  
  private long[] longUpdateCounts;
  
  private static final long serialVersionUID = 5977529877145521757L;
  
  public BatchUpdateException(String paramString1, String paramString2, int paramInt, int[] paramArrayOfInt) {
    super(paramString1, paramString2, paramInt);
    this.updateCounts = (paramArrayOfInt == null) ? null : Arrays.copyOf(paramArrayOfInt, paramArrayOfInt.length);
    this.longUpdateCounts = (paramArrayOfInt == null) ? null : copyUpdateCount(paramArrayOfInt);
  }
  
  public BatchUpdateException(String paramString1, String paramString2, int[] paramArrayOfInt) { this(paramString1, paramString2, 0, paramArrayOfInt); }
  
  public BatchUpdateException(String paramString, int[] paramArrayOfInt) { this(paramString, null, 0, paramArrayOfInt); }
  
  public BatchUpdateException(int[] paramArrayOfInt) { this(null, null, 0, paramArrayOfInt); }
  
  public BatchUpdateException() { this(null, null, 0, null); }
  
  public BatchUpdateException(Throwable paramThrowable) { this((paramThrowable == null) ? null : paramThrowable.toString(), null, 0, (int[])null, paramThrowable); }
  
  public BatchUpdateException(int[] paramArrayOfInt, Throwable paramThrowable) { this((paramThrowable == null) ? null : paramThrowable.toString(), null, 0, paramArrayOfInt, paramThrowable); }
  
  public BatchUpdateException(String paramString, int[] paramArrayOfInt, Throwable paramThrowable) { this(paramString, null, 0, paramArrayOfInt, paramThrowable); }
  
  public BatchUpdateException(String paramString1, String paramString2, int[] paramArrayOfInt, Throwable paramThrowable) { this(paramString1, paramString2, 0, paramArrayOfInt, paramThrowable); }
  
  public BatchUpdateException(String paramString1, String paramString2, int paramInt, int[] paramArrayOfInt, Throwable paramThrowable) {
    super(paramString1, paramString2, paramInt, paramThrowable);
    this.updateCounts = (paramArrayOfInt == null) ? null : Arrays.copyOf(paramArrayOfInt, paramArrayOfInt.length);
    this.longUpdateCounts = (paramArrayOfInt == null) ? null : copyUpdateCount(paramArrayOfInt);
  }
  
  public int[] getUpdateCounts() { return (this.updateCounts == null) ? null : Arrays.copyOf(this.updateCounts, this.updateCounts.length); }
  
  public BatchUpdateException(String paramString1, String paramString2, int paramInt, long[] paramArrayOfLong, Throwable paramThrowable) {
    super(paramString1, paramString2, paramInt, paramThrowable);
    this.longUpdateCounts = (paramArrayOfLong == null) ? null : Arrays.copyOf(paramArrayOfLong, paramArrayOfLong.length);
    this.updateCounts = (this.longUpdateCounts == null) ? null : copyUpdateCount(this.longUpdateCounts);
  }
  
  public long[] getLargeUpdateCounts() { return (this.longUpdateCounts == null) ? null : Arrays.copyOf(this.longUpdateCounts, this.longUpdateCounts.length); }
  
  private static long[] copyUpdateCount(int[] paramArrayOfInt) {
    long[] arrayOfLong = new long[paramArrayOfInt.length];
    for (byte b = 0; b < paramArrayOfInt.length; b++)
      arrayOfLong[b] = paramArrayOfInt[b]; 
    return arrayOfLong;
  }
  
  private static int[] copyUpdateCount(long[] paramArrayOfLong) {
    int[] arrayOfInt = new int[paramArrayOfLong.length];
    for (byte b = 0; b < paramArrayOfLong.length; b++)
      arrayOfInt[b] = (int)paramArrayOfLong[b]; 
    return arrayOfInt;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    int[] arrayOfInt = (int[])getField.get("updateCounts", null);
    long[] arrayOfLong = (long[])getField.get("longUpdateCounts", null);
    if (arrayOfInt != null && arrayOfLong != null && arrayOfInt.length != arrayOfLong.length)
      throw new InvalidObjectException("update counts are not the expected size"); 
    if (arrayOfInt != null)
      this.updateCounts = (int[])arrayOfInt.clone(); 
    if (arrayOfLong != null)
      this.longUpdateCounts = (long[])arrayOfLong.clone(); 
    if (this.updateCounts == null && this.longUpdateCounts != null)
      this.updateCounts = copyUpdateCount(this.longUpdateCounts); 
    if (this.longUpdateCounts == null && this.updateCounts != null)
      this.longUpdateCounts = copyUpdateCount(this.updateCounts); 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException, ClassNotFoundException {
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("updateCounts", this.updateCounts);
    putField.put("longUpdateCounts", this.longUpdateCounts);
    paramObjectOutputStream.writeFields();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\BatchUpdateException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */