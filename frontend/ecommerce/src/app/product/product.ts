import {ChangeDetectorRef, Component, inject, OnInit} from '@angular/core';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {CommonModule} from '@angular/common';

import {ProductStatus} from './Enum/ProductStatus';
import {ProductCategory} from './Enum/ProductCategory';
import {ProductDTO} from './DTO/product-Model';
import {Router} from '@angular/router';

@Component({
  selector: 'app-product',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, CommonModule],
  templateUrl: './product.html',
  styleUrl: './product.css',
})
export class Product {
  showProducts: boolean = false;

  private http = inject(HttpClient);
  private cdr = inject(ChangeDetectorRef);
  private router = inject(Router);

  products: ProductDTO[] = [];
  selectedFile!: File;

  productStatuses = Object.values(ProductStatus);
  categoryOptions = Object.values(ProductCategory);

  addProductForm = new FormGroup({
    sku: new FormControl<string>(''),
    title: new FormControl<string>(''),
    description: new FormControl<string>(''),
    price: new FormControl<number>(0),
    stock: new FormControl<number>(0),
    status: new FormControl<ProductStatus>(ProductStatus.ACTIVE),
    categories: new FormControl<ProductCategory[]>([])
  });


  loadProducts(): void {
    this.http.get<ProductDTO[]>(
      "http://localhost:8080/product/postedProducts",
      {withCredentials: true}
    ).subscribe({
      next: (data) => {
        this.products = [...data];
        this.showProducts = true;
        this.cdr.detectChanges();   // 🔥 FORCE RENDER
        console.log("DATA RECEIVED:", data);

      },
      error: (err) => console.error("Load failed", err)
    });
  }

  addProduct(): void {

    const product = {
      ...this.addProductForm.value,
      categories: this.addProductForm.value.categories ?? []
    };

    this.http.post(
      "http://localhost:8080/product/new",
      product,
      {withCredentials: true}
    ).subscribe({
      next: () => {
        this.addProductForm.reset({
          status: ProductStatus.ACTIVE,
          categories: []
        });
        this.loadProducts();
      },
      error: (err) => console.error("Add failed", err)
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  addPhoto(productId: number): void {

    if (!this.selectedFile) return;

    const formData = new FormData();
    formData.append("image", this.selectedFile);

    this.http.post(
      "http://localhost:8080/product/add/image/" + productId,
      formData,
      {withCredentials: true}
    ).subscribe({
      next: () => {
        this.selectedFile = undefined as any;
        this.loadProducts();
      },
      error: (err) => console.error("Upload failed", err)
    });
  }

  deleteImage(productId: number, imageId: number): void {
    this.http.delete(
      "http://localhost:8080/product/delete/image/" + productId + "/" + imageId,
      {withCredentials: true}
    ).subscribe({
      next: () => this.loadProducts(),
      error: (err) => console.error("Delete image failed", err)
    });
  }

  deleteProduct(productId: number): void {
    this.http.delete(
      "http://localhost:8080/product/delete/" + productId,
      {withCredentials: true}
    ).subscribe({
      next: () => this.loadProducts(),
      error: (err) => console.error("Delete product failed", err)
    });
  }

  updateStatus(product: ProductDTO, newStatus: ProductStatus): void {

    const updatedProduct: ProductDTO = {
      ...product,
      status: newStatus
    };

    this.http.put(
      "http://localhost:8080/product/update/" + product.productId,
      updatedProduct,
      {withCredentials: true}
    ).subscribe({
      next: () => product.status = newStatus,
      error: (err) => console.error("Status update failed", err)
    });
  }

//buy product
  buyProduct(productId: number, quantity: number) {
    //place the order and show the pay now button
    this.http.put<String>("http://localhost:8080/product/buy/" + productId + "/" + quantity, {}, {withCredentials: true})
      .subscribe({
        next: (response) => {
          console.log("Order Placed Successfully\n" + response);
        },
        error: (error) => {
          console.log(error);
        }


      });

  }

  viewProductRoute(productId: number): void {
    this.router.navigate(['/product', productId]);


  }


}
