package com.haulmont.components.search.gui.components;

import com.haulmont.components.search.context.SearchContext;
import com.haulmont.components.search.presenter.SearchPresenter;
import com.haulmont.components.search.strategy.SearchEntry;
import com.haulmont.components.search.strategy.SearchStrategy;
import com.haulmont.cuba.gui.components.SuggestionField;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Declares view level component for highly customizable searching.
 * <br />
 * @see SearchPresenter
 * @see SearchContext
 * @see SearchEntry
 * @see SearchStrategy
 */
public interface RichSearch extends SuggestionField {
    String NAME = "richSearch";

    /**
     * Method must be called in component loader {@link com.haulmont.components.search.gui.xml.layouts.loaders.RichSearchLoader}.
     * It links search presentation level with component.
     * <br />
     * @param context search object with params
     * @param presenter bean, that contains specific search related configuration and has access to search model level
     * <br />
     * @see SearchPresenter#init(com.haulmont.components.search.gui.components.RichSearch, com.haulmont.components.search.context.SearchConfiguration)
     */
    void init(SearchContext context, SearchPresenter presenter);

    /**
     * Method must reset component state
     */
    void resetValue();

    /**
     * Adds value listener to component
     * listener will be invoked on entry choosing
     */
    void addValueChangeListener(BiConsumer<SearchContext, SearchEntry> listener);

    /**
     * Provide possibility to add named search strategy via controller
     * @param searchStrategy implementation
     * <br />
     * @see SearchPresenter#addStrategy(com.haulmont.components.search.strategy.SearchStrategy)
     */
    void addStrategy(SearchStrategy searchStrategy);

    /**
     * Provide possibility to add named search strategy via controller
     * by setting specific data searching method and invoke method
     * <br />
     * @param name of searching strategy
     * @param searcher function, that must present data entry list by specific query
     * @param invoker function, that must declare specific behavior on entry choosing
     * @param <T> depends on entry implementation
     * <br />
     * @see SearchPresenter#addStrategy(java.lang.String, java.util.function.BiFunction, java.util.function.BiConsumer)
     */
    <T extends SearchEntry> void addStrategy(String name, Function<String, List<T>> searcher,
                                             Consumer<T> invoker);

    /**
     * Removes strategy from component configuration
     * @param name of strategy
     * <br />
     * @see SearchPresenter#removeStrategy(java.lang.String)
     */
    void removeStrategy(String name);
}