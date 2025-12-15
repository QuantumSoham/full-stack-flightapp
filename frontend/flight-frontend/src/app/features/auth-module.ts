import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AuthRoutingModule } from './auth-routing-module';


// This module is meant to group all authentication-related features
// Example: login, register, forgot-password (if added later)
//
// Right now it does NOT declare any components
// It only exists as a logical container
@NgModule({

  // No declarations here
  // This usually means components are either:
  // - standalone, or
  // - not added yet
  declarations: [],

  // CommonModule gives access to common directives (*ngIf, *ngFor)
  // AuthRoutingModule defines routes related to auth
  imports: [
    CommonModule,
    AuthRoutingModule
  ]
})
export class AuthModule { }
