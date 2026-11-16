import { Component, ChangeDetectorRef, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HomeService } from './home.service';
import { ProductListItem } from '../../core/models/product.model';
import { CartService } from '../../core/services/cart.service';
import { ToastService } from '../../core/services/toast.service';
import { ThreeBgComponent } from '../../shared/three-bg/three-bg.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, CommonModule, ThreeBgComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit {
  featured: ProductListItem[] = [];
  categories: { name: string; slug: string; icon: string }[] = [];

  private homeService = inject(HomeService);
  private cart = inject(CartService);
  private toast = inject(ToastService);
  protected cdr  = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.homeService.getProducts({ size: 8, sort: 'id,desc' }).subscribe({
      next: (p) => {
        this.featured = p.content;
        this.cdr.markForCheck();
      },
      error: () => {
        this.cdr.markForCheck();
      }
    });

    this.homeService.getCategories().subscribe({
      next: (cats) => {
        this.categories = cats.map(c => ({
          name: c.name,
          slug: c.slug,
          icon: this.getCategoryIcon(c.slug),
        }));
        this.cdr.markForCheck();
      },
      error: () => {
        this.cdr.markForCheck();
      }
    });
  }

  getCategoryIcon(slug: string): string {
    const map: Record<string, string> = {
      'whey-protein': 'fas fa-flask',
      'protein': 'fas fa-flask',
      'mass-gainer': 'fas fa-weight-hanging',
      'mass-gainers': 'fas fa-weight-hanging',
      'bcaa-eaa': 'fas fa-bolt',
      'bcaas': 'fas fa-bolt',
      'creatine': 'fas fa-fire-alt',
      'pre-workout': 'fas fa-running',
      'vitamins-minerals': 'fas fa-capsules',
      'vitamins': 'fas fa-capsules',
      'fat-burner': 'fas fa-fire',
      'weight-loss': 'fas fa-fire',
      'healthy-snacks': 'fas fa-cookie-bite',
      'snacks': 'fas fa-cookie-bite',
      'gym-accessories': 'fas fa-tshirt',
      'accessories': 'fas fa-tshirt',
    };
    return map[slug] ?? 'fas fa-dumbbell';
  }

  addToCart(product: ProductListItem): void {
    this.cart.addItem(product.id, 1);
    this.toast.success(`Đã thêm "${product.name}" vào giỏ hàng!`);
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('vi-VN').format(price) + ' ₫';
  }
}
