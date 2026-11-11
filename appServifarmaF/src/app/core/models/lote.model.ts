export interface Lote {
  id?: number;
  productoId: number;
  proveedorId: number;
  lote: string;
  numeroFactura?: string; // ✅ NUEVO
  autorizarVencimientoCorto?: boolean; // ✅ NUEVO (solo para el request)
  fechaIngreso: string; // Date en string (ISO)
  fechaVencimiento: string;
  cantidad: number;
  precioCompra: number;
  precioVenta: number;
  estado?: 'activo' | 'deteriorado' | 'vencido' | 'agotado';
  // Datos de relaciones (para mostrar)
  productoNombre?: string;
  proveedorRazonSocial?: string;
  createdAt?: string;
  updatedAt?: string;
  productoImagen?: string;
}