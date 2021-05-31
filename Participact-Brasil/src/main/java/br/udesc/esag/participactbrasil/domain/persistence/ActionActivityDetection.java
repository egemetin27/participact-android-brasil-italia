package br.udesc.esag.participactbrasil.domain.persistence;


public class ActionActivityDetection extends Action {

    private static final long serialVersionUID = -2980165443628417413L;

    public ActionFlat convertToActionFlat() {
        return new ActionFlat(this);
    }
}
