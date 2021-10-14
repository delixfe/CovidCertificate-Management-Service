package ch.admin.bag.covidcertificate.util;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.TestCertificateDataDto;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_SAMPLE_DATE_TIME;

public class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

    static final String testCertificate = TestCertificateDataDto.class.getSimpleName();

    @Override
    public ZonedDateTime deserialize(JsonParser jsonparser, DeserializationContext context) throws IOException {
        String dateAsString = jsonparser.getText();
        var origin = jsonparser.getParsingContext().getCurrentValue().getClass().getSimpleName();

        try {
            return ZonedDateTime.parse(dateAsString);
        } catch (DateTimeParseException e) {
            if (testCertificate.equals(origin)) {
                throw new CreateCertificateException(INVALID_SAMPLE_DATE_TIME);
            }
            throw e;
        }
    }
}
