import { Component, inject, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UserInfo } from './user-info';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [ReactiveFormsModule,CommonModule],
  templateUrl: './user.html',
  styleUrl: './user.css',
})
export class User implements OnInit {

  private http = inject(HttpClient);
  edit: boolean = true;

  userInfo?: UserInfo;
  addresses: any[] = [];

  userForm = new FormGroup({
    name: new FormControl(''),
    email: new FormControl('')
  });

  private loadUser(): void {
    this.http.get<UserInfo>('http://localhost:8080/getUser', {
      withCredentials: true
    }).subscribe(response => {
      this.userInfo = response;
      this.addresses = response.addresses ?? [];

      this.userForm.patchValue({
        name: response.name,
        email: response.email
      });
    });
  }

  ngOnInit() {
    this.loadUser();
  }

  resetForm() {
    if (!this.userInfo) return;

    this.userForm.patchValue({
      name: this.userInfo.name,
      email: this.userInfo.email
    });

    this.edit = false;
  }

  // edit form
  editForm() {
    this.edit = !this.edit;
  }

  updateForm(): void {
    this.http.put(
      'http://localhost:8080/editUser',
      this.userForm.value,
      { responseType: 'text', withCredentials: true }
    ).subscribe({
      next: res => {
        alert(res);
        this.edit = false;
        this.loadUser(); // ✅ added
      },
      error: err => {
        console.error(err);
        alert("Update failed");
      }
    });
  }

  showAddressForm = false;

  addressForm = new FormGroup({
    address: new FormControl(''),
    pincode: new FormControl(0),
    phoneNumber: new FormControl(0)
  });

  openAddressForm() {
    this.showAddressForm = true;
  }

  closeAddressForm() {
    this.showAddressForm = false;
    this.addressForm.reset();
  }

  submitAddress() {
    this.addAddress(this.addressForm.value);
    this.closeAddressForm();
  }

  addAddress(addressData: any): void {
    this.http.post(
      'http://localhost:8080/addAddress',
      addressData,
      { responseType: 'text', withCredentials: true }
    ).subscribe({
      next: res => {
        alert(res);
        this.loadUser(); // ✅ added
      },
      error: err => {
        console.error(err);
        alert("Add Address Failed");
      }
    });
  }

  updateAddress(id: number, addressData: any): void {
    this.http.put(
      `http://localhost:8080/updateAddress/${id}`,
      addressData,
      { responseType: 'text', withCredentials: true }
    ).subscribe({
      next: res => {
        alert(res);
        this.loadUser(); // ✅ added
      },
      error: err => {
        console.error(err);
        alert("Update Address Failed");
      }
    });
  }

  deleteAddress(id: number): void {
    this.http.delete(
      `http://localhost:8080/deleteAddress/${id}`,
      { responseType: 'text', withCredentials: true }
    ).subscribe({
      next: res => {
        alert(res);
        this.loadUser(); // ✅ added
      },
      error: err => {
        console.error(err);
        alert("Delete Address Failed");
      }
    });
  }

}
