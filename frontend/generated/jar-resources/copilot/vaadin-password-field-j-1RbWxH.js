import { inputFieldProperties as r, labelProperties as a, helperTextProperties as s, errorMessageProperties as t } from "./vaadin-text-field-aZB4R9jc.js";
import { N as e } from "./copilot-VGmjfoSi.js";
import { standardButtonProperties as i } from "./vaadin-button-6rBltYAq.js";
const d = {
  tagName: "vaadin-password-field",
  displayName: "Password Field",
  elements: [
    {
      selector: "vaadin-password-field::part(input-field)",
      displayName: "Input field",
      properties: r
    },
    {
      selector: "vaadin-password-field::part(label)",
      displayName: "Label",
      properties: a
    },
    {
      selector: "vaadin-password-field::part(helper-text)",
      displayName: "Helper text",
      properties: s
    },
    {
      selector: "vaadin-password-field::part(error-message)",
      displayName: "Error message",
      properties: t
    },
    {
      selector: "vaadin-password-field::part(clear-button)",
      displayName: "Clear button",
      properties: i
    },
    {
      selector: "vaadin-password-field::part(reveal-button)",
      displayName: "Reveal button",
      properties: [e.iconColor, e.iconSize]
    }
  ]
};
export {
  d as default
};
