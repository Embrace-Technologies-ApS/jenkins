import hudsonBehaviorSource from "../../../war/src/main/webapp/scripts/hudson-behavior.js?raw";
import { beforeEach, describe, expect, it } from "vitest";

/**
 * hudson-behavior.js is a classic script that publishes its functions as
 * globals, so evaluate it in the global scope rather than importing it.
 */
function loadHudsonBehavior() {
  globalThis.Behaviour = {
    register: () => {},
    specify: () => {},
    applySubtree: () => {},
  };
  // indirect eval, so that the top level `function`s of the script become globals
  (0, eval)(hudsonBehaviorSource);
}

/**
 * Builds a form item with a number input and its validation error area,
 * then registers the min/max validator on the input.
 */
function numberField({ min, max }) {
  const item = document.createElement("div");
  item.className = "jenkins-form-item";
  const input = document.createElement("input");
  input.type = "number";
  if (min !== undefined) input.setAttribute("min", min);
  if (max !== undefined) input.setAttribute("max", max);
  const area = document.createElement("div");
  area.className = "validation-error-area";
  item.append(input, area);
  document.body.append(item);
  registerMinMaxValidator(input);
  return { input, area };
}

/** Types the value into the input and fires the change handler, as a user would. */
function typeValue(input, value) {
  input.value = value;
  input.onchange.call(input);
  return input.targetElement.textContent;
}

describe("registerMinMaxValidator", () => {
  beforeEach(() => {
    document.body.innerHTML = "";
    loadHudsonBehavior();
  });

  it("says the min-only limit is inclusive and accepts the boundary", () => {
    const { input } = numberField({ min: "5" });

    expect(typeValue(input, "4")).toBe("This value should be at least 5");
    expect(typeValue(input, "5")).toBe("");
    expect(typeValue(input, "6")).toBe("");
  });

  it("says the max-only limit is inclusive and accepts the boundary", () => {
    const { input } = numberField({ max: "10" });

    expect(typeValue(input, "11")).toBe("This value should be at most 10");
    expect(typeValue(input, "10")).toBe("");
    expect(typeValue(input, "9")).toBe("");
  });

  it("keeps the combined message and accepts both boundaries when min and max are set", () => {
    const { input } = numberField({ min: "5", max: "10" });

    expect(typeValue(input, "4")).toBe("This value should be between 5 and 10");
    expect(typeValue(input, "5")).toBe("");
    expect(typeValue(input, "10")).toBe("");
    expect(typeValue(input, "11")).toBe(
      "This value should be between 5 and 10",
    );
  });
});
