package com.alex.web.node.pdm.exception.hundler;

/**
 * This class contains all the necessary information about error.
 * @param code code of error.
 * @param message message of error.
 */

public record ErrorResponse(int code,String message){
}
