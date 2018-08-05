package moe.him188.gui.template.element;

import cn.nukkit.form.element.Element;
import cn.nukkit.form.element.ElementStepSlider;
import moe.him188.gui.template.response.TemplateResponse;
import moe.him188.gui.utils.ResponseParseException;

import java.util.ArrayList;
import java.util.List;

public class TemplateElementStepSlider<K> extends TemplateElement<K> {
    private final String name;
    private final List<String> steps;
    private final int defaultStepIndex;

    public TemplateElementStepSlider(K elementKey, String name) {
        this(elementKey, name, new ArrayList<>());
    }

    public TemplateElementStepSlider(K elementKey, String name, List<String> steps) {
        this(elementKey, name, steps, steps.size() == 0 ? 0 : steps.size() - 1);
    }

    public TemplateElementStepSlider(K elementKey, String name, List<String> steps, int defaultStep) {
        super(elementKey);
        this.name = name;
        this.steps = steps;
        this.defaultStepIndex = defaultStep;
    }

    public String getName() {
        return name;
    }

    public List<String> getSteps() {
        return steps;
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
            return new TemplateElementStepSlider.Response(Integer.parseInt(String.valueOf(object)));
        } catch (NumberFormatException e) {
            throw new ResponseParseException(this, e);
        }
    }

    public static class Response extends TemplateResponse<Integer> {
        private Response(Integer response) {
            super(response);
        }

        @Override
        public Integer get() {
            return response;
        }
    }
}