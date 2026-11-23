export type EstadoOrden = 'borrador' | 'enviada' | 'recibida' | 'anulada';
export type CanalEnvio = 'EMAIL' | 'WHATSAPP';

export interface OrdenCompraDetalle {
  id: number;
  productoId: number;
  productoNombre: string;
  cantidad: number;
  precio: number;
  subtotal: number;
}

export interface OrdenCompra {
  id: number;
  numero: string;
  proveedorId: number;
  proveedorNombre: string;
  proveedorEmail?: string;
  proveedorTelefono?: string;
  usuarioId: number;
  fechaCreacion: string;
  estado: EstadoOrden;
  total: number;
  fechaEnvio?: string;
  canalEnvio?: string;
  notas?: string | null;
  detalles: OrdenCompraDetalle[];
}

export interface ActualizarOrdenRequest {
  notas: string | null;
  lineas: { detalleId: number; cantidad: number; precio: number }[];
}

export interface EnvioOrdenResponse {
  canal: CanalEnvio;
  whatsappUrl?: string;
  mensaje: string;
}
