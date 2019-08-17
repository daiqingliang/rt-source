package com.sun.org.apache.xalan.internal.xsltc.runtime;

import java.util.ListResourceBundle;

public class ErrorMessages extends ListResourceBundle {
  public Object[][] getContents() { return new Object[][] { 
        { "RUN_TIME_INTERNAL_ERR", "Run-time internal error in ''{0}''" }, { "RUN_TIME_COPY_ERR", "Run-time error when executing <xsl:copy>." }, { "DATA_CONVERSION_ERR", "Invalid conversion from ''{0}'' to ''{1}''." }, { "EXTERNAL_FUNC_ERR", "External function ''{0}'' not supported by XSLTC." }, { "EQUALITY_EXPR_ERR", "Unknown argument type in equality expression." }, { "INVALID_ARGUMENT_ERR", "Invalid argument type ''{0}'' in call to ''{1}''" }, { "FORMAT_NUMBER_ERR", "Attempting to format number ''{0}'' using pattern ''{1}''." }, { "ITERATOR_CLONE_ERR", "Cannot clone iterator ''{0}''." }, { "AXIS_SUPPORT_ERR", "Iterator for axis ''{0}'' not supported." }, { "TYPED_AXIS_SUPPORT_ERR", "Iterator for typed axis ''{0}'' not supported." }, 
        { "STRAY_ATTRIBUTE_ERR", "Attribute ''{0}'' outside of element." }, { "STRAY_NAMESPACE_ERR", "Namespace declaration ''{0}''=''{1}'' outside of element." }, { "NAMESPACE_PREFIX_ERR", "Namespace for prefix ''{0}'' has not been declared." }, { "DOM_ADAPTER_INIT_ERR", "DOMAdapter created using wrong type of source DOM." }, { "PARSER_DTD_SUPPORT_ERR", "The SAX parser you are using does not handle DTD declaration events." }, { "NAMESPACES_SUPPORT_ERR", "The SAX parser you are using does not have support for XML Namespaces." }, { "CANT_RESOLVE_RELATIVE_URI_ERR", "Could not resolve the URI reference ''{0}''." }, { "UNSUPPORTED_XSL_ERR", "Unsupported XSL element ''{0}''" }, { "UNSUPPORTED_EXT_ERR", "Unrecognized XSLTC extension ''{0}''" }, { "UNKNOWN_TRANSLET_VERSION_ERR", "The specified translet, ''{0}'', was created using a version of XSLTC more recent than the version of the XSLTC run-time that is in use.  You must recompile the stylesheet or use a more recent version of XSLTC to run this translet." }, 
        { "INVALID_QNAME_ERR", "An attribute whose value must be a QName had the value ''{0}''" }, { "INVALID_NCNAME_ERR", "An attribute whose value must be an NCName had the value ''{0}''" }, { "UNALLOWED_EXTENSION_FUNCTION_ERR", "Use of the extension function ''{0}'' is not allowed when the secure processing feature is set to true." }, { "UNALLOWED_EXTENSION_ELEMENT_ERR", "Use of the extension element ''{0}'' is not allowed when the secure processing feature is set to true." } }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\runtime\ErrorMessages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */