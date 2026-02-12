import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { LoginForm } from './login-form';
import { CommonModule } from '@angular/common';
import { AuthenticationService } from '../authentication-service';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css'],
})
export class Login {

  private httpClient = inject(HttpClient);
  private authenticationService = inject(AuthenticationService);
  private cookie = inject(CookieService);

  loginForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(8)])
  });

  login(): void {

    if (this.loginForm.invalid) {
      alert('Form is invalid');
      return;
    }

    const loginFormData: LoginForm = this.loginForm.value as LoginForm;

    console.log("Sending login request:", loginFormData);

    this.httpClient.post(
      'http://localhost:8080/login',
      loginFormData,
      { responseType: 'text' }
    ).subscribe({
      next: (response) => {
        console.log("Login success. Token:", response);

        this.cookie.set("Authorization", response, {
          path: '/'
        });

        this.authenticationService.setAuthenticated(true);
      },
      error: (err) => {
        console.error("Login error:", err);
        alert('Login failed');
      }
    });
  }

  resetForm(): void {
    this.loginForm.reset();
  }
}
