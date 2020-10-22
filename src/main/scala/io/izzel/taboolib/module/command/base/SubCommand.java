package io.izzel.taboolib.module.command.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 子命令注解
 * 可对 BaseSubCommand 或自定义方法进行声明
 * <p>
 * 对方法声明时使用
 * public void command(CommandSender sender, String[] args)
 * 或
 * public void command(Player player, String[] args)
 *
 * @Author sky
 * @Since 2018-05-09 22:38
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SubCommand {

    /**
     * 优先级
     * 用于在帮助列表中排序子命令
     */
    double priority() default 0;

    /**
     * 子命令权限
     * 缺少权限将不会显示在帮助列表中且无法执行
     */
    String permission() default "";

    /**
     * 描述
     * 用于在帮助列表中显示
     */
    String description() default "";

    /**
     * 别名
     */
    String[] aliases() default {};

    /**
     * 参数
     * 用法：
     * {"player", "name?"}
     * 使用 "?" 结尾与 optional() 等价
     */
    String[] arguments() default {};

    /**
     * 是否忽略子命令名称作为第一个传入参数
     * /example test bukkitObj 1
     * <p>
     * 启用时传入参数：
     * [bukkitObj, 1]
     * 仅用时传入参数：
     * [test, bukkitObj, 1]
     */
    boolean ignoredLabel() default true;

    /**
     * 是否需要玩家执行
     * 已过时
     */
    @Deprecated
    boolean requiredPlayer() default false;

    /**
     * 是否在帮助列表中隐藏该子命令
     */
    boolean hideInHelp() default false;

    /**
     * 指令执行者约束
     * 类型无效将不会显示在帮助列表中且无法执行
     */
    CommandType type() default CommandType.ALL;
}
