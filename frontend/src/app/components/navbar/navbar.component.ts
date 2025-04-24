import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
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
}
