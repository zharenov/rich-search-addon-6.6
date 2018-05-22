package com.haulmont.addon.search.web.gui.components.toolkit.ui;

import com.haulmont.cuba.web.toolkit.ui.CubaSuggestionField;
import com.haulmont.cuba.web.toolkit.ui.client.suggestionfield.CubaSuggestionFieldServerRpc;
import com.vaadin.event.FieldEvents;

public class RichSearchField extends CubaSuggestionField {

    public RichSearchField() {
        setValidationVisible(false);
        serverRpc = new CubaSuggestionFieldServerRpc() {
            @Override
            public void searchSuggestions(String query) {
                if (searchExecutor != null) {
                    searchExecutor.accept(query);
                }
            }

            @Override
            public void selectSuggestion(String suggestionId) {
                Object suggestion = keyMapper.get(suggestionId);
                setValue(suggestion);

                updateTextPresentation(getValue());
            }

            @Override
            public void onEnterKeyPressed(String currentSearchString) {
                if (enterActionHandler != null) {
                    enterActionHandler.accept(currentSearchString);
                }
            }

            @Override
            public void onArrowDownKeyPressed(String currentSearchString) {
                if (arrowDownActionHandler != null) {
                    arrowDownActionHandler.accept(currentSearchString);
                }
            }

            @Override
            public void cancelSearch() {
                cancelSearchHandler.run();
            }
        };
        registerRpc(serverRpc);

        focusBlurRpc = new FieldEvents.FocusAndBlurServerRpcImpl(this) {
            private static final long serialVersionUID = - 780524775769549747L;

            @Override
            protected void fireEvent(Event event) {
                RichSearchField.this.fireEvent(event);
            }
        };
        registerRpc(focusBlurRpc);
    }
}
