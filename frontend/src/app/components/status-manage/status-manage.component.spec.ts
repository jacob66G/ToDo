import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StatusManageComponent } from './status-manage.component';

describe('StatusManageComponent', () => {
  let component: StatusManageComponent;
  let fixture: ComponentFixture<StatusManageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StatusManageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StatusManageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
