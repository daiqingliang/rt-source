package java.lang;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class StringBuilder extends AbstractStringBuilder implements Serializable, CharSequence {
  static final long serialVersionUID = 4383685877147921099L;
  
  public StringBuilder() { super(16); }
  
  public StringBuilder(int paramInt) { super(paramInt); }
  
  public StringBuilder(String paramString) {
    super(paramString.length() + 16);
    append(paramString);
  }
  
  public StringBuilder(CharSequence paramCharSequence) {
    this(paramCharSequence.length() + 16);
    append(paramCharSequence);
  }
  
  public StringBuilder append(Object paramObject) { return append(String.valueOf(paramObject)); }
  
  public StringBuilder append(String paramString) {
    super.append(paramString);
    return this;
  }
  
  public StringBuilder append(StringBuffer paramStringBuffer) {
    super.append(paramStringBuffer);
    return this;
  }
  
  public StringBuilder append(CharSequence paramCharSequence) {
    super.append(paramCharSequence);
    return this;
  }
  
  public StringBuilder append(CharSequence paramCharSequence, int paramInt1, int paramInt2) {
    super.append(paramCharSequence, paramInt1, paramInt2);
    return this;
  }
  
  public StringBuilder append(char[] paramArrayOfChar) {
    super.append(paramArrayOfChar);
    return this;
  }
  
  public StringBuilder append(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    super.append(paramArrayOfChar, paramInt1, paramInt2);
    return this;
  }
  
  public StringBuilder append(boolean paramBoolean) {
    super.append(paramBoolean);
    return this;
  }
  
  public StringBuilder append(char paramChar) {
    super.append(paramChar);
    return this;
  }
  
  public StringBuilder append(int paramInt) {
    super.append(paramInt);
    return this;
  }
  
  public StringBuilder append(long paramLong) {
    super.append(paramLong);
    return this;
  }
  
  public StringBuilder append(float paramFloat) {
    super.append(paramFloat);
    return this;
  }
  
  public StringBuilder append(double paramDouble) {
    super.append(paramDouble);
    return this;
  }
  
  public StringBuilder appendCodePoint(int paramInt) {
    super.appendCodePoint(paramInt);
    return this;
  }
  
  public StringBuilder delete(int paramInt1, int paramInt2) {
    super.delete(paramInt1, paramInt2);
    return this;
  }
  
  public StringBuilder deleteCharAt(int paramInt) {
    super.deleteCharAt(paramInt);
    return this;
  }
  
  public StringBuilder replace(int paramInt1, int paramInt2, String paramString) {
    super.replace(paramInt1, paramInt2, paramString);
    return this;
  }
  
  public StringBuilder insert(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3) {
    super.insert(paramInt1, paramArrayOfChar, paramInt2, paramInt3);
    return this;
  }
  
  public StringBuilder insert(int paramInt, Object paramObject) {
    super.insert(paramInt, paramObject);
    return this;
  }
  
  public StringBuilder insert(int paramInt, String paramString) {
    super.insert(paramInt, paramString);
    return this;
  }
  
  public StringBuilder insert(int paramInt, char[] paramArrayOfChar) {
    super.insert(paramInt, paramArrayOfChar);
    return this;
  }
  
  public StringBuilder insert(int paramInt, CharSequence paramCharSequence) {
    super.insert(paramInt, paramCharSequence);
    return this;
  }
  
  public StringBuilder insert(int paramInt1, CharSequence paramCharSequence, int paramInt2, int paramInt3) {
    super.insert(paramInt1, paramCharSequence, paramInt2, paramInt3);
    return this;
  }
  
  public StringBuilder insert(int paramInt, boolean paramBoolean) {
    super.insert(paramInt, paramBoolean);
    return this;
  }
  
  public StringBuilder insert(int paramInt, char paramChar) {
    super.insert(paramInt, paramChar);
    return this;
  }
  
  public StringBuilder insert(int paramInt1, int paramInt2) {
    super.insert(paramInt1, paramInt2);
    return this;
  }
  
  public StringBuilder insert(int paramInt, long paramLong) {
    super.insert(paramInt, paramLong);
    return this;
  }
  
  public StringBuilder insert(int paramInt, float paramFloat) {
    super.insert(paramInt, paramFloat);
    return this;
  }
  
  public StringBuilder insert(int paramInt, double paramDouble) {
    super.insert(paramInt, paramDouble);
    return this;
  }
  
  public int indexOf(String paramString) { return super.indexOf(paramString); }
  
  public int indexOf(String paramString, int paramInt) { return super.indexOf(paramString, paramInt); }
  
  public int lastIndexOf(String paramString) { return super.lastIndexOf(paramString); }
  
  public int lastIndexOf(String paramString, int paramInt) { return super.lastIndexOf(paramString, paramInt); }
  
  public StringBuilder reverse() {
    super.reverse();
    return this;
  }
  
  public String toString() { return new String(this.value, 0, this.count); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(this.count);
    paramObjectOutputStream.writeObject(this.value);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.count = paramObjectInputStream.readInt();
    this.value = (char[])paramObjectInputStream.readObject();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\StringBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */