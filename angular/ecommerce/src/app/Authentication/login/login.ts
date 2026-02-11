import {Component, inject} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {form} from '@angular/forms/signals';
import {LoginForm} from './login-form';
import {CommonModule} from '@angular/common';
import {AuthenticationService} from '../authentication-service';
import { CookieService } from 'ngx-cookie-service';


@Component({
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  cookie: CookieService = inject(CookieService);

  httpClient:HttpClient=inject(HttpClient);
  authenticationService:AuthenticationService=inject(AuthenticationService);

  loginForm:any=new FormGroup({

    email:new FormControl<String>('',[Validators.required, Validators.email]),
    password:new FormControl<String>('',[Validators.minLength(8),Validators.required])

    });

  login(): void {
    const loginFormData: LoginForm = this.loginForm.value as LoginForm;

    this.httpClient.post(
      'http://localhost:8081/login',
      loginFormData,
      { responseType: 'text' }
    ).subscribe({
      next: (response) => {
        this.cookie.set("Authorization",response);
        this.authenticationService.setAuthenticated(true);
      },
      error: (err) => {
        console.error(err);
        alert('Login failed');
      }
    });
  }


  //reset
  resetForm(form:any):void{
    form.reset();
  }

}
