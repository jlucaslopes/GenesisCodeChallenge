package main;

import Model.AssetsApiResponses.Assets;
import Model.AssetsFromCSV;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class GenesisCodeChallengeTest {

    @Test
    void shouldGetStatusCode200AssetFromAPI() throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> btcResponse = GenesisCodeChallenge.getAssetsResponse("BTC");
        Assert.assertEquals(200, btcResponse.statusCode());
    }

    @Test
    void shouldGetStatusCode200WithNonExistsAssetFromAPI() throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> btcResponse = GenesisCodeChallenge.getAssetsResponse("HUASDAADS");
        Assertions.assertEquals(200, btcResponse.statusCode());
    }

    @Test
    void shouldReturnAnAssetFromAPI() throws URISyntaxException, IOException, InterruptedException {
        Assets btc = GenesisCodeChallenge.getAssetFromAPI("BTC");

        Assertions.assertTrue(btc instanceof Assets);
        Assertions.assertNotNull(btc);
        Assertions.assertEquals("bitcoin", btc.getId());

    }


    @Test
    void shouldGetStatusCode200HistoryFromAPI() throws URISyntaxException, IOException, InterruptedException {
        String priceUsd = GenesisCodeChallenge.getPriceUsdFromAPI("bitcoin");
        Assertions.assertTrue(new Double(priceUsd) instanceof Double);
    }

    @Test
    void shouldReturn200FromHistoryWithValidId() throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> bitcoin = GenesisCodeChallenge.getHistoryHttpResponse("bitcoin");
        Assert.assertEquals(200, bitcoin.statusCode());
    }

    @Test
    void shouldReturn200FromHistoryWithInvalidId() throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> bitcoin = GenesisCodeChallenge.getHistoryHttpResponse("bitcoin");
        Assert.assertEquals(200, bitcoin.statusCode());
    }

    @Test
    void shouldCalculatePerformance() {
        AssetsFromCSV assetsFromCSV = new AssetsFromCSV("Teste", (double) 1, (double) 100);
        GenesisCodeChallenge.AssetsResult results = new GenesisCodeChallenge.AssetsResult(assetsFromCSV, "150");
        Double performance = GenesisCodeChallenge.calculatePerformance(results);
        Assertions.assertEquals(1.5, performance);
    }


    @Test
    public void testAssetsResultConstructor() {
        AssetsFromCSV assetsFromCSV = new AssetsFromCSV("Symbol", 10.0, 10.0);

        GenesisCodeChallenge.AssetsResult actualAssetsResult = new GenesisCodeChallenge.AssetsResult(assetsFromCSV);
        assertSame(assetsFromCSV, actualAssetsResult.getAssetsFromCSV());
        assertNull(actualAssetsResult.getPrice());
    }

    @Test
    public void testAssetsResultConstructor2() {
        AssetsFromCSV assetsFromCSV = new AssetsFromCSV("Symbol", 10.0, 10.0);

        GenesisCodeChallenge.AssetsResult actualAssetsResult = new GenesisCodeChallenge.AssetsResult(assetsFromCSV,
                "1234.12");

        assertSame(assetsFromCSV, actualAssetsResult.getAssetsFromCSV());
        assertEquals("1234.12", actualAssetsResult.getPrice());
    }



}