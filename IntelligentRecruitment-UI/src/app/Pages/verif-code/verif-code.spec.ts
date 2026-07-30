import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerifCode } from './verif-code';

describe('VerifCode', () => {
  let component: VerifCode;
  let fixture: ComponentFixture<VerifCode>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerifCode],
    }).compileComponents();

    fixture = TestBed.createComponent(VerifCode);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
