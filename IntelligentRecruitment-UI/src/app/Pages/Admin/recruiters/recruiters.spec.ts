import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Recruiters } from './recruiters';

describe('Recruiters', () => {
  let component: Recruiters;
  let fixture: ComponentFixture<Recruiters>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Recruiters],
    }).compileComponents();

    fixture = TestBed.createComponent(Recruiters);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
