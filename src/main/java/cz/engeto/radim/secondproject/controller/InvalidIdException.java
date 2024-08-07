package cz.engeto.radim.secondproject.controller;

public class InvalidIdException extends RuntimeException{
    public InvalidIdException(String errorString){
        super(errorString);
    }
}
