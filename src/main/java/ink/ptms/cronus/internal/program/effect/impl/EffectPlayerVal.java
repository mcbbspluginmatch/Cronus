package ink.ptms.cronus.internal.program.effect.impl;

import ink.ptms.cronus.internal.program.QuestProgram;
import ink.ptms.cronus.uranus.annotations.Auto;
import ink.ptms.cronus.uranus.program.Program;
import ink.ptms.cronus.uranus.program.effect.Effect;

import java.util.regex.Matcher;

/**
 * @Author 坏黑
 * @Since 2019-05-11 15:24
 */
@Auto
public class EffectPlayerVal extends Effect {

    private String name;
    private String symbol;
    private String value;

    @Override
    public String pattern() {
        return "player\\.val\\.(?<name>\\S+) (?<symbol>\\S+)( (?<value>.+))?";
    }

    @Override
    public String getExample() {
        return "player.val.[name] [symbol] [content]";
    }

    @Override
    public void match(Matcher matcher) {
        name = matcher.group("name");
        value = matcher.group("value");
        symbol = matcher.group("symbol");
    }

    @Override
    public void eval(Program program) {
        if (program instanceof QuestProgram) {
            ink.ptms.cronus.uranus.program.effect.EffectVal.eval(program, value, symbol, name, ((QuestProgram) program).getDataPlayer().getDataGlobal());
        }
    }

    @Override
    public String toString() {
        return "EffectPlayerVal{" +
                "name='" + name + '\'' +
                ", symbol='" + symbol + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
