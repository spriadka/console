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
package org.jboss.hal.ballroom.table;

/**
 * Builder for data table {@linkplain Options options}. It's highly recommended to use this builder instead of creating
 * and assembling an options instance on your own:
 * <pre>
 * class FooBar {
 *     String foo;
 *     String bar;
 * }
 *
 * Options&lt;FooBar&gt; options = new OptionsBuilder&lt;FooBar&gt;()
 *     .button("Click Me", (event, api) -> Window.alert("Hello"))
 *     .column("foo", "Foo", (cell, type, row, meta) -> row.foo)
 *     .column("bar", "Bar", (cell, type, row, meta) -> row.baz)
 *     .build();
 * </pre>
 *
 * @param <T> the row type
 *
 * @author Harald Pehl
 */
public class OptionsBuilder<T> extends GenericOptionsBuilder<OptionsBuilder<T>, T> {

    @Override
    protected OptionsBuilder<T> that() {
        return this;
    }
}
