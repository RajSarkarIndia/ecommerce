import {ChangeDetectorRef, Component, inject, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ProductDTO} from './DTO/ProductResponse';
import {Router} from '@angular/router';

@Component({
  selector: 'app-homepage',
  imports: [],
  templateUrl: './homepage.html',
  styleUrl: './homepage.css',
})
export class Homepage implements OnInit{
  product:ProductDTO[]=[];

  httpClient:HttpClient=inject(HttpClient);
  private cdr = inject(ChangeDetectorRef);
  router:Router=inject(Router);
ngOnInit() {
  this.allProduct();
}


//fetch all product
allProduct(){
this.httpClient.get<ProductDTO[]>("http://localhost:8080/product/all")
  .subscribe({
    next:(response)=>{
      this.product=response;
      this.cdr.detectChanges();
    },
    error:(error)=>{
      console.log(error);
    }


  });
}
  viewProduct(productId: number) {
    this.router.navigate(['/product', productId]);
  }




}
