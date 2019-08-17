package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public final class CodeSetComponentInfo {
  private CodeSetComponent forCharData = JAVASOFT_DEFAULT_CODESETS.forCharData;
  
  private CodeSetComponent forWCharData = JAVASOFT_DEFAULT_CODESETS.forWCharData;
  
  public static final CodeSetComponentInfo JAVASOFT_DEFAULT_CODESETS;
  
  public static final CodeSetContext LOCAL_CODE_SETS;
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof CodeSetComponentInfo))
      return false; 
    CodeSetComponentInfo codeSetComponentInfo = (CodeSetComponentInfo)paramObject;
    return (this.forCharData.equals(codeSetComponentInfo.forCharData) && this.forWCharData.equals(codeSetComponentInfo.forWCharData));
  }
  
  public int hashCode() { return this.forCharData.hashCode() ^ this.forWCharData.hashCode(); }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer("CodeSetComponentInfo(");
    stringBuffer.append("char_data:");
    stringBuffer.append(this.forCharData.toString());
    stringBuffer.append(" wchar_data:");
    stringBuffer.append(this.forWCharData.toString());
    stringBuffer.append(")");
    return stringBuffer.toString();
  }
  
  public CodeSetComponentInfo() {}
  
  public CodeSetComponentInfo(CodeSetComponent paramCodeSetComponent1, CodeSetComponent paramCodeSetComponent2) {}
  
  public void read(MarshalInputStream paramMarshalInputStream) {
    this.forCharData = new CodeSetComponent();
    this.forCharData.read(paramMarshalInputStream);
    this.forWCharData = new CodeSetComponent();
    this.forWCharData.read(paramMarshalInputStream);
  }
  
  public void write(MarshalOutputStream paramMarshalOutputStream) {
    this.forCharData.write(paramMarshalOutputStream);
    this.forWCharData.write(paramMarshalOutputStream);
  }
  
  public CodeSetComponent getCharComponent() { return this.forCharData; }
  
  public CodeSetComponent getWCharComponent() { return this.forWCharData; }
  
  public static CodeSetComponent createFromString(String paramString) {
    ORBUtilSystemException oRBUtilSystemException = ORBUtilSystemException.get("rpc.encoding");
    if (paramString == null || paramString.length() == 0)
      throw oRBUtilSystemException.badCodeSetString(); 
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, ", ", false);
    int i = 0;
    int[] arrayOfInt = null;
    try {
      i = Integer.decode(stringTokenizer.nextToken()).intValue();
      if (OSFCodeSetRegistry.lookupEntry(i) == null)
        throw oRBUtilSystemException.unknownNativeCodeset(new Integer(i)); 
      ArrayList arrayList = new ArrayList(10);
      while (stringTokenizer.hasMoreTokens()) {
        Integer integer = Integer.decode(stringTokenizer.nextToken());
        if (OSFCodeSetRegistry.lookupEntry(integer.intValue()) == null)
          throw oRBUtilSystemException.unknownConversionCodeSet(integer); 
        arrayList.add(integer);
      } 
      arrayOfInt = new int[arrayList.size()];
      for (byte b = 0; b < arrayOfInt.length; b++)
        arrayOfInt[b] = ((Integer)arrayList.get(b)).intValue(); 
    } catch (NumberFormatException numberFormatException) {
      throw oRBUtilSystemException.invalidCodeSetNumber(numberFormatException);
    } catch (NoSuchElementException noSuchElementException) {
      throw oRBUtilSystemException.invalidCodeSetString(noSuchElementException, paramString);
    } 
    return new CodeSetComponent(i, arrayOfInt);
  }
  
  static  {
    CodeSetComponent codeSetComponent1 = new CodeSetComponent(OSFCodeSetRegistry.ISO_8859_1.getNumber(), new int[] { OSFCodeSetRegistry.UTF_8.getNumber(), OSFCodeSetRegistry.ISO_646.getNumber() });
    CodeSetComponent codeSetComponent2 = new CodeSetComponent(OSFCodeSetRegistry.UTF_16.getNumber(), new int[] { OSFCodeSetRegistry.UCS_2.getNumber() });
    JAVASOFT_DEFAULT_CODESETS = new CodeSetComponentInfo(codeSetComponent1, codeSetComponent2);
    LOCAL_CODE_SETS = new CodeSetContext(OSFCodeSetRegistry.ISO_8859_1.getNumber(), OSFCodeSetRegistry.UTF_16.getNumber());
  }
  
  public static final class CodeSetComponent {
    int nativeCodeSet;
    
    int[] conversionCodeSets;
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (!(param1Object instanceof CodeSetComponent))
        return false; 
      CodeSetComponent codeSetComponent = (CodeSetComponent)param1Object;
      return (this.nativeCodeSet == codeSetComponent.nativeCodeSet && Arrays.equals(this.conversionCodeSets, codeSetComponent.conversionCodeSets));
    }
    
    public int hashCode() {
      int i = this.nativeCodeSet;
      for (byte b = 0; b < this.conversionCodeSets.length; b++)
        i = 37 * i + this.conversionCodeSets[b]; 
      return i;
    }
    
    public CodeSetComponent() {}
    
    public CodeSetComponent(int param1Int, int[] param1ArrayOfInt) {
      this.nativeCodeSet = param1Int;
      if (param1ArrayOfInt == null) {
        this.conversionCodeSets = new int[0];
      } else {
        this.conversionCodeSets = param1ArrayOfInt;
      } 
    }
    
    public void read(MarshalInputStream param1MarshalInputStream) {
      this.nativeCodeSet = param1MarshalInputStream.read_ulong();
      int i = param1MarshalInputStream.read_long();
      this.conversionCodeSets = new int[i];
      param1MarshalInputStream.read_ulong_array(this.conversionCodeSets, 0, i);
    }
    
    public void write(MarshalOutputStream param1MarshalOutputStream) {
      param1MarshalOutputStream.write_ulong(this.nativeCodeSet);
      param1MarshalOutputStream.write_long(this.conversionCodeSets.length);
      param1MarshalOutputStream.write_ulong_array(this.conversionCodeSets, 0, this.conversionCodeSets.length);
    }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer("CodeSetComponent(");
      stringBuffer.append("native:");
      stringBuffer.append(Integer.toHexString(this.nativeCodeSet));
      stringBuffer.append(" conversion:");
      if (this.conversionCodeSets == null) {
        stringBuffer.append("null");
      } else {
        for (byte b = 0; b < this.conversionCodeSets.length; b++) {
          stringBuffer.append(Integer.toHexString(this.conversionCodeSets[b]));
          stringBuffer.append(' ');
        } 
      } 
      stringBuffer.append(")");
      return stringBuffer.toString();
    }
  }
  
  public static final class CodeSetContext {
    private int char_data;
    
    private int wchar_data;
    
    public CodeSetContext() {}
    
    public CodeSetContext(int param1Int1, int param1Int2) {
      this.char_data = param1Int1;
      this.wchar_data = param1Int2;
    }
    
    public void read(MarshalInputStream param1MarshalInputStream) {
      this.char_data = param1MarshalInputStream.read_ulong();
      this.wchar_data = param1MarshalInputStream.read_ulong();
    }
    
    public void write(MarshalOutputStream param1MarshalOutputStream) {
      param1MarshalOutputStream.write_ulong(this.char_data);
      param1MarshalOutputStream.write_ulong(this.wchar_data);
    }
    
    public int getCharCodeSet() { return this.char_data; }
    
    public int getWCharCodeSet() { return this.wchar_data; }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("CodeSetContext char set: ");
      stringBuffer.append(Integer.toHexString(this.char_data));
      stringBuffer.append(" wchar set: ");
      stringBuffer.append(Integer.toHexString(this.wchar_data));
      return stringBuffer.toString();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\CodeSetComponentInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */