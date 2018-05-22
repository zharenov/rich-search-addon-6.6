package com.haulmont.addon.search.web.toolkit.ui.client.suggestionfield;

import com.haulmont.addon.search.web.gui.components.toolkit.ui.RichSearchField;
import com.haulmont.cuba.web.toolkit.ui.client.suggestionfield.CubaSuggestionFieldConnector;
import com.haulmont.cuba.web.toolkit.ui.client.suggestionfield.CubaSuggestionFieldWidget;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.shared.ui.Connect;

@Connect(RichSearchField.class)
public class RichSearchConnector extends CubaSuggestionFieldConnector {
    @Override
    protected void init() {
        super.init();

        CubaSuggestionFieldWidget widget = getWidget();

        widget.searchExecutor = query -> serverRpc.searchSuggestions(query);
        widget.arrowDownActionHandler = query -> serverRpc.onArrowDownKeyPressed(query);
        widget.enterActionHandler = query -> serverRpc.onEnterKeyPressed(query);

        widget.suggestionSelectedHandler = suggestion -> {
            serverRpc.selectSuggestion(suggestion.getId());

            updateWidgetValue(widget);
        };

        widget.cancelSearchHandler = () -> serverRpc.cancelSearch();
    }

    protected void updateWidgetValue(CubaSuggestionFieldWidget widget) {
        String stateValue = getState().text;
        if (! stateValue.equals(widget.getValue())) {
            widget.setValue(stateValue, false);
        }
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        CubaSuggestionFieldWidget widget = getWidget();

        if (stateChangeEvent.hasPropertyChanged("text")) {
            updateWidgetValue(widget);
        }

        widget.setReadonly(isReadOnly());
    }

}
