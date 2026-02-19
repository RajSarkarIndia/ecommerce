import { Routes } from '@angular/router';
import {Login} from './Authentication/login/login';
import {Register} from './Authentication/register/register';
import {User} from './Authentication/user/user';
import {Product} from './product/product';
import {ProductView} from './product/product-view/product-view';
import {Cart} from './cart/cart';
import {Order} from './order/order';
import {authenticationGuard} from './routeGuard/authentication-guard';

export const routes: Routes = [
  {path:"login",component:Login,title:"Login"},
  {path:"register",component:Register,title:"Register"},
  {path:"profile",component:User,title:"Profile",canActivate: [authenticationGuard]},
  {path:"ProductProfile",component:Product,title:"Product",canActivate: [authenticationGuard]},
  {path:"product/:id",component:ProductView},
  {path:"cart",component:Cart,title:"cart"},
  {path:"order",component:Order,title:"Order",canActivate: [authenticationGuard]}


];
