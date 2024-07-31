package cz.engeto.radim.secondproject.controller;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String errorString){
        super(errorString);
    }
}
