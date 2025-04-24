import { Component } from '@angular/core';
import { CategoryManageComponent } from '../../components/category-manage/category-manage.component';
import { TaskManageComponent } from '../../components/task-manage/task-manage.component';
import { NavbarComponent } from '../../components/navbar/navbar.component';

@Component({
  selector: 'app-user-page',
  imports: [CategoryManageComponent, TaskManageComponent, NavbarComponent],
  templateUrl: './user-page.component.html',
  styleUrl: './user-page.component.css'
})
export class UserPageComponent {

}
