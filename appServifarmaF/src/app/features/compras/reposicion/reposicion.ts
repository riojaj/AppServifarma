import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ReposicionService } from '../../../core/services/reposicion';
import { ReposicionSugerida } from '../../../core/models/reposicion.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-reposicion',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './reposicion.html',
  styleUrls: ['./reposicion.css']
})
export class ReposicionComponent implements OnInit {
  sugerencias: ReposicionSugerida[] = [];
  cargando: boolean = false;
  generando: boolean = false;
  totalEstimado: number = 0;

  constructor(
    private reposicionService: ReposicionService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarDatos();
  }

  /** Cantidad de productos que se incluirán en las órdenes (cantidad > 0). */
  get productosAComprar(): number {
    return this.sugerencias.filter(s => s.cantidadSugerida > 0).length;
  }

  cargarDatos(): void {
    this.cargando = true;
    this.reposicionService.obtenerSugerencias().subscribe({
      next: (data) => {
        // El backend omite proveedorId cuando es null: se normaliza para el <select>
        this.sugerencias = data.map(s => ({ ...s, proveedorId: s.proveedorId ?? null }));
        this.calcularTotal();
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar sugerencias', err);
        this.cargando = false;
        this.cdr.detectChanges();
        Swal.fire('Error', this.mensajeError(err, 'No se pudieron cargar las sugerencias de reposición'), 'error');
      }
    });
  }

  subtotal(item: ReposicionSugerida): number {
    return (item.cantidadSugerida || 0) * (item.precioEstimado || 0);
  }

  calcularTotal(): void {
    this.totalEstimado = this.sugerencias.reduce((acc, item) => acc + this.subtotal(item), 0);
  }

  onCantidadChange(item: ReposicionSugerida): void {
    item.cantidadSugerida = Math.max(0, Math.floor(Number(item.cantidadSugerida) || 0));
    this.calcularTotal();
  }

  /** Al cambiar el proveedor se actualiza el precio estimado de la fila. */
  onProveedorChange(item: ReposicionSugerida): void {
    const opcion = item.opciones.find(o => o.id === item.proveedorId);
    if (opcion) {
      item.proveedorSugerido = opcion.razonSocial;
      item.precioEstimado = opcion.precio;
    }
    this.calcularTotal();
  }

  trackByProducto(_: number, item: ReposicionSugerida): number {
    return item.productoId;
  }

  generarOrden(): void {
    const aComprar = this.sugerencias.filter(s => s.cantidadSugerida > 0);

    if (aComprar.length === 0) {
      Swal.fire('Aviso', 'No hay productos con cantidad mayor a 0 para generar la orden.', 'info');
      return;
    }

    const sinProveedor = aComprar.filter(s => s.proveedorId == null);
    if (sinProveedor.length > 0) {
      Swal.fire(
        'Falta el proveedor',
        `Elige un proveedor para: ${sinProveedor.map(s => s.nombreProducto).join(', ')}`,
        'warning'
      );
      return;
    }

    const cantidadProveedores = new Set(aComprar.map(s => s.proveedorId)).size;

    Swal.fire({
      title: '¿Generar Órdenes de Compra?',
      html: `<b>${aComprar.length}</b> producto(s) en <b>${cantidadProveedores}</b> orden(es) (una por proveedor).<br>` +
            `Total estimado: <b>S/ ${this.totalEstimado.toFixed(2)}</b><br>` +
            `<small class="text-muted">Quedarán en borrador: podrás revisarlas, generar el PDF y enviarlas.</small>`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#3b82f6',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Sí, generar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (!result.isConfirmed) return;

      this.generando = true;
      this.cdr.detectChanges();

      const items = aComprar.map(s => ({
        productoId: s.productoId,
        proveedorId: s.proveedorId as number,
        cantidad: s.cantidadSugerida
      }));

      this.reposicionService.convertirAOrden(items).subscribe({
        next: (ordenes) => {
          this.generando = false;
          this.cdr.detectChanges();
          // Una sola orden: se abre directamente. Varias: se muestra la lista de borradores.
          if (ordenes.length === 1) {
            this.router.navigate(['/ordenes-compra', ordenes[0].id]);
          } else {
            this.router.navigate(['/ordenes-compra'], { queryParams: { estado: 'borrador' } });
          }
        },
        error: (err) => {
          console.error(err);
          this.generando = false;
          this.cdr.detectChanges();
          Swal.fire('Error', this.mensajeError(err, 'Ocurrió un error al generar las órdenes de compra.'), 'error');
        }
      });
    });
  }

  private mensajeError(err: any, porDefecto: string): string {
    return err?.error?.mensaje || err?.error?.message || porDefecto;
  }
}
