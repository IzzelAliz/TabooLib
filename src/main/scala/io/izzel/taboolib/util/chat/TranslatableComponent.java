package io.izzel.taboolib.util.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TranslatableComponent extends BaseComponent {

    private final Pattern format = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

    /**
     * The key into the Minecraft locale files to use for the translation. The
     * text depends on the client's locale setting. The console is always en_US
     */
    private String translate;
    /**
     * The components to substitute into the translation
     */
    private List<BaseComponent> with;

    /**
     * Creates a translatable component from the original to clone it.
     *
     * @param original the original for the new translatable component.
     */
    public TranslatableComponent(TranslatableComponent original) {
        super(original);
        setTranslate(original.getTranslate());

        if (original.getWith() != null) {
            List<BaseComponent> temp = new ArrayList<BaseComponent>();
            for (BaseComponent baseComponent : original.getWith()) {
                temp.add(baseComponent.duplicate());
            }
            setWith(temp);
        }
    }

    /**
     * Creates a translatable component with the passed substitutions
     *
     * @param translate the translation key
     * @param with      the {@link String}s and
     *                  {@link BaseComponent}s to use into the
     *                  translation
     * @see #translate
     * @see #setWith(List)
     */
    public TranslatableComponent(String translate, Object... with) {
        setTranslate(translate);
        if (with != null && with.length != 0) {
            List<BaseComponent> temp = new ArrayList<BaseComponent>();
            for (Object w : with) {
                if (w instanceof BaseComponent) {
                    temp.add((BaseComponent) w);
                } else {
                    temp.add(new TextComponent(String.valueOf(w)));
                }
            }
            setWith(temp);
        }
    }

    public TranslatableComponent() {
    }

    /**
     * Creates a duplicate of this TranslatableComponent.
     *
     * @return the duplicate of this TranslatableComponent.
     */
    @Override
    public TranslatableComponent duplicate() {
        return new TranslatableComponent(this);
    }

    /**
     * Sets the translation substitutions to be used in this component. Removes
     * any previously set substitutions
     *
     * @param components the components to substitute
     */
    public void setWith(List<BaseComponent> components) {
        for (BaseComponent component : components) {
            component.parent = this;
        }
        with = components;
    }

    /**
     * Adds a text substitution to the component. The text will inherit this
     * component's formatting
     *
     * @param text the text to substitute
     */
    public void addWith(String text) {
        addWith(new TextComponent(text));
    }

    /**
     * Adds a component substitution to the component. The text will inherit
     * this component's formatting
     *
     * @param component the component to substitute
     */
    public void addWith(BaseComponent component) {
        if (with == null) {
            with = new ArrayList<BaseComponent>();
        }
        component.parent = this;
        with.add(component);
    }

    @Override
    protected void toPlainText(StringBuilder builder) {
        convert(builder, false);
        super.toPlainText(builder);
    }

    @Override
    protected void toLegacyText(StringBuilder builder) {
        convert(builder, true);
        super.toLegacyText(builder);
    }

    private void convert(StringBuilder builder, boolean applyFormat) {
        String trans = TranslationRegistry.INSTANCE.translate(translate);

        Matcher matcher = format.matcher(trans);
        int position = 0;
        int i = 0;
        while (matcher.find(position)) {
            int pos = matcher.start();
            if (pos != position) {
                if (applyFormat) {
                    addFormat(builder);
                }
                builder.append(trans, position, pos);
            }
            position = matcher.end();

            String formatCode = matcher.group(2);
            switch (formatCode.charAt(0)) {
                case 's':
                case 'd':
                    String withIndex = matcher.group(1);

                    BaseComponent withComponent = with.get(withIndex != null ? Integer.parseInt(withIndex) - 1 : i++);
                    if (applyFormat) {
                        withComponent.toLegacyText(builder);
                    } else {
                        withComponent.toPlainText(builder);
                    }
                    break;
                case '%':
                    if (applyFormat) {
                        addFormat(builder);
                    }
                    builder.append('%');
                    break;
            }
        }
        if (trans.length() != position) {
            if (applyFormat) {
                addFormat(builder);
            }
            builder.append(trans.substring(position));
        }
    }

    public Pattern getFormat() {
        return this.format;
    }

    public String getTranslate() {
        return this.translate;
    }

    public List<BaseComponent> getWith() {
        return this.with;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof TranslatableComponent)) return false;
        final TranslatableComponent other = (TranslatableComponent) o;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        final Object this$format = this.getFormat();
        final Object other$format = other.getFormat();
        if (this$format == null ? other$format != null : !this$format.equals(other$format)) return false;
        final Object this$translate = this.getTranslate();
        final Object other$translate = other.getTranslate();
        if (this$translate == null ? other$translate != null : !this$translate.equals(other$translate)) return false;
        final Object this$with = this.getWith();
        final Object other$with = other.getWith();
        return this$with == null ? other$with == null : this$with.equals(other$with);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof TranslatableComponent;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $format = this.getFormat();
        result = result * PRIME + ($format == null ? 43 : $format.hashCode());
        final Object $translate = this.getTranslate();
        result = result * PRIME + ($translate == null ? 43 : $translate.hashCode());
        final Object $with = this.getWith();
        result = result * PRIME + ($with == null ? 43 : $with.hashCode());
        return result;
    }

    public String toString() {
        return "TranslatableComponent(format=" + this.getFormat() + ", translate=" + this.getTranslate() + ", with=" + this.getWith() + ")";
    }
}
