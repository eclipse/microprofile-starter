/*
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.microprofile.starter.view.renderer;

import org.eclipse.microprofile.starter.addon.microprofile.servers.model.MicroprofileSpec;
import org.primefaces.component.selectmanycheckbox.SelectManyCheckbox;
import org.primefaces.component.selectmanycheckbox.SelectManyCheckboxRenderer;
import org.primefaces.component.tooltip.Tooltip;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.SelectItem;
import java.io.IOException;

public class CustomSelectManyCheckboxRenderer extends SelectManyCheckboxRenderer {

    private UIComponent labelFacet;
    private UIComponent tooltipFacet;

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        labelFacet = component.getFacet("label");
        tooltipFacet = component.getFacet("tooltip");
        super.encodeEnd(context, component);
    }

    protected void encodeOptionLabel(FacesContext context, SelectManyCheckbox checkbox, String containerClientId, SelectItem option,
                                     boolean disabled) throws IOException {

        if (labelFacet == null) {
            super.encodeOptionLabel(context, checkbox, containerClientId, option, disabled);
        } else {

            ResponseWriter writer = context.getResponseWriter();

            writer.startElement("label", null);
            if (disabled) {
                writer.writeAttribute("class", "ui-state-disabled", null);
            }

            writer.writeAttribute("for", containerClientId, null);
            // Added ':label' here so that we have an 'anchor' for the tooltip
            String target = containerClientId + UINamingContainer.getSeparatorChar(context) + "label";
            writer.writeAttribute("id", target, null);

            if (option.getDescription() != null) {
                writer.writeAttribute("title", option.getDescription(), null);
            }

            context.getExternalContext().getRequestMap().put("item", MicroprofileSpec.valueFor(option.getValue().toString()));
            encodeLabel(context, option, target);
            writer.endElement("label");
        }
    }


    protected void encodeLabel(FacesContext context, SelectItem option, String target) throws IOException {
        labelFacet.encodeAll(context);  // From the facet
        if (tooltipFacet != null) {
            // Add the tooltip for this option.

            Tooltip tooltip = new Tooltip();
            tooltip.setFor(target);
            tooltip.getChildren().add(tooltipFacet);

            encodeMarkupTooltip(context, tooltip, target);
            encodeScriptTooltip(context, tooltip, target);

        }

    }

    protected void encodeMarkupTooltip(FacesContext context, Tooltip tooltip, String target) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        if (target != null) {
            String styleClass = tooltip.getStyleClass();
            styleClass = styleClass == null ? Tooltip.CONTAINER_CLASS : Tooltip.CONTAINER_CLASS + " " + styleClass;
            styleClass = styleClass + " ui-tooltip-" + tooltip.getPosition();

            writer.startElement("div", tooltip);
            writer.writeAttribute("id", tooltip.getClientId(context), null);
            writer.writeAttribute("class", styleClass, "styleClass");

            if (tooltip.getStyle() != null) {
                writer.writeAttribute("style", tooltip.getStyle(), "style");
            }

            writer.startElement("div", tooltip);
            writer.writeAttribute("class", "ui-tooltip-arrow", null);
            writer.endElement("div");

            writer.startElement("div", tooltip);
            writer.writeAttribute("class", "ui-tooltip-text ui-shadow ui-corner-all", null);

            if (tooltip.getChildCount() > 0) {
                renderChildren(context, tooltip);
            } else {
                String valueToRender = ComponentUtils.getValueToRender(context, tooltip);
                if (valueToRender != null) {
                    if (tooltip.isEscape()) {
                        writer.writeText(valueToRender, "value");
                    } else {
                        writer.write(valueToRender);
                    }
                }
            }

            writer.endElement("div");

            writer.endElement("div");
        }
    }

    protected void encodeScriptTooltip(FacesContext context, Tooltip tooltip, String target) throws IOException {
        String clientId = tooltip.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Tooltip", tooltip.resolveWidgetVar(), clientId)
                .attr("showEvent", tooltip.getShowEvent(), null)
                .attr("hideEvent", tooltip.getHideEvent(), null)
                .attr("showEffect", tooltip.getShowEffect(), null)
                .attr("hideEffect", tooltip.getHideEffect(), null)
                .attr("showDelay", tooltip.getShowDelay(), 150)
                .attr("hideDelay", tooltip.getHideDelay(), 0)
                .attr("target", target, null)
                .attr("globalSelector", tooltip.getGlobalSelector(), null)
                .attr("escape", tooltip.isEscape(), true)
                .attr("trackMouse", tooltip.isTrackMouse(), false)
                .attr("position", tooltip.getPosition(), "right")
                .attr("delegate", tooltip.isDelegate(), false)
                .returnCallback("beforeShow", "function()", tooltip.getBeforeShow())
                .callback("onShow", "function()", tooltip.getOnShow())
                .callback("onHide", "function()", tooltip.getOnHide());

        wb.finish();
    }
}
