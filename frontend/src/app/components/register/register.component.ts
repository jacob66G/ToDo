import { Component } from '@angular/core';
import { AuthorizationService } from '../../services/authorization/authorization.service';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  registerForm: FormGroup;

  constructor(private authService: AuthorizationService, private formBuilder: FormBuilder, private router: Router){
    this.registerForm = this.formBuilder.group({username: [''], email: [''], password: [''], repeat_password: ['']});
  }

  onSubmit(): void{
    const credentials = this.registerForm.value;
    //console.log(credentials);
    this.authService.register(credentials).subscribe({
      next: (response) => {
        //console.log('Użytkownik zarejestrowany:', response);
        alert('Rejestracja zakończona sukcesem!');
      },
      error: (err: any) => {
        if (err instanceof HttpErrorResponse) {
          const backendMsg = err.error?.message;
          const userMsg = backendMsg || err.message || 'Nieznany błąd serwera';
          alert(userMsg);
        }
        else if (err instanceof Error) {
          alert(err.message);
        }
        else {
          alert('Wystąpił nieoczekiwany błąd');
        }
      }
    });
  }
}
