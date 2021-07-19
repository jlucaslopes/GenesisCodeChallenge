package main;

import Model.AssetsApiResponses.AssestsEndpoint;
import Model.AssetsApiResponses.Assets;
import Model.AssetsApiResponses.AssetsHistory;
import Model.AssetsFromCSV;
import Model.FinalResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class GenesisCodeChallenge {
    private static final String ASSETS_URI = "https://api.coincap.io/v2/assets/";

    public static void main(String[] args) throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(3);
        List<Future<AssetsResult>> futures = null;
        List<AssetsFromCSV> assets = readCSV();

        try {
            System.out.println("Now is "+ LocalDateTime.now().getHour()+":"+ LocalDateTime.now().getMinute()+":"+ LocalDateTime.now().getSecond());
            List<AssetsResult> tasks = new ArrayList<>();
            assets.forEach(assetsFromCSV -> tasks.add(new AssetsResult(assetsFromCSV)));

            futures = service.invokeAll(tasks);

            service.awaitTermination(5, TimeUnit.SECONDS);
            service.shutdown();

        }catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (service != null) {
                service.shutdownNow();
            }
        }

        List<FinalResult> results = new ArrayList<>();
        AtomicReference<Double> total = new AtomicReference<>((double) 0);

        futures.forEach(result -> {
            try {
                FinalResult finalResult = new FinalResult();
                Double quantity = result.get().assetsFromCSV.getQuantity();
                Double price = Double.parseDouble(result.get().getPrice());

                total.updateAndGet(v -> (double) (v + quantity * price));

                finalResult.setPosition(quantity * price);
                finalResult.setOriginalPrice(result.get().assetsFromCSV.getPrice());
                finalResult.setSymbol(result.get().assetsFromCSV.getSymbol());
                finalResult.setPerfomance(calculatePerformance(result.get()));

                results.add(finalResult);

            } catch (InterruptedException | ExecutionException ignored){}
        });
        results.stream().sorted(Comparator.comparing(FinalResult::getPerfomance)).collect(Collectors.toList());

        System.out.println("total="+total
                + ", best_asset= "+results.get(0).getSymbol()
                + ", best_performance= "+ String.format("%.2f",results.get(0).getPerfomance())
                + ", worst_asset= "+ results.get(results.size()-1).getSymbol()
                + ", worst_performance= " + String.format("%.2f", results.get(results.size()-1).getPerfomance()));


    }

    static Double calculatePerformance(AssetsResult results) {
        return Double.parseDouble(results.price) /results.getAssetsFromCSV().getPrice();
    }


    public static class AssetsResult implements Callable<AssetsResult> {
        private AssetsFromCSV assetsFromCSV;
        private String price;
        public AssetsResult(AssetsFromCSV assetsFromCSV) {
            this.assetsFromCSV = assetsFromCSV;
        }

        public AssetsResult(AssetsFromCSV assetsFromCSV, String price) {
            this.assetsFromCSV = assetsFromCSV;
            this.price = price;
        }

        public AssetsFromCSV getAssetsFromCSV() {
            return assetsFromCSV;
        }

        public String getPrice() {
            return price;
        }

        @Override
        public AssetsResult call() throws URISyntaxException, IOException, InterruptedException {
            System.out.println("Submitted request " + assetsFromCSV.getSymbol() +" at "
                    +LocalDateTime.now().getHour()+":"+ LocalDateTime.now().getMinute()+":"+ LocalDateTime.now().getSecond());
            String assetId = getAssetFromAPI(assetsFromCSV.getSymbol()).getId();
            String price = getPriceUsdFromAPI(assetId);
            return new AssetsResult(assetsFromCSV,price);
        }
    }



    public static Assets getAssetFromAPI(String symbol) throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> response = getAssetsResponse(symbol);

        ObjectMapper objectMapper = new ObjectMapper();
        AssestsEndpoint assestsEndpoint = objectMapper.readValue(response.body(), AssestsEndpoint.class);

        return assestsEndpoint.getAssets()
                .stream()
                .filter(assets -> symbol.equals(assets.getSymbol()))
                .collect(Collectors.toList())
                .get(0);
    }



    static HttpResponse<String> getAssetsResponse(String symbol) throws URISyntaxException, IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(new URI(ASSETS_URI+"?search="+symbol))
                .GET()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }



    public static String getPriceUsdFromAPI(String id) throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> response = getHistoryHttpResponse(id);

        ObjectMapper objectMapper = new ObjectMapper();
        AssetsHistory history = objectMapper.readValue(response.body(), AssetsHistory.class);


       return history.getHistoryDataList()
                .get(0)
                .getPriceUsd();
    }

    static HttpResponse<String> getHistoryHttpResponse(String id) throws URISyntaxException, IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(new URI(ASSETS_URI+ id +"/history?interval=d1&start=1617753600000&end=1617753601000"))
                .GET()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }



    public static List<AssetsFromCSV> readCSV() {
        String fileName = "assets.csv";
        ClassLoader classLoader = GenesisCodeChallenge.class.getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());
        List<AssetsFromCSV> list = new ArrayList<>();

        try ( BufferedReader br = Files.newBufferedReader(file.toPath());) {
            String line;
            line = br.readLine();
            while (line != null) {

                String[] vect = line.split(",");
                String symbol = vect[0];
                Double quantity = Double.parseDouble(vect[1]);
                Double price = Double.parseDouble(vect[2]);

                AssetsFromCSV assetsFromCSV = new AssetsFromCSV(symbol, quantity, price);
                list.add(assetsFromCSV);

                line = br.readLine();
            }
        }
        catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return list;
    }


}



