import { Component } from '@angular/core';
import { LoginComponent } from '../../components/login/login.component';
import { RegisterComponent } from '../../components/register/register.component';

@Component({
  selector: 'app-home-page',
  imports: [LoginComponent, RegisterComponent],
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.css'
})
export class HomePageComponent {
  loginVisible: boolean = true;

  loginVisibleChange(): void {
    this.loginVisible = !this.loginVisible;
  }
}
