import { Component } from '@angular/core';
import { CategoryService } from '../../services/category/category.service';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { NgFor, AsyncPipe  } from '@angular/common';
import { Category } from '../../models/category';
import { HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-category-manage',
  imports: [ReactiveFormsModule, NgFor, AsyncPipe],
  templateUrl: './category-manage.component.html',
  styleUrl: './category-manage.component.css'
})
export class CategoryManageComponent {
  categoryForm: FormGroup;
  selectedCategory: Category | null = null;
  categories: Observable<Category[]>;

  constructor(private categoryService: CategoryService, private formBuilder: FormBuilder)
  {
    this.categoryForm = this.formBuilder.group({name: ['']});
    this.categories = this.categoryService.categories;
  }

  selectCategory(category: Category) {
    this.selectedCategory = category;
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

  onCategoryAdd(): void {
    const category = this.categoryForm.value;
    //console.log(category);
    
    this.categoryService.createCategory(category).subscribe({
      next: (response) => {
        //console.log('Dodano kategorię:', response);
      },
      error: (err: any) => this.errorDisplay(err)
    });
  }

  onCategoryEdit(): void {
    if(this.selectedCategory == null)
    {
      alert("Najpierw wybierz kategorię, którą chcesz edytować");
    }
    else
    {
      const category = this.categoryForm.value;
      //console.log(category);
      this.categoryService.updateCategory(this.selectedCategory.id, category).subscribe({
        next: (response) => {
          //console.log(response);
        },
        error: (err: any) => this.errorDisplay(err)
      })
    }
  }

  onCategoryDelete(): void {
    if(this.selectedCategory == null)
    {
      alert("Najpierw wybierz kategorię, którą chcesz usunąć");
    }
    else
    {
      this.categoryService.deleteCategory(this.selectedCategory.id).subscribe({
        next: () => {
          //console.log('Kategoria usunięta:');
        },
        error: (err: any) => this.errorDisplay(err)
      });
    }
  }
}
