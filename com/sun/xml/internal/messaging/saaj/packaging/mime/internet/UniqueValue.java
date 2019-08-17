package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

class UniqueValue {
  private static int part = 0;
  
  public static String getUniqueBoundaryValue() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("----=_Part_").append(part++).append("_").append(stringBuffer.hashCode()).append('.').append(System.currentTimeMillis());
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\UniqueValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */