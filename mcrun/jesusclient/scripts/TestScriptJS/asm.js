export default ASM => {
    print("asm test begin");

    const { ARRAY, desc, L } = ASM;

    ASM.injectBuilder(
        "net/minecraft/client/Minecraft",
        "startGame",
        desc("V"),
        ASM.At(ASM.At.TAIL)
    )
    .instructions($ => {
        $.iconst_1();
        $.anewarray("java/lang/Object");
        $.dup();
        $.iconst_0();
        $.ldc("hello from Minecraft::startGame asm inject");
        $.aastore();
        $.invokeStatic("cum/jesus/jesusclient/util/Logger", "debug", desc("V", ARRAY(L("java/lang/Object"))));
    })
    .execute();

    print("asm test end");
}