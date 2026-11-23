import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { OrdenCompraService } from '../../../core/services/orden-compra';
import { EstadoOrden, OrdenCompra } from '../../../core/models/orden-compra.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-ordenes-compra',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './ordenes-compra.html',
  styleUrls: ['./ordenes-compra.css']
})
export class OrdenesCompraComponent implements OnInit {
  ordenes: OrdenCompra[] = [];
  estado: EstadoOrden | '' = '';
  cargando: boolean = false;

  constructor(
    private ordenService: OrdenCompraService,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const estadoUrl = this.route.snapshot.queryParamMap.get('estado');
    if (estadoUrl) {
      this.estado = estadoUrl as EstadoOrden;
    }
    this.cargar();
  }

  cargar(): void {
    this.cargando = true;
    this.ordenService.listar(this.estado).subscribe({
      next: (data) => {
        this.ordenes = data;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar órdenes', err);
        this.cargando = false;
        this.cdr.detectChanges();
        Swal.fire('Error', err?.error?.mensaje || err?.error?.message || 'No se pudieron cargar las órdenes de compra', 'error');
      }
    });
  }

  claseEstado(estado: EstadoOrden): string {
    switch (estado) {
      case 'borrador': return 'bg-warning-subtle text-warning-emphasis border border-warning';
      case 'enviada':  return 'bg-primary-subtle text-primary border border-primary';
      case 'recibida': return 'bg-success-subtle text-success border border-success';
      default:         return 'bg-secondary-subtle text-secondary border border-secondary';
    }
  }
}
