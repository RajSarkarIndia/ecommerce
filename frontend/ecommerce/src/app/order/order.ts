import {Component, inject, OnInit} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ProductInfoForBuying } from './DTO/ProductInfoForBuying';

@Component({
  selector: 'app-order',
  standalone: true,
  templateUrl: './order.html',
  styleUrl: './order.css'
})
export class Order implements OnInit{

  private httpClient = inject(HttpClient);
  private allOrderInfo:ProductResponse[];

  buyNow(productsForPurchasing: ProductInfoForBuying[]) {

    this.httpClient.post<string>(
      'http://localhost:8080/order/create',
      productsForPurchasing,
      {
        withCredentials: true   // send cookies
      }
    ).subscribe({

      next: (response: string) => {
        console.log("Order created successfully");

        // Redirect to Stripe checkout page
        window.location.href = response;
      },

      error: (error: any) => {
        console.error("Order failed:", error);
      }

    });
  }

ngOnInit(){


}






}
