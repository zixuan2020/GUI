package moe.him188.gui.window;

import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.Element;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import com.google.gson.Gson;
import moe.him188.gui.utils.Backable;
import moe.him188.gui.window.listener.response.ResponseListenerCustom;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 能直接接受提交表单 ({@link #onResponded}) 和关闭窗口事件({@link #onClosed}) 的 {@link FormWindowCustom}.
 * 相较于 {@link FormWindowCustom}, 该类无需依赖于 {@link Listener} 去监听 {@link PlayerFormRespondedEvent}, 而可以直接通过 lambda 方法收到返回数据.
 * <br>
 * {@link FormWindowCustom} that can receive click button event({@link #onResponded}) and close window event({@link #onClosed}).
 * Comparing with {@link FormWindowCustom}, this responsible one does not need {@link Listener} to listen {@link PlayerFormRespondedEvent},
 * but it can directly receive {@link FormResponse} through lambda statements.
 *
 * @author Him188moe @ GUI Project
 */
public class ResponsibleFormWindowCustom extends FormWindowCustom implements Backable, ResponseListenerCustom {
    protected transient BiConsumer<FormResponseCustom, Player> buttonClickedListener = null;

    protected transient Consumer<Player> windowClosedListener = null;

    private transient FormWindow parent = null;

    public ResponsibleFormWindowCustom() {
        this("");
    }

    public ResponsibleFormWindowCustom(String title) {
        this(title, new ArrayList<>());
    }

    public ResponsibleFormWindowCustom(String title, Element... contents) {
        this(title, Arrays.asList(contents));
    }

    public ResponsibleFormWindowCustom(String title, @NotNull List<Element> contents) {
        this(title, contents, "");
    }

    public ResponsibleFormWindowCustom(String title, @NotNull List<Element> contents, @NotNull String icon) {
        super(Objects.requireNonNull(title), Objects.requireNonNull(contents), Objects.requireNonNull(icon));
    }

    @Override
    public void setParent(FormWindow parent) {
        this.parent = parent;
    }

    @Override
    public FormWindow getParent() {
        return this.parent;
    }

    /**
     * 在玩家提交表单后调用 <br>
     * Called on submitted
     *
     * @param listener 调用的方法
     */
    public final ResponsibleFormWindowCustom onResponded(@NotNull BiConsumer<FormResponseCustom, Player> listener) {
        Objects.requireNonNull(listener);
        this.buttonClickedListener = listener;
        return this;
    }

    /**
     * 在玩家提交表单后调用 <br>
     * Called on submitted
     *
     * @param listener 调用的方法(无 Player)
     */
    public final ResponsibleFormWindowCustom onResponded(@NotNull Consumer<FormResponseCustom> listener) {
        Objects.requireNonNull(listener);
        this.buttonClickedListener = (response, player) -> listener.accept(response);
        return this;
    }

    /**
     * 在玩家提交表单后调用 <br>
     * Called on closed without submitting.
     *
     * @param listener 调用的方法(无参数)
     */
    public final ResponsibleFormWindowCustom onResponded(@NotNull Runnable listener) {
        Objects.requireNonNull(listener);
        this.buttonClickedListener = (id, player) -> listener.run();
        return this;
    }

    /**
     * 在玩家关闭窗口而没有点击按钮提交表单后调用. <br>
     * Called on closed without submitting.
     *
     * @param listener 调用的方法
     */
    public final ResponsibleFormWindowCustom onClosed(@NotNull Consumer<Player> listener) {
        Objects.requireNonNull(listener);
        this.windowClosedListener = listener;
        return this;
    }

    /**
     * 在玩家关闭窗口而没有点击按钮提交表单后调用. <br>
     * Called on closed without submitting.
     *
     * @param listener 调用的方法
     */
    public final ResponsibleFormWindowCustom onClosed(@NotNull Runnable listener) {
        Objects.requireNonNull(listener);
        this.windowClosedListener = (player) -> listener.run();
        return this;
    }

    @Override
    public String getJSONData() {
        String toModify = new Gson().toJson(this, FormWindowCustom.class);//must!!
        //We need to replace this due to Java not supporting declaring class field 'default'
        return toModify.replace("defaultOptionIndex", "default")
                .replace("defaultText", "default")
                .replace("defaultValue", "default")
                .replace("defaultStepIndex", "default");
    }

    public void callClicked(@NotNull FormResponseCustom response, @NotNull Player player) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(response);

        this.onClicked(response, player);

        if (this.buttonClickedListener != null) {
            this.buttonClickedListener.accept(response, player);
        }
    }

    public void callClosed(@NotNull Player player) {
        Objects.requireNonNull(player);

        this.onClosed(player);

        if (this.windowClosedListener != null) {
            this.windowClosedListener.accept(player);
        }
    }

    static boolean onEvent(FormWindow formWindow, FormResponse response, Player player) {
        if (formWindow instanceof ResponsibleFormWindowCustom) {
            ResponsibleFormWindowCustom window = (ResponsibleFormWindowCustom) formWindow;

            if (window.wasClosed() || response == null) {
                window.callClosed(player);
                window.closed = false;//for resending
            } else {
                window.callClicked(((FormResponseCustom) response), player);
            }
            return true;
        }
        return false;
    }
}
