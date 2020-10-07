package io.izzel.taboolib.module.nms.nbt;

import io.izzel.taboolib.module.nms.NMS;

import java.util.Arrays;

/**
 * Attribute 映射类
 *
 * @Author sky
 * @Since 2019-12-11 19:31
 */
public enum Attribute {

    /**
     * 最大生命值
     */
    MAX_HEALTH("generic.maxHealth", new String[]{"health", "maxHealth"}),

    /**
     * 最大跟随距离
     */
    FOLLOW_RANGE("generic.followRange", new String[]{"follow", "followRange"}),

    /**
     * 击退抗性
     */
    KNOCKBACK_RESISTANCE("generic.knockbackResistance", new String[]{"knockback", "knockbackResistance"}),

    /**
     * 移动速度
     */
    MOVEMENT_SPEED("generic.movementSpeed", new String[]{"speed", "movementSpeed", "walkSpeed"}),

    /**
     * 飞行速度
     */
    FLYING_SPEED("generic.flyingSpeed", new String[]{"flySpeed", "flyingSpeed"}),

    /**
     * 攻击力
     */
    ATTACK_DAMAGE("generic.attackDamage", new String[]{"damage", "attackDamage"}),

    /**
     * 击退
     */
    ATTACK_KNOCKBACK("generic.attackKnockback", new String[]{"damageKnockback", "attackKnockback"}),

    /**
     * 攻速
     */
    ATTACK_SPEED("generic.attackSpeed", new String[]{"damageSpeed", "attackSpeed"}),

    /**
     * 护甲
     */
    ARMOR("generic.armor", new String[]{"armor"}),

    /**
     * 护甲任性
     */
    ARMOR_TOUGHNESS("generic.armorToughness", new String[]{"toughness", "armorToughness"}),

    /**
     * 幸运
     */
    LUCK("generic.luck", new String[]{"luck"});

    String minecraftKey;
    String[] simplifiedKey;

    Attribute(String minecraftKey, String[] simplifiedKey) {
        this.minecraftKey = minecraftKey;
        this.simplifiedKey = simplifiedKey;
    }

    public String getMinecraftKey() {
        return minecraftKey;
    }

    public String[] getSimplifiedKey() {
        return simplifiedKey;
    }

    public Object toNMS() {
        return NMS.handle().toNMS(this);
    }

    public boolean match(String source) {
        return name().equalsIgnoreCase(source) || minecraftKey.equalsIgnoreCase(source) || Arrays.stream(simplifiedKey).anyMatch(key -> key.equalsIgnoreCase(source));
    }

    public static Attribute parse(String source) {
        return Arrays.stream(values()).filter(attribute -> attribute.match(source)).findFirst().orElse(null);
    }
}
