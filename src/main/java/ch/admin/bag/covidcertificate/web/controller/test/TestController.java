package ch.admin.bag.covidcertificate.web.controller.test;

import ch.admin.bag.covidcertificate.api.request.AntibodyCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.ExceptionalCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryRatCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationTouristCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.client.signing.SigningClient;
import ch.admin.bag.covidcertificate.client.signing.VerifySignatureRequestDto;
import ch.admin.bag.covidcertificate.domain.SigningInformation;
import ch.admin.bag.covidcertificate.domain.SigningInformationRepository;
import ch.admin.bag.covidcertificate.service.domain.SigningCertificateCategory;
import ch.admin.bag.covidcertificate.service.test.TestCovidCertificateGenerationService;
import ch.admin.bag.covidcertificate.web.controller.SecurityHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/api/v1/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final SecurityHelper securityHelper;
    private final SigningClient signingClient;
    private final SigningInformationRepository signingInformationRepository;
    private final TestCovidCertificateGenerationService testCovidCertificateGenerationService;

    @GetMapping("/{validAt}")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public List<SigningInformation> testSigningInformationConfiguration(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validAt,
            HttpServletRequest request) {
        securityHelper.authorizeUser(request);

        List<SigningInformation> errors = new ArrayList<>();
        for(SigningCertificateCategory signingCertificateCategory: SigningCertificateCategory.values()) {
            var signingInformationList = signingInformationRepository.findSigningInformation(signingCertificateCategory.value, validAt);

            for (SigningInformation signingInformation : signingInformationList) {
                try {
                    var messageBytes = UUID.randomUUID().toString().getBytes();
                    var signatureBytes = signingClient.createSignature(messageBytes, signingInformation);
                    if(signingInformation.getCertificateAlias()!=null && !signingInformation.getCertificateAlias().isBlank()) {
                        var message = Base64.getEncoder().encodeToString(messageBytes);
                        var signature = Base64.getEncoder().encodeToString(signatureBytes);

                        var verifySignatureDto = new VerifySignatureRequestDto(message, signature, signingInformation.getCertificateAlias());
                        var validSignature = signingClient.verifySignature(verifySignatureDto);
                        if (!validSignature) {
                            errors.add(signingInformation);
                        }
                    }
                } catch (Exception e) {
                    errors.add(signingInformation);
                }
            }
        }
        return errors;
    }

    @PostMapping("/vaccination/{validAt}")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto createVaccinationCertificate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validAt,
            @Valid @RequestBody VaccinationCertificateCreateDto createDto, HttpServletRequest request) throws IOException {
        securityHelper.authorizeUser(request);
        createDto.validate();
        return testCovidCertificateGenerationService.generateCovidCertificate(createDto, validAt);
    }

    @PostMapping("/vaccination-tourist/{validAt}")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto createVaccinationTouristCertificate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validAt,
            @Valid @RequestBody VaccinationTouristCertificateCreateDto createDto, HttpServletRequest request) throws IOException {
        securityHelper.authorizeUser(request);
        createDto.validate();
        return testCovidCertificateGenerationService.generateCovidCertificate(createDto, validAt);
    }

    @PostMapping("/test/{validAt}")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto createTestCertificate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validAt,
            @Valid @RequestBody TestCertificateCreateDto createDto, HttpServletRequest request) throws IOException {
        securityHelper.authorizeUser(request);
        createDto.validate();
        return testCovidCertificateGenerationService.generateCovidCertificate(createDto, validAt);
    }

    @PostMapping("/recovery/{validAt}")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto createRecoveryCertificate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validAt,
            @Valid @RequestBody RecoveryCertificateCreateDto createDto, HttpServletRequest request) throws IOException {
        securityHelper.authorizeUser(request);
        createDto.validate();
        return testCovidCertificateGenerationService.generateCovidCertificate(createDto, validAt);
    }

    @PostMapping("/recovery-rat/{validAt}")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto createRecoveryRatCertificate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validAt,
            @Valid @RequestBody RecoveryRatCertificateCreateDto createDto, HttpServletRequest request) throws IOException {
        securityHelper.authorizeUser(request);
        createDto.validate();
        return testCovidCertificateGenerationService.generateCovidCertificate(createDto, validAt);
    }

    @PostMapping("/antibody/{validAt}")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto createAntibodyCertificate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validAt,
            @Valid @RequestBody AntibodyCertificateCreateDto createDto, HttpServletRequest request) throws IOException {
        securityHelper.authorizeUser(request);
        createDto.validate();
        return testCovidCertificateGenerationService.generateCovidCertificate(createDto, validAt);
    }

    @PostMapping("/exceptional/{validAt}")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto createExceptionalCertificate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validAt,
            @Valid @RequestBody ExceptionalCertificateCreateDto createDto, HttpServletRequest request) throws IOException {
        securityHelper.authorizeUser(request);
        createDto.validate();
        return testCovidCertificateGenerationService.generateCovidCertificate(createDto, validAt);
    }
}
