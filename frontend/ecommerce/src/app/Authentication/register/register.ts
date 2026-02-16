import {Component, inject} from '@angular/core';
import {Form, FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {User} from './user';
import {HttpClient} from '@angular/common/http';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-register',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  httpClient:HttpClient=inject(HttpClient);

  //register form
  registerForm=new FormGroup({

    name:new FormControl('',[Validators.min(3)]),
    email:new FormControl('',Validators.email),
    password:new FormControl('',Validators.minLength(8))

  });
  resetForm(form:Form){
    this.registerForm.reset();
  }

  register() {
    const userData: User = this.registerForm.value as User;

    this.httpClient.post<string>("http://localhost:8080/user/register", userData)
      .subscribe({
        next: (response) => {
          console.log("Server response:", response);
          alert("Registration successful: " + response);
        },
        error: (err) => {
          console.error("Registration failed", err);
          alert("Error: " + err.error);
        }
      });
  }



}
