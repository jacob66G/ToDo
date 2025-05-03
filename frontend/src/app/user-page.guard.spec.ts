import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { userPageGuard } from './user-page.guard';

describe('userPageGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => userPageGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
