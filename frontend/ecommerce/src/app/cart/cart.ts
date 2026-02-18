import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { CartService } from './service/CartService';
import { CartItem } from './CartItem';
import { UserInfo } from './DTO/UserDTO';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cart.html',
  styleUrl: './cart.css',
})
export class Cart implements OnInit {

  private cartService = inject(CartService);
  private http = inject(HttpClient);

  cartItems: CartItem[] = [];
  userDetails?: UserInfo;   // optional for strict mode
  selectedAddressId: number | null = null;

  ngOnInit(): void {
    this.loadCart();
    this.fetchUser();
  }

  // ---------------------------
  // CART METHODS
  // ---------------------------

  loadCart() {
    this.cartItems = this.cartService.getCart();
  }

  increase(productId: number) {
    const item = this.cartItems.find(i => i.productId === productId);
    if (item) {
      this.cartService.updateQuantity(productId, item.quantity + 1);
      this.loadCart();
    }
  }

  decrease(productId: number) {
    const item = this.cartItems.find(i => i.productId === productId);
    if (item && item.quantity > 1) {
      this.cartService.updateQuantity(productId, item.quantity - 1);
      this.loadCart();
    }
  }

  remove(productId: number) {
    this.cartService.removeItem(productId);
    this.loadCart();
  }

  // ---------------------------
  // FETCH USER (COOKIE AUTH)
  // ---------------------------

  fetchUser() {
    this.http.get<UserInfo>(
      "http://localhost:8080/getUser",
      { withCredentials: true }   // ✅ send cookie
    ).subscribe({
      next: (data) => {
        this.userDetails = data;

        if (data.addresses && data.addresses.length > 0) {
          this.selectedAddressId = data.addresses[0].id;
        }
      },
      error: (err) => {
        console.log(err);
      }
    });
  }

  // ---------------------------
  // CHECKOUT (COOKIE AUTH)
  // ---------------------------

  checkout() {

    if (!this.selectedAddressId) {
      alert("Please select address");
      return;
    }

    const orderPayload = this.cartItems.map(item => ({
      productId: item.productId,
      quantity: item.quantity,
      addressId: this.selectedAddressId
    }));

    this.http.post(
      "http://localhost:8080/order/create",
      orderPayload,
      { withCredentials: true }   // ✅ send cookie
    ).subscribe({
      next: (paymentUrl: any) => {
        this.cartService.clearCart();
        window.location.href = paymentUrl;  // redirect to payment
      },
      error: (err) => {
        console.log(err);
        alert("Order failed!");
      }
    });
  }
}

