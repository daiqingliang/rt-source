package javax.print.attribute;

import java.io.Serializable;
import java.util.Vector;

public abstract class SetOfIntegerSyntax implements Serializable, Cloneable {
  private static final long serialVersionUID = 3666874174847632203L;
  
  private int[][] members;
  
  protected SetOfIntegerSyntax(String paramString) { this.members = parse(paramString); }
  
  private static int[][] parse(String paramString) {
    Vector vector = new Vector();
    boolean bool = (paramString == null) ? 0 : paramString.length();
    byte b1 = 0;
    byte b2 = 0;
    int i = 0;
    int j = 0;
    while (b1 < bool) {
      int k;
      char c = paramString.charAt(b1++);
      switch (b2) {
        case false:
          if (Character.isWhitespace(c)) {
            b2 = 0;
            continue;
          } 
          if ((k = Character.digit(c, 10)) != -1) {
            i = k;
            b2 = 1;
            continue;
          } 
          throw new IllegalArgumentException();
        case true:
          if (Character.isWhitespace(c)) {
            b2 = 2;
            continue;
          } 
          if ((k = Character.digit(c, 10)) != -1) {
            i = 10 * i + k;
            b2 = 1;
            continue;
          } 
          if (c == '-' || c == ':') {
            b2 = 3;
            continue;
          } 
          if (c == ',') {
            accumulate(vector, i, i);
            b2 = 6;
            continue;
          } 
          throw new IllegalArgumentException();
        case true:
          if (Character.isWhitespace(c)) {
            b2 = 2;
            continue;
          } 
          if (c == '-' || c == ':') {
            b2 = 3;
            continue;
          } 
          if (c == ',') {
            accumulate(vector, i, i);
            b2 = 6;
            continue;
          } 
          throw new IllegalArgumentException();
        case true:
          if (Character.isWhitespace(c)) {
            b2 = 3;
            continue;
          } 
          if ((k = Character.digit(c, 10)) != -1) {
            j = k;
            b2 = 4;
            continue;
          } 
          throw new IllegalArgumentException();
        case true:
          if (Character.isWhitespace(c)) {
            b2 = 5;
            continue;
          } 
          if ((k = Character.digit(c, 10)) != -1) {
            j = 10 * j + k;
            b2 = 4;
            continue;
          } 
          if (c == ',') {
            accumulate(vector, i, j);
            b2 = 6;
            continue;
          } 
          throw new IllegalArgumentException();
        case true:
          if (Character.isWhitespace(c)) {
            b2 = 5;
            continue;
          } 
          if (c == ',') {
            accumulate(vector, i, j);
            b2 = 6;
            continue;
          } 
          throw new IllegalArgumentException();
        case true:
          if (Character.isWhitespace(c)) {
            b2 = 6;
            continue;
          } 
          if ((k = Character.digit(c, 10)) != -1) {
            i = k;
            b2 = 1;
            continue;
          } 
          throw new IllegalArgumentException();
      } 
    } 
    switch (b2) {
      case 1:
      case 2:
        accumulate(vector, i, i);
        break;
      case 4:
      case 5:
        accumulate(vector, i, j);
        break;
      case 3:
      case 6:
        throw new IllegalArgumentException();
    } 
    return canonicalArrayForm(vector);
  }
  
  private static void accumulate(Vector paramVector, int paramInt1, int paramInt2) {
    if (paramInt1 <= paramInt2) {
      paramVector.add(new int[] { paramInt1, paramInt2 });
      for (int i = paramVector.size() - 2; i >= 0; i--) {
        int[] arrayOfInt1 = (int[])paramVector.elementAt(i);
        int j = arrayOfInt1[0];
        int k = arrayOfInt1[1];
        int[] arrayOfInt2 = (int[])paramVector.elementAt(i + 1);
        int m = arrayOfInt2[0];
        int n = arrayOfInt2[1];
        if (Math.max(j, m) - Math.min(k, n) <= 1) {
          paramVector.setElementAt(new int[] { Math.min(j, m), Math.max(k, n) }i);
          paramVector.remove(i + 1);
        } else if (j > m) {
          paramVector.setElementAt(arrayOfInt2, i);
          paramVector.setElementAt(arrayOfInt1, i + 1);
        } else {
          break;
        } 
      } 
    } 
  }
  
  private static int[][] canonicalArrayForm(Vector paramVector) { return (int[][])paramVector.toArray(new int[paramVector.size()][]); }
  
  protected SetOfIntegerSyntax(int[][] paramArrayOfInt) { this.members = parse(paramArrayOfInt); }
  
  private static int[][] parse(int[][] paramArrayOfInt) {
    Vector vector = new Vector();
    boolean bool = (paramArrayOfInt == null) ? 0 : paramArrayOfInt.length;
    for (byte b = 0; b < bool; b++) {
      int j;
      int i;
      if (paramArrayOfInt[b].length == 1) {
        i = j = paramArrayOfInt[b][0];
      } else if (paramArrayOfInt[b].length == 2) {
        i = paramArrayOfInt[b][0];
        j = paramArrayOfInt[b][1];
      } else {
        throw new IllegalArgumentException();
      } 
      if (i <= j && i < 0)
        throw new IllegalArgumentException(); 
      accumulate(vector, i, j);
    } 
    return canonicalArrayForm(vector);
  }
  
  protected SetOfIntegerSyntax(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException(); 
    this.members = new int[][] { { paramInt, paramInt } };
  }
  
  protected SetOfIntegerSyntax(int paramInt1, int paramInt2) {
    if (paramInt1 <= paramInt2 && paramInt1 < 0)
      throw new IllegalArgumentException(); 
    new int[2][0] = paramInt1;
    new int[2][1] = paramInt2;
    new int[1][][0] = new int[2];
    this.members = (paramInt1 <= paramInt2) ? new int[1][] : new int[0][];
  }
  
  public int[][] getMembers() {
    int i = this.members.length;
    int[][] arrayOfInt = new int[i][];
    for (byte b = 0; b < i; b++) {
      new int[2][0] = this.members[b][0];
      new int[2][1] = this.members[b][1];
      arrayOfInt[b] = new int[2];
    } 
    return arrayOfInt;
  }
  
  public boolean contains(int paramInt) {
    int i = this.members.length;
    for (byte b = 0; b < i; b++) {
      if (paramInt < this.members[b][0])
        return false; 
      if (paramInt <= this.members[b][1])
        return true; 
    } 
    return false;
  }
  
  public boolean contains(IntegerSyntax paramIntegerSyntax) { return contains(paramIntegerSyntax.getValue()); }
  
  public int next(int paramInt) {
    int i = this.members.length;
    for (byte b = 0; b < i; b++) {
      if (paramInt < this.members[b][0])
        return this.members[b][0]; 
      if (paramInt < this.members[b][1])
        return paramInt + 1; 
    } 
    return -1;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject != null && paramObject instanceof SetOfIntegerSyntax) {
      int[][] arrayOfInt1 = this.members;
      int[][] arrayOfInt2 = ((SetOfIntegerSyntax)paramObject).members;
      int i = arrayOfInt1.length;
      int j = arrayOfInt2.length;
      if (i == j) {
        for (byte b = 0; b < i; b++) {
          if (arrayOfInt1[b][0] != arrayOfInt2[b][0] || arrayOfInt1[b][1] != arrayOfInt2[b][1])
            return false; 
        } 
        return true;
      } 
      return false;
    } 
    return false;
  }
  
  public int hashCode() {
    int i = 0;
    int j = this.members.length;
    for (byte b = 0; b < j; b++)
      i += this.members[b][0] + this.members[b][1]; 
    return i;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    int i = this.members.length;
    for (byte b = 0; b < i; b++) {
      if (b)
        stringBuffer.append(','); 
      stringBuffer.append(this.members[b][0]);
      if (this.members[b][0] != this.members[b][1]) {
        stringBuffer.append('-');
        stringBuffer.append(this.members[b][1]);
      } 
    } 
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\SetOfIntegerSyntax.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */