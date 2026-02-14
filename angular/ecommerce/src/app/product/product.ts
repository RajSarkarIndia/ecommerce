import { Component, inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ProductStatus } from './Enum/ProductStatus';
import {ProductDTO} from './product-Model';


@Component({
  selector: 'app-product',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './product.html',
  styleUrl: './product.css',
})
export class Product implements OnInit {

  private http = inject(HttpClient);

  products: ProductDTO[] = [];
  selectedFile!: File;

  // expose enum values to template
  productStatuses = Object.values(ProductStatus) as ProductStatus[];

  addProductForm = new FormGroup({
    sku: new FormControl<string>(''),
    title: new FormControl<string>(''),
    description: new FormControl<string>(''),
    price: new FormControl<number>(0),
    stock: new FormControl<number>(0),
    status: new FormControl<ProductStatus>(ProductStatus.ACTIVE),
    categories: new FormControl<string>('') // comma separated
  });

  ngOnInit(): void {
    this.loadProducts();
  }

  // ===============================
  // LOAD PRODUCTS
  // ===============================
  loadProducts(): void {
    this.http.get<ProductDTO[]>(
      "http://localhost:8080/product/postedProducts",
      { withCredentials: true }
    ).subscribe({
      next: (data) => this.products = data,
      error: (err) => console.error("Load failed", err)
    });
  }

  // ===============================
  // ADD PRODUCT
  // ===============================
  addProduct(): void {

    const formValue = this.addProductForm.value;

    const product = {
      ...formValue,
      categories: formValue.categories
        ? formValue.categories.split(',').map(c => c.trim())
        : []
    };

    this.http.post(
      "http://localhost:8080/product/new",
      product,
      { withCredentials: true }
    ).subscribe({
      next: () => {
        console.log("Product added");

        this.addProductForm.reset({
          status: ProductStatus.ACTIVE
        });

        this.loadProducts();
      },
      error: (err) => console.error("Add failed", err)
    });
  }

  // ===============================
  // FILE SELECT
  // ===============================
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  // ===============================
  // ADD IMAGE
  // ===============================
  addPhoto(productId: number): void {

    if (!this.selectedFile) return;

    const formData = new FormData();
    formData.append("image", this.selectedFile);

    this.http.post(
      "http://localhost:8080/product/add/image/" + productId,
      formData,
      { withCredentials: true }
    ).subscribe({
      next: () => {
        console.log("Image uploaded");
        this.loadProducts();
      },
      error: (err) => console.error("Upload failed", err)
    });
  }

  // ===============================
  // DELETE IMAGE
  // ===============================
  deleteImage(productId: number, imageId: number): void {

    this.http.delete(
      "http://localhost:8080/product/delete/image/" + productId + "/" + imageId,
      { withCredentials: true }
    ).subscribe({
      next: () => this.loadProducts(),
      error: (err) => console.error("Delete image failed", err)
    });
  }

  // ===============================
  // DELETE PRODUCT
  // ===============================
  deleteProduct(productId: number): void {

    this.http.delete(
      "http://localhost:8080/product/delete/" + productId,
      { withCredentials: true }
    ).subscribe({
      next: () => this.loadProducts(),
      error: (err) => console.error("Delete product failed", err)
    });
  }

  // ===============================
  // UPDATE STATUS (NO CASTING NEEDED)
  // ===============================
  updateStatus(product: ProductDTO, newStatus: ProductStatus): void {

    const updatedProduct: ProductDTO = {
      ...product,
      status: newStatus
    };

    this.http.put(
      "http://localhost:8080/product/update/" + product.productId,
      updatedProduct,
      { withCredentials: true }
    ).subscribe({
      next: () => {
        product.status = newStatus;
        console.log("Status updated");
      },
      error: (err) => console.error("Status update failed", err)
    });
  }
}
