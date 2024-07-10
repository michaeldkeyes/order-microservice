package com.example.orderservice.stub;


import static com.github.tomakehurst.wiremock.client.WireMock.*;

public final class InventoryStub {

    private InventoryStub() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void stubInventoryCall(final String skuCode, final int quantity) {
        stubFor(get(urlEqualTo("/api/inventory?skuCode=" + skuCode + "&quantity=" + quantity))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")));
    }
}
