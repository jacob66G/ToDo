import { Routes } from '@angular/router';
import { HomePageComponent } from './pages/home-page/home-page.component';
import { UserPageComponent } from './pages/user-page/user-page.component';

export const routes: Routes = [
    { path: '', component: HomePageComponent },
    { path: 'home-page', component: HomePageComponent },
    { path: 'user-page', component:  UserPageComponent},
];
