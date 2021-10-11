package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.client.signing.SigningClient;
import ch.admin.bag.covidcertificate.domain.SigningInformation;
import com.flextrade.jfixture.JFixture;
import org.apache.commons.codec.DecoderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static ch.admin.bag.covidcertificate.api.Constants.CREATE_COSE_PAYLOAD_FAILED;
import static ch.admin.bag.covidcertificate.api.Constants.CREATE_COSE_PROTECTED_HEADER_FAILED;
import static ch.admin.bag.covidcertificate.api.Constants.CREATE_COSE_SIGN1_FAILED;
import static ch.admin.bag.covidcertificate.api.Constants.CREATE_COSE_SIGNATURE_DATA_FAILED;
import static ch.admin.bag.covidcertificate.api.Constants.CREATE_SIGNATURE_FAILED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class COSEServiceTest {
    private final JFixture fixture = new JFixture();
    @InjectMocks
    COSEService coseService;

    @Mock
    private CBORService cborService;
    @Mock
    private SigningClient signingClient;

    @BeforeEach
    public void init() throws Exception {
        lenient().when(cborService.getProtectedHeader(any())).thenReturn(fixture.create(byte[].class));
        lenient().when(cborService.getPayload(any(byte[].class))).thenReturn(fixture.create(byte[].class));
        lenient().when(cborService.getSignatureData(any(byte[].class), any(byte[].class))).thenReturn(fixture.create(byte[].class));
        lenient().when(cborService.getCOSESign1(any(byte[].class), any(byte[].class), any(byte[].class))).thenReturn(fixture.create(byte[].class));
        lenient().when(signingClient.create(any(byte[].class), any())).thenReturn(fixture.create(byte[].class));
    }

    @Nested
    class GetCOSESign1 {
        @Test
        void callsCBORServiceGetProtectedHeader_withCorrectKeyIdentifier() throws Exception {
            var signingInformation = fixture.create(SigningInformation.class);
            coseService.getCOSESign1(fixture.create(byte[].class), signingInformation);
            // then
            verify(cborService).getProtectedHeader(signingInformation.getKeyIdentifier());
        }

        @Test
        void callsCBORServiceGetPayload_withCorrectCBORObject() {
            var dgcCBOR = fixture.create(byte[].class);
            // when
            coseService.getCOSESign1(dgcCBOR, fixture.create(SigningInformation.class));
            // then
            verify(cborService).getPayload(dgcCBOR);
        }

        @Test
        void callsCBORServiceGetSignatureData_withCorrectProtectedHeader() throws DecoderException {
            var protectedHeader = fixture.create(byte[].class);
            when(cborService.getProtectedHeader(any())).thenReturn(protectedHeader);
            // when
            coseService.getCOSESign1(fixture.create(byte[].class), fixture.create(SigningInformation.class));
            // then
            verify(cborService).getSignatureData(eq(protectedHeader), any());
        }

        @Test
        void callsCBORServiceGetSignatureData_withCorrectPayload() throws DecoderException {
            var payload = fixture.create(byte[].class);
            when(cborService.getPayload(any())).thenReturn(payload);
            // when
            coseService.getCOSESign1(fixture.create(byte[].class), fixture.create(SigningInformation.class));
            // then
            verify(cborService).getSignatureData(any(), eq(payload));
        }

        @Test
        void callSigningClientCreate_withCorrectSignatureData() {
            var signatureData = fixture.create(byte[].class);
            when(cborService.getSignatureData(any(), any())).thenReturn(signatureData);
            // when
            coseService.getCOSESign1(fixture.create(byte[].class), fixture.create(SigningInformation.class));
            // then
            verify(signingClient).create(eq(signatureData), any());
        }

        @Test
        void callSigningClientCreate_withCorrectSigningInformation() {
            var signingInformation = fixture.create(SigningInformation.class);
            // when
            coseService.getCOSESign1(fixture.create(byte[].class), signingInformation);
            // then
            verify(signingClient).create(any(), eq(signingInformation));
        }


        @Test
        void callsCBORServiceGetCOSESign1_withCorrectProtectedHeader() throws DecoderException {
            var protectedHeader = fixture.create(byte[].class);
            when(cborService.getProtectedHeader(any())).thenReturn(protectedHeader);
            // when
            coseService.getCOSESign1(fixture.create(byte[].class), fixture.create(SigningInformation.class));
            // then
            verify(cborService).getCOSESign1(eq(protectedHeader), any(), any());
        }

        @Test
        void callsCBORServiceGetCOSESign1_withCorrectPayload() {
            var payload = fixture.create(byte[].class);
            when(cborService.getPayload(any())).thenReturn(payload);
            // when
            coseService.getCOSESign1(fixture.create(byte[].class), fixture.create(SigningInformation.class));
            // then
            verify(cborService).getCOSESign1(any(), eq(payload), any());
        }

        @Test
        void callsCBORServiceGetCOSESign1_withCorrectSignature() {
            var signature = fixture.create(byte[].class);
            when(signingClient.create(any(), any())).thenReturn(signature);
            // when
            coseService.getCOSESign1(fixture.create(byte[].class), fixture.create(SigningInformation.class));
            // then
            verify(cborService).getCOSESign1(any(), any(), eq(signature));
        }

        @Test
        void returnsCOSESign1() {
            var coseSign1 = fixture.create(byte[].class);
            when(cborService.getCOSESign1(any(), any(), any())).thenReturn(coseSign1);
            // when
            byte[] result = coseService.getCOSESign1(fixture.create(byte[].class), fixture.create(SigningInformation.class));
            // then
            assertEquals(coseSign1, result);
        }

        @ParameterizedTest
        @ValueSource(classes = {DecoderException.class, RuntimeException.class, IllegalArgumentException.class, CreateCertificateException.class})
        void throwsCreateCertificateException_ifCBORServiceGetProtectedHeaderThrowsAnyException(Class<Exception> exceptionClass) throws DecoderException {
            var dgcCBOR = fixture.create(byte[].class);
            var signingInformation = fixture.create(SigningInformation.class);
            // given
            when(cborService.getProtectedHeader(any())).thenThrow(exceptionClass);
            // when then
            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> coseService.getCOSESign1(dgcCBOR, signingInformation));
            assertEquals(CREATE_COSE_PROTECTED_HEADER_FAILED, exception.getError());
        }

        @ParameterizedTest
        @ValueSource(classes = {RuntimeException.class, CreateCertificateException.class, IllegalArgumentException.class})
        void throwsCreateCertificateException_ifCBORServiceGetPayloadThrowsAnyException(Class<Exception> exceptionClass) {
            var dgcCBOR = fixture.create(byte[].class);
            var signingInformation = fixture.create(SigningInformation.class);
            // given
            when(cborService.getPayload(any(byte[].class))).thenThrow(exceptionClass);
            // when then
            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> coseService.getCOSESign1(dgcCBOR, signingInformation));
            assertEquals(CREATE_COSE_PAYLOAD_FAILED, exception.getError());
        }

        @ParameterizedTest
        @ValueSource(classes = {RuntimeException.class, CreateCertificateException.class, IllegalArgumentException.class})
        void throwsCreateCertificateException_ifCBORServiceGetSignatureDataThrowsAnyException(Class<Exception> exceptionClass) {
            var dgcCBOR = fixture.create(byte[].class);
            var signingInformation = fixture.create(SigningInformation.class);
            // given
            when(cborService.getSignatureData(any(byte[].class), any(byte[].class))).thenThrow(exceptionClass);
            // when then
            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> coseService.getCOSESign1(dgcCBOR, signingInformation));
            assertEquals(CREATE_COSE_SIGNATURE_DATA_FAILED, exception.getError());
        }

        @ParameterizedTest
        @ValueSource(classes = {RuntimeException.class, CreateCertificateException.class, IllegalArgumentException.class})
        void throwsCreateCertificateException_ifSigningThrowsAnyException(Class<Exception> exceptionClass) {
            var dgcCBOR = fixture.create(byte[].class);
            var signingInformation = fixture.create(SigningInformation.class);
            // given
            when(signingClient.create(any(byte[].class), any())).thenThrow(exceptionClass);
            // when then
            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> coseService.getCOSESign1(dgcCBOR, signingInformation));
            assertEquals(CREATE_SIGNATURE_FAILED, exception.getError());
        }

        @ParameterizedTest
        @ValueSource(classes = {RuntimeException.class, CreateCertificateException.class, IllegalArgumentException.class})
        void throwsCreateCertificateException_ifCBORServiceGetCOSESign1ThrowsAnyException(Class<Exception> exceptionClass) {
            var dgcCBOR = fixture.create(byte[].class);
            var signingInformation = fixture.create(SigningInformation.class);
            // given
            when(cborService.getCOSESign1(any(byte[].class), any(byte[].class), any(byte[].class))).thenThrow(exceptionClass);
            // when then
            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> coseService.getCOSESign1(dgcCBOR, signingInformation));
            assertEquals(CREATE_COSE_SIGN1_FAILED, exception.getError());
        }
    }
}
