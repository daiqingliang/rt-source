package java.lang.invoke;

import java.lang.invoke.LambdaForm;
import java.lang.invoke.LambdaForm.Name;
import java.lang.invoke.LambdaFormBuffer;
import java.util.ArrayList;
import java.util.Arrays;

final class LambdaFormBuffer {
  private int arity;
  
  private int length;
  
  private LambdaForm.Name[] names;
  
  private LambdaForm.Name[] originalNames;
  
  private byte flags;
  
  private int firstChange;
  
  private LambdaForm.Name resultName;
  
  private String debugName;
  
  private ArrayList<LambdaForm.Name> dups;
  
  private static final int F_TRANS = 16;
  
  private static final int F_OWNED = 3;
  
  LambdaFormBuffer(LambdaForm paramLambdaForm) {
    this.arity = paramLambdaForm.arity;
    setNames(paramLambdaForm.names);
    int i = paramLambdaForm.result;
    if (i == -2)
      i = this.length - 1; 
    if (i >= 0 && (paramLambdaForm.names[i]).type != LambdaForm.BasicType.V_TYPE)
      this.resultName = paramLambdaForm.names[i]; 
    this.debugName = paramLambdaForm.debugName;
    assert paramLambdaForm.nameRefsAreLegal();
  }
  
  private LambdaForm lambdaForm() {
    assert !inTrans();
    return new LambdaForm(this.debugName, this.arity, nameArray(), resultIndex());
  }
  
  LambdaForm.Name name(int paramInt) {
    assert paramInt < this.length;
    return this.names[paramInt];
  }
  
  LambdaForm.Name[] nameArray() { return (Name[])Arrays.copyOf(this.names, this.length); }
  
  int resultIndex() {
    if (this.resultName == null)
      return -1; 
    int i = indexOf(this.resultName, this.names);
    assert i >= 0;
    return i;
  }
  
  void setNames(LambdaForm.Name[] paramArrayOfName) {
    this.names = this.originalNames = paramArrayOfName;
    this.length = paramArrayOfName.length;
    this.flags = 0;
  }
  
  private boolean verifyArity() {
    int i;
    for (i = 0; i < this.arity && i < this.firstChange; i++)
      assert this.names[i].isParam() : "#" + i + "=" + this.names[i]; 
    for (i = this.arity; i < this.length; i++)
      assert !this.names[i].isParam() : "#" + i + "=" + this.names[i]; 
    for (i = this.length; i < this.names.length; i++)
      assert this.names[i] == null : "#" + i + "=" + this.names[i]; 
    if (this.resultName != null) {
      i = indexOf(this.resultName, this.names);
      assert i >= 0 : "not found: " + this.resultName.exprString() + Arrays.asList(this.names);
      assert this.names[i] == this.resultName;
    } 
    return true;
  }
  
  private boolean verifyFirstChange() {
    assert inTrans();
    for (byte b = 0; b < this.length; b++) {
      if (this.names[b] != this.originalNames[b]) {
        assert this.firstChange == b : Arrays.asList(new Object[] { Integer.valueOf(this.firstChange), Integer.valueOf(b), this.originalNames[b].exprString(), Arrays.asList(this.names) });
        return true;
      } 
    } 
    assert this.firstChange == this.length : Arrays.asList(new Object[] { Integer.valueOf(this.firstChange), Arrays.asList(this.names) });
    return true;
  }
  
  private static int indexOf(LambdaForm.NamedFunction paramNamedFunction, LambdaForm.NamedFunction[] paramArrayOfNamedFunction) {
    for (byte b = 0; b < paramArrayOfNamedFunction.length; b++) {
      if (paramArrayOfNamedFunction[b] == paramNamedFunction)
        return b; 
    } 
    return -1;
  }
  
  private static int indexOf(LambdaForm.Name paramName, LambdaForm.Name[] paramArrayOfName) {
    for (byte b = 0; b < paramArrayOfName.length; b++) {
      if (paramArrayOfName[b] == paramName)
        return b; 
    } 
    return -1;
  }
  
  boolean inTrans() { return ((this.flags & 0x10) != 0); }
  
  int ownedCount() { return this.flags & 0x3; }
  
  void growNames(int paramInt1, int paramInt2) {
    int i = this.length;
    int j = i + paramInt2;
    int k = ownedCount();
    if (k == 0 || j > this.names.length) {
      this.names = (Name[])Arrays.copyOf(this.names, (this.names.length + paramInt2) * 5 / 4);
      if (k == 0) {
        this.flags = (byte)(this.flags + 1);
        assert ownedCount() == ++k;
      } 
    } 
    if (this.originalNames != null && this.originalNames.length < this.names.length) {
      this.originalNames = (Name[])Arrays.copyOf(this.originalNames, this.names.length);
      if (k == 1) {
        this.flags = (byte)(this.flags + 1);
        assert ownedCount() == ++k;
      } 
    } 
    if (paramInt2 == 0)
      return; 
    int m = paramInt1 + paramInt2;
    int n = i - paramInt1;
    System.arraycopy(this.names, paramInt1, this.names, m, n);
    Arrays.fill(this.names, paramInt1, m, null);
    if (this.originalNames != null) {
      System.arraycopy(this.originalNames, paramInt1, this.originalNames, m, n);
      Arrays.fill(this.originalNames, paramInt1, m, null);
    } 
    this.length = j;
    if (this.firstChange >= paramInt1)
      this.firstChange += paramInt2; 
  }
  
  int lastIndexOf(LambdaForm.Name paramName) {
    byte b1 = -1;
    for (byte b2 = 0; b2 < this.length; b2++) {
      if (this.names[b2] == paramName)
        b1 = b2; 
    } 
    return b1;
  }
  
  private void noteDuplicate(int paramInt1, int paramInt2) {
    LambdaForm.Name name = this.names[paramInt1];
    assert name == this.names[paramInt2];
    assert this.originalNames[paramInt1] != null;
    assert this.originalNames[paramInt2] == null || this.originalNames[paramInt2] == name;
    if (this.dups == null)
      this.dups = new ArrayList(); 
    this.dups.add(name);
  }
  
  private void clearDuplicatesAndNulls() {
    if (this.dups != null) {
      assert ownedCount() >= 1;
      for (LambdaForm.Name name : this.dups) {
        for (int k = this.firstChange; k < this.length; k++) {
          if (this.names[k] == name && this.originalNames[k] != name) {
            this.names[k] = null;
            assert Arrays.asList(this.names).contains(name);
            break;
          } 
        } 
      } 
      this.dups.clear();
    } 
    int i = this.length;
    for (int j = this.firstChange; j < this.length; j++) {
      if (this.names[j] == null) {
        System.arraycopy(this.names, j + 1, this.names, j, --this.length - j);
        j--;
      } 
    } 
    if (this.length < i)
      Arrays.fill(this.names, this.length, i, null); 
    assert !Arrays.asList(this.names).subList(0, this.length).contains(null);
  }
  
  void startEdit() {
    assert verifyArity();
    int i = ownedCount();
    assert !inTrans();
    this.flags = (byte)(this.flags | 0x10);
    LambdaForm.Name[] arrayOfName1 = this.names;
    LambdaForm.Name[] arrayOfName2 = (i == 2) ? this.originalNames : null;
    assert arrayOfName2 != arrayOfName1;
    if (arrayOfName2 != null && arrayOfName2.length >= this.length) {
      this.names = copyNamesInto(arrayOfName2);
    } else {
      this.names = (Name[])Arrays.copyOf(arrayOfName1, Math.max(this.length + 2, arrayOfName1.length));
      if (i < 2)
        this.flags = (byte)(this.flags + 1); 
      assert ownedCount() == i + 1;
    } 
    this.originalNames = arrayOfName1;
    assert this.originalNames != this.names;
    this.firstChange = this.length;
    assert inTrans();
  }
  
  private void changeName(int paramInt, LambdaForm.Name paramName) {
    assert inTrans();
    assert paramInt < this.length;
    LambdaForm.Name name = this.names[paramInt];
    assert name == this.originalNames[paramInt];
    assert verifyFirstChange();
    if (ownedCount() == 0)
      growNames(0, 0); 
    this.names[paramInt] = paramName;
    if (this.firstChange > paramInt)
      this.firstChange = paramInt; 
    if (this.resultName != null && this.resultName == name)
      this.resultName = paramName; 
  }
  
  void setResult(LambdaForm.Name paramName) {
    assert paramName == null || lastIndexOf(paramName) >= 0;
    this.resultName = paramName;
  }
  
  LambdaForm endEdit() {
    assert verifyFirstChange();
    for (int i = Math.max(this.firstChange, this.arity); i < this.length; i++) {
      LambdaForm.Name name = this.names[i];
      if (name != null) {
        LambdaForm.Name name1 = name.replaceNames(this.originalNames, this.names, this.firstChange, i);
        if (name1 != name) {
          this.names[i] = name1;
          if (this.resultName == name)
            this.resultName = name1; 
        } 
      } 
    } 
    assert inTrans();
    this.flags = (byte)(this.flags & 0xFFFFFFEF);
    clearDuplicatesAndNulls();
    this.originalNames = null;
    if (this.firstChange < this.arity) {
      LambdaForm.Name[] arrayOfName = new LambdaForm.Name[this.arity - this.firstChange];
      int j = this.firstChange;
      int k = 0;
      for (int m = this.firstChange; m < this.arity; m++) {
        LambdaForm.Name name = this.names[m];
        if (name.isParam()) {
          this.names[j++] = name;
        } else {
          arrayOfName[k++] = name;
        } 
      } 
      assert k == this.arity - j;
      System.arraycopy(arrayOfName, 0, this.names, j, k);
      this.arity -= k;
    } 
    assert verifyArity();
    return lambdaForm();
  }
  
  private LambdaForm.Name[] copyNamesInto(LambdaForm.Name[] paramArrayOfName) {
    System.arraycopy(this.names, 0, paramArrayOfName, 0, this.length);
    Arrays.fill(paramArrayOfName, this.length, paramArrayOfName.length, null);
    return paramArrayOfName;
  }
  
  LambdaFormBuffer replaceFunctions(LambdaForm.NamedFunction[] paramArrayOfNamedFunction1, LambdaForm.NamedFunction[] paramArrayOfNamedFunction2, Object... paramVarArgs) {
    assert inTrans();
    if (paramArrayOfNamedFunction1.length == 0)
      return this; 
    for (int i = this.arity; i < this.length; i++) {
      LambdaForm.Name name = this.names[i];
      int j = indexOf(name.function, paramArrayOfNamedFunction1);
      if (j >= 0 && Arrays.equals(name.arguments, paramVarArgs))
        changeName(i, new LambdaForm.Name(paramArrayOfNamedFunction2[j], name.arguments)); 
    } 
    return this;
  }
  
  private void replaceName(int paramInt, LambdaForm.Name paramName) {
    assert inTrans();
    assert verifyArity();
    assert paramInt < this.arity;
    LambdaForm.Name name = this.names[paramInt];
    assert name.isParam();
    assert name.type == paramName.type;
    changeName(paramInt, paramName);
  }
  
  LambdaFormBuffer renameParameter(int paramInt, LambdaForm.Name paramName) {
    assert paramName.isParam();
    replaceName(paramInt, paramName);
    return this;
  }
  
  LambdaFormBuffer replaceParameterByNewExpression(int paramInt, LambdaForm.Name paramName) {
    assert !paramName.isParam();
    assert lastIndexOf(paramName) < 0;
    replaceName(paramInt, paramName);
    return this;
  }
  
  LambdaFormBuffer replaceParameterByCopy(int paramInt1, int paramInt2) {
    assert paramInt1 != paramInt2;
    replaceName(paramInt1, this.names[paramInt2]);
    noteDuplicate(paramInt1, paramInt2);
    return this;
  }
  
  private void insertName(int paramInt, LambdaForm.Name paramName, boolean paramBoolean) {
    assert inTrans();
    assert verifyArity();
    assert false;
    throw new AssertionError();
  }
  
  LambdaFormBuffer insertExpression(int paramInt, LambdaForm.Name paramName) {
    assert !paramName.isParam();
    insertName(paramInt, paramName, false);
    return this;
  }
  
  LambdaFormBuffer insertParameter(int paramInt, LambdaForm.Name paramName) {
    assert paramName.isParam();
    insertName(paramInt, paramName, true);
    return this;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\invoke\LambdaFormBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */