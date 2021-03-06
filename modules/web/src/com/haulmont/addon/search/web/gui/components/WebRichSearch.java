package com.haulmont.addon.search.web.gui.components;

import com.google.common.collect.Lists;
import com.haulmont.addon.search.context.SearchContext;
import com.haulmont.addon.search.gui.components.RichSearch;
import com.haulmont.addon.search.presenter.SearchPresenter;
import com.haulmont.addon.search.strategy.SearchEntity;
import com.haulmont.addon.search.strategy.SearchEntry;
import com.haulmont.addon.search.strategy.SearchStrategy;
import com.haulmont.addon.search.web.gui.components.toolkit.ui.RichSearchField;
import com.haulmont.chile.core.model.MetaPropertyPath;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.CaptionMode;
import com.haulmont.cuba.web.gui.components.WebSuggestionField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Implements Web Vaadin version of component
 *
 * @see SearchPresenter
 * @see com.haulmont.addon.search.presenter.impl.SearchPresenterImpl
 */
public class WebRichSearch extends WebSuggestionField implements RichSearch {

    private Logger log = LoggerFactory.getLogger(WebRichSearch.class);

    protected SearchContext context;
    protected SearchPresenter presenter;

    public WebRichSearch() {

        component = new RichSearchField();

        component.setTextViewConverter(obj -> {
            if (obj == null) {
                return StringUtils.EMPTY;
            }

            Entity entity = (Entity) obj;
            if (captionMode == CaptionMode.ITEM) {
                return textViewConverter.convertToPresentation(entity, String.class, userSession.getLocale());
            }

            if (StringUtils.isNotEmpty(captionProperty)) {
                MetaPropertyPath propertyPath = entity.getMetaClass().getPropertyPath(captionProperty);
                if (propertyPath == null) {
                    throw new IllegalArgumentException(String.format("Can't find property for given caption property: %s", captionProperty));
                }

                return metadataTools.format(entity.getValueEx(captionProperty), propertyPath.getMetaProperty());
            }

            log.warn("Using StringToEntityConverter to get entity text presentation. Caption property is not defined " +
                    "while caption mode is \"PROPERTY\"");
            return textViewConverter.convertToPresentation(entity, String.class, userSession.getLocale());
        });

        component.setSearchExecutor(query -> {
            cancelSearch();
            searchSuggestions(query);
        });

        component.setCancelSearchHandler(this::cancelSearch);

        attachListener(component);

        setCaptionProperty("caption");
        setCaptionMode(CaptionMode.PROPERTY);

        setSearchExecutor(this::search);

        addValueChangeListener((context, value) -> {
            if (presenter != null) {
                presenter.invoke(context, value);

                resetValue();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(SearchContext context, SearchPresenter presenter) {
        this.presenter = presenter;
        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetValue() {
        setValue(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addValueChangeListener(BiConsumer<SearchContext, SearchEntry> listener) {
        super.addValueChangeListener(e -> listener.accept(context,
                e.getValue() == null ? null : ((SearchEntity) e.getValue()).getDelegate()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addStrategy(SearchStrategy searchStrategy) {
        presenter.addStrategy(searchStrategy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends SearchEntry> void addStrategy(String name, Function<String, List<T>> searcher, Consumer<T> invoker) {
        presenter.addStrategy(name, (context, query) -> searcher.apply(query), (context, value) -> invoker.accept(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeStrategy(String name) {
        presenter.removeStrategy(name);
    }

    protected List<SearchEntity> search(String searchString, Map<String, Object> searchParams) {
        List<SearchEntity> searchResult = Lists.newArrayList();
        if (presenter != null) {
            searchResult = presenter.load(context.withParams(searchParams), searchString);
        }
        return CollectionUtils.isNotEmpty(searchResult) ? searchResult : Lists.newArrayList(SearchEntity.NO_RESULTS);
    }

}
