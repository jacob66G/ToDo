import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';

interface LoginBody {
  email: string;
  password: string;
}

interface LoginResponse {
  token: string;
}

interface RegisterBody {
  username: string;
  email: string;
  password: string;
  repeat_password: string;
}

interface RegisterResponse {
  id: string;
  username: string;
  email: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthorizationService {
  private baseURL = 'http://localhost:8080/api/auth'
  private loginUrl = `${this.baseURL}/login`;
  private registerUrl = `${this.baseURL}/register`;

  constructor(private http: HttpClient) { }

  login(credentials: LoginBody): Observable<LoginResponse>{
    return this.http.post<LoginResponse>(this.loginUrl, credentials);
  }

  register(credentials: RegisterBody): Observable<RegisterResponse>{
    console.log(credentials);
    const { username, email, password, repeat_password } = credentials;
    const emailRegex = /^\S+@\S+\.\S+$/; 

    if (!username || !email || !password || !repeat_password) {
      return throwError(() => new Error('Zostawiłeś gdzieś puste pole')); 
    }
    else if(!emailRegex.test(email)){
      return throwError(() => new Error('Niepoprawny format e-mail')); 
    }
    else if(username.length < 4){
      return throwError(() => new Error('Nazwa użytkownika nie może zawierać mniej niż 4 znaki')); 
    }
    else if(password.length < 6){
      return throwError(() => new Error('Hasło nie może zawierać mniej niż 6 znaków')); 
    }
    else if(password != repeat_password){
      return throwError(() => new Error('Hasło i Powtórz hasło muszą być takie same')); 
    }
    else{
      return this.http.post<RegisterResponse>(this.registerUrl, credentials);
    }
  }

  saveToken(token: string): void {
    localStorage.setItem('token', token);
  }

  getToken(): string | null {
    return localStorage.getItem('token')
  }

  logout(): void {
    localStorage.removeItem('token');
  }
}