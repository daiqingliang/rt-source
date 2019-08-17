package com.sun.corba.se.spi.orb;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.StringTokenizer;
import sun.corba.SharedSecrets;

public abstract class OperationFactory {
  private static Operation suffixActionImpl = new SuffixAction(null);
  
  private static Operation valueActionImpl = new ValueAction(null);
  
  private static Operation identityActionImpl = new IdentityAction(null);
  
  private static Operation booleanActionImpl = new BooleanAction(null);
  
  private static Operation integerActionImpl = new IntegerAction(null);
  
  private static Operation stringActionImpl = new StringAction(null);
  
  private static Operation classActionImpl = new ClassAction(null);
  
  private static Operation setFlagActionImpl = new SetFlagAction(null);
  
  private static Operation URLActionImpl = new URLAction(null);
  
  private static Operation convertIntegerToShortImpl = new ConvertIntegerToShort(null);
  
  private static String getString(Object paramObject) {
    if (paramObject instanceof String)
      return (String)paramObject; 
    throw new Error("String expected");
  }
  
  private static Object[] getObjectArray(Object paramObject) {
    if (paramObject instanceof Object[])
      return (Object[])paramObject; 
    throw new Error("Object[] expected");
  }
  
  private static StringPair getStringPair(Object paramObject) {
    if (paramObject instanceof StringPair)
      return (StringPair)paramObject; 
    throw new Error("StringPair expected");
  }
  
  public static Operation maskErrorAction(Operation paramOperation) { return new MaskErrorAction(paramOperation); }
  
  public static Operation indexAction(int paramInt) { return new IndexAction(paramInt); }
  
  public static Operation identityAction() { return identityActionImpl; }
  
  public static Operation suffixAction() { return suffixActionImpl; }
  
  public static Operation valueAction() { return valueActionImpl; }
  
  public static Operation booleanAction() { return booleanActionImpl; }
  
  public static Operation integerAction() { return integerActionImpl; }
  
  public static Operation stringAction() { return stringActionImpl; }
  
  public static Operation classAction() { return classActionImpl; }
  
  public static Operation setFlagAction() { return setFlagActionImpl; }
  
  public static Operation URLAction() { return URLActionImpl; }
  
  public static Operation integerRangeAction(int paramInt1, int paramInt2) { return new IntegerRangeAction(paramInt1, paramInt2); }
  
  public static Operation listAction(String paramString, Operation paramOperation) { return new ListAction(paramString, paramOperation); }
  
  public static Operation sequenceAction(String paramString, Operation[] paramArrayOfOperation) { return new SequenceAction(paramString, paramArrayOfOperation); }
  
  public static Operation compose(Operation paramOperation1, Operation paramOperation2) { return new ComposeAction(paramOperation1, paramOperation2); }
  
  public static Operation mapAction(Operation paramOperation) { return new MapAction(paramOperation); }
  
  public static Operation mapSequenceAction(Operation[] paramArrayOfOperation) { return new MapSequenceAction(paramArrayOfOperation); }
  
  public static Operation convertIntegerToShort() { return convertIntegerToShortImpl; }
  
  private static class BooleanAction extends OperationBase {
    private BooleanAction() { super(null); }
    
    public Object operate(Object param1Object) { return new Boolean(OperationFactory.getString(param1Object)); }
    
    public String toString() { return "booleanAction"; }
  }
  
  private static class ClassAction extends OperationBase {
    private ClassAction() { super(null); }
    
    public Object operate(Object param1Object) {
      String str = OperationFactory.getString(param1Object);
      try {
        return SharedSecrets.getJavaCorbaAccess().loadClass(str);
      } catch (Exception exception) {
        ORBUtilSystemException oRBUtilSystemException = ORBUtilSystemException.get("orb.lifecycle");
        throw oRBUtilSystemException.couldNotLoadClass(exception, str);
      } 
    }
    
    public String toString() { return "classAction"; }
  }
  
  private static class ComposeAction extends OperationBase {
    private Operation op1;
    
    private Operation op2;
    
    ComposeAction(Operation param1Operation1, Operation param1Operation2) {
      super(null);
      this.op1 = param1Operation1;
      this.op2 = param1Operation2;
    }
    
    public Object operate(Object param1Object) { return this.op2.operate(this.op1.operate(param1Object)); }
    
    public String toString() { return "composition(" + this.op1 + "," + this.op2 + ")"; }
  }
  
  private static class ConvertIntegerToShort extends OperationBase {
    private ConvertIntegerToShort() { super(null); }
    
    public Object operate(Object param1Object) {
      Integer integer = (Integer)param1Object;
      return new Short(integer.shortValue());
    }
    
    public String toString() { return "ConvertIntegerToShort"; }
  }
  
  private static class IdentityAction extends OperationBase {
    private IdentityAction() { super(null); }
    
    public Object operate(Object param1Object) { return param1Object; }
    
    public String toString() { return "identityAction"; }
  }
  
  private static class IndexAction extends OperationBase {
    private int index;
    
    public IndexAction(int param1Int) {
      super(null);
      this.index = param1Int;
    }
    
    public Object operate(Object param1Object) { return OperationFactory.getObjectArray(param1Object)[this.index]; }
    
    public String toString() { return "indexAction(" + this.index + ")"; }
  }
  
  private static class IntegerAction extends OperationBase {
    private IntegerAction() { super(null); }
    
    public Object operate(Object param1Object) { return new Integer(OperationFactory.getString(param1Object)); }
    
    public String toString() { return "integerAction"; }
  }
  
  private static class IntegerRangeAction extends OperationBase {
    private int min;
    
    private int max;
    
    IntegerRangeAction(int param1Int1, int param1Int2) {
      super(null);
      this.min = param1Int1;
      this.max = param1Int2;
    }
    
    public Object operate(Object param1Object) {
      int i = Integer.parseInt(OperationFactory.getString(param1Object));
      if (i >= this.min && i <= this.max)
        return new Integer(i); 
      throw new IllegalArgumentException("Property value " + i + " is not in the range " + this.min + " to " + this.max);
    }
    
    public String toString() { return "integerRangeAction(" + this.min + "," + this.max + ")"; }
  }
  
  private static class ListAction extends OperationBase {
    private String sep;
    
    private Operation act;
    
    ListAction(String param1String, Operation param1Operation) {
      super(null);
      this.sep = param1String;
      this.act = param1Operation;
    }
    
    public Object operate(Object param1Object) {
      StringTokenizer stringTokenizer = new StringTokenizer(OperationFactory.getString(param1Object), this.sep);
      int i = stringTokenizer.countTokens();
      Object object = null;
      byte b = 0;
      while (stringTokenizer.hasMoreTokens()) {
        String str = stringTokenizer.nextToken();
        Object object1 = this.act.operate(str);
        if (object == null)
          object = Array.newInstance(object1.getClass(), i); 
        Array.set(object, b++, object1);
      } 
      return object;
    }
    
    public String toString() { return "listAction(separator=\"" + this.sep + "\",action=" + this.act + ")"; }
  }
  
  private static class MapAction extends OperationBase {
    Operation op;
    
    MapAction(Operation param1Operation) {
      super(null);
      this.op = param1Operation;
    }
    
    public Object operate(Object param1Object) {
      Object[] arrayOfObject1 = (Object[])param1Object;
      Object[] arrayOfObject2 = new Object[arrayOfObject1.length];
      for (byte b = 0; b < arrayOfObject1.length; b++)
        arrayOfObject2[b] = this.op.operate(arrayOfObject1[b]); 
      return arrayOfObject2;
    }
    
    public String toString() { return "mapAction(" + this.op + ")"; }
  }
  
  private static class MapSequenceAction extends OperationBase {
    private Operation[] op;
    
    public MapSequenceAction(Operation[] param1ArrayOfOperation) {
      super(null);
      this.op = param1ArrayOfOperation;
    }
    
    public Object operate(Object param1Object) {
      Object[] arrayOfObject1 = (Object[])param1Object;
      Object[] arrayOfObject2 = new Object[arrayOfObject1.length];
      for (byte b = 0; b < arrayOfObject1.length; b++)
        arrayOfObject2[b] = this.op[b].operate(arrayOfObject1[b]); 
      return arrayOfObject2;
    }
    
    public String toString() { return "mapSequenceAction(" + Arrays.toString(this.op) + ")"; }
  }
  
  private static class MaskErrorAction extends OperationBase {
    private Operation op;
    
    public MaskErrorAction(Operation param1Operation) {
      super(null);
      this.op = param1Operation;
    }
    
    public Object operate(Object param1Object) {
      try {
        return this.op.operate(param1Object);
      } catch (Exception exception) {
        return null;
      } 
    }
    
    public String toString() { return "maskErrorAction(" + this.op + ")"; }
  }
  
  private static abstract class OperationBase implements Operation {
    private OperationBase() {}
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (!(param1Object instanceof OperationBase))
        return false; 
      OperationBase operationBase = (OperationBase)param1Object;
      return toString().equals(operationBase.toString());
    }
    
    public int hashCode() { return toString().hashCode(); }
  }
  
  private static class SequenceAction extends OperationBase {
    private String sep;
    
    private Operation[] actions;
    
    SequenceAction(String param1String, Operation[] param1ArrayOfOperation) {
      super(null);
      this.sep = param1String;
      this.actions = param1ArrayOfOperation;
    }
    
    public Object operate(Object param1Object) {
      StringTokenizer stringTokenizer = new StringTokenizer(OperationFactory.getString(param1Object), this.sep);
      int i = stringTokenizer.countTokens();
      if (i != this.actions.length)
        throw new Error("Number of tokens and number of actions do not match"); 
      byte b = 0;
      Object[] arrayOfObject = new Object[i];
      while (stringTokenizer.hasMoreTokens()) {
        Operation operation = this.actions[b];
        String str = stringTokenizer.nextToken();
        arrayOfObject[b++] = operation.operate(str);
      } 
      return arrayOfObject;
    }
    
    public String toString() { return "sequenceAction(separator=\"" + this.sep + "\",actions=" + Arrays.toString(this.actions) + ")"; }
  }
  
  private static class SetFlagAction extends OperationBase {
    private SetFlagAction() { super(null); }
    
    public Object operate(Object param1Object) { return Boolean.TRUE; }
    
    public String toString() { return "setFlagAction"; }
  }
  
  private static class StringAction extends OperationBase {
    private StringAction() { super(null); }
    
    public Object operate(Object param1Object) { return param1Object; }
    
    public String toString() { return "stringAction"; }
  }
  
  private static class SuffixAction extends OperationBase {
    private SuffixAction() { super(null); }
    
    public Object operate(Object param1Object) { return OperationFactory.getStringPair(param1Object).getFirst(); }
    
    public String toString() { return "suffixAction"; }
  }
  
  private static class URLAction extends OperationBase {
    private URLAction() { super(null); }
    
    public Object operate(Object param1Object) {
      String str = (String)param1Object;
      try {
        return new URL(str);
      } catch (MalformedURLException malformedURLException) {
        ORBUtilSystemException oRBUtilSystemException = ORBUtilSystemException.get("orb.lifecycle");
        throw oRBUtilSystemException.badUrl(malformedURLException, str);
      } 
    }
    
    public String toString() { return "URLAction"; }
  }
  
  private static class ValueAction extends OperationBase {
    private ValueAction() { super(null); }
    
    public Object operate(Object param1Object) { return OperationFactory.getStringPair(param1Object).getSecond(); }
    
    public String toString() { return "valueAction"; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orb\OperationFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */