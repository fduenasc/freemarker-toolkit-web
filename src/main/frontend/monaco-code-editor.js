import * as monaco from 'monaco-editor';
import EditorWorker from 'monaco-editor/editor/editor.worker.js?worker';
import JsonWorker from 'monaco-editor/language/json/json.worker.js?worker';
import HtmlWorker from 'monaco-editor/language/html/html.worker.js?worker';

globalThis.MonacoEnvironment = {
    getWorker(_workerId, label) {
        if (label === 'json') {
            return new JsonWorker();
        }
        if (label === 'html' || label === 'handlebars' || label === 'razor') {
            return new HtmlWorker();
        }
        return new EditorWorker();
    }
};

function applyMonacoTheme() {
    const theme = document.documentElement.getAttribute('theme') || '';
    const dark = theme.split(/\s+/).includes('dark');
    monaco.editor.setTheme(dark ? 'vs-dark' : 'vs');
}

applyMonacoTheme();

if (!globalThis.__monacoThemeObserver) {
    globalThis.__monacoThemeObserver = new MutationObserver(applyMonacoTheme);
    globalThis.__monacoThemeObserver.observe(document.documentElement, {
        attributes: true,
        attributeFilter: ['theme']
    });
}

class MonacoCodeEditor extends HTMLElement {

    constructor() {
        super();
        this._value = '';
        this._language = 'plaintext';
        this._readOnly = false;
        this._label = '';
        this._lastEmitted = '';
        this._applying = false;
        this._editor = null;
        this._timer = 0;
        this._onPointerDown = (event) => {
            const target = event.target;
            if (target instanceof Element && target.closest('vaadin-button, vaadin-menu-bar-item')) {
                this._emit();
            }
        };
    }

    connectedCallback() {
        if (this._editor) {
            return;
        }
        const host = document.createElement('div');
        host.className = 'monaco-host';
        this.replaceChildren(host);

        this._editor = monaco.editor.create(host, {
            value: this._value,
            language: this._language,
            readOnly: this._readOnly,
            theme: 'vs',
            automaticLayout: true,
            fixedOverflowWidgets: true,
            minimap: {enabled: false},
            fontSize: 13,
            fontFamily: 'Consolas, "Courier New", monospace',
            scrollBeyondLastLine: false,
            wordWrap: 'on',
            tabSize: 2,
            padding: {top: 8, bottom: 8},
            ariaLabel: this._label || 'Editor'
        });
        applyMonacoTheme();

        this._editor.onDidChangeModelContent(() => {
            if (!this._applying) {
                this._schedule();
            }
        });
        this._editor.onDidBlurEditorText(() => this._emit());
        document.addEventListener('pointerdown', this._onPointerDown, true);
    }

    disconnectedCallback() {
        document.removeEventListener('pointerdown', this._onPointerDown, true);
        clearTimeout(this._timer);
        if (this._editor) {
            this._editor.dispose();
            this._editor = null;
        }
    }

    get value() {
        return this._editor ? this._editor.getValue() : this._value;
    }

    set value(next) {
        const text = next == null ? '' : String(next);
        this._value = text;
        this._lastEmitted = text;
        if (this._editor && this._editor.getValue() !== text) {
            this._applying = true;
            this._editor.setValue(text);
            this._applying = false;
        }
    }

    get language() {
        return this._language;
    }

    set language(next) {
        this._language = next || 'plaintext';
        const model = this._editor?.getModel();
        if (model) {
            monaco.editor.setModelLanguage(model, this._language);
        }
    }

    get readOnly() {
        return this._readOnly;
    }

    set readOnly(next) {
        this._readOnly = next === true || next === 'true';
        this._editor?.updateOptions({readOnly: this._readOnly});
    }

    get label() {
        return this._label;
    }

    set label(next) {
        this._label = next == null ? '' : String(next);
        this._editor?.updateOptions({ariaLabel: this._label || 'Editor'});
    }

    _schedule() {
        clearTimeout(this._timer);
        this._timer = setTimeout(() => this._emit(), 400);
    }

    _emit() {
        clearTimeout(this._timer);
        if (!this._editor) {
            return;
        }
        const next = this._editor.getValue();
        this._value = next;
        if (next === this._lastEmitted) {
            return;
        }
        this._lastEmitted = next;
        this.dispatchEvent(new CustomEvent('value-changed', {bubbles: true}));
    }
}

customElements.define('monaco-code-editor', MonacoCodeEditor);
