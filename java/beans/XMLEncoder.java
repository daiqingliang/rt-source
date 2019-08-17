package java.beans;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class XMLEncoder extends Encoder implements AutoCloseable {
  private final CharsetEncoder encoder;
  
  private final String charset;
  
  private final boolean declaration;
  
  private OutputStreamWriter out;
  
  private Object owner;
  
  private int indentation = 0;
  
  private boolean internal = false;
  
  private Map<Object, ValueData> valueToExpression;
  
  private Map<Object, List<Statement>> targetToStatementList;
  
  private boolean preambleWritten = false;
  
  private NameGenerator nameGenerator;
  
  public XMLEncoder(OutputStream paramOutputStream) { this(paramOutputStream, "UTF-8", true, 0); }
  
  public XMLEncoder(OutputStream paramOutputStream, String paramString, boolean paramBoolean, int paramInt) {
    if (paramOutputStream == null)
      throw new IllegalArgumentException("the output stream cannot be null"); 
    if (paramInt < 0)
      throw new IllegalArgumentException("the indentation must be >= 0"); 
    Charset charset1 = Charset.forName(paramString);
    this.encoder = charset1.newEncoder();
    this.charset = paramString;
    this.declaration = paramBoolean;
    this.indentation = paramInt;
    this.out = new OutputStreamWriter(paramOutputStream, charset1.newEncoder());
    this.valueToExpression = new IdentityHashMap();
    this.targetToStatementList = new IdentityHashMap();
    this.nameGenerator = new NameGenerator();
  }
  
  public void setOwner(Object paramObject) {
    this.owner = paramObject;
    writeExpression(new Expression(this, "getOwner", new Object[0]));
  }
  
  public Object getOwner() { return this.owner; }
  
  public void writeObject(Object paramObject) {
    if (this.internal) {
      super.writeObject(paramObject);
    } else {
      writeStatement(new Statement(this, "writeObject", new Object[] { paramObject }));
    } 
  }
  
  private List<Statement> statementList(Object paramObject) {
    List list = (List)this.targetToStatementList.get(paramObject);
    if (list == null) {
      list = new ArrayList();
      this.targetToStatementList.put(paramObject, list);
    } 
    return list;
  }
  
  private void mark(Object paramObject, boolean paramBoolean) {
    if (paramObject == null || paramObject == this)
      return; 
    ValueData valueData = getValueData(paramObject);
    Expression expression = valueData.exp;
    if (paramObject.getClass() == String.class && expression == null)
      return; 
    if (paramBoolean)
      valueData.refs++; 
    if (valueData.marked)
      return; 
    valueData.marked = true;
    Object object = expression.getTarget();
    mark(expression);
    if (!(object instanceof Class)) {
      statementList(object).add(expression);
      valueData.refs++;
    } 
  }
  
  private void mark(Statement paramStatement) {
    Object[] arrayOfObject = paramStatement.getArguments();
    for (byte b = 0; b < arrayOfObject.length; b++) {
      Object object = arrayOfObject[b];
      mark(object, true);
    } 
    mark(paramStatement.getTarget(), paramStatement instanceof Expression);
  }
  
  public void writeStatement(Statement paramStatement) {
    boolean bool = this.internal;
    this.internal = true;
    try {
      super.writeStatement(paramStatement);
      mark(paramStatement);
      Object object = paramStatement.getTarget();
      if (object instanceof Field) {
        String str = paramStatement.getMethodName();
        Object[] arrayOfObject = paramStatement.getArguments();
        if (str != null && arrayOfObject != null)
          if (str.equals("get") && arrayOfObject.length == 1) {
            object = arrayOfObject[0];
          } else if (str.equals("set") && arrayOfObject.length == 2) {
            object = arrayOfObject[0];
          }  
      } 
      statementList(object).add(paramStatement);
    } catch (Exception exception) {
      getExceptionListener().exceptionThrown(new Exception("XMLEncoder: discarding statement " + paramStatement, exception));
    } 
    this.internal = bool;
  }
  
  public void writeExpression(Expression paramExpression) {
    boolean bool = this.internal;
    this.internal = true;
    Object object = getValue(paramExpression);
    if (get(object) == null || (object instanceof String && !bool)) {
      (getValueData(object)).exp = paramExpression;
      super.writeExpression(paramExpression);
    } 
    this.internal = bool;
  }
  
  public void flush() {
    if (!this.preambleWritten) {
      if (this.declaration)
        writeln("<?xml version=" + quote("1.0") + " encoding=" + quote(this.charset) + "?>"); 
      writeln("<java version=" + quote(System.getProperty("java.version")) + " class=" + quote(XMLDecoder.class.getName()) + ">");
      this.preambleWritten = true;
    } 
    this.indentation++;
    List list = statementList(this);
    while (!list.isEmpty()) {
      Statement statement1 = (Statement)list.remove(0);
      if ("writeObject".equals(statement1.getMethodName())) {
        outputValue(statement1.getArguments()[0], this, true);
        continue;
      } 
      outputStatement(statement1, this, false);
    } 
    this.indentation--;
    for (Statement statement = getMissedStatement(); statement != null; statement = getMissedStatement())
      outputStatement(statement, this, false); 
    try {
      this.out.flush();
    } catch (IOException iOException) {
      getExceptionListener().exceptionThrown(iOException);
    } 
    clear();
  }
  
  void clear() {
    super.clear();
    this.nameGenerator.clear();
    this.valueToExpression.clear();
    this.targetToStatementList.clear();
  }
  
  Statement getMissedStatement() {
    for (List list : this.targetToStatementList.values()) {
      for (byte b = 0; b < list.size(); b++) {
        if (Statement.class == ((Statement)list.get(b)).getClass())
          return (Statement)list.remove(b); 
      } 
    } 
    return null;
  }
  
  public void close() {
    flush();
    writeln("</java>");
    try {
      this.out.close();
    } catch (IOException iOException) {
      getExceptionListener().exceptionThrown(iOException);
    } 
  }
  
  private String quote(String paramString) { return "\"" + paramString + "\""; }
  
  private ValueData getValueData(Object paramObject) {
    ValueData valueData = (ValueData)this.valueToExpression.get(paramObject);
    if (valueData == null) {
      valueData = new ValueData(null);
      this.valueToExpression.put(paramObject, valueData);
    } 
    return valueData;
  }
  
  private static boolean isValidCharCode(int paramInt) { return ((32 <= paramInt && paramInt <= 55295) || 10 == paramInt || 9 == paramInt || 13 == paramInt || (57344 <= paramInt && paramInt <= 65533) || (65536 <= paramInt && paramInt <= 1114111)); }
  
  private void writeln(String paramString) {
    try {
      StringBuilder stringBuilder = new StringBuilder();
      for (byte b = 0; b < this.indentation; b++)
        stringBuilder.append(' '); 
      stringBuilder.append(paramString);
      stringBuilder.append('\n');
      this.out.write(stringBuilder.toString());
    } catch (IOException iOException) {
      getExceptionListener().exceptionThrown(iOException);
    } 
  }
  
  private void outputValue(Object paramObject1, Object paramObject2, boolean paramBoolean) {
    if (paramObject1 == null) {
      writeln("<null/>");
      return;
    } 
    if (paramObject1 instanceof Class) {
      writeln("<class>" + ((Class)paramObject1).getName() + "</class>");
      return;
    } 
    ValueData valueData = getValueData(paramObject1);
    if (valueData.exp != null) {
      Object object = valueData.exp.getTarget();
      String str = valueData.exp.getMethodName();
      if (object == null || str == null)
        throw new NullPointerException(((object == null) ? "target" : "methodName") + " should not be null"); 
      if (paramBoolean && object instanceof Field && str.equals("get")) {
        Field field = (Field)object;
        writeln("<object class=" + quote(field.getDeclaringClass().getName()) + " field=" + quote(field.getName()) + "/>");
        return;
      } 
      Class clazz = primitiveTypeFor(paramObject1.getClass());
      if (clazz != null && object == paramObject1.getClass() && str.equals("new")) {
        String str1 = clazz.getName();
        if (clazz == char.class) {
          char c = ((Character)paramObject1).charValue();
          if (!isValidCharCode(c)) {
            writeln(createString(c));
            return;
          } 
          paramObject1 = quoteCharCode(c);
          if (paramObject1 == null)
            paramObject1 = Character.valueOf(c); 
        } 
        writeln("<" + str1 + ">" + paramObject1 + "</" + str1 + ">");
        return;
      } 
    } else if (paramObject1 instanceof String) {
      writeln(createString((String)paramObject1));
      return;
    } 
    if (valueData.name != null) {
      if (paramBoolean) {
        writeln("<object idref=" + quote(valueData.name) + "/>");
      } else {
        outputXML("void", " idref=" + quote(valueData.name), paramObject1, new Object[0]);
      } 
    } else if (valueData.exp != null) {
      outputStatement(valueData.exp, paramObject2, paramBoolean);
    } 
  }
  
  private static String quoteCharCode(int paramInt) {
    switch (paramInt) {
      case 38:
        return "&amp;";
      case 60:
        return "&lt;";
      case 62:
        return "&gt;";
      case 34:
        return "&quot;";
      case 39:
        return "&apos;";
      case 13:
        return "&#13;";
    } 
    return null;
  }
  
  private static String createString(int paramInt) { return "<char code=\"#" + Integer.toString(paramInt, 16) + "\"/>"; }
  
  private String createString(String paramString) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("<string>");
    for (int i = 0; i < paramString.length(); i++) {
      int j = paramString.codePointAt(i);
      int k = Character.charCount(j);
      if (isValidCharCode(j) && this.encoder.canEncode(paramString.substring(i, i + k))) {
        String str = quoteCharCode(j);
        if (str != null) {
          stringBuilder.append(str);
        } else {
          stringBuilder.appendCodePoint(j);
        } 
        i += k;
        continue;
      } 
      stringBuilder.append(createString(paramString.charAt(i)));
    } 
    stringBuilder.append("</string>");
    return stringBuilder.toString();
  }
  
  private void outputStatement(Statement paramStatement, Object paramObject, boolean paramBoolean) {
    Object object1 = paramStatement.getTarget();
    String str1 = paramStatement.getMethodName();
    if (object1 == null || str1 == null)
      throw new NullPointerException(((object1 == null) ? "target" : "methodName") + " should not be null"); 
    Object[] arrayOfObject = paramStatement.getArguments();
    boolean bool = (paramStatement.getClass() == Expression.class) ? 1 : 0;
    Object object2 = bool ? getValue((Expression)paramStatement) : null;
    String str2 = (bool && paramBoolean) ? "object" : "void";
    String str3 = "";
    ValueData valueData = getValueData(object2);
    if (object1 != paramObject)
      if (object1 == java.lang.reflect.Array.class && str1.equals("newInstance")) {
        str2 = "array";
        str3 = str3 + " class=" + quote(((Class)arrayOfObject[0]).getName());
        str3 = str3 + " length=" + quote(arrayOfObject[1].toString());
        arrayOfObject = new Object[0];
      } else if (object1.getClass() == Class.class) {
        str3 = str3 + " class=" + quote(((Class)object1).getName());
      } else {
        valueData.refs = 2;
        if (valueData.name == null) {
          (getValueData(object1)).refs++;
          List list = statementList(object1);
          if (!list.contains(paramStatement))
            list.add(paramStatement); 
          outputValue(object1, paramObject, false);
        } 
        if (bool)
          outputValue(object2, paramObject, paramBoolean); 
        return;
      }  
    if (bool && valueData.refs > 1) {
      String str = this.nameGenerator.instanceName(object2);
      valueData.name = str;
      str3 = str3 + " id=" + quote(str);
    } 
    if ((!bool && str1.equals("set") && arrayOfObject.length == 2 && arrayOfObject[0] instanceof Integer) || (bool && str1.equals("get") && arrayOfObject.length == 1 && arrayOfObject[0] instanceof Integer)) {
      str3 = str3 + " index=" + quote(arrayOfObject[0].toString());
      new Object[1][0] = arrayOfObject[1];
      arrayOfObject = (arrayOfObject.length == 1) ? new Object[0] : new Object[1];
    } else if ((!bool && str1.startsWith("set") && arrayOfObject.length == 1) || (bool && str1.startsWith("get") && arrayOfObject.length == 0)) {
      if (3 < str1.length())
        str3 = str3 + " property=" + quote(Introspector.decapitalize(str1.substring(3))); 
    } else if (!str1.equals("new") && !str1.equals("newInstance")) {
      str3 = str3 + " method=" + quote(str1);
    } 
    outputXML(str2, str3, object2, arrayOfObject);
  }
  
  private void outputXML(String paramString1, String paramString2, Object paramObject, Object... paramVarArgs) {
    List list = statementList(paramObject);
    if (paramVarArgs.length == 0 && list.size() == 0) {
      writeln("<" + paramString1 + paramString2 + "/>");
      return;
    } 
    writeln("<" + paramString1 + paramString2 + ">");
    this.indentation++;
    for (byte b = 0; b < paramVarArgs.length; b++)
      outputValue(paramVarArgs[b], null, true); 
    while (!list.isEmpty()) {
      Statement statement = (Statement)list.remove(0);
      outputStatement(statement, paramObject, false);
    } 
    this.indentation--;
    writeln("</" + paramString1 + ">");
  }
  
  static Class primitiveTypeFor(Class paramClass) { return (paramClass == Boolean.class) ? boolean.class : ((paramClass == Byte.class) ? byte.class : ((paramClass == Character.class) ? char.class : ((paramClass == Short.class) ? short.class : ((paramClass == Integer.class) ? int.class : ((paramClass == Long.class) ? long.class : ((paramClass == Float.class) ? float.class : ((paramClass == Double.class) ? double.class : ((paramClass == Void.class) ? void.class : null)))))))); }
  
  private class ValueData {
    public int refs = 0;
    
    public boolean marked = false;
    
    public String name = null;
    
    public Expression exp = null;
    
    private ValueData() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\XMLEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */