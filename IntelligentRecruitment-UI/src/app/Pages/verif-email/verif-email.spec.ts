import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerifEmail } from './verif-email';

describe('VerifEmail', () => {
  let component: VerifEmail;
  let fixture: ComponentFixture<VerifEmail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerifEmail],
    }).compileComponents();

    fixture = TestBed.createComponent(VerifEmail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
