import { n as b, t as f, a as y, i as w, x as l, l as C, s as x, P, b as I, c as d, d as g, e as $, f as o, h as E, j as _, k as O, m as u, o as L, p as F, q as A, u as D, v as k, F as U } from "./copilot-VGmjfoSi.js";
import { B as K } from "./base-panel-f4d2zbj_.js";
const T = "copilot-outline-panel{padding:0;position:relative}copilot-outline-panel vaadin-grid::part(cell){padding:var(--space-100) var(--space-150);cursor:default}copilot-outline-panel vaadin-grid::part(selected-row){background:var(--blue-100);color:var(--color-high-contrast)}copilot-outline-panel vaadin-grid::part(cell):focus-visible,copilot-outline-panel vaadin-grid::part(row):focus-visible{outline:2px solid var(--blue-300);outline-offset:-2px}copilot-outline-panel vaadin-grid-tree-toggle::part(toggle){color:var(--border-color-high-contrast);opacity:0}copilot-outline-panel:hover vaadin-grid-tree-toggle::part(toggle){opacity:1}";
var j = Object.defineProperty, S = Object.getOwnPropertyDescriptor, m = (e, t, i, n) => {
  for (var r = n > 1 ? void 0 : n ? S(t, i) : t, s = e.length - 1, a; s >= 0; s--)
    (a = e[s]) && (r = (n ? a(t, i, r) : a(r)) || r);
  return n && r && j(t, i, r), r;
};
let c = class extends y {
  constructor() {
    super(), this.removers = [], this.text = "";
  }
  static get styles() {
    return w`
      :host {
        display: block;
        position: absolute;
        inset: 0;
        z-index: 100000;
        background: rgba(255, 255, 255, 0.6);
      }

      .text {
        padding: 1em;
        position: absolute;
        top: 50%;
        text-align: center;
        width: 100%;
        background: white;
      }
    `;
  }
  render() {
    return l`
      <div class="text">
        <a href="javascript:void(0);" @click="${this.signUpOrLogin}">Sign up</a> or
        <a href="javascript:void(0);" @click="${this.signUpOrLogin}">Log in</a> to ${this.text}.
      </div>
    `;
  }
  connectedCallback() {
    super.connectedCallback();
    const e = (t) => {
      t.data === "validate-license" && this.licenseValidated();
    };
    window.addEventListener("message", e), this.removers.push(() => window.removeEventListener("message", e)), ["focus", "click", "keydown", "keyup", "keypress"].forEach((t) => {
      const i = (n) => {
        const r = I(this);
        r && (this.contains(n.target) || n.composed && n.composedPath().includes(this) || (r.contains(n.target) || n.composed && n.composedPath().includes(r)) && (n.preventDefault(), n.stopPropagation(), t === "focus" && n.target.blur()));
      };
      document.body.addEventListener(t, i, { capture: !0 }), this.removers.push(() => document.body.removeEventListener(t, i, { capture: !0 }));
    });
  }
  async licenseValidated() {
    await C();
  }
  disconnectedCallback() {
    super.disconnectedCallback(), this.removers.forEach((e) => e());
  }
  async signUpOrLogin() {
    await x(`${P}log-in`, {}, (e) => {
      const { loginUrl: t } = e.data;
      t && window.open(t);
    });
  }
};
m([
  b({ type: String })
], c.prototype, "text", 2);
c = m([
  f("copilot-login-to-access")
], c);
var M = Object.defineProperty, H = Object.getOwnPropertyDescriptor, v = (e, t, i, n) => {
  for (var r = n > 1 ? void 0 : n ? H(t, i) : t, s = e.length - 1, a; s >= 0; s--)
    (a = e[s]) && (r = (n ? a(t, i, r) : a(r)) || r);
  return n && r && M(t, i, r), r;
};
function h(e) {
  if (e.currentTarget)
    return e.currentTarget.getEventContext(e).item;
}
let p = class extends K {
  constructor() {
    super(...arguments), this.expandedItems = [], this.initialExpandDone = !1, this.filter = (e) => d(e) ? !0 : !!g(e), this.getFilteredChildren = (e) => {
      const t = $(e);
      if (t.length === 0)
        return [];
      const i = t.filter(this.filter);
      return i.length === t.length ? t : t.flatMap((n) => i.includes(n) ? n : this.getFilteredChildren(n));
    }, this.dataProvider = (e, t) => {
      if (!this.reactApp)
        t([], 0);
      else if (!e.parentItem)
        t([this.reactApp], 1);
      else {
        const i = this.getFilteredChildren(e.parentItem);
        t(i, i.length);
      }
    };
  }
  connectedCallback() {
    super.connectedCallback(), this.componentTreeUpdated(), this.onCommand("component-tree-updated", () => this.componentTreeUpdated());
  }
  render() {
    return o.userInfo ? l`
      ${o.userInfo.proKey ? "" : l`<copilot-login-to-access .text=${"use the outline"}></copilot-login-to-access>`}
      <style>
        ${T}
      </style>
      <vaadin-grid
        all-rows-visible
        .dataProvider=${this.dataProvider}
        .selectedItems=${o.getSelections.map((e) => E(e.element))}
        @keydown=${this.gridKeyDown}
        @mousemove=${this.gridItemMouseMove}
        @click=${this.gridItemClick}>
        <vaadin-grid-tree-column
          auto-width
          .__getToggleContent=${this.renderToggleColumn}
          .__isLeafItem=${this.isLeafItem.bind(this)}></vaadin-grid-tree-column>
      </vaadin-grid>
    ` : l`Initializing...`;
  }
  renderToggleColumn(e, t) {
    let i = "";
    return g(t) ? i = "♦ " : _(t) && (i = "</> "), `${i}${O(t)}`;
  }
  isLeafItem(e) {
    return this.getFilteredChildren(e).length === 0;
  }
  gridKeyDown(e) {
    e.code === "Space" && !e.altKey && !e.metaKey && !e.ctrlKey && !e.shiftKey && (e.preventDefault(), e.stopPropagation());
  }
  gridItemMouseMove(e) {
    let t;
    const i = h(e);
    i && d(i) && (t = u(i)), t ? o.setHighlighted({ element: t }) : o.setHighlighted(void 0), e.preventDefault(), e.stopPropagation();
  }
  gridItemClick(e) {
    const t = h(e);
    if (!t || !d(t))
      return;
    !e.metaKey && !e.ctrlKey && o.clearSelection();
    const i = u(t);
    i ? o.isSelected(i) ? o.deselect(i) : o.select(i) : console.error("Unable to find element for selection", t), L("use-outline");
  }
  updated(e) {
    super.updated(e), this.initialExpandDone || this.reactApp && this.grid && (this.grid.expandedItems = [this.reactApp, ...F(this.reactApp)], this.initialExpandDone = !0);
  }
  componentTreeUpdated() {
    this.reactApp = A(), this.grid && (this.reactApp && (this.grid.expandedItems = this.grid.expandedItems.map((e) => D(e))), this.grid.clearCache()), this.requestUpdate();
  }
};
v([
  k("vaadin-grid")
], p.prototype, "grid", 2);
p = v([
  f("copilot-outline-panel")
], p);
const R = {
  header: "Outline",
  expanded: !0,
  draggable: !0,
  panelOrder: 0,
  panel: "left",
  floating: !1,
  tag: "copilot-outline-panel",
  showOn: [U.HillaReact]
}, V = {
  init(e) {
    e.addPanel(R);
  }
};
window.Vaadin.copilotPlugins.push(V);
export {
  p as CopilotOutlinePanel
};
