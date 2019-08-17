package com.sun.jndi.ldap;

import java.util.Vector;
import javax.naming.ConfigurationException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InvalidAttributeIdentifierException;
import javax.naming.directory.InvalidAttributeValueException;

final class LdapSchemaParser {
  private static final boolean debug = false;
  
  static final String OBJECTCLASSDESC_ATTR_ID = "objectClasses";
  
  static final String ATTRIBUTEDESC_ATTR_ID = "attributeTypes";
  
  static final String SYNTAXDESC_ATTR_ID = "ldapSyntaxes";
  
  static final String MATCHRULEDESC_ATTR_ID = "matchingRules";
  
  static final String OBJECTCLASS_DEFINITION_NAME = "ClassDefinition";
  
  private static final String[] CLASS_DEF_ATTRS = { "objectclass", "ClassDefinition" };
  
  static final String ATTRIBUTE_DEFINITION_NAME = "AttributeDefinition";
  
  private static final String[] ATTR_DEF_ATTRS = { "objectclass", "AttributeDefinition" };
  
  static final String SYNTAX_DEFINITION_NAME = "SyntaxDefinition";
  
  private static final String[] SYNTAX_DEF_ATTRS = { "objectclass", "SyntaxDefinition" };
  
  static final String MATCHRULE_DEFINITION_NAME = "MatchingRule";
  
  private static final String[] MATCHRULE_DEF_ATTRS = { "objectclass", "MatchingRule" };
  
  private static final char SINGLE_QUOTE = '\'';
  
  private static final char WHSP = ' ';
  
  private static final char OID_LIST_BEGIN = '(';
  
  private static final char OID_LIST_END = ')';
  
  private static final char OID_SEPARATOR = '$';
  
  private static final String NUMERICOID_ID = "NUMERICOID";
  
  private static final String NAME_ID = "NAME";
  
  private static final String DESC_ID = "DESC";
  
  private static final String OBSOLETE_ID = "OBSOLETE";
  
  private static final String SUP_ID = "SUP";
  
  private static final String PRIVATE_ID = "X-";
  
  private static final String ABSTRACT_ID = "ABSTRACT";
  
  private static final String STRUCTURAL_ID = "STRUCTURAL";
  
  private static final String AUXILARY_ID = "AUXILIARY";
  
  private static final String MUST_ID = "MUST";
  
  private static final String MAY_ID = "MAY";
  
  private static final String EQUALITY_ID = "EQUALITY";
  
  private static final String ORDERING_ID = "ORDERING";
  
  private static final String SUBSTR_ID = "SUBSTR";
  
  private static final String SYNTAX_ID = "SYNTAX";
  
  private static final String SINGLE_VAL_ID = "SINGLE-VALUE";
  
  private static final String COLLECTIVE_ID = "COLLECTIVE";
  
  private static final String NO_USER_MOD_ID = "NO-USER-MODIFICATION";
  
  private static final String USAGE_ID = "USAGE";
  
  private static final String SCHEMA_TRUE_VALUE = "true";
  
  private boolean netscapeBug;
  
  LdapSchemaParser(boolean paramBoolean) { this.netscapeBug = paramBoolean; }
  
  static final void LDAP2JNDISchema(Attributes paramAttributes, LdapSchemaCtx paramLdapSchemaCtx) throws NamingException {
    Attribute attribute1 = null;
    Attribute attribute2 = null;
    Attribute attribute3 = null;
    Attribute attribute4 = null;
    attribute1 = paramAttributes.get("objectClasses");
    if (attribute1 != null)
      objectDescs2ClassDefs(attribute1, paramLdapSchemaCtx); 
    attribute2 = paramAttributes.get("attributeTypes");
    if (attribute2 != null)
      attrDescs2AttrDefs(attribute2, paramLdapSchemaCtx); 
    attribute3 = paramAttributes.get("ldapSyntaxes");
    if (attribute3 != null)
      syntaxDescs2SyntaxDefs(attribute3, paramLdapSchemaCtx); 
    attribute4 = paramAttributes.get("matchingRules");
    if (attribute4 != null)
      matchRuleDescs2MatchRuleDefs(attribute4, paramLdapSchemaCtx); 
  }
  
  private static final DirContext objectDescs2ClassDefs(Attribute paramAttribute, LdapSchemaCtx paramLdapSchemaCtx) throws NamingException {
    BasicAttributes basicAttributes = new BasicAttributes(true);
    basicAttributes.put(CLASS_DEF_ATTRS[0], CLASS_DEF_ATTRS[1]);
    LdapSchemaCtx ldapSchemaCtx = paramLdapSchemaCtx.setup(2, "ClassDefinition", basicAttributes);
    NamingEnumeration namingEnumeration = paramAttribute.getAll();
    while (namingEnumeration.hasMore()) {
      String str = (String)namingEnumeration.next();
      try {
        Object[] arrayOfObject = desc2Def(str);
        String str1 = (String)arrayOfObject[0];
        Attributes attributes = (Attributes)arrayOfObject[1];
        ldapSchemaCtx.setup(6, str1, attributes);
      } catch (NamingException namingException) {}
    } 
    return ldapSchemaCtx;
  }
  
  private static final DirContext attrDescs2AttrDefs(Attribute paramAttribute, LdapSchemaCtx paramLdapSchemaCtx) throws NamingException {
    BasicAttributes basicAttributes = new BasicAttributes(true);
    basicAttributes.put(ATTR_DEF_ATTRS[0], ATTR_DEF_ATTRS[1]);
    LdapSchemaCtx ldapSchemaCtx = paramLdapSchemaCtx.setup(3, "AttributeDefinition", basicAttributes);
    NamingEnumeration namingEnumeration = paramAttribute.getAll();
    while (namingEnumeration.hasMore()) {
      String str = (String)namingEnumeration.next();
      try {
        Object[] arrayOfObject = desc2Def(str);
        String str1 = (String)arrayOfObject[0];
        Attributes attributes = (Attributes)arrayOfObject[1];
        ldapSchemaCtx.setup(7, str1, attributes);
      } catch (NamingException namingException) {}
    } 
    return ldapSchemaCtx;
  }
  
  private static final DirContext syntaxDescs2SyntaxDefs(Attribute paramAttribute, LdapSchemaCtx paramLdapSchemaCtx) throws NamingException {
    BasicAttributes basicAttributes = new BasicAttributes(true);
    basicAttributes.put(SYNTAX_DEF_ATTRS[0], SYNTAX_DEF_ATTRS[1]);
    LdapSchemaCtx ldapSchemaCtx = paramLdapSchemaCtx.setup(4, "SyntaxDefinition", basicAttributes);
    NamingEnumeration namingEnumeration = paramAttribute.getAll();
    while (namingEnumeration.hasMore()) {
      String str = (String)namingEnumeration.next();
      try {
        Object[] arrayOfObject = desc2Def(str);
        String str1 = (String)arrayOfObject[0];
        Attributes attributes = (Attributes)arrayOfObject[1];
        ldapSchemaCtx.setup(8, str1, attributes);
      } catch (NamingException namingException) {}
    } 
    return ldapSchemaCtx;
  }
  
  private static final DirContext matchRuleDescs2MatchRuleDefs(Attribute paramAttribute, LdapSchemaCtx paramLdapSchemaCtx) throws NamingException {
    BasicAttributes basicAttributes = new BasicAttributes(true);
    basicAttributes.put(MATCHRULE_DEF_ATTRS[0], MATCHRULE_DEF_ATTRS[1]);
    LdapSchemaCtx ldapSchemaCtx = paramLdapSchemaCtx.setup(5, "MatchingRule", basicAttributes);
    NamingEnumeration namingEnumeration = paramAttribute.getAll();
    while (namingEnumeration.hasMore()) {
      String str = (String)namingEnumeration.next();
      try {
        Object[] arrayOfObject = desc2Def(str);
        String str1 = (String)arrayOfObject[0];
        Attributes attributes = (Attributes)arrayOfObject[1];
        ldapSchemaCtx.setup(9, str1, attributes);
      } catch (NamingException namingException) {}
    } 
    return ldapSchemaCtx;
  }
  
  private static final Object[] desc2Def(String paramString) throws NamingException {
    BasicAttributes basicAttributes = new BasicAttributes(true);
    Attribute attribute = null;
    int[] arrayOfInt = { 1 };
    boolean bool = true;
    attribute = readNumericOID(paramString, arrayOfInt);
    String str = (String)attribute.get(0);
    basicAttributes.put(attribute);
    skipWhitespace(paramString, arrayOfInt);
    while (bool) {
      attribute = readNextTag(paramString, arrayOfInt);
      basicAttributes.put(attribute);
      if (attribute.getID().equals("NAME"))
        str = (String)attribute.get(0); 
      skipWhitespace(paramString, arrayOfInt);
      if (arrayOfInt[0] >= paramString.length() - 1)
        bool = false; 
    } 
    return new Object[] { str, basicAttributes };
  }
  
  private static final int findTrailingWhitespace(String paramString, int paramInt) {
    for (int i = paramInt; i > 0; i--) {
      if (paramString.charAt(i) != ' ')
        return i + 1; 
    } 
    return 0;
  }
  
  private static final void skipWhitespace(String paramString, int[] paramArrayOfInt) {
    for (int i = paramArrayOfInt[0]; i < paramString.length(); i++) {
      if (paramString.charAt(i) != ' ') {
        paramArrayOfInt[0] = i;
        return;
      } 
    } 
  }
  
  private static final Attribute readNumericOID(String paramString, int[] paramArrayOfInt) throws NamingException {
    String str = null;
    skipWhitespace(paramString, paramArrayOfInt);
    int i = paramArrayOfInt[0];
    int j = paramString.indexOf(' ', i);
    if (j == -1 || j - i < 1)
      throw new InvalidAttributeValueException("no numericoid found: " + paramString); 
    str = paramString.substring(i, j);
    paramArrayOfInt[0] = paramArrayOfInt[0] + str.length();
    return new BasicAttribute("NUMERICOID", str);
  }
  
  private static final Attribute readNextTag(String paramString, int[] paramArrayOfInt) throws NamingException {
    BasicAttribute basicAttribute = null;
    String str = null;
    String[] arrayOfString = null;
    skipWhitespace(paramString, paramArrayOfInt);
    int i = paramString.indexOf(' ', paramArrayOfInt[0]);
    if (i < 0) {
      str = paramString.substring(paramArrayOfInt[0], paramString.length() - 1);
    } else {
      str = paramString.substring(paramArrayOfInt[0], i);
    } 
    arrayOfString = readTag(str, paramString, paramArrayOfInt);
    if (arrayOfString.length < 0)
      throw new InvalidAttributeValueException("no values for attribute \"" + str + "\""); 
    basicAttribute = new BasicAttribute(str, arrayOfString[0]);
    for (byte b = 1; b < arrayOfString.length; b++)
      basicAttribute.add(arrayOfString[b]); 
    return basicAttribute;
  }
  
  private static final String[] readTag(String paramString1, String paramString2, int[] paramArrayOfInt) throws NamingException {
    paramArrayOfInt[0] = paramArrayOfInt[0] + paramString1.length();
    skipWhitespace(paramString2, paramArrayOfInt);
    return paramString1.equals("NAME") ? readQDescrs(paramString2, paramArrayOfInt) : (paramString1.equals("DESC") ? readQDString(paramString2, paramArrayOfInt) : ((paramString1.equals("EQUALITY") || paramString1.equals("ORDERING") || paramString1.equals("SUBSTR") || paramString1.equals("SYNTAX")) ? readWOID(paramString2, paramArrayOfInt) : ((paramString1.equals("OBSOLETE") || paramString1.equals("ABSTRACT") || paramString1.equals("STRUCTURAL") || paramString1.equals("AUXILIARY") || paramString1.equals("SINGLE-VALUE") || paramString1.equals("COLLECTIVE") || paramString1.equals("NO-USER-MODIFICATION")) ? new String[] { "true" } : ((paramString1.equals("SUP") || paramString1.equals("MUST") || paramString1.equals("MAY") || paramString1.equals("USAGE")) ? readOIDs(paramString2, paramArrayOfInt) : readQDStrings(paramString2, paramArrayOfInt)))));
  }
  
  private static final String[] readQDString(String paramString, int[] paramArrayOfInt) throws NamingException {
    int i = paramString.indexOf('\'', paramArrayOfInt[0]) + 1;
    int j = paramString.indexOf('\'', i);
    if (i == -1 || j == -1 || i == j)
      throw new InvalidAttributeIdentifierException("malformed QDString: " + paramString); 
    if (paramString.charAt(i - 1) != '\'')
      throw new InvalidAttributeIdentifierException("qdstring has no end mark: " + paramString); 
    paramArrayOfInt[0] = j + 1;
    return new String[] { paramString.substring(i, j) };
  }
  
  private static final String[] readQDStrings(String paramString, int[] paramArrayOfInt) throws NamingException { return readQDescrs(paramString, paramArrayOfInt); }
  
  private static final String[] readQDescrs(String paramString, int[] paramArrayOfInt) throws NamingException {
    skipWhitespace(paramString, paramArrayOfInt);
    switch (paramString.charAt(paramArrayOfInt[0])) {
      case '(':
        return readQDescrList(paramString, paramArrayOfInt);
      case '\'':
        return readQDString(paramString, paramArrayOfInt);
    } 
    throw new InvalidAttributeValueException("unexpected oids string: " + paramString);
  }
  
  private static final String[] readQDescrList(String paramString, int[] paramArrayOfInt) throws NamingException {
    Vector vector = new Vector(5);
    paramArrayOfInt[0] = paramArrayOfInt[0] + 1;
    skipWhitespace(paramString, paramArrayOfInt);
    int i = paramArrayOfInt[0];
    int j = paramString.indexOf(')', i);
    if (j == -1)
      throw new InvalidAttributeValueException("oidlist has no end mark: " + paramString); 
    while (i < j) {
      String[] arrayOfString1 = readQDString(paramString, paramArrayOfInt);
      vector.addElement(arrayOfString1[0]);
      skipWhitespace(paramString, paramArrayOfInt);
      i = paramArrayOfInt[0];
    } 
    paramArrayOfInt[0] = j + 1;
    String[] arrayOfString = new String[vector.size()];
    for (byte b = 0; b < arrayOfString.length; b++)
      arrayOfString[b] = (String)vector.elementAt(b); 
    return arrayOfString;
  }
  
  private static final String[] readWOID(String paramString, int[] paramArrayOfInt) throws NamingException {
    skipWhitespace(paramString, paramArrayOfInt);
    if (paramString.charAt(paramArrayOfInt[0]) == '\'')
      return readQDString(paramString, paramArrayOfInt); 
    int i = paramArrayOfInt[0];
    int j = paramString.indexOf(' ', i);
    if (j == -1 || i == j)
      throw new InvalidAttributeIdentifierException("malformed OID: " + paramString); 
    paramArrayOfInt[0] = j + 1;
    return new String[] { paramString.substring(i, j) };
  }
  
  private static final String[] readOIDs(String paramString, int[] paramArrayOfInt) throws NamingException {
    skipWhitespace(paramString, paramArrayOfInt);
    if (paramString.charAt(paramArrayOfInt[0]) != '(')
      return readWOID(paramString, paramArrayOfInt); 
    String str = null;
    Vector vector = new Vector(5);
    paramArrayOfInt[0] = paramArrayOfInt[0] + 1;
    skipWhitespace(paramString, paramArrayOfInt);
    int i = paramArrayOfInt[0];
    int k = paramString.indexOf(')', i);
    int j = paramString.indexOf('$', i);
    if (k == -1)
      throw new InvalidAttributeValueException("oidlist has no end mark: " + paramString); 
    if (j == -1 || k < j)
      j = k; 
    while (j < k && j > 0) {
      int n = findTrailingWhitespace(paramString, j - 1);
      str = paramString.substring(i, n);
      vector.addElement(str);
      paramArrayOfInt[0] = j + 1;
      skipWhitespace(paramString, paramArrayOfInt);
      i = paramArrayOfInt[0];
      j = paramString.indexOf('$', i);
    } 
    int m = findTrailingWhitespace(paramString, k - 1);
    str = paramString.substring(i, m);
    vector.addElement(str);
    paramArrayOfInt[0] = k + 1;
    String[] arrayOfString = new String[vector.size()];
    for (byte b = 0; b < arrayOfString.length; b++)
      arrayOfString[b] = (String)vector.elementAt(b); 
    return arrayOfString;
  }
  
  private final String classDef2ObjectDesc(Attributes paramAttributes) throws NamingException {
    StringBuffer stringBuffer = new StringBuffer("( ");
    Attribute attribute = null;
    byte b = 0;
    attribute = paramAttributes.get("NUMERICOID");
    if (attribute != null) {
      stringBuffer.append(writeNumericOID(attribute));
      b++;
    } else {
      throw new ConfigurationException("Class definition doesn'thave a numeric OID");
    } 
    attribute = paramAttributes.get("NAME");
    if (attribute != null) {
      stringBuffer.append(writeQDescrs(attribute));
      b++;
    } 
    attribute = paramAttributes.get("DESC");
    if (attribute != null) {
      stringBuffer.append(writeQDString(attribute));
      b++;
    } 
    attribute = paramAttributes.get("OBSOLETE");
    if (attribute != null) {
      stringBuffer.append(writeBoolean(attribute));
      b++;
    } 
    attribute = paramAttributes.get("SUP");
    if (attribute != null) {
      stringBuffer.append(writeOIDs(attribute));
      b++;
    } 
    attribute = paramAttributes.get("ABSTRACT");
    if (attribute != null) {
      stringBuffer.append(writeBoolean(attribute));
      b++;
    } 
    attribute = paramAttributes.get("STRUCTURAL");
    if (attribute != null) {
      stringBuffer.append(writeBoolean(attribute));
      b++;
    } 
    attribute = paramAttributes.get("AUXILIARY");
    if (attribute != null) {
      stringBuffer.append(writeBoolean(attribute));
      b++;
    } 
    attribute = paramAttributes.get("MUST");
    if (attribute != null) {
      stringBuffer.append(writeOIDs(attribute));
      b++;
    } 
    attribute = paramAttributes.get("MAY");
    if (attribute != null) {
      stringBuffer.append(writeOIDs(attribute));
      b++;
    } 
    if (b < paramAttributes.size()) {
      String str = null;
      NamingEnumeration namingEnumeration = paramAttributes.getAll();
      while (namingEnumeration.hasMoreElements()) {
        attribute = (Attribute)namingEnumeration.next();
        str = attribute.getID();
        if (str.equals("NUMERICOID") || str.equals("NAME") || str.equals("SUP") || str.equals("MAY") || str.equals("MUST") || str.equals("STRUCTURAL") || str.equals("DESC") || str.equals("AUXILIARY") || str.equals("ABSTRACT") || str.equals("OBSOLETE"))
          continue; 
        stringBuffer.append(writeQDStrings(attribute));
      } 
    } 
    stringBuffer.append(")");
    return stringBuffer.toString();
  }
  
  private final String attrDef2AttrDesc(Attributes paramAttributes) throws NamingException {
    StringBuffer stringBuffer = new StringBuffer("( ");
    Attribute attribute = null;
    byte b = 0;
    attribute = paramAttributes.get("NUMERICOID");
    if (attribute != null) {
      stringBuffer.append(writeNumericOID(attribute));
      b++;
    } else {
      throw new ConfigurationException("Attribute type doesn'thave a numeric OID");
    } 
    attribute = paramAttributes.get("NAME");
    if (attribute != null) {
      stringBuffer.append(writeQDescrs(attribute));
      b++;
    } 
    attribute = paramAttributes.get("DESC");
    if (attribute != null) {
      stringBuffer.append(writeQDString(attribute));
      b++;
    } 
    attribute = paramAttributes.get("OBSOLETE");
    if (attribute != null) {
      stringBuffer.append(writeBoolean(attribute));
      b++;
    } 
    attribute = paramAttributes.get("SUP");
    if (attribute != null) {
      stringBuffer.append(writeWOID(attribute));
      b++;
    } 
    attribute = paramAttributes.get("EQUALITY");
    if (attribute != null) {
      stringBuffer.append(writeWOID(attribute));
      b++;
    } 
    attribute = paramAttributes.get("ORDERING");
    if (attribute != null) {
      stringBuffer.append(writeWOID(attribute));
      b++;
    } 
    attribute = paramAttributes.get("SUBSTR");
    if (attribute != null) {
      stringBuffer.append(writeWOID(attribute));
      b++;
    } 
    attribute = paramAttributes.get("SYNTAX");
    if (attribute != null) {
      stringBuffer.append(writeWOID(attribute));
      b++;
    } 
    attribute = paramAttributes.get("SINGLE-VALUE");
    if (attribute != null) {
      stringBuffer.append(writeBoolean(attribute));
      b++;
    } 
    attribute = paramAttributes.get("COLLECTIVE");
    if (attribute != null) {
      stringBuffer.append(writeBoolean(attribute));
      b++;
    } 
    attribute = paramAttributes.get("NO-USER-MODIFICATION");
    if (attribute != null) {
      stringBuffer.append(writeBoolean(attribute));
      b++;
    } 
    attribute = paramAttributes.get("USAGE");
    if (attribute != null) {
      stringBuffer.append(writeQDString(attribute));
      b++;
    } 
    if (b < paramAttributes.size()) {
      String str = null;
      NamingEnumeration namingEnumeration = paramAttributes.getAll();
      while (namingEnumeration.hasMoreElements()) {
        attribute = (Attribute)namingEnumeration.next();
        str = attribute.getID();
        if (str.equals("NUMERICOID") || str.equals("NAME") || str.equals("SYNTAX") || str.equals("DESC") || str.equals("SINGLE-VALUE") || str.equals("EQUALITY") || str.equals("ORDERING") || str.equals("SUBSTR") || str.equals("NO-USER-MODIFICATION") || str.equals("USAGE") || str.equals("SUP") || str.equals("COLLECTIVE") || str.equals("OBSOLETE"))
          continue; 
        stringBuffer.append(writeQDStrings(attribute));
      } 
    } 
    stringBuffer.append(")");
    return stringBuffer.toString();
  }
  
  private final String syntaxDef2SyntaxDesc(Attributes paramAttributes) throws NamingException {
    StringBuffer stringBuffer = new StringBuffer("( ");
    Attribute attribute = null;
    byte b = 0;
    attribute = paramAttributes.get("NUMERICOID");
    if (attribute != null) {
      stringBuffer.append(writeNumericOID(attribute));
      b++;
    } else {
      throw new ConfigurationException("Attribute type doesn'thave a numeric OID");
    } 
    attribute = paramAttributes.get("DESC");
    if (attribute != null) {
      stringBuffer.append(writeQDString(attribute));
      b++;
    } 
    if (b < paramAttributes.size()) {
      String str = null;
      NamingEnumeration namingEnumeration = paramAttributes.getAll();
      while (namingEnumeration.hasMoreElements()) {
        attribute = (Attribute)namingEnumeration.next();
        str = attribute.getID();
        if (str.equals("NUMERICOID") || str.equals("DESC"))
          continue; 
        stringBuffer.append(writeQDStrings(attribute));
      } 
    } 
    stringBuffer.append(")");
    return stringBuffer.toString();
  }
  
  private final String matchRuleDef2MatchRuleDesc(Attributes paramAttributes) throws NamingException {
    StringBuffer stringBuffer = new StringBuffer("( ");
    Attribute attribute = null;
    byte b = 0;
    attribute = paramAttributes.get("NUMERICOID");
    if (attribute != null) {
      stringBuffer.append(writeNumericOID(attribute));
      b++;
    } else {
      throw new ConfigurationException("Attribute type doesn'thave a numeric OID");
    } 
    attribute = paramAttributes.get("NAME");
    if (attribute != null) {
      stringBuffer.append(writeQDescrs(attribute));
      b++;
    } 
    attribute = paramAttributes.get("DESC");
    if (attribute != null) {
      stringBuffer.append(writeQDString(attribute));
      b++;
    } 
    attribute = paramAttributes.get("OBSOLETE");
    if (attribute != null) {
      stringBuffer.append(writeBoolean(attribute));
      b++;
    } 
    attribute = paramAttributes.get("SYNTAX");
    if (attribute != null) {
      stringBuffer.append(writeWOID(attribute));
      b++;
    } else {
      throw new ConfigurationException("Attribute type doesn'thave a syntax OID");
    } 
    if (b < paramAttributes.size()) {
      String str = null;
      NamingEnumeration namingEnumeration = paramAttributes.getAll();
      while (namingEnumeration.hasMoreElements()) {
        attribute = (Attribute)namingEnumeration.next();
        str = attribute.getID();
        if (str.equals("NUMERICOID") || str.equals("NAME") || str.equals("SYNTAX") || str.equals("DESC") || str.equals("OBSOLETE"))
          continue; 
        stringBuffer.append(writeQDStrings(attribute));
      } 
    } 
    stringBuffer.append(")");
    return stringBuffer.toString();
  }
  
  private final String writeNumericOID(Attribute paramAttribute) throws NamingException {
    if (paramAttribute.size() != 1)
      throw new InvalidAttributeValueException("A class definition must have exactly one numeric OID"); 
    return (String)paramAttribute.get() + ' ';
  }
  
  private final String writeWOID(Attribute paramAttribute) throws NamingException { return this.netscapeBug ? writeQDString(paramAttribute) : (paramAttribute.getID() + ' ' + paramAttribute.get() + ' '); }
  
  private final String writeQDString(Attribute paramAttribute) throws NamingException {
    if (paramAttribute.size() != 1)
      throw new InvalidAttributeValueException(paramAttribute.getID() + " must have exactly one value"); 
    return paramAttribute.getID() + ' ' + '\'' + paramAttribute.get() + '\'' + ' ';
  }
  
  private final String writeQDStrings(Attribute paramAttribute) throws NamingException { return writeQDescrs(paramAttribute); }
  
  private final String writeQDescrs(Attribute paramAttribute) throws NamingException {
    switch (paramAttribute.size()) {
      case 0:
        throw new InvalidAttributeValueException(paramAttribute.getID() + "has no values");
      case 1:
        return writeQDString(paramAttribute);
    } 
    StringBuffer stringBuffer = new StringBuffer(paramAttribute.getID());
    stringBuffer.append(' ');
    stringBuffer.append('(');
    NamingEnumeration namingEnumeration = paramAttribute.getAll();
    while (namingEnumeration.hasMore()) {
      stringBuffer.append(' ');
      stringBuffer.append('\'');
      stringBuffer.append((String)namingEnumeration.next());
      stringBuffer.append('\'');
      stringBuffer.append(' ');
    } 
    stringBuffer.append(')');
    stringBuffer.append(' ');
    return stringBuffer.toString();
  }
  
  private final String writeOIDs(Attribute paramAttribute) throws NamingException {
    switch (paramAttribute.size()) {
      case 0:
        throw new InvalidAttributeValueException(paramAttribute.getID() + "has no values");
      case 1:
        if (this.netscapeBug)
          break; 
        return writeWOID(paramAttribute);
    } 
    StringBuffer stringBuffer = new StringBuffer(paramAttribute.getID());
    stringBuffer.append(' ');
    stringBuffer.append('(');
    NamingEnumeration namingEnumeration = paramAttribute.getAll();
    stringBuffer.append(' ');
    stringBuffer.append(namingEnumeration.next());
    while (namingEnumeration.hasMore()) {
      stringBuffer.append(' ');
      stringBuffer.append('$');
      stringBuffer.append(' ');
      stringBuffer.append((String)namingEnumeration.next());
    } 
    stringBuffer.append(' ');
    stringBuffer.append(')');
    stringBuffer.append(' ');
    return stringBuffer.toString();
  }
  
  private final String writeBoolean(Attribute paramAttribute) throws NamingException { return paramAttribute.getID() + ' '; }
  
  final Attribute stringifyObjDesc(Attributes paramAttributes) throws NamingException {
    BasicAttribute basicAttribute = new BasicAttribute("objectClasses");
    basicAttribute.add(classDef2ObjectDesc(paramAttributes));
    return basicAttribute;
  }
  
  final Attribute stringifyAttrDesc(Attributes paramAttributes) throws NamingException {
    BasicAttribute basicAttribute = new BasicAttribute("attributeTypes");
    basicAttribute.add(attrDef2AttrDesc(paramAttributes));
    return basicAttribute;
  }
  
  final Attribute stringifySyntaxDesc(Attributes paramAttributes) throws NamingException {
    BasicAttribute basicAttribute = new BasicAttribute("ldapSyntaxes");
    basicAttribute.add(syntaxDef2SyntaxDesc(paramAttributes));
    return basicAttribute;
  }
  
  final Attribute stringifyMatchRuleDesc(Attributes paramAttributes) throws NamingException {
    BasicAttribute basicAttribute = new BasicAttribute("matchingRules");
    basicAttribute.add(matchRuleDef2MatchRuleDesc(paramAttributes));
    return basicAttribute;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\LdapSchemaParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */