/*
 * Copyright 2015-2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.hal.core.finder;

import com.google.gwt.core.client.GWT;
import elemental.dom.Element;
import org.jboss.gwt.elemento.core.Elements;
import org.jboss.gwt.elemento.core.IsElement;
import org.jboss.hal.meta.security.SecurityContext;
import org.jboss.hal.meta.security.SecurityContextAware;
import org.jboss.hal.resources.Constants;
import org.jboss.hal.resources.UIConstants;

import static org.jboss.gwt.elemento.core.EventType.click;
import static org.jboss.hal.core.finder.Finder.DATA_BREADCRUMB;
import static org.jboss.hal.resources.CSS.*;
import static org.jboss.hal.resources.Names.NOT_AVAILABLE;

/**
 * UI class for a single row in in a finder column. Only used internally in the finder.
 *
 * TODO Add an option to activate an inline progress element which sets a striped background for long running
 * actions like 'restart server group'. Think about replacing the actions with a cancel button
 *
 * @author Harald Pehl
 */
class FinderRow<T> implements IsElement, SecurityContextAware {

    private static final Constants CONSTANTS = GWT.create(Constants.class);
    private static final String FOLDER_ELEMENT = "folderElement";
    private static final String BUTTON_CONTAINER = "buttonContainer";
    private static final String TOOLTIP_TARGET = "tooltipTarget";

    private final Finder finder;
    private final FinderColumn<T> column;
    private final String nextColumn;
    private final String id;
    private final T item;
    private ItemActionHandler<T> primaryAction;
    private final PreviewContent previewContent;

    private final Element root;
    private final Element folderElement;
    private final Element buttonContainer;

    FinderRow(final Finder finder,
            final FinderColumn<T> column,
            final T item,
            final ItemDisplay<T> display,
            final PreviewCallback<T> previewCallback) {

        this.finder = finder;
        this.column = column;
        this.nextColumn = display.nextColumn();
        this.id = display.getId();
        this.item = item;
        this.primaryAction = display.actions().isEmpty() ? null : display.actions().iterator().next().handler;
        this.previewContent = previewCallback != null ? previewCallback.onPreview(item) : new PreviewContent(
                display.getTitle());

        Elements.Builder eb = new Elements.Builder().li()
                .id(display.getId())
                .data(DATA_BREADCRUMB, display.getTitle())
                .data(filter, display.getFilterData());

        if (display.getMarker() != null) {
            eb.css(display.getMarker().name().toLowerCase() + "-marker");
        }

        Element tooltipTarget;
        if (display.asElement() != null) {
            eb.add(display.asElement());
            tooltipTarget = display.asElement();
        } else if (display.getTitle() != null) {
            eb.span().css(itemText).textContent(display.getTitle()).rememberAs(TOOLTIP_TARGET).end();
            tooltipTarget = eb.referenceFor(TOOLTIP_TARGET);
        } else {
            eb.span().css(itemText).textContent(NOT_AVAILABLE).rememberAs(TOOLTIP_TARGET).end();
            tooltipTarget = eb.referenceFor(TOOLTIP_TARGET);
        }

        if (display.getTooltip() != null && tooltipTarget != null) {
            tooltipTarget.setTitle(display.getTooltip());
            tooltipTarget.getDataset().setAt(UIConstants.TOGGLE, UIConstants.TOOLTIP);
            tooltipTarget.getDataset().setAt(UIConstants.PLACEMENT, "top");
        }

        if (display.nextColumn() != null) {
            eb.span().css(folder, fontAwesome("angle-right")).rememberAs(FOLDER_ELEMENT).end();
        }

        if (!display.actions().isEmpty()) {
            if (display.actions().size() == 1) {
                ItemAction<T> action = display.actions().get(0);
                eb.button()
                        .css(btn, btnFinder)
                        .textContent(action.title)
                        .on(click, event -> action.handler.execute(item))
                        .rememberAs(BUTTON_CONTAINER)
                        .end();
            } else {
                boolean firstAction = true;
                boolean ulCreated = false;
                eb.div().css(btnGroup, pullRight).rememberAs(BUTTON_CONTAINER);
                for (ItemAction<T> action : display.actions()) {
                    if (firstAction) {
                        // @formatter:off
                        eb.button()
                                .css(btn, btnFinder)
                                .textContent(action.title)
                                .on(click, event -> action.handler.execute(item))
                        .end();
                        eb.button()
                                .css(btn, btnFinder, dropdownToggle)
                                .data(UIConstants.TOGGLE,  "dropdown") //NON-NLS
                                .aria(UIConstants.HAS_POPUP, String.valueOf(true))
                                .aria(UIConstants.EXPANDED, String.valueOf(false))
                            .span().css(caret).end()
                            .span().css(srOnly).textContent(CONSTANTS.toggleDropdown()).end()
                        .end();
                        // @formatter:on
                        firstAction = false;

                    } else {
                        if (!ulCreated) {
                            eb.ul().css(dropdownMenu);
                            ulCreated = true;
                        }
                        eb.li().a()
                                .textContent(action.title)
                                .css(clickable)
                                .on(click, event -> action.handler.execute(item))
                                .end().end();
                    }
                }
                eb.end().end(); // </ul> && </div>
            }
        }
        eb.end(); // </li>

        root = eb.build();
        folderElement = display.nextColumn() != null ? eb.referenceFor(FOLDER_ELEMENT) : null;
        buttonContainer = display.actions().isEmpty() ? null : eb.referenceFor(BUTTON_CONTAINER);
        Elements.setVisible(buttonContainer, false);

        root.setOnclick(event -> click());
    }

    void click() {
        column.markSelected(id);
        // <keep> this in order!
        finder.reduceTo(column);
        finder.updateContext();
        finder.publishContext();
        appendNextColumn();
        // </keep>
        showPreview();
    }

    void markSelected(boolean select) {
        if (select) {
            root.getClassList().add(active);
            if (buttonContainer != null) {
                Elements.setVisible(buttonContainer, true);
                Elements.setVisible(folderElement, false);
            }

        } else {
            root.getClassList().remove(active);
            Elements.setVisible(buttonContainer, false);
            Elements.setVisible(folderElement, true);
        }
    }

    void appendNextColumn() {
        if (nextColumn != null) {
            finder.appendColumn(nextColumn, null);
        }
    }

    void showPreview() {
        finder.showPreview(previewContent);
    }

    @Override
    public Element asElement() {
        return root;
    }

    @Override
    public void onSecurityContextChange(final SecurityContext securityContext) {

    }


    // ------------------------------------------------------ getter

    public String getId() {
        return id;
    }

    String getNextColumn() {
        return nextColumn;
    }

    ItemActionHandler<T> getPrimaryAction() {
        return primaryAction;
    }

    T getItem() {
        return item;
    }
}
