package io.izzel.taboolib.module.hologram;

import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * 全息观察对象
 *
 * @Author sky
 * @Since 2020-03-07 16:56
 */
public class HologramViewer {

    private final int id;
    private final Player player;
    private boolean spawned;
    private boolean visible;

    HologramViewer(Player player) {
        this.id = THologram.nextIndex(player);
        this.player = player;
    }

    /**
     * 设置全息字是否可见
     *
     * @param visible 是否可见
     */
    public void setVisible(boolean visible) {
        this.spawned = false;
        this.visible = visible;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HologramViewer)) {
            return false;
        }
        HologramViewer that = (HologramViewer) o;
        return player.getName().equals(that.getPlayer().getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(player.getName());
    }

    @Override
    public String toString() {
        return "HologramViewer{" +
                "id=" + id +
                ", player=" + player +
                ", spawned=" + spawned +
                ", visible=" + visible +
                '}';
    }

    public int getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isSpawned() {
        return spawned;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setSpawned(boolean spawned) {
        this.spawned = spawned;
    }
}
