package org.example.agent.global.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseHeader {

    private static final ResponseHeader SUCCESS = new ResponseHeader(true, "", "성공");
    private static final ResponseHeader ASYNC_SUCCESS = new ResponseHeader(true, "TN0000", "성공");

    @Schema(description = "성공 여부", example = "true")
    @JsonProperty("isSuccessful")
    boolean isSuccessful;

    @Schema(description = "메시지 코드", example = "")
    @JsonProperty("resultCode")
    String resultCode;

    @Schema(description = "메시지", example = "")
    @JsonProperty("resultMessage")
    String resultMessage;

    private ResponseHeader(boolean isSuccessful, String resultCode, String message) {
        this.isSuccessful = isSuccessful;
        this.resultCode = resultCode;
        this.resultMessage = message;
    }

    public static ResponseHeader of(boolean isSuccessful, String resultCode, String message) {
        return new ResponseHeader(isSuccessful, resultCode, message);
    }

    public static ResponseHeader success() {
        return SUCCESS;
    }

    public static ResponseHeader asyncSuccess() {
        return ASYNC_SUCCESS;
    }

}
