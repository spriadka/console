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
package org.jboss.hal.ballroom.tree;

import elemental.js.util.JsArrayOf;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

/**
 * @author Harald Pehl
 */
@JsType(isNative = true)
public class Api<T> {

    @JsFunction
    @FunctionalInterface
    public interface OpenCallback {

        void opened();
    }

    @JsMethod(name = "get_node")
    public native Node<T> getNode(String id);

    @JsMethod
    public native JsArrayOf<Node<T>> get_selected(boolean full);

    @JsMethod(name = "open_node")
    public native void openNode(String id);

    @JsMethod(name = "open_node")
    public native void openNode(String id, OpenCallback callback);

    @JsMethod(name = "close_node")
    public native void closeNode(String id);

    @JsMethod(name = "refresh_node")
    public native void refreshNode(String id);

    @JsOverlay
    public final Node<T> getSelected() {
        JsArrayOf<Node<T>> selected = get_selected(true);
        return selected.isEmpty() ? null : selected.get(0);
    }

    @JsMethod(name = "select_node")
    public native void selectNode(String id, boolean suppressEvent, boolean preventOpen);

    @JsMethod(name = "deselect_all")
    public native void deselectAll(boolean suppressEvent);

    @JsMethod
    public native void destroy(boolean keepHtml);
}
