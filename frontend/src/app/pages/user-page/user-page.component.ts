import { Component } from '@angular/core';
import { CategoryManageComponent } from '../../components/category-manage/category-manage.component';
import { TaskManageComponent } from '../../components/task-manage/task-manage.component';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { StatusManageComponent } from '../../components/status-manage/status-manage.component';

@Component({
  selector: 'app-user-page',
  imports: [CategoryManageComponent, TaskManageComponent, NavbarComponent, StatusManageComponent],
  templateUrl: './user-page.component.html',
  styleUrl: './user-page.component.css'
})
export class UserPageComponent {
  showCategory = false;
  showStatus = false;
  showTask = false;

  toggleCategory(){
    this.showCategory = !this.showCategory;
  }

  toggleStatus(){
    this.showStatus = !this.showStatus;
  }

  toggleTask(){
    this.showTask = !this.showTask;
  }
}
