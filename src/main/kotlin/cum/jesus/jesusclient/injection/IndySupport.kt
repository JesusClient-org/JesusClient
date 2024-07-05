package cum.jesus.jesusclient.injection

import cum.jesus.jesusclient.script.ScriptManager
import java.lang.invoke.CallSite
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.invoke.MutableCallSite
import java.lang.invoke.SwitchPoint

object IndySupport {
    private var invocationInvalidator = SwitchPoint()

    @JvmStatic
    fun bootstrapInvokeJS(
        lookup: MethodHandles.Lookup,
        name: String,
        type: MethodType,
        scriptName: String,
        functionID: String
    ): CallSite {
        val callSite = MutableCallSite(type)

        val initHandle = MethodHandles.insertArguments(
            lookup.findStatic(
                IndySupport::class.java,
                "initInvokeJS",
                MethodType.methodType(
                    Any::class.java,
                    MutableCallSite::class.java,
                    String::class.java,
                    String::class.java,
                    Array<Any?>::class.java
                )
            ), 0, callSite, scriptName, functionID
        )

        callSite.target = initHandle.asType(type)
        return callSite
    }

    @JvmStatic
    fun initInvokeJS(callSite: MutableCallSite, scriptName: String, functionID: String, args: Array<Any?>): Any? {
        val targetHandle = ScriptManager.asmInvokeLookup(scriptName, functionID)
        val initTarget = callSite.target
        val guardedTarget = invocationInvalidator.guardWithTest(targetHandle, initTarget)

        callSite.target = guardedTarget

        return targetHandle.invoke(args)
    }

    fun invalidateInvocations() {
        SwitchPoint.invalidateAll(arrayOf(invocationInvalidator))
        invocationInvalidator = SwitchPoint()
    }
}