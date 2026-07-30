import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecruiterJobDetails } from './recruiter-job-details';

describe('RecruiterJobDetails', () => {
  let component: RecruiterJobDetails;
  let fixture: ComponentFixture<RecruiterJobDetails>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecruiterJobDetails],
    }).compileComponents();

    fixture = TestBed.createComponent(RecruiterJobDetails);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
