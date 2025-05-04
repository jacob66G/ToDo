import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';

interface Category {
  name: string;
}

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private baseURL = 'http://localhost:8080/api/categories'

  constructor(private http: HttpClient) { }

  getCategoryById(id: number): Observable<Category> {
    return this.http.get<Category>(`${this.baseURL}/${id}`);
  }

  createCategory(category: Category): Observable<Category>{
    return this.http.post<Category>(this.baseURL, category);
  }

  updateCategory(id: number, category: Category): Observable<Category> {
    return this.http.put<Category>(`${this.baseURL}/${id}`, category);
  }

  deleteCategory(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseURL}/${id}`);
  }
}