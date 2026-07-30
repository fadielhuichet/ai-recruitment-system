// job-description-format.pipe.ts
import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Pipe({ name: 'formatJobDesc', standalone: true })
export class FormatJobDescPipe implements PipeTransform {
  constructor(private sanitizer: DomSanitizer) {}

  transform(value: string): SafeHtml {
    if (!value) return '';
    const formatted = value
      .split('\n')
      .map(line => {
        const trimmed = line.trim();
        // Heading: non-empty, no trailing period, not too long
        const isHeading = trimmed.length > 0 && !trimmed.endsWith('.') && trimmed.length < 60;
        return isHeading
          ? `<p class="text-slate-800 font-bold text-base mt-5 mb-2 tracking-wide">${trimmed}</p>`
          : `<p class="text-slate-600 text-sm leading-relaxed mb-1">${trimmed}</p>`;
      })
      .join('');
    return this.sanitizer.bypassSecurityTrustHtml(formatted);
  }
}
