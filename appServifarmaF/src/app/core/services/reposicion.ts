import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ItemReposicion, ReposicionResumen, ReposicionSugerida } from '../models/reposicion.model';
import { OrdenCompra } from '../models/orden-compra.model';

@Injectable({
  providedIn: 'root'
})
export class ReposicionService {
  private apiUrl = `${environment.apiUrl}/reposicion`;

  constructor(private http: HttpClient) { }

  obtenerSugerencias(): Observable<ReposicionSugerida[]> {
    return this.http.get<ReposicionSugerida[]>(`${this.apiUrl}/sugerencias`);
  }

  obtenerResumen(): Observable<ReposicionResumen> {
    return this.http.get<ReposicionResumen>(`${this.apiUrl}/resumen`);
  }

  /** Crea una orden en borrador por cada proveedor y devuelve las órdenes creadas. */
  convertirAOrden(items: ItemReposicion[]): Observable<OrdenCompra[]> {
    return this.http.post<OrdenCompra[]>(`${this.apiUrl}/convertir-orden`, items);
  }
}
