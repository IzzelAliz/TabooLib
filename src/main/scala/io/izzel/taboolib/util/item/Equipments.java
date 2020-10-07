package io.izzel.taboolib.util.item;

import io.izzel.taboolib.Version;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 装备类型转换工具
 *
 * @Author 坏黑
 * @Since 2019-04-25 22:01
 */
public enum Equipments {

    /**
     * 主手
     */
    HAND(EquipmentSlot.HAND, "mainhand", -1),

    /**
     * 副手
     */
    OFF_HAND(EquipmentSlot.OFF_HAND, "offhand", 40),

    /**
     * 脚
     */
    FEET(EquipmentSlot.FEET, "feet", 36),

    /**
     * 腿
     */
    LEGS(EquipmentSlot.LEGS, "legs", 37),

    /**
     * 胸
     */
    CHEST(EquipmentSlot.CHEST, "chest", 38),

    /**
     * 头
     */
    HEAD(EquipmentSlot.HEAD, "head", 39);

    private final EquipmentSlot bukkit;
    private final String nms;
    private final int slot;
    private final boolean supported = Version.isAfter(Version.v1_9);

    Equipments(EquipmentSlot bukkit, String nms, int slot) {
        this.bukkit = bukkit;
        this.nms = nms;
        this.slot = slot;
    }

    /**
     * 设置物品
     *
     * @param player 玩家实例
     * @param item   物品
     */
    public void setItem(Player player, ItemStack item) {
        setItem((LivingEntity) player, item);
    }

    /**
     * 设置物品
     *
     * @param entity 实体实例
     * @param item   物品
     */
    public void setItem(LivingEntity entity, ItemStack item) {
        switch (this) {
            case HAND:
                if (supported) {
                    entity.getEquipment().setItemInMainHand(item);
                } else {
                    entity.getEquipment().setItemInHand(item);
                }
                break;
            case OFF_HAND:
                if (supported) {
                    entity.getEquipment().setItemInOffHand(item);
                }
                break;
            case FEET:
                entity.getEquipment().setBoots(item);
                break;
            case LEGS:
                entity.getEquipment().setLeggings(item);
                break;
            case CHEST:
                entity.getEquipment().setChestplate(item);
                break;
            case HEAD:
                entity.getEquipment().setHelmet(item);
                break;
        }
    }

    /**
     * 设置物品掉落几率
     *
     * @param entity 实体实例
     * @param chance 几率
     */
    public void setItemDropChance(LivingEntity entity, float chance) {
        switch (this) {
            case HAND:
                if (supported) {
                    entity.getEquipment().setItemInMainHandDropChance(chance);
                } else {
                    entity.getEquipment().setItemInHandDropChance(chance);
                }
                break;
            case OFF_HAND:
                if (supported) {
                    entity.getEquipment().setItemInOffHandDropChance(chance);
                }
                break;
            case FEET:
                entity.getEquipment().setBootsDropChance(chance);
                break;
            case LEGS:
                entity.getEquipment().setLeggingsDropChance(chance);
                break;
            case CHEST:
                entity.getEquipment().setChestplateDropChance(chance);
                break;
            case HEAD:
                entity.getEquipment().setHelmetDropChance(chance);
                break;
        }
    }

    /**
     * 获取物品
     *
     * @param player 玩家实例
     */
    @Nullable
    public ItemStack getItem(Player player) {
        return getItem((LivingEntity) player);
    }

    /**
     * 获取物品
     *
     * @param entity 玩家实例
     */
    @Nullable
    public ItemStack getItem(LivingEntity entity) {
        switch (this) {
            case HAND:
                if (supported) {
                    return entity.getEquipment().getItemInMainHand();
                } else {
                    return entity.getEquipment().getItemInHand();
                }
            case OFF_HAND:
                if (supported) {
                    return entity.getEquipment().getItemInOffHand();
                }
            case FEET:
                return entity.getEquipment().getBoots();
            case LEGS:
                return entity.getEquipment().getLeggings();
            case CHEST:
                return entity.getEquipment().getChestplate();
            case HEAD:
                return entity.getEquipment().getHelmet();
            default:
                return null;
        }
    }

    /**
     * 获取物品掉落几率
     *
     * @param entity 实体实例
     */
    public float getItemDropChance(LivingEntity entity) {
        switch (this) {
            case HAND:
                if (supported) {
                    return entity.getEquipment().getItemInMainHandDropChance();
                } else {
                    return entity.getEquipment().getItemInHandDropChance();
                }
            case OFF_HAND:
                if (supported) {
                    return entity.getEquipment().getItemInOffHandDropChance();
                }
            case FEET:
                return entity.getEquipment().getBootsDropChance();
            case LEGS:
                return entity.getEquipment().getLeggingsDropChance();
            case CHEST:
                return entity.getEquipment().getChestplateDropChance();
            case HEAD:
                return entity.getEquipment().getHelmetDropChance();
            default:
                return 0f;
        }
    }

    /**
     * 通过 nms 物品类型名称获取 {@link Equipments}
     */
    @Nullable
    public static Equipments fromNMS(String nms) {
        return Arrays.stream(values()).filter(tEquipment -> tEquipment.nms.equalsIgnoreCase(nms)).findFirst().orElse(null);
    }

    /**
     * 通过 bukkit 物品类型名称获取 {@link Equipments}
     */
    @Nullable
    public static Equipments fromBukkit(EquipmentSlot bukkit) {
        return Arrays.stream(values()).filter(tEquipment -> tEquipment.bukkit == bukkit).findFirst().orElse(null);
    }

    /**
     * 获取所有物品
     *
     * @param player 玩家实例
     */
    @NotNull
    public static Map<Equipments, ItemStack> getItems(Player player) {
        return getItems((LivingEntity) player);
    }

    /**
     * 获取所有物品
     * @param entity 实体实例
     */
    @NotNull
    public static Map<Equipments, ItemStack> getItems(LivingEntity entity) {
        return Arrays.stream(values()).collect(Collectors.toMap(equipment -> equipment, equipment -> equipment.getItem(entity), (a, b) -> b));
    }

    public EquipmentSlot getBukkit() {
        return bukkit;
    }

    public String getNMS() {
        return nms;
    }

    public int getSlot() {
        return slot;
    }
}
