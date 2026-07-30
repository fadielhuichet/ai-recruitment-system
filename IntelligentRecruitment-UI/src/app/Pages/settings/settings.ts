import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';



import {Header} from '../../Shared/header/header';
import {EmailConfigService} from '../../services/services/email-service/email-config-service';
import {EmailConfigDto} from '../../models/Dto/EmailConfigDto';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, FormsModule, Header],
  templateUrl: './settings.html',
  styleUrl: './settings.css'
})
export default class Settings implements OnInit {
  private emailConfigService = inject(EmailConfigService);

  config = signal<EmailConfigDto>({
    acceptSubject: '',
    acceptBody: '',
    refuseSubject: '',
    refuseBody: ''
  });

  loading = signal(true);
  saving  = signal(false);
  saved   = signal(false);
  error   = signal('');

  ngOnInit() {
    this.emailConfigService.getMyConfig().subscribe({
      next: (data) => {
        this.config.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Failed to load your email templates.');
        this.loading.set(false);
      }
    });
  }

  update(field: keyof EmailConfigDto, value: string) {
    this.saved.set(false);
    this.config.update(c => ({ ...c, [field]: value }));
  }

  save() {
    this.saving.set(true);
    this.saved.set(false);
    this.error.set('');

    this.emailConfigService.updateMyConfig(this.config()).subscribe({
      next: () => {
        this.saving.set(false);
        this.saved.set(true);
      },
      error: () => {
        this.saving.set(false);
        this.error.set('Failed to save templates. Please try again.');
      }
    });
  }
}
