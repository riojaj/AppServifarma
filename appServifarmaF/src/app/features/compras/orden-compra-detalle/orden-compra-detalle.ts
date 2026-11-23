import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Observable, of } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';
import { OrdenCompraService } from '../../../core/services/orden-compra';
import { CanalEnvio, EstadoOrden, OrdenCompra, OrdenCompraDetalle } from '../../../core/models/orden-compra.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-orden-compra-detalle',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './orden-compra-detalle.html',
  styleUrls: ['./orden-compra-detalle.css']
})
export class OrdenCompraDetalleComponent implements OnInit {
  orden: OrdenCompra | null = null;
  cargando: boolean = true;
  procesando: boolean = false;
  /** true cuando hay cambios sin guardar. */
  dirty: boolean = false;

  constructor(
    private ordenService: OrdenCompraService,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargar(Number(this.route.snapshot.paramMap.get('id')));
  }

  /** Solo las órdenes en borrador se pueden editar y enviar. */
  get editable(): boolean {
    return this.orden?.estado === 'borrador';
  }

  get total(): number {
    return (this.orden?.detalles ?? []).reduce((acc, d) => acc + this.subtotal(d), 0);
  }

  subtotal(d: OrdenCompraDetalle): number {
    return (d.cantidad || 0) * (d.precio || 0);
  }

  claseEstado(estado: EstadoOrden): string {
    switch (estado) {
      case 'borrador': return 'bg-warning-subtle text-warning-emphasis border border-warning';
      case 'enviada':  return 'bg-primary-subtle text-primary border border-primary';
      case 'recibida': return 'bg-success-subtle text-success border border-success';
      default:         return 'bg-secondary-subtle text-secondary border border-secondary';
    }
  }

  cargar(id: number): void {
    this.cargando = true;
    this.ordenService.obtener(id).subscribe({
      next: (orden) => {
        this.orden = orden;
        this.dirty = false;
        this.cargando = false;
        this.procesando = false;
        this.cdr.detectChanges();
      },
      error: (err) => this.mostrarError(err, 'No se pudo cargar la orden de compra')
    });
  }

  onCambio(detalle?: OrdenCompraDetalle): void {
    if (detalle) {
      detalle.cantidad = Math.max(0, Math.floor(Number(detalle.cantidad) || 0));
      detalle.precio = Math.max(0, Number(detalle.precio) || 0);
    }
    this.dirty = true;
  }

  /** Guarda cantidades, precios y notas. Una cantidad en 0 quita la línea de la orden. */
  private guardar$(): Observable<OrdenCompra> {
    const o = this.orden!;
    return this.ordenService.actualizar(o.id, {
      notas: o.notas ?? null,
      lineas: o.detalles.map(d => ({ detalleId: d.id, cantidad: d.cantidad, precio: d.precio }))
    }).pipe(
      tap(actualizada => {
        this.orden = actualizada;
        this.dirty = false;
      })
    );
  }

  guardar(): void {
    this.procesando = true;
    this.guardar$().subscribe({
      next: () => {
        this.procesando = false;
        this.cdr.detectChanges();
        Swal.fire({ toast: true, position: 'top-end', icon: 'success', title: 'Orden guardada', showConfirmButton: false, timer: 1800 });
      },
      error: (err) => this.mostrarError(err, 'No se pudo guardar la orden')
    });
  }

  generarPdf(): void {
    const o = this.orden!;
    this.procesando = true;
    // Si hay cambios sin guardar, se guardan antes para que el PDF salga actualizado
    const antes$: Observable<unknown> = this.editable && this.dirty ? this.guardar$() : of(null);

    antes$.pipe(switchMap(() => this.ordenService.descargarPdf(o.id))).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `${o.numero}.pdf`;
        a.click();
        URL.revokeObjectURL(url);
        this.procesando = false;
        this.cdr.detectChanges();
      },
      error: (err) => this.mostrarError(err, 'No se pudo generar el PDF')
    });
  }

  async enviar(): Promise<void> {
    const o = this.orden!;
    const opciones: Record<string, string> = {};
    if (o.proveedorEmail) {
      opciones['EMAIL'] = `Correo: ${o.proveedorEmail} (con el PDF adjunto)`;
    }
    if (o.proveedorTelefono) {
      opciones['WHATSAPP'] = `WhatsApp: ${o.proveedorTelefono}`;
    }

    if (Object.keys(opciones).length === 0) {
      Swal.fire('Proveedor sin contacto', 'Registra el correo o el teléfono del proveedor para poder enviar la orden.', 'warning');
      return;
    }

    const resultado = await Swal.fire({
      title: `Enviar ${o.numero}`,
      text: `Proveedor: ${o.proveedorNombre}. Al enviarla ya no podrás editarla.`,
      input: 'radio',
      inputOptions: opciones,
      inputValue: opciones['EMAIL'] ? 'EMAIL' : 'WHATSAPP',
      inputValidator: (valor) => (!valor ? 'Elige cómo enviarla' : null),
      showCancelButton: true,
      confirmButtonText: 'Enviar orden',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#3b82f6',
      cancelButtonColor: '#6b7280'
    });
    if (!resultado.isConfirmed) return;

    const canal = resultado.value as CanalEnvio;
    this.procesando = true;
    this.cdr.detectChanges();

    const antes$: Observable<unknown> = this.dirty ? this.guardar$() : of(null);

    antes$.pipe(switchMap(() => this.ordenService.enviar(o.id, canal))).subscribe({
      next: (envio) => {
        if (envio.whatsappUrl) {
          window.open(envio.whatsappUrl, '_blank');
        }
        Swal.fire('Orden enviada', envio.mensaje, 'success');
        this.cargar(o.id);
      },
      error: (err) => this.mostrarError(err, 'No se pudo enviar la orden')
    });
  }

  anular(): void {
    const o = this.orden!;
    Swal.fire({
      title: `¿Anular ${o.numero}?`,
      text: 'Esta acción no se puede deshacer.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc2626',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Sí, anular',
      cancelButtonText: 'Volver'
    }).then((r) => {
      if (!r.isConfirmed) return;
      this.procesando = true;
      this.cdr.detectChanges();
      this.ordenService.anular(o.id).subscribe({
        next: () => this.cargar(o.id),
        error: (err) => this.mostrarError(err, 'No se pudo anular la orden')
      });
    });
  }

  private mostrarError(err: any, porDefecto: string): void {
    console.error(err);
    this.cargando = false;
    this.procesando = false;
    this.cdr.detectChanges();
    Swal.fire('Error', err?.error?.mensaje || err?.error?.message || porDefecto, 'error');
  }
}
