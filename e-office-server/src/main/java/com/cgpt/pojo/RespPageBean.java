package com.cgpt.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespPageBean {
	/**
	 * 總條數
	 */
    private Long total;
    
    /**
     * List數組
     */
    private List<?> data;
}
