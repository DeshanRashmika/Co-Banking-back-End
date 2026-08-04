package edu.icet.banking.auth.api;

import edu.icet.banking.auth.api.dto.TotpSetupResponse;
import edu.icet.banking.auth.api.dto.VerifyTotpRequest;
import edu.icet.banking.auth.application.TotpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/2fa")
public class TwoFactorAuthController {

    private final TotpService totpService;
    private static final String BANK_NAME = "Co-Banking";

    public TwoFactorAuthController(TotpService totpService) {
        this.totpService = totpService;
    }

    @PostMapping("/setup")
    public ResponseEntity<TotpSetupResponse> setup2FA(@RequestParam String email) {
        try {
            String secretKey = totpService.generateSecretKey();
            String qrCodeDataUrl = totpService.generateQrCodeBase64(email, secretKey, BANK_NAME);

            return ResponseEntity.ok(new TotpSetupResponse(secretKey, qrCodeDataUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify2FA(@RequestBody VerifyTotpRequest request) {
        boolean isValid = totpService.verifyCode(request.secretKey(), request.code());

        if (isValid) {
            return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "2FA validation successful."));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "FAILED", "message", "Invalid 2FA code. Please try again."));
        }
    }
}
