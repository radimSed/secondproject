package cz.engeto.radim.secondproject.controller;

public class PersonIdUsedException extends RuntimeException{
    public PersonIdUsedException(String errorString){
        super(errorString);
    }

}
