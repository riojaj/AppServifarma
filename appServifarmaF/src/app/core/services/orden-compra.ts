import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ActualizarOrdenRequest,
  CanalEnvio,
  EnvioOrdenResponse,
  EstadoOrden,
  OrdenCompra
} from '../models/orden-compra.model';

@Injectable({
  providedIn: 'root'
})
export class OrdenCompraService {
  private apiUrl = `${environment.apiUrl}/ordenes-compra`;

  constructor(private http: HttpClient) { }

  listar(estado?: EstadoOrden | ''): Observable<OrdenCompra[]> {
    let params = new HttpParams();
    if (estado) {
      params = params.set('estado', estado);
    }
    return this.http.get<OrdenCompra[]>(this.apiUrl, { params });
  }

  obtener(id: number): Observable<OrdenCompra> {
    return this.http.get<OrdenCompra>(`${this.apiUrl}/${id}`);
  }

  actualizar(id: number, body: ActualizarOrdenRequest): Observable<OrdenCompra> {
    return this.http.put<OrdenCompra>(`${this.apiUrl}/${id}`, body);
  }

  descargarPdf(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/pdf`, { responseType: 'blob' });
  }

  enviar(id: number, canal: CanalEnvio): Observable<EnvioOrdenResponse> {
    return this.http.post<EnvioOrdenResponse>(`${this.apiUrl}/${id}/enviar`, { canal });
  }

  anular(id: number): Observable<OrdenCompra> {
    return this.http.post<OrdenCompra>(`${this.apiUrl}/${id}/anular`, {});
  }
}
