package com.example.proyecto.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnvioOrdenResponse {
    private String canal;
    /** Solo para WHATSAPP: enlace wa.me con el mensaje ya armado. */
    private String whatsappUrl;
    private String mensaje;
}
