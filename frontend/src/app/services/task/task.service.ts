import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { Task } from '../../models/task';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
    private tasksSubject = new BehaviorSubject<Task[]>([]);
    tasks = this.tasksSubject.asObservable();

    private baseURL = 'http://localhost:8080/api/tasks'
    private headers = this.createHeader();

    constructor(private http: HttpClient) { this.fetchTasks(); }
    
    fetchTasks() {
        this.http.get<Task[]>(this.baseURL, { headers: this.headers }).subscribe((data) => {
          this.tasksSubject.next(data);
        });
      }

    createHeader(){
      const token = localStorage.getItem('token');
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
      return headers;
    }

    getTasks(): Observable<Task[]> {
      return this.tasks;
    }
}
