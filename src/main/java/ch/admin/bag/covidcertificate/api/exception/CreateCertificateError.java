package ch.admin.bag.covidcertificate.api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@Getter
public class CreateCertificateError implements Serializable {
    private final int errorCode;
    private final String errorMessage;
    private final HttpStatus httpStatus;

    @Override
    public String toString() {
        return "{\"errorCode\":" + errorCode + "," +
                "\"errorMessage\":\"" + errorMessage + "\"," +
                "\"httpStatus\":\"" + httpStatus.name() + "\"}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateCertificateError that = (CreateCertificateError) o;
        return errorCode == that.errorCode && errorMessage.equals(that.errorMessage) && httpStatus == that.httpStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(errorCode, errorMessage, httpStatus);
    }
}
