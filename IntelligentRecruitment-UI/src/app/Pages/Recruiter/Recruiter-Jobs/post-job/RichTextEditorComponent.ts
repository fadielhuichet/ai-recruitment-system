import {
  booleanAttribute,
  Component, forwardRef, Input
} from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-rich-text-editor',
  standalone: true,
  imports: [CommonModule],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => RichTextEditorComponent),
      multi: true
    }
  ],
  templateUrl: './rich-text-editor.component.html',
})
export class RichTextEditorComponent implements ControlValueAccessor {
  @Input() placeholder = 'Write something...';
  @Input({transform: booleanAttribute}) hasError = false;

  hasSelection = false;

  private onChange: (val: string) => void = () => {};
  private onTouched: () => void = () => {};

  writeValue(value: string): void {
    const el = document.getElementById('rte-editor') as HTMLElement;
    if (el && value !== el.innerHTML) el.innerHTML = value ?? '';
  }

  registerOnChange(fn: any) { this.onChange = fn; }
  registerOnTouched(fn: any) { this.onTouched = fn; }

  onInput(event: Event) {
    const el = event.target as HTMLElement;
    this.onChange(el.innerHTML);
  }

  onBlur() { this.onTouched(); }

  checkSelection() {
    const sel = window.getSelection();
    this.hasSelection = !!sel && sel.toString().trim().length > 0;
  }

  format(type: 'heading' | 'bold' | 'normal') {
    const sel = window.getSelection();
    if (!sel || sel.rangeCount === 0 || !sel.toString().trim()) return;

    const range = sel.getRangeAt(0);
    const text = sel.toString();

    const anchor = range.commonAncestorContainer;
    const parentEl = anchor.nodeType === 3 ? anchor.parentElement : anchor as HTMLElement;
    const parentBlock = parentEl?.closest('p, h2, h3, div') as HTMLElement;

    let newEl: HTMLElement;

    if (type === 'heading') {
      newEl = document.createElement('h2');
      newEl.className = 'rte-heading';
      newEl.textContent = text;
      newEl.setAttribute('data-type', 'heading');
    } else if (type === 'bold') {
      const strong = document.createElement('strong');
      strong.textContent = text;
      range.deleteContents();
      range.insertNode(strong);
      sel.removeAllRanges();
      this.syncValue();
      this.hasSelection = false;
      return;
    } else {
      newEl = document.createElement('p');
      newEl.className = 'rte-paragraph';        // ← stable class
      newEl.textContent = text;
    }

    if (parentBlock && parentBlock.id !== 'rte-editor' && parentBlock.textContent?.trim() === text.trim()) {
      parentBlock.replaceWith(newEl);
    } else {
      range.deleteContents();
      range.insertNode(newEl);
    }

    sel.removeAllRanges();
    this.syncValue();
    this.hasSelection = false;
  }

  private syncValue() {
    const el = document.getElementById('rte-editor') as HTMLElement;
    if (el) this.onChange(el.innerHTML);
  }
}
