package com.sun.org.apache.xml.internal.serialize;

import com.sun.org.apache.xerces.internal.util.EncodingMap;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Method;

public class EncodingInfo {
  private Object[] fArgsForMethod = null;
  
  String ianaName;
  
  String javaName;
  
  int lastPrintable;
  
  Object fCharsetEncoder = null;
  
  Object fCharToByteConverter = null;
  
  boolean fHaveTriedCToB = false;
  
  boolean fHaveTriedCharsetEncoder = false;
  
  public EncodingInfo(String paramString1, String paramString2, int paramInt) {
    this.ianaName = paramString1;
    this.javaName = EncodingMap.getIANA2JavaMapping(paramString1);
    this.lastPrintable = paramInt;
  }
  
  public String getIANAName() { return this.ianaName; }
  
  public Writer getWriter(OutputStream paramOutputStream) throws UnsupportedEncodingException {
    if (this.javaName != null)
      return new OutputStreamWriter(paramOutputStream, this.javaName); 
    this.javaName = EncodingMap.getIANA2JavaMapping(this.ianaName);
    return (this.javaName == null) ? new OutputStreamWriter(paramOutputStream, "UTF8") : new OutputStreamWriter(paramOutputStream, this.javaName);
  }
  
  public boolean isPrintable(char paramChar) { return (paramChar <= this.lastPrintable) ? true : isPrintable0(paramChar); }
  
  private boolean isPrintable0(char paramChar) {
    if (this.fCharsetEncoder == null && fgNIOCharsetAvailable && !this.fHaveTriedCharsetEncoder) {
      if (this.fArgsForMethod == null)
        this.fArgsForMethod = new Object[1]; 
      try {
        this.fArgsForMethod[0] = this.javaName;
        Object object = fgCharsetForNameMethod.invoke(null, this.fArgsForMethod);
        if (((Boolean)fgCharsetCanEncodeMethod.invoke(object, (Object[])null)).booleanValue()) {
          this.fCharsetEncoder = fgCharsetNewEncoderMethod.invoke(object, (Object[])null);
        } else {
          this.fHaveTriedCharsetEncoder = true;
        } 
      } catch (Exception exception) {
        this.fHaveTriedCharsetEncoder = true;
      } 
    } 
    if (this.fCharsetEncoder != null)
      try {
        this.fArgsForMethod[0] = new Character(paramChar);
        return ((Boolean)fgCharsetEncoderCanEncodeMethod.invoke(this.fCharsetEncoder, this.fArgsForMethod)).booleanValue();
      } catch (Exception exception) {
        this.fCharsetEncoder = null;
        this.fHaveTriedCharsetEncoder = false;
      }  
    if (this.fCharToByteConverter == null) {
      if (this.fHaveTriedCToB || !fgConvertersAvailable)
        return false; 
      if (this.fArgsForMethod == null)
        this.fArgsForMethod = new Object[1]; 
      try {
        this.fArgsForMethod[0] = this.javaName;
        this.fCharToByteConverter = fgGetConverterMethod.invoke(null, this.fArgsForMethod);
      } catch (Exception exception) {
        this.fHaveTriedCToB = true;
        return false;
      } 
    } 
    try {
      this.fArgsForMethod[0] = new Character(paramChar);
      return ((Boolean)fgCanConvertMethod.invoke(this.fCharToByteConverter, this.fArgsForMethod)).booleanValue();
    } catch (Exception exception) {
      this.fCharToByteConverter = null;
      this.fHaveTriedCToB = false;
      return false;
    } 
  }
  
  public static void testJavaEncodingName(String paramString) throws UnsupportedEncodingException {
    byte[] arrayOfByte = { 118, 97, 108, 105, 100 };
    String str = new String(arrayOfByte, paramString);
  }
  
  static class CharToByteConverterMethods {
    private static Method fgGetConverterMethod = null;
    
    private static Method fgCanConvertMethod = null;
    
    private static boolean fgConvertersAvailable = false;
    
    static  {
      try {
        Class clazz = Class.forName("sun.io.CharToByteConverter");
        fgGetConverterMethod = clazz.getMethod("getConverter", new Class[] { String.class });
        fgCanConvertMethod = clazz.getMethod("canConvert", new Class[] { char.class });
        fgConvertersAvailable = true;
      } catch (Exception exception) {
        fgGetConverterMethod = null;
        fgCanConvertMethod = null;
        fgConvertersAvailable = false;
      } 
    }
  }
  
  static class CharsetMethods {
    private static Method fgCharsetForNameMethod = null;
    
    private static Method fgCharsetCanEncodeMethod = null;
    
    private static Method fgCharsetNewEncoderMethod = null;
    
    private static Method fgCharsetEncoderCanEncodeMethod = null;
    
    private static boolean fgNIOCharsetAvailable = false;
    
    static  {
      try {
        Class clazz1;
        Class clazz2 = (clazz1 = Class.forName("java.nio.charset.Charset")).forName("java.nio.charset.CharsetEncoder");
        fgCharsetForNameMethod = clazz1.getMethod("forName", new Class[] { String.class });
        fgCharsetCanEncodeMethod = clazz1.getMethod("canEncode", new Class[0]);
        fgCharsetNewEncoderMethod = clazz1.getMethod("newEncoder", new Class[0]);
        fgCharsetEncoderCanEncodeMethod = clazz2.getMethod("canEncode", new Class[] { char.class });
        fgNIOCharsetAvailable = true;
      } catch (Exception exception) {
        fgCharsetForNameMethod = null;
        fgCharsetCanEncodeMethod = null;
        fgCharsetEncoderCanEncodeMethod = null;
        fgCharsetNewEncoderMethod = null;
        fgNIOCharsetAvailable = false;
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serialize\EncodingInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */