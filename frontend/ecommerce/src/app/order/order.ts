import {Component, inject, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ProductInfoForBuying} from './DTO/ProductInfoForBuying';
import {ProductDTO} from './DTO/ProductResponse';
import {OrderResponse} from './DTO/OrderResponse';

@Component({
  selector: 'app-order',
  standalone: true,
  templateUrl: './order.html',
  styleUrl: './order.css'
})
export class Order implements OnInit {

  private httpClient = inject(HttpClient);
  private allOrderInfo?: ProductDTO[];
  private allOrder?: OrderResponse[];

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

//load all order
  ngOnInit() {
    this.httpClient.get<OrderResponse[]>("http://localhost:8080/order/myOrders")
      .subscribe({
        next: (response) => {
          this.allOrder = response;
          console.log("All order fetched");
        },
        error: (error) => {
          console.log(error);
        }

      });


  }


//cancel order item

  cancelOrder(orderId: number): void {
    this.httpClient.delete<void>("http://localhost:8080/order/cancelOrder/" + orderId, {withCredentials: true})
      .subscribe({
        next: (response) => {
          console.log("deleted");
        },
        error: (error) => {
          console.log(error);
        }


      });


  }


}
