package com.example.proyecto.app.dto.request;

import lombok.Data;

@Data
public class EnviarOrdenCompraRequest {
    /** EMAIL o WHATSAPP */
    private String canal;
}
