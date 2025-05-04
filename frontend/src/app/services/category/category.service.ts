import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { Category } from '../../models/category';

@Injectable({
  providedIn: 'root'
})

export class CategoryService {
  private baseURL = 'http://localhost:8080/api/categories'
  private headers = this.createHeader();

  constructor(private http: HttpClient) { }

  createHeader(){
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return headers;
  }

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.baseURL, { headers: this.headers });
  }
  

  getCategoryById(id: number): Observable<Category> {
    return this.http.get<Category>(`${this.baseURL}/${id}`, { headers: this.headers });
  }

  createCategory(category: Category): Observable<Category>{
    return this.http.post<Category>(this.baseURL, category, { headers: this.headers });
  }

  updateCategory(id: number, category: Category): Observable<Category> {
    return this.http.put<Category>(`${this.baseURL}/${id}`, category, { headers: this.headers });
  }

  deleteCategory(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseURL}/${id}`, { headers: this.headers });
  }
}