import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { Status } from '../../models/status';

@Injectable({
  providedIn: 'root'
})
export class StatusService {
  private baseURL = 'http://localhost:8080/api/statuses'
  private headers = this.createHeader();
  
    private regex = /^[a-zA-Z0-9 ,.:;!?()\-\+*/&^%$#@]*$/;
  
    constructor(private http: HttpClient) { }
  
    createHeader(){
      const token = localStorage.getItem('token');
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
      return headers;
    }
  
    validateStatus(category: Status): Observable<never> | null {
      if (!category.name || category.name.length < 5 || category.name.length > 20) {
        return throwError(() => new Error('Nazwa nie może mieć mniej niż 5 znaków i więcej niż 20 znaków'));
      } else if (!this.regex.test(category.name)) {
        return throwError(() => new Error('Nazwa posiada niedozwolone znaki'));
      }
      return null;
    }
  
    getStatuses(): Observable<Status[]> {
      return this.http.get<Status[]>(this.baseURL, { headers: this.headers });
    }
  
    getStatusById(id: number): Observable<Status> {
      return this.http.get<Status>(`${this.baseURL}/${id}`, { headers: this.headers });
    }
  
    createStatus(category: Status): Observable<Status> {
      const validationError = this.validateStatus(category);
      if (validationError) return validationError;
    
      return this.http.post<Status>(this.baseURL, category, { headers: this.headers });
    }
    
    updateStatus(id: number, category: Status): Observable<Status> {
      const validationError = this.validateStatus(category);
      if (validationError) return validationError;
    
      return this.http.put<Status>(`${this.baseURL}/${id}`, category, { headers: this.headers });
    }
  
    deleteStatus(id: number): Observable<void> {
      return this.http.delete<void>(`${this.baseURL}/${id}`, { headers: this.headers });
    }
}
