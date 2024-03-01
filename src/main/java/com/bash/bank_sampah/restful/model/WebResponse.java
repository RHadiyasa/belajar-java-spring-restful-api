package com.bash.bank_sampah.restful.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Auto setter getter, toString, dll
@AllArgsConstructor
@NoArgsConstructor
@Builder //--> ini beguna ketika melakukan banyak instance dengan parameter yang berbeda2
public class WebResponse<T> {

    private T data;
    private String errors;
    private PagingResponse pagingResponse;
}
