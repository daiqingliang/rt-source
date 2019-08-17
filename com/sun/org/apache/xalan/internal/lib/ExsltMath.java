package com.sun.org.apache.xalan.internal.lib;

import com.sun.org.apache.xpath.internal.NodeSet;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ExsltMath extends ExsltBase {
  private static String PI = "3.1415926535897932384626433832795028841971693993751";
  
  private static String E = "2.71828182845904523536028747135266249775724709369996";
  
  private static String SQRRT2 = "1.41421356237309504880168872420969807856967187537694";
  
  private static String LN2 = "0.69314718055994530941723212145817656807550013436025";
  
  private static String LN10 = "2.302585092994046";
  
  private static String LOG2E = "1.4426950408889633";
  
  private static String SQRT1_2 = "0.7071067811865476";
  
  public static double max(NodeList paramNodeList) {
    if (paramNodeList == null || paramNodeList.getLength() == 0)
      return NaND; 
    double d = -1.7976931348623157E308D;
    for (byte b = 0; b < paramNodeList.getLength(); b++) {
      Node node = paramNodeList.item(b);
      double d1 = toNumber(node);
      if (Double.isNaN(d1))
        return NaND; 
      if (d1 > d)
        d = d1; 
    } 
    return d;
  }
  
  public static double min(NodeList paramNodeList) {
    if (paramNodeList == null || paramNodeList.getLength() == 0)
      return NaND; 
    double d = Double.MAX_VALUE;
    for (byte b = 0; b < paramNodeList.getLength(); b++) {
      Node node = paramNodeList.item(b);
      double d1 = toNumber(node);
      if (Double.isNaN(d1))
        return NaND; 
      if (d1 < d)
        d = d1; 
    } 
    return d;
  }
  
  public static NodeList highest(NodeList paramNodeList) {
    double d = max(paramNodeList);
    NodeSet nodeSet = new NodeSet();
    nodeSet.setShouldCacheNodes(true);
    if (Double.isNaN(d))
      return nodeSet; 
    for (byte b = 0; b < paramNodeList.getLength(); b++) {
      Node node = paramNodeList.item(b);
      double d1 = toNumber(node);
      if (d1 == d)
        nodeSet.addElement(node); 
    } 
    return nodeSet;
  }
  
  public static NodeList lowest(NodeList paramNodeList) {
    double d = min(paramNodeList);
    NodeSet nodeSet = new NodeSet();
    nodeSet.setShouldCacheNodes(true);
    if (Double.isNaN(d))
      return nodeSet; 
    for (byte b = 0; b < paramNodeList.getLength(); b++) {
      Node node = paramNodeList.item(b);
      double d1 = toNumber(node);
      if (d1 == d)
        nodeSet.addElement(node); 
    } 
    return nodeSet;
  }
  
  public static double abs(double paramDouble) { return Math.abs(paramDouble); }
  
  public static double acos(double paramDouble) { return Math.acos(paramDouble); }
  
  public static double asin(double paramDouble) { return Math.asin(paramDouble); }
  
  public static double atan(double paramDouble) { return Math.atan(paramDouble); }
  
  public static double atan2(double paramDouble1, double paramDouble2) { return Math.atan2(paramDouble1, paramDouble2); }
  
  public static double cos(double paramDouble) { return Math.cos(paramDouble); }
  
  public static double exp(double paramDouble) { return Math.exp(paramDouble); }
  
  public static double log(double paramDouble) { return Math.log(paramDouble); }
  
  public static double power(double paramDouble1, double paramDouble2) { return Math.pow(paramDouble1, paramDouble2); }
  
  public static double random() { return Math.random(); }
  
  public static double sin(double paramDouble) { return Math.sin(paramDouble); }
  
  public static double sqrt(double paramDouble) { return Math.sqrt(paramDouble); }
  
  public static double tan(double paramDouble) { return Math.tan(paramDouble); }
  
  public static double constant(String paramString, double paramDouble) {
    String str = null;
    if (paramString.equals("PI")) {
      str = PI;
    } else if (paramString.equals("E")) {
      str = E;
    } else if (paramString.equals("SQRRT2")) {
      str = SQRRT2;
    } else if (paramString.equals("LN2")) {
      str = LN2;
    } else if (paramString.equals("LN10")) {
      str = LN10;
    } else if (paramString.equals("LOG2E")) {
      str = LOG2E;
    } else if (paramString.equals("SQRT1_2")) {
      str = SQRT1_2;
    } 
    if (str != null) {
      int i = (new Double(paramDouble)).intValue();
      if (i <= str.length())
        str = str.substring(0, i); 
      return Double.parseDouble(str);
    } 
    return NaND;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\lib\ExsltMath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */