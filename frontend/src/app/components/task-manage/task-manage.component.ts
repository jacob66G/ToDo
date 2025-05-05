import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule, NgFor, AsyncPipe } from '@angular/common';
import { Observable } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { take } from 'rxjs';
import { Task, BodyTaskAdd, BodyTaskEdit } from '../../models/task';
import { Category } from '../../models/category';
import { Status } from '../../models/status';
import { TaskService } from '../../services/task/task.service';
import { CategoryService } from '../../services/category/category.service';
import { StatusService } from '../../services/status/status.service';

@Component({
  selector: 'app-task-manage',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    NgFor,
    AsyncPipe
  ],
  templateUrl: './task-manage.component.html',
  styleUrls: ['./task-manage.component.css']
})
export class TaskManageComponent {
  taskForm: FormGroup;
  selectedTask: Task | null = null;
  categories: Observable<Category[]>;
  statuses:  Observable<Status[]>;
  tasks:     Observable<Task[]>;

  constructor(
    private fb: FormBuilder,
    private taskService: TaskService,
    private categoryService: CategoryService,
    private statusService: StatusService
  ) {
    this.taskForm = this.fb.group({
      title:       [''],
      description: [''],
      category:    null,
      status:      null
    });
    this.categories = this.categoryService.categories;
    this.statuses   = this.statusService.statuses;
    this.tasks      = this.taskService.tasks;
    //console.log(this.tasks);
  }

  selectTask(task: Task) {
    this.selectedTask = task;
    console.log('Wybrane zadanie:', task);
  
    this.taskForm.patchValue({
      title:       task.title,
      description: task.description
    });
  
    this.categories
      .pipe(take(1))
      .subscribe(cats => {
        const matchCat = cats.find(c => c.name === task.categoryName);
        if (matchCat) {
          this.taskForm.get('category')?.setValue(matchCat);
        } else {
          console.warn(`Nie znaleziono kategorii '${task.categoryName}'`);
        }
      });
  
    this.statuses
      .pipe(take(1))
      .subscribe(stats => {
        const matchStatus = stats.find(s => s.name === task.statusName);
        if (matchStatus) {
          this.taskForm.get('status')?.setValue(matchStatus);
        } else {
          console.warn(`Nie znaleziono statusu '${task.statusName}'`);
        }
      });
  }

  errorDisplay(err: any): void {
    if (err instanceof HttpErrorResponse) {
      const backendMsg = err.error?.message;
      const userMsg = backendMsg || err.message || 'Nieznany błąd serwera';
      alert(userMsg);
    } else if (err instanceof Error) {
      alert(err.message);
    } else {
      alert('Wystąpił nieoczekiwany błąd');
    }
  }

  onTaskCreate(){
    const rawForm = this.taskForm.value;

    const bodyTask: BodyTaskAdd = {
      title: rawForm.title,
      description: rawForm.description,
      categoryId: rawForm.category.id
    };

    console.log(bodyTask);
    this.taskService.createTask(bodyTask).subscribe({
      next: (response) => {
        console.log('Dodano kategorię:', response);
      },
      error: (err: any) => this.errorDisplay(err)
    });
  }

  onTaskEdit(){
    if(this.selectedTask == null)
      {
        alert("Najpierw wybierz zadanie, które chcesz edytować");
      }
      else
      {
        const rawForm = this.taskForm.value;

        const bodyTask: BodyTaskEdit = {
          title: rawForm.title,
          description: rawForm.description,
          categoryId: rawForm.category.id,
          statusId: rawForm.status.id
        };

        console.log(bodyTask);
        this.taskService.updateTask(this.selectedTask.id, bodyTask).subscribe({
          next: (response) => {
            console.log(response);
          },
          error: (err: any) => this.errorDisplay(err)
        })
      }
  }

  onTaskDelete(): void{
    if(this.selectedTask == null)
    {
      alert("Najpierw wybierz zadanie, które chcesz usunąć");
    }
    else
    {
      this.taskService.deleteTask(this.selectedTask.id).subscribe({
        next: () => {
          //console.log('Kategoria usunięta:');
        },
        error: (err: any) => this.errorDisplay(err)
      });
    }
  }
}