package krincraft.idk;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class IDK extends JavaPlugin {
    @Override
    public void onEnable() {
        //插件启用逻辑
        Bukkit.getPluginCommand("IDK").setExecutor(new IDKCommand()); //注册指令
        Bukkit.getPluginManager().registerEvents(new IDKListener(), this); //注册事件处理
    }

    @Override
    public void onDisable() {
        //插件关闭逻辑
    }
}
