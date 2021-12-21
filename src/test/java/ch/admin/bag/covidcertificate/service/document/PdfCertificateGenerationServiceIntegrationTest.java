package ch.admin.bag.covidcertificate.service.document;

import ch.admin.bag.covidcertificate.api.mapper.AntibodyCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.mapper.AntibodyCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.mapper.RecoveryCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.mapper.RecoveryCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.mapper.TestCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.mapper.TestCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.mapper.VaccinationCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.mapper.VaccinationCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.mapper.VaccinationTouristCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.mapper.VaccinationTouristCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.request.AntibodyCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.Issuable;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationTouristCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableTestDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableVaccineDto;
import ch.admin.bag.covidcertificate.api.valueset.TestType;
import ch.admin.bag.covidcertificate.service.domain.AbstractCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.AntibodyCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.AntibodyCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.TestCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.TestCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.VaccinationTouristCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.VaccinationTouristCertificateQrCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Stream;

import static ch.admin.bag.covidcertificate.TestModelProvider.getAntibodyCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getRecoveryCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getTestCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getVaccinationCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getVaccinationTouristCertificateCreateDto;

@Slf4j
class PdfCertificateGenerationServiceIntegrationTest {

    private final PdfCertificateGenerationService service = new PdfCertificateGenerationService();

    private final String countryEn = "Switzerland";

    private static final String familyNameBig = "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW";
    private static final String givenNameBig = "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW";

    private static final String familyNameSmall = "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW";
    private static final String givenNameSmall = "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW";

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(service, "showWatermark", true);
    }


    @ParameterizedTest
    @MethodSource("testConfiguration")
    void generateRecoveryDocuments(String language, String familyName, String givenName) throws Exception {
        generateDocument_recovery(language, familyName, givenName);
    }

    @ParameterizedTest
    @MethodSource("testConfiguration")
    void generateVaccinationDocuments(String language, String familyName, String givenName) throws Exception {
        generateDocument_vaccine(language, familyName, givenName);
    }

    @ParameterizedTest
    @MethodSource("testConfiguration")
    void generatePartialVaccinationDocuments(String language, String familyName, String givenName) throws Exception {
        generateDocument_partialVaccination(language, familyName, givenName);
    }

    @ParameterizedTest
    @MethodSource("testConfiguration")
    void generateVaccinationTouristDocuments(String language, String familyName, String givenName) throws Exception {
        generateDocument_vaccineTourist(language, familyName, givenName);
    }

    @ParameterizedTest
    @MethodSource("testConfiguration")
    void generateTestDocuments(String language, String familyName, String givenName) throws Exception {
        generateDocument_test(language, familyName, givenName);
    }

    @ParameterizedTest
    @MethodSource("testConfiguration")
    void generateAntibodyDocuments(String language, String familyName, String givenName) throws Exception {
        generateDocument_antibody(language, familyName, givenName);
    }


    private void generateDocument_vaccine(String language, String familyName, String givenName) throws Exception {
        VaccinationCertificateCreateDto createDto = getVaccinationCertificateCreateDto("1119349007", language);
        this.generateDocument_vaccine(createDto, language, familyName, givenName, "vaccine");
    }

    private void generateDocument_partialVaccination(String language, String familyName, String givenName) throws Exception {
        VaccinationCertificateCreateDto createDto = getVaccinationCertificateCreateDto("1119349007", language);
        ReflectionTestUtils.setField(createDto.getVaccinationInfo().get(0), "numberOfDoses", 1);
        this.generateDocument_vaccine(createDto, language, familyName, givenName, "partial-vaccine");
    }

    private void generateDocument_vaccine(VaccinationCertificateCreateDto createDto,
                                          String language, String familyName, String givenName, String filename) throws Exception {
        IssuableVaccineDto vaccineDto = new IssuableVaccineDto("EU/1/20/1528", "Comirnaty", "1119349007",
                "SARS-CoV-2 mRNA vaccine", "ORG-100030215",
                "Biontech Manufacturing GmbH", Issuable.CH_ONLY, false);
        ReflectionTestUtils.setField(createDto.getPersonData().getName(), "familyName", familyName);
        ReflectionTestUtils.setField(createDto.getPersonData().getName(), "givenName", givenName);
        String country = "Schweiz";

        VaccinationCertificateQrCode qrCodeData = VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(
                createDto, vaccineDto);
        VaccinationCertificatePdf pdfData = VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(createDto,
                vaccineDto,
                qrCodeData,
                country,
                countryEn);

        createPdf(pdfData, buildUniqueFilename(filename, language, familyName, givenName));
    }

    private void generateDocument_vaccineTourist(String language, String familyName, String givenName) throws Exception {
        VaccinationTouristCertificateCreateDto createDto = getVaccinationTouristCertificateCreateDto("1119349007", language);
        this.generateDocument_vaccineTourist(createDto, language, familyName, givenName);
    }

    private void generateDocument_vaccineTourist(VaccinationTouristCertificateCreateDto createDto, String language, String familyName, String givenName) throws Exception {
        IssuableVaccineDto vaccineDto = new IssuableVaccineDto("EU/1/20/1528", "Comirnaty", "1119349007",
                "SARS-CoV-2 mRNA vaccine", "ORG-100030215",
                "Biontech Manufacturing GmbH", Issuable.CH_ONLY, false);
        ReflectionTestUtils.setField(createDto.getPersonData().getName(), "familyName", familyName);
        ReflectionTestUtils.setField(createDto.getPersonData().getName(), "givenName", givenName);
        String country = "Schweiz";

        VaccinationTouristCertificateQrCode qrCodeData = VaccinationTouristCertificateQrCodeMapper.toVaccinationTouristCertificateQrCode(
                createDto, vaccineDto);
        VaccinationTouristCertificatePdf pdfData = VaccinationTouristCertificatePdfMapper.toVaccinationTouristCertificatePdf(createDto,
                vaccineDto, qrCodeData, country, countryEn);

        createPdf(pdfData, buildUniqueFilename("vaccine-tourist", language, familyName, givenName));
    }

    private void generateDocument_test(String language, String familyName, String givenName) throws Exception {
        TestCertificateCreateDto createDto = getTestCertificateCreateDto("test", "test", language);
        IssuableTestDto testValueSet = new IssuableTestDto("1341", "Qingdao Hightop Biotech Co., Ltd, SARS-CoV-2 Antigen Rapid Test (Immunochromatography)", TestType.RAPID_TEST, null);
        ReflectionTestUtils.setField(createDto.getPersonData().getName(), "familyName", familyName);
        ReflectionTestUtils.setField(createDto.getPersonData().getName(), "givenName", givenName);
        String country = "Schweiz";

        TestCertificateQrCode qrCodeData = TestCertificateQrCodeMapper.toTestCertificateQrCode(createDto, testValueSet);
        TestCertificatePdf pdfData = TestCertificatePdfMapper.toTestCertificatePdf(createDto, testValueSet, qrCodeData, country, countryEn);

        createPdf(pdfData, buildUniqueFilename("test", language, familyName, givenName));
    }

    private void generateDocument_recovery(String language, String familyName, String givenName) throws Exception {
        RecoveryCertificateCreateDto createDto = getRecoveryCertificateCreateDto(language);
        String country = "Schweiz";
        ReflectionTestUtils.setField(createDto.getPersonData().getName(), "familyName", familyName);
        ReflectionTestUtils.setField(createDto.getPersonData().getName(), "givenName", givenName);

        RecoveryCertificateQrCode qrCodeData = RecoveryCertificateQrCodeMapper.toRecoveryCertificateQrCode(createDto);
        RecoveryCertificatePdf pdfData = RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(createDto, qrCodeData, country, countryEn);

        createPdf(pdfData, buildUniqueFilename("recovery", language, familyName, givenName));
    }

    private void generateDocument_antibody(String language, String familyName, String givenName) throws Exception {
        AntibodyCertificateCreateDto createDto = getAntibodyCertificateCreateDto(language);
        String country = "Schweiz";
        ReflectionTestUtils.setField(createDto.getPersonData().getName(), "familyName", familyName);
        ReflectionTestUtils.setField(createDto.getPersonData().getName(), "givenName", givenName);

        AntibodyCertificateQrCode qrCodeData = AntibodyCertificateQrCodeMapper.toAntibodyCertificateQrCode(createDto);
        AntibodyCertificatePdf pdfData = AntibodyCertificatePdfMapper.toAntibodyCertificatePdf(createDto, qrCodeData, country, countryEn);

        createPdf(pdfData, buildUniqueFilename("antibody", language, familyName, givenName));
    }

    private String buildUniqueFilename(String prefix, String language, String familyName, String givenName) {
        return prefix + "-" + familyName.length() + "-" + givenName.length() + "-" + language;
    }

    private static final Map<String, String> barcodes = Map.of(
            "909x909", "HC1:NCFOXNYTSFDHJI8Y0PZLQ7*B53II*EG.2MI6LR5+T9R6VOGI/W5VACM*4FGNNTQ NI4EFSYSC%OW4PYE9*FJ*TTAZ8-.A2*CEHJ5$0$/AQ$3CY0T:D 1LE%2*%9 -5:Y0H0LD+9%:K00MA3LYE9/MVLREOH6VO5 *JAYUQJATK25M9:OQPAU:IAJ0AGY0OWCR/C*45%47NPNC-4**4/43+BBT 4A05423423ZQT4I3DG37$G3NJZ739K075IPF5.0M+G9U3Q7A5A6921ARN9UDPE-PVA4HHAXRK%KOQBHTDC0D9E2LBHHGKLO-K%FG5IAMEADII-GGUJKXGGRB8-B940JIBG1KKQJKM+GHFEZI9PJAQJK6KNSKE MCAOI8%MUE1CBP5PI .UDT7N00%07Z LP*OJ.VLKC6/VAICPTM L6WB4I47YK5LHM*$OM9H /KH-K.9T$/IQ0531TR20*ZV BFHYNMJRL+EZBFD67/TVEX0QIV9Y0VJJ.0KW+6:IOC/B52I824TR8SA7G6MR1R88P3R6JINQ+MN/QP9QE8Q NIUE8L1VWQN36H$MQNDRRBT AJT BO+J08P3JHIN1LH1-DJG.16%JP 658T5V1*F83CC0139/TSTMPVEHVV/3P$$HKWU3.L. VTQ0-$UG*5W:3P5TPGMM5EA89NSQ3Z9M6QCAQR3O3RP:8R 1C1-HTMN-OUJHS*/5ITP33JQCW*%76EJNMSC74W8J-K1T1SKDEPF2Z1CPLO1 I8OFLD1/YQ$+079US1CX:BY.5+2V6$CSRQAU1*9PJRT81MR2B7PHEV9N0OK7GNCWG/EO0EXQJBOSV*1W0BCBR6DKU*CQJCO832$1OAE2AV3MSZ/M5YUJIDTX0TYVONFKEUE*254PU7JS.TV:PW$6V+F2N9LZLJYNBRD4DOFMN-BUC$P70BZ3A*$GXAT7.7D67K%PVTJ9+G*U4H50P/IG4",
            "930x930", "HC1:NCFI60FG0/3WUWGSLKH47GO0XM5ZVFO780PK9CK*500XK0JCV493F383RZ9U3F3B1M7UCY50.FK6ZK7:EDOLOPCO8F6%E3.DA%EOPC1G72A6YM8NG7S46F:67:6CA6LA7D%60G6OF60A6EM8S461T8UPC/IC 7BFVCPD0LVC6JD846Y96*963W5H*6+EDG8F3I80/D6$CBECSUER:C2$NS346$C2%E9VC- CSUE145GB8JA5B$D% D3IA4W5646846C46IA7.JCP9E.G8YB956ASN8KKEE2C3KC.SC4KCD3DX47B46IL6646H*6Z/ER2DD46JH8946JPCT3E6JD846Y96*963W5/A6..DX%DZJC3/DO1B60AS6AI+8I3DB CPVD7%ELPCG/D+2DG695S7IS8MPCG/DB C S9IZA$PC5$CUZC$$5Y$5FBB900X31K6PKGMI/Q173T*MDKARJT4K5K:KFW97CH411P+24E73AOVI7WDIA69%RC4WK%868S80RBV77UI65 L6U2. A8N8EAHD-C68G%96IAAQSL-4EMFNNFUR2QNN9NSPF4F$XR*DS1VCGFQ000ERMLZT1S73/JXIP+BA-9A9Q9RGQ*:DKKDPOA76L*.LU.76PKHJK.2740LNAU8 2XHT.H5 4VXQ3LDWNCK7/NKM2$RN NH1L5W25VGAYV02526MO%2Q CEEEMA52YYFX2FDZ0*7IA1HC5RF$8MMR866GP4KKQ9CU8ZJY0305PBB5RCU0P6:SQCELY%C/ 8D57P0G/JMNF3-.RL0LTA3P+4+0E6X4$EE/2W51R3XM6MRAKD+7CM9921",
            "970x970", "HC1:NCFOXNYTSFDHJI820P1TT*:CM9W K2U 2JH6O:5+T9R6VOGIHY5+8KM*4FGN+H4 NI4EFSYS:%OD3PYE9*FJ*TTAZ8-.A2*CEHJ5$0$/AQ$3--8*Z0K.GJ$2L0H40IJ1M%:KN2LR$2S.8%*5YE9/MVLRE*LA/CJ6IAXPMHQ1*P14W19UEZ%POH6VO5 *JAYUQJATK25M9:OQPAU:IAJ0AGY0OWCR/C*45%47NPNC-4D-4HRVUMNMD3323R131*EC-4A+2XEN QT QTHC31M3+E3CP456L X4CZKHKB-43.E3-C32Y93O5RF6$T61R63B0 %PPRA$TI$*AIFTZD3/9TL4T.B9TXPMDQMFV50BU9ESQA8$P3+P+G9V/AD9QS90OKH%J63M2+GAAN6XTM*KA28C9JAO/BW3F%JCHFE4JB7814SL-8A%+HT7JI8LQP9H-HD5L79HKYD$PT-JT%USLC3VV01C1PVMYZQ H9:.NZP3NSGUVPQRHIY1+ H1O1Z73ZYHKEPU4S3ME4I3G.7WVVSB0S3A4HP-2VT-N0B4LT616N.HBJ1NK1A+8V4 NH8I0ALL$LP-Q KDHBE2%8/WPDTJC:5G$TJ1FX2EM0U*PVE87XXKT/J:FVBZUKAN9JC-.PUVMPK9SNVH.KF$DNZLZ$M/0LFTP4EQ2 UL.2BL6+/FYJHYHS.9V.XV4YSYZD ZS$6U$NMMGCPJCQ3EO7JYRV5EB37L*F5HWQC$E0$RY3IA27HV059IE SH+D2QU83U+7J6KPK2AB*KT L+:R%6V2LL6ZQS$L43AXV7Y.3FQBO1JT MWLQL2JA2V.9WGLAJ-DQEFUBED:MC2N/XFXX8W26$TSCT3M*40AEPATOHGRHUTAURMA7:V88OK2G1T71YN*$JSUDBDJR6K0G4/3V25JV40+QVO0",
            "975x975", "HC1:NCFOXNYTSFDHJI8-.O0:A%1W RI%.BI06%BF1WG21QKP85NPV*JVH5QWKIW18WA%NE/P3F/8X*G3M9FQH+4JZW4V/AY73CIBVQFSA36238FNB939PJ*KN%DJ3239L7BRNHKBWINEV40AT0C7LS4AZKZ73423ZQT-EJEG3LS4JXITAFK1HG%8SC91Z8YA7-TIP+PQE1W9L $N3-Q-*OGF2F%M RFUS2CPA-DG:A3AGJLC1788M7DD-I/2DBAJDAJCNB-439Y4.$SINOPK3.T4RZ4E%5MK9QM9DB9E%5:I9YHQ1FDIV4RB4VIOTNPS46UDBQEAJJKHHGQA8EL4QN9J9E6LF6JC1A5N11+N1X*8O13E20ZO8%3",
            "979x979", "HC1:NCF+50%G0/3WUWGSLKH47GO08.RR2VRVSJ%N8CKGUU*70YM8FN03$C+KKWY0VFC.J7D97TK0F90GECQHGWJC0FDL:4:KEPH7M/ESDD746KG7+59U*8DS85:6BL61A6-96TW64R6H:6746+F6VX8WJCT3EYM8XJCS.CNF6OF64W5KF6%96XJC/$ENF6PF63W5NW6WF6ZJC+KENF6OF64W5KF6746%JC+QE$.32%E6VCHQEU$DE44NXOBJE719$QE0/D+8D-ED.24-G8$:84KCD3DX47B46IL6646I*6GVCXJC1A65:6TPCBEC7ZKW.CDPD1$C6$C1$C$34IS8 JC1/D3O9JECJQEW.CDWEK09C+9WNA3+9S6AJ+8I3D7WEGN9UB8HOAO/EZKEZ967L6256V503.JUFGNIGXIM:AU5I0NJC$GB *J5FNNY6:VA.-Q7C0QFJ1 AKYU4YCO7DW9BFKJ%3D5CM%Z7$N0G1E/X9TF1FIFM.FK5ALP2DL7*10WXC2OP38EULJ74PU81XAD4OFXK5G6Q2M9 6HUF6O1GS7WOX1N$BP.G7KQ*GA0J0.ZJ$$N/A213GQMPHCWH+6JLM7XGG2FB:N 29$%AC UJX8T*FMHBY%JGQH.MEMER2 1SWB99KBHO.M5U%I VD3:HFIBCET%FI*KE4LP7$MHEH-$U-XG4SB71OE$I39T751C/2*ADIX6%+V 5ABVI$.R%CCI7NJM6B6GEF7-JNDSPZ.6W99MO7:JF1NG/94$994TF8E3NHR1$L2K5TH58OJZTLQ3HNS6VJQ"
        );

    private final boolean storePdf = false;          // stores a generated pdf in timestamp folder
    private final boolean allQRCodeSizes = false;    // generates a pdf for each qr-code size (see "barcodes")

    private static final String path = "/home/dev/Downloads/";  // root folder for timestamp and compare-folder
    private static final String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));  // used for storing generated pdf

    void createPdf(AbstractCertificatePdf pdfData, String filename) throws Exception {

        String subFolder = storePdf ? timestamp : null;

        if (allQRCodeSizes) {
            for (String barcodeSize : barcodes.keySet()) {
                generate(pdfData, barcodes.get(barcodeSize), filename+"-"+barcodeSize, subFolder);
            }
        } else {
            generate(pdfData, barcodes.get("975x975"), filename, subFolder);
        }
    }

    private void generate(AbstractCertificatePdf pdfData, String barcodePayload, String filename, String subFolder) throws Exception {

        byte[] document = service.generateCovidCertificate(pdfData, barcodePayload, LocalDateTime.now());

        if (subFolder != null) {

            Path folder = createFolder(path, subFolder);

            String fullFilename = "certificate-" + filename + ".pdf";
            OutputStream out = new FileOutputStream(folder.toString() + "/" + fullFilename);
            out.write(document);
            out.close();
        }
    }

    private Path createFolder(String root, String folder) throws IOException {
        Path subFolder = Paths.get(root, folder);
        if (!Files.exists(subFolder)) {
            Files.createDirectory(subFolder);
        }
        return subFolder;
    }

    private static Stream<Arguments> testConfiguration() {
        return Stream.of("de", "fr", "it", "rm").
                flatMap(language -> Stream.of(
                        Arguments.of(language, familyNameSmall, givenNameSmall),
                        Arguments.of(language, familyNameBig, givenNameBig)
                ));
    }
}
