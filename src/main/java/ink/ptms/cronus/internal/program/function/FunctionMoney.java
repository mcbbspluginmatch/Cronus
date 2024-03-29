package ink.ptms.cronus.internal.program.function;

import ink.ptms.cronus.internal.program.QuestProgram;
import ink.ptms.cronus.uranus.annotations.Auto;
import ink.ptms.cronus.uranus.function.Function;
import ink.ptms.cronus.uranus.program.Program;
import io.izzel.taboolib.TabooLibAPI;

/**
 * @Author 坏黑
 * @Since 2019-05-12 15:24
 */
@Auto
public class FunctionMoney extends Function {

    @Override
    public String getName() {
        return "money";
    }

    @Override
    public Object eval(Program program, String... args) {
        if (program instanceof QuestProgram) {
            return TabooLibAPI.getPluginBridge().economyLook(((QuestProgram) program).getPlayer());
        }
        return "<Non-Quest>";
    }
}
