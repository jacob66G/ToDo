import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { Task, BodyTaskAdd, BodyTaskEdit } from '../../models/task';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
    private tasksSubject = new BehaviorSubject<Task[]>([]);
    tasks = this.tasksSubject.asObservable();

    private baseURL = 'http://localhost:8080/api/tasks'
    private headers = this.createHeader();

    private regex = /^[a-zA-Z0-9 ,.:;!?()\-\+*/&^%$#@]*$/;

    constructor(private http: HttpClient) { this.fetchTasks(); }
    
    fetchTasks() {
      this.http.get<Task[]>(this.baseURL, { headers: this.headers }).subscribe((data) => {
        this.tasksSubject.next(data);
      });
    }

    validateTask(task: BodyTaskAdd | BodyTaskEdit, isEdit: boolean = false): Observable<never> | null {
      const requiredFields = ['title', 'description', 'categoryId'];
      if (isEdit) requiredFields.push('statusId');
    
      for (const field of requiredFields) {
        if (!(task as any)[field]) {
          return throwError(() => new Error('Jedna z wymaganych wartości jest pusta'));
        }
      }
    
      if (task.title.length < 5 || task.title.length > 20) {
        return throwError(() => new Error('Tytuł nie może mieć mniej niż 5 znaków i więcej niż 20 znaków'));
      }
    
      if (task.description.length < 30 || task.description.length > 255) {
        return throwError(() => new Error('Opis nie może mieć mniej niż 30 znaków i więcej niż 255 znaków'));
      }
    
      if (!this.regex.test(task.title) || !this.regex.test(task.description)) {
        return throwError(() => new Error('Tytuł albo opis posiada niedozwolone znaki'));
      }
    
      return null;
    }

    createHeader(){
      const token = localStorage.getItem('token');
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
      return headers;
    }

    getTasks(): Observable<Task[]> {
      return this.tasks;
    }

    createTask(bodyTask: BodyTaskAdd): Observable<Task>{
      const validationError = this.validateTask(bodyTask);
      if (validationError) return validationError;

      return this.http.post<Task>(this.baseURL, bodyTask, { headers: this.headers }).pipe(tap(() => this.fetchTasks()));
    }

    updateTask(id: number, task: BodyTaskEdit): Observable<Task>{
      const validationError = this.validateTask(task);
      if (validationError) return validationError;

      return this.http.put<Task>(`${this.baseURL}/${id}`, task, { headers: this.headers }).pipe(tap(() => this.fetchTasks()));
    }

    deleteTask(id: number): Observable<void>{
      return this.http.delete<void>(`${this.baseURL}/${id}`, { headers: this.headers }).pipe(tap(() => this.fetchTasks()));
    }
}
