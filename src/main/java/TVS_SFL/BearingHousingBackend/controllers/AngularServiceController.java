package TVS_SFL.BearingHousingBackend.controllers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Base64;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/angular")
@CrossOrigin(origins = "*")
public class AngularServiceController {

    // =========================================================
    // SERVICE CONFIGURATION
    // =========================================================

    /**
     * Service registry - Add new services here
     * Key: service-id (from frontend)
     * Value: WinSW executable path
     * 
     * TODO: Add Spring Boot service path
     * "spring-boot" -> "C:\\SpringBootService\\SpringBootService.exe"
     * 
     * TODO: Add other services as needed
     */
    private static final Map<String, String> SERVICE_PATHS = Map.of(
            "angular", "C:\\AngularService\\AngularService.exe",
            "node-red", "C:\\BearingHousingNoderedService\\BearingHousingNoderedService.exe"
            // TODO: Add Spring Boot: "spring-boot", "C:\\SpringBootService\\SpringBootService.exe"
    );

    /**
     * Windows service names used for reliable status queries.
     */
    private static final Map<String, String> WINDOWS_SERVICE_NAMES = Map.of(
            "angular", "AngularApplication",
            "node-red", "BearingHousingNoderedService"
    );

    /**
     * Angular application port - used for QR code generation
     * npm start -> ng serve -> port 4200
     */
    private static final int ANGULAR_PORT = 4200;

    /**
     * Maximum time allowed for a WinSW command.
     */
    private static final long COMMAND_TIMEOUT_SECONDS = 15;


    // =========================================================
    // GENERATE ANGULAR URL + QR CODE
    // =========================================================

    @GetMapping("/qr")
    public ResponseEntity<?> generateAngularQr() {

        try {

            // Find the PC's active usable IPv4 address
            String pcIp = getPcIpAddress();

            // Create Angular URL
            String angularUrl =
                    "https://" + pcIp + ":" + ANGULAR_PORT;

            // Generate QR
            String qrCode =
                    generateQrCode(angularUrl);

            Map<String, Object> response =
                    new LinkedHashMap<>();

            response.put("success", true);

            response.put("ip", pcIp);

            response.put("port", ANGULAR_PORT);

            response.put("url", angularUrl);

            response.put(
                    "qrCode",
                    qrCode
            );

            response.put(
                    "warning",
                    "Make sure your mobile and this PC " +
                    "are connected to the same network " +
                    "(LAN, Wi-Fi, or mobile hotspot)."
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            Map<String, Object> response =
                    new LinkedHashMap<>();

            response.put("success", false);

            response.put(
                    "message",
                    "Unable to generate Angular QR code."
            );

            response.put(
                    "error",
                    e.getMessage()
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }


    // =========================================================
    // START ANGULAR SERVICE
    // =========================================================

    @PostMapping("/start")
    public ResponseEntity<?> startAngular() {

        return executeWinSwCommand("start");
    }


    // =========================================================
    // STOP ANGULAR SERVICE
    // =========================================================

    @PostMapping("/stop")
    public ResponseEntity<?> stopAngular() {

        return executeWinSwCommand("stop");
    }


    // =========================================================
    // RESTART ANGULAR SERVICE
    // =========================================================

    @PostMapping("/restart")
    public ResponseEntity<?> restartAngular() {

        return executeWinSwCommand("restart");
    }


    // =========================================================
    // CHECK ANGULAR SERVICE STATUS
    // =========================================================

    @GetMapping("/status")
    public ResponseEntity<?> getAngularStatus() {

        return executeWinSwCommand("status");
    }


    // =========================================================
    // CHECK SPECIFIC SERVICE STATUS (Multiple Services)
    // =========================================================

    @GetMapping("/status/{serviceName}")
    public ResponseEntity<?> getServiceStatus(
            @PathVariable String serviceName) {

        return executeServiceCommand(serviceName, "status");
    }


    // =========================================================
    // RESTART SPECIFIC SERVICE (Multiple Services)
    // =========================================================

    @PostMapping("/restart/{serviceName}")
    public ResponseEntity<?> restartService(
            @PathVariable String serviceName) {

        return executeServiceCommand(serviceName, "restart");
    }


    // =========================================================
    // EXECUTE WINSW COMMAND
    // =========================================================

    private ResponseEntity<?> executeWinSwCommand(
            String command) {
        return executeServiceCommand("angular", command);
    }


    // =========================================================
    // ALLOWED WINSW COMMANDS
    // =========================================================

    private boolean isAllowedCommand(
            String command) {

        return command.equals("start")
                || command.equals("stop")
                || command.equals("restart")
                || command.equals("status");
    }


    // =========================================================
    // EXECUTE SERVICE COMMAND (Multiple Services)
    // =========================================================

    private ResponseEntity<?> executeServiceCommand(
            String serviceName,
            String command) {

        /*
         * Security:
         * 
         * Only predefined commands are allowed.
         * Only registered services are allowed.
         * 
         * Service names and paths come from SERVICE_PATHS map.
         */

        if (!isAllowedCommand(command)) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "success", false,
                                    "message",
                                    "Invalid service command."
                            )
                    );
        }

        if (!SERVICE_PATHS.containsKey(serviceName)) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "success", false,
                                    "message",
                                    "Service not registered: " + serviceName
                            )
                    );
        }

        String servicePath = SERVICE_PATHS.get(serviceName);

        try {

                        if (command.equals("status")) {
                                return queryWindowsServiceStatus(serviceName);
                        }

            ProcessBuilder processBuilder =
                    new ProcessBuilder(
                            servicePath,
                            command
                    );

            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            boolean completed =
                    process.waitFor(
                            COMMAND_TIMEOUT_SECONDS,
                            TimeUnit.SECONDS
                    );

            if (!completed) {

                process.destroyForcibly();

                return ResponseEntity
                        .status(
                                HttpStatus.GATEWAY_TIMEOUT
                        )
                        .body(
                                Map.of(
                                        "success", false,
                                        "service", serviceName,
                                        "command", command,
                                        "message",
                                        "Service command timed out."
                                )
                        );
            }

            String output =
                    new String(
                            process.getInputStream()
                                    .readAllBytes()
                    );

            int exitCode = process.exitValue();

            Map<String, Object> response =
                    new LinkedHashMap<>();

            response.put("success", exitCode == 0);
            response.put("service", serviceName);
            response.put("command", command);
            response.put("exitCode", exitCode);
            response.put("message", output.trim());

            if (exitCode == 0) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity
                        .status(
                                HttpStatus.INTERNAL_SERVER_ERROR
                        )
                        .body(response);
            }

        } catch (IOException e) {

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                            Map.of(
                                    "success", false,
                                    "service", serviceName,
                                    "command", command,
                                    "message",
                                    "Unable to execute service command.",
                                    "error",
                                    e.getMessage()
                            )
                    );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                            Map.of(
                                    "success", false,
                                    "service", serviceName,
                                    "command", command,
                                    "message",
                                    "Service command was interrupted."
                            )
                    );
        }
    }

    private ResponseEntity<?> queryWindowsServiceStatus(
            String serviceName) {

        String windowsServiceName = WINDOWS_SERVICE_NAMES.get(serviceName);

        if (windowsServiceName == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "success", false,
                            "service", serviceName,
                            "message", "Windows service name not configured."
                    ));
        }

        try {
            Process process = new ProcessBuilder(
                    "sc.exe",
                    "query",
                    windowsServiceName
            ).redirectErrorStream(true).start();

            boolean completed = process.waitFor(
                    COMMAND_TIMEOUT_SECONDS,
                    TimeUnit.SECONDS
            );

            if (!completed) {
                process.destroyForcibly();
                return ResponseEntity
                        .status(HttpStatus.GATEWAY_TIMEOUT)
                        .body(Map.of(
                                "success", false,
                                "service", serviceName,
                                "message", "Windows service status query timed out."
                        ));
            }

            String output = new String(
                    process.getInputStream().readAllBytes()
            );
            boolean isRunning = output.contains("RUNNING");

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", isRunning);
            response.put("service", serviceName);
            response.put("isRunning", isRunning);
            response.put("status", isRunning ? "Running" : "Stopped");
            response.put("message", output.trim());

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "service", serviceName,
                            "message", "Unable to query Windows service status.",
                            "error", e.getMessage()
                    ));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "service", serviceName,
                            "message", "Windows service status query was interrupted."
                    ));
        }
    }


    // =========================================================
    // FIND PC IPv4 ADDRESS
    // =========================================================

    private String getPcIpAddress()
            throws IOException {

        Enumeration<NetworkInterface> interfaces =
                NetworkInterface.getNetworkInterfaces();


        if (interfaces == null) {

            throw new IOException(
                    "No network interfaces found."
            );
        }


        /*
         * Look through all network interfaces.
         */
        while (interfaces.hasMoreElements()) {

            NetworkInterface networkInterface =
                    interfaces.nextElement();


            /*
             * Ignore:
             *
             * - Disabled interfaces
             * - Loopback interfaces
             * - Virtual interfaces
             */
            if (!networkInterface.isUp()
                    || networkInterface.isLoopback()
                    || networkInterface.isVirtual()) {

                continue;
            }


            Enumeration<InetAddress> addresses =
                    networkInterface.getInetAddresses();


            while (addresses.hasMoreElements()) {

                InetAddress address =
                        addresses.nextElement();


                /*
                 * We only want IPv4.
                 */
                if (!(address instanceof Inet4Address)) {

                    continue;
                }


                String ip =
                        address.getHostAddress();


                /*
                 * Ignore localhost.
                 */
                if (ip.startsWith("127.")) {

                    continue;
                }


                /*
                 * Ignore APIPA / link-local addresses.
                 *
                 * 169.254.x.x means the PC normally
                 * did not receive a valid network address.
                 */
                if (ip.startsWith("169.254.")) {

                    continue;
                }


                /*
                 * This is the usable IPv4 address
                 * assigned to the PC's active adapter.
                 */
                return ip;
            }
        }


        throw new IOException(
                "No usable PC IPv4 address found."
        );
    }


    // =========================================================
    // GENERATE QR CODE
    // =========================================================

    private String generateQrCode(
            String url)
            throws WriterException, IOException {


        QRCodeWriter qrCodeWriter =
                new QRCodeWriter();


        /*
         * Generate 300 x 300 QR code.
         */
        BitMatrix bitMatrix =
                qrCodeWriter.encode(
                        url,
                        BarcodeFormat.QR_CODE,
                        300,
                        300
                );


        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();


        /*
         * Convert QR matrix to PNG.
         */
        MatrixToImageWriter.writeToStream(
                bitMatrix,
                "PNG",
                outputStream
        );


        byte[] qrBytes =
                outputStream.toByteArray();


        /*
         * Convert PNG to Base64.
         *
         * Angular can directly use this:
         *
         * <img [src]="qrCode">
         */
        return "data:image/png;base64,"
                + Base64.getEncoder()
                        .encodeToString(qrBytes);
    }
}