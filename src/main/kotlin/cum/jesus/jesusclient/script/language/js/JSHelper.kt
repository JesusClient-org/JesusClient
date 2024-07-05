package cum.jesus.jesusclient.script.language.js

import cum.jesus.jesusclient.script.languages.js.JSLoader
import dev.falsehonesty.asmhelper.dsl.At
import dev.falsehonesty.asmhelper.dsl.applyField
import dev.falsehonesty.asmhelper.dsl.inject
import dev.falsehonesty.asmhelper.dsl.instructions.InsnListBuilder
import dev.falsehonesty.asmhelper.dsl.remove
import dev.falsehonesty.asmhelper.dsl.writers.AccessType
import org.mozilla.javascript.Context
import org.mozilla.javascript.NativeJavaObject
import org.mozilla.javascript.Wrapper

object JSHelper {
    @JvmStatic
    fun asmInjectHelper(
        _className: String,
        _methodName: String,
        _methodDesc: String,
        _at: At,
        _fieldMaps: Map<String, String>,
        _methodMaps: Map<String, String>,
        _insnList: (Wrapper) -> Unit,
    ) {
        inject {
            className = _className
            methodName = _methodName
            methodDesc = _methodDesc
            at = _at
            fieldMaps = _fieldMaps
            methodMaps = _methodMaps

            insnList {
                wrapInContext {
                    _insnList(NativeJavaObject(JSLoader.INSTANCE.scope, this, InsnListBuilder::class.java))
                }
            }
        }
    }

    @JvmStatic
    fun asmRemoveHelper(
        _className: String,
        _at: At,
        _methodName: String,
        _methodDesc: String,
        _methodMaps: Map<String, String>,
        _numberToRemove: Int,
    ) {
        remove {
            className = _className
            methodName = _methodName
            methodDesc = _methodDesc
            at = _at
            methodMaps = _methodMaps
            numberToRemove = _numberToRemove
        }
    }

    @JvmStatic
    fun asmFieldHelper(
        _className: String,
        _fieldName: String,
        _fieldDesc: String,
        _initialValue: Any?,
        _accessTypes: List<AccessType>,
    ) {
        applyField {
            className = _className
            fieldName = _fieldName
            fieldDesc = _fieldDesc
            initialValue = _initialValue
            accessTypes = _accessTypes
        }
    }

    private inline fun <T> wrapInContext(crossinline block: () -> T): T {
        try {
            JSLoader.INSTANCE.pushContext()
            return block()
        } finally {
            JSLoader.INSTANCE.popContext()
        }
    }
}