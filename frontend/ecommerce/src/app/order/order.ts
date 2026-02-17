import {Component, inject, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ProductInfoForBuying} from './DTO/ProductInfoForBuying';
import {OrderResponse} from './DTO/OrderResponse';
import {PaymentStatus} from './Enum/PaymentStatus';
import {DeliveryStatus} from './Enum/DelhiveryStatus';

@Component({
  selector: 'app-order',
  standalone: true,
  templateUrl: './order.html',
  styleUrl: './order.css'
})
export class Order implements OnInit {

  private httpClient = inject(HttpClient);

  // ✅ EXPOSE ENUMS TO TEMPLATE
  PaymentStatus = PaymentStatus;
  DeliveryStatus = DeliveryStatus;

  allOrder?: OrderResponse[];
  orderInfo?: OrderResponse;

  // =========================
  // CREATE ORDER (Buy Now)
  // =========================
  buyNow(productsForPurchasing: ProductInfoForBuying[]) {
    this.httpClient.post<string>(
      'http://localhost:8080/order/create',
      productsForPurchasing
    ).subscribe({
      next: (response: string) => {
        console.log("Order created successfully");
        window.location.href = response;
      },
      error: (error: any) => {
        console.error("Order failed:", error);
      }
    });
  }

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
    this.httpClient
      .get<OrderResponse[]>("http://localhost:8080/order/myOrders")
      .subscribe({
        next: (response) => {
          this.allOrder = response;
          console.log("All orders fetched");
        },
        error: (error) => {
          console.log(error);
        }
      });
  }

  getOrderDetails(orderId: number) {
    this.httpClient
      .get<OrderResponse>("http://localhost:8080/order/viewOrder/" + orderId)
      .subscribe({
        next: (response) => {
          this.orderInfo = response;
          console.log("Order fetch successful");
        },
        error: (error) => {
          console.log(error);
        }
      });
  }

  cancelOrder(orderId: number): void {
    this.httpClient
      .delete<void>("http://localhost:8080/order/cancelOrder/" + orderId)
      .subscribe({
        next: () => {
          console.log("Order cancelled");
          this.loadOrders();
        },
        error: (error) => {
          console.log(error);
        }
      });
  }

}
