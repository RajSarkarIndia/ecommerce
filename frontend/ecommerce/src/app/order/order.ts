import {ChangeDetectorRef, Component, inject, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ProductInfoForBuying} from './DTO/ProductInfoForBuying';
import {OrderResponse} from './DTO/OrderResponse';
import {PaymentStatus} from './Enum/PaymentStatus';
import {DeliveryStatus} from './Enum/DelhiveryStatus';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-order',
  imports:[CommonModule],
  templateUrl: './order.html',
  styleUrl: './order.css'
})
export class Order implements OnInit {

  private httpClient = inject(HttpClient);
  private cdr = inject(ChangeDetectorRef);



  PaymentStatus = PaymentStatus;
  DeliveryStatus = DeliveryStatus;

  allOrder?: OrderResponse[];
  orderInfo?: OrderResponse;

  // =========================
  // CREATE ORDER (Buy Now)
  // =========================
  buyNow(productsForPurchasing: ProductInfoForBuying[]) {
    this.httpClient.post<{ paymentUrl: string }>(
  'http://localhost:8080/order/create',
  productsForPurchasing,
  { withCredentials: true }
).subscribe({
  next: (response) => {
    window.location.href = response.paymentUrl;
  },
  error: (error) => {
    console.error(error);
  }
});
  }

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
    this.httpClient
      .get<OrderResponse[]>("http://localhost:8080/order/myOrders",{withCredentials: true})
      .subscribe({
        next: (response) => {
          this.allOrder = response;
          console.log("All orders fetched");
          this.cdr.detectChanges();

        },
        error: (error) => {
          console.log(error);
        }
      });
  }

  getOrderDetails(orderId: number) {
    this.httpClient
      .get<OrderResponse>("http://localhost:8080/order/viewOrder/" + orderId,{withCredentials: true})
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
      .delete<void>("http://localhost:8080/order/cancelOrder/" + orderId,{withCredentials: true})
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
