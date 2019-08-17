package com.sun.org.apache.xpath.internal.compiler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Keywords {
  private static final Map<String, Integer> m_keywords;
  
  private static final Map<String, Integer> m_axisnames;
  
  private static final Map<String, Integer> m_nodetests;
  
  private static final Map<String, Integer> m_nodetypes;
  
  private static final String FROM_ANCESTORS_STRING = "ancestor";
  
  private static final String FROM_ANCESTORS_OR_SELF_STRING = "ancestor-or-self";
  
  private static final String FROM_ATTRIBUTES_STRING = "attribute";
  
  private static final String FROM_CHILDREN_STRING = "child";
  
  private static final String FROM_DESCENDANTS_STRING = "descendant";
  
  private static final String FROM_DESCENDANTS_OR_SELF_STRING = "descendant-or-self";
  
  private static final String FROM_FOLLOWING_STRING = "following";
  
  private static final String FROM_FOLLOWING_SIBLINGS_STRING = "following-sibling";
  
  private static final String FROM_PARENT_STRING = "parent";
  
  private static final String FROM_PRECEDING_STRING = "preceding";
  
  private static final String FROM_PRECEDING_SIBLINGS_STRING = "preceding-sibling";
  
  private static final String FROM_SELF_STRING = "self";
  
  private static final String FROM_NAMESPACE_STRING = "namespace";
  
  private static final String FROM_SELF_ABBREVIATED_STRING = ".";
  
  private static final String NODETYPE_COMMENT_STRING = "comment";
  
  private static final String NODETYPE_TEXT_STRING = "text";
  
  private static final String NODETYPE_PI_STRING = "processing-instruction";
  
  private static final String NODETYPE_NODE_STRING = "node";
  
  private static final String NODETYPE_ANYELEMENT_STRING = "*";
  
  public static final String FUNC_CURRENT_STRING = "current";
  
  public static final String FUNC_LAST_STRING = "last";
  
  public static final String FUNC_POSITION_STRING = "position";
  
  public static final String FUNC_COUNT_STRING = "count";
  
  static final String FUNC_ID_STRING = "id";
  
  public static final String FUNC_KEY_STRING = "key";
  
  public static final String FUNC_LOCAL_PART_STRING = "local-name";
  
  public static final String FUNC_NAMESPACE_STRING = "namespace-uri";
  
  public static final String FUNC_NAME_STRING = "name";
  
  public static final String FUNC_GENERATE_ID_STRING = "generate-id";
  
  public static final String FUNC_NOT_STRING = "not";
  
  public static final String FUNC_TRUE_STRING = "true";
  
  public static final String FUNC_FALSE_STRING = "false";
  
  public static final String FUNC_BOOLEAN_STRING = "boolean";
  
  public static final String FUNC_LANG_STRING = "lang";
  
  public static final String FUNC_NUMBER_STRING = "number";
  
  public static final String FUNC_FLOOR_STRING = "floor";
  
  public static final String FUNC_CEILING_STRING = "ceiling";
  
  public static final String FUNC_ROUND_STRING = "round";
  
  public static final String FUNC_SUM_STRING = "sum";
  
  public static final String FUNC_STRING_STRING = "string";
  
  public static final String FUNC_STARTS_WITH_STRING = "starts-with";
  
  public static final String FUNC_CONTAINS_STRING = "contains";
  
  public static final String FUNC_SUBSTRING_BEFORE_STRING = "substring-before";
  
  public static final String FUNC_SUBSTRING_AFTER_STRING = "substring-after";
  
  public static final String FUNC_NORMALIZE_SPACE_STRING = "normalize-space";
  
  public static final String FUNC_TRANSLATE_STRING = "translate";
  
  public static final String FUNC_CONCAT_STRING = "concat";
  
  public static final String FUNC_SYSTEM_PROPERTY_STRING = "system-property";
  
  public static final String FUNC_EXT_FUNCTION_AVAILABLE_STRING = "function-available";
  
  public static final String FUNC_EXT_ELEM_AVAILABLE_STRING = "element-available";
  
  public static final String FUNC_SUBSTRING_STRING = "substring";
  
  public static final String FUNC_STRING_LENGTH_STRING = "string-length";
  
  public static final String FUNC_UNPARSED_ENTITY_URI_STRING = "unparsed-entity-uri";
  
  public static final String FUNC_DOCLOCATION_STRING = "document-location";
  
  static Integer getAxisName(String paramString) { return (Integer)m_axisnames.get(paramString); }
  
  static Integer lookupNodeTest(String paramString) { return (Integer)m_nodetests.get(paramString); }
  
  static Integer getKeyWord(String paramString) { return (Integer)m_keywords.get(paramString); }
  
  static Integer getNodeType(String paramString) { return (Integer)m_nodetypes.get(paramString); }
  
  static  {
    HashMap hashMap1 = new HashMap();
    HashMap hashMap2 = new HashMap();
    HashMap hashMap3 = new HashMap();
    HashMap hashMap4 = new HashMap();
    hashMap2.put("ancestor", Integer.valueOf(37));
    hashMap2.put("ancestor-or-self", Integer.valueOf(38));
    hashMap2.put("attribute", Integer.valueOf(39));
    hashMap2.put("child", Integer.valueOf(40));
    hashMap2.put("descendant", Integer.valueOf(41));
    hashMap2.put("descendant-or-self", Integer.valueOf(42));
    hashMap2.put("following", Integer.valueOf(43));
    hashMap2.put("following-sibling", Integer.valueOf(44));
    hashMap2.put("parent", Integer.valueOf(45));
    hashMap2.put("preceding", Integer.valueOf(46));
    hashMap2.put("preceding-sibling", Integer.valueOf(47));
    hashMap2.put("self", Integer.valueOf(48));
    hashMap2.put("namespace", Integer.valueOf(49));
    m_axisnames = Collections.unmodifiableMap(hashMap2);
    hashMap4.put("comment", Integer.valueOf(1030));
    hashMap4.put("text", Integer.valueOf(1031));
    hashMap4.put("processing-instruction", Integer.valueOf(1032));
    hashMap4.put("node", Integer.valueOf(1033));
    hashMap4.put("*", Integer.valueOf(36));
    m_nodetypes = Collections.unmodifiableMap(hashMap4);
    hashMap1.put(".", Integer.valueOf(48));
    hashMap1.put("id", Integer.valueOf(4));
    hashMap1.put("key", Integer.valueOf(5));
    m_keywords = Collections.unmodifiableMap(hashMap1);
    hashMap3.put("comment", Integer.valueOf(1030));
    hashMap3.put("text", Integer.valueOf(1031));
    hashMap3.put("processing-instruction", Integer.valueOf(1032));
    hashMap3.put("node", Integer.valueOf(1033));
    m_nodetests = Collections.unmodifiableMap(hashMap3);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\compiler\Keywords.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */