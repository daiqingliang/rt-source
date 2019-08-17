package com.sun.xml.internal.bind.v2.schemagen;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.api.ErrorListener;
import com.sun.xml.internal.bind.v2.TODO;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import com.sun.xml.internal.bind.v2.model.core.ArrayInfo;
import com.sun.xml.internal.bind.v2.model.core.AttributePropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.core.Element;
import com.sun.xml.internal.bind.v2.model.core.ElementInfo;
import com.sun.xml.internal.bind.v2.model.core.ElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.EnumConstant;
import com.sun.xml.internal.bind.v2.model.core.EnumLeafInfo;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.MapPropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.MaybeElement;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.NonElementRef;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.ReferencePropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import com.sun.xml.internal.bind.v2.model.core.TypeInfoSet;
import com.sun.xml.internal.bind.v2.model.core.TypeRef;
import com.sun.xml.internal.bind.v2.model.core.ValuePropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.WildcardMode;
import com.sun.xml.internal.bind.v2.model.impl.ClassInfoImpl;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.schemagen.episode.Bindings;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Any;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.AttrDecls;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.AttributeType;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ComplexExtension;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ComplexType;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ComplexTypeHost;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ContentModelContainer;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ExplicitGroup;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Import;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalAttribute;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalElement;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Schema;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleExtension;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleRestriction;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleType;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleTypeHost;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.TopLevelAttribute;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.TopLevelElement;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.TypeHost;
import com.sun.xml.internal.bind.v2.util.CollisionCheckStack;
import com.sun.xml.internal.bind.v2.util.StackRecorder;
import com.sun.xml.internal.txw2.TXW;
import com.sun.xml.internal.txw2.TxwException;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.output.ResultFactory;
import com.sun.xml.internal.txw2.output.XmlSerializer;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.MimeType;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.SAXParseException;

public final class XmlSchemaGenerator<T, C, F, M> extends Object {
  private static final Logger logger = Util.getClassLogger();
  
  private final Map<String, Namespace> namespaces = new TreeMap(NAMESPACE_COMPARATOR);
  
  private ErrorListener errorListener;
  
  private Navigator<T, C, F, M> navigator;
  
  private final TypeInfoSet<T, C, F, M> types;
  
  private final NonElement<T, C> stringType;
  
  private final NonElement<T, C> anyType;
  
  private final CollisionCheckStack<ClassInfo<T, C>> collisionChecker = new CollisionCheckStack();
  
  private static final Comparator<String> NAMESPACE_COMPARATOR = new Comparator<String>() {
      public int compare(String param1String1, String param1String2) { return -param1String1.compareTo(param1String2); }
    };
  
  private static final String newline = "\n";
  
  public XmlSchemaGenerator(Navigator<T, C, F, M> paramNavigator, TypeInfoSet<T, C, F, M> paramTypeInfoSet) {
    this.navigator = paramNavigator;
    this.types = paramTypeInfoSet;
    this.stringType = paramTypeInfoSet.getTypeInfo(paramNavigator.ref(String.class));
    this.anyType = paramTypeInfoSet.getAnyTypeInfo();
    for (ClassInfo classInfo : paramTypeInfoSet.beans().values())
      add(classInfo); 
    for (ElementInfo elementInfo : paramTypeInfoSet.getElementMappings(null).values())
      add(elementInfo); 
    for (EnumLeafInfo enumLeafInfo : paramTypeInfoSet.enums().values())
      add(enumLeafInfo); 
    for (ArrayInfo arrayInfo : paramTypeInfoSet.arrays().values())
      add(arrayInfo); 
  }
  
  private Namespace getNamespace(String paramString) {
    Namespace namespace = (Namespace)this.namespaces.get(paramString);
    if (namespace == null)
      this.namespaces.put(paramString, namespace = new Namespace(paramString)); 
    return namespace;
  }
  
  public void add(ClassInfo<T, C> paramClassInfo) {
    assert paramClassInfo != null;
    String str = null;
    if (paramClassInfo.getClazz() == this.navigator.asDecl(com.sun.xml.internal.bind.api.CompositeStructure.class))
      return; 
    if (paramClassInfo.isElement()) {
      str = paramClassInfo.getElementName().getNamespaceURI();
      Namespace namespace1;
      namespace1.classes.add(paramClassInfo);
      namespace1.addDependencyTo(paramClassInfo.getTypeName());
      add(paramClassInfo.getElementName(), false, paramClassInfo);
    } 
    QName qName = paramClassInfo.getTypeName();
    if (qName != null) {
      str = qName.getNamespaceURI();
    } else if (str == null) {
      return;
    } 
    Namespace namespace;
    namespace.classes.add(paramClassInfo);
    for (PropertyInfo propertyInfo : paramClassInfo.getProperties()) {
      namespace.processForeignNamespaces(propertyInfo, 1);
      if (propertyInfo instanceof AttributePropertyInfo) {
        AttributePropertyInfo attributePropertyInfo = (AttributePropertyInfo)propertyInfo;
        String str1 = attributePropertyInfo.getXmlName().getNamespaceURI();
        if (str1.length() > 0) {
          getNamespace(str1).addGlobalAttribute(attributePropertyInfo);
          namespace.addDependencyTo(attributePropertyInfo.getXmlName());
        } 
      } 
      if (propertyInfo instanceof ElementPropertyInfo) {
        ElementPropertyInfo elementPropertyInfo = (ElementPropertyInfo)propertyInfo;
        for (TypeRef typeRef : elementPropertyInfo.getTypes()) {
          String str1 = typeRef.getTagName().getNamespaceURI();
          if (str1.length() > 0 && !str1.equals(namespace.uri)) {
            getNamespace(str1).addGlobalElement(typeRef);
            namespace.addDependencyTo(typeRef.getTagName());
          } 
        } 
      } 
      if (generateSwaRefAdapter(propertyInfo))
        namespace.useSwaRef = true; 
      MimeType mimeType = propertyInfo.getExpectedMimeType();
      if (mimeType != null)
        namespace.useMimeNs = true; 
    } 
    ClassInfo classInfo = paramClassInfo.getBaseClass();
    if (classInfo != null) {
      add(classInfo);
      namespace.addDependencyTo(classInfo.getTypeName());
    } 
  }
  
  public void add(ElementInfo<T, C> paramElementInfo) {
    ElementInfo elementInfo;
    assert paramElementInfo != null;
    boolean bool = false;
    QName qName = paramElementInfo.getElementName();
    Namespace namespace = getNamespace(qName.getNamespaceURI());
    if (paramElementInfo.getScope() != null) {
      elementInfo = this.types.getElementInfo(paramElementInfo.getScope().getClazz(), qName);
    } else {
      elementInfo = this.types.getElementInfo(null, qName);
    } 
    XmlElement xmlElement = (XmlElement)elementInfo.getProperty().readAnnotation(XmlElement.class);
    if (xmlElement == null) {
      bool = false;
    } else {
      bool = xmlElement.nillable();
    } 
    namespace.getClass();
    namespace.elementDecls.put(qName.getLocalPart(), new Namespace.ElementWithType(bool, paramElementInfo.getContentType()));
    namespace.processForeignNamespaces(paramElementInfo.getProperty(), 1);
  }
  
  public void add(EnumLeafInfo<T, C> paramEnumLeafInfo) {
    assert paramEnumLeafInfo != null;
    String str = null;
    if (paramEnumLeafInfo.isElement()) {
      str = paramEnumLeafInfo.getElementName().getNamespaceURI();
      Namespace namespace1;
      namespace1.enums.add(paramEnumLeafInfo);
      namespace1.addDependencyTo(paramEnumLeafInfo.getTypeName());
      add(paramEnumLeafInfo.getElementName(), false, paramEnumLeafInfo);
    } 
    QName qName = paramEnumLeafInfo.getTypeName();
    if (qName != null) {
      str = qName.getNamespaceURI();
    } else if (str == null) {
      return;
    } 
    Namespace namespace;
    namespace.enums.add(paramEnumLeafInfo);
    namespace.addDependencyTo(paramEnumLeafInfo.getBaseType().getTypeName());
  }
  
  public void add(ArrayInfo<T, C> paramArrayInfo) {
    assert paramArrayInfo != null;
    String str = paramArrayInfo.getTypeName().getNamespaceURI();
    Namespace namespace;
    namespace.arrays.add(paramArrayInfo);
    namespace.addDependencyTo(paramArrayInfo.getItemType().getTypeName());
  }
  
  public void add(QName paramQName, boolean paramBoolean, NonElement<T, C> paramNonElement) {
    if (paramNonElement != null && paramNonElement.getType() == this.navigator.ref(com.sun.xml.internal.bind.api.CompositeStructure.class))
      return; 
    namespace.getClass();
    Namespace namespace;
    namespace.elementDecls.put(paramQName.getLocalPart(), new Namespace.ElementWithType(paramBoolean, paramNonElement));
    if (paramNonElement != null)
      namespace.addDependencyTo(paramNonElement.getTypeName()); 
  }
  
  public void writeEpisodeFile(XmlSerializer paramXmlSerializer) {
    Bindings bindings = (Bindings)TXW.create(Bindings.class, paramXmlSerializer);
    if (this.namespaces.containsKey(""))
      bindings._namespace("http://java.sun.com/xml/ns/jaxb", "jaxb"); 
    bindings.version("2.1");
    for (Map.Entry entry : this.namespaces.entrySet()) {
      String str1;
      Bindings bindings1 = bindings.bindings();
      String str2 = (String)entry.getKey();
      if (!str2.equals("")) {
        bindings1._namespace(str2, "tns");
        str1 = "tns:";
      } else {
        str1 = "";
      } 
      bindings1.scd("x-schema::" + (str2.equals("") ? "" : "tns"));
      bindings1.schemaBindings().map(false);
      for (ClassInfo classInfo : ((Namespace)entry.getValue()).classes) {
        if (classInfo.getTypeName() == null)
          continue; 
        if (classInfo.getTypeName().getNamespaceURI().equals(str2)) {
          Bindings bindings2 = bindings1.bindings();
          bindings2.scd('~' + str1 + classInfo.getTypeName().getLocalPart());
          bindings2.klass().ref(classInfo.getName());
        } 
        if (classInfo.isElement() && classInfo.getElementName().getNamespaceURI().equals(str2)) {
          Bindings bindings2 = bindings1.bindings();
          bindings2.scd(str1 + classInfo.getElementName().getLocalPart());
          bindings2.klass().ref(classInfo.getName());
        } 
      } 
      for (EnumLeafInfo enumLeafInfo : ((Namespace)entry.getValue()).enums) {
        if (enumLeafInfo.getTypeName() == null)
          continue; 
        Bindings bindings2 = bindings1.bindings();
        bindings2.scd('~' + str1 + enumLeafInfo.getTypeName().getLocalPart());
        bindings2.klass().ref(this.navigator.getClassName(enumLeafInfo.getClazz()));
      } 
      bindings1.commit(true);
    } 
    bindings.commit();
  }
  
  public void write(SchemaOutputResolver paramSchemaOutputResolver, ErrorListener paramErrorListener) throws IOException {
    if (paramSchemaOutputResolver == null)
      throw new IllegalArgumentException(); 
    if (logger.isLoggable(Level.FINE))
      logger.log(Level.FINE, "Writing XML Schema for " + toString(), new StackRecorder()); 
    paramSchemaOutputResolver = new FoolProofResolver(paramSchemaOutputResolver);
    this.errorListener = paramErrorListener;
    Map map = this.types.getSchemaLocations();
    HashMap hashMap1 = new HashMap();
    HashMap hashMap2 = new HashMap();
    this.namespaces.remove("http://www.w3.org/2001/XMLSchema");
    for (Namespace namespace : this.namespaces.values()) {
      String str = (String)map.get(namespace.uri);
      if (str != null) {
        hashMap2.put(namespace, str);
      } else {
        Result result = paramSchemaOutputResolver.createOutput(namespace.uri, "schema" + (hashMap1.size() + 1) + ".xsd");
        if (result != null) {
          hashMap1.put(namespace, result);
          hashMap2.put(namespace, result.getSystemId());
        } 
      } 
      namespace.resetWritten();
    } 
    for (Map.Entry entry : hashMap1.entrySet()) {
      Result result = (Result)entry.getValue();
      ((Namespace)entry.getKey()).writeTo(result, hashMap2);
      if (result instanceof StreamResult) {
        OutputStream outputStream = ((StreamResult)result).getOutputStream();
        if (outputStream != null) {
          outputStream.close();
          continue;
        } 
        Writer writer = ((StreamResult)result).getWriter();
        if (writer != null)
          writer.close(); 
      } 
    } 
  }
  
  private boolean generateSwaRefAdapter(NonElementRef<T, C> paramNonElementRef) { return generateSwaRefAdapter(paramNonElementRef.getSource()); }
  
  private boolean generateSwaRefAdapter(PropertyInfo<T, C> paramPropertyInfo) {
    Adapter adapter = paramPropertyInfo.getAdapter();
    if (adapter == null)
      return false; 
    Object object = this.navigator.asDecl(com.sun.xml.internal.bind.v2.runtime.SwaRefAdapter.class);
    return (object == null) ? false : object.equals(adapter.adapterType);
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (Namespace namespace : this.namespaces.values()) {
      if (stringBuilder.length() > 0)
        stringBuilder.append(','); 
      stringBuilder.append(namespace.uri).append('=').append(namespace);
    } 
    return super.toString() + '[' + stringBuilder + ']';
  }
  
  private static String getProcessContentsModeName(WildcardMode paramWildcardMode) {
    switch (paramWildcardMode) {
      case LAX:
      case SKIP:
        return paramWildcardMode.name().toLowerCase();
      case STRICT:
        return null;
    } 
    throw new IllegalStateException();
  }
  
  protected static String relativize(String paramString1, String paramString2) {
    try {
      assert paramString1 != null;
      if (paramString2 == null)
        return paramString1; 
      URI uRI1 = new URI(Util.escapeURI(paramString1));
      URI uRI2 = new URI(Util.escapeURI(paramString2));
      if (uRI1.isOpaque() || uRI2.isOpaque())
        return paramString1; 
      if (!Util.equalsIgnoreCase(uRI1.getScheme(), uRI2.getScheme()) || !Util.equal(uRI1.getAuthority(), uRI2.getAuthority()))
        return paramString1; 
      String str1 = uRI1.getPath();
      String str2 = uRI2.getPath();
      if (!str2.endsWith("/"))
        str2 = Util.normalizeUriPath(str2); 
      if (str1.equals(str2))
        return "."; 
      String str3 = calculateRelativePath(str1, str2, fixNull(uRI1.getScheme()).equals("file"));
      if (str3 == null)
        return paramString1; 
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(str3);
      if (uRI1.getQuery() != null)
        stringBuilder.append('?').append(uRI1.getQuery()); 
      if (uRI1.getFragment() != null)
        stringBuilder.append('#').append(uRI1.getFragment()); 
      return stringBuilder.toString();
    } catch (URISyntaxException uRISyntaxException) {
      throw new InternalError("Error escaping one of these uris:\n\t" + paramString1 + "\n\t" + paramString2);
    } 
  }
  
  private static String fixNull(String paramString) { return (paramString == null) ? "" : paramString; }
  
  private static String calculateRelativePath(String paramString1, String paramString2, boolean paramBoolean) {
    boolean bool = (File.pathSeparatorChar == ';') ? 1 : 0;
    return (paramString2 == null) ? null : (((paramBoolean && bool && startsWithIgnoreCase(paramString1, paramString2)) || paramString1.startsWith(paramString2)) ? paramString1.substring(paramString2.length()) : ("../" + calculateRelativePath(paramString1, Util.getParentUriPath(paramString2), paramBoolean)));
  }
  
  private static boolean startsWithIgnoreCase(String paramString1, String paramString2) { return paramString1.toUpperCase().startsWith(paramString2.toUpperCase()); }
  
  private class Namespace {
    @NotNull
    final String uri;
    
    private final Set<Namespace> depends = new LinkedHashSet();
    
    private boolean selfReference;
    
    private final Set<ClassInfo<T, C>> classes = new LinkedHashSet();
    
    private final Set<EnumLeafInfo<T, C>> enums = new LinkedHashSet();
    
    private final Set<ArrayInfo<T, C>> arrays = new LinkedHashSet();
    
    private final MultiMap<String, AttributePropertyInfo<T, C>> attributeDecls = new MultiMap(null);
    
    private final MultiMap<String, XmlSchemaGenerator<T, C, F, M>.Namespace.ElementDeclaration> elementDecls = new MultiMap(new ElementWithType(this, true, XmlSchemaGenerator.this.anyType));
    
    private Form attributeFormDefault;
    
    private Form elementFormDefault;
    
    private boolean useSwaRef;
    
    private boolean useMimeNs;
    
    private final Set<ClassInfo> written = new HashSet();
    
    public Namespace(String param1String) {
      this.uri = param1String;
      assert !this$0.namespaces.containsKey(param1String);
      this$0.namespaces.put(param1String, this);
    }
    
    void resetWritten() { this.written.clear(); }
    
    private void processForeignNamespaces(PropertyInfo<T, C> param1PropertyInfo, int param1Int) {
      for (TypeInfo typeInfo : param1PropertyInfo.ref()) {
        if (typeInfo instanceof ClassInfo && param1Int > 0) {
          List list = ((ClassInfo)typeInfo).getProperties();
          for (PropertyInfo propertyInfo : list)
            processForeignNamespaces(propertyInfo, --param1Int); 
        } 
        if (typeInfo instanceof Element)
          addDependencyTo(((Element)typeInfo).getElementName()); 
        if (typeInfo instanceof NonElement)
          addDependencyTo(((NonElement)typeInfo).getTypeName()); 
      } 
    }
    
    private void addDependencyTo(@Nullable QName param1QName) {
      if (param1QName == null)
        return; 
      String str = param1QName.getNamespaceURI();
      if (str.equals("http://www.w3.org/2001/XMLSchema"))
        return; 
      if (str.equals(this.uri)) {
        this.selfReference = true;
        return;
      } 
      this.depends.add(XmlSchemaGenerator.this.getNamespace(str));
    }
    
    private void writeTo(Result param1Result, Map<Namespace, String> param1Map) throws IOException {
      try {
        Schema schema = (Schema)TXW.create(Schema.class, ResultFactory.createSerializer(param1Result));
        Map map = XmlSchemaGenerator.this.types.getXmlNs(this.uri);
        for (Map.Entry entry : map.entrySet())
          schema._namespace((String)entry.getValue(), (String)entry.getKey()); 
        if (this.useSwaRef)
          schema._namespace("http://ws-i.org/profiles/basic/1.1/xsd", "swaRef"); 
        if (this.useMimeNs)
          schema._namespace("http://www.w3.org/2005/05/xmlmime", "xmime"); 
        this.attributeFormDefault = Form.get(XmlSchemaGenerator.this.types.getAttributeFormDefault(this.uri));
        this.attributeFormDefault.declare("attributeFormDefault", schema);
        this.elementFormDefault = Form.get(XmlSchemaGenerator.this.types.getElementFormDefault(this.uri));
        this.elementFormDefault.declare("elementFormDefault", schema);
        if (!map.containsValue("http://www.w3.org/2001/XMLSchema") && !map.containsKey("xs"))
          schema._namespace("http://www.w3.org/2001/XMLSchema", "xs"); 
        schema.version("1.0");
        if (this.uri.length() != 0)
          schema.targetNamespace(this.uri); 
        for (Namespace namespace : this.depends)
          schema._namespace(namespace.uri); 
        if (this.selfReference && this.uri.length() != 0)
          schema._namespace(this.uri, "tns"); 
        schema._pcdata("\n");
        for (Namespace namespace : this.depends) {
          Import import = schema._import();
          if (namespace.uri.length() != 0)
            import.namespace(namespace.uri); 
          String str = (String)param1Map.get(namespace);
          if (str != null && !str.equals(""))
            import.schemaLocation(XmlSchemaGenerator.relativize(str, param1Result.getSystemId())); 
          schema._pcdata("\n");
        } 
        if (this.useSwaRef)
          schema._import().namespace("http://ws-i.org/profiles/basic/1.1/xsd").schemaLocation("http://ws-i.org/profiles/basic/1.1/swaref.xsd"); 
        if (this.useMimeNs)
          schema._import().namespace("http://www.w3.org/2005/05/xmlmime").schemaLocation("http://www.w3.org/2005/05/xmlmime"); 
        for (Map.Entry entry : this.elementDecls.entrySet()) {
          ((ElementDeclaration)entry.getValue()).writeTo((String)entry.getKey(), schema);
          schema._pcdata("\n");
        } 
        for (ClassInfo classInfo : this.classes) {
          if (classInfo.getTypeName() == null)
            continue; 
          if (this.uri.equals(classInfo.getTypeName().getNamespaceURI()))
            writeClass(classInfo, schema); 
          schema._pcdata("\n");
        } 
        for (EnumLeafInfo enumLeafInfo : this.enums) {
          if (enumLeafInfo.getTypeName() == null)
            continue; 
          if (this.uri.equals(enumLeafInfo.getTypeName().getNamespaceURI()))
            writeEnum(enumLeafInfo, schema); 
          schema._pcdata("\n");
        } 
        for (ArrayInfo arrayInfo : this.arrays) {
          writeArray(arrayInfo, schema);
          schema._pcdata("\n");
        } 
        for (Map.Entry entry : this.attributeDecls.entrySet()) {
          TopLevelAttribute topLevelAttribute = schema.attribute();
          topLevelAttribute.name((String)entry.getKey());
          if (entry.getValue() == null) {
            writeTypeRef(topLevelAttribute, XmlSchemaGenerator.this.stringType, "type");
          } else {
            writeAttributeTypeRef((AttributePropertyInfo)entry.getValue(), topLevelAttribute);
          } 
          schema._pcdata("\n");
        } 
        schema.commit();
      } catch (TxwException txwException) {
        logger.log(Level.INFO, txwException.getMessage(), txwException);
        throw new IOException(txwException.getMessage());
      } 
    }
    
    private void writeTypeRef(TypeHost param1TypeHost, NonElementRef<T, C> param1NonElementRef, String param1String) {
      switch (param1NonElementRef.getSource().id()) {
        case LAX:
          param1TypeHost._attribute(param1String, new QName("http://www.w3.org/2001/XMLSchema", "ID"));
          return;
        case SKIP:
          param1TypeHost._attribute(param1String, new QName("http://www.w3.org/2001/XMLSchema", "IDREF"));
          return;
        case STRICT:
          break;
        default:
          throw new IllegalStateException();
      } 
      MimeType mimeType = param1NonElementRef.getSource().getExpectedMimeType();
      if (mimeType != null)
        param1TypeHost._attribute(new QName("http://www.w3.org/2005/05/xmlmime", "expectedContentTypes", "xmime"), mimeType.toString()); 
      if (XmlSchemaGenerator.this.generateSwaRefAdapter(param1NonElementRef)) {
        param1TypeHost._attribute(param1String, new QName("http://ws-i.org/profiles/basic/1.1/xsd", "swaRef", "ref"));
        return;
      } 
      if (param1NonElementRef.getSource().getSchemaType() != null) {
        param1TypeHost._attribute(param1String, param1NonElementRef.getSource().getSchemaType());
        return;
      } 
      writeTypeRef(param1TypeHost, param1NonElementRef.getTarget(), param1String);
    }
    
    private void writeTypeRef(TypeHost param1TypeHost, NonElement<T, C> param1NonElement, String param1String) {
      Element element = null;
      if (param1NonElement instanceof MaybeElement) {
        MaybeElement maybeElement = (MaybeElement)param1NonElement;
        boolean bool = maybeElement.isElement();
        if (bool)
          element = maybeElement.asElement(); 
      } 
      if (param1NonElement instanceof Element)
        element = (Element)param1NonElement; 
      if (param1NonElement.getTypeName() == null) {
        if (element != null && element.getElementName() != null) {
          param1TypeHost.block();
          if (param1NonElement instanceof ClassInfo) {
            writeClass((ClassInfo)param1NonElement, param1TypeHost);
          } else {
            writeEnum((EnumLeafInfo)param1NonElement, (SimpleTypeHost)param1TypeHost);
          } 
        } else {
          param1TypeHost.block();
          if (param1NonElement instanceof ClassInfo) {
            if (XmlSchemaGenerator.this.collisionChecker.push((ClassInfo)param1NonElement)) {
              XmlSchemaGenerator.this.errorListener.warning(new SAXParseException(Messages.ANONYMOUS_TYPE_CYCLE.format(new Object[] { XmlSchemaGenerator.access$1600(XmlSchemaGenerator.this).getCycleString() }, ), null));
            } else {
              writeClass((ClassInfo)param1NonElement, param1TypeHost);
            } 
            XmlSchemaGenerator.this.collisionChecker.pop();
          } else {
            writeEnum((EnumLeafInfo)param1NonElement, (SimpleTypeHost)param1TypeHost);
          } 
        } 
      } else {
        param1TypeHost._attribute(param1String, param1NonElement.getTypeName());
      } 
    }
    
    private void writeArray(ArrayInfo<T, C> param1ArrayInfo, Schema param1Schema) {
      ComplexType complexType = param1Schema.complexType().name(param1ArrayInfo.getTypeName().getLocalPart());
      complexType._final("#all");
      LocalElement localElement = complexType.sequence().element().name("item");
      localElement.type(param1ArrayInfo.getItemType().getTypeName());
      localElement.minOccurs(0).maxOccurs("unbounded");
      localElement.nillable(true);
      complexType.commit();
    }
    
    private void writeEnum(EnumLeafInfo<T, C> param1EnumLeafInfo, SimpleTypeHost param1SimpleTypeHost) {
      SimpleType simpleType = param1SimpleTypeHost.simpleType();
      writeName(param1EnumLeafInfo, simpleType);
      SimpleRestriction simpleRestriction = simpleType.restriction();
      writeTypeRef(simpleRestriction, param1EnumLeafInfo.getBaseType(), "base");
      for (EnumConstant enumConstant : param1EnumLeafInfo.getConstants())
        simpleRestriction.enumeration().value(enumConstant.getLexicalValue()); 
      simpleType.commit();
    }
    
    private void writeClass(ClassInfo<T, C> param1ClassInfo, TypeHost param1TypeHost) {
      if (this.written.contains(param1ClassInfo))
        return; 
      this.written.add(param1ClassInfo);
      if (containsValueProp(param1ClassInfo)) {
        if (param1ClassInfo.getProperties().size() == 1) {
          ValuePropertyInfo valuePropertyInfo = (ValuePropertyInfo)param1ClassInfo.getProperties().get(0);
          SimpleType simpleType = ((SimpleTypeHost)param1TypeHost).simpleType();
          writeName(param1ClassInfo, simpleType);
          if (valuePropertyInfo.isCollection()) {
            writeTypeRef(simpleType.list(), valuePropertyInfo.getTarget(), "itemType");
          } else {
            writeTypeRef(simpleType.restriction(), valuePropertyInfo.getTarget(), "base");
          } 
          return;
        } 
        ComplexType complexType1 = ((ComplexTypeHost)param1TypeHost).complexType();
        writeName(param1ClassInfo, complexType1);
        if (param1ClassInfo.isFinal())
          complexType1._final("extension restriction"); 
        SimpleExtension simpleExtension = complexType1.simpleContent().extension();
        simpleExtension.block();
        for (PropertyInfo propertyInfo : param1ClassInfo.getProperties()) {
          ValuePropertyInfo valuePropertyInfo;
          switch (propertyInfo.kind()) {
            case LAX:
              handleAttributeProp((AttributePropertyInfo)propertyInfo, simpleExtension);
              continue;
            case SKIP:
              TODO.checkSpec("what if vp.isCollection() == true?");
              valuePropertyInfo = (ValuePropertyInfo)propertyInfo;
              simpleExtension.base(valuePropertyInfo.getTarget().getTypeName());
              continue;
          } 
          assert false;
          throw new IllegalStateException();
        } 
        simpleExtension.commit();
        TODO.schemaGenerator("figure out what to do if bc != null");
        TODO.checkSpec("handle sec 8.9.5.2, bullet #4");
        return;
      } 
      ComplexType complexType = ((ComplexTypeHost)param1TypeHost).complexType();
      writeName(param1ClassInfo, complexType);
      if (param1ClassInfo.isFinal())
        complexType._final("extension restriction"); 
      if (param1ClassInfo.isAbstract())
        complexType._abstract(true); 
      ComplexExtension complexExtension1 = complexType;
      ComplexExtension complexExtension2 = complexType;
      ClassInfo classInfo = param1ClassInfo.getBaseClass();
      if (classInfo != null)
        if (classInfo.hasValueProperty()) {
          SimpleExtension simpleExtension2 = complexType.simpleContent().extension();
          SimpleExtension simpleExtension1 = simpleExtension2;
          complexExtension2 = null;
          simpleExtension2.base(classInfo.getTypeName());
        } else {
          ComplexExtension complexExtension = complexType.complexContent().extension();
          complexExtension1 = complexExtension;
          complexExtension2 = complexExtension;
          complexExtension.base(classInfo.getTypeName());
        }  
      if (complexExtension2 != null) {
        ArrayList arrayList = new ArrayList();
        for (PropertyInfo propertyInfo : param1ClassInfo.getProperties()) {
          if (propertyInfo instanceof ReferencePropertyInfo && ((ReferencePropertyInfo)propertyInfo).isMixed())
            complexType.mixed(true); 
          Tree tree1 = buildPropertyContentModel(propertyInfo);
          if (tree1 != null)
            arrayList.add(tree1); 
        } 
        Tree tree = Tree.makeGroup(param1ClassInfo.isOrdered() ? GroupKind.SEQUENCE : GroupKind.ALL, arrayList);
        tree.write(complexExtension2);
      } 
      for (PropertyInfo propertyInfo : param1ClassInfo.getProperties()) {
        if (propertyInfo instanceof AttributePropertyInfo)
          handleAttributeProp((AttributePropertyInfo)propertyInfo, complexExtension1); 
      } 
      if (param1ClassInfo.hasAttributeWildcard())
        complexExtension1.anyAttribute().namespace("##other").processContents("skip"); 
      complexType.commit();
    }
    
    private void writeName(NonElement<T, C> param1NonElement, TypedXmlWriter param1TypedXmlWriter) {
      QName qName = param1NonElement.getTypeName();
      if (qName != null)
        param1TypedXmlWriter._attribute("name", qName.getLocalPart()); 
    }
    
    private boolean containsValueProp(ClassInfo<T, C> param1ClassInfo) {
      for (PropertyInfo propertyInfo : param1ClassInfo.getProperties()) {
        if (propertyInfo instanceof ValuePropertyInfo)
          return true; 
      } 
      return false;
    }
    
    private Tree buildPropertyContentModel(PropertyInfo<T, C> param1PropertyInfo) {
      switch (param1PropertyInfo.kind()) {
        case STRICT:
          return handleElementProp((ElementPropertyInfo)param1PropertyInfo);
        case LAX:
          return null;
        case null:
          return handleReferenceProp((ReferencePropertyInfo)param1PropertyInfo);
        case null:
          return handleMapProp((MapPropertyInfo)param1PropertyInfo);
        case SKIP:
          assert false;
          throw new IllegalStateException();
      } 
      assert false;
      throw new IllegalStateException();
    }
    
    private Tree handleElementProp(final ElementPropertyInfo<T, C> ep) {
      if (param1ElementPropertyInfo.isValueList())
        return new Tree.Term() {
            protected void write(ContentModelContainer param2ContentModelContainer, boolean param2Boolean1, boolean param2Boolean2) {
              TypeRef typeRef = (TypeRef)ep.getTypes().get(0);
              LocalElement localElement = param2ContentModelContainer.element();
              localElement.block();
              QName qName = typeRef.getTagName();
              localElement.name(qName.getLocalPart());
              List list = localElement.simpleType().list();
              XmlSchemaGenerator.Namespace.this.writeTypeRef(list, typeRef, "itemType");
              XmlSchemaGenerator.Namespace.this.elementFormDefault.writeForm(localElement, qName);
              writeOccurs(localElement, (param2Boolean1 || !ep.isRequired()), param2Boolean2);
            }
          }; 
      ArrayList arrayList = new ArrayList();
      for (TypeRef typeRef : param1ElementPropertyInfo.getTypes()) {
        arrayList.add(new Tree.Term() {
              protected void write(ContentModelContainer param2ContentModelContainer, boolean param2Boolean1, boolean param2Boolean2) {
                LocalElement localElement = param2ContentModelContainer.element();
                QName qName = t.getTagName();
                PropertyInfo propertyInfo = t.getSource();
                TypeInfo typeInfo = (propertyInfo == null) ? null : propertyInfo.parent();
                if (XmlSchemaGenerator.Namespace.this.canBeDirectElementRef(t, qName, typeInfo)) {
                  if (!t.getTarget().isSimpleType() && t.getTarget() instanceof ClassInfo && XmlSchemaGenerator.Namespace.this.this$0.collisionChecker.findDuplicate((ClassInfo)t.getTarget())) {
                    localElement.ref(new QName(XmlSchemaGenerator.Namespace.this.uri, qName.getLocalPart()));
                  } else {
                    QName qName1 = null;
                    if (t.getTarget() instanceof Element) {
                      Element element = (Element)t.getTarget();
                      qName1 = element.getElementName();
                    } 
                    Collection collection = propertyInfo.ref();
                    TypeInfo typeInfo1;
                    if (collection != null && !collection.isEmpty() && qName1 != null && ((typeInfo1 = (TypeInfo)collection.iterator().next()) == null || typeInfo1 instanceof ClassInfoImpl)) {
                      ClassInfoImpl classInfoImpl = (ClassInfoImpl)typeInfo1;
                      if (classInfoImpl != null && classInfoImpl.getElementName() != null) {
                        localElement.ref(new QName(classInfoImpl.getElementName().getNamespaceURI(), qName.getLocalPart()));
                      } else {
                        localElement.ref(new QName("", qName.getLocalPart()));
                      } 
                    } else {
                      localElement.ref(qName);
                    } 
                  } 
                } else {
                  localElement.name(qName.getLocalPart());
                  XmlSchemaGenerator.Namespace.this.writeTypeRef(localElement, t, "type");
                  XmlSchemaGenerator.Namespace.this.elementFormDefault.writeForm(localElement, qName);
                } 
                if (t.isNillable())
                  localElement.nillable(true); 
                if (t.getDefaultValue() != null)
                  localElement._default(t.getDefaultValue()); 
                writeOccurs(localElement, param2Boolean1, param2Boolean2);
              }
            });
      } 
      final Tree choice = Tree.makeGroup(GroupKind.CHOICE, arrayList).makeOptional(!param1ElementPropertyInfo.isRequired()).makeRepeated(param1ElementPropertyInfo.isCollection());
      final QName ename = param1ElementPropertyInfo.getXmlName();
      return (qName != null) ? new Tree.Term() {
          protected void write(ContentModelContainer param2ContentModelContainer, boolean param2Boolean1, boolean param2Boolean2) {
            LocalElement localElement = param2ContentModelContainer.element();
            if (ename.getNamespaceURI().length() > 0 && !ename.getNamespaceURI().equals(XmlSchemaGenerator.Namespace.this.uri)) {
              localElement.ref(new QName(ename.getNamespaceURI(), ename.getLocalPart()));
              return;
            } 
            localElement.name(ename.getLocalPart());
            XmlSchemaGenerator.Namespace.this.elementFormDefault.writeForm(localElement, ename);
            if (ep.isCollectionNillable())
              localElement.nillable(true); 
            writeOccurs(localElement, !ep.isCollectionRequired(), param2Boolean2);
            ComplexType complexType = localElement.complexType();
            choice.write(complexType);
          }
        } : tree;
    }
    
    private boolean canBeDirectElementRef(TypeRef<T, C> param1TypeRef, QName param1QName, TypeInfo param1TypeInfo) {
      Element element = null;
      ClassInfo classInfo = null;
      QName qName = null;
      if (param1TypeRef.isNillable() || param1TypeRef.getDefaultValue() != null)
        return false; 
      if (param1TypeRef.getTarget() instanceof Element) {
        element = (Element)param1TypeRef.getTarget();
        qName = element.getElementName();
        if (element instanceof ClassInfo)
          classInfo = (ClassInfo)element; 
      } 
      String str = param1QName.getNamespaceURI();
      return (!str.equals(this.uri) && str.length() > 0 && (!(param1TypeInfo instanceof ClassInfo) || ((ClassInfo)param1TypeInfo).getTypeName() != null)) ? true : ((classInfo != null && qName != null && element.getScope() == null && qName.getNamespaceURI() == null && qName.equals(param1QName)) ? true : ((element != null) ? ((qName != null && qName.equals(param1QName))) : false));
    }
    
    private void handleAttributeProp(AttributePropertyInfo<T, C> param1AttributePropertyInfo, AttrDecls param1AttrDecls) {
      LocalAttribute localAttribute = param1AttrDecls.attribute();
      String str = param1AttributePropertyInfo.getXmlName().getNamespaceURI();
      if (str.equals("")) {
        localAttribute.name(param1AttributePropertyInfo.getXmlName().getLocalPart());
        writeAttributeTypeRef(param1AttributePropertyInfo, localAttribute);
        this.attributeFormDefault.writeForm(localAttribute, param1AttributePropertyInfo.getXmlName());
      } else {
        localAttribute.ref(param1AttributePropertyInfo.getXmlName());
      } 
      if (param1AttributePropertyInfo.isRequired())
        localAttribute.use("required"); 
    }
    
    private void writeAttributeTypeRef(AttributePropertyInfo<T, C> param1AttributePropertyInfo, AttributeType param1AttributeType) {
      if (param1AttributePropertyInfo.isCollection()) {
        writeTypeRef(param1AttributeType.simpleType().list(), param1AttributePropertyInfo, "itemType");
      } else {
        writeTypeRef(param1AttributeType, param1AttributePropertyInfo, "type");
      } 
    }
    
    private Tree handleReferenceProp(final ReferencePropertyInfo<T, C> rp) {
      ArrayList arrayList = new ArrayList();
      for (Element element : param1ReferencePropertyInfo.getElements()) {
        arrayList.add(new Tree.Term() {
              protected void write(ContentModelContainer param2ContentModelContainer, boolean param2Boolean1, boolean param2Boolean2) {
                LocalElement localElement = param2ContentModelContainer.element();
                boolean bool = false;
                QName qName = e.getElementName();
                if (e.getScope() != null) {
                  boolean bool1 = qName.getNamespaceURI().equals(XmlSchemaGenerator.Namespace.this.uri);
                  boolean bool2 = qName.getNamespaceURI().equals("");
                  if (bool1 || bool2) {
                    if (bool2) {
                      if (this.this$1.elementFormDefault.isEffectivelyQualified)
                        localElement.form("unqualified"); 
                    } else if (!this.this$1.elementFormDefault.isEffectivelyQualified) {
                      localElement.form("qualified");
                    } 
                    bool = true;
                    localElement.name(qName.getLocalPart());
                    if (e instanceof ClassInfo) {
                      XmlSchemaGenerator.Namespace.this.writeTypeRef(localElement, (ClassInfo)e, "type");
                    } else {
                      XmlSchemaGenerator.Namespace.this.writeTypeRef(localElement, ((ElementInfo)e).getContentType(), "type");
                    } 
                  } 
                } 
                if (!bool)
                  localElement.ref(qName); 
                writeOccurs(localElement, param2Boolean1, param2Boolean2);
              }
            });
      } 
      final WildcardMode wc = param1ReferencePropertyInfo.getWildcard();
      if (wildcardMode != null)
        arrayList.add(new Tree.Term() {
              protected void write(ContentModelContainer param2ContentModelContainer, boolean param2Boolean1, boolean param2Boolean2) {
                Any any = param2ContentModelContainer.any();
                String str = XmlSchemaGenerator.getProcessContentsModeName(wc);
                if (str != null)
                  any.processContents(str); 
                any.namespace("##other");
                writeOccurs(any, param2Boolean1, param2Boolean2);
              }
            }); 
      final Tree choice = Tree.makeGroup(GroupKind.CHOICE, arrayList).makeRepeated(param1ReferencePropertyInfo.isCollection()).makeOptional(!param1ReferencePropertyInfo.isRequired());
      final QName ename = param1ReferencePropertyInfo.getXmlName();
      return (qName != null) ? new Tree.Term() {
          protected void write(ContentModelContainer param2ContentModelContainer, boolean param2Boolean1, boolean param2Boolean2) {
            LocalElement localElement = param2ContentModelContainer.element().name(ename.getLocalPart());
            XmlSchemaGenerator.Namespace.this.elementFormDefault.writeForm(localElement, ename);
            if (rp.isCollectionNillable())
              localElement.nillable(true); 
            writeOccurs(localElement, true, param2Boolean2);
            ComplexType complexType = localElement.complexType();
            choice.write(complexType);
          }
        } : tree;
    }
    
    private Tree handleMapProp(final MapPropertyInfo<T, C> mp) { return new Tree.Term() {
          protected void write(ContentModelContainer param2ContentModelContainer, boolean param2Boolean1, boolean param2Boolean2) {
            QName qName = mp.getXmlName();
            LocalElement localElement = param2ContentModelContainer.element();
            XmlSchemaGenerator.Namespace.this.elementFormDefault.writeForm(localElement, qName);
            if (mp.isCollectionNillable())
              localElement.nillable(true); 
            localElement = localElement.name(qName.getLocalPart());
            writeOccurs(localElement, param2Boolean1, param2Boolean2);
            ComplexType complexType = localElement.complexType();
            localElement = complexType.sequence().element();
            localElement.name("entry").minOccurs(0).maxOccurs("unbounded");
            ExplicitGroup explicitGroup = localElement.complexType().sequence();
            XmlSchemaGenerator.Namespace.this.writeKeyOrValue(explicitGroup, "key", mp.getKeyType());
            XmlSchemaGenerator.Namespace.this.writeKeyOrValue(explicitGroup, "value", mp.getValueType());
          }
        }; }
    
    private void writeKeyOrValue(ExplicitGroup param1ExplicitGroup, String param1String, NonElement<T, C> param1NonElement) {
      LocalElement localElement = param1ExplicitGroup.element().name(param1String);
      localElement.minOccurs(0);
      writeTypeRef(localElement, param1NonElement, "type");
    }
    
    public void addGlobalAttribute(AttributePropertyInfo<T, C> param1AttributePropertyInfo) {
      this.attributeDecls.put(param1AttributePropertyInfo.getXmlName().getLocalPart(), param1AttributePropertyInfo);
      addDependencyTo(param1AttributePropertyInfo.getTarget().getTypeName());
    }
    
    public void addGlobalElement(TypeRef<T, C> param1TypeRef) {
      this.elementDecls.put(param1TypeRef.getTagName().getLocalPart(), new ElementWithType(false, param1TypeRef.getTarget()));
      addDependencyTo(param1TypeRef.getTarget().getTypeName());
    }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("[classes=").append(this.classes);
      stringBuilder.append(",elementDecls=").append(this.elementDecls);
      stringBuilder.append(",enums=").append(this.enums);
      stringBuilder.append("]");
      return super.toString();
    }
    
    abstract class ElementDeclaration {
      public abstract boolean equals(Object param2Object);
      
      public abstract int hashCode();
      
      public abstract void writeTo(String param2String, Schema param2Schema);
    }
    
    class ElementWithType extends XmlSchemaGenerator<T, C, F, M>.Namespace.ElementDeclaration {
      private final boolean nillable;
      
      private final NonElement<T, C> type;
      
      public ElementWithType(boolean param2Boolean, NonElement<T, C> param2NonElement) {
        super(XmlSchemaGenerator.Namespace.this);
        this.type = param2NonElement;
        this.nillable = param2Boolean;
      }
      
      public void writeTo(String param2String, Schema param2Schema) {
        TopLevelElement topLevelElement = param2Schema.element().name(param2String);
        if (this.nillable)
          topLevelElement.nillable(true); 
        if (this.type != null) {
          XmlSchemaGenerator.Namespace.this.writeTypeRef(topLevelElement, this.type, "type");
        } else {
          topLevelElement.complexType();
        } 
        topLevelElement.commit();
      }
      
      public boolean equals(Object param2Object) {
        if (this == param2Object)
          return true; 
        if (param2Object == null || getClass() != param2Object.getClass())
          return false; 
        ElementWithType elementWithType = (ElementWithType)param2Object;
        return this.type.equals(elementWithType.type);
      }
      
      public int hashCode() { return this.type.hashCode(); }
    }
  }
  
  class ElementWithType extends Namespace.ElementDeclaration {
    private final boolean nillable;
    
    private final NonElement<T, C> type;
    
    public ElementWithType(boolean param1Boolean, NonElement<T, C> param1NonElement) {
      super(XmlSchemaGenerator.this);
      this.type = param1NonElement;
      this.nillable = param1Boolean;
    }
    
    public void writeTo(String param1String, Schema param1Schema) {
      TopLevelElement topLevelElement = param1Schema.element().name(param1String);
      if (this.nillable)
        topLevelElement.nillable(true); 
      if (this.type != null) {
        this.this$1.writeTypeRef(topLevelElement, this.type, "type");
      } else {
        topLevelElement.complexType();
      } 
      topLevelElement.commit();
    }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (param1Object == null || getClass() != param1Object.getClass())
        return false; 
      ElementWithType elementWithType = (ElementWithType)param1Object;
      return this.type.equals(elementWithType.type);
    }
    
    public int hashCode() { return this.type.hashCode(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\XmlSchemaGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */