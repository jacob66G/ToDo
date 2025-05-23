import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { Category } from '../../models/category';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})

export class CategoryService {
  private categoriesSubject = new BehaviorSubject<Category[]>([]);
  categories = this.categoriesSubject.asObservable();

  private baseURL = 'http://localhost:8080/api/categories'
  private headers = this.createHeader();

  private regex = /^[a-zA-Z0-9 ,.:;!?()\-\+*/&^%$#@]*$/;

  constructor(private http: HttpClient) { this.fetchCategories(); }

  fetchCategories() {
    this.http.get<Category[]>(this.baseURL, { headers: this.headers }).subscribe((data) => {
      this.categoriesSubject.next(data);
    });
  }

  createHeader(){
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return headers;
  }

  validateCategory(category: Category): Observable<never> | null {
    if (!category.name || category.name.length < 5 || category.name.length > 20) {
      return throwError(() => new Error('Nazwa nie może mieć mniej niż 5 znaków i więcej niż 20 znaków'));
    } else if (!this.regex.test(category.name)) {
      return throwError(() => new Error('Nazwa posiada niedozwolone znaki'));
    }
    return null;
  }

  getCategories(): Observable<Category[]> {
    return this.categories;
  }

  getCategoryById(id: number): Observable<Category> {
    return this.http.get<Category>(`${this.baseURL}/${id}`, { headers: this.headers });
  }

  createCategory(category: Category): Observable<Category> {
    const validationError = this.validateCategory(category);
    if (validationError) return validationError;
  
    return this.http.post<Category>(this.baseURL, category, { headers: this.headers }).pipe(tap(() => this.fetchCategories()));
  }
  
  updateCategory(id: number, category: Category): Observable<Category> {
    const validationError = this.validateCategory(category);
    if (validationError) return validationError;
  
    return this.http.put<Category>(`${this.baseURL}/${id}`, category, { headers: this.headers }).pipe(tap(() => this.fetchCategories()));
  }

  deleteCategory(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseURL}/${id}`, { headers: this.headers }).pipe(tap(() => this.fetchCategories()));
  }
}