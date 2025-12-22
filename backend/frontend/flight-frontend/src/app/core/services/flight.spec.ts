import { TestBed } from '@angular/core/testing';

import { FlightService } from './flight.service';

describe('Flight', () => {
  let service: FlightService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FlightService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
