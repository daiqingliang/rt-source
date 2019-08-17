package java.lang;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.Arrays;

public final class StringBuffer extends AbstractStringBuilder implements Serializable, CharSequence {
  private char[] toStringCache;
  
  static final long serialVersionUID = 3388685877147921107L;
  
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("value", char[].class), new ObjectStreamField("count", int.class), new ObjectStreamField("shared", boolean.class) };
  
  public StringBuffer() { super(16); }
  
  public StringBuffer(int paramInt) { super(paramInt); }
  
  public StringBuffer(String paramString) {
    super(paramString.length() + 16);
    append(paramString);
  }
  
  public StringBuffer(CharSequence paramCharSequence) {
    this(paramCharSequence.length() + 16);
    append(paramCharSequence);
  }
  
  public int length() { return this.count; }
  
  public int capacity() { return this.value.length; }
  
  public void ensureCapacity(int paramInt) { super.ensureCapacity(paramInt); }
  
  public void trimToSize() { super.trimToSize(); }
  
  public void setLength(int paramInt) {
    this.toStringCache = null;
    super.setLength(paramInt);
  }
  
  public char charAt(int paramInt) {
    if (paramInt < 0 || paramInt >= this.count)
      throw new StringIndexOutOfBoundsException(paramInt); 
    return this.value[paramInt];
  }
  
  public int codePointAt(int paramInt) { return super.codePointAt(paramInt); }
  
  public int codePointBefore(int paramInt) { return super.codePointBefore(paramInt); }
  
  public int codePointCount(int paramInt1, int paramInt2) { return super.codePointCount(paramInt1, paramInt2); }
  
  public int offsetByCodePoints(int paramInt1, int paramInt2) { return super.offsetByCodePoints(paramInt1, paramInt2); }
  
  public void getChars(int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3) { super.getChars(paramInt1, paramInt2, paramArrayOfChar, paramInt3); }
  
  public void setCharAt(int paramInt, char paramChar) {
    if (paramInt < 0 || paramInt >= this.count)
      throw new StringIndexOutOfBoundsException(paramInt); 
    this.toStringCache = null;
    this.value[paramInt] = paramChar;
  }
  
  public StringBuffer append(Object paramObject) {
    this.toStringCache = null;
    super.append(String.valueOf(paramObject));
    return this;
  }
  
  public StringBuffer append(String paramString) {
    this.toStringCache = null;
    super.append(paramString);
    return this;
  }
  
  public StringBuffer append(StringBuffer paramStringBuffer) {
    this.toStringCache = null;
    super.append(paramStringBuffer);
    return this;
  }
  
  StringBuffer append(AbstractStringBuilder paramAbstractStringBuilder) {
    this.toStringCache = null;
    super.append(paramAbstractStringBuilder);
    return this;
  }
  
  public StringBuffer append(CharSequence paramCharSequence) {
    this.toStringCache = null;
    super.append(paramCharSequence);
    return this;
  }
  
  public StringBuffer append(CharSequence paramCharSequence, int paramInt1, int paramInt2) {
    this.toStringCache = null;
    super.append(paramCharSequence, paramInt1, paramInt2);
    return this;
  }
  
  public StringBuffer append(char[] paramArrayOfChar) {
    this.toStringCache = null;
    super.append(paramArrayOfChar);
    return this;
  }
  
  public StringBuffer append(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    this.toStringCache = null;
    super.append(paramArrayOfChar, paramInt1, paramInt2);
    return this;
  }
  
  public StringBuffer append(boolean paramBoolean) {
    this.toStringCache = null;
    super.append(paramBoolean);
    return this;
  }
  
  public StringBuffer append(char paramChar) {
    this.toStringCache = null;
    super.append(paramChar);
    return this;
  }
  
  public StringBuffer append(int paramInt) {
    this.toStringCache = null;
    super.append(paramInt);
    return this;
  }
  
  public StringBuffer appendCodePoint(int paramInt) {
    this.toStringCache = null;
    super.appendCodePoint(paramInt);
    return this;
  }
  
  public StringBuffer append(long paramLong) {
    this.toStringCache = null;
    super.append(paramLong);
    return this;
  }
  
  public StringBuffer append(float paramFloat) {
    this.toStringCache = null;
    super.append(paramFloat);
    return this;
  }
  
  public StringBuffer append(double paramDouble) {
    this.toStringCache = null;
    super.append(paramDouble);
    return this;
  }
  
  public StringBuffer delete(int paramInt1, int paramInt2) {
    this.toStringCache = null;
    super.delete(paramInt1, paramInt2);
    return this;
  }
  
  public StringBuffer deleteCharAt(int paramInt) {
    this.toStringCache = null;
    super.deleteCharAt(paramInt);
    return this;
  }
  
  public StringBuffer replace(int paramInt1, int paramInt2, String paramString) {
    this.toStringCache = null;
    super.replace(paramInt1, paramInt2, paramString);
    return this;
  }
  
  public String substring(int paramInt) { return substring(paramInt, this.count); }
  
  public CharSequence subSequence(int paramInt1, int paramInt2) { return super.substring(paramInt1, paramInt2); }
  
  public String substring(int paramInt1, int paramInt2) { return super.substring(paramInt1, paramInt2); }
  
  public StringBuffer insert(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3) {
    this.toStringCache = null;
    super.insert(paramInt1, paramArrayOfChar, paramInt2, paramInt3);
    return this;
  }
  
  public StringBuffer insert(int paramInt, Object paramObject) {
    this.toStringCache = null;
    super.insert(paramInt, String.valueOf(paramObject));
    return this;
  }
  
  public StringBuffer insert(int paramInt, String paramString) {
    this.toStringCache = null;
    super.insert(paramInt, paramString);
    return this;
  }
  
  public StringBuffer insert(int paramInt, char[] paramArrayOfChar) {
    this.toStringCache = null;
    super.insert(paramInt, paramArrayOfChar);
    return this;
  }
  
  public StringBuffer insert(int paramInt, CharSequence paramCharSequence) {
    super.insert(paramInt, paramCharSequence);
    return this;
  }
  
  public StringBuffer insert(int paramInt1, CharSequence paramCharSequence, int paramInt2, int paramInt3) {
    this.toStringCache = null;
    super.insert(paramInt1, paramCharSequence, paramInt2, paramInt3);
    return this;
  }
  
  public StringBuffer insert(int paramInt, boolean paramBoolean) {
    super.insert(paramInt, paramBoolean);
    return this;
  }
  
  public StringBuffer insert(int paramInt, char paramChar) {
    this.toStringCache = null;
    super.insert(paramInt, paramChar);
    return this;
  }
  
  public StringBuffer insert(int paramInt1, int paramInt2) {
    super.insert(paramInt1, paramInt2);
    return this;
  }
  
  public StringBuffer insert(int paramInt, long paramLong) {
    super.insert(paramInt, paramLong);
    return this;
  }
  
  public StringBuffer insert(int paramInt, float paramFloat) {
    super.insert(paramInt, paramFloat);
    return this;
  }
  
  public StringBuffer insert(int paramInt, double paramDouble) {
    super.insert(paramInt, paramDouble);
    return this;
  }
  
  public int indexOf(String paramString) { return super.indexOf(paramString); }
  
  public int indexOf(String paramString, int paramInt) { return super.indexOf(paramString, paramInt); }
  
  public int lastIndexOf(String paramString) { return lastIndexOf(paramString, this.count); }
  
  public int lastIndexOf(String paramString, int paramInt) { return super.lastIndexOf(paramString, paramInt); }
  
  public StringBuffer reverse() {
    this.toStringCache = null;
    super.reverse();
    return this;
  }
  
  public String toString() {
    if (this.toStringCache == null)
      this.toStringCache = Arrays.copyOfRange(this.value, 0, this.count); 
    return new String(this.toStringCache, true);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("value", this.value);
    putField.put("count", this.count);
    putField.put("shared", false);
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    this.value = (char[])getField.get("value", null);
    this.count = getField.get("count", 0);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\StringBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */