package com.sree.swingengine.market.client.angel.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AngelLoginRequest {

    private String clientcode;
    private String password;
    private String totp;

}