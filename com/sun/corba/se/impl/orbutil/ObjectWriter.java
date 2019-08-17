package com.sun.corba.se.impl.orbutil;

import java.util.Arrays;

public abstract class ObjectWriter {
  protected StringBuffer result = new StringBuffer();
  
  public static ObjectWriter make(boolean paramBoolean, int paramInt1, int paramInt2) { return paramBoolean ? new IndentingObjectWriter(paramInt1, paramInt2) : new SimpleObjectWriter(null); }
  
  public abstract void startObject(Object paramObject);
  
  public abstract void startElement();
  
  public abstract void endElement();
  
  public abstract void endObject(String paramString);
  
  public abstract void endObject();
  
  public String toString() { return this.result.toString(); }
  
  public void append(boolean paramBoolean) { this.result.append(paramBoolean); }
  
  public void append(char paramChar) { this.result.append(paramChar); }
  
  public void append(short paramShort) { this.result.append(paramShort); }
  
  public void append(int paramInt) { this.result.append(paramInt); }
  
  public void append(long paramLong) { this.result.append(paramLong); }
  
  public void append(float paramFloat) { this.result.append(paramFloat); }
  
  public void append(double paramDouble) { this.result.append(paramDouble); }
  
  public void append(String paramString) { this.result.append(paramString); }
  
  protected void appendObjectHeader(Object paramObject) {
    this.result.append(paramObject.getClass().getName());
    this.result.append("<");
    this.result.append(System.identityHashCode(paramObject));
    this.result.append(">");
    Class clazz = paramObject.getClass().getComponentType();
    if (clazz != null) {
      this.result.append("[");
      if (clazz == boolean.class) {
        boolean[] arrayOfBoolean = (boolean[])paramObject;
        this.result.append(arrayOfBoolean.length);
        this.result.append("]");
      } else if (clazz == byte.class) {
        byte[] arrayOfByte = (byte[])paramObject;
        this.result.append(arrayOfByte.length);
        this.result.append("]");
      } else if (clazz == short.class) {
        short[] arrayOfShort = (short[])paramObject;
        this.result.append(arrayOfShort.length);
        this.result.append("]");
      } else if (clazz == int.class) {
        int[] arrayOfInt = (int[])paramObject;
        this.result.append(arrayOfInt.length);
        this.result.append("]");
      } else if (clazz == long.class) {
        long[] arrayOfLong = (long[])paramObject;
        this.result.append(arrayOfLong.length);
        this.result.append("]");
      } else if (clazz == char.class) {
        char[] arrayOfChar = (char[])paramObject;
        this.result.append(arrayOfChar.length);
        this.result.append("]");
      } else if (clazz == float.class) {
        float[] arrayOfFloat = (float[])paramObject;
        this.result.append(arrayOfFloat.length);
        this.result.append("]");
      } else if (clazz == double.class) {
        double[] arrayOfDouble = (double[])paramObject;
        this.result.append(arrayOfDouble.length);
        this.result.append("]");
      } else {
        Object[] arrayOfObject = (Object[])paramObject;
        this.result.append(arrayOfObject.length);
        this.result.append("]");
      } 
    } 
    this.result.append("(");
  }
  
  private static class IndentingObjectWriter extends ObjectWriter {
    private int level;
    
    private int increment;
    
    public IndentingObjectWriter(int param1Int1, int param1Int2) {
      this.level = param1Int1;
      this.increment = param1Int2;
      startLine();
    }
    
    private void startLine() {
      char[] arrayOfChar = new char[this.level * this.increment];
      Arrays.fill(arrayOfChar, ' ');
      this.result.append(arrayOfChar);
    }
    
    public void startObject(Object param1Object) {
      appendObjectHeader(param1Object);
      this.level++;
    }
    
    public void startElement() {
      this.result.append("\n");
      startLine();
    }
    
    public void endElement() {}
    
    public void endObject(String param1String) {
      this.level--;
      this.result.append(param1String);
      this.result.append(")");
    }
    
    public void endObject() {
      this.level--;
      this.result.append("\n");
      startLine();
      this.result.append(")");
    }
  }
  
  private static class SimpleObjectWriter extends ObjectWriter {
    private SimpleObjectWriter() {}
    
    public void startObject(Object param1Object) {
      appendObjectHeader(param1Object);
      this.result.append(" ");
    }
    
    public void startElement() { this.result.append(" "); }
    
    public void endObject(String param1String) {
      this.result.append(param1String);
      this.result.append(")");
    }
    
    public void endElement() {}
    
    public void endObject() { this.result.append(")"); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\ObjectWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */