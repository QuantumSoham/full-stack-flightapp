import { ComponentFixture, TestBed } from '@angular/core/testing';

import {ChangePasswordComponent as ChangePasswordComponentHtml } from './change-password.component';

describe('ChangePasswordComponentHtml', () => {
  let component: ChangePasswordComponentHtml;
  let fixture: ComponentFixture<ChangePasswordComponentHtml>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChangePasswordComponentHtml]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChangePasswordComponentHtml);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
