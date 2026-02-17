import {Component, inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ProductDTO} from '../DTO/product-Model';

@Component({
  selector: 'app-product-view',
  imports: [],
  templateUrl: './product-view.html',
  styleUrl: './product-view.css',
})
export class ProductView {
  httpClient:HttpClient=inject(HttpClient);
productResponse?:ProductDTO;

  viewProduct(productId:number):void{
this.httpClient.get<ProductDTO>("http://localhost:8080/product/view/"+productId)
  .subscribe({
    next:(response)=>{
      this.productResponse=response;
    },
    error:(error)=>{
      console.log(error);
    }


  });

  }

//buy now

}
