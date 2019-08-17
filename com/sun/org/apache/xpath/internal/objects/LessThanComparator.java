package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.utils.XMLString;

class LessThanComparator extends Comparator {
  boolean compareStrings(XMLString paramXMLString1, XMLString paramXMLString2) { return (paramXMLString1.toDouble() < paramXMLString2.toDouble()); }
  
  boolean compareNumbers(double paramDouble1, double paramDouble2) { return (paramDouble1 < paramDouble2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\objects\LessThanComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */