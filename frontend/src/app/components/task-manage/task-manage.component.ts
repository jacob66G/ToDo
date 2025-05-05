import { Component } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { NgFor, AsyncPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Task } from '../../models/task';
import { Category } from '../../models/category';
import { Status } from '../../models/status';

import { TaskService } from '../../services/task/task.service';
import { CategoryService } from '../../services/category/category.service';
import { StatusService } from '../../services/status/status.service';

@Component({
  selector: 'app-task-manage',
  imports: [ReactiveFormsModule, NgFor, AsyncPipe],
  templateUrl: './task-manage.component.html',
  styleUrl: './task-manage.component.css',
  standalone: true,
})
export class TaskManageComponent {
    taskForm: FormGroup;
    selectedTask: Task | null = null;
    categories: Observable<Category[]>;
    tasks: Observable<Task[]>;
    statuses: Observable<Status[]>;
  
    constructor(private taskService: TaskService, private formBuilder: FormBuilder, 
      private categoryService: CategoryService, private statusService: StatusService)
    {
      this.taskForm = this.formBuilder.group({title: [''], description: [''], category: null, status: null});
      this.categories = this.categoryService.categories;
      this.tasks = this.taskService.tasks;
      this.statuses = this.statusService.statuses;
      console.log(this.categories);
      console.log(this.statuses);
    }

    selectTask(task: Task) {
        this.selectedTask = task;
    }

    displayTask() {
      if (this.selectedTask) {
        this.taskForm.patchValue({
          title: this.selectedTask.title,
          description: this.selectedTask.description,
          category: this.selectedTask.category?.id,
          status: this.selectedTask.status?.id
        });
      }
    }

    
}
