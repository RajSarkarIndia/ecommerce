import {PaymentStatus} from '../Enum/PaymentStatus';
import {DeliveryStatus} from '../Enum/DelhiveryStatus';
import {OrderItemResponse} from './OrderItemResponse';

export interface OrderResponse {
  orderId: number;
  totalAmount: number;
  addressId: number;
  placedAt: Date;
  paymentStatus: PaymentStatus;
  deliveryStatus: DeliveryStatus;
  items: OrderItemResponse[];
}

