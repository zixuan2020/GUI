package moe.him188.gui.template.element;

import cn.nukkit.form.element.Element;
import cn.nukkit.form.element.ElementStepSlider;
import moe.him188.gui.template.response.TemplateResponse;
import moe.him188.gui.utils.ResponseParseException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TemplateElementStepSlider<K> extends TemplateElement<K> {
    private final String name;
    private final List<String> steps;
    private final int defaultStepIndex;

    /**
     * @param steps size >= 1
     */
    public TemplateElementStepSlider(@NotNull K elementKey, String name, List<String> steps) throws IllegalArgumentException {
        this(elementKey, name, steps, steps.size() - 1);
    }

    /**
     * @param steps       size >= 1
     * @param defaultStep >= 0
     */
    public TemplateElementStepSlider(@NotNull K elementKey, String name, List<String> steps, int defaultStep) throws IllegalArgumentException {
        super(elementKey);
        this.name = name;
        this.steps = steps;
        if (this.steps.size() == 0) {
            throw new IllegalArgumentException("steps must have at least 1 element");
            //or FormWindowCustom#setResponse will throw IndexOutOfBoundsException
        }

        this.defaultStepIndex = defaultStep;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<String> getSteps() {
        return steps;
    }

    public String getStep(int id) {
        return this.steps.get(id);
    }

    public int getDefaultStepIndex() {
        return defaultStepIndex;
    }

    @Override
    public Element build() {
        return new ElementStepSlider(this.name, this.steps, this.defaultStepIndex);
    }

    @Override
    public TemplateElementStepSlider.Response parseResponse(Object object) throws ResponseParseException {
        try {
            int id = Integer.parseInt(String.valueOf(object));
            return new TemplateElementStepSlider.Response(id, this.getStep(id));
        } catch (NumberFormatException e) {
            throw new ResponseParseException(this, e);
        }
    }

    public static class Response extends TemplateResponse<Integer> {
        private final String step;

        private Response(Integer response, String step) {
            super(response);
            this.step = step;
        }

        @Override
        public Integer get() {
            return response;
        }

        public String getStep() {
            return step;
        }
    }
}
