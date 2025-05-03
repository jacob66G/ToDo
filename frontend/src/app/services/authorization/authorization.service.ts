import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

interface LoginBody {
  username: string;
  password: string;
}

interface LoginResponse {
  token: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthorizationService {
  private baseURL = 'http://localhost:8080'
  private loginUrl = `${this.baseURL}/api/auth/login`;

  constructor(private http: HttpClient) { }

  login(credentials: LoginBody): Observable<LoginResponse>{
    return this.http.post<LoginResponse>(this.loginUrl, credentials);
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