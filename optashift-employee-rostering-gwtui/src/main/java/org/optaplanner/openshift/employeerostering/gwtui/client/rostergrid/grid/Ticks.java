/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.openshift.employeerostering.gwtui.client.rostergrid.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.optaplanner.openshift.employeerostering.gwtui.client.rostergrid.model.LinearScale;
import org.optaplanner.openshift.employeerostering.gwtui.client.rostergrid.model.Viewport;
import org.optaplanner.openshift.employeerostering.gwtui.client.util.PageUtils;

public class Ticks<T> {

    private final LinearScale<T> scale;
    private final String className;
    private final Long position;
    private final Long stepSize;
    private final Long offset;
    private final Supplier<HTMLElement> divFactory;
    private final PageUtils pageUtils;

    private final List<IsElement> tickElements;

    Ticks(final LinearScale<T> scale,
          final String className,
          final Long position,
          final Long stepSize,
          final Long offset,
          final Supplier<HTMLElement> divFactory,
          final PageUtils pageUtils) {
        this.scale = scale;
        this.className = className;
        this.position = position;
        this.stepSize = stepSize;
        this.offset = offset;
        this.divFactory = divFactory;
        this.pageUtils = pageUtils;
        tickElements = new ArrayList<>();
    }

    public void drawAt(final IsElement target,
                       final Viewport<T> viewport,
                       final Function<T, String> tickText) {
        clear();

        final HTMLElement background = divFactory.get();
        background.classList.remove(className);
        background.classList.add(className + "-background");
        viewport.setAbsPositionInScreenPixels(() -> background, 0L);
        viewport.setSizeInScreenPixels(() -> background, scale.getEndInGridPixels());
        viewport.setAbsGroupPosition(() -> background, position);
        viewport.setGroupSizeInScreenPixels(() -> background, 1L);
        target.getElement().appendChild(background);
        tickElements.add(() -> background);

        Long start = (offset > 0) ? offset - stepSize : offset;
        for (Long i = start; i < scale.getEndInGridPixels(); i += stepSize) {
            final HTMLElement tick = divFactory.get();
            tick.textContent = tickText.apply(scale.toScaleUnits(i));
            viewport.setPositionInScreenPixels(() -> tick, i);
            viewport.setSizeInScreenPixels(() -> tick, stepSize);
            viewport.setAbsGroupPosition(() -> tick, position);
            viewport.setGroupSizeInScreenPixels(() -> tick, 1L);
            target.getElement().appendChild(tick);
            tickElements.add(() -> tick);
        }

        pageUtils.appendHeightConsumingElementsAsRow(tickElements);
    }

    public void clear() {
        pageUtils.removeHeightConsumingElementsAsRow(tickElements);
        tickElements.forEach((e) -> e.getElement().remove());
        tickElements.clear();
    }
}