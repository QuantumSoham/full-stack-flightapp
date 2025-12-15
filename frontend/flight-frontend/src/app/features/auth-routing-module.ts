import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

// Routes related to authentication
// This is where you would normally define paths like:
// /login
// /register
// /forgot-password
const routes: Routes = [];


@NgModule({

  // forChild() means:
  // "These routes are part of a feature module, not the root app"
  //
  // Angular will merge these routes into the main router
  imports: [RouterModule.forChild(routes)],

  // Export RouterModule so AuthModule can use routing directives
  exports: [RouterModule]
})
export class AuthRoutingModule { }
