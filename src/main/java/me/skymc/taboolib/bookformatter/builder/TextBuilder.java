package me.skymc.taboolib.bookformatter.builder;

import com.ilummc.tlib.bungee.api.chat.*;
import me.skymc.taboolib.bookformatter.action.ClickAction;
import me.skymc.taboolib.bookformatter.action.HoverAction;

/**
 * @author sky
 * @since 2018-03-08 22:37:27
 */
public class TextBuilder {

    private String text = "";
    private ClickAction onClick = null;
    private HoverAction onHover = null;

    public TextBuilder() {
    }

    public TextBuilder(String text) {
        this.text = text;
    }

    /**
     * Creates a new TextBuilder with the parameter as his initial text
     *
     * @param text initial text
     * @return a new TextBuilder with the parameter as his initial text
     */
    public static TextBuilder of(String text) {
        return new TextBuilder(text);
    }

    public String getText() {
        return text;
    }

    public ClickAction getClick() {
        return onClick;
    }

    public HoverAction getHover() {
        return onHover;
    }

    public void text(String text) {
        this.text = text;
    }

    public void onClick(ClickAction onClick) {
        this.onClick = onClick;
    }

    public void onHover(HoverAction onHover) {
        this.onHover = onHover;
    }

    /**
     * Creates the component representing the built text
     *
     * @return the component representing the built text
     */
    public BaseComponent build() {
        TextComponent res = new TextComponent(text);
        if (onClick != null) {
            res.setClickEvent(new ClickEvent(onClick.action(), onClick.value()));
        }
        if (onHover != null) {
            res.setHoverEvent(new HoverEvent(onHover.action(), onHover.value()));
        }
        return res;
    }
}
