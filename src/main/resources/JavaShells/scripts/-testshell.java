import org.bukkit.Bukkit;

public class testshell {

    public void onEnable() {
        Bukkit.broadcastMessage("testshell enable!");
    }

    public void onDisable() {
        Bukkit.broadcastMessage("testshell disable!");
    }
}