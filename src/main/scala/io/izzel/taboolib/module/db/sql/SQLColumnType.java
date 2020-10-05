package io.izzel.taboolib.module.db.sql;

/**
 * @Author sky
 * @Since 2018-05-14 19:13
 */
public enum SQLColumnType {

    /**
     * 有符号值：-128 到127（- 27 到27 - 1）
     * 无符号值：0到255（0 到28 - 1） 1个字节
     */
    TINYINT,

    /**
     * 有符号值：-32768 到32767（- 215 到215 - 1）
     * 无符号值：0到65535（0 到21 6 - 1） 2个字节
     */
    SMALLINT,

    /**
     * 有符号值：-8388608 到8388607（- 22 3 到22 3 - 1 ）
     * 无符号值：0到16777215（0 到22 4 - 1） 3个字节
     */
    MEDIUMINT,

    /**
     * 有符号值：-2147683648 到2147683647（- 231 到231- 1）
     * 无符号值：0到4294967295（0 到232 - 1） 4个字节
     */
    INT,

    /**
     * 有符号值：-9223372036854775808 到9223373036854775807（- 263到263-1）
     * 无符号值：0到18446744073709551615（0到264 – 1） 8个字节
     */
    BIGINT,

    /**
     * 最小非零值：±1.175494351e - 38
     */
    FLOAT,

    /**
     * 最小非零值：±2.2250738585072014e - 308
     */
    DOUBLE,

    /**
     * 可变：其值的范围依赖于m 和d
     */
    DECIMAL,

    /**
     * 定长：磁盘空间比较浪费,但是效率高,确定数据长度都一样,就使用定长
     * 比如：电话号码,身份证号
     * 最长可取255
     */
    CHAR,

    /**
     * 边长：比较节省空间,但是效率低,数据不能确定长度(不同数据长度有变化)
     * 比如：地址
     * 最长可取65535
     */
    VARCHAR,

    /**
     * 0~255字节值的长度+2字节
     */
    TINYTEXT,

    /**
     * 0~65535字节值的长度+2字节
     */
    TEXT,

    /**
     * 0~167772150字节值的长度+3字节
     */
    MEDIUMTEXT,

    /**
     * 0~4294967295字节值的长度+4字节
     */
    LONGTEXT,

    /**
     * 字节数为M，允许长度为0~M的定长二进制字符串
     */
    BINARY,

    /**
     * 允许长度为0~M的变长二进制字符串，字节数为值的长度加1
     */
    VARBINARY,

    /**
     * M位二进制数据，M最大值为64
     */
    BIT,

    /**
     * 可变长二进制数据，最多255个字节
     */
    TINYBLOB,

    /**
     * 可变长二进制数据，最多2的16次方-1个字节
     */
    BLOB,

    /**
     * 可变长二进制数据，最多2的24次方-1个字节
     */
    MEDIUMBLOB,

    /**
     * 可变长二进制数据，最多2的32次方-1个字节
     */
    LONGBLOB;

    /**
     * 5.38 update
     */
    public SQLColumn toColumn(String name) {
        return new SQLColumn(this, name);
    }

    public SQLColumn toColumn(int m, String name) {
        return new SQLColumn(this, m, name);
    }

    public SQLColumn toColumn(int m, int d, String name) {
        return new SQLColumn(this, m, d, name);
    }
}
