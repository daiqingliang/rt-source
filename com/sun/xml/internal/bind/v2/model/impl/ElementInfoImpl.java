package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.v2.TODO;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationSource;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.core.Element;
import com.sun.xml.internal.bind.v2.model.core.ElementInfo;
import com.sun.xml.internal.bind.v2.model.core.ElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import com.sun.xml.internal.bind.v2.model.core.TypeRef;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.Location;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.activation.MimeType;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

class ElementInfoImpl<T, C, F, M> extends TypeInfoImpl<T, C, F, M> implements ElementInfo<T, C> {
  private final QName tagName;
  
  private final NonElement<T, C> contentType;
  
  private final T tOfJAXBElementT;
  
  private final T elementType;
  
  private final ClassInfo<T, C> scope;
  
  private final XmlElementDecl anno;
  
  private ElementInfoImpl<T, C, F, M> substitutionHead;
  
  private FinalArrayList<ElementInfoImpl<T, C, F, M>> substitutionMembers;
  
  private final M method;
  
  private final Adapter<T, C> adapter;
  
  private final boolean isCollection;
  
  private final ID id;
  
  private final PropertyImpl property;
  
  private final MimeType expectedMimeType;
  
  private final boolean inlineBinary;
  
  private final QName schemaType;
  
  public ElementInfoImpl(ModelBuilder<T, C, F, M> paramModelBuilder, RegistryInfoImpl<T, C, F, M> paramRegistryInfoImpl, M paramM) throws IllegalAnnotationException {
    super(paramModelBuilder, paramRegistryInfoImpl);
    this.method = paramM;
    this.anno = (XmlElementDecl)reader().getMethodAnnotation(XmlElementDecl.class, paramM, this);
    assert this.anno != null;
    assert this.anno instanceof com.sun.xml.internal.bind.v2.model.annotation.Locatable;
    this.elementType = nav().getReturnType(paramM);
    Object object1 = nav().getBaseClass(this.elementType, nav().asDecl(javax.xml.bind.JAXBElement.class));
    if (object1 == null)
      throw new IllegalAnnotationException(Messages.XML_ELEMENT_MAPPING_ON_NON_IXMLELEMENT_METHOD.format(new Object[] { nav().getMethodName(paramM) }, ), this.anno); 
    this.tagName = parseElementName(this.anno);
    Object[] arrayOfObject = nav().getMethodParameters(paramM);
    Adapter adapter1 = null;
    if (arrayOfObject.length > 0) {
      XmlJavaTypeAdapter xmlJavaTypeAdapter = (XmlJavaTypeAdapter)reader().getMethodAnnotation(XmlJavaTypeAdapter.class, paramM, this);
      if (xmlJavaTypeAdapter != null) {
        adapter1 = new Adapter(xmlJavaTypeAdapter, reader(), nav());
      } else {
        XmlAttachmentRef xmlAttachmentRef = (XmlAttachmentRef)reader().getMethodAnnotation(XmlAttachmentRef.class, paramM, this);
        if (xmlAttachmentRef != null) {
          TODO.prototype("in Annotation Processing swaRefAdapter isn't avaialble, so this returns null");
          adapter1 = new Adapter(this.owner.nav.asDecl(com.sun.xml.internal.bind.v2.runtime.SwaRefAdapter.class), this.owner.nav);
        } 
      } 
    } 
    this.adapter = adapter1;
    this.tOfJAXBElementT = (arrayOfObject.length > 0) ? arrayOfObject[0] : nav().getTypeArgument(object1, 0);
    if (this.adapter == null) {
      Object object = nav().getBaseClass(this.tOfJAXBElementT, nav().asDecl(List.class));
      if (object == null) {
        this.isCollection = false;
        this.contentType = paramModelBuilder.getTypeInfo(this.tOfJAXBElementT, this);
      } else {
        this.isCollection = true;
        this.contentType = paramModelBuilder.getTypeInfo(nav().getTypeArgument(object, 0), this);
      } 
    } else {
      this.contentType = paramModelBuilder.getTypeInfo(this.adapter.defaultType, this);
      this.isCollection = false;
    } 
    Object object2 = reader().getClassValue(this.anno, "scope");
    if (nav().isSameType(object2, nav().ref(XmlElementDecl.GLOBAL.class))) {
      this.scope = null;
    } else {
      NonElement nonElement = paramModelBuilder.getClassInfo(nav().asDecl(object2), this);
      if (!(nonElement instanceof ClassInfo))
        throw new IllegalAnnotationException(Messages.SCOPE_IS_NOT_COMPLEXTYPE.format(new Object[] { nav().getTypeName(object2) }, ), this.anno); 
      this.scope = (ClassInfo)nonElement;
    } 
    this.id = calcId();
    this.property = createPropertyImpl();
    this.expectedMimeType = Util.calcExpectedMediaType(this.property, paramModelBuilder);
    this.inlineBinary = reader().hasMethodAnnotation(javax.xml.bind.annotation.XmlInlineBinaryData.class, this.method);
    this.schemaType = Util.calcSchemaType(reader(), this.property, paramRegistryInfoImpl.registryClass, getContentInMemoryType(), this);
  }
  
  final QName parseElementName(XmlElementDecl paramXmlElementDecl) {
    String str1 = paramXmlElementDecl.name();
    String str2 = paramXmlElementDecl.namespace();
    if (str2.equals("##default")) {
      XmlSchema xmlSchema = (XmlSchema)reader().getPackageAnnotation(XmlSchema.class, nav().getDeclaringClassForMethod(this.method), this);
      if (xmlSchema != null) {
        str2 = xmlSchema.namespace();
      } else {
        str2 = this.builder.defaultNsUri;
      } 
    } 
    return new QName(str2.intern(), str1.intern());
  }
  
  protected PropertyImpl createPropertyImpl() { return new PropertyImpl(); }
  
  public ElementPropertyInfo<T, C> getProperty() { return this.property; }
  
  public NonElement<T, C> getContentType() { return this.contentType; }
  
  public T getContentInMemoryType() { return (this.adapter == null) ? (T)this.tOfJAXBElementT : (T)this.adapter.customType; }
  
  public QName getElementName() { return this.tagName; }
  
  public T getType() { return (T)this.elementType; }
  
  public final boolean canBeReferencedByIDREF() { return false; }
  
  private ID calcId() { return reader().hasMethodAnnotation(javax.xml.bind.annotation.XmlID.class, this.method) ? ID.ID : (reader().hasMethodAnnotation(javax.xml.bind.annotation.XmlIDREF.class, this.method) ? ID.IDREF : ID.NONE); }
  
  public ClassInfo<T, C> getScope() { return this.scope; }
  
  public ElementInfo<T, C> getSubstitutionHead() { return this.substitutionHead; }
  
  public Collection<? extends ElementInfoImpl<T, C, F, M>> getSubstitutionMembers() { return (this.substitutionMembers == null) ? Collections.emptyList() : this.substitutionMembers; }
  
  void link() {
    if (this.anno.substitutionHeadName().length() != 0) {
      QName qName = new QName(this.anno.substitutionHeadNamespace(), this.anno.substitutionHeadName());
      this.substitutionHead = this.owner.getElementInfo(null, qName);
      if (this.substitutionHead == null) {
        this.builder.reportError(new IllegalAnnotationException(Messages.NON_EXISTENT_ELEMENT_MAPPING.format(new Object[] { qName.getNamespaceURI(), qName.getLocalPart() }, ), this.anno));
      } else {
        this.substitutionHead.addSubstitutionMember(this);
      } 
    } else {
      this.substitutionHead = null;
    } 
    super.link();
  }
  
  private void addSubstitutionMember(ElementInfoImpl<T, C, F, M> paramElementInfoImpl) {
    if (this.substitutionMembers == null)
      this.substitutionMembers = new FinalArrayList(); 
    this.substitutionMembers.add(paramElementInfoImpl);
  }
  
  public Location getLocation() { return nav().getMethodLocation(this.method); }
  
  protected class PropertyImpl extends Object implements ElementPropertyInfo<T, C>, TypeRef<T, C>, AnnotationSource {
    public NonElement<T, C> getTarget() { return ElementInfoImpl.this.contentType; }
    
    public QName getTagName() { return ElementInfoImpl.this.tagName; }
    
    public List<? extends TypeRef<T, C>> getTypes() { return Collections.singletonList(this); }
    
    public List<? extends NonElement<T, C>> ref() { return Collections.singletonList(ElementInfoImpl.this.contentType); }
    
    public QName getXmlName() { return ElementInfoImpl.this.tagName; }
    
    public boolean isCollectionRequired() { return false; }
    
    public boolean isCollectionNillable() { return true; }
    
    public boolean isNillable() { return true; }
    
    public String getDefaultValue() {
      String str = ElementInfoImpl.this.anno.defaultValue();
      return str.equals("\000") ? null : str;
    }
    
    public ElementInfoImpl<T, C, F, M> parent() { return ElementInfoImpl.this; }
    
    public String getName() { return "value"; }
    
    public String displayName() { return "JAXBElement#value"; }
    
    public boolean isCollection() { return ElementInfoImpl.this.isCollection; }
    
    public boolean isValueList() { return ElementInfoImpl.this.isCollection; }
    
    public boolean isRequired() { return true; }
    
    public PropertyKind kind() { return PropertyKind.ELEMENT; }
    
    public Adapter<T, C> getAdapter() { return ElementInfoImpl.this.adapter; }
    
    public ID id() { return ElementInfoImpl.this.id; }
    
    public MimeType getExpectedMimeType() { return ElementInfoImpl.this.expectedMimeType; }
    
    public QName getSchemaType() { return ElementInfoImpl.this.schemaType; }
    
    public boolean inlineBinaryData() { return ElementInfoImpl.this.inlineBinary; }
    
    public PropertyInfo<T, C> getSource() { return this; }
    
    public <A extends Annotation> A readAnnotation(Class<A> param1Class) { return (A)ElementInfoImpl.this.reader().getMethodAnnotation(param1Class, ElementInfoImpl.this.method, ElementInfoImpl.this); }
    
    public boolean hasAnnotation(Class<? extends Annotation> param1Class) { return ElementInfoImpl.this.reader().hasMethodAnnotation(param1Class, ElementInfoImpl.this.method); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\ElementInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */