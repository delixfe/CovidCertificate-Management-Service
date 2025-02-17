package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.domain.SigningInformation;
import com.flextrade.jfixture.JFixture;
import com.upokecenter.cbor.CBORObject;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.digg.dgc.encoding.Barcode;
import se.digg.dgc.encoding.BarcodeException;

import java.io.IOException;
import java.security.SignatureException;
import java.time.Instant;

import static ch.admin.bag.covidcertificate.api.Constants.CREATE_BARCODE_FAILED;
import static ch.admin.bag.covidcertificate.api.Constants.CREATE_SIGNATURE_FAILED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BarcodeServiceTest {
    @Mock
    private SwissDGCBarcodeEncoder dgcBarcodeEncoder;
    @Mock
    private COSETime coseTime;

    @InjectMocks
    private BarcodeService barcodeService;

    private final JFixture fixture = new JFixture();

    @BeforeEach
    void init() throws BarcodeException, IOException, SignatureException {
        lenient().when(dgcBarcodeEncoder.encodeToBarcode(any(byte[].class), any(SigningInformation.class), any(Instant.class))).thenReturn(fixture.create(Barcode.class));
        lenient().when(coseTime.calculateExpirationInstantPlusMonths(Constants.EXPIRATION_PERIOD_24_MONTHS)).thenReturn(fixture.create(Instant.class));
    }

    @Nested
    class CreateBarcode{
        @Test
        void callsEncoderWithCorrectJson() throws Exception {
            var signingInformation = fixture.create(SigningInformation.class);
            var instant = fixture.create(Instant.class);
            var dgcJson = fixture.create(JSONObject.class).toString();
            var dgcCbor = CBORObject.FromJSONString(dgcJson).EncodeToBytes();

            barcodeService.createBarcode(dgcJson, signingInformation, instant);

            verify(dgcBarcodeEncoder).encodeToBarcode(eq(dgcCbor), any(SigningInformation.class), any(Instant.class));
        }

        @Test
        void callsEncoderWithCorrectSigningInformation() throws Exception {
            var signingInformation = fixture.create(SigningInformation.class);
            var instant = fixture.create(Instant.class);
            var dgcJson = fixture.create(JSONObject.class).toString();

            barcodeService.createBarcode(dgcJson, signingInformation, instant);

            verify(dgcBarcodeEncoder).encodeToBarcode(any(), eq(signingInformation), any(Instant.class));
        }

        @Test
        void callsEncoderWithCorrectExpiration() throws Exception {
            var signingInformation = fixture.create(SigningInformation.class);
            var instant = fixture.create(Instant.class);
            var dgcJson = fixture.create(JSONObject.class).toString();

            barcodeService.createBarcode(dgcJson, signingInformation, instant);

            verify(dgcBarcodeEncoder).encodeToBarcode(any(), any(), eq(instant));
        }

        @Test
        void returnsGeneratedBarcode() throws Exception {
            var signingInformation = fixture.create(SigningInformation.class);
            var dgcJson = fixture.create(JSONObject.class).toString();
            var barcode = fixture.create(Barcode.class);
            var instant = fixture.create(Instant.class);
            when(dgcBarcodeEncoder.encodeToBarcode(any(), any(SigningInformation.class), any(Instant.class))).thenReturn(barcode);

            Barcode result = barcodeService.createBarcode(dgcJson, signingInformation, instant);

            assertEquals(barcode, result);
        }

        @ParameterizedTest
        @ValueSource(classes = {BarcodeException.class, IOException.class, SignatureException.class})
        void throwsCreateCertificateException_ifACheckedException(Class<Exception> exceptionClass) throws BarcodeException, IOException, SignatureException {
            var signingInformation = fixture.create(SigningInformation.class);
            var dgcJson = fixture.create(JSONObject.class).toString();
            var instant = fixture.create(Instant.class);
            when(dgcBarcodeEncoder.encodeToBarcode(any(), any(SigningInformation.class), any(Instant.class))).thenThrow(fixture.create(exceptionClass));

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> barcodeService.createBarcode(dgcJson, signingInformation, instant));

            assertEquals(CREATE_BARCODE_FAILED, exception.getError());
        }

        @Test
        void propagatesCreateCertificateException_ifACreateCertificateExceptionIsThrownByTheBarcodeEncoder() throws BarcodeException, IOException, SignatureException {
            var signingInformation = fixture.create(SigningInformation.class);
            var dgcJson = fixture.create(JSONObject.class).toString();
            var instant = fixture.create(Instant.class);
            var expectedException = new CreateCertificateException(CREATE_SIGNATURE_FAILED);
            when(dgcBarcodeEncoder.encodeToBarcode(any(), any(SigningInformation.class), any(Instant.class))).thenThrow(expectedException);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> barcodeService.createBarcode(dgcJson, signingInformation, instant));

            assertEquals(expectedException, exception);
        }

    }

    @Nested
    class CreateBarcodeWithDefaultExpiration{
        @Test
        void callsEncoderWithCorrectJson() throws Exception {
            var signingInformation = fixture.create(SigningInformation.class);
            var dgcJson = fixture.create(JSONObject.class).toString();
            var dgcCbor = CBORObject.FromJSONString(dgcJson).EncodeToBytes();

            barcodeService.createBarcode(dgcJson, signingInformation, coseTime.calculateExpirationInstantPlusMonths(24));

            verify(dgcBarcodeEncoder).encodeToBarcode(eq(dgcCbor), any(SigningInformation.class), any(Instant.class));
        }

        @Test
        void callsEncoderWithCorrectSigningInformation() throws Exception {
            var signingInformation = fixture.create(SigningInformation.class);
            var dgcJson = fixture.create(JSONObject.class).toString();

            barcodeService.createBarcode(dgcJson, signingInformation, coseTime.calculateExpirationInstantPlusMonths(24));

            verify(dgcBarcodeEncoder).encodeToBarcode(any(), eq(signingInformation), any(Instant.class));
        }

        @Test
        void callsEncoderWithCorrectExpiration() throws Exception {
            var signingInformation = fixture.create(SigningInformation.class);
            var instant = fixture.create(Instant.class);
            var dgcJson = fixture.create(JSONObject.class).toString();
            when(coseTime.calculateExpirationInstantPlusMonths(24)).thenReturn(instant);

            barcodeService.createBarcode(dgcJson, signingInformation, coseTime.calculateExpirationInstantPlusMonths(24));

            verify(dgcBarcodeEncoder).encodeToBarcode(any(), any(), eq(instant));
        }

        @Test
        void returnsGeneratedBarcode() throws Exception {
            var signingInformation = fixture.create(SigningInformation.class);
            var dgcJson = fixture.create(JSONObject.class).toString();
            var barcode = fixture.create(Barcode.class);

            when(dgcBarcodeEncoder.encodeToBarcode(any(), any(SigningInformation.class), any(Instant.class))).thenReturn(barcode);

            Barcode result = barcodeService.createBarcode(dgcJson, signingInformation, coseTime.calculateExpirationInstantPlusMonths(24));

            assertEquals(barcode, result);
        }

        @ParameterizedTest
        @ValueSource(classes = {BarcodeException.class, IOException.class, SignatureException.class})
        void throwsCreateCertificateException_ifACheckedException(Class<Exception> exceptionClass) throws BarcodeException, IOException, SignatureException {
            var signingInformation = fixture.create(SigningInformation.class);
            var dgcJson = fixture.create(JSONObject.class).toString();
            when(dgcBarcodeEncoder.encodeToBarcode(any(), any(SigningInformation.class), any(Instant.class))).thenThrow(fixture.create(exceptionClass));

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> barcodeService.createBarcode(dgcJson, signingInformation, coseTime.calculateExpirationInstantPlusMonths(24)));

            assertEquals(CREATE_BARCODE_FAILED, exception.getError());
        }

        @Test
        void propagatesCreateCertificateException_ifACreateCertificateExceptionIsThrownByTheBarcodeEncoder() throws BarcodeException, IOException, SignatureException {
            var signingInformation = fixture.create(SigningInformation.class);
            var dgcJson = fixture.create(JSONObject.class).toString();
            var expectedException = new CreateCertificateException(CREATE_SIGNATURE_FAILED);
            when(dgcBarcodeEncoder.encodeToBarcode(any(), any(SigningInformation.class), any(Instant.class))).thenThrow(expectedException);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> barcodeService.createBarcode(dgcJson, signingInformation, coseTime.calculateExpirationInstantPlusMonths(24)));

            assertEquals(expectedException, exception);
        }

    }
}
