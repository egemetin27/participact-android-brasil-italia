package br.udesc.esag.participactbrasil.domain.persistence;


public class ActionPhoto extends Action {

    private static final long serialVersionUID = -191570571061988661L;

    public ActionFlat convertToActionFlat() {
        return new ActionFlat(this);
    }
}
