package neostudy.dossier.feignclient;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.log4j.Log4j2;
import neostudy.dossier.exceptions.DealMicroserviceException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Log4j2
public class FeignClientErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        String requestUrl = response.request().url();
        Response.Body responseBody = response.body();

        if (responseBody != null && responseBody.length() != null) {
            byte[] buff = new byte[responseBody.length()];
            try {
                responseBody.asInputStream().read(buff);
                String msg = new String(buff, StandardCharsets.UTF_8);
                log.error("Request to " + requestUrl + " failed. Response status: " + response.status() + ". Message: " + msg);
                return new DealMicroserviceException(msg);
            } catch (IOException e) {
                log.error("Request to " + requestUrl + " failed. Response status: " + response.status());
                return new DealMicroserviceException("Request to " + requestUrl + " failed. Response status: " + response.status());
            }
        } else {
            log.error("Request to " + requestUrl + " failed. Response status: " + response.status());
            return new DealMicroserviceException("Request to " + requestUrl + " failed. Response status: " + response.status());
        }
    }

}
