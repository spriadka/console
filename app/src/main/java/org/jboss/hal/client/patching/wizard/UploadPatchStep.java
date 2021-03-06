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
package org.jboss.hal.client.patching.wizard;

import com.google.gwt.safehtml.shared.SafeHtml;
import elemental2.dom.HTMLElement;
import org.jboss.hal.ballroom.wizard.WizardStep;

public class UploadPatchStep extends WizardStep<PatchContext, PatchState> {

    private final PatchElement uploadElement;

    public UploadPatchStep(final String title, final SafeHtml onError) {
        super(title);
        this.uploadElement = new PatchElement(onError);
    }

    @Override
    public HTMLElement asElement() {
        return uploadElement.asElement();
    }

    @Override
    public void reset(final PatchContext context) {
        context.file = null;
    }

    @Override
    protected void onShow(final PatchContext context) {
        uploadElement.reset();
    }

    @Override
    protected boolean onNext(final PatchContext context) {
        if (uploadElement.validate()) {
            context.file = uploadElement.getFiles().item(0);
            return true;
        }
        return false;
    }
}
