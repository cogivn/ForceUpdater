package com.legatotechnologies.updater;

/**
 * Created by davidng on 7/3/17.
 */

public class illegalUrlException extends RuntimeException {
    private static final String MESSAGE = "Url is invalid";

    illegalUrlException(){
        super(MESSAGE);
    }

}
