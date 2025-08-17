package org.example.agent.global.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseResult<T> {
    @Schema(description = "공통 헤더")
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    ResponseHeader header;

    @Schema(description = "응답 데이터")
    T result;

    private ResponseResult(ResponseHeader header, T result) {
        this.header = header;
        this.result = result;
    }

    public static ResponseResult of(ResponseHeader header, Object result) {
        return new ResponseResult(header, result);
    }
}
