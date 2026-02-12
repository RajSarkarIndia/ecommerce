import { Component, inject, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UserInfo } from './user-info';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './user.html',
  styleUrl: './user.css',
})
export class User implements OnInit {

  private http = inject(HttpClient);
  edit:boolean=true;

  userInfo?: UserInfo;

  userForm = new FormGroup({
    name: new FormControl(''),
    email: new FormControl('')
  });

  ngOnInit() {
    this.http.post<UserInfo>('http://localhost:8080/getUser', {},{withCredentials: true})
      .subscribe(response => {
        this.userInfo = response;

        this.userForm.patchValue({
          name: this.userInfo.name,
          email: this.userInfo.email
        });
      });
  }

  resetForm(){
    if (!this.userInfo) return;

    this.userForm.patchValue({
      name: this.userInfo.name,
      email: this.userInfo.email
    });

    this.edit = false;

  }

  //edit form
  editForm(){
    this.edit=!this.edit;


  }

  updateForm(): void {

    this.http.put('http://localhost:8080/editUser', this.userForm.value,  {responseType: 'text' ,withCredentials: true})
      .subscribe({
        next: res => {
          alert(res);
          this.edit = false;
        },
        error: err => {
          console.error(err);
          alert("Update failed");
        }
      });
  }
  get addresses() {
    return this.userInfo?.address ?? [];
  }
}
