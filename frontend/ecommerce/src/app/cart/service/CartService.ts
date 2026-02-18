import { Injectable } from '@angular/core';
import {CartItem} from '../CartItem';


@Injectable({ providedIn: 'root' })
export class CartService {

  private CART_KEY = "cart";

  getCart(): CartItem[] {
    return JSON.parse(localStorage.getItem(this.CART_KEY) || "[]");
  }

  addToCart(productId: number, quantity: number) {

    const cart = this.getCart();
    const existing = cart.find(p => p.productId === productId);

    if (existing) {
      existing.quantity += quantity;
    } else {
      cart.push({ productId, quantity });
    }

    localStorage.setItem(this.CART_KEY, JSON.stringify(cart));
  }

  updateQuantity(productId: number, quantity: number) {

    const cart = this.getCart().map(item =>
      item.productId === productId ? { ...item, quantity } : item
    );

    localStorage.setItem(this.CART_KEY, JSON.stringify(cart));
  }

  removeItem(productId: number) {

    const cart = this.getCart().filter(item => item.productId !== productId);
    localStorage.setItem(this.CART_KEY, JSON.stringify(cart));
  }

  clearCart() {
    localStorage.removeItem(this.CART_KEY);
  }
}
