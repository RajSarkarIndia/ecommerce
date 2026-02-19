import { Routes } from '@angular/router';
import {Login} from './Authentication/login/login';
import {Register} from './Authentication/register/register';
import {User} from './Authentication/user/user';
import {Product} from './product/product';
import {ProductView} from './product/product-view/product-view';
import {Cart} from './cart/cart';
import {Order} from './order/order';

export const routes: Routes = [
  {path:"login",component:Login},
  {path:"register",component:Register},
  {path:"profile",component:User},
  {path:"ProductProfile",component:Product},
  {path:"product/:id",component:ProductView},
  {path:"cart",component:Cart},
  {path:"order",component:Order}


];
