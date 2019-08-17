package com.sun.org.apache.xalan.internal.xsltc.runtime;

import java.util.ListResourceBundle;

public class ErrorMessages_zh_CN extends ListResourceBundle {
  public Object[][] getContents() { return new Object[][] { 
        { "RUN_TIME_INTERNAL_ERR", "''{0}'' 中的运行时内部错误" }, { "RUN_TIME_COPY_ERR", "执行 <xsl:copy> 时出现运行时错误。" }, { "DATA_CONVERSION_ERR", "从 ''{0}'' 到 ''{1}'' 的转换无效。" }, { "EXTERNAL_FUNC_ERR", "XSLTC 不支持外部函数 ''{0}''。" }, { "EQUALITY_EXPR_ERR", "等式表达式中的参数类型未知。" }, { "INVALID_ARGUMENT_ERR", "调用 ''{1}'' 时的参数类型 ''{0}'' 无效" }, { "FORMAT_NUMBER_ERR", "尝试使用模式 ''{1}'' 设置数字 ''{0}'' 的格式。" }, { "ITERATOR_CLONE_ERR", "无法克隆迭代器 ''{0}''。" }, { "AXIS_SUPPORT_ERR", "不支持轴 ''{0}'' 的迭代器。" }, { "TYPED_AXIS_SUPPORT_ERR", "不支持类型化轴 ''{0}'' 的迭代器。" }, 
        { "STRAY_ATTRIBUTE_ERR", "属性 ''{0}'' 在元素外部。" }, { "STRAY_NAMESPACE_ERR", "名称空间声明 ''{0}''=''{1}'' 在元素外部。" }, { "NAMESPACE_PREFIX_ERR", "没有说明名称空间前缀 ''{0}''。" }, { "DOM_ADAPTER_INIT_ERR", "使用错误类型的源 DOM 创建了 DOMAdapter。" }, { "PARSER_DTD_SUPPORT_ERR", "使用的 SAX 解析器不会处理 DTD 声明事件。" }, { "NAMESPACES_SUPPORT_ERR", "使用的 SAX 解析器不支持 XML 名称空间。" }, { "CANT_RESOLVE_RELATIVE_URI_ERR", "无法解析 URI 引用 ''{0}''。" }, { "UNSUPPORTED_XSL_ERR", "XSL 元素 ''{0}'' 不受支持" }, { "UNSUPPORTED_EXT_ERR", "XSLTC 扩展 ''{0}'' 无法识别" }, { "UNKNOWN_TRANSLET_VERSION_ERR", "创建指定 translet ''{0}'' 时使用的 XSLTC 的版本高于正在使用的 XSLTC 运行时的版本。必须重新编译样式表或使用较新的 XSLTC 版本运行此 translet。" }, 
        { "INVALID_QNAME_ERR", "其值必须为 QName 的属性具有值 ''{0}''" }, { "INVALID_NCNAME_ERR", "其值必须为 NCName 的属性具有值 ''{0}''" }, { "UNALLOWED_EXTENSION_FUNCTION_ERR", "当安全处理功能设置为“真”时, 不允许使用扩展函数 ''{0}''。" }, { "UNALLOWED_EXTENSION_ELEMENT_ERR", "当安全处理功能设置为“真”时, 不允许使用扩展元素 ''{0}''。" } }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\runtime\ErrorMessages_zh_CN.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */