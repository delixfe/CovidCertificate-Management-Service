package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;

import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.NO_EXCEPTIONAL_INFO;

@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExceptionalCertificateCreateDto extends CertificateCreateDto {

    private List<ExceptionalCertificateDataDto> exceptionalInfo;

    public ExceptionalCertificateCreateDto(
            CovidCertificatePersonDto personData,
            List<ExceptionalCertificateDataDto> exceptionalInfo,
            String language,
            CovidCertificateAddressDto address,
            String inAppDeliveryCode,
            SystemSource systemSource
    ) {
        super(personData, language, address, inAppDeliveryCode, systemSource);
        this.exceptionalInfo = exceptionalInfo;
    }

    @Override
    public void validate() {
        super.validate();
        if ( exceptionalInfo == null || exceptionalInfo.isEmpty()) {
            throw new CreateCertificateException(NO_EXCEPTIONAL_INFO);
        } else {
            exceptionalInfo.forEach(
                    ExceptionalCertificateDataDto -> ExceptionalCertificateDataDto.validate(this.getSystemSource()));
        }
    }
}
