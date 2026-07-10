package com.sree.swingengine.market.client.angel.dto;

import lombok.Data;

@Data
public class AngelLoginResponse {

    private Boolean status;

    private String message;

    private DataResponse data;

    @Data
    public static class DataResponse {

        private String jwtToken;

        private String refreshToken;

        private String feedToken;

    }
}