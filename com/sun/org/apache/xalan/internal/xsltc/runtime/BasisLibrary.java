package com.sun.org.apache.xalan.internal.xsltc.runtime;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xalan.internal.xsltc.dom.AbsoluteIterator;
import com.sun.org.apache.xalan.internal.xsltc.dom.ArrayNodeListIterator;
import com.sun.org.apache.xalan.internal.xsltc.dom.DOMAdapter;
import com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM;
import com.sun.org.apache.xalan.internal.xsltc.dom.SingletonIterator;
import com.sun.org.apache.xalan.internal.xsltc.dom.StepIterator;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeProxy;
import com.sun.org.apache.xml.internal.serializer.NamespaceMappings;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class BasisLibrary {
  private static final String EMPTYSTRING = "";
  
  private static final ThreadLocal<StringBuilder> threadLocalStringBuilder = new ThreadLocal<StringBuilder>() {
      protected StringBuilder initialValue() { return new StringBuilder(); }
    };
  
  private static final ThreadLocal<StringBuffer> threadLocalStringBuffer = new ThreadLocal<StringBuffer>() {
      protected StringBuffer initialValue() { return new StringBuffer(); }
    };
  
  private static final int DOUBLE_FRACTION_DIGITS = 340;
  
  private static final double lowerBounds = 0.001D;
  
  private static final double upperBounds = 1.0E7D;
  
  private static DecimalFormat defaultFormatter;
  
  private static DecimalFormat xpathFormatter;
  
  private static String defaultPattern = "";
  
  private static FieldPosition _fieldPosition;
  
  private static char[] _characterArray;
  
  private static final ThreadLocal<AtomicInteger> threadLocalPrefixIndex;
  
  public static final String RUN_TIME_INTERNAL_ERR = "RUN_TIME_INTERNAL_ERR";
  
  public static final String RUN_TIME_COPY_ERR = "RUN_TIME_COPY_ERR";
  
  public static final String DATA_CONVERSION_ERR = "DATA_CONVERSION_ERR";
  
  public static final String EXTERNAL_FUNC_ERR = "EXTERNAL_FUNC_ERR";
  
  public static final String EQUALITY_EXPR_ERR = "EQUALITY_EXPR_ERR";
  
  public static final String INVALID_ARGUMENT_ERR = "INVALID_ARGUMENT_ERR";
  
  public static final String FORMAT_NUMBER_ERR = "FORMAT_NUMBER_ERR";
  
  public static final String ITERATOR_CLONE_ERR = "ITERATOR_CLONE_ERR";
  
  public static final String AXIS_SUPPORT_ERR = "AXIS_SUPPORT_ERR";
  
  public static final String TYPED_AXIS_SUPPORT_ERR = "TYPED_AXIS_SUPPORT_ERR";
  
  public static final String STRAY_ATTRIBUTE_ERR = "STRAY_ATTRIBUTE_ERR";
  
  public static final String STRAY_NAMESPACE_ERR = "STRAY_NAMESPACE_ERR";
  
  public static final String NAMESPACE_PREFIX_ERR = "NAMESPACE_PREFIX_ERR";
  
  public static final String DOM_ADAPTER_INIT_ERR = "DOM_ADAPTER_INIT_ERR";
  
  public static final String PARSER_DTD_SUPPORT_ERR = "PARSER_DTD_SUPPORT_ERR";
  
  public static final String NAMESPACES_SUPPORT_ERR = "NAMESPACES_SUPPORT_ERR";
  
  public static final String CANT_RESOLVE_RELATIVE_URI_ERR = "CANT_RESOLVE_RELATIVE_URI_ERR";
  
  public static final String UNSUPPORTED_XSL_ERR = "UNSUPPORTED_XSL_ERR";
  
  public static final String UNSUPPORTED_EXT_ERR = "UNSUPPORTED_EXT_ERR";
  
  public static final String UNKNOWN_TRANSLET_VERSION_ERR = "UNKNOWN_TRANSLET_VERSION_ERR";
  
  public static final String INVALID_QNAME_ERR = "INVALID_QNAME_ERR";
  
  public static final String INVALID_NCNAME_ERR = "INVALID_NCNAME_ERR";
  
  public static final String UNALLOWED_EXTENSION_FUNCTION_ERR = "UNALLOWED_EXTENSION_FUNCTION_ERR";
  
  public static final String UNALLOWED_EXTENSION_ELEMENT_ERR = "UNALLOWED_EXTENSION_ELEMENT_ERR";
  
  private static ResourceBundle m_bundle;
  
  public static final String ERROR_MESSAGES_KEY = "error-messages";
  
  public static int countF(DTMAxisIterator paramDTMAxisIterator) { return paramDTMAxisIterator.getLast(); }
  
  public static int positionF(DTMAxisIterator paramDTMAxisIterator) { return paramDTMAxisIterator.isReverse() ? (paramDTMAxisIterator.getLast() - paramDTMAxisIterator.getPosition() + 1) : paramDTMAxisIterator.getPosition(); }
  
  public static double sumF(DTMAxisIterator paramDTMAxisIterator, DOM paramDOM) {
    try {
      double d;
      int i;
      for (d = 0.0D; (i = paramDTMAxisIterator.next()) != -1; d += Double.parseDouble(paramDOM.getStringValueX(i)));
      return d;
    } catch (NumberFormatException numberFormatException) {
      return NaND;
    } 
  }
  
  public static String stringF(int paramInt, DOM paramDOM) { return paramDOM.getStringValueX(paramInt); }
  
  public static String stringF(Object paramObject, DOM paramDOM) { return (paramObject instanceof DTMAxisIterator) ? paramDOM.getStringValueX(((DTMAxisIterator)paramObject).reset().next()) : ((paramObject instanceof Node) ? paramDOM.getStringValueX(((Node)paramObject).node) : ((paramObject instanceof DOM) ? ((DOM)paramObject).getStringValue() : paramObject.toString())); }
  
  public static String stringF(Object paramObject, int paramInt, DOM paramDOM) {
    if (paramObject instanceof DTMAxisIterator)
      return paramDOM.getStringValueX(((DTMAxisIterator)paramObject).reset().next()); 
    if (paramObject instanceof Node)
      return paramDOM.getStringValueX(((Node)paramObject).node); 
    if (paramObject instanceof DOM)
      return ((DOM)paramObject).getStringValue(); 
    if (paramObject instanceof Double) {
      Double double = (Double)paramObject;
      String str = double.toString();
      int i = str.length();
      return (str.charAt(i - 2) == '.' && str.charAt(i - 1) == '0') ? str.substring(0, i - 2) : str;
    } 
    return (paramObject != null) ? paramObject.toString() : "";
  }
  
  public static double numberF(int paramInt, DOM paramDOM) { return stringToReal(paramDOM.getStringValueX(paramInt)); }
  
  public static double numberF(Object paramObject, DOM paramDOM) {
    if (paramObject instanceof Double)
      return ((Double)paramObject).doubleValue(); 
    if (paramObject instanceof Integer)
      return ((Integer)paramObject).doubleValue(); 
    if (paramObject instanceof Boolean)
      return ((Boolean)paramObject).booleanValue() ? 1.0D : 0.0D; 
    if (paramObject instanceof String)
      return stringToReal((String)paramObject); 
    if (paramObject instanceof DTMAxisIterator) {
      DTMAxisIterator dTMAxisIterator = (DTMAxisIterator)paramObject;
      return stringToReal(paramDOM.getStringValueX(dTMAxisIterator.reset().next()));
    } 
    if (paramObject instanceof Node)
      return stringToReal(paramDOM.getStringValueX(((Node)paramObject).node)); 
    if (paramObject instanceof DOM)
      return stringToReal(((DOM)paramObject).getStringValue()); 
    String str = paramObject.getClass().getName();
    runTimeError("INVALID_ARGUMENT_ERR", str, "number()");
    return 0.0D;
  }
  
  public static double roundF(double paramDouble) { return (paramDouble < -0.5D || paramDouble > 0.0D) ? Math.floor(paramDouble + 0.5D) : ((paramDouble == 0.0D) ? paramDouble : (Double.isNaN(paramDouble) ? NaND : -0.0D)); }
  
  public static boolean booleanF(Object paramObject) {
    if (paramObject instanceof Double) {
      double d = ((Double)paramObject).doubleValue();
      return (d != 0.0D && !Double.isNaN(d));
    } 
    if (paramObject instanceof Integer)
      return (((Integer)paramObject).doubleValue() != 0.0D); 
    if (paramObject instanceof Boolean)
      return ((Boolean)paramObject).booleanValue(); 
    if (paramObject instanceof String)
      return !((String)paramObject).equals(""); 
    if (paramObject instanceof DTMAxisIterator) {
      DTMAxisIterator dTMAxisIterator = (DTMAxisIterator)paramObject;
      return (dTMAxisIterator.reset().next() != -1);
    } 
    if (paramObject instanceof Node)
      return true; 
    if (paramObject instanceof DOM) {
      String str1 = ((DOM)paramObject).getStringValue();
      return !str1.equals("");
    } 
    String str = paramObject.getClass().getName();
    runTimeError("INVALID_ARGUMENT_ERR", str, "boolean()");
    return false;
  }
  
  public static String substringF(String paramString, double paramDouble) {
    if (Double.isNaN(paramDouble))
      return ""; 
    int i = getStringLength(paramString);
    int j = (int)Math.round(paramDouble) - 1;
    if (j > i)
      return ""; 
    if (j < 1)
      j = 0; 
    try {
      j = paramString.offsetByCodePoints(0, j);
      return paramString.substring(j);
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      runTimeError("RUN_TIME_INTERNAL_ERR", "substring()");
      return null;
    } 
  }
  
  public static String substringF(String paramString, double paramDouble1, double paramDouble2) {
    int k;
    if (Double.isInfinite(paramDouble1) || Double.isNaN(paramDouble1) || Double.isNaN(paramDouble2) || paramDouble2 < 0.0D)
      return ""; 
    int i = (int)Math.round(paramDouble1) - 1;
    int j = (int)Math.round(paramDouble2);
    if (Double.isInfinite(paramDouble2)) {
      k = Integer.MAX_VALUE;
    } else {
      k = i + j;
    } 
    int m = getStringLength(paramString);
    if (k < 0 || i > m)
      return ""; 
    if (i < 0) {
      j += i;
      i = 0;
    } 
    try {
      i = paramString.offsetByCodePoints(0, i);
      if (k > m)
        return paramString.substring(i); 
      int n = paramString.offsetByCodePoints(i, j);
      return paramString.substring(i, n);
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      runTimeError("RUN_TIME_INTERNAL_ERR", "substring()");
      return null;
    } 
  }
  
  public static String substring_afterF(String paramString1, String paramString2) {
    int i = paramString1.indexOf(paramString2);
    return (i >= 0) ? paramString1.substring(i + paramString2.length()) : "";
  }
  
  public static String substring_beforeF(String paramString1, String paramString2) {
    int i = paramString1.indexOf(paramString2);
    return (i >= 0) ? paramString1.substring(0, i) : "";
  }
  
  public static String translateF(String paramString1, String paramString2, String paramString3) {
    int i = paramString3.length();
    int j = paramString2.length();
    int k = paramString1.length();
    StringBuilder stringBuilder = (StringBuilder)threadLocalStringBuilder.get();
    stringBuilder.setLength(0);
    for (byte b = 0; b < k; b++) {
      char c = paramString1.charAt(b);
      byte b1;
      for (b1 = 0; b1 < j; b1++) {
        if (c == paramString2.charAt(b1)) {
          if (b1 < i)
            stringBuilder.append(paramString3.charAt(b1)); 
          break;
        } 
      } 
      if (b1 == j)
        stringBuilder.append(c); 
    } 
    return stringBuilder.toString();
  }
  
  public static String normalize_spaceF(int paramInt, DOM paramDOM) { return normalize_spaceF(paramDOM.getStringValueX(paramInt)); }
  
  public static String normalize_spaceF(String paramString) {
    byte b = 0;
    int i = paramString.length();
    StringBuilder stringBuilder = (StringBuilder)threadLocalStringBuilder.get();
    stringBuilder.setLength(0);
    while (b < i && isWhiteSpace(paramString.charAt(b)))
      b++; 
    while (true) {
      if (b < i && !isWhiteSpace(paramString.charAt(b))) {
        stringBuilder.append(paramString.charAt(b++));
        continue;
      } 
      if (b == i)
        break; 
      while (b < i && isWhiteSpace(paramString.charAt(b)))
        b++; 
      if (b < i)
        stringBuilder.append(' '); 
    } 
    return stringBuilder.toString();
  }
  
  public static String generate_idF(int paramInt) { return (paramInt > 0) ? ("N" + paramInt) : ""; }
  
  public static String getLocalName(String paramString) {
    int i = paramString.lastIndexOf(':');
    if (i >= 0)
      paramString = paramString.substring(i + 1); 
    i = paramString.lastIndexOf('@');
    if (i >= 0)
      paramString = paramString.substring(i + 1); 
    return paramString;
  }
  
  public static void unresolved_externalF(String paramString) { runTimeError("EXTERNAL_FUNC_ERR", paramString); }
  
  public static void unallowed_extension_functionF(String paramString) { runTimeError("UNALLOWED_EXTENSION_FUNCTION_ERR", paramString); }
  
  public static void unallowed_extension_elementF(String paramString) { runTimeError("UNALLOWED_EXTENSION_ELEMENT_ERR", paramString); }
  
  public static void unsupported_ElementF(String paramString, boolean paramBoolean) {
    if (paramBoolean) {
      runTimeError("UNSUPPORTED_EXT_ERR", paramString);
    } else {
      runTimeError("UNSUPPORTED_XSL_ERR", paramString);
    } 
  }
  
  public static String namespace_uriF(DTMAxisIterator paramDTMAxisIterator, DOM paramDOM) { return namespace_uriF(paramDTMAxisIterator.next(), paramDOM); }
  
  public static String system_propertyF(String paramString) {
    if (paramString.equals("xsl:version"))
      return "1.0"; 
    if (paramString.equals("xsl:vendor"))
      return "Apache Software Foundation (Xalan XSLTC)"; 
    if (paramString.equals("xsl:vendor-url"))
      return "http://xml.apache.org/xalan-j"; 
    runTimeError("INVALID_ARGUMENT_ERR", paramString, "system-property()");
    return "";
  }
  
  public static String namespace_uriF(int paramInt, DOM paramDOM) {
    String str = paramDOM.getNodeName(paramInt);
    int i = str.lastIndexOf(':');
    return (i >= 0) ? str.substring(0, i) : "";
  }
  
  public static String objectTypeF(Object paramObject) { return (paramObject instanceof String) ? "string" : ((paramObject instanceof Boolean) ? "boolean" : ((paramObject instanceof Number) ? "number" : ((paramObject instanceof DOM) ? "RTF" : ((paramObject instanceof DTMAxisIterator) ? "node-set" : "unknown")))); }
  
  public static DTMAxisIterator nodesetF(Object paramObject) {
    if (paramObject instanceof DOM) {
      DOM dOM = (DOM)paramObject;
      return new SingletonIterator(dOM.getDocument(), true);
    } 
    if (paramObject instanceof DTMAxisIterator)
      return (DTMAxisIterator)paramObject; 
    String str = paramObject.getClass().getName();
    runTimeError("DATA_CONVERSION_ERR", "node-set", str);
    return null;
  }
  
  private static boolean isWhiteSpace(char paramChar) { return (paramChar == ' ' || paramChar == '\t' || paramChar == '\n' || paramChar == '\r'); }
  
  private static boolean compareStrings(String paramString1, String paramString2, int paramInt, DOM paramDOM) {
    switch (paramInt) {
      case 0:
        return paramString1.equals(paramString2);
      case 1:
        return !paramString1.equals(paramString2);
      case 2:
        return (numberF(paramString1, paramDOM) > numberF(paramString2, paramDOM));
      case 3:
        return (numberF(paramString1, paramDOM) < numberF(paramString2, paramDOM));
      case 4:
        return (numberF(paramString1, paramDOM) >= numberF(paramString2, paramDOM));
      case 5:
        return (numberF(paramString1, paramDOM) <= numberF(paramString2, paramDOM));
    } 
    runTimeError("RUN_TIME_INTERNAL_ERR", "compare()");
    return false;
  }
  
  public static boolean compare(DTMAxisIterator paramDTMAxisIterator1, DTMAxisIterator paramDTMAxisIterator2, int paramInt, DOM paramDOM) {
    paramDTMAxisIterator1.reset();
    int i;
    while ((i = paramDTMAxisIterator1.next()) != -1) {
      String str = paramDOM.getStringValueX(i);
      paramDTMAxisIterator2.reset();
      int j;
      while ((j = paramDTMAxisIterator2.next()) != -1) {
        if (i == j) {
          if (paramInt == 0)
            return true; 
          if (paramInt == 1)
            continue; 
        } 
        if (compareStrings(str, paramDOM.getStringValueX(j), paramInt, paramDOM))
          return true; 
      } 
    } 
    return false;
  }
  
  public static boolean compare(int paramInt1, DTMAxisIterator paramDTMAxisIterator, int paramInt2, DOM paramDOM) {
    int i;
    switch (paramInt2) {
      case 0:
        i = paramDTMAxisIterator.next();
        if (i != -1) {
          String str = paramDOM.getStringValueX(paramInt1);
          do {
            if (paramInt1 == i || str.equals(paramDOM.getStringValueX(i)))
              return true; 
          } while ((i = paramDTMAxisIterator.next()) != -1);
        } 
        break;
      case 1:
        i = paramDTMAxisIterator.next();
        if (i != -1) {
          String str = paramDOM.getStringValueX(paramInt1);
          do {
            if (paramInt1 != i && !str.equals(paramDOM.getStringValueX(i)))
              return true; 
          } while ((i = paramDTMAxisIterator.next()) != -1);
        } 
        break;
      case 3:
        while ((i = paramDTMAxisIterator.next()) != -1) {
          if (i > paramInt1)
            return true; 
        } 
        break;
      case 2:
        while ((i = paramDTMAxisIterator.next()) != -1) {
          if (i < paramInt1)
            return true; 
        } 
        break;
    } 
    return false;
  }
  
  public static boolean compare(DTMAxisIterator paramDTMAxisIterator, double paramDouble, int paramInt, DOM paramDOM) {
    int i;
    switch (paramInt) {
      case 0:
        while ((i = paramDTMAxisIterator.next()) != -1) {
          if (numberF(paramDOM.getStringValueX(i), paramDOM) == paramDouble)
            return true; 
        } 
        return false;
      case 1:
        while ((i = paramDTMAxisIterator.next()) != -1) {
          if (numberF(paramDOM.getStringValueX(i), paramDOM) != paramDouble)
            return true; 
        } 
        return false;
      case 2:
        while ((i = paramDTMAxisIterator.next()) != -1) {
          if (numberF(paramDOM.getStringValueX(i), paramDOM) > paramDouble)
            return true; 
        } 
        return false;
      case 3:
        while ((i = paramDTMAxisIterator.next()) != -1) {
          if (numberF(paramDOM.getStringValueX(i), paramDOM) < paramDouble)
            return true; 
        } 
        return false;
      case 4:
        while ((i = paramDTMAxisIterator.next()) != -1) {
          if (numberF(paramDOM.getStringValueX(i), paramDOM) >= paramDouble)
            return true; 
        } 
        return false;
      case 5:
        while ((i = paramDTMAxisIterator.next()) != -1) {
          if (numberF(paramDOM.getStringValueX(i), paramDOM) <= paramDouble)
            return true; 
        } 
        return false;
    } 
    runTimeError("RUN_TIME_INTERNAL_ERR", "compare()");
    return false;
  }
  
  public static boolean compare(DTMAxisIterator paramDTMAxisIterator, String paramString, int paramInt, DOM paramDOM) {
    int i;
    while ((i = paramDTMAxisIterator.next()) != -1) {
      if (compareStrings(paramDOM.getStringValueX(i), paramString, paramInt, paramDOM))
        return true; 
    } 
    return false;
  }
  
  public static boolean compare(Object paramObject1, Object paramObject2, int paramInt, DOM paramDOM) {
    boolean bool = false;
    boolean bool1 = (hasSimpleType(paramObject1) && hasSimpleType(paramObject2)) ? 1 : 0;
    if (paramInt != 0 && paramInt != 1) {
      if (paramObject1 instanceof Node || paramObject2 instanceof Node) {
        if (paramObject1 instanceof Boolean) {
          paramObject2 = new Boolean(booleanF(paramObject2));
          bool1 = true;
        } 
        if (paramObject2 instanceof Boolean) {
          paramObject1 = new Boolean(booleanF(paramObject1));
          bool1 = true;
        } 
      } 
      if (bool1) {
        switch (paramInt) {
          case 2:
            return (numberF(paramObject1, paramDOM) > numberF(paramObject2, paramDOM));
          case 3:
            return (numberF(paramObject1, paramDOM) < numberF(paramObject2, paramDOM));
          case 4:
            return (numberF(paramObject1, paramDOM) >= numberF(paramObject2, paramDOM));
          case 5:
            return (numberF(paramObject1, paramDOM) <= numberF(paramObject2, paramDOM));
        } 
        runTimeError("RUN_TIME_INTERNAL_ERR", "compare()");
      } 
    } 
    if (bool1) {
      if (paramObject1 instanceof Boolean || paramObject2 instanceof Boolean) {
        bool = (booleanF(paramObject1) == booleanF(paramObject2));
      } else if (paramObject1 instanceof Double || paramObject2 instanceof Double || paramObject1 instanceof Integer || paramObject2 instanceof Integer) {
        bool = (numberF(paramObject1, paramDOM) == numberF(paramObject2, paramDOM));
      } else {
        bool = stringF(paramObject1, paramDOM).equals(stringF(paramObject2, paramDOM));
      } 
      if (paramInt == 1)
        bool = !bool; 
    } else {
      if (paramObject1 instanceof Node)
        paramObject1 = new SingletonIterator(((Node)paramObject1).node); 
      if (paramObject2 instanceof Node)
        paramObject2 = new SingletonIterator(((Node)paramObject2).node); 
      if (hasSimpleType(paramObject1) || (paramObject1 instanceof DOM && paramObject2 instanceof DTMAxisIterator)) {
        Object object = paramObject2;
        paramObject2 = paramObject1;
        paramObject1 = object;
        paramInt = Operators.swapOp(paramInt);
      } 
      if (paramObject1 instanceof DOM) {
        if (paramObject2 instanceof Boolean) {
          bool = ((Boolean)paramObject2).booleanValue();
          return (bool == ((paramInt == 0)));
        } 
        String str = ((DOM)paramObject1).getStringValue();
        if (paramObject2 instanceof Number) {
          bool = (((Number)paramObject2).doubleValue() == stringToReal(str));
        } else if (paramObject2 instanceof String) {
          bool = str.equals((String)paramObject2);
        } else if (paramObject2 instanceof DOM) {
          bool = str.equals(((DOM)paramObject2).getStringValue());
        } 
        if (paramInt == 1)
          bool = !bool; 
        return bool;
      } 
      DTMAxisIterator dTMAxisIterator = ((DTMAxisIterator)paramObject1).reset();
      if (paramObject2 instanceof DTMAxisIterator) {
        bool = compare(dTMAxisIterator, (DTMAxisIterator)paramObject2, paramInt, paramDOM);
      } else if (paramObject2 instanceof String) {
        bool = compare(dTMAxisIterator, (String)paramObject2, paramInt, paramDOM);
      } else if (paramObject2 instanceof Number) {
        double d = ((Number)paramObject2).doubleValue();
        bool = compare(dTMAxisIterator, d, paramInt, paramDOM);
      } else if (paramObject2 instanceof Boolean) {
        boolean bool2 = ((Boolean)paramObject2).booleanValue();
        bool = (((dTMAxisIterator.reset().next() != -1)) == bool2);
      } else if (paramObject2 instanceof DOM) {
        bool = compare(dTMAxisIterator, ((DOM)paramObject2).getStringValue(), paramInt, paramDOM);
      } else {
        if (paramObject2 == null)
          return false; 
        String str = paramObject2.getClass().getName();
        runTimeError("INVALID_ARGUMENT_ERR", str, "compare()");
      } 
    } 
    return bool;
  }
  
  public static boolean testLanguage(String paramString, DOM paramDOM, int paramInt) {
    String str = paramDOM.getLanguage(paramInt);
    if (str == null)
      return false; 
    str = str.toLowerCase();
    paramString = paramString.toLowerCase();
    return (paramString.length() == 2) ? str.startsWith(paramString) : str.equals(paramString);
  }
  
  private static boolean hasSimpleType(Object paramObject) { return (paramObject instanceof Boolean || paramObject instanceof Double || paramObject instanceof Integer || paramObject instanceof String || paramObject instanceof Node || paramObject instanceof DOM); }
  
  public static double stringToReal(String paramString) {
    try {
      return Double.valueOf(paramString).doubleValue();
    } catch (NumberFormatException numberFormatException) {
      return NaND;
    } 
  }
  
  public static int stringToInt(String paramString) {
    try {
      return Integer.parseInt(paramString);
    } catch (NumberFormatException numberFormatException) {
      return -1;
    } 
  }
  
  public static String realToString(double paramDouble) {
    double d = Math.abs(paramDouble);
    if (d >= 0.001D && d < 1.0E7D) {
      String str = Double.toString(paramDouble);
      int i = str.length();
      return (str.charAt(i - 2) == '.' && str.charAt(i - 1) == '0') ? str.substring(0, i - 2) : str;
    } 
    if (Double.isNaN(paramDouble) || Double.isInfinite(paramDouble))
      return Double.toString(paramDouble); 
    paramDouble += 0.0D;
    StringBuffer stringBuffer = (StringBuffer)threadLocalStringBuffer.get();
    stringBuffer.setLength(0);
    xpathFormatter.format(paramDouble, stringBuffer, _fieldPosition);
    return stringBuffer.toString();
  }
  
  public static int realToInt(double paramDouble) { return (int)paramDouble; }
  
  public static String formatNumber(double paramDouble, String paramString, DecimalFormat paramDecimalFormat) {
    if (paramDecimalFormat == null)
      paramDecimalFormat = defaultFormatter; 
    try {
      StringBuffer stringBuffer = (StringBuffer)threadLocalStringBuffer.get();
      stringBuffer.setLength(0);
      if (paramString != defaultPattern)
        paramDecimalFormat.applyLocalizedPattern(paramString); 
      paramDecimalFormat.format(paramDouble, stringBuffer, _fieldPosition);
      return stringBuffer.toString();
    } catch (IllegalArgumentException illegalArgumentException) {
      runTimeError("FORMAT_NUMBER_ERR", Double.toString(paramDouble), paramString);
      return "";
    } 
  }
  
  public static DTMAxisIterator referenceToNodeSet(Object paramObject) {
    if (paramObject instanceof Node)
      return new SingletonIterator(((Node)paramObject).node); 
    if (paramObject instanceof DTMAxisIterator)
      return ((DTMAxisIterator)paramObject).cloneIterator().reset(); 
    String str = paramObject.getClass().getName();
    runTimeError("DATA_CONVERSION_ERR", str, "node-set");
    return null;
  }
  
  public static NodeList referenceToNodeList(Object paramObject, DOM paramDOM) {
    if (paramObject instanceof Node || paramObject instanceof DTMAxisIterator) {
      DTMAxisIterator dTMAxisIterator = referenceToNodeSet(paramObject);
      return paramDOM.makeNodeList(dTMAxisIterator);
    } 
    if (paramObject instanceof DOM) {
      paramDOM = (DOM)paramObject;
      return paramDOM.makeNodeList(0);
    } 
    String str = paramObject.getClass().getName();
    runTimeError("DATA_CONVERSION_ERR", str, "org.w3c.dom.NodeList");
    return null;
  }
  
  public static Node referenceToNode(Object paramObject, DOM paramDOM) {
    if (paramObject instanceof Node || paramObject instanceof DTMAxisIterator) {
      DTMAxisIterator dTMAxisIterator = referenceToNodeSet(paramObject);
      return paramDOM.makeNode(dTMAxisIterator);
    } 
    if (paramObject instanceof DOM) {
      paramDOM = (DOM)paramObject;
      DTMAxisIterator dTMAxisIterator = paramDOM.getChildren(0);
      return paramDOM.makeNode(dTMAxisIterator);
    } 
    String str = paramObject.getClass().getName();
    runTimeError("DATA_CONVERSION_ERR", str, "org.w3c.dom.Node");
    return null;
  }
  
  public static long referenceToLong(Object paramObject) {
    if (paramObject instanceof Number)
      return ((Number)paramObject).longValue(); 
    String str = paramObject.getClass().getName();
    runTimeError("DATA_CONVERSION_ERR", str, long.class);
    return 0L;
  }
  
  public static double referenceToDouble(Object paramObject) {
    if (paramObject instanceof Number)
      return ((Number)paramObject).doubleValue(); 
    String str = paramObject.getClass().getName();
    runTimeError("DATA_CONVERSION_ERR", str, double.class);
    return 0.0D;
  }
  
  public static boolean referenceToBoolean(Object paramObject) {
    if (paramObject instanceof Boolean)
      return ((Boolean)paramObject).booleanValue(); 
    String str = paramObject.getClass().getName();
    runTimeError("DATA_CONVERSION_ERR", str, boolean.class);
    return false;
  }
  
  public static String referenceToString(Object paramObject, DOM paramDOM) {
    if (paramObject instanceof String)
      return (String)paramObject; 
    if (paramObject instanceof DTMAxisIterator)
      return paramDOM.getStringValueX(((DTMAxisIterator)paramObject).reset().next()); 
    if (paramObject instanceof Node)
      return paramDOM.getStringValueX(((Node)paramObject).node); 
    if (paramObject instanceof DOM)
      return ((DOM)paramObject).getStringValue(); 
    String str = paramObject.getClass().getName();
    runTimeError("DATA_CONVERSION_ERR", str, String.class);
    return null;
  }
  
  public static DTMAxisIterator node2Iterator(Node paramNode, Translet paramTranslet, DOM paramDOM) {
    final Node inNode = paramNode;
    NodeList nodeList = new NodeList() {
        public int getLength() { return 1; }
        
        public Node item(int param1Int) { return (param1Int == 0) ? inNode : null; }
      };
    return nodeList2Iterator(nodeList, paramTranslet, paramDOM);
  }
  
  private static DTMAxisIterator nodeList2IteratorUsingHandleFromNode(NodeList paramNodeList, Translet paramTranslet, DOM paramDOM) {
    int i = paramNodeList.getLength();
    int[] arrayOfInt = new int[i];
    DTMManager dTMManager = null;
    if (paramDOM instanceof MultiDOM)
      dTMManager = ((MultiDOM)paramDOM).getDTMManager(); 
    for (byte b = 0; b < i; b++) {
      int j;
      Node node = paramNodeList.item(b);
      if (dTMManager != null) {
        j = dTMManager.getDTMHandleFromNode(node);
      } else if (node instanceof DTMNodeProxy && ((DTMNodeProxy)node).getDTM() == paramDOM) {
        j = ((DTMNodeProxy)node).getDTMNodeNumber();
      } else {
        runTimeError("RUN_TIME_INTERNAL_ERR", "need MultiDOM");
        return null;
      } 
      arrayOfInt[b] = j;
      System.out.println("Node " + b + " has handle 0x" + Integer.toString(j, 16));
    } 
    return new ArrayNodeListIterator(arrayOfInt);
  }
  
  public static DTMAxisIterator nodeList2Iterator(NodeList paramNodeList, Translet paramTranslet, DOM paramDOM) {
    byte b1 = 0;
    Document document = null;
    DTMManager dTMManager = null;
    int[] arrayOfInt1 = new int[paramNodeList.getLength()];
    if (paramDOM instanceof MultiDOM)
      dTMManager = ((MultiDOM)paramDOM).getDTMManager(); 
    for (byte b2 = 0; b2 < paramNodeList.getLength(); b2++) {
      Element element;
      Node node = paramNodeList.item(b2);
      if (node instanceof DTMNodeProxy) {
        DTMNodeProxy dTMNodeProxy = (DTMNodeProxy)node;
        DTM dTM = dTMNodeProxy.getDTM();
        int i = dTMNodeProxy.getDTMNodeNumber();
        boolean bool = (dTM == paramDOM) ? 1 : 0;
        if (!bool && dTMManager != null)
          try {
            bool = (dTM == dTMManager.getDTM(i)) ? 1 : 0;
          } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {} 
        if (bool) {
          arrayOfInt1[b2] = i;
          b1++;
          continue;
        } 
      } 
      arrayOfInt1[b2] = -1;
      short s = node.getNodeType();
      if (document == null) {
        if (!(paramDOM instanceof MultiDOM)) {
          runTimeError("RUN_TIME_INTERNAL_ERR", "need MultiDOM");
          return null;
        } 
        try {
          AbstractTranslet abstractTranslet = (AbstractTranslet)paramTranslet;
          document = abstractTranslet.newDocument("", "__top__");
        } catch (ParserConfigurationException parserConfigurationException) {
          runTimeError("RUN_TIME_INTERNAL_ERR", parserConfigurationException.getMessage());
          return null;
        } 
      } 
      switch (s) {
        case 1:
        case 3:
        case 4:
        case 5:
        case 7:
        case 8:
          element = document.createElementNS(null, "__dummy__");
          element.appendChild(document.importNode(node, true));
          document.getDocumentElement().appendChild(element);
          b1++;
          break;
        case 2:
          element = document.createElementNS(null, "__dummy__");
          element.setAttributeNodeNS((Attr)document.importNode(node, true));
          document.getDocumentElement().appendChild(element);
          b1++;
          break;
        default:
          runTimeError("RUN_TIME_INTERNAL_ERR", "Don't know how to convert node type " + s);
          break;
      } 
      continue;
    } 
    AbsoluteIterator absoluteIterator = null;
    DTMAxisIterator dTMAxisIterator1 = null;
    DTMAxisIterator dTMAxisIterator2 = null;
    if (document != null) {
      MultiDOM multiDOM = (MultiDOM)paramDOM;
      DOM dOM = (DOM)dTMManager.getDTM(new DOMSource(document), false, null, true, false);
      DOMAdapter dOMAdapter = new DOMAdapter(dOM, paramTranslet.getNamesArray(), paramTranslet.getUrisArray(), paramTranslet.getTypesArray(), paramTranslet.getNamespaceArray());
      multiDOM.addDOMAdapter(dOMAdapter);
      DTMAxisIterator dTMAxisIterator3 = dOM.getAxisIterator(3);
      DTMAxisIterator dTMAxisIterator4 = dOM.getAxisIterator(3);
      absoluteIterator = new AbsoluteIterator(new StepIterator(dTMAxisIterator3, dTMAxisIterator4));
      absoluteIterator.setStartNode(0);
      dTMAxisIterator1 = dOM.getAxisIterator(3);
      dTMAxisIterator2 = dOM.getAxisIterator(2);
    } 
    int[] arrayOfInt2 = new int[b1];
    b1 = 0;
    for (byte b3 = 0; b3 < paramNodeList.getLength(); b3++) {
      if (arrayOfInt1[b3] != -1) {
        arrayOfInt2[b1++] = arrayOfInt1[b3];
      } else {
        Node node = paramNodeList.item(b3);
        DTMAxisIterator dTMAxisIterator = null;
        short s = node.getNodeType();
        switch (s) {
          case 1:
          case 3:
          case 4:
          case 5:
          case 7:
          case 8:
            dTMAxisIterator = dTMAxisIterator1;
            break;
          case 2:
            dTMAxisIterator = dTMAxisIterator2;
            break;
          default:
            throw new InternalRuntimeError("Mismatched cases");
        } 
        if (dTMAxisIterator != null) {
          dTMAxisIterator.setStartNode(absoluteIterator.next());
          arrayOfInt2[b1] = dTMAxisIterator.next();
          if (arrayOfInt2[b1] == -1)
            throw new InternalRuntimeError("Expected element missing at " + b3); 
          if (dTMAxisIterator.next() != -1)
            throw new InternalRuntimeError("Too many elements at " + b3); 
          b1++;
        } 
      } 
    } 
    if (b1 != arrayOfInt2.length)
      throw new InternalRuntimeError("Nodes lost in second pass"); 
    return new ArrayNodeListIterator(arrayOfInt2);
  }
  
  public static DOM referenceToResultTree(Object paramObject) {
    try {
      return (DOM)paramObject;
    } catch (IllegalArgumentException illegalArgumentException) {
      String str = paramObject.getClass().getName();
      runTimeError("DATA_CONVERSION_ERR", "reference", str);
      return null;
    } 
  }
  
  public static DTMAxisIterator getSingleNode(DTMAxisIterator paramDTMAxisIterator) {
    int i = paramDTMAxisIterator.next();
    return new SingletonIterator(i);
  }
  
  public static void copy(Object paramObject, SerializationHandler paramSerializationHandler, int paramInt, DOM paramDOM) {
    try {
      if (paramObject instanceof DTMAxisIterator) {
        DTMAxisIterator dTMAxisIterator = (DTMAxisIterator)paramObject;
        paramDOM.copy(dTMAxisIterator.reset(), paramSerializationHandler);
      } else if (paramObject instanceof Node) {
        paramDOM.copy(((Node)paramObject).node, paramSerializationHandler);
      } else if (paramObject instanceof DOM) {
        DOM dOM = (DOM)paramObject;
        dOM.copy(dOM.getDocument(), paramSerializationHandler);
      } else {
        String str = paramObject.toString();
        int i = str.length();
        if (i > _characterArray.length)
          _characterArray = new char[i]; 
        str.getChars(0, i, _characterArray, 0);
        paramSerializationHandler.characters(_characterArray, 0, i);
      } 
    } catch (SAXException sAXException) {
      runTimeError("RUN_TIME_COPY_ERR");
    } 
  }
  
  public static void checkAttribQName(String paramString) {
    int i = paramString.indexOf(":");
    int j = paramString.lastIndexOf(":");
    String str = paramString.substring(j + 1);
    if (i > 0) {
      String str1 = paramString.substring(0, i);
      if (i != j) {
        String str2 = paramString.substring(i + 1, j);
        if (!XML11Char.isXML11ValidNCName(str2))
          runTimeError("INVALID_QNAME_ERR", str2 + ":" + str); 
      } 
      if (!XML11Char.isXML11ValidNCName(str1))
        runTimeError("INVALID_QNAME_ERR", str1 + ":" + str); 
    } 
    if (!XML11Char.isXML11ValidNCName(str) || str.equals("xmlns"))
      runTimeError("INVALID_QNAME_ERR", str); 
  }
  
  public static void checkNCName(String paramString) {
    if (!XML11Char.isXML11ValidNCName(paramString))
      runTimeError("INVALID_NCNAME_ERR", paramString); 
  }
  
  public static void checkQName(String paramString) {
    if (!XML11Char.isXML11ValidQName(paramString))
      runTimeError("INVALID_QNAME_ERR", paramString); 
  }
  
  public static String startXslElement(String paramString1, String paramString2, SerializationHandler paramSerializationHandler, DOM paramDOM, int paramInt) {
    try {
      int i = paramString1.indexOf(':');
      if (i > 0) {
        String str = paramString1.substring(0, i);
        if (paramString2 == null || paramString2.length() == 0)
          try {
            paramString2 = paramDOM.lookupNamespace(paramInt, str);
          } catch (RuntimeException runtimeException) {
            paramSerializationHandler.flushPending();
            NamespaceMappings namespaceMappings = paramSerializationHandler.getNamespaceMappings();
            paramString2 = namespaceMappings.lookupNamespace(str);
            if (paramString2 == null)
              runTimeError("NAMESPACE_PREFIX_ERR", str); 
          }  
        paramSerializationHandler.startElement(paramString2, paramString1.substring(i + 1), paramString1);
        paramSerializationHandler.namespaceAfterStartElement(str, paramString2);
      } else if (paramString2 != null && paramString2.length() > 0) {
        String str = generatePrefix();
        paramString1 = str + ':' + paramString1;
        paramSerializationHandler.startElement(paramString2, paramString1, paramString1);
        paramSerializationHandler.namespaceAfterStartElement(str, paramString2);
      } else {
        paramSerializationHandler.startElement(null, null, paramString1);
      } 
    } catch (SAXException sAXException) {
      throw new RuntimeException(sAXException.getMessage());
    } 
    return paramString1;
  }
  
  public static String getPrefix(String paramString) {
    int i = paramString.indexOf(':');
    return (i > 0) ? paramString.substring(0, i) : null;
  }
  
  public static String generatePrefix() { return "ns" + ((AtomicInteger)threadLocalPrefixIndex.get()).getAndIncrement(); }
  
  public static void resetPrefixIndex() { ((AtomicInteger)threadLocalPrefixIndex.get()).set(0); }
  
  public static void runTimeError(String paramString) { throw new RuntimeException(m_bundle.getString(paramString)); }
  
  public static void runTimeError(String paramString, Object[] paramArrayOfObject) {
    String str = MessageFormat.format(m_bundle.getString(paramString), paramArrayOfObject);
    throw new RuntimeException(str);
  }
  
  public static void runTimeError(String paramString, Object paramObject) { runTimeError(paramString, new Object[] { paramObject }); }
  
  public static void runTimeError(String paramString, Object paramObject1, Object paramObject2) { runTimeError(paramString, new Object[] { paramObject1, paramObject2 }); }
  
  public static void consoleOutput(String paramString) { System.out.println(paramString); }
  
  public static String replace(String paramString1, char paramChar, String paramString2) { return (paramString1.indexOf(paramChar) < 0) ? paramString1 : replace(paramString1, String.valueOf(paramChar), new String[] { paramString2 }); }
  
  public static String replace(String paramString1, String paramString2, String[] paramArrayOfString) {
    int i = paramString1.length();
    StringBuilder stringBuilder = (StringBuilder)threadLocalStringBuilder.get();
    stringBuilder.setLength(0);
    for (byte b = 0; b < i; b++) {
      char c = paramString1.charAt(b);
      int j = paramString2.indexOf(c);
      if (j >= 0) {
        stringBuilder.append(paramArrayOfString[j]);
      } else {
        stringBuilder.append(c);
      } 
    } 
    return stringBuilder.toString();
  }
  
  public static String mapQNameToJavaName(String paramString) { return replace(paramString, ".-:/{}?#%*", new String[] { "$dot$", "$dash$", "$colon$", "$slash$", "", "$colon$", "$ques$", "$hash$", "$per$", "$aster$" }); }
  
  public static int getStringLength(String paramString) { return paramString.codePointCount(0, paramString.length()); }
  
  static  {
    NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
    defaultFormatter = (numberFormat instanceof DecimalFormat) ? (DecimalFormat)numberFormat : new DecimalFormat();
    defaultFormatter.setMaximumFractionDigits(340);
    defaultFormatter.setMinimumFractionDigits(0);
    defaultFormatter.setMinimumIntegerDigits(1);
    defaultFormatter.setGroupingUsed(false);
    xpathFormatter = new DecimalFormat("", new DecimalFormatSymbols(Locale.US));
    xpathFormatter.setMaximumFractionDigits(340);
    xpathFormatter.setMinimumFractionDigits(0);
    xpathFormatter.setMinimumIntegerDigits(1);
    xpathFormatter.setGroupingUsed(false);
    _fieldPosition = new FieldPosition(0);
    _characterArray = new char[32];
    threadLocalPrefixIndex = new ThreadLocal<AtomicInteger>() {
        protected AtomicInteger initialValue() { return new AtomicInteger(); }
      };
    String str = "com.sun.org.apache.xalan.internal.xsltc.runtime.ErrorMessages";
    m_bundle = SecuritySupport.getResourceBundle(str);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\runtime\BasisLibrary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */