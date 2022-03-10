import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.Data;
import io.swagger.client.model.LiftRide;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class DriverClass {
    // upgrade EC2
    // threads and request number
    // client -> EC2
    static int numThreads = 0;
    static int numSkiers = 0;
    static int numLifts = 40;
    static int numRuns = 10;
    static String url = null;
    static List<Data> data = new ArrayList<>();
    final private static int MAX_RETRY = 5;
    final private static String FILE_NAME = "res/Data1.xlsx";
    static int successfulRequests = 0;
    static int unsuccessfulRequests = 0;

    private static int getRandom(int start, int end) {
        Random random = new Random();
        return random.nextInt(end) + start + 1;
    }

    private static void sendPostRequest(int requests, int startSkierId, int endSkierId, int startLiftRideTime, int endLiftRideTime) {
        SkiersApi api = new SkiersApi();
        ApiClient client = api.getApiClient();
        client.setBasePath(url);

        for (int i = 0; i < requests; i++) {
            Integer resortId = 1;
            String seasonId = "1";
            String dayId = "1";
            Integer skierId = getRandom(startSkierId, endSkierId);
            LiftRide liftRide = new LiftRide(getRandom(startLiftRideTime, endLiftRideTime), getRandom(0, 100), getRandom(0, 10));

            int count = 0;
            while (count <= MAX_RETRY) {
                try {
                    ApiResponse response = api.writeNewLiftRideWithHttpInfo(liftRide, resortId, seasonId, dayId, skierId);
                    Long startTime = System.currentTimeMillis();
                    api.writeNewLiftRide(liftRide, resortId, seasonId, dayId, skierId);
                    Long endTime = System.currentTimeMillis();
                    data.add(new Data(startTime, endTime, endTime - startTime, "POST", response.getStatusCode()));
                    successfulRequests++;
                    //System.out.println(String.format("Request sent = %d ms", endTime - startTime));
                    break;
                } catch (ApiException e) {
                    count++;
                    unsuccessfulRequests++;
                }
            }
        }
    }

    private static void setEnvironmentParameters(String[] args) {
        try {
           numThreads = Integer.parseInt(args[0]);
           numSkiers = Integer.parseInt(args[1]);
           if (args.length == 3) {
               url = args[2];
           } else if (args.length == 4) {
                numLifts = Integer.parseInt(args[2]);
                url = args[3];
           } else {
               numLifts = Integer.parseInt(args[2]);
               numRuns = Integer.parseInt(args[3]);
               url = args[4];
           }
        } catch (NumberFormatException e) {
            System.out.println("Invalid argument passed to the parameters list");
        }
    }

    private static void startPhase1() throws InterruptedException {
        final int threads = numThreads / 4;
        final int skierIDs = numSkiers / threads;
        final int requests = ((int) (numRuns * 0.2)) * skierIDs;
        CountDownLatch countDownLatch = new CountDownLatch((int) (0.2 * threads));

        final long startTime = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
            int nextValue = i + 1;
            final int startSkierId = i * skierIDs + 1;
            final int endSkierId = skierIDs * nextValue;
            Runnable thread = () -> {
                sendPostRequest(requests, startSkierId, endSkierId, 0 , 90);
                countDownLatch.countDown();
            };
            new Thread(thread).start();
        }
        countDownLatch.await();
        long endTime = System.currentTimeMillis();

        System.out.println(String.format("The time to execute the first phase is %d ms", endTime - startTime));
    }

    private static void startPhase2() throws InterruptedException {
        final int threads = numThreads;
        final int skierIDs = numSkiers / threads;
        final int requests = ((int) (numRuns * 0.6)) * skierIDs;
        CountDownLatch countDownLatch = new CountDownLatch((int) (0.2 * threads));

        final long startTime = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
            int nextValue = i + 1;
            final int startSkierId = i * skierIDs + 1;
            final int endSkierId = skierIDs * nextValue;
            Runnable thread = () -> {
                sendPostRequest(requests, startSkierId, endSkierId, 91, 360);
                countDownLatch.countDown();
            };
            new Thread(thread).start();
        }
        countDownLatch.await();
        long endTime = System.currentTimeMillis();

        System.out.println(String.format("The time to execute the second phase is %d ms", endTime - startTime));
    }

    private static void startPhase3() throws InterruptedException {
        final int threads = (int) (0.1 * numThreads);
        final int skierIDs = numSkiers / threads;
        final int requests = (int) (numRuns * 0.1);
        CountDownLatch countDownLatch = new CountDownLatch(threads);

        final long startTime = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
            int nextValue = i + 1;
            final int startSkierId = i * skierIDs + 1;
            final int endSkierId = skierIDs * nextValue;
            Runnable thread = () -> {
                sendPostRequest(requests, startSkierId, endSkierId, 361 , 420);
                countDownLatch.countDown();
            };
            new Thread(thread).start();
        }
        countDownLatch.await();
        long endTime = System.currentTimeMillis();

        System.out.println(String.format("The time to execute the third phase is %d ms", endTime - startTime));
    }

    private static void showDetails(long wallTime) {
        System.out.println(String.format("\nThe number of successful requests is %d", successfulRequests));
        System.out.println(String.format("The number of unsuccessful requests is %d\n", unsuccessfulRequests));
    }

    private static void fillData() throws IOException {
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Data");

        int rowCount = 0;
        Row header = sheet.createRow(rowCount);
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Start Time");
        headerCell = header.createCell(1);
        headerCell.setCellValue("End Time");
        headerCell = header.createCell(2);
        headerCell.setCellValue("Latency (in ms)");
        headerCell = header.createCell(3);
        headerCell.setCellValue("Request Type");
        headerCell = header.createCell(4);
        headerCell.setCellValue("Response Code");

        for (Data d : data) {
            rowCount++;
            Row row = sheet.createRow(rowCount);
            Cell cell = row.createCell(0);
            cell.setCellValue(d.getStartTime());
            cell = row.createCell(1);
            cell.setCellValue(d.getEndTime());
            cell = row.createCell(2);
            cell.setCellValue(d.getLatency());
            cell = row.createCell(3);
            cell.setCellValue(d.getRequestType());
            cell = row.createCell(4);
            cell.setCellValue(d.getResponseCode());
        }

        FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
        workbook.write(outputStream);
        workbook.close();
        System.out.println("The data has been exported to " + FILE_NAME);
    }

    private static void calculateDataSetResults(long wallTime) {
        Collections.sort(data);
        long totalLatency = 0L;
        long minimumResponseTime = Long.MAX_VALUE;
        long maximumResponseTime = Long.MIN_VALUE;
        for (Data d : data) {
            final long latency = d.getLatency();
            totalLatency += latency;
            if (latency > maximumResponseTime) maximumResponseTime = latency;
            if (latency < minimumResponseTime) minimumResponseTime = latency;
        }
        final long meanResponseTime = totalLatency / ( successfulRequests );
        final long medianResponseTime = (data.get(successfulRequests / 2).getLatency() + data.get(successfulRequests / 2 - 1).getLatency()) / 2;
        final float throughput = successfulRequests / (float) wallTime;

        System.out.println(String.format("\nThe findings from the data set collected are as follows:\n" +
                "1. Mean Response Time = %dms\n" +
                "2. Median Response Time = %dms\n" +
                "3. Minimum Response Time = %dms\n" +
                "4. Maximum Response Time = %dms\n" +
                "5. Throughput = %.2f\n" +
                "6. Wall Time = %dms", meanResponseTime, medianResponseTime, minimumResponseTime, maximumResponseTime, throughput, wallTime));
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length >= 3 && args.length <= 5) {
            setEnvironmentParameters(args);
            final long startTime = System.currentTimeMillis();
            startPhase1();
            startPhase2();
            startPhase3();
            final long endTime = System.currentTimeMillis();

            long wallTime = endTime - startTime;
            showDetails(wallTime);

            try {
                fillData();
                calculateDataSetResults(endTime - startTime);
            } catch (IOException e) {
                System.out.println("There was error while saving or loading the data");
            }
        } else {
            System.out.println("Invalid arguments passed to the program");
        }
    }

}
