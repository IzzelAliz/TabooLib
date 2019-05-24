package me.skymc.taboolib.common.nms.nbt;

/**
 * @Author 坏黑
 * @Since 2019-05-24 17:46
 */
public enum NBTType {

    END(0),

    BYTE(1),

    SHORT(2),

    INT(3),

    LONG(4),

    FLOAT(5),

    DOUBLE(6),

    BYTE_ARRAY(7),

    INT_ARRAY(11),

    STRING(8),

    LIST(9),

    COMPOUND(10);

    private int id;

    NBTType(int i) {
        this.id = i;
    }

    public int getId() {
        return this.id;
    }
}
