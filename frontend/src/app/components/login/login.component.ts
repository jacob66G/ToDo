import { Component } from '@angular/core';
import { AuthorizationService } from '../../services/authorization/authorization.service';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loginForm: FormGroup;

  constructor(private authService: AuthorizationService, private formBuilder: FormBuilder, private router: Router){
    this.loginForm = this.formBuilder.group({email: [''], password: ['']});
  }

  onSubmit(): void {
    const credentials = this.loginForm.value;

    console.log(credentials);

    this.authService.login(credentials).subscribe({
      next: (response) => {
        console.log('Token JWT:', response.token);
        this.authService.saveToken(response.token);
        this.router.navigate(['/user-page']);
      },
      error:() => {
        alert('Niepoprawny login lub hasło');
      }
    });
  }
}
