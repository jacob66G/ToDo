import { Component, EventEmitter, Output } from '@angular/core';
import { AuthorizationService } from '../../services/authorization/authorization.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {

  constructor(private authService: AuthorizationService, private router: Router){}

  @Output() categoryClicked = new EventEmitter<void>();
  @Output() statusClicked = new EventEmitter<void>();
  @Output() taskClicked = new EventEmitter<void>();

  onCategoryClick() {
    this.categoryClicked.emit();
  }

  onStatusClick() {
    this.statusClicked.emit();
  }

  onTaskClick() {
    this.taskClicked.emit();
  }

  onLogoutClick(){
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
