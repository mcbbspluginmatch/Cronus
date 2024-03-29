package ink.ptms.cronus.internal.asm;

import ink.ptms.cronus.Cronus;
import io.izzel.taboolib.module.inject.TFunction;
import io.izzel.taboolib.module.lite.SimpleVersionControl;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @Author 坏黑
 * @Since 2019-05-30 22:25
 */
@TFunction(enable = "init")
public abstract class AsmHandler {

    private static AsmHandler impl;

    public static AsmHandler getImpl() {
        return impl;
    }

    static void init() {
        try {
            AsmHandler.impl = (AsmHandler) SimpleVersionControl.createNMS("ink.ptms.cronus.internal.asm.AsmHandlerImpl").useCache().translate(Cronus.getInst()).newInstance();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    abstract public Entity getEntityByEntityId(int id);

    abstract public ItemStack[] getRecipe(Inventory inventory);

}
