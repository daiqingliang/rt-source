package com.sun.org.apache.xpath.internal.compiler;

import com.sun.org.apache.xpath.internal.functions.Function;
import java.util.HashMap;
import javax.xml.transform.TransformerException;

public class FunctionTable {
  public static final int FUNC_CURRENT = 0;
  
  public static final int FUNC_LAST = 1;
  
  public static final int FUNC_POSITION = 2;
  
  public static final int FUNC_COUNT = 3;
  
  public static final int FUNC_ID = 4;
  
  public static final int FUNC_KEY = 5;
  
  public static final int FUNC_LOCAL_PART = 7;
  
  public static final int FUNC_NAMESPACE = 8;
  
  public static final int FUNC_QNAME = 9;
  
  public static final int FUNC_GENERATE_ID = 10;
  
  public static final int FUNC_NOT = 11;
  
  public static final int FUNC_TRUE = 12;
  
  public static final int FUNC_FALSE = 13;
  
  public static final int FUNC_BOOLEAN = 14;
  
  public static final int FUNC_NUMBER = 15;
  
  public static final int FUNC_FLOOR = 16;
  
  public static final int FUNC_CEILING = 17;
  
  public static final int FUNC_ROUND = 18;
  
  public static final int FUNC_SUM = 19;
  
  public static final int FUNC_STRING = 20;
  
  public static final int FUNC_STARTS_WITH = 21;
  
  public static final int FUNC_CONTAINS = 22;
  
  public static final int FUNC_SUBSTRING_BEFORE = 23;
  
  public static final int FUNC_SUBSTRING_AFTER = 24;
  
  public static final int FUNC_NORMALIZE_SPACE = 25;
  
  public static final int FUNC_TRANSLATE = 26;
  
  public static final int FUNC_CONCAT = 27;
  
  public static final int FUNC_SUBSTRING = 29;
  
  public static final int FUNC_STRING_LENGTH = 30;
  
  public static final int FUNC_SYSTEM_PROPERTY = 31;
  
  public static final int FUNC_LANG = 32;
  
  public static final int FUNC_EXT_FUNCTION_AVAILABLE = 33;
  
  public static final int FUNC_EXT_ELEM_AVAILABLE = 34;
  
  public static final int FUNC_UNPARSED_ENTITY_URI = 36;
  
  public static final int FUNC_DOCLOCATION = 35;
  
  private static Class[] m_functions;
  
  private static HashMap m_functionID = new HashMap();
  
  private Class[] m_functions_customer = new Class[30];
  
  private HashMap m_functionID_customer = new HashMap();
  
  private static final int NUM_BUILT_IN_FUNCS = 37;
  
  private static final int NUM_ALLOWABLE_ADDINS = 30;
  
  private int m_funcNextFreeIndex = 37;
  
  String getFunctionName(int paramInt) { return (paramInt < 37) ? m_functions[paramInt].getName() : this.m_functions_customer[paramInt - 37].getName(); }
  
  Function getFunction(int paramInt) throws TransformerException {
    try {
      return (paramInt < 37) ? (Function)m_functions[paramInt].newInstance() : (Function)this.m_functions_customer[paramInt - 37].newInstance();
    } catch (IllegalAccessException illegalAccessException) {
      throw new TransformerException(illegalAccessException.getMessage());
    } catch (InstantiationException instantiationException) {
      throw new TransformerException(instantiationException.getMessage());
    } 
  }
  
  Object getFunctionID(String paramString) {
    Object object = this.m_functionID_customer.get(paramString);
    if (null == object)
      object = m_functionID.get(paramString); 
    return object;
  }
  
  public int installFunction(String paramString, Class paramClass) {
    int i;
    Object object = getFunctionID(paramString);
    if (null != object) {
      i = ((Integer)object).intValue();
      if (i < 37) {
        i = this.m_funcNextFreeIndex++;
        this.m_functionID_customer.put(paramString, new Integer(i));
      } 
      this.m_functions_customer[i - 37] = paramClass;
    } else {
      i = this.m_funcNextFreeIndex++;
      this.m_functions_customer[i - 37] = paramClass;
      this.m_functionID_customer.put(paramString, new Integer(i));
    } 
    return i;
  }
  
  public boolean functionAvailable(String paramString) {
    Object object = m_functionID.get(paramString);
    if (null != object)
      return true; 
    object = this.m_functionID_customer.get(paramString);
    return (null != object);
  }
  
  static  {
    m_functions = new Class[37];
    m_functions[0] = com.sun.org.apache.xpath.internal.functions.FuncCurrent.class;
    m_functions[1] = com.sun.org.apache.xpath.internal.functions.FuncLast.class;
    m_functions[2] = com.sun.org.apache.xpath.internal.functions.FuncPosition.class;
    m_functions[3] = com.sun.org.apache.xpath.internal.functions.FuncCount.class;
    m_functions[4] = com.sun.org.apache.xpath.internal.functions.FuncId.class;
    m_functions[7] = com.sun.org.apache.xpath.internal.functions.FuncLocalPart.class;
    m_functions[8] = com.sun.org.apache.xpath.internal.functions.FuncNamespace.class;
    m_functions[9] = com.sun.org.apache.xpath.internal.functions.FuncQname.class;
    m_functions[10] = com.sun.org.apache.xpath.internal.functions.FuncGenerateId.class;
    m_functions[11] = com.sun.org.apache.xpath.internal.functions.FuncNot.class;
    m_functions[12] = com.sun.org.apache.xpath.internal.functions.FuncTrue.class;
    m_functions[13] = com.sun.org.apache.xpath.internal.functions.FuncFalse.class;
    m_functions[14] = com.sun.org.apache.xpath.internal.functions.FuncBoolean.class;
    m_functions[32] = com.sun.org.apache.xpath.internal.functions.FuncLang.class;
    m_functions[15] = com.sun.org.apache.xpath.internal.functions.FuncNumber.class;
    m_functions[16] = com.sun.org.apache.xpath.internal.functions.FuncFloor.class;
    m_functions[17] = com.sun.org.apache.xpath.internal.functions.FuncCeiling.class;
    m_functions[18] = com.sun.org.apache.xpath.internal.functions.FuncRound.class;
    m_functions[19] = com.sun.org.apache.xpath.internal.functions.FuncSum.class;
    m_functions[20] = com.sun.org.apache.xpath.internal.functions.FuncString.class;
    m_functions[21] = com.sun.org.apache.xpath.internal.functions.FuncStartsWith.class;
    m_functions[22] = com.sun.org.apache.xpath.internal.functions.FuncContains.class;
    m_functions[23] = com.sun.org.apache.xpath.internal.functions.FuncSubstringBefore.class;
    m_functions[24] = com.sun.org.apache.xpath.internal.functions.FuncSubstringAfter.class;
    m_functions[25] = com.sun.org.apache.xpath.internal.functions.FuncNormalizeSpace.class;
    m_functions[26] = com.sun.org.apache.xpath.internal.functions.FuncTranslate.class;
    m_functions[27] = com.sun.org.apache.xpath.internal.functions.FuncConcat.class;
    m_functions[31] = com.sun.org.apache.xpath.internal.functions.FuncSystemProperty.class;
    m_functions[33] = com.sun.org.apache.xpath.internal.functions.FuncExtFunctionAvailable.class;
    m_functions[34] = com.sun.org.apache.xpath.internal.functions.FuncExtElementAvailable.class;
    m_functions[29] = com.sun.org.apache.xpath.internal.functions.FuncSubstring.class;
    m_functions[30] = com.sun.org.apache.xpath.internal.functions.FuncStringLength.class;
    m_functions[35] = com.sun.org.apache.xpath.internal.functions.FuncDoclocation.class;
    m_functions[36] = com.sun.org.apache.xpath.internal.functions.FuncUnparsedEntityURI.class;
    m_functionID.put("current", new Integer(0));
    m_functionID.put("last", new Integer(1));
    m_functionID.put("position", new Integer(2));
    m_functionID.put("count", new Integer(3));
    m_functionID.put("id", new Integer(4));
    m_functionID.put("key", new Integer(5));
    m_functionID.put("local-name", new Integer(7));
    m_functionID.put("namespace-uri", new Integer(8));
    m_functionID.put("name", new Integer(9));
    m_functionID.put("generate-id", new Integer(10));
    m_functionID.put("not", new Integer(11));
    m_functionID.put("true", new Integer(12));
    m_functionID.put("false", new Integer(13));
    m_functionID.put("boolean", new Integer(14));
    m_functionID.put("lang", new Integer(32));
    m_functionID.put("number", new Integer(15));
    m_functionID.put("floor", new Integer(16));
    m_functionID.put("ceiling", new Integer(17));
    m_functionID.put("round", new Integer(18));
    m_functionID.put("sum", new Integer(19));
    m_functionID.put("string", new Integer(20));
    m_functionID.put("starts-with", new Integer(21));
    m_functionID.put("contains", new Integer(22));
    m_functionID.put("substring-before", new Integer(23));
    m_functionID.put("substring-after", new Integer(24));
    m_functionID.put("normalize-space", new Integer(25));
    m_functionID.put("translate", new Integer(26));
    m_functionID.put("concat", new Integer(27));
    m_functionID.put("system-property", new Integer(31));
    m_functionID.put("function-available", new Integer(33));
    m_functionID.put("element-available", new Integer(34));
    m_functionID.put("substring", new Integer(29));
    m_functionID.put("string-length", new Integer(30));
    m_functionID.put("unparsed-entity-uri", new Integer(36));
    m_functionID.put("document-location", new Integer(35));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\compiler\FunctionTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */