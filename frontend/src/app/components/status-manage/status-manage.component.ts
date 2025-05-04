import { Component } from '@angular/core';
import { StatusService } from '../../services/status/status.service';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { NgFor } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Status } from '../../models/status';

@Component({
  selector: 'app-status-manage',
  imports: [ReactiveFormsModule, NgFor],
  standalone: true,
  templateUrl: './status-manage.component.html',
  styleUrl: './status-manage.component.css'
})
export class StatusManageComponent {
  statusForm: FormGroup;
  statuses: Status[] = [];
  selectedStatus: Status | null = null;

  constructor(private statusService: StatusService, private formBuilder: FormBuilder)
  {
    this.statusForm = this.formBuilder.group({name: ['']});
    this.loadCategories();
  }

  loadCategories() {
    this.statusService.getStatuses().subscribe((data) => {
      this.statuses = data;
    });
  }

  selectStatus(status: Status) {
    this.selectedStatus = status;
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

  onStatusAdd(): void {
    const category = this.statusForm.value;
    //console.log(category);
    
    this.statusService.createStatus(category).subscribe({
      next: (response) => {
        //console.log('Dodano kategorię:', response);
        this.loadCategories();
      },
      error: (err: any) => this.errorDisplay(err)
    });
  }

  onStatusEdit(): void {
    if(this.selectedStatus == null)
    {
      alert("Najpierw wybierz status, który chcesz edytować");
    }
    else
    {
      const status = this.statusForm.value;
      //console.log(category);
      this.statusService.updateStatus(this.selectedStatus.id, status).subscribe({
        next: (response) => {
          //console.log(response);
          this.loadCategories();
        },
        error: (err: any) => this.errorDisplay(err)
      })
    }
  }

  onStatusDelete(): void {
    if(this.selectedStatus == null)
    {
      alert("Najpierw wybierz status, który chcesz usunąć");
    }
    else
    {
      this.statusService.deleteStatus(this.selectedStatus.id).subscribe({
        next: () => {
          //console.log('Kategoria usunięta:');
          this.loadCategories();
        },
        error: (err: any) => this.errorDisplay(err)
      });
    }
  }
}
