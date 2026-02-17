import {Component, inject, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';
import {ProductDTO} from '../DTO/product-Model';

@Component({
  selector: 'app-product-view',
  standalone: true,
  templateUrl: './product-view.html',
  styleUrl: './product-view.css',
})
export class ProductView implements OnInit {

  private http = inject(HttpClient);
  private route = inject(ActivatedRoute);

  productResponse?: ProductDTO;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.viewProduct(id);
  }

  viewProduct(productId: number): void {
    this.http.get<ProductDTO>(
      "http://localhost:8080/product/view/" + productId
    ).subscribe({
      next: (response) => {
        this.productResponse = response;
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

}
